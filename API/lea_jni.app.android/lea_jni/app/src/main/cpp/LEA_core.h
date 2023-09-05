//
// Created by reborn on 2021-07-22.
//

#ifndef HELLO_JNI_LEA_CORE_H
#define HELLO_JNI_LEA_CORE_H

typedef unsigned long DWORD;
typedef unsigned char BYTE;

#define ROR(W,i) (((W)>>(i)) | ((W)<<(32-(i))))
#define ROL(W,i) (((W)<<(i)) | ((W)>>(32-(i))))

#define DWORD_in(x)            (*(DWORD*)(x))
#define DWORD_out(x, v)        {*((DWORD*)(x)) = (v);}

void LEA_Key(const uint8_t* userkey, uint8_t* roundkey);
void LEA_Enc(const uint8_t* roundkey, uint8_t* data);
void LEA_Dec(const uint8_t* roundkey, uint8_t* data);


#endif //HELLO_JNI_LEA_CORE_H
