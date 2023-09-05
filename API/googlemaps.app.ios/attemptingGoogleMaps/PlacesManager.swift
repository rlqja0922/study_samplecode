//
//  PlacesManager.swift
//  attemptingGoogleMaps
//
//  Created by reborn-m1macmini1 on 2021/11/03.
//

import GooglePlaces

class PlacesManager: NSObject, ObservableObject {
    //1
    private var placesClient = GMSPlacesClient.shared()
    
    //2
    @Published var places = [GMSPlaceLikelihood]()
    
    override init() {
        super.init()
        currentPlacesList { (places) in
            guard let places = places else { return }
            self.places = places
        }
    }
    
    func currentPlacesList(completion: @escaping (([GMSPlaceLikelihood ]?) -> Void)) {
        //3
        placesClient.currentPlace { (placeLikelyHoodList, error) in
            if let error = error {
                print("Places faild to initialize with error \(error)")
                completion(nil)
                return
            }
            
            guard let placeLikelyHoodList = placeLikelyHoodList else { return }
            self.places = placeLikelyHoodList.likelihoods
            print(self.places)
            completion(self.places)
        }
    }
    
    
    
    
    
    
}
