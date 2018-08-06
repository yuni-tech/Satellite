//
//  ArrayUtils.swift
//  SatelliteIOS_Tests
//
//  Created by 张小明 on 2018/8/5.
//  Copyright © 2018年 CocoaPods. All rights reserved.
//

import Foundation

extension Array {
    
    func compareEach(action: (_ a: Element, _ b: Element) -> Void) {
        for i in 0..<self.count {
            for j in i+1..<self.count {
                action(self[i], self[j])
            }
        }
    }
    
}
