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

class PickerCell : UITableViewCell {
   
    let cellWidth : CGFloat = 44
    let cellHeight : CGFloat = 44
    let gap : CGFloat = 10
    var elementImageView: UIImageView = {
        let imageView = UIImageView(frame: CGRect.zero)
        imageView.contentMode = .scaleAspectFit
        return imageView
    }()
    
    var elementTitleLable : UILabel = {
        let label = UILabel(frame: CGRect.zero)
        return label
    }()
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        self.accessoryType = .none
        self.contentView.backgroundColor = UIColor.white
        self.contentView.addSubview(self.elementImageView)
        self.contentView.addSubview(self.elementTitleLable)
        
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("Never will happen")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        self.elementImageView.frame = CGRect(x:0, y:0, width:self.cellHeight, height:self.cellHeight)
        self.elementTitleLable.frame = CGRect(x:self.elementImageView.frame.size.width + self.gap,
                                              y:0,
                                              width:(self.frame.width - self.elementImageView.frame.size.width - self.gap*2),
                                              height:self.cellHeight)
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        
        self.elementImageView.image = nil
        self.elementTitleLable.text = nil
        self.accessoryType = .none
    }
}

public class SDKSamplePickerViewController: SDKSampleBaseViewController, UITableViewDelegate, UITableViewDataSource {
    
    var elementsPickerCompletionHandler : (([Any]) -> Void)?
    
    
    var viewTitle : String?
    var elements : [(imageUrl:URL?, name:String?, id:Any)]?
    var selectedElements : [Bool]
    
    public convenience init(title:String,
                            elements: [(URL?, String?, Any)],
                            elementsPickerCompletionHandler: (([Any]) -> Void)? = nil) {
        self.init()
        self.viewTitle = title
        self.elements = elements
        self.elements?.forEach({ _ in
            self.selectedElements.append(false)
        })
        self.elementsPickerCompletionHandler = elementsPickerCompletionHandler
    }
    
    public override init() {
        self.selectedElements = [Bool]()
        
        super.init()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("Never will happen")
    }
    
    override public func loadView() {
        super.loadView()
        
        setupViews()
    }
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        self.tableView.delegate = self
        self.tableView.dataSource = self
        
        self.title = "\(self.viewTitle ?? "")(0)"
    }
    
    override public func setupViews() {
        super.setupViews()
        
        self.tableView.register(PickerCell.self, forCellReuseIdentifier: "PickerCell")
        
        self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Send", style: .plain, target: self, action: #selector(SDKSamplePickerViewController.send(sender:)))
        self.navigationItem.rightBarButtonItem?.isEnabled = false
    }
    
    @objc func send(sender:Any) {
        var selectedIds = [Any]()
            
        for (index, checked) in self.selectedElements.enumerated() {
            if (checked) {
                if let (_, _, id) = self.elements?[index] {
                    selectedIds.append(id)
                }
            }
        }
        
        if let completionHandler = elementsPickerCompletionHandler {
            completionHandler(selectedIds)
            self.navigationController?.popViewController(animated: true)
        }
    }
    
    
    public func numberOfSections(in tableView: UITableView) -> Int {
        return 1
    }

    public func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if let elements = self.elements {
            return elements.count
        }
        else {
            return 0
        }
    }
    
    public func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "PickerCell", for: indexPath) as! PickerCell
        
        let (imageUrl, name, _) = self.elements![indexPath.row]
        
        if let imageUrl = imageUrl {
            DispatchQueue.global().async {
                if let data = try? Data( contentsOf:imageUrl)
                {
                    DispatchQueue.main.async {
                        cell.elementImageView.image = UIImage(data:data)
                    }
                }
            }
        }
        else {
            cell.elementImageView.image = UIImage(named: "Default")
        }
        cell.elementTitleLable.adjustsFontSizeToFitWidth = true
        cell.elementTitleLable.text = name
        cell.elementTitleLable.backgroundColor = UIColor.init(white: 1.0, alpha: 0)
        cell.elementTitleLable.textColor = UIColor.black
        return cell
    }
    
    public func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if let pickerCell = self.tableView.cellForRow(at: indexPath) as? PickerCell {
            self.selectedElements[indexPath.row] = !self.selectedElements[indexPath.row]
            if (self.selectedElements[indexPath.row]) {
                pickerCell.accessoryType = .checkmark
            }
            else {
                pickerCell.accessoryType = .none
            }
        }
        
        var selectedCount = 0
        self.selectedElements.forEach({ (checked) in
            if (checked) {
                selectedCount += 1
            }
        })

        if selectedCount > 0 {
            self.navigationItem.rightBarButtonItem?.isEnabled = true
        }
        else {
            self.navigationItem.rightBarButtonItem?.isEnabled = false
        }
        self.title = "\(self.viewTitle ?? "")(\(selectedCount))"
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1 * 1) {
           tableView.deselectRow(at: indexPath, animated: false)
        }
    }
}
