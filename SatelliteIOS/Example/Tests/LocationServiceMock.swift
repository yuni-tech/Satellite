//
//  LocationServiceMock.swift
//  SatelliteIOS_Tests
//
//  Created by mingo on 2018/8/5.
//  Copyright © 2018年 CocoaPods. All rights reserved.
//

import Foundation
import CoreLocation
import SatelliteIOS

class LocationServiceMock : LocationService {
    
    var isStarted: Bool = false
    
    var lastLocation: CLLocation?
    var listener: SatelliteListener?
    var options: Satellite.Options?
    
    func start(options: Satellite.Options) {
        isStarted = true
        self.options = options
    }
    
    func stop() {
        isStarted = false
    }
    
    func setListener(_ listener: @escaping SatelliteListener) {
        self.listener = listener
    }
    
    func updateOptions(_ options: Satellite.Options) {
        self.options = options
    }
    
    func getOnce(options: Satellite.Options, listener: @escaping SatelliteListener) {
        fireLocation(once: true)
        listener(nil, lastLocation)
    }
    
    func fireLocation(once: Bool = false) {
        if !isStarted && !once {
            return
        }
        lastLocation = makeLocation()
        listener?(nil, lastLocation)
    }
    
    private func makeLocation() -> CLLocation {
        let location = CLLocation(coordinate: CLLocationCoordinate2D(latitude: 0, longitude: 0), altitude: 0, horizontalAccuracy: 0, verticalAccuracy: 0, timestamp: Date())
        return location
    }
    
}
