package wavetech.facelocker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import org.bytedeco.javacpp.opencv_core;
//import org.opencv.android.CameraBridgeViewBase;
//import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.List;

import wavetech.facelocker.utils.LockscreenIntentReceiver;
import wavetech.facelocker.utils.LockscreenService;
import wavetech.facelocker.utils.LockscreenUtils;
import wavetech.facelocker.utils.PasswordStore;
import wavetech.facelocker.utils.StorageHelper;

import static org.bytedeco.javacpp.opencv_core.LINE_8;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;

public class LockScreen extends AbstractCameraPreviewActivity
  implements
  LockscreenUtils.OnLockStatusChangedListener
{

  // User-interface
  private Button btnUnlock;

  // Member variables
  private LockscreenUtils mLockscreenUtils;

  private PatternLockViewListener patternLockViewListener;
  private PatternLockView mPatternLockView;
  private PasswordStore passwordStore;
  private EditText pinCodeInput;
  boolean isPredicting=false;
  @Override
  public opencv_core.Mat onCameraFrame(opencv_core.Mat rgbaMat) {
    if (faceDetector != null) {
      opencv_core.Mat grayMat = new opencv_core.Mat(rgbaMat.rows(), rgbaMat.cols()); // mat객체 생성

      cvtColor(rgbaMat, grayMat, CV_BGR2GRAY); // 원본이미지를 회색조로 변경

      opencv_core.RectVector faces = new opencv_core.RectVector(); // detectMultiScale 함수의 인자로 사용되며 감지된 얼굴을 박스형태로 만드는 객체

//      1. image : image는 바로 검출하고자 하는 원본 이미지
//      2. <Rect> object :objects에 검출된 이미지가 채워집니다.
//      3. scaleFactor : scaleFactor 이미지 피라미드에서 사용되는 시야의 거리 설정
//      4. minNeighbors : minNeighbors 얼굴사이의 최소간격을 의미, 너무낮은 값을 사용하면 중복돼서 검출될수도 있음.
//      5. flags : OpenCV의 Cascade 옛날버전사용시 설정하는 flag 값 object를 검출할때 사용하는 방식.
//      6. minSize : 검출되는 이미지의 최소 사이즈 이보다 작은 object는 무시 됩니다.
//      7. maxSize : 검출되는 이미지의 최대 사이즈 이보다 큰 object는 무시 됩니다.
      faceDetector.detectMultiScale(grayMat, faces, 1.1, 2, 2,
        new opencv_core.Size(absoluteFaceSize, absoluteFaceSize),
        new opencv_core.Size(4 * absoluteFaceSize, 4 * absoluteFaceSize));
      opencv_core.Mat duplicateMat=rgbaMat.clone(); //원본이미지를 새로운객체로 복사함.
      if (faces.size() == 1) { // 검출된 이미지가 있다면
          int x = faces.get(0).x();
          int y = faces.get(0).y();  // 사각형의 두개의점과 가로 세로 사이즈 저장, rectangle 함수의 인자로 사용.
          int w = faces.get(0).width();
          int h = faces.get(0).height();

        // 1. rgbaMat : 원본이미지.
        // 2,3 Point : 찾은 얼굴좌표에 점2개를 받고 사각형을 그림.
        // 4. Scalar : 그리려는 사각형의 색상지정.
        // 5. thickness : 선의 두께 지정.
        // 6. LineType : 부드럽게 선을 표현하기위해 사용.
        // 7. shift : 사용될 일이 없으므로 0으로 지정( 중심 좌표의 소수 비트 수와 축 값 )
        // Point2, Point3 사이에 사각형을 그리는 함수 OpenCV.JavaCpp
          rectangle(rgbaMat, new opencv_core.Point(x, y), new opencv_core.Point(x + w, y + h), opencv_core.Scalar.GREEN, 2, LINE_8, 0);
      }
      try {
        isPredicting=true; // 이미지가 인식되면
        boolean recognizedFace=faceRegister.predict(this,duplicateMat);
        if(recognizedFace) { // 얼굴인식이 성공하면
          this.runOnUiThread(new Runnable() {
            public void run() {
              isPredicting = true;
              unlockDevice(); // finish() 가 정의되어있음 ( 앱종료 )
            }
          });
        }

      }catch (IOException e){
        Log.e(CameraActivity.TAG,"IO Error: "+ e.getMessage());
      }
      catch (Exception e){
        Log.e(CameraActivity.TAG,"Exception: "+ e.getMessage());
      }
      finally {
        isPredicting=false;
        duplicateMat.release(); // 메모리누수를 막기위해 메모리 해제
      }
      grayMat.release(); // 메모리누수를 막기위해 메모리 해제
    }

    return rgbaMat;
  }


  private void initializeListeners(){
    patternLockViewListener=new PatternLockViewListener() {
      @Override
      public void onStarted() {
      /*Log.v(getClass().getName(), "Pattern drawing started");
      showToastMessage("Pattern drawing has started");*/

      }

      @Override
      public void onProgress(List<PatternLockView.Dot> progressPattern) {
      /*Log.v(getClass().getName(), "Pattern progress: " +
        PatternLockUtils.patternToString(mPatternLockView, progressPattern));
      showToastMessage("Pattern progress: " +
        PatternLockUtils.patternToString(mPatternLockView, progressPattern));*/
      }

      @Override
      public void onComplete(List<PatternLockView.Dot> pattern) {
//      showToastMessage("Pattern complete: " +
//        PatternLockUtils.patternToString(mPatternLockView, pattern));
        //Log.v(getClass().getName(), "Pattern complete: " +
          //PatternLockUtils.patternToString(mPatternLockView, pattern));
        String patternInput=PatternLockUtils.patternToString(mPatternLockView, pattern);
        if(passwordStore.getPatternCode().equals(patternInput))
          unlockDevice();
        else{
          Toast.makeText(getApplicationContext(),"Invalid pattern!",Toast.LENGTH_SHORT).show();
          mPatternLockView.clearPattern();
        }
      }

      @Override
      public void onCleared() {
        Log.v(getClass().getName(), "Pattern has been cleared");
        //showToastMessage("pattern cleard");
      }
    };
    mPatternLockView.addPatternLockListener(patternLockViewListener);

    pinCodeInput.setOnEditorActionListener(new EditText.OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
          String pinCode=passwordStore.getPinCode();
          //Toast.makeText(getApplicationContext(),"Saved: " + pinCode+" Current: "+pinCodeInput.getText().toString(),Toast.LENGTH_SHORT).show();
          if(pinCode.equals(pinCodeInput.getText().toString()))
            unlockDevice();
          else{
            Toast.makeText(getApplicationContext(),"Incorrect Password!",Toast.LENGTH_SHORT).show();
            pinCodeInput.setText("");
          }
          return true;
        }
        return false;
      }
    });
  }

  public void showPatternView(View v){
    findViewById(R.id.patternLayout).setVisibility(View.VISIBLE);
    findViewById(R.id.pinCodeLayout).setVisibility(View.GONE);
    findViewById(R.id.cameraLayout).setVisibility(View.GONE);
  }
  public void showPinCodeView(View v){
    findViewById(R.id.pinCodeLayout).setVisibility(View.VISIBLE);
    findViewById(R.id.patternLayout).setVisibility(View.GONE);

    findViewById(R.id.cameraLayout).setVisibility(View.GONE);
    //mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
    //mOpenCvCameraView.setCvCameraViewListener(null);
  }
  public void showCameraView(View v){
    findViewById(R.id.pinCodeLayout).setVisibility(View.GONE);
    findViewById(R.id.patternLayout).setVisibility(View.GONE);

    findViewById(R.id.cameraLayout).setVisibility(View.VISIBLE);
    cameraView.setVisibility(SurfaceView.VISIBLE);
    cameraView.setCvCameraViewListener(this);
  }

  // Set appropriate flags to make the screen appear over the keyguard
  @Override
  public void onAttachedToWindow() {
    /*this.getWindow().setType(
      WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
    this.getWindow().addFlags(
      WindowManager.LayoutParams.FLAG_FULLSCREEN
        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
    );*/

    super.onAttachedToWindow();
    //this.getWindow().setType(LayoutParams.TYPE_KEYGUARD_DIALOG);
  }
  @Override
  public void onCreate(Bundle savedInstanceState) {
    this.getWindow().setType(
      WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
    this.getWindow().addFlags(
      WindowManager.LayoutParams.FLAG_FULLSCREEN
        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
    );
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_lock_screen);
    passwordStore= new PasswordStore(getApplicationContext());
    mPatternLockView=findViewById(R.id.pattern_lock_view);
    pinCodeInput = findViewById(R.id.passwordEditText);
    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... voids) { // CasCadeClassfier를 사용한 raw폴에 frontalface를 로드합니다. 안면인식에서 많이 사용되는 xml
        faceDetector = StorageHelper.loadClassifierCascade(LockScreen.this, R.raw.frontalface);
        return null;
      }
    }.execute();
    //OpenCV initializations
    cameraView = findViewById(R.id.camera_view);
    cameraView.setVisibility(SurfaceView.VISIBLE);
    cameraView.setCvCameraViewListener(this);

    //Hide the action bar
    getSupportActionBar().hide();
    initializeListeners();
    init();

    // unlock screen in case of app get killed by system
    if (getIntent() != null && getIntent().hasExtra("kill")
      && getIntent().getExtras().getInt("kill") == 1) {
      enableKeyguard();
      unlockHomeButton();
    } else {

      try {
        // disable keyguard
        disableKeyguard();

        // lock home button
        lockHomeButton();

        // start service for observing intents
        startService(new Intent(this, LockscreenService.class));

        // listen the events get fired during the call
        StateListener phoneStateListener = new StateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener,
          PhoneStateListener.LISTEN_CALL_STATE);

        initializeListeners();

      } catch (Exception e) {
        Log.e(CameraActivity.TAG, e.getMessage());
      }

    }
  }

  @Override
  public void onPause() {
    super.onPause();

    //The following are used to disable minimize button
    ActivityManager activityManager = (ActivityManager) getApplicationContext()
      .getSystemService(Context.ACTIVITY_SERVICE);

    activityManager.moveTaskToFront(getTaskId(), 0);
  }

  private void init() {
    mLockscreenUtils = new LockscreenUtils();
//    btnUnlock = (Button) findViewById(R.id.btnUnlock);
//    btnUnlock.setOnClickListener(new View.OnClickListener() {
//
//      @Override
//      public void onClick(View v) {
//        // unlock home button and then screen on button press
//        unlockDevice();
//      }
//    });
  }

  // Handle events of calls and unlock screen if necessary
  private class StateListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

      super.onCallStateChanged(state, incomingNumber);
      switch (state) {
        case TelephonyManager.CALL_STATE_RINGING:
          unlockHomeButton();
          break;
        case TelephonyManager.CALL_STATE_OFFHOOK:
          break;
        case TelephonyManager.CALL_STATE_IDLE:
          break;
      }
    }
  };

  // Don't finish Activity on Back press
  @Override
  public void onBackPressed() {
    return;
  }

  // Handle button clicks
  @Override
  public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
      || (keyCode == KeyEvent.KEYCODE_POWER)
      || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
      || (keyCode == KeyEvent.KEYCODE_CAMERA)) {
      return true;
    }
    if ((keyCode == KeyEvent.KEYCODE_HOME)) {
      Toast.makeText(getApplicationContext(),"Home  button pressed",Toast.LENGTH_LONG).show();
      return true;
    }

    return false;

  }

  // handle the key press events here itself
  public boolean dispatchKeyEvent(KeyEvent event) {
    if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
      || (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)
      || (event.getKeyCode() == KeyEvent.KEYCODE_POWER)) {
      return false;
    }
    if ((event.getKeyCode() == KeyEvent.KEYCODE_HOME)) {
      Toast.makeText(getApplicationContext(),"Home  button pressed",Toast.LENGTH_LONG).show();
      return false;
    }

    //Let the number keys work for the password text input
    if(event.getKeyCode() >= KeyEvent.KEYCODE_1 && event.getKeyCode() <= KeyEvent.KEYCODE_9)
    {
      return super.dispatchKeyEvent(event);
    }
    return false;
  }

  // Lock home button
  public void lockHomeButton() {
    mLockscreenUtils.lock(LockScreen.this);
  }

  // Unlock home button and wait for its callback
  public void unlockHomeButton() {
    mLockscreenUtils.unlock();
  }

  // Simply unlock device when home button is successfully unlocked
  @Override
  public void onLockStatusChanged(boolean isLocked) {
    if (!isLocked) {
      unlockDevice();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    unlockHomeButton();
  }

  @SuppressWarnings("deprecation")
  private void disableKeyguard() {
    KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
    KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
    mKL.disableKeyguard();
  }

  @SuppressWarnings("deprecation")
  private void enableKeyguard() {
    KeyguardManager mKM = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
    KeyguardManager.KeyguardLock mKL = mKM.newKeyguardLock("IN");
    mKL.reenableKeyguard();
  }

  // 장치 잠금해제
  private void unlockDevice()
  {
    finish();
  }
}
