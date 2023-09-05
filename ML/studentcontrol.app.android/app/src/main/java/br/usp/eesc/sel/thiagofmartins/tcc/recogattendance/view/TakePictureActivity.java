package br.usp.eesc.sel.thiagofmartins.tcc.recogattendance.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.contrib.Contrib;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.photo.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import br.usp.eesc.sel.thiagofmartins.tcc.recogattendance.database.helper.DatabaseHelper;
import br.usp.eesc.sel.thiagofmartins.tcc.recogattendance.recognition.Olhos;
import br.usp.eesc.sel.thiagofmartins.tcc.recogattendance.recognition.ProcessaImagem;
import br.usp.eesc.sel.thiagofmartins.tcc.recogattendance.recognition.TutorialCamera;

//import java.io.FileNotFoundException;
//import org.opencv.contrib.FaceRecognizer;


/**
 * Created by Thiago on 1/31/2016.
 */



public class TakePictureActivity extends Activity implements CvCameraViewListener2{ ///TakePicture extends Activity implements CameraBridgeViewBase.CvCameraViewListener2



    /*
        private static final String    TAG = "OCVSample::Activity";
        private File         mCascadeFile;
        private CascadeClassifier      mJavaDetector;
        private TutorialCamera mOpenCvCameraView;

        public static final int        JAVA_DETECTOR       = 0;
        public static final int        NATIVE_DETECTOR     = 1;

        private int                    mDetectorType       = JAVA_DETECTOR;
        private String[]               mDetectorName;

        private static final int frontCam =1;
        private static final int backCam =2;
        private int mChooseCamera = backCam;
        ImageButton imCamera;

        private Mat                    mRgba;
        private Mat                    mGray;
    */
    private Camera mCamera;
    private TutorialCamera mOpenCvCameraView;
    private static final String    TAG                 = "OCVSample::Activity";
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;

    public static final int TRAINING= 0;
    public static final int SEARCHING= 1;
    public static final int IDLE= 2;

    private static final int frontCam =1;
    private static final int backCam =2;


    private int faceState=IDLE;
//    private int countTrain=0;

    //    private MenuItem               mItemFace50;
//    private MenuItem               mItemFace40;
//    private MenuItem               mItemFace30;
//    private MenuItem               mItemFace20;
//    private MenuItem               mItemType;
//
    private MenuItem               nBackCam;
    private MenuItem               mFrontCam;
    private MenuItem               mEigen;


    private Mat                    mRgba; //OpenCV에서 RGB색상에서 Alpha값을 추가한 투명도를 나타냄
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;
    Olhos olhos = new Olhos();

    private File                   eyeCascade1File;
    private File                   eyeCascade2File;
    public CascadeClassifier      eyeCascade1;
    public CascadeClassifier      eyeCascade2;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    private float                  mRelativeFaceSize   = 0.2f;
    private int                    mAbsoluteFaceSize   = 0;
    private int mLikely=999;

    String mPath="";


    private int mChooseCamera = backCam;

    EditText text;
    TextView textresult;
    private  ImageView Iv;
    Bitmap mBitmap;
    static Handler mHandler;


    ToggleButton toggleButtonGrabar,toggleButtonTrain,buttonSearch;
    Button buttonCatalog;
    ImageView ivGreen,ivYellow,ivRed;
    ImageButton imCamera;
    ImageButton buttonClick;
    boolean takePhoto = false;

    TextView textState;
    com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer faceRecognizer;


    long MAXIMG=9;
    public int resultCode = 2;

    ArrayList<Mat> alimgs = new ArrayList<Mat>();
    Bitmap test;



    int[] labels = new int[(int)MAXIMG];
    int countImages=0;
    long[]addr = new long[]{0,1,2,3,4,5,6,7,8};
    ProcessaImagem meuProcesso = new ProcessaImagem();





    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    // Load native library after(!) OpenCV initialization
                    //   System.loadLibrary("detection_based_tracker");


