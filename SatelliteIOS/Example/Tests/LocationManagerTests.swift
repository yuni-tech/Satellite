//
//  LocationManagerTests.swift
//  SatelliteIOS_Tests
//
//  Created by mingo on 2018/8/5.
//  Copyright © 2018年 CocoaPods. All rights reserved.
//

import Foundation
import Quick
import CoreLocation
import Nimble
import SatelliteIOS

class LocationManagerTests: QuickSpec {
    
    override func spec() {
        
        describe("once location") {
            
            var locations: [CLLocation] = []
            
            beforeEach {
                locations.removeAll()
                Satellite.shared.setup(locationService: LocationServiceMock())
            }
            
            it("test") {
                let options = Satellite.Options()
                Satellite.shared.getLocationOnce(options: options) { error, location in
                    expect(location).toNot(beNil())
                }
            }
            
            it("not cache") {
                let options = Satellite.Options()
                for _ in 0..<10 {
                    Satellite.shared.getLocationOnce(options: options) { error, location in
                        expect(location).toNot(beNil())
                        locations.append(location!)
                    }
                }
                
                locations.compareEach { a, b in
                    expect(a).toNot(equal(b))
                }
            }
            
            it("cache") {
                var options = Satellite.Options()
                options.cacheTime = 0.1
                for _ in 0..<10 {
                    Satellite.shared.getLocationOnce(options: options) { error, location in
                        expect(location).toNot(beNil())
                        locations.append(location!)
                    }
                }
                
                locations.compareEach { a, b in
                    expect(a).to(equal(b))
                }
                
                Thread.sleep(forTimeInterval: options.cacheTime.advanced(by: 0.1))
                
                Satellite.shared.getLocationOnce(options: options) { error, location in
                    expect(location).toNot(beNil())
                    expect(location).toNot(equal(locations[0]))
                }
            }
        }
        
        describe("continue location") {
            
            var locations: [CLLocation] = []
            var locationService: LocationServiceMock!
            
            beforeEach {
                locations.removeAll()
                locationService = LocationServiceMock()
                Satellite.shared.setup(locationService: locationService)
            }
            
            it("test") {
                let times = 10
                let options = Satellite.Options()
                let locationHandler = Satellite.shared.createContinue(options: options) { error, location in
                    expect(location).toNot(beNil())
                    locations.append(location!)
                }
                
                locationHandler.start()
                
                for _ in (0..<times) {
                    locationService.fireLocation()
                }
                
                expect(locations.count).to(equal(times))
            }
            
            it("multiple") {
                
                let optionsA = Satellite.Options()
                optionsA.desiredAccuracy = 100
                optionsA.distanceFilter = 200
                
                let handlerA = Satellite.shared.createContinue(options: optionsA) { error, location in
                    expect(location).toNot(beNil())
                    locations.append(location!)
                }
                handlerA.start()
                
                expect(locationService.options?.desiredAccuracy).to(equal(optionsA.desiredAccuracy))
                expect(locationService.options?.distanceFilter).to(equal(optionsA.distanceFilter))
                
                let optionsB = Satellite.Options()
                optionsB.desiredAccuracy = 200
                optionsB.distanceFilter = 100
                
                let handlerB = Satellite.shared.createContinue(options: optionsB) { error, location in
                    expect(location).toNot(beNil())
                    locations.append(location!)
                }
                handlerB.start()
                
                expect(locationService.options?.desiredAccuracy).to(equal(optionsA.desiredAccuracy))
                expect(locationService.options?.distanceFilter).to(equal(optionsB.distanceFilter))
                
                let optionsC = Satellite.Options()
                optionsC.desiredAccuracy = 50
                optionsC.distanceFilter = 120
                
                let handlerC = Satellite.shared.createContinue(options: optionsC) { error, location in
                    expect(location).toNot(beNil())
                    locations.append(location!)
                }
                handlerC.start()
                
                expect(locationService.options?.desiredAccuracy).to(equal(optionsC.desiredAccuracy))
                expect(locationService.options?.distanceFilter).to(equal(optionsB.distanceFilter))
                
                handlerB.stop()
                
                expect(locationService.options?.desiredAccuracy).to(equal(optionsC.desiredAccuracy))
                expect(locationService.options?.distanceFilter).to(equal(optionsC.distanceFilter))
                
                let optionsD = Satellite.Options()
                optionsD.desiredAccuracy = 20
                optionsD.distanceFilter = 5
                handlerA.setOptions(optionsD)
                
                expect(locationService.options?.desiredAccuracy).to(equal(optionsD.desiredAccuracy))
                expect(locationService.options?.distanceFilter).to(equal(optionsD.distanceFilter))
            }
            
        }
    }
}
