import Foundation
import CoreLocation

public typealias SatelliteLogFunc = (_ message: String) -> Void
public typealias SatelliteListener = (_ error: Error?, _ location: CLLocation?) -> Void

public protocol SatelliteLogDelegate {
    func log(message: String)
}

public struct Satellite {
    
    public static let shared: Satellite = Satellite()
    
    public struct Options {
        var distanceFilter: CLLocationDistance = 10
        var desiredAccuracy: CLLocationAccuracy = kCLLocationAccuracyHundredMeters
        var cacheTime: Int64 = 0
        var cache: Bool {
            return cacheTime > 0
        }
        
        public func copy() -> Options {
            var options = Options()
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
