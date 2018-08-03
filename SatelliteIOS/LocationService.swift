//
//  LocationService.swift
//  SatelliteIOS
//
//  Created by mingo on 2018/8/3.
//

import Foundation
import CoreLocation

public protocol LocationService {
    
    var isStarted: Bool { get }
    
    func start(options: Satellite.Options)
    
    func stop()
    
    func setListener(_ listener: @escaping SatelliteListener)
    
    func updateOptions(_ options: Satellite.Options)
    
    func getOnce(options: Satellite.Options, listener: @escaping SatelliteListener)
    
}

class SystemLocationService : NSObject, LocationService {
    
    let locationManager: CLLocationManager
    var isStarted: Bool = false
    var listener: SatelliteListener?
    var onceListeners: [SatelliteListener] = []
    
    override init() {
        locationManager = CLLocationManager()
        super.init()
        locationManager.delegate = self
    }
    
    func start(options: Satellite.Options) {
        isStarted = true
        locationManager.activityType = options.activityType
        locationManager.distanceFilter = options.distanceFilter
        locationManager.desiredAccuracy = options.desiredAccuracy
    }
    
    func stop() {
        isStarted = false
        locationManager.stopUpdatingLocation()
    }
    
    func setListener(_ listener: @escaping SatelliteListener) {
        self.listener = listener
    }
    
    func updateOptions(_ options: Satellite.Options) {
        locationManager.stopUpdatingLocation()
        locationManager.activityType = options.activityType
        locationManager.distanceFilter = options.distanceFilter
        locationManager.desiredAccuracy = options.desiredAccuracy
        locationManager.startUpdatingLocation()
    }
    
    func getOnce(options: Satellite.Options, listener: @escaping SatelliteListener) {
        onceListeners.append(listener)
        if isStarted {
            locationManager.stopUpdatingLocation()
        }
        locationManager.startUpdatingLocation()
    }
    
}

extension SystemLocationService : CLLocationManagerDelegate {
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        
    }
    
}
