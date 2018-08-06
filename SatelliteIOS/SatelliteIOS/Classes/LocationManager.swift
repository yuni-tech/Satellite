//
//  LocationManager.swift
//  SatelliteIOS
//
//  Created by mingo on 2018/8/3.
//

import Foundation
import CoreLocation

class LocationManager {
    
    let locationService: LocationService
    
    var lock: NSRecursiveLock = NSRecursiveLock()
    private(set) var usingOptions: Satellite.Options?
    private var holderList: [ContinueLocationHolder] = []
    
    var lastLocation: CLLocation? {
        return _lastLocation ?? locationService.lastLocation
    }
    
    private var _lastLocation: CLLocation?
    
    init(locationService: LocationService) {
        self.locationService = locationService
        self.locationService.setListener { [weak self] (error, location) in
            guard let sself = self else { return }
            guard let location = location else { return }
            sself._lastLocation = location
            sself.notifyLocationChanged(location)
        }
    }
    
    func startContinue(owner: AnyObject, options: Satellite.Options, listener: @escaping SatelliteListener) -> ContinueLocationHolder {
        lock.lock(); defer { lock.unlock() }
        
        let holder = ContinueLocationHolder()
        holder.owner = owner
        holder.options = options
        holder.listener = listener
        holderList.append(holder)
        
        if computeOptions() {
            startOrStop()
        }
        
        return holder
    }
    
    func stopContinue(holder: ContinueLocationHolder) {
        lock.lock(); defer { lock.unlock() }
        
        var removed = false
        for i in (0...holderList.count-1) {
            if holder == holderList[i] {
                holderList.remove(at: i)
                removed = true
                break
            }
        }
        
        if !removed {
            return
        }
        
        if computeOptions() {
            startOrStop()
        }
    }
    
    func updateOptions(holder: ContinueLocationHolder, options: Satellite.Options) {
        lock.lock(); defer { lock.unlock() }
        
        holder.options = options.clone()
        if computeOptions() {
            startOrStop()
        }
    }
    
    func getLocationOnce(options: Satellite.Options, listener: @escaping SatelliteListener) {
        if let lastLocation = self.lastLocation, options.cache {
            let now = getCurrentTime()
            if now - lastLocation.timestamp.timeIntervalSince1970 < options.cacheTime {
                listener(nil, lastLocation)
                return
            }
        }
        locationService.getOnce(options: options.clone(), listener: listener)
    }
    
    func checkOwners() {
        if computeOptions() {
            startOrStop()
        }
    }
    
    func getCurrentTime() -> TimeInterval {
        return Date().timeIntervalSince1970
    }
    
    private func computeOptions() -> Bool {
        lock.lock(); defer { lock.unlock() }
        
        var distanceFilter: CLLocationDistance = 999999999
        var desiredAccuracy: CLLocationAccuracy = 999999999
        
        for i in (0..<holderList.count).reversed() {
            let holder = holderList[i]
            if holder.owner == nil {
                holderList.remove(at: i)
                continue
            }
            
            let options = holder.options!
            if options.distanceFilter < distanceFilter {
                distanceFilter = options.distanceFilter
            }
            if options.desiredAccuracy < desiredAccuracy {
                desiredAccuracy = options.desiredAccuracy
            }
        }
        
        if holderList.isEmpty {
            return true
        }
        
        var changed = false
        if self.usingOptions == nil {
            self.usingOptions = Satellite.Options()
            changed = true
        }
        
        var options = self.usingOptions!
        
        if options.desiredAccuracy != desiredAccuracy {
            options.desiredAccuracy = desiredAccuracy
            changed = true
        }
        
        if options.distanceFilter != distanceFilter {
            options.distanceFilter = distanceFilter
            changed = true
        }
        
        self.usingOptions = options
        
        if !self.locationService.isStarted {
            return true
        }
        
        return changed
    }
    
    private func startOrStop() {
        lock.lock(); defer { lock.unlock() }
        
        if holderList.isEmpty {
            self.locationService.stop()
            self.usingOptions = nil
            return
        }
        
        if self.locationService.isStarted {
            self.locationService.updateOptions(self.usingOptions!)
        } else {
            self.locationService.start(options: self.usingOptions!)
        }
    }
    
    private func notifyLocationChanged(_ location: CLLocation) {
        lock.lock(); defer { lock.unlock() }
        
        for holder in holderList {
            if holder.owner != nil {
                holder.listener(nil, location)
            }
        }
        
        if computeOptions() {
            startOrStop()
        }
    }
    
    class ContinueLocationHolder : Equatable {
        
        static func == (lhs: ContinueLocationHolder, rhs: ContinueLocationHolder) -> Bool {
            return lhs == rhs
        }
        
        weak var owner: AnyObject?
        var options: Satellite.Options!
        var listener: SatelliteListener!
    }
    
}
