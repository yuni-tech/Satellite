package yuni.library.satellite;

interface LocationService {

    boolean isStarted();

    void start(Satellite.Options options);

    void stop();

    void setListener(Satellite.Listener listener);

    void updateOptions(Satellite.Options options);

    void getOnce(Satellite.Options options, final Satellite.Listener listener);

}