                    try {
                        // 리소스 폴더에 있는 lipcascade 파일을 불러옴
                        InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);

                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
                        mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml"); //얼굴 인식을 위해 해당 파일을 사용
                        //mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);


                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                        is.close();
                        os.close();

                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());

                        //                 mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);

                        cascadeDir.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    try {
                        // 리소스 폴더에 raw폴더에 있는 왼쪽눈검출 cacade파일을 불러옴
                        InputStream isa = getResources().openRawResource(R.raw.haarcascade_mcs_lefteye);
                        File cascadeDira = getDir("cascade", Context.MODE_PRIVATE);
                        eyeCascade1File = new File(cascadeDira, "haarcascade_mcs_lefteye.xml");
                        FileOutputStream osa = new FileOutputStream(eyeCascade1File);

                        byte[] buffera = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = isa.read(buffera)) != -1) {
                            osa.write(buffera, 0, bytesRead);
                        }
                        isa.close();
                        osa.close();

                        eyeCascade1 = new CascadeClassifier(eyeCascade1File.getAbsolutePath());
                        if (eyeCascade1.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            eyeCascade1 = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " +eyeCascade1File.getAbsolutePath());

                        //mJavaDetector = new CascadeClassifier(faceCascadeFile.getAbsolutePath());

                        cascadeDira.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }


                    try {
                        // 리소스파일에서 오른쪾눈 검출 cascade파일 로드
                        InputStream isb = getResources().openRawResource(R.raw.haarcascade_mcs_righteye);
                        File cascadeDirb = getDir("cascade", Context.MODE_PRIVATE);
                        eyeCascade2File = new File(cascadeDirb, "haarcascade_mcs_righteye.xml");
                        FileOutputStream osb = new FileOutputStream(eyeCascade2File);

                        byte[] bufferb = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = isb.read(bufferb)) != -1) {
                            osb.write(bufferb, 0, bytesRead);
                        }
                        isb.close();
                        osb.close();

                        eyeCascade2 = new CascadeClassifier(eyeCascade2File.getAbsolutePath());
                        if (eyeCascade2.empty()) {
                            Log.e(TAG, "Failed to load cascade classifier");
                            eyeCascade2 = null;
                        } else
                            Log.i(TAG, "Loaded cascade classifier from " +eyeCascade2File.getAbsolutePath());

                        //mJavaDetector = new CascadeClassifier(faceCascadeFile.getAbsolutePath());

                        cascadeDirb.delete();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }


                    mOpenCvCameraView.enableView();

                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;


            }
        }
    };

    public TakePictureActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";

        Log.i(TAG, "Instantiated new " + this.getClass());
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mPath=Environment.getExternalStorageDirectory()+"/faceapp/faces/";
        setContentView(R.layout.activity_take_picture);



        //

        Iv=(ImageView)findViewById(R.id.takeView);

        /*mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.obj=="img") {
                    Canvas canvas = new Canvas();
                    canvas.setBitmap(test);
                    Iv.setImageBitmap(test);

                }
                else
                {

                }

            }
        };*/

        //
        buttonClick = (ImageButton) findViewById(R.id.imageButton);

        mOpenCvCameraView = (TutorialCamera) findViewById(R.id.tutorialCamera);

        mOpenCvCameraView.setCvCameraViewListener(this);

        int max = getIntent().getIntExtra("flag_qdt_foto",0);

        if (max==1) {
            MAXIMG = 1;
            resultCode = 1;
        }
        else if (max==2)
        {
            MAXIMG = 9;
            resultCode = 2;
        }

        else
        {
            resultCode=1;
        }


        buttonClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto=true;
            }

        });

        imCamera=(ImageButton)findViewById(R.id.changeCamera);

        imCamera.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (mChooseCamera == frontCam) {
                    mChooseCamera = backCam;
                    mOpenCvCameraView.setCamBack();
                } else {
                    mChooseCamera = frontCam;
                    mOpenCvCameraView.setCamFront();

                }
            }
        });




    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }

    }


    public void onDestroy() {
        super.onDestroy();

        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
//        이미지를 담기위한 matrix를 생성
        mRgba = inputFrame.rgba(); // 컬러데이터 리턴
        Size size = mRgba.size();

        mGray = inputFrame.gray(); // 회색이미지 리턴

        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }

        /************************************************Detect face - LBP cascade***********************************/




        MatOfRect faces = new MatOfRect(); // 검출하는 이미지를 담을 Rect배열 생성




