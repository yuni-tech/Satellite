Pod::Spec.new do |s|
  s.name             = 'SatelliteIOS'
  s.version          = '0.1.0'
  s.summary          = 'A location library'
  s.description      = <<-DESC
  iOS location library
                       DESC

  s.homepage         = 'https://github.com/yuni-tech/Satellite'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'mingo' => 'zzmingo@qq.com' }
  s.source           = { :git => 'https://github.com/yuni-tech/Satellite.git', :tag => s.version.to_s }

  s.ios.deployment_target = '8.0'

  s.source_files = 'SatelliteIOS/Classes/**/*'
end
