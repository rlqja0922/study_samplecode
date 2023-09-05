/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
#include <stdio.h>
//#include <string.h>
#include <stdint.h>
#include "LEA_core.h"
#include "LEA_core.c"



#include <android/log.h>
#include <string.h>
#include <jni.h>


/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   hello-jni/app/src/main/java/com/example/hellojni/HelloJni.java
 */
JNIEXPORT jstring JNICALL
Java_com_example_hellojni_HelloJni_stringFromJNI( JNIEnv* env, jobject thiz )
{
#if defined(__arm__)
    #if defined(__ARM_ARCH_7A__)
    #if defined(__ARM_NEON__)
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a/NEON (hard-float)"
      #else
        #define ABI "armeabi-v7a/NEON"
      #endif
    #else
      #if defined(__ARM_PCS_VFP)
        #define ABI "armeabi-v7a (hard-float)"
      #else
        #define ABI "armeabi-v7a"
      #endif
    #endif
  #else
   #define ABI "armeabi"
  #endif
#elif defined(__i386__)
#define ABI "x86"
#elif defined(__x86_64__)
#define ABI "x86_64"
#elif defined(__mips64)  /* mips64el-* toolchain defines __mips__ too */
#define ABI "mips64"
#elif defined(__mips__)
#define ABI "mips"
#elif defined(__aarch64__)
#define ABI "arm64-v8a"
#else
#define ABI "unknown"
#endif


    char temp1 = 0x41;
    BYTE temp2 = temp1;

    BYTE pbUserKey[16] = { 0x0f, 0x1e, 0x2d, 0x3c, 0x4b, 0x5a, 0x69, 0x78, 0x87, 0x96, 0xa5, 0xb4, 0xc3, 0xd2, 0x41, 0x53 };
    BYTE pdwRoundKey[384] = { 0x0, };
    BYTE pbData[16] = { 0x4F, 0x50, 0x45, 0x4E, 0x44, 0x4C, temp1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
    BYTE pbData1[16] = { 0x4F, 0x50, 0x45, 0x4E, 0x44, 0x4C, temp1, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0 };
    int i;

    LEA_Key(pbUserKey, pdwRoundKey);
    LEA_Enc(pdwRoundKey,pbData);
    LEA_Key(pbUserKey, pdwRoundKey);
    LEA_Key(pbUserKey, pdwRoundKey);
    LEA_Enc(pdwRoundKey,pbData1);
    char temp[200]="암호화:";
    for(i=0;i<16;i++){
        char ctemp[10];
        sprintf(ctemp, "0x%X", pbData1[i]);
        strcat(temp,ctemp);
        strcat(temp," ");

    }




    /*
    for(i=0;i<16;i++)
        Serial.print(pbData[i],HEX);
    Serial.println("");
    delay(1000);

    LEA_Key(pbUserKey, pdwRoundKey);
    LEA_Dec(pdwRoundKey,pbData);
    */


    /*
    for(i=0;i<16;i++)
        Serial.print(pbData[i],HEX);
    Serial.println("");
    delay(1000);
    */
    //return (*env)->NewStringUTF(env, "Hello from JNI !  Compiled with ABI1 " ABI "." );
    return (*env)->NewStringUTF(env,temp);
    //return (*env)->NewStringUTF(env, pbData);
    //return pbData;
}
