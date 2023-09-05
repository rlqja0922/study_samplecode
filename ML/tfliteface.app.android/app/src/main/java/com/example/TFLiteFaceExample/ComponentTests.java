package com.example.TFLiteFaceExample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.TFLiteFaceExample.TFLite.FaceDetectorRetina;
import com.example.TFLiteFaceExample.TFLite.FaceRecognizer;
import com.example.TFLiteFaceExample.TFLite.FaceRecognizerMobileFaceNet;
import com.example.TFLiteFaceExample.TFLite.FaceDetector;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Vector;

public class ComponentTests extends AppCompatActivity{
    //load OpenCV
    static {
        try {
            System.loadLibrary("opencv_java3");
            Log.d("johnyi-jni", "Successfully loaded OpenCV");
        } catch (UnsatisfiedLinkError e) {
            Log.d("johnyi-jni", "OpenCV not found");
        }
    }

    private FaceDetector facedetector;
    private FaceRecognizer facerecognizer;

    private static final int REQUEST_CODE = 1;

    private Activity a;

    //for detection
    Bitmap bmpOrig;
    ImageView imageView_original;
    Mat matOrig, matDisplay;
    int H, W;



    int img_idx = 0;
    int probe_idx1 = 0;
    int probe_idx2 = 0;

    //for recognition
    ImageView imageView_probe1, imageView_probe2;
    Bitmap bmpProbe1, bmpProbe2;
    Mat matProbe1, matProbe2;
    byte[] probe1_bytes, probe2_bytes;
    float[] probe1_feature, probe2_feature;
    int H_probe = 112;
    int W_probe = 112;
    int feature_dim = 512;

    int numFrames = 3;

    String dir_name = "scenes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_test);
        requestPermissions();

        a = this;
        //Face detection related block
        imageView_original = findViewById(R.id.original);
        img_idx = 0;
        bmpOrig = utils.getBitmapFromAssets(this, String.format("%s/%04d.png", dir_name, img_idx));



        H = bmpOrig.getHeight();
        W = bmpOrig.getWidth();
        matOrig = new Mat(H, W, CvType.CV_8UC3);

        Utils.bitmapToMat(bmpOrig , matOrig); //비트맵 클래스로 변환
        Imgproc.cvtColor(matOrig, matOrig, Imgproc.COLOR_RGBA2RGB);

        matDisplay = matOrig.clone();

        imageView_original.setImageBitmap(bmpOrig);

        FaceDetectionResult = (TextView)findViewById(R.id.txt_face_detection_result);

        FaceRecognitionResult = (TextView)findViewById(R.id.txt_face_recognition_result);
        imageView_probe1 = findViewById(R.id.probe1);
        imageView_probe2 = findViewById(R.id.probe2);

        // 같은사람 왼쪽사진 파일 초기화
        bmpProbe1 = utils.getBitmapFromAssets(this, "probes/same1-0.png"); // asset폴더에있는 파일을 불러옴
        matProbe1 = new Mat();
        Utils.bitmapToMat(bmpProbe1 , matProbe1);
        Imgproc.cvtColor(matProbe1, matProbe1, Imgproc.COLOR_RGBA2RGB);
        probe1_bytes = new byte[H_probe * W_probe * matProbe1.channels()]; // 가로 세로 채널 값
        matProbe1.clone().get(0,0, probe1_bytes);

        // 같은사람 오른쪽 사진 파일 초기화
        bmpProbe2 = utils.getBitmapFromAssets(this, "probes/same2-0.png");
        matProbe2 = new Mat();
        Utils.bitmapToMat(bmpProbe2 , matProbe2);
        Imgproc.cvtColor(matProbe2, matProbe2, Imgproc.COLOR_RGBA2RGB);
        probe2_bytes = new byte[H_probe * W_probe * matProbe2.channels()];
        matProbe2.clone().get(0,0, probe2_bytes); // 새로운 객체를 만들어서 복사

        imageView_probe1.setImageBitmap(bmpProbe1);
        imageView_probe2.setImageBitmap(bmpProbe2);
        imageView_probe1.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView_probe2.setScaleType(ImageView.ScaleType.FIT_XY);

        probe1_feature = new float[feature_dim]; //512
        probe2_feature = new float[feature_dim]; //512

