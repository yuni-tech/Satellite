package yuni.library.satellite;

import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

class LocationManager {

    private LocationService mLocationService;
    private Location mLastLocation;

    private List<ContinueLocationHolder> mHolderList = new ArrayList<>();
    private Satellite.Options mOptions;

    public LocationManager(LocationService locationService) {
        mLocationService = locationService;
        mLocationService.setListener(new Satellite.Listener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
                notifyLocationChanged(location);
            }
        });
    }

    public Location getLastLocation() {
        if (mLastLocation == null) {
            return mLocationService.getLastLocation();
        }
        return mLastLocation.copy();
    }

    public Satellite.Options getUsingOptions() {
        if (mOptions == null) {
            return mOptions;
        }
        return mOptions.copy();
    }

    public synchronized ContinueLocationHolder startContinue(Object owner, Satellite.Options options, Satellite.Listener listener) {
        options = options.copy();

        ContinueLocationHolder holder = new ContinueLocationHolder(owner);
        holder.options = options;
        holder.listener = listener;
        mHolderList.add(holder);

        if (computeOptions()) {
            startOrStop();
        }

        return holder;
    }

    public synchronized void updateOptions(ContinueLocationHolder holder, Satellite.Options options) {
        options = options.copy();
        holder.options = options;
        if (computeOptions()) {
            startOrStop();
        }
    }

    public synchronized void stopContinue(ContinueLocationHolder holder) {
        if (!mHolderList.remove(holder)) {
            return;
        }

        if (computeOptions()) {
            startOrStop();
        }
    }

    public void getLocationOnce(Satellite.Options options, final Satellite.Listener listener) {
        if (mLastLocation != null && options.isCache() && !options.address) {
            long now = getCurrentTime();
            if (now - mLastLocation.getTime() < options.cacheTime) {
                listener.onLocationChanged(mLastLocation.copy());
                return;
            }
        }
        mLocationService.getOnce(options.copy(), location -> {
            mLastLocation = location;
            listener.onLocationChanged(location);
        });
    }

    public void checkOwners() {
        if (computeOptions()) {
            startOrStop();
        }
    }

    long getCurrentTime() {
        return System.currentTimeMillis();
    }

    private synchronized void notifyLocationChanged(Location location) {
        mLastLocation = location;

        for (int i=mHolderList.size()-1; i>=0; i--) {
            ContinueLocationHolder holder = mHolderList.get(i);
            if (holder.ownerRef.get() == null) {
                warnWeakReleased(holder.ownerKey);
                continue;
            }
            holder.listener.onLocationChanged(location);
        }

        if (computeOptions()) {
            startOrStop();
        }
    }

    private boolean computeOptions() {
        int mode = 0;
        long interval = Long.MAX_VALUE;
        boolean address = false;

        for (int i=mHolderList.size()-1; i>=0; i--) {
            ContinueLocationHolder holder = mHolderList.get(i);

            if (holder.ownerRef.get() == null) {
                mHolderList.remove(i);
                warnWeakReleased(holder.ownerKey);
                continue;
            }

            Satellite.Options options = holder.options;

            // 模式计算
            if (options.mode > mode) {
                mode = options.mode;
            }

            // interval
            if (options.interval < interval ) {
                interval = options.interval;
            }

            // address
            address |= options.address;
        }

        if (mHolderList.isEmpty()) {
            return true;
        }

        boolean changed = false;
        if (mOptions == null) {
            mOptions = new Satellite.Options();
            changed = true;
        }

        if (mOptions.mode != mode) {
            changed = true;
            mOptions.mode = mode;
        }

        if (mOptions.interval != interval) {
            changed = true;
            mOptions.interval = interval;
        }

        if (mOptions.address != address) {
            changed = true;
            mOptions.address = address;
        }

        if (!mLocationService.isStarted()) {
            return true;
        }

        return changed;
    }

    private void startOrStop() {
        if (mHolderList.isEmpty()) {
            mLocationService.stop();
            mOptions = null;
            return;
        }
        if (mLocationService.isStarted()) {
            mLocationService.updateOptions(mOptions);
        } else {
            mLocationService.start(mOptions);
        }
    }

    private void warnWeakReleased(String key) {
        Satellite.shared.log(Log.WARN, "LocationHandler has been released but stopContinue not called, owned by " + key);
    }

    class ContinueLocationHolder {

        WeakReference<Object> ownerRef;
        String ownerKey;
        Satellite.Options options;
        Satellite.Listener listener;

        public ContinueLocationHolder(Object owner) {
            this.ownerRef = new WeakReference<>(owner);
            this.ownerKey = owner.toString();
        }
    }

}
