//
//  LEA.cpp
//  rbsdl
//
//  Created by 이상섭 on 2021/07/27.
//

#include "LEA.hpp"
#include "LEA_core.h"
#include "LEA_core.c"

std::string LEA::sayHello() {
    return "Hello from CPP world!";
}


BYTE randomchar(){
    
    //srand((unsigned int)time(NULL));
    BYTE randomvalue;

    while (1){
        //randomvalue = rand() % ((122 - 48) + 1) + 48; // 48(0) ~ 122(z)
        //randomvalue = random(48, 123); // 48(0) ~ 122(z)
        randomvalue = random() % ((122 - 48) + 1) + 48; // 48(0) ~ 122(z)
        
        
        if (randomvalue >= 97){ // 97: a
            break;
            
        } else if (randomvalue >= 65 && randomvalue <= 90) { // 65(A)~90(Z)
            break;
            
        } else if (randomvalue <= 57) { //57(9)
            break;
        }
        
    }
    
    return randomvalue;
}

std::string LEA::leadec(const char char1, const char char2) {
    
    BYTE byte1 = randomchar();
    BYTE byte2 = randomchar();
    
    BYTE pbUserKey[16] = { 0x0f, 0x1e, 0x2d, 0x3c, 0x4b, 0x5a, 0x69, 0x78, 0x87, 0x96, 0xa5, 0xb4, 0xc3, 0xd2, static_cast<BYTE>(char1), static_cast<BYTE>(char2) };
    BYTE pdwRoundKey[384] = { 0x0, };
    BYTE pbData[16] = { 0x4F, 0x50, 0x45, 0x4E, 0x44, 0x4C, byte1, byte2, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };

    
    LEA_Key(pbUserKey, pdwRoundKey);
    LEA_Enc(pdwRoundKey,pbData);
    
    
    std::string str = "";
    int i ;
    char temp[200]="DEC ";
    
    for(i=0;i<16;i++){
            char ctemp[10];
            sprintf(ctemp, "0x%X", pbData[i]);
            strcat(temp,ctemp);
            strcat(temp," ");
    }
    str = temp;
    
    return str;
}
