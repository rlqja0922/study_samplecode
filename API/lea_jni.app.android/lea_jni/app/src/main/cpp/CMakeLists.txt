cmake_minimum_required(VERSION 3.4.1)

add_library(hello-jni SHARED
            hello-jni.c)

# Include libraries needed for hello-jni lib
target_link_libraries(hello-jni
                      android
                      log
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )

# Specifies a path to native header files.
include_directories(${CMAKE_CURRENT_SOURCE_DIR}
        ${ANDROID_NDK}/sources/android/native_app_glue/android_native_app_glue.c )

