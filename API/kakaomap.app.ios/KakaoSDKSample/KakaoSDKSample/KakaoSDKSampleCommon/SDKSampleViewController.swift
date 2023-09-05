//  Copyright 2019 Kakao Corp.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.

import UIKit

import SafariServices
import Alamofire

import KakaoSDKCommon
import KakaoSDKAuth
import KakaoSDKUser
import KakaoSDKTalk
import KakaoSDKStory
import KakaoSDKLink
import KakaoSDKNavi
import KakaoSDKTemplate

// MARK: - tableview helper =============================================================================================

public class Row {
    public var title: String
    public var action: (() -> Void)?
    public var apiResult: ApiResult
    public var backgroundColor: UIColor?
    public init(title: String, action: (() -> Void)?, result: ApiResult = .Ready, backgroundColor:UIColor? = nil) {
        self.title = title
        self.action = action
        self.apiResult = result
        self.backgroundColor = backgroundColor
    }
}

public class SectionOfRow {
    public typealias Item = Row
    
    public var header: String
    public var items: [Item]
    public init(header:String, items:[Item]) {
        self.header = header
        self.items = items
    }
    
    required public convenience init(original: SectionOfRow, items: [Item]) {
        self.init(header: original.header, items: original.items)
        self.items = items
    }
}

// MARK: tableview helper =============================================================================================
// MARK: -

extension SDKSampleViewController {
    private func updateResultIndicator(cell:UITableViewCell?, row:Row) {
        
        switch(row.apiResult) {
        case .Ready: cell?.backgroundColor = UIColor.white
        case .Success: cell?.backgroundColor = UIColor.green
        case .Fail: cell?.backgroundColor = UIColor.red
        }
    }
    
    private func updateResultIndicator() {
        if let resultIndicator = self.resultIndicator {
            let cell = self.tableView.cellForRow(at: resultIndicator.0)
            self.updateResultIndicator(cell: cell, row: resultIndicator.1)
        }
    }
    
    private func success(_ object: Any? = nil, updateIndicator: Bool = true) {
        if let object = object {
            print("\(String(describing:object))")
        }
        
        if (updateIndicator) {
            self.resultIndicator?.1.apiResult = .Success
            self.updateResultIndicator()
        }
    }
    
    
    private func errorHandler(error:Error?, updateIndicator: Bool = true) {
        guard error != nil else { return }
        if (updateIndicator) {
            self.resultIndicator?.1.apiResult = .Fail
            self.updateResultIndicator()
        }
        
        if let sdkError = error as? SdkError {
            print("\n\n[sdk error handling start]------------------------------------------------------------------------------")
            
            switch(sdkError) {
            case .ApiFailed:
                let apiError = sdkError.getApiError()
                switch(apiError.reason) {
                default:
                    print("api error: \(String(describing: error))")
                }
                break
            case .AuthFailed:
                let authError = sdkError.getAuthError()
                switch(authError.reason) {
                default:
                    print("auth error: \(String(describing: error))")
                }
                break
            case .ClientFailed:
                print("client error: \(String(describing: error))")
                break
            }
            print("[sdk error handling end]--------------------------------------------------------------------------------\n")
        }
        else {
            print("\n\n[not sdk error start]------------------------------------------------------------------------------")
            if let nsError = error as NSError? {
                print("normal error: \(String(describing: nsError))")
            }
            print("[not sdk error end]------------------------------------------------------------------------------\n")
        }
    }
}


public class SDKSampleViewController: SDKSampleBaseViewController, UITableViewDelegate, UITableViewDataSource {
    
//    lazy public var dataSource = RxTableViewSectionedReloadDataSource<SectionOfRow>(
//        configureCell: { (dataSource, tableView, indexPath, item) in
//            let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
//
//            self.updateResultIndicator(cell:cell, row:item)
//
////            cell.selectionStyle = UITableViewCell.SelectionStyle.none
//            cell.textLabel?.adjustsFontSizeToFitWidth = true
//            cell.textLabel?.text = "\(item.title)"
//            cell.textLabel?.backgroundColor = UIColor.init(white: 1.0, alpha: 0)
//            cell.textLabel?.textColor = UIColor.black
//            return cell
//        })
    
    var safariViewController : SFSafariViewController?
    
    
    //for ui
    var resultIndicator : (IndexPath, Row)?
    
    var recursiveAppFriendsCompletion : ((Friends<Friend>?, Error?) -> Void)!
    
