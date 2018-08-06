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
                locations = []
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
                options.cacheTime = 1000
                for _ in 0..<10 {
                    Satellite.shared.getLocationOnce(options: options) { error, location in
                        expect(location).toNot(beNil())
                        locations.append(location!)
                    }
                }
                
                locations.compareEach { a, b in
                    expect(a).to(equal(b))
                }
            }
            
        }
    }
}
