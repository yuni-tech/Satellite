package yuni.library.satellite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LocationManagerTests {

    LocationManager mLocationManager;
    LocationServiceMock mLocationServiceMock;
    Location mLocation;
    Location mLocation2;
    Location mLocation3;
    int locationTimes = 10;
    List<Location> mLocations;
    FakeClock mClock;
    List<LocationManager.ContinueLocationHolder> mHolders;


    @Before
    public void before() {
        TestUtils.setup();

        mClock = new FakeClock();
        mLocationServiceMock = new LocationServiceMock(mClock);

        mLocationManager = spy(new LocationManager(mLocationServiceMock));
        doAnswer(mClock.asAnswer()).when(mLocationManager).getCurrentTime();

        mLocation = null;
        mLocation2 = null;
        mLocations = new ArrayList<>();
        mHolders = new ArrayList<>();
    }

    @After
    public void after() {
        mLocationManager = null;
        mLocation = null;
        mLocation2 = null;
    }

    @Test
    public void testGetOnceNotCache() {
        Satellite.Options options = new Satellite.Options();
        mLocationManager.getLocationOnce(options, location -> mLocation = location);
        assertNotNull(mLocation);
        assertNotNull(mLocationManager.getLastLocation());
        assertEquals(mLocation, mLocationManager.getLastLocation());
    }

    @Test
    public void testGetOnceCache() {
        Satellite.Options options = new Satellite.Options();
        options.cacheTime = 1000;

        mLocationManager.getLocationOnce(options, location -> mLocation = location);

        assertNotNull(mLocation);
        assertNotNull(mLocationManager.getLastLocation());
        assertEquals(mLocation, mLocationManager.getLastLocation());

        mClock.walk(500);

        mLocationManager.getLocationOnce(options, location -> mLocation2 = location);


        assertNotNull(mLocation2);
        assertNotNull(mLocationManager.getLastLocation());
        assertEquals(mLocation2, mLocationManager.getLastLocation());
        assertEquals(mLocation, mLocation2);

        mClock.walk(600);

        mLocationManager.getLocationOnce(options, location -> mLocation3 = location);

        assertNotNull(mLocation3);
        assertNotNull(mLocationManager.getLastLocation());
        assertEquals(mLocation3, mLocationManager.getLastLocation());
        assertNotEquals(mLocation3, mLocation2);

    }

    @Test
    public void testContinueLocation() {
        Satellite.Options options = new Satellite.Options();
        options.interval = 1000;
        mLocationManager.startContinue(this, options, location -> mLocations.add(location));

        for(int i=0; i<locationTimes; i++) {
            mClock.walk(options.interval);
            mLocationServiceMock.fireLocaiton();
        }

        assertEquals(locationTimes, mLocations.size());
    }

    @Test
    public void testMultipleContinueLocation() {
        Satellite.Options options = new Satellite.Options();
        options.interval = 1000;

        mLocationManager.startContinue(this, options, location -> mLocations.add(location));
        mLocationManager.startContinue(this, options, location -> mLocations.add(location));
        mLocationManager.startContinue(this, options, location -> mLocations.add(location));

        for(int i=0; i<locationTimes; i++) {
            mClock.walk(options.interval);
            mLocationServiceMock.fireLocaiton();
        }


        assertEquals(locationTimes * 3, mLocations.size());
    }

    @Test
    public void testComplexUsageContinueLocation() {
        Satellite.Options options = new Satellite.Options();
        options.interval = 5000;
        options.address = false;
        options.mode = Satellite.MODE_FINE;
        LocationManager.ContinueLocationHolder holder1 = mLocationManager.startContinue(this, options, location -> mLocations.add(location));

        System.out.println(mLocationManager.getUsingOptions());
        assertEquals(options, mLocationManager.getUsingOptions());

        options = new Satellite.Options();
        options.interval = 3000;
        options.address = false;
        options.mode = Satellite.MODE_LOW;
        LocationManager.ContinueLocationHolder holder2 = mLocationManager.startContinue(this, options, location -> mLocations.add(location));

        Satellite.Options expected = new Satellite.Options();
        expected.interval = 3000;
        expected.address = false;
        expected.mode = Satellite.MODE_FINE;

        System.out.println(mLocationManager.getUsingOptions());
        assertEquals(expected, mLocationManager.getUsingOptions());

        options = new Satellite.Options();
        options.interval = 100000;
        options.address = true;
        options.mode = Satellite.MODE_HIGH;
        LocationManager.ContinueLocationHolder holder3 = mLocationManager.startContinue(this, options, location -> mLocations.add(location));

        expected = new Satellite.Options();
        expected.interval = 3000;
        expected.address = true;
        expected.mode = Satellite.MODE_HIGH;

        System.out.println(mLocationManager.getUsingOptions());
        assertEquals(expected, mLocationManager.getUsingOptions());

        mLocationManager.stopContinue(holder2);

        expected = new Satellite.Options();
        expected.interval = 5000;
        expected.address = true;
        expected.mode = Satellite.MODE_HIGH;

        System.out.println(mLocationManager.getUsingOptions());
        assertEquals(expected, mLocationManager.getUsingOptions());

        mLocationManager.stopContinue(holder1);
        mLocationManager.stopContinue(holder3);

        assertTrue(!mLocationServiceMock.isStarted());
        assertNull(mLocationManager.getUsingOptions());
    }

    @Test
    public void testWeakUsageContinueLocation() {

        mLocationManager = new LocationManager(new LocationServiceMock(mClock));

        Satellite.Options options = new Satellite.Options();
        options.interval = 5000;
        options.address = false;
        options.mode = Satellite.MODE_FINE;

        Object owner = new Object();
        mLocationManager.startContinue(owner, options, new Satellite.Listener() {
            @Override
            public void onLocationChanged(Location location) {
                mLocations.add(location);
            }
        });

        System.out.println(mLocationManager.getUsingOptions());
        assertEquals(options, mLocationManager.getUsingOptions());

        owner = null;
        System.gc();

        mLocationManager.checkOwners();

        assertNull(mLocationManager.getUsingOptions());
    }

}
