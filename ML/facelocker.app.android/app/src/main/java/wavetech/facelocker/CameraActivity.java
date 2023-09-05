package wavetech.facelocker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;
//OpenCV Java Classes
import com.kaopiz.kprogresshud.KProgressHUD;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.FrameFilter;

import org.opencv.android.BaseLoaderCallback;

import org.opencv.android.LoaderCallbackInterface;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import wavetech.facelocker.utils.FaceRegister;
import wavetech.facelocker.utils.LockscreenService;
import wavetech.facelocker.utils.PasswordStore;
import wavetech.facelocker.utils.StorageHelper;

import static org.bytedeco.javacpp.opencv_core.LINE_8;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import org.opencv.core.Rect;

public class CameraActivity extends AbstractCameraPreviewActivity  {
  public static String TAG="facelocker.camera";
  CvCameraPreview mOpenCvCameraView; // 카메라
  protected KProgressHUD progressLoader; //Kprogress 라이브러리
  boolean isRecognizing=false;
  private void startScreenLock(){
    startService(new Intent(this, LockscreenService.class));
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_camera);
    //Hide the action bar
    getSupportActionBar().hide();
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    //askForPermissions();
    mOpenCvCameraView = (CvCameraPreview) findViewById(R.id.camera_view);
    mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
    mOpenCvCameraView.setCvCameraViewListener(this);











    progressLoader = KProgressHUD.create(CameraActivity.this)  //Kprogress 라이브러리 초기화
      .setStyle(KProgressHUD.Style.BAR_DETERMINATE) // 진행상태를 보여주는 설정
      .setLabel("Registering Face") // 타이틀
      .setMaxProgress(100) // 게이지
      .setCancellable(false) // 뒤로버튼사용 취소 여부
      .show();

