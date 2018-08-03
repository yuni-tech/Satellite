package yuni.library.satellite;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SatelliteTests {


    FakeClock mClock;
    Location mLocation;
    List<Location> mLocationList;
    LocationServiceMock mLocationServiceMock;

    @Before
    public void before() {
        mClock = new FakeClock();
        mLocation = null;
        mLocationList = new ArrayList<>();
        mLocationServiceMock = new LocationServiceMock(mClock);
    }

    @Test
    public void test() {
        Satellite.shared.init(null, mLocationServiceMock);
        Satellite.shared.getLocationOnce(new Satellite.Options(), new Satellite.Listener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocation = location;
            }
        });

        assertNotNull(mLocation);

        Satellite.Options options = new Satellite.Options();
        Satellite.LocationHandler handler = Satellite.shared.createContinue(options, new Satellite.Listener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocationList.add(location);
            }
        });

        handler.start();

        mLocationServiceMock.fireLocaiton();
        mLocationServiceMock.fireLocaiton();
        mLocationServiceMock.fireLocaiton();

        assertEquals(3, mLocationList.size());
    }

}
