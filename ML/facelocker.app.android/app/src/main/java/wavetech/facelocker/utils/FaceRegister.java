package wavetech.facelocker.utils;


import java.io.File;

import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_core;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.resize;





//New imports
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import org.bytedeco.javacpp.opencv_face.FisherFaceRecognizer;
import org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer;
//import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import wavetech.facelocker.CameraActivity;

public class FaceRegister{
  private static final int maxImages=30;
  private int savedImagesCount=0;
  private  static String TAG = CameraActivity.TAG;
  private final String imgPath = Environment.DIRECTORY_PICTURES;
  FaceRecognizer faceRecognizer;
  static{
    //System.loadLibrary("tbb");
    //System.loadLibrary("opencv_core");

  }
  public FaceRegister(){
    //faceRecognizer =   FisherFaceRecognizer.create();//com.googlecode.javacv.cpp.opencv_contrib.createLBPHFaceRecognizer(2,8,8,8,200);
  }

  public static int getMaxImages() {
    return maxImages;
  }

  private long lastPredictTime;

  public boolean predict(Context context,opencv_core.Mat mat) throws IOException{

    // 중복클릭 방지
    long lastClickTime = lastPredictTime;
    long now = System.currentTimeMillis();
    if (now - lastClickTime < 500) {
      //Log.d(TAG, "Too much predict call ignored");
      return false;
    }
    lastPredictTime=now;



    // 이미지를 저장할 경로 지정  File = sdcard 내장메모리.
    PasswordStore passwordStore=new PasswordStore(context);  //SharedPreference 사용하는 DB 초기화.
    File path = context.getFilesDir(); //  파일경로 초기화.
    File file = new File(path, "current.jpg"); // 파일이름 초기화.

    opencv_core.Mat resizeimage=new opencv_core.Mat(); // 리사이징된 이미지를 담을 객체 생성.
    opencv_core.Size size = new opencv_core.Size(200, 200); // 200,200 사이즈 지정.
    resize(mat,resizeimage,size); // 사이즈 변경된 이미지를 적용 합니다.
    imwrite(file.getAbsolutePath(),resizeimage); // 지정된 경로로 이미지를 저장.

    // 리사이징된 이미지를 그대로 가져오기위해 사용 Flag값(IMAGE_GRAYSCALE) : 0으로 설정되어있음.
    opencv_core.Mat image=imread(file.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

    // predict 함수를 사용하기위한 매개변수로  Javacpp.predict함수에서 사용되는 변수
    IntPointer label = new IntPointer(1);
    DoublePointer confidence = new DoublePointer(1);

   // javacpp.opencv 가 성공적으로 로드가 되면
    if(faceRecognizer==null){
      File trainingFile=new File(path, "train.xml"); //파일 이름과 경로 생성.

//      1. radius : 원형 바이너리 패턴을 만드는데 사용 반경이 클수록 이미지가 더 부드러워집니다.
//      2. neighbors : 원형 패턴을 구축하기위해 필요한 샘플 포인트 갯수 8이 기본값.
//      3. grid_x : 가로방향의 셀의 갯수, 8은 기본값.
//      4. grid_y : 세로방향의 셀의 갯수, 8은 기본값.
//      5. threshold : 고정된 임계값을 적용합니다. 너무 작거나 큰값을 가진 픽셀을 필터링하는데 사용.
      faceRecognizer = LBPHFaceRecognizer.create(2,8,8,8,200); // OpenCV.JavaCPP LBP알고리즘을 사용한 메소드.
      faceRecognizer.read(trainingFile.getAbsolutePath()); // 지정된 train.xml 파일을 로드함.
    }
//    1. image : 비교할 이미지 샘플
//    2. label : 주어진 이미지에 대한 예측 라벨
//    3. confidence : 예측 거리
    faceRecognizer.predict(image, label, confidence);  // 이미지를 비교하는 메소드 OpenCV.Javacpp에서 불러와 사용되는 함수.

    int predictedLabel = label.get(0);

    image.release(); //메모리 관리를위한 해제
    resizeimage.release(); //메모리 관리를위한 해제
    if(passwordStore.hasFaceLabel(predictedLabel) && confidence.get(0)<80) // 분석필요
      return true;
    return false;
  } //predict




  public boolean clearFaceDatabase(Context context){
    File path = context.getFilesDir();
    File targetFile=new File(path, "train.xml");
    return targetFile.delete();
  }
  public void trainModels(Context context) throws IOException{ //분석 필요
    FilenameFilter fileNameFilter = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(".jpeg");
        // File 객체를 생성할때 특정 파일명을 사용하기위해서  사용
      };
    };
    File path = context.getFilesDir(); // /data/data/패키지이름/files 저장경로 설정
    File[] capturedImages=path.listFiles(fileNameFilter); // File 배열로 변환
    opencv_core.Mat labels = new opencv_core.Mat(capturedImages.length, 1, opencv_core.CV_32SC1);
    PasswordStore passwordStore=new PasswordStore(context);
    int label=passwordStore.getIncrementFaceLabel();
    //int[] labels = new int[capturedImages.length];
    MatVector images = new MatVector(capturedImages.length);
    opencv_core.Mat mats[] =new opencv_core.Mat[capturedImages.length];
    IntBuffer labelsBuf = labels.createBuffer();
    for (int i=0;i<capturedImages.length;i++)
    {
      File imageFile=capturedImages[i];
      opencv_core.Mat img = imread(imageFile.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
      imageFile.delete();
      if(img==null){
        throw  new IOException("Unable to load CV IMage from path: "+imageFile.getAbsolutePath());
      }
      labelsBuf.put(i, label);
      images.put(i, img);
      mats[i]=img;
    }
    File databaseModelFile=new File(path, "train.xml"); // 검출결과를 저장할 파일 생성
//      1. radius : 원형 바이너리 패턴을 만드는데 사용, 반경이 클수록 이미지가 더 부드러워집니다.
//      2. neighbors : 원형 패턴을 구축하기위해 필요한 샘플 포인트 갯수 8이 기본값.
//      3. grid_x : 가로방향의 셀의 갯수, 8은 일반적으로 사용되는 값.
//      4. grid_y : 세로방향의 셀의 갯수, 8은 일반적으로 사용되는 값.
//      5. threshold : 고정된 임계값을 적용합니다. 너무 작거나 큰값을 가진 픽셀을 필터링하는데 사용.
    FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create(2,8,8,8,200); // OpenCV.JavaCpp의 얼굴검출 메소드 사용
    //Read existing faces database, and update with new faces
    if(databaseModelFile.exists()) { // 해당파일이 없다면 새로 만듬
      faceRecognizer.read(databaseModelFile.getAbsolutePath()); // 위에 저장했던 파일을 불러옴
      faceRecognizer.update(images,labels); // 업데이트 새로 만듬
    }
    else{
      faceRecognizer.train(images,labels); // OpenCVcore에 있는 알고리즘 사용 하여 train
    }


    faceRecognizer.save(databaseModelFile.getAbsolutePath());
    passwordStore.addFace(passwordStore.getCurrentFaceName(),label);
    Log.v(TAG,"Saving Face: Name"+passwordStore.getCurrentFaceName()+" Label:"+label);
    passwordStore.save();
    Log.v(TAG,"Saved: "+databaseModelFile.exists()+" image path: " + capturedImages[0].getAbsolutePath());


    for(opencv_core.Mat mat:mats) mat.release();
  }
  private void saveMatToImg(Context context,opencv_core.Mat mat) throws IOException {


      // Resize image to 100x100

      opencv_core.Mat resizeimage=new opencv_core.Mat();
      opencv_core.Size size = new opencv_core.Size(200, 200);
      resize(mat,resizeimage,size);
      File path = context.getFilesDir();//Environment.getExternalStoragePublicDirectory(imgPath);
      Log.v(TAG,"Path Exists: "+path.exists()+" Path: "+path.getAbsolutePath());

      if (savedImagesCount<getMaxImages()) savedImagesCount++;


      String filename = "pic" + savedImagesCount + ".jpeg";
      File file = new File(path, filename);
      Log.v(TAG,"Created: "+file.createNewFile());

      filename = file.toString();

      //Bitmap for processing and saving image
      //Bitmap bitmap= Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
      //MatOfInt param=new MatOfInt(CV_IMWRITE_JPEG_QUALITY,100);
      boolean saved=imwrite(filename,resizeimage);
      if(!saved)
        throw  new IOException("Failed to save image: "+filename+" to external storage!");
  }

  public int getSavedImagesCount() {
    return savedImagesCount;
  }

  private long lastDebounceTime;
  public void debounceImageSaveCall(Context context, opencv_core.Mat mat, long delay) throws  IOException{
    long lastClickTime = lastDebounceTime;
    long now = System.currentTimeMillis(); // 1970.1.1 부터 현재시간을 리턴

    if (now - lastClickTime < delay) { // delay: 300
      //Log.d(TAG, "Too much call ignored");
    }
    else{
      lastDebounceTime = now;
      Log.v(TAG,"Calling save");
      saveMatToImg(context,mat);
    }
  }
}
