//
//  ContentView.swift
//  ios_share
//
//  Created by reborn on 2021/10/27.
//

import SwiftUI
import FirebaseDynamicLinks

struct ContentView: View {
    @State private var isSharePresented: Bool = false
    @State private var isSharePresented1: Bool = false
    @State var url : URL =  URL(string: "https://lkbshareapp.page.link/rniX")!
      var body: some View {
          Button("Share app") {
              self.isSharePresented = true
          }
          .frame(width: 200, height: 100)
          .sheet(isPresented: $isSharePresented, onDismiss: {
              print("Dismiss")
          }, content: {
              ActivityViewController(activityItems: [URL(string: "https://www.apple.com")!])
          })
          
          
          Button("share_firebase"){
              url = generateContentLink()
              self.isSharePresented1 = true
          }
          .frame(width: 200, height: 100)
          .sheet(isPresented: $isSharePresented1, onDismiss: {
              print("Dismiss")
          }, content: {
              ActivityViewController(activityItems: [url])
          })
      }
    func generateContentLink() -> URL {
      let baseURL = URL(string: "https://lkbshareapp.page.link/rniX")!
      let domain = "https://lkbshareapp.page.link/rniX"
      let linkBuilder = DynamicLinkComponents(link: baseURL, domainURIPrefix: domain)
      linkBuilder?.iOSParameters = DynamicLinkIOSParameters(bundleID: "kb.ios-share")
      linkBuilder?.androidParameters =
          DynamicLinkAndroidParameters(packageName: "com.example.shareapp")

      // Fall back to the base url if we can't generate a dynamic link.
      return linkBuilder?.link ?? baseURL
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}

import UIKit
import SwiftUI

struct ActivityViewController: UIViewControllerRepresentable {

    var activityItems: [Any]
    var applicationActivities: [UIActivity]? = nil

    func makeUIViewController(context: UIViewControllerRepresentableContext<ActivityViewController>) -> UIActivityViewController {
        let controller = UIActivityViewController(activityItems: activityItems, applicationActivities: applicationActivities)
        return controller
    }

    func updateUIViewController(_ uiViewController: UIActivityViewController, context: UIViewControllerRepresentableContext<ActivityViewController>) {}

}