        try {
            facerecognizer = new FaceRecognizerMobileFaceNet(a, H_probe, W_probe, 3, feature_dim);
            facerecognizer.useGpu();
        }catch(Exception e){
            Log.d("johnyi-TFLite", "Exception: "+e);
        }
    }

    public void onTestFaceRecognizer(View view){
        // 인식버튼 클릭시 얼굴 특징 추출
        try {
            facerecognizer.extract_feature(probe1_bytes, probe1_feature, times); // input이미지, 512, 시간을저장할객체
            facerecognizer.extract_feature(probe2_bytes, probe2_feature, times); //

            // 임계값 추출 메서드
            final float distance = utils.l2_distance(probe1_feature, probe2_feature); // 두이미지를 비교

            runOnUiThread(new Runnable() {
                public void run() { // 두이미지의 거리의 비교값과 비교한 메서드의 실행시간을 출력
                    FaceRecognitionResult.setText(String.format("L2 distance: %.4f inference time: %d ms", distance, (int) times[1]));
                }
            });
        }catch(Exception e){
            Log.d("johnyi-TFLite", "Exception: "+e);
        }
    }


    public void onSwitchProbe(View view){
        switch (view.getId()) {
            case R.id.bt_switchProbe1: { // 같은사람의 사진
                probe_idx1 = (probe_idx1+1)%20; //저장할 사진의 인덱스
                bmpProbe1 = utils.getBitmapFromAssets(this, String.format("probes/same1-%d.png", probe_idx1)); //지정된 파일이름으로 저장된 같은사람의 왼쪽사진 파일
                imageView_probe1.setImageBitmap(bmpProbe1); // 비트맵 객체를 사용해서 imaveView에 보여줌
                Utils.bitmapToMat(bmpProbe1 , matProbe1); // Utils클래스를 사용 Bitmap에서 mat객체로 변환
                Imgproc.cvtColor(matProbe1, matProbe1, Imgproc.COLOR_RGBA2RGB); //
                probe1_bytes = new byte[H_probe * W_probe * matProbe1.channels()]; //H_probe, W_probe : 112로 초기화가 되어있음,  행렬의 채널값을 반환
                matProbe1.clone().get(0,0, probe1_bytes); // 112,112, 채널의 반환값 만큼 깊은복사

                bmpProbe2 = utils.getBitmapFromAssets(this, String.format("probes/same2-%d.png", probe_idx1)); //지정된 파일이름으로 저장된 같은사람의 오른쪽사진 파일
                imageView_probe2.setImageBitmap(bmpProbe2); // 비트맵 객체를 사용해서 imaveView에 보여줌
                Utils.bitmapToMat(bmpProbe2 , matProbe2);  // Utils클래스를 사용 Bitmap에서 mat객체로 변환
                Imgproc.cvtColor(matProbe2, matProbe2, Imgproc.COLOR_RGBA2RGB);
                probe2_bytes = new byte[H_probe * W_probe * matProbe2.channels()]; //H_probe, W_probe : 112로 초기화가 되어있음,  행렬의 채널값을 반환
                matProbe2.clone().get(0,0, probe2_bytes); // 112,112, 채널의 반환값 만큼 깊은복사
                break;
            }
            case R.id.bt_switchProbe2: { // 다른사람의 사진
                probe_idx2 = (probe_idx2+1)%19; //저장할 사진의 인덱스
                bmpProbe1 = utils.getBitmapFromAssets(this, String.format("probes/diff1-%d.png", probe_idx2)); //지정된 파일이름으로 저장된 같은사람의 왼쪽사진 파일
                imageView_probe1.setImageBitmap(bmpProbe1); // 비트맵 객체를 사용해서 imaveView에 보여줌
                Utils.bitmapToMat(bmpProbe1 , matProbe1); // Utils클래스를 사용 Bitmap에서 mat객체로 변환
                Imgproc.cvtColor(matProbe1, matProbe1, Imgproc.COLOR_RGBA2RGB); //
                probe1_bytes = new byte[H_probe * W_probe * matProbe1.channels()]; //H_probe, W_probe : 112로 초기화가 되어있음,  행렬의 채널값을 반환
                matProbe1.clone().get(0,0, probe1_bytes); // 112,112, 채널의 반환값 만큼 깊은복사

                bmpProbe2 = utils.getBitmapFromAssets(this, String.format("probes/diff2-%d.png", probe_idx2)); //지정된 파일이름으로 저장된 같은사람의 오른쪽사진 파일
                imageView_probe2.setImageBitmap(bmpProbe2); // 비트맵 객체를 사용해서 imaveView에 보여줌
                Utils.bitmapToMat(bmpProbe2 , matProbe2);  // Utils클래스를 사용 Bitmap에서 mat객체로 변환
                Imgproc.cvtColor(matProbe2, matProbe2, Imgproc.COLOR_RGBA2RGB);
                probe2_bytes = new byte[H_probe * W_probe * matProbe2.channels()]; //H_probe, W_probe : 112로 초기화가 되어있음,  행렬의 채널값을 반환
                matProbe2.clone().get(0,0, probe2_bytes); // 112,112, 채널의 반환값 만큼 깊은복사
                break;
            }
        }
    }

    public void onResetDetectionImage(View view){
        imageView_original.setImageBitmap(bmpOrig);
        runOnUiThread(new Runnable() {
            public void run() {
                FaceDetectionResult.setText("Inference time: ______ ms");
            }
        });
    }

    public void onSwitchDetectionImage(View view){
        img_idx = (img_idx+1)%numFrames;
        bmpOrig = utils.getBitmapFromAssets(this, String.format("%s/%04d.png", dir_name, img_idx));
        H = bmpOrig.getHeight();
        W = bmpOrig.getWidth();
        matOrig = new Mat(H, W, CvType.CV_8UC3);
        Utils.bitmapToMat(bmpOrig , matOrig);
        Imgproc.cvtColor(matOrig, matOrig, Imgproc.COLOR_RGBA2RGB);
        Log.d("johnyi","channels: "+matOrig.channels());

        imageView_original.setImageBitmap(bmpOrig);

        matDisplay = matOrig.clone();
        runOnUiThread(new Runnable() {
            public void run() {
                FaceDetectionResult.setText("Inference time: ______ ms");
            }
        });
    }

    TextView FaceDetectionResult, FaceRecognitionResult;

    Thread testThread;
    long[] times = new long[3];

    long startTime, endTime;
    public void onTestFaceDetectorRetina(View view){
        testThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(facedetector != null){
                    facedetector.close();
                    facedetector = null;
                }
                try {
                    // 3차원 배열로 이미지를 메모리에 저장하기위해
                    // 이미지형식에는 row 높이값, colum 넓이값, 채널에대한 값 RGB
                    // 채널 : 컬러 이미지를 설정에는 빨강, 녹색, 파랑 요소를 구성하는 픽셀값이 3개의 채널 (2가지방식은 흑백)
                    // 딥러닝 신경망에서는 이미지데이터가 3차원배열로 제공되어야 함.
                    // input image
                    int h = 1080; // row height 값.
                    int w = 1920; // colum width 값.
                    int c = 3;   // 컬러이미지에대한 채널값.
                    byte[] img_bytes = new byte[h*w*c]; // 이미지값들을 곱셈하여 바이트 배열로 초기화.

                    Mat matResized = new Mat(); // 리사이징할 이미지를 저장하기위해 mat 객체 생성.

                    // 이미지 사이즈 변환.
                    // 1. inputImage : mat객체로 초기화된 원본이미지.
                    // 2. outputImage : 사이즈 변경될 이미지.
                    // 3. outputImageSize : 출력할 이미지 사이즈. ( 위에서 지정된 width, height 값)
                    // 4. fx : 수평축  값.
                    // 5. fy : 수직축  값.
                    // 6. interpolator : 두점을 연결하는 방법. 궤적을 형성, 4x4 픽셀을 사용할때 ( 보간법 )
                    Imgproc.resize(matOrig, matResized, new Size(w,h),0,0, Imgproc.INTER_CUBIC);
                    matResized.clone().get(0,0, img_bytes); // 바이트로저장한 이미지의 크기만큼 깊은복사 (새로운객체생성해서 복사)

                    // 1. Activity
                    // 2,3,4 이미지계산을 위한 row, colum, channel 값
                    // 5.  scale값
                    // 6.  model_name 값
                    facedetector = new FaceDetectorRetina(a, h,w,c, 1f,"MobileNet"); // FaceDetectRectina Detect, 알고리즘 클래스

                    // 시간을 저장할 변수 초기화
                    times[0] = 0;
                    times[1] = 0;
                    times[2] = 0;

                    Log.d("johnyi","start detection!!!");

                    // 코드의 진행 속도를 측정하기위한 안드로이드의 함수
                    startTime = SystemClock.uptimeMillis(); // 시작

                    //  detect함수의 매개변수로 inputData h,w,c와 시간을 측정할 times
                    Vector<boundingBox> bboxes = facedetector.detect(img_bytes, times); //
                    endTime = SystemClock.uptimeMillis(); // 종료
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //
                            FaceDetectionResult.setText(String.format("Inference time: %d ms", (int)times[1]));
                        }
                    });


                    // 랜드마크와 사각형 이미지를 그리는 함수
                    // 리사이징된 이미지와 bboxes detect 함수를 사용하여 검출
                    utils.overlay_bboxes(matResized, bboxes);
                    Imgproc.resize(matResized, matResized, new Size(W,H),0,0, Imgproc.INTER_CUBIC); // 사이즈변경 확대 INTER_CUBIC

                    // 비트맵 객체 생성 리사이징된  ARGB_8888은 투명처리 가능 설정
                    Bitmap bmpOut = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(matResized, bmpOut); // Utils클래스로 Bitmap으로 변환
                    imageView_original.setImageBitmap(bmpOut); // asset 폴더에 저장되어있는 png 파일을 로드
                }catch (Exception e){
                    Log.d("johnyi-TFLite","initialization Exception: "+e);
                }
            }
        });
        testThread.start(); // 쓰레드 실행
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void requestPermissions() {
        String[] PERMISSIONS = {
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, REQUEST_CODE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }
}