package yuni.library.satellite;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

class AMapLocationService implements LocationService {

    private Context mContext;
    private AMapLocationClient mClient;
    private Satellite.Listener mListener;

    public AMapLocationService(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public boolean isStarted() {
        return mClient != null;
    }

    @Override
    public void setListener(Satellite.Listener listener) {
        mListener = listener;
    }

    @Override
    public void start(Satellite.Options options) {
        mClient = new AMapLocationClient(mContext);
        mClient.setLocationOption(toAMapOption(options));
        mClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                Location location = transformLocation(aMapLocation);
                mListener.onLocationChanged(location);
            }
        });
        mClient.startLocation();
    }

    @Override
    public void stop() {
        if (mClient != null) {
            mClient.stopLocation();
            mClient.onDestroy();
            mClient = null;
        }
    }

    @Override
    public void updateOptions(Satellite.Options options) {
        mClient.stopLocation();
        mClient.setLocationOption(toAMapOption(options));
        mClient.startLocation();
    }

    public void getOnce(Satellite.Options options, final Satellite.Listener listener) {
        mClient = new AMapLocationClient(mContext);
        mClient.setLocationOption(toAMapOption(options));
        mClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                listener.onLocationChanged(transformLocation(aMapLocation));
            }
        });
        mClient.startLocation();
    }

    private static AMapLocationClientOption toAMapOption(Satellite.Options options) {
        AMapLocationClientOption aMapOption = new AMapLocationClientOption();
        aMapOption.setOnceLocation(options.once);
        aMapOption.setInterval(options.interval);
        aMapOption.setNeedAddress(options.address);
        aMapOption.setLocationCacheEnable(options.cache);
        switch (options.mode) {
            case Satellite.MODE_LOW:
                aMapOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
                break;
            case Satellite.MODE_FINE:
                aMapOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Device_Sensors);
                break;
            case Satellite.MODE_HIGH:
                aMapOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                break;
            default:
                aMapOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
                break;
        }
        return aMapOption;
    }

    private static Location transformLocation(AMapLocation aMapLocation) {
        Location location = new Location();
        location.setAccuracy(aMapLocation.getAccuracy());
        location.setLat(aMapLocation.getLatitude());
        location.setLng(aMapLocation.getLongitude());
        location.setAddress(aMapLocation.getAddress());
        location.setCity(aMapLocation.getCity());
        location.setCityCode(aMapLocation.getCityCode());
        location.setCountry(aMapLocation.getCountry());
        location.setProvince(aMapLocation.getProvince());
        location.setStreat(aMapLocation.getStreet());
        location.setAddress(aMapLocation.getAddress());
        location.setTime(aMapLocation.getTime());
        return location;
    }
}
