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
    
    var lastLocation: CLLocation? { get }
    
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
    var cacheLocations: [CLLocation] = []
    var lock: NSRecursiveLock = NSRecursiveLock()
    
    var lastLocation: CLLocation? {
        return self.locationManager.location
    }
    
    override init() {
        locationManager = CLLocationManager()
        super.init()
        locationManager.delegate = self
        locationManager.pausesLocationUpdatesAutomatically = false
        locationManager.activityType = .other
    }
    
    func start(options: Satellite.Options) {
        lock.lock(); defer { lock.unlock() }
        isStarted = true
        
        if !isStarted {
            locationManager.stopUpdatingLocation()
        }
    
        locationManager.distanceFilter = options.distanceFilter
        locationManager.desiredAccuracy = options.desiredAccuracy
        locationManager.startUpdatingLocation()
    }
    
    func stop() {
        lock.lock(); defer { lock.unlock() }
        isStarted = false
        locationManager.stopUpdatingLocation()
    }
    
    func setListener(_ listener: @escaping SatelliteListener) {
        self.listener = listener
    }
    
    func updateOptions(_ options: Satellite.Options) {
        lock.lock(); defer { lock.unlock() }
        locationManager.stopUpdatingLocation()
        locationManager.distanceFilter = options.distanceFilter
        locationManager.desiredAccuracy = options.desiredAccuracy
        locationManager.startUpdatingLocation()
    }
    
    func getOnce(options: Satellite.Options, listener: @escaping SatelliteListener) {
        lock.lock(); defer { lock.unlock() }
        onceListeners.append(listener)
        if isStarted {
            if let lastLocation = self.locationManager.location {
                DispatchQueue.main.async { [weak self] in
                    self?.notifyLocation(nil, lastLocation, false)
                }
                return
            }
            self.locationManager.stopUpdatingLocation()
        }
        self.locationManager.startUpdatingLocation()
    }
    
    fileprivate func notifyLocation(_ error: Error?, _ location: CLLocation?, _ delegate: Bool = true) {
        lock.lock(); defer { lock.unlock() }
        
        // notify delegate
        if (delegate) {
            self.listener?(error, location)
        }
        
        // notify once listeners
        self.onceListeners.forEach { listener in listener(error, location) }
        
        // 如果只是做了一次位置获取，则停止
        if (!isStarted) {
            self.locationManager.stopUpdatingLocation()
        }
    }
    
}

extension SystemLocationService : CLLocationManagerDelegate {
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        notifyLocation(error, nil)
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if locations.count <= 0 {
            return
        }
        
        cacheLocations.append(findHighAccurary(locations: locations))
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.15) { [weak self] in
            guard let sself = self else { return }
            let location = sself.findHighAccurary(locations: sself.cacheLocations)
            sself.notifyLocation(nil, location)
        }
    }
    
    private func findHighAccurary(locations: [CLLocation]) -> CLLocation {
        var acc: Double = 9999999999
        var result: CLLocation? = nil
        
        // find hightest location
        locations.forEach { (loc: CLLocation) in
            let locAcc = max(loc.horizontalAccuracy, loc.verticalAccuracy)
            if acc > locAcc {
                result = loc
                acc = locAcc
            }
        }
        
        return result!
    }
    
}
