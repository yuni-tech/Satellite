import Foundation
import CoreLocation

public typealias SatelliteLogFunc = (_ message: String) -> Void
public typealias SatelliteListener = (_ error: Error?, _ location: CLLocation?) -> Void

public protocol SatelliteLogDelegate {
    func log(message: String)
}

public class Satellite {
    
    public static let shared: Satellite = Satellite()
    
    public class Options {
        public var distanceFilter: CLLocationDistance = 10
        public var desiredAccuracy: CLLocationAccuracy = kCLLocationAccuracyHundredMeters
        public var cacheTime: TimeInterval = 0
        
        var cache: Bool {
            return cacheTime > 0
        }
        
        public init() {}
        
        public func clone() -> Options {
            let options = Options()
            options.cacheTime = cacheTime
            options.distanceFilter = distanceFilter
            options.desiredAccuracy = desiredAccuracy
            return options
        }
    }
    
    public var log: SatelliteLogFunc = { message in print(message) }
    public var lastLocation: CLLocation? {
        return locationManager.lastLocation
    }
    
    private var locationManager: LocationManager!
    
    public func setup(locationService: LocationService? = nil) {
        if locationService == nil {
            self.locationManager = LocationManager(locationService: SystemLocationService())
        } else {
            self.locationManager = LocationManager(locationService: locationService!)
        }
    }
    
    public func getLocationOnce(options: Options, listener: @escaping SatelliteListener) {
        self.locationManager.getLocationOnce(options: options, listener: listener)
    }
    
    public func createContinue(options: Options, listener: @escaping SatelliteListener) -> LocationHandler {
        return LocationHandler(self.locationManager, options: options, listener: listener)
    }
    
    public class LocationHandler {
        
        weak var locationManager: LocationManager?
        var options: Options
        let listener: SatelliteListener
        var holder: LocationManager.ContinueLocationHolder?
        
        init(_ locationManager: LocationManager, options: Options, listener: @escaping SatelliteListener) {
            self.locationManager = locationManager
            self.listener = listener
            self.options = options
        }
        
        public func setOptions(_ options: Options) {
            self.options = options.clone()
            if let holder = self.holder {
                self.locationManager?.updateOptions(holder: holder, options: self.options)
            }
        }
        
        public func start() {
            if self.holder != nil {
                return
            }
            self.holder = self.locationManager?.startContinue(owner: self, options: options, listener: listener)
        }
        
        public func stop() {
            if let holder = self.holder {
                self.locationManager?.stopContinue(holder: holder)
                self.holder = nil
            }
        }
        
    }
    
}