    override public func loadView() {
        super.loadView()
        
        setupViews()
    }
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor.white
        self.tableView.delegate = self
        self.tableView.dataSource = self
        
//        setMenu()
    }
    
    override public func setupViews() {
        super.setupViews()
        self.tableView.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
    }
    
    public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let item = self.sections![indexPath.section].items[indexPath.row]
        if let action = item.action {
            action()
            
            self.resultIndicator = (indexPath, item)
            
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1 * 1) {
               tableView.deselectRow(at: indexPath, animated: false)
            }
        }
    }
    
    public func numberOfSections(in tableView: UITableView) -> Int {
        if let sections = self.sections, sections.count != 0 {
            print("sections count: ", sections.count)
            return sections.count
        }
        else {
            return 0
        }
    }
    
    public func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        if let sections = self.sections, sections.count != 0 {
            return sections[section].header
        }
        else {
            return nil
        }
    }
    
    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let sections = self.sections, sections.count != 0 {
            return sections[section].items.count
        }
        else {
            return 0
        }
    }

    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
        
        let item = self.sections![indexPath.section].items[indexPath.row]
        
        self.updateResultIndicator(cell:cell, row:item)
        cell.textLabel?.adjustsFontSizeToFitWidth = true
        cell.textLabel?.text = "\(item.title)"
        cell.textLabel?.backgroundColor = UIColor.init(white: 1.0, alpha: 0)
        cell.textLabel?.textColor = UIColor.black
        
        if let backgroundColor = item.backgroundColor {
            cell.backgroundColor = backgroundColor
        }
        
        return cell
    }
    
    public func setTitle(title:String = "카카오 링크, 씽크 샘플 코드", args: String? = nil) {
        self.navigationItem.title = (args != nil) ? "\(title)(\(args!))" : "\(title)"
    }
    
    func extractPickerElements(friends:Friends<Friend>) -> [(URL?, String?, Any)]? {
        return friends.elements?.map({ (friend) -> (URL?, String?, Any) in
            return (friend.profileThumbnailImage, friend.profileNickname, friend.uuid)
        })
    }
    
    func setMenu() {
        self.sections = [
            
//            // MARK: - section kakaolink start =============================================================================================
            SectionOfRow(header: "카카오 링크", items: [
                Row(title: "defaultLink(templatable:) - webSharer", action: {

                    let feedTemplateJsonStringData =
                      """
                      {
                          "object_type": "feed",
                          "content": {
                              "title": "딸기 치즈 케익",
                              "description": "#케익 #딸기 #삼평동 #카페 #분위기 #소개팅",
                              "image_url": "http://mud-kage.kakao.co.kr/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
                              "link": {
                                  "mobile_web_url": "https://developers.kakao.com",
                                  "web_url": "https://developers.kakao.com"
                              }
                          },
                          "item_content" : {
                                "profile_text" :"Kakao",
                                "profile_image_url" :"http://mud-kage.kakao.co.kr/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
                                "title_image_url" : "http://mud-kage.kakao.co.kr/dn/Q2iNx/btqgeRgV54P/VLdBs9cvyn8BJXB3o7N8UK/kakaolink40_original.png",
                                "title_image_text" :"Cheese cake",
                                "title_image_category" : "cake",
                                "items" : [
                                                {
                                                    "item" :"cake1",
                                                    "item_op" : "1000원",
                                                },
                                                {
                                                    "item" :"cake2",
                                                    "item_op" : "2000원",
                                                },
                                                {
                                                    "item" :"cake3",
                                                    "item_op" : "3000원",
                                                },
                                                {
                                                    "item" :"cake4",
                                                    "item_op" : "4000원",
                                                },
                                                {
                                                    "item" :"cake5",
                                                    "item_op" : "5000원",
                                                }
                                            ],
                                            "sum" :"total",
                                            "sum_op" : "15000원"
                          },
                          "social": {
                              "comment_count": 45,
                              "like_count": 286,
                              "shared_count": 845
                          },
                          "buttons": [
                              {
                                  "title": "웹으로 보기",
                                  "link": {
                                      "mobile_web_url": "https://developers.kakao.com",
                                      "web_url": "https://developers.kakao.com"
                                  }
                              },
                              {
                                  "title": "앱으로 보기",
                                  "link": {
                                      "android_execution_params": "key1=value1&key2=value2",
                                      "ios_execution_params": "key1=value1&key2=value2"
                                  }
                              }
                          ]
                      }
                      """.data(using: .utf8)!

                    if let templatable = try? SdkJSONDecoder.custom.decode(FeedTemplate.self, from: feedTemplateJsonStringData) {
                        if let url = LinkApi.shared.makeSharerUrlforDefaultLink(templatable: templatable) {
                            print("=============================sharer default url: \(url)")
                            self.safariViewController = SFSafariViewController(url: url)
                            self.safariViewController?.modalTransitionStyle = .crossDissolve
                            self.safariViewController?.modalPresentationStyle = .overCurrentContext
                            self.present(self.safariViewController!, animated: true) {
                                print("웹 present success")
                            }
                        }
                    }
                }),
                Row(title: "scrapLink(requestUrl:) - webSharer", action: {
                    if let url = LinkApi.shared.makeSharerUrlforScrapLink(requestUrl: "https://developers.kakao.com") {
                        print("=============================sharer scrap url: \(url)")
                        self.safariViewController = SFSafariViewController(url: url)
                        self.safariViewController?.modalTransitionStyle = .crossDissolve
                        self.safariViewController?.modalPresentationStyle = .overCurrentContext
                        self.present(self.safariViewController!, animated: true) {
                            print("웹 present success")
                        }
                    }
                }),
                Row(title: "scrapLink(requestUrl:templateId:) - webSharer", action: {
                    if let url = LinkApi.shared.makeSharerUrlforScrapLink(requestUrl: "https://developers.kakao.com", templateId: AppDelegate.templateId().link ) {
                        print("=============================sharer scrap url: \(url)")
                        self.safariViewController = SFSafariViewController(url: url)
                        self.safariViewController?.modalTransitionStyle = .crossDissolve
                        self.safariViewController?.modalPresentationStyle = .overCurrentContext
                        self.present(self.safariViewController!, animated: true) {
                            print("웹 present success")
                        }
                    }
                }),
                Row(title: "customLink(templateId:,templateArgs:) - webSharer", action: {
                    if let url = LinkApi.shared.makeSharerUrlforCustomLink(templateId:AppDelegate.templateId().link, templateArgs:["title":"제목입니다.", "description":"설명입니다."]) {
                        print("=============================sharer custom url: \(url)")
                        self.safariViewController = SFSafariViewController(url: url)
                        self.safariViewController?.modalTransitionStyle = .crossDissolve
                        self.safariViewController?.modalPresentationStyle = .overCurrentContext
                        self.present(self.safariViewController!, animated: true) {
                            print("웹 present success")
                        }
                    }
                }),
              
            ]),
//            // section kakaolink end =============================================================================================

            
            // MARK: - section kakaosync start =============================================================================================
            SectionOfRow(header: "카카오 씽크", items: [
                 Row(title: "약관선택 로그인", action: {
                    
                    let serviceTerms = ["service", "policy"]
                    
                    UserApi.shared.loginWithKakaoAccount(serviceTerms: serviceTerms, completion: {[weak self](oauthToken, error) in
                        if let error = error {
                            self?.errorHandler(error: error)
                        }
                        else {
                            self?.success()
                            
                            //do something
                            _ = oauthToken
                        }
                    })
                 }),
                Row(title: "약관선택 로그인 with prompt", action: {
                   
                   let serviceTerms = ["service", "policy"]
                   
                    UserApi.shared.loginWithKakaoAccount(prompts:[.Login], serviceTerms: serviceTerms, completion: {[weak self](oauthToken, error) in
                       if let error = error {
                           self?.errorHandler(error: error)
                       }
                       else {
                           self?.success()
                           
                           //do something
                           _ = oauthToken
                       }
                   })
                })
            ]),
            // section kakaosync start =============================================================================================
            
            
            
        ]
    }
}

class TestCustomTokenManager : TokenManagable {
    public static let tokenKey = "test_token_key"
    
    
    func getToken() -> OAuthToken? {
        print(">> TestCustomTokenManager.getToken() called.")
        
        if let data = UserDefaults.standard.data(forKey: TestCustomTokenManager.tokenKey) {
            let token = try? JSONDecoder().decode(OAuthToken.self, from:data)
            print(">> loaded token: \(String(describing:token))\n\n")
            
            return token
        }
        return nil
    }
    
    func setToken(_ token: OAuthToken?) {
        print(">> TestCustomTokenManager.setToke(_) called. token: \(String(describing:token)) ")
        
        if let data = try? JSONEncoder().encode(token) {
            print(">> 토큰 암호화를 권장합니다.\n\n")
            
            UserDefaults.standard.set(data, forKey:TestCustomTokenManager.tokenKey)
            UserDefaults.standard.synchronize()
        }
    }
    
    func deleteToken() {
        print(">> TestCustomTokenManager.deleteToken() called.")
        
        UserDefaults.standard.removeObject(forKey: TestCustomTokenManager.tokenKey)
    }
}
