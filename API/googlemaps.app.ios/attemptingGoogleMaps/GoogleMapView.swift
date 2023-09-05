import SwiftUI
import GoogleMaps



struct GoogleMapView: UIViewRepresentable {
    
    
    
    
    //1
    //@ObservedObject var locationManager = LocationManager()
    private let zoom: Float = 15.0
    
   
    
    //2
    func makeUIView(context: Self.Context) -> GMSMapView {
        
        let camera = GMSCameraPosition.camera(withLatitude: 37.3, longitude: 126.9, zoom: 7.4)
        let mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
        return mapView
    }
    
    //3
    
    func updateUIView(_ mapView: GMSMapView, context: Context) {
        //let camera = GMSCameraPosition.camera(withLatitude: locationManager.latitude, longitude: locationManager.longitude, zoom: zoom)
//        mapView.animate(toLocation: CLLocationCoordinate2D(latitude: locationManager.latitude, longitude: locationManager.longitude))
    }
}

struct GoogleMapView_Previews: PreviewProvider {
    static var previews: some View {
        GoogleMapView()
    }
}
