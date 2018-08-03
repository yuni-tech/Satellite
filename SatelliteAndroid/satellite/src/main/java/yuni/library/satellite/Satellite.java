package yuni.library.satellite;

import android.content.Context;
import android.util.Log;

import java.util.Objects;

public class Satellite {

    public static final String TAG = Satellite.class.getName();

    /* 精度模式 */
    public static final int MODE_LOW = 100;
    public static final int MODE_FINE = 200;
    public static final int MODE_HIGH = 300;

    public static final Satellite shared = new Satellite();

    /**
     * 定位参数配置
     */
    public static class Options {
        /**
         *  为true时只获取一次，为false时开启持续定位
         */
        public boolean once = false;

        /**
         *  当once为false，此值有效，持续定位间隔
         */
        public long interval = 10000;

        /**
         * 是否返回逆地理位置信息
         */
        public boolean address = false;

        /**
         * 是否可以用缓存的位置
         */
        public boolean cache = false;

        /**
         * 当cache为true，设置可获取缓存位置时间，如果最后一次位置超时了则重新获取
         */
        public long cacheTime = 0;

        /**
         * 精度模式
         */
        public int mode = MODE_HIGH;

        public Options copy() {
            Options options = new Options();
            options.once = once;
            options.interval = interval;
            options.address = address;
            options.cache = cache;
            options.cacheTime = cacheTime;
            options.mode = mode;
            return options;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Options options = (Options) o;
            return once == options.once &&
                    interval == options.interval &&
                    address == options.address &&
                    cache == options.cache &&
                    cacheTime == options.cacheTime &&
                    mode == options.mode;
        }

        @Override
        public int hashCode() {
            return Objects.hash(once, interval, address, cache, cacheTime, mode);
        }

        @Override
        public String toString() {
            return "Options{" +
                    "once=" + once +
                    ", interval=" + interval +
                    ", address=" + address +
                    ", cache=" + cache +
                    ", cacheTime=" + cacheTime +
                    ", mode=" + mode +
                    '}';
        }
    }

    public interface Logger {
        void log(int level, String tag, String message);
    }

    public interface Listener {
        void onLocationChanged(Location location);
    }

    Logger mLogger = new Logger() {
        @Override
        public void log(int level, String tag,  String message) {
            Log.println(level, tag, message);
        }
    };

    private LocationService mLocationService;
    private LocationManager mLocationManager;


    Satellite() {}

    public void init(Context context) {
       this.init(context, new AMapLocationService(context));
    }

    public void init(Context context, LocationService locationService) {
        mLocationService = locationService;
        mLocationManager = new LocationManager(mLocationService);
    }

    public void log(int level, String message) {
        mLogger.log(level, TAG, message);
    }

    public void setLogger(Logger logger) {
        if (logger == null) {
            return;
        }
        mLogger = logger;
    }

    public void getLocationOnce(Options options, Listener listener) {
        options.once = true;
        mLocationManager.getLocationOnce(options, listener);
    }

    public LocationHandler createContinue(Options options, Listener listener) {
        options.once = false;
        return new LocationHandler(options, listener);
    }

    public Location getLastLocation() {
        return mLocationManager.getLastLocation();
    }

    public class LocationHandler {

        private Options mOptions;
        private Listener mListener;
        private LocationManager.ContinueLocationHolder mHolder;

        LocationHandler(Options options, Listener listener) {
            mOptions = options;
            mListener = listener;
        }

        public void setOptions(Options options) {
            options.once = false;
            mOptions = options;
            if (mHolder != null) {
                mLocationManager.updateOptions(mHolder, mOptions);
            }
        }

        public void stop() {
            if (mHolder == null) {
                return;
            }
            mLocationManager.stopContinue(mHolder);
            mHolder = null;
        }

        public void start() {
            if (mHolder != null) {
                return;
            }
            mHolder = mLocationManager.startContinue(this, mOptions, mListener);
        }

    }

}
