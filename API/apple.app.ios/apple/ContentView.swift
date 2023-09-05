//
//  ContentView.swift
//  apple
//
//  Created by reborn on 2021/11/04.
//

import SwiftUI
import AuthenticationServices
import SwiftUI
final class SignInWithAppleButton: UIViewRepresentable {
  func makeUIView(context: Context) -> ASAuthorizationAppleIDButton {
    return ASAuthorizationAppleIDButton(type: .signIn, style: .whiteOutline)
  }
  func updateUIView(_ uiView: ASAuthorizationAppleIDButton, context: Context) {}
}

import AuthenticationServices
import SwiftUI
struct ContentView: View {
  @State var appleSignInDelegate: SignInWithAppleDelegate! = nil
    
    var body: some View {
        VStack{
            SignInWithAppleButton().frame(width: 280, height: 60)
              .cornerRadius(5)
              .onTapGesture(perform: showAppleLogin)
            Button("로그아웃?"){
                UserDefaults.standard.set(nil, forKey: "userID")
            }
            
        }
            .onAppear(){
                if let userID = UserDefaults.standard.string(forKey: "userID") {
                           
                   // get the login status of Apple sign in for the app
                   // asynchronous
              
                    let provider = ASAuthorizationAppleIDProvider()
                    provider.getCredentialState(forUserID: "currentUserIdentifier") { state, error in
                        switch state {
                            case .authorized: // Credentials are valid.
                                print("user remain logged in, proceed to another view")
                            break
                            case .revoked: // Credential revoked, log them out
                                print("user logged in before but revoked")
                            break
                            case .notFound: // Credentials not found, show login UI
                                print("user haven't log in before")
                            break
                            default:
                                print("unknown state")
                        }
                        
                    }
                    print("111111 ================= 첫 로그인")
                    print(userID)
                }else {
                    self.performExistingAccountSetupFlows()
                }
            }
    }
       
    private func showAppleLogin() {
      appleSignInDelegate = SignInWithAppleDelegate {
          print("로그인 성공?: \($0)")
      }
      let request = ASAuthorizationAppleIDProvider().createRequest()
        request.requestedScopes = [.fullName, .email]
        performSignIn(using: [request])

      let controller = ASAuthorizationController(authorizationRequests: [request])
        controller.delegate = appleSignInDelegate as! ASAuthorizationControllerDelegate
        controller.presentationContextProvider = appleSignInDelegate as! ASAuthorizationControllerPresentationContextProviding
      controller.performRequests()
    }
    private func performSignIn(using requests: [ASAuthorizationRequest]) { appleSignInDelegate = SignInWithAppleDelegate() { success in if success { // update UI
        
    } else { // show the user an error
        
    }
        
    }
        let controller = ASAuthorizationController(authorizationRequests: requests)
        controller.delegate = appleSignInDelegate
        controller.performRequests()
    }
    private func performExistingAccountSetupFlows() { // 1
        #if !targetEnvironment(simulator) // 2
        let requests = [ ASAuthorizationAppleIDProvider().createRequest(), ASAuthorizationPasswordProvider().createRequest() ] // 2
        performSignIn(using: requests) #endif
        
    }

}
import AuthenticationServices
import SwiftUI
class SignInWithAppleDelegate: NSObject {
  private let signInSucceeded: (Bool) -> Void
  init(onSignedIn: @escaping (Bool) -> Void) {
    signInSucceeded = onSignedIn
  }
}
extension SignInWithAppleDelegate: ASAuthorizationControllerDelegate {
  func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
    switch authorization.credential {
    case let appleIdCredential as ASAuthorizationAppleIDCredential:
        let userIdentifier = appleIdCredential.user
             let fullName = appleIdCredential.fullName
             let email = appleIdCredential.email
             
      if let _ = appleIdCredential.email, let _ = appleIdCredential.fullName {
        print("111111 ================= 첫 로그인")
        displayLog(credential: appleIdCredential)
      } else {
        print("222222 ================== 로그인 했었음")
        displayLog(credential: appleIdCredential)
      }
        UserDefaults.standard.set(appleIdCredential.user, forKey: "userID")
      signInSucceeded(true)
    case let passwordCredential as ASPasswordCredential:
      
          // Sign in using an existing iCloud Keychain credential.
          let username = passwordCredential.user
          let password = passwordCredential.password
          
         
    default:
      break
    }
  }
  func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {}
  private func displayLog(credential: ASAuthorizationAppleIDCredential) {
    print("identityToken: \(String(describing: credential.identityToken))\nauthorizationCode: \(credential.authorizationCode!)\nuser: \(credential.user)\nemail: \(String(describing: credential.email))\ncredential: \(credential)")
  }
}
extension SignInWithAppleDelegate: ASAuthorizationControllerPresentationContextProviding {
  func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
    return UIApplication.shared.windows.last!
  }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
