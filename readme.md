# Satellite (Developing)

Satellite 皆在提供一个稳定、省电的移动定位库。

使用这个库进行定位时，可以通过设定参数(Options)，开启多组持续或者不限定的单次定位，但底层依然只维持一个定位服务，这个定位服务根据多组复杂的参数而进行最优化调整。

## Android

### Install

```
compile 'yuni.library.satellite:+'
```

### Usage

单次定位:

```java
Satellite.Options options = new Satellite.Options();

// 若要使用缓存，设置缓存时间
options.cacheTime = 5000; // 可获取5秒定位到的缓存位置，否则重新获取

// 获取
Satellite.shared.getLocationOnce(options, location -> {

});
```

持续定位:

```java
Satellite.Options options = new Satellite.Options();
options.interval = 5000; // 5秒间隔时间

// 如果需要逆地理编码
options.address = true;

LocationHandler handler = Satellite.shared.createContinue(options, location -> {
    // 持续输出位置
});

// 开始
handler.start();

// 停止
handler.stop();

// 更改定位参数
Satellite.Options options = new Satellite.Options();
options.interval = 3000; // 3秒间隔时间
options.mode = Satellite.MODE_LOW; // 省电模式
handler.setOptions(options); // 更改后会立即生效
```

## iOS

### Install

```
pod 'SatelliteIOS'
```

### Usage

单次定位:

```swift
var options = Satellite.Options()
options.distanceFilter = 10 // 设置distanceFilter
options.desiredAccuracy = 20 // 设置精度 20米


// 在若要使用缓存，设置缓存时间，仅限单次定位
options.cacheTime = 5000; // 可获取5秒定位到的缓存位置，否则重新获取

Satellite.shared.getLocationOnce(options: options) { error, location in 
  // ...
}
```

持续定位:

```swift
var options = Satellite.Options()
options.distanceFilter = 10 // 设置distanceFilter
options.desiredAccuracy = 20 // 设置精度 20米

let handler = Satellite.shared.createContinue(options: options) { error, location in 
  //...
}

// 开始
handler.start();

// 停止
handler.stop();

// 更改定位参数
var options = new Satellite.Options();
options.distanceFilter = ...
...
handler.setOptions(options); // 更改后会立即生效
```

## MIT License