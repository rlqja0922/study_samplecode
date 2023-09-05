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

public class SampleError : Error {
    var msg : String?
    
    public init(_ msg : String? = "Unknown" ) {
        self.msg = msg
    }
}

public enum ApiResult {
    case Ready
    case Success
    case Fail
}

open class SDKSampleBaseViewController: UIViewController {
    
    public let tableView: UITableView = UITableView(frame: CGRect.zero, style: .plain)
    public var safeArea: UILayoutGuide!

    public var sections : [SectionOfRow]?
    
    public init() {
        super.init(nibName: nil, bundle: nil)
        self.view.backgroundColor = UIColor.white
        
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("Never will happen")
    }
    
    override open func loadView() {
        super.loadView()        
        self.safeArea = view.layoutMarginsGuide
    }
    
    override open func viewDidLoad() {
        super.viewDidLoad()
        self.view.backgroundColor = UIColor.white
    }
    
    open func setupViews() {
        self.tableView.backgroundColor = UIColor.blue
        self.tableView.separatorStyle = UITableViewCell.SeparatorStyle.none
        self.view.addSubview(self.tableView)
        
        self.tableView.translatesAutoresizingMaskIntoConstraints = false
        self.tableView.topAnchor.constraint(equalTo: safeArea.topAnchor).isActive = true
        self.tableView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        self.tableView.bottomAnchor.constraint(equalTo: safeArea.bottomAnchor).isActive = true
        self.tableView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
    }
    
    public func resetResultIndicator() {
        
        sections?.forEach({ (sectionRow) in
            sectionRow.items.forEach({ (row) in
                row.apiResult = .Ready
            })
        })
        
        self.tableView.reloadData()
    }
}
