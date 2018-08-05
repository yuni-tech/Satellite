package yuni.library.satellite;

public class LocationServiceMock implements LocationService {

    private boolean mStarted;
    private Satellite.Listener mListener;
    private Satellite.Options mOptions;

    private FakeClock mClock;
    private Location mLastLocation;

    public LocationServiceMock(FakeClock clock) {
        mClock = clock;
    }

    @Override
    public Location getLastLocation() {
        return mLastLocation;
    }

    @Override
    public boolean isStarted() {
        return mStarted;
    }

    @Override
    public void start(Satellite.Options options) {
        mStarted = true;
    }

    @Override
    public void stop() {
        mStarted = false;
    }

    @Override
    public void setListener(Satellite.Listener listener) {
        mListener = listener;
    }

    @Override
    public void updateOptions(Satellite.Options options) {
        mOptions = options;
    }

    @Override
    public void getOnce(Satellite.Options options, Satellite.Listener listener) {
        Location location = new Location();
        location.setTime(mClock.now);
        listener.onLocationChanged(location);
    }

    public void fireLocaiton() {
        if (!isStarted()) {
            return;
        }
        Location location = new Location();
        location.setTime(mClock.now);
        mLastLocation = location;
        mListener.onLocationChanged(location);
    }

}