//        detectMultiScale() : input이미지에서 크기가 다른 object를 검출(detect)하는 함수입니다.
//        1번째 인자는 image는 바로 검출하고자 하는 원본 이미지
//        2번째 인자는 objects에 검출된 이미지가 채워짐
//        3번쨰 인자는 scaleFactor는 이미지 피라미드에서 사용되는 scale factor를 의미합니다.
//        4번째 인자는 inNeighbors는 이미지 피라미드에 의한 여러 스케일의 크기에서 minNeighbors 횟수 이상 검출된object는 valid하게 검출할때 쓰임
//        5번째 인자는  flags값, old cascade 사용시에만 사용되는 파라미터( opencv1.x 버전에서 사용되던 cacasde 파일의 형식 )
//        6번째 인자는 minSize는 검출하려는 이미지의 최소 사이즈입니다. 이 크기보다 작은 object는 무시됩니다.
//        7번째 인자는 maxSize는 검출하려는 이미지의 최대 사이즈입니다. 이 크기보다 큰 object는 무시됩니다.
//        OpenCV의 함수
        mJavaDetector.detectMultiScale(mGray, faces, 1.1f, 2, Objdetect.CASCADE_FIND_BIGGEST_OBJECT,new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        Log.d("검출","DetectMultiScale");

//        검출되는 이미지에서 찾은 이미지가  여러개일수 있기 때문에 검출된 이미지를 배열로 저장
        Rect [] faceArray = faces.toArray();

//       카메라에서 얼굴을 인식하게될때
        if (faceArray.length>0) {
            Rect r = faceArray[0];  // 처음 검출된 이미지를 사각형 Rect 객체로 가르킴
            Mat face = mRgba.submat(r); // 검출된 이미지만큼 자른후 Mat 객체에 담는다
            if (face.width()<=0)
            {
                Point leftEye = new Point();
            }

//          clone() 함수는  깊은복사를 함   깊은복사 : 자기자신과 동일한 Mat객체를 새로 만들어서 반환
//          face의 저장된 이미지를 packet에 복사
            Mat packet = face.clone();

            Point leftEye = new Point();
            Point rightEye = new Point();



            // 1번째 인자는 화면에 보이는 원본 이미지
            // 2번쨰,3번째 인자는 opencv의 cascade 파일의 left,right.xml의 코드를 불러와 사용
            // 3,4,5,6 번쨰 인자는 Olhos의 클래스에 있는 OpenCV Point,Rect로 초기화한것을 사용
            olhos = detectBothEyes(face,eyeCascade1,eyeCascade2,olhos.leftEye, olhos.rightEye, olhos.searchedLeftEye, olhos.searchedRightEye);
            Log.d("검출","눈검출 시작");
            /***********Send intent back*****************/

            /*Intent intent = new Intent();
            long addr = face.getNativeObjAddr();
            intent.putExtra("obj",addr);
            setResult(2,intent);
            finish();*/

            /******************************************/

            /*************************************************************************************************************************/
            leftEye = olhos.leftEye; // 검출된 왼쪽눈의
            rightEye=olhos.rightEye;
            //leftEye.x = 1;
            //rightEye.x=1;

            // 두눈이 모두 감지가 되면
            if (leftEye.x >= 0 && rightEye.x >= 0)
            //if(true)
            {
                //Core.putText(mRgba, "Face detectada", new Point(20, 20), 1, 5, new Scalar(255, 255, 255), 1, 8, false);
                //for (int i = 0; i < faceArray.length; i++)
                // 1번째 파라미터는 그리고자하는 원본이미지 여기에 사각형이 그려짐
                // 2번째, 3번째 파라미터는 두개의점을 설정합니다.
                // 4번째 파라미터는 만들려는 사각형의 색상을 지정 new Scalar(0, 255, 0, 255); 위에서 색상값을 지정해놓음
                // 5번째 파라미터는 만들려는 사각형의 두께를 지정
                Core.rectangle(mRgba, faceArray[0].tl(), faceArray[0].br(), FACE_RECT_COLOR, 3);





                // 사진버튼을 클릭시
                if (takePhoto)
                {

                    /*Message msg = new Message();
                    String textTochange = "img";
                    msg.obj = textTochange;
                    mHandler.sendMessage(msg);
                    */
                    //
                    //Mat processada = meuProcesso.equalize(packet, leftEye, rightEye, 200, true);


                    // 히스토리그램 평활화
                    // 영상의 밝기값의 분포를 균일하게 만드는 작업
                    // 흑백이미지에서 히스토그램 평활화하는 함수
                    Mat processada = meuProcesso.gray(packet);
                    Imgproc.equalizeHist(processada,processada);





//                  이미지를 저장하기위해 bitmap객체를 생성하고 이미지를 저장 saveImage 함수를통해서 저장
                    if (countImages < MAXIMG &&(processada.width()>0))
                    {

                        //rightX[countImages]=rightEye.x;
                        // rightY[countImages]=rightEye.y;
                        // leftX[countImages]=leftEye.x;
                        //leftY[countImages]=leftEye.y;
                        //addr[countImages] = processada.getNativeObjAddr();//mudar de volta!!!
                        Bitmap bmp = ProcessaImagem.MattoBitmap(processada);     // 이미지를 저장하기위해 bitmap객체를 생성하고 이미지를 저장 saveImage 함수를통해서 저장
                        SaveImage(bmp,Integer.toString(countImages)+"A.jpg");

                        Bitmap final_Image = ProcessaImagem.MattoBitmap(packet);
                        SaveImage(final_Image,Integer.toString(countImages)+"B.jpg");//ATENÇÃOOOOO!!!!  이미지를 저장 saveImage 함수를통해서 저장
                        //SaveImage(bmp,Integer.toString(countImages)+"B.jpg");




                    }
                    else
                    {
                        Intent intent = new Intent();
                        //long addr = face.getNativeObjAddr();
                        //intent.putExtra("obj", addr);
                        //intent.putExtra("9or1", resultCode);
                        //intent.putExtra("leftX",leftX);
                        //intent.putExtra("rightX", rightX);
                        //intent.putExtra("leftY",leftY);
                        //intent.putExtra("rightY", rightY);
                        setResult(RESULT_OK, intent);
                        //setResult(2,intent);
                        finish();
                    }
                    countImages++;
                }
            }


        }


        //Imgproc.resize(mRgba, mRgba, new Size(), 2, 2, Imgproc.INTER_LINEAR);
        return mRgba;


    }


    private void SaveImage(Bitmap finalBitmap, String fname) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/EESC-Face/temp/"); // 해당경로로 파일경로 지정
        myDir.mkdirs();
        Random generator = new Random();
        //int n = 10000;
        //n = generator.nextInt(n);
        //String fname = n +".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Rect detectLargestObject( Mat img, CascadeClassifier cascade, Rect faceRect, int scaledWidth)
    {
        // 하나의 개체 만 검색합니다 (이미지에서 가장 큰 개체)
        // OpenCV의 Objdetect 클래스를 사용
        int flags = Objdetect.CASCADE_FIND_BIGGEST_OBJECT; // | CASCADE_DO_ROUGH_SEARCH;

        // 검출하려는 이미지의 최소 사이즈 지정
        Size minFeatureSize =  new Size(20, 20);

        //   이미지 피라미드에서 사용되는 scalefactor
        float searchScaleFactor = 1.3f;
        // How much the detections should be filtered out. This should depend on how bad false detections are to your system.
        // minNeighbors=2 means lots of good+bad detections, and minNeighbors=6 means only good detections are given but some are missed.

        // 얼굴사이의 최소간격을 의미, 너무낮은 값을 사용하면 중복돼서 검출될수도 있음. 기본값은 3
        int minNeighbors = 2;

        //개체 또는 얼굴 감지를 수행하여 1 개의 개체 만 찾습니다 (이미지에서 가장큰이미지).
        List<Rect> objectsa = new ArrayList<>();
        MatOfRect objects = new MatOfRect();


        // 1. img : 검출되는 원본이미지,
        // 2. cascade : OpenCV Cascade의 검출하는 xml 파일
        // 3. objects : 이미지가 검출되면 object에 반환
        // 4. scaledwidth : scaledwidth는 검색을좀더 해봐야함.
        // 5. flags : OpenCV의 Objdetect 기능을 사용 cascade 사용시에만 설정하는 값
        // 6. minFeatureSize : 검출하려는 이미지의 최소 사이즈를 지정
        // 7. searchScaleFactor : 보이는 시야에대한 값을지정.
        // 8. minNeighbors : 얼굴사이의 최소간격을 지정.
        detectObjectsCustom(img, cascade, objects, scaledWidth, flags, minFeatureSize, searchScaleFactor, minNeighbors);

        if (objects.toArray().length>0) {
            // 검출된 객체가 감지가되면
            faceRect = objects.toArray()[0];
        }
        else {
            // Return an invalid rect.
            faceRect = new Rect(-1,-1,-1,-1);
        }
        return faceRect;
    }

    public void detectObjectsCustom(final Mat img, CascadeClassifier cascade, MatOfRect objects, int scaledWidth, int flags, Size minFeatureSize, float searchScaleFactor, int minNeighbors)
    {
        //입력 이미지가 회색조가 아닌 경우 BGR 또는 BGRA 컬러 이미지를 회색조로 변환합니다.
        Mat gray = new Mat();
        if (img.channels() == 3) { // 채널값3 컬러이미지 채널
            Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);

        }
        else if (img.channels() == 4) {
            Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGRA2GRAY);
        }
        else {
            // 이미 회색이므로 입력 이미지에 직접 액세스합니다.
            gray = img;
        }

        // 빠른실행을 위해 이미지를 축소.
        Mat inputImg = new Mat();



        float scale = img.cols() / (float)scaledWidth;
        if (img.cols() > scaledWidth) {
            // 동일한 종횡비를 유지하면서 이미지 축소

            int scaledHeight = Math.round(img.rows() / scale);
            // resize : 이미지의크기를 조정
            Imgproc.resize(gray, inputImg, new Size(scaledWidth, scaledHeight));
        }
        else {
            // Access the input image directly, since it is already small.
            inputImg = gray;
        }


        Mat equalizedImg = new Mat();
        Imgproc.equalizeHist(inputImg, equalizedImg); // equalizeHist : 밝기와 대비를 표준화하여 어두운 이미지를 개선합니다.
        Bitmap a = ProcessaImagem.MattoBitmap(equalizedImg);
        Size maxFeatureSize = new Size (equalizedImg.width(),equalizedImg.height());


        cascade.detectMultiScale(equalizedImg, objects, searchScaleFactor, minNeighbors, flags, minFeatureSize, maxFeatureSize);
        // Detect objects in the small grayscale image.
        //cascade.detectMultiScale(equalizedImg, objects, searchScaleFactor, minNeighbors, flags, minFeatureSize);
        //cascade.detectMultiScale(equalizedImg, objects, searchScaleFactor, minNeighbors, flags, minFeatureSize, maxFeatureSize);

        // 감지 전에 이미지가 일시적으로 축소된 경우 결과를 확대합니다.
        if (img.cols() > scaledWidth) {
            for (int i = 0; i < objects.toArray().length; i++ ) {
                int x = objects.toArray()[i].x;
                objects.toArray()[i].x =
                        objects.toArray()[i].x = Math.round(objects.toArray()[i].x * scale);
                objects.toArray()[i].y = Math.round(objects.toArray()[i].y * scale);
                objects.toArray()[i].width = Math.round(objects.toArray()[i].width * scale);
                objects.toArray()[i].height = Math.round(objects.toArray()[i].height * scale);
            }
        }

        // 테두리에 있는 경우 개체가 이미지 내에 완전히 있는지 확인합니다..
        for (int i = 0; i < objects.toArray().length; i++ ) {
            if (objects.toArray()[i].x < 0)
                objects.toArray()[i].x = 0;
            if (objects.toArray()[i].y < 0)
                objects.toArray()[i].y = 0;
            if (objects.toArray()[i].x + objects.toArray()[i].width > img.cols())
                objects.toArray()[i].x = img.cols() - objects.toArray()[i].width;
            if (objects.toArray()[i].y + objects.toArray()[i].height > img.rows())
                objects.toArray()[i].y = img.rows() - objects.toArray()[i].height;
        }

        // Return with the detected face rectangles stored in "objects".
    }

    public Olhos detectBothEyes(Mat face, CascadeClassifier eyeCascade1, CascadeClassifier eyeCascade2, Point leftEye, Point rightEye, Rect searchedLeftEye, Rect searchedRightEye)
    {
        // Skip the borders of the face, since it is usually just hair and ears, that we don't care about.
/*
    // For "2splits.xml": Finds both eyes in roughly 60% of detected faces, also detects closed eyes.
    const float EYE_SX = 0.12f;
    const float EYE_SY = 0.17f;
    const float EYE_SW = 0.37f;
    const float EYE_SH = 0.36f;
*/
/*
    // For mcs.xml: Finds both eyes in roughly 80% of detected faces, also detects closed eyes.
    const float EYE_SX = 0.10f;
    const float EYE_SY = 0.19f;
    const float EYE_SW = 0.40f;
    const float EYE_SH = 0.36f;
*/

        // For default eye.xml or eyeglasses.xml: Finds both eyes in roughly 40% of detected faces, but does not detect closed eyes.
/*        const float EYE_SX = 0.16f;
        const float EYE_SY = 0.26f;
        const float EYE_SW = 0.30f;
        const float EYE_SH = 0.28f;*/
        final float EYE_SX = 0.16f;
        final float EYE_SY = 0.26f;
        final float EYE_SW = 0.30f;
        final float EYE_SH = 0.28f;



//      검출된 이미지 좌표값들을 계산하여 반올림
        int leftX = Math.round(face.cols() * EYE_SX);
        int topY = Math.round(face.rows() * EYE_SY);
        int widthX = Math.round(face.cols() * EYE_SW);
        int heightY = Math.round(face.rows() * EYE_SH);
        int rightX = (int)Math.round(face.cols() * (1.0 - EYE_SX - EYE_SW));  // 오른쪽 눈모서리부터 시작



        Mat topLeftOfFace = new Mat (face,(new Rect(leftX, topY, widthX, heightY))); //원본이미지, 왼쪽얼굴영역 사각형 그리기
        Mat topRightOfFace = new Mat (face,(new Rect(rightX, topY, widthX, heightY))); //오른쪽영역 사각형 그리기
        Rect leftEyeRect = new Rect();
        Rect rightEyeRect = new Rect();


        //원하는 경우 검색 창을 호출자에게 반환합니다.
        if (searchedLeftEye!=null)///////////////////////////////////////////check
            searchedLeftEye = new Rect(leftX, topY, widthX, heightY);
        if (searchedRightEye!=null)///////////////////////////////////////////check
            searchedRightEye = new Rect(rightX, topY, widthX, heightY);

        //첫 번째 눈 검출을 사용하여 왼쪽 영역을 검색 한 다음 오른쪽 영역을 검색합니다.
        leftEyeRect = detectLargestObject(topLeftOfFace, eyeCascade1, leftEyeRect, topLeftOfFace.cols()); //얼굴의 왼쪽영역 검출
        rightEyeRect = detectLargestObject(topRightOfFace, eyeCascade1, rightEyeRect, topRightOfFace.cols()); //얼굴의 오른쪽영역 검출

        // 눈이 검출되지 않으면 다른 캐스케이드를 사용
        boolean try_twice = false;
        if (try_twice) {
            if (leftEyeRect.width <= 0 && !eyeCascade2.empty()) {
                leftEyeRect = detectLargestObject(topLeftOfFace, eyeCascade2, leftEyeRect, topLeftOfFace.cols());
            }

            if (rightEyeRect.width <= 0 && !eyeCascade2.empty()) {
                rightEyeRect = detectLargestObject(topRightOfFace, eyeCascade2, rightEyeRect, topRightOfFace.cols());
            }
        }

        if (leftEyeRect.width > 0) {   // 왼쪽 눈이 감지되었는지 확인
            leftEyeRect.x += leftX;    // Adjust the left-eye rectangle because the face border was removed.
            leftEyeRect.y += topY;
            leftEye = new Point(leftEyeRect.x + leftEyeRect.width/2, leftEyeRect.y + leftEyeRect.height/2);
        }
        else {
            leftEye = new Point(-1, -1);    // Return an invalid point
        }

        if (rightEyeRect.width > 0) { // 오른쪾 눈이 감지되었는지 확인
            rightEyeRect.x += rightX; //오른쪽 눈 사각형은 이미지의 오른쪽에서 시작하므로 조정합니다..
            rightEyeRect.y += topY;  // Adjust the right-eye rectangle because the face border was removed.
            rightEye = new Point(rightEyeRect.x + rightEyeRect.width/2, rightEyeRect.y + rightEyeRect.height/2);
        }
        else {
            rightEye = new Point(-1, -1);    // Return an invalid point
        }

//      Olhos 클래스의 OpenCV기능인 Point와 Rect객체를 생성하고 사용하여 인자값을 리턴
        Olhos olhos = new Olhos();
        olhos.leftEye = leftEye;
        olhos.rightEye = rightEye;
        olhos.searchedRightEye = searchedRightEye;
        olhos.searchedLeftEye = searchedLeftEye;
        return olhos;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "called onCreateOptionsMenu");
        if (mOpenCvCameraView.numberCameras()>1)
        {
            nBackCam = menu.add("front");
            mFrontCam = menu.add("back");
//        mEigen = menu.add("EigenFaces");
//        mLBPH.setChecked(true);
        }
        else
        {imCamera.setVisibility(View.INVISIBLE);

        }
        //mOpenCvCameraView.setAutofocus();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
