////
////  LocationManager.swift
////  attemptingGoogleMaps
////
////  Created by reborn-m1macmini1 on 2021/11/03.
////
//
//import Combine
//import CoreLocation
//
//class LocationManager: NSObject, ObservableObject {
//    private let locationManager = CLLocationManager()
//
//    //1
//    @Published var location:  CLLocation? {
//        willSet { objectWillChange.send() }
//    }
//
//    //2
//    var latitude: CLLocationDegrees {
//        return location?.coordinate.latitude ?? 0
//    }
//
//    var longitude: CLLocationDegrees {
//        return location?.coordinate.longitude ?? 0
//    }
//
//    //3
//
//    override init() {
//        super.init()
//
//        locationManager.delegate = self
//        locationManager.desiredAccuracy = kCLLocationAccuracyBest
//        locationManager.requestWhenInUseAuthorization()
//        locationManager.startUpdatingLocation()
//    }
//}
//
//extension LocationManager: CLLocationManagerDelegate {
//    //4
//
//    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
//        guard let location = locations.last else { return }
//        self.location = location
//    }
//}
