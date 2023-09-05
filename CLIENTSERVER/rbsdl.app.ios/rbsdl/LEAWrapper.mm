//
//  LEAWrapper.m
//  rbsdl
//
//  Created by 이상섭 on 2021/07/27.
//

#import <Foundation/Foundation.h>

#import "LEAWrapper.h"
#import "LEA.hpp"
#import "LEA_core.h"
#import "rot32.h"

@implementation LEAWrapper

- (NSString *) sayHello {
    LEA lea;
    std::string helloWorldMessage = lea.sayHello();
    return [NSString
            stringWithCString:helloWorldMessage.c_str()
            encoding:NSUTF8StringEncoding];
}

- (NSString *) leadec : (const char) char1 : (const char) char2{
    LEA lea;
    std::string leadecMessage = lea.leadec(char1, char2);
    return [NSString
            stringWithCString:leadecMessage.c_str()
            encoding:NSUTF8StringEncoding];
}
@end