//        if (item == mItemFace50)
//            setMinFaceSize(0.5f);
//        else if (item == mItemFace40)
//            setMinFaceSize(0.4f);
//        else if (item == mItemFace30)
//            setMinFaceSize(0.3f);
//        else if (item == mItemFace20)
//            setMinFaceSize(0.2f);
//        else if (item == mItemType) {
//            mDetectorType = (mDetectorType + 1) % mDetectorName.length;
//            item.setTitle(mDetectorName[mDetectorType]);
//            setDetectorType(mDetectorType);
//
//        }
        nBackCam.setChecked(false);
        mFrontCam.setChecked(false);
        //  mEigen.setChecked(false);
        if (item == nBackCam)
        {
            mOpenCvCameraView.setCamFront();
            mChooseCamera=frontCam;
        }
        //fr.changeRecognizer(0);
        else if (item==mFrontCam)
        {
            mChooseCamera=backCam;
            mOpenCvCameraView.setCamBack();

        }

        item.setChecked(true);

        return true;
    }

    private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }

    private void setDetectorType(int type) {
//        if (mDetectorType != type) {
//            mDetectorType = type;
//
//            if (type == NATIVE_DETECTOR) {
//                Log.i(TAG, "Detection Based Tracker enabled");
//                mNativeDetector.start();
//            } else {
//                Log.i(TAG, "Cascade detector enabled");
//                mNativeDetector.stop();
//            }
//        }
    }




}
