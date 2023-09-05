package com.example.TFLiteFaceExample.TFLite;

/* Copyright 2017 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.SystemClock;
import android.util.Log;

import com.example.TFLiteFaceExample.boundingBox;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Vector;

import org.tensorflow.lite.Delegate;
import org.tensorflow.lite.Interpreter;

public abstract class FaceDetector {
    /** Tag for the {@link Log}. */
    private static final String TAG = "johnyi-TFLite";

    /** Options for configuring the Interpreter. */
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    /** The loaded TensorFlow Lite model. */
    private MappedByteBuffer tfliteModel;

    /** An instance of the driver class to run model inference with Tensorflow Lite. */
    protected Interpreter tflite;

    protected ByteBuffer imgData = null;

    int h,w,c;
    int p;
    boolean use_padding = false;
    int out_h, out_w;
    Vector<boundingBox> bboxes, bboxes_refined;
    float iou_thresh = 0.4f; // for NMS

    /** holds a gpu delegate */
    Delegate gpuDelegate = null;

    String model_name;

    boundingBox tmp, tmp2; // this is used for NMS
    long startTime, endTime; // this is used for timing measurements


//    검출하는 메소드 onTestFaceDetectorRetina 에서 파라미터값들을 받음
    FaceDetector(Activity activity, int h, int w, int c, String model_name) throws IOException {
        this.h = h;  // 1090
        this.w = w;  // 1920
        this.c = c;  // 채널값 3
        this.model_name = model_name; // "MobileNet"

        startTime = SystemClock.uptimeMillis(); // 속도체크 시작
        tfliteModel = loadModelFile(activity); // 텐서플로어의 모델을 메모리에 로드
        tflite = new Interpreter(tfliteModel, tfliteOptions); // 텐서플로어의 모델을 사용하기위해 Interpreter 초기화
        endTime = SystemClock.uptimeMillis();  // 속도체크 종료
        Log.d("johnyi-TFLite", "Created a Tensorflow Lite Face detector. loading time:, "+ Long.toString(endTime - startTime));


        bboxes = new Vector(); // 박스를 그리는 알고리즘 클래스
        bboxes_refined = new Vector(); // detect 검출메소드에서 사용하기위해 boundingBox

        imgData = ByteBuffer.allocateDirect(1 * h * w * c * 4); // heap메모리가아닌 운영체제 시스템에 메모리할당, 입출력 시스템의 속도적 이점이있음
        imgData.order(ByteOrder.nativeOrder()); // 하드웨어에서 little Endian/ Big Endian 중 사용되는 것을 가져와서 버퍼를 빨리 다루는 메서드
    }


    public abstract void calc_bboxes();
    public abstract Vector<boundingBox> detect(byte[] img_in, long[] times);

    public int getMaxScoreBBoxIdx(Vector<boundingBox> bboxes){
        int idx = 0;
        float maxScore = 0.0f;
        float score;
        for(int i = 0; i < bboxes.size(); i++){
            score = bboxes.elementAt(i).score;
            if(score >= maxScore){
                maxScore = score;
                idx = i;

            }
        }
        return idx;
    }

    public void nonMaximumSuppression(){
        bboxes_refined.clear();
        int idx;
        int remaining, next_comp_idx;
        while(bboxes.size() > 0){
            idx = getMaxScoreBBoxIdx(bboxes);
            tmp = bboxes.elementAt(idx);
            bboxes.remove(idx);
            remaining = bboxes.size();
            next_comp_idx = 0;
            for(int i = 0; i <remaining; i++){
                tmp2 = bboxes.elementAt(next_comp_idx);
                if(tmp.calcIOU(tmp2) > iou_thresh){
                    bboxes.remove(next_comp_idx);
                }else{
                    next_comp_idx+=1;
                }
            }
            bboxes_refined.add(bboxes_refined.size(), tmp);
        }
    }

    private void recreateInterpreter() {
        if (tflite != null) {
            tflite.close();
            // TODO(b/120679982)
            // gpuDelegate.close();
            tflite = new Interpreter(tfliteModel, tfliteOptions);
        }
    }

    public void useGpu() {
        Log.d("johnyi-TFLite-Facedetector","Using GPU");
        if (gpuDelegate == null && GpuDelegateHelper.isGpuDelegateAvailable()) {
            gpuDelegate = GpuDelegateHelper.createGpuDelegate();
            tfliteOptions.addDelegate(gpuDelegate);
            recreateInterpreter();
        }
    }

    public void useCPU() {
        tfliteOptions.setUseNNAPI(false);
        recreateInterpreter();
    }

    public void useNNAPI() {
        tfliteOptions.setUseNNAPI(true);
        recreateInterpreter();
    }

    public void setNumThreads(int numThreads) {
        tfliteOptions.setNumThreads(numThreads);
        recreateInterpreter();
    }

    /** Closes tflite to release resources. */
    public void close() {
        tflite.close();
        tflite = null;
        tfliteModel = null;
    }


    /** Memory-map the model file in Assets. */
    // 모델을 읽어오는 함수로, 텐서플로 라이트 홈페이지에 있다.
    // MappedByteBuffer 바이트 버퍼를 Interpreter 객체에 전달하면 모델 해석을 할 수 있다.
    // Asset폴더에있는 모델을 로드 한다.
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(getModelPath());
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


    /**
     * Get the name of the model file stored in Assets.
     *
     * @return
     */
    protected abstract String getModelPath();
}