    // OpenCV에서 사용하는 Cascade frontalface.xml을  res.raw.frontalface.xml 을 로드함
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... voids) {
        faceDetector = StorageHelper.loadClassifierCascade(CameraActivity.this, R.raw.frontalface);
        return null;
      }
    }.execute();
  }



  @Override
  public opencv_core.Mat onCameraFrame(opencv_core.Mat rgbaMat) {
//   opencv_objdetect.CascadeClassifier 성공적으로 로드가 되었다면
    if (faceDetector != null) {
      opencv_core.Mat grayMat = new opencv_core.Mat(rgbaMat.rows(), rgbaMat.cols()); // 객체생성
      cvtColor(rgbaMat, grayMat, CV_BGR2GRAY); // 이미지를 회색조로 변경

      opencv_core.RectVector faces = new opencv_core.RectVector(); // detectMultiScale의 함수의 인자로 사용됨 감지된 얼굴을 박스형태로 만드는

//       OpenCV Javacpp에서 사용되는 검출 메소드 입니다.
//      1. img    : image는 바로 검출하고자 하는 원본 이미지
//      2. object : 이미지를 찾게되면 objects에 검출된 이미지가 채워집니다.
//      3. scaleFactor  :  시야의 거리 설정
//      4. minNeighbors : 얼굴사이의 최소간격을 의미, 너무낮은 값을 사용하면 중복된 검출값이 출력될수 있음.
//      5. flag    : OpenCV.Cascade의 옛날버전사용시 설정하는 flag 값
//      6. minSize : 검출되는 이미지의 최소 사이즈 이보다 작은 faces= object는 무시 됩니다.
//      7. maxSize : 검출되는 이미지의 최대 사이즈  이보다 크 faces= object는 무시 됩니다.
      faceDetector.detectMultiScale(grayMat, faces, 1.25f, 3, 1,
        new opencv_core.Size(absoluteFaceSize, absoluteFaceSize),
        new opencv_core.Size(4 * absoluteFaceSize, 4 * absoluteFaceSize)); // absoluteFaceSize는 0값으로 초기화 되어있습니다.
      if (faces.size() == 1) { // 검출된 이미지가 있다면
        int x = faces.get(0).x(); //얼굴좌표 4군데를 찾습니다.
        int y = faces.get(0).y();
        int w = faces.get(0).width();
        int h = faces.get(0).height();

        opencv_core.Mat duplicateMat=rgbaMat.clone(); // 원본 이미지 rgbaMat을 새로운객체를 만들면서 복사 JavaCpp


        // 1. rgbaMat : 원본이미지
        // 2. Point : 찾은 얼굴좌표에 점2개를 받고 사각형을 그림
        // 3. Point : 찾은 얼굴좌표에 점2개를 받고 사각형을 그림
        // 4. Scalar : 그리려는 사각형의 색상지정
        // 5. thickness : 선의 두께 지정
        // 6. LineType : 부드럽게 선을 표현하기위해 사용
        // 7. shift : 사용될 일이 없으므로 0으로 지정(  중심 좌표의 소수 비트 수와 축 값 )
        rectangle(rgbaMat, new opencv_core.Point(x, y),
                new opencv_core.Point(x + w, y + h),  // Point2, Point3 사이에 사각형을 그리는 함수 JavaCpp
                opencv_core.Scalar.GREEN, 2, LINE_8, 0);

        if(isRecognizing) // false
          return rgbaMat;

        //1. 복사된 원본이미지, 2. 검출된 이미지의 좌표값
        opencv_core.Mat croppedMat=new opencv_core.Mat(duplicateMat,faces.get(0)); // mat객체 생성

        try {
          isRecognizing=true; //얼굴이 인식되면
          faceRegister.debounceImageSaveCall(this,duplicateMat, 300); // 중복클릭 방지
          croppedMat.release();  // 메모리 저장후 누수를 막기위해 메모리를 해제
          duplicateMat.release(); // 메모리 저장후 누수를 막기위해 메모리를 해제
          // Kprogress 라이브러리 사용하여 Progress바를 만듬    //  getMaxImages() = 30 으로 초기화
          // 현재 진행률을 설정 setProgress()
          progressLoader.setProgress(faceRegister.getSavedImagesCount()*100/FaceRegister.getMaxImages()); // 현재 촬영된 이미지갯수 / 최대가능갯수30
          if(faceRegister.getSavedImagesCount()>=FaceRegister.getMaxImages() ){ // 검출된 이미지의 갯수가 max이미지보다 같거나 많을경우

            this.runOnUiThread(new Runnable() { //쓰레드 시작
              public void run() {
                isRecognizing=true;
                try{ // 캡처된 이미지의 정보들을 저장
                  faceRegister.trainModels(CameraActivity.this.getApplicationContext());
                  Log.v(TAG,"Finish training models");
                }
                catch (IOException e){
                  e.printStackTrace();
                  Log.e(TAG,"IO Exception: "+ e.getMessage());
                }
                catch (Exception e){
                  e.printStackTrace();
                  Log.e(TAG,"IO Exception: "+ e.getMessage());
                }
//              AlertDailog 생성
                AlertDialog alertDialog = new AlertDialog.Builder(CameraActivity.this).create();
                alertDialog.setTitle("Success");
                alertDialog.setMessage("Face registered successfully!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      try {
                        // 저장할 데이터베이스를 불러옴  SharedPreference 사용하여 저장
                        PasswordStore passwordStore = new PasswordStore(getApplicationContext());
                        passwordStore.setIsScreenLockEnabled(true);
                        passwordStore.save(); // 파일저장

                        // 등록이 완료되고 확인버튼을 누르면 LockScreen으로 이동
                        startScreenLock();
                        Intent intent = new Intent(CameraActivity.this, LockScreen.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                      }catch (Exception e){

                      }
                    }
                  });
                alertDialog.show();
              }
            });





          }
        }catch (IOException e){
          Log.e(TAG,"IO Error: "+ e.getMessage());
        }
        catch (Exception e){
          Log.e(TAG,"Exception: "+ e.getMessage());
        }
        finally {
          isRecognizing=false;
        }
        // 메모리 관리를위해 주소에서 해제
      duplicateMat.release();

      }


      // 메모리 관리를위해 주소에서 해제
      grayMat.release();
    }


    return rgbaMat;
  }
}
