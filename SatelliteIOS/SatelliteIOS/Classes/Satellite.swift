import Foundation
import CoreLocation

public typealias SatelliteLogFunc = (_ message: String) -> Void
public typealias SatelliteListener = (_ lat: Double, _ lng: Double, _ acc: Double, _ error: Error?) -> Void

public protocol SatelliteLogDelegate {
    func log(message: String)
}

public struct Satellite {
    
    public static let shared: Satellite = Satellite()
    
    public struct Options {
        var activityType: CLActivityType = CLActivityType.other
        var distanceFilter: CLLocationDistance = 10
        var desiredAccuracy: CLLocationAccuracy = kCLLocationAccuracyHundredMeters
        
        public func copy() -> Options {
            var options = Options()
            options.activityType = activityType
            options.distanceFilter = distanceFilter
            options.desiredAccuracy = desiredAccuracy
            return options
        }
    }
    
    var log: SatelliteLogFunc = { message in print(message) }
    
    private var locationManager: LocationManager
    
    public init() {
        self.init(locationService: SystemLocationService())
    }
    
    public init(locationService: LocationService) {
        locationManager = LocationManager(locationService: locationService)
    }
    
}
