package com.example.TFLiteFaceExample;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.util.Vector;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class utils {
    public static float l2_distance(float[] feat1, float[] feat2){
//       두 이미지의 유사성 비교
        float distance = 0;
        for(int i = 0; i < feat1.length; i++){
            distance += Math.pow((double)(feat1[i]-feat2[i]),2); // 두이미지의 값을빼서 제곱 연산
        }
        return distance;
    }

    public static void overlay_bboxes(Mat img_mat, Vector<boundingBox> bboxes){
        boundingBox tmp;
        for(int i = 0; i < bboxes.size(); i++){
            tmp = bboxes.elementAt(i);

            // 1. rgbaMat : 원본이미지
            // 2,3 Point : 찾은 얼굴좌표에 점2개를 받고 사각형을 그림
            // 4. Scalar : 그리려는 사각형의 색상지정
            // 5. thickness : 선의 두께 지정
            // Point2, Point3 사이에 사각형을 그리는 함수 JavaCpp
            Imgproc.rectangle(img_mat, new Point(tmp.r0, tmp.r1), new Point(tmp.r2, tmp.r3), new Scalar(255, 0, 0, 255), 3);

            // 1.원본이미지  2.포인트 원의 가운데 점(객체) 3.원의 반경(int) 4.원의 색상  5.원의 두께
            if (tmp.has_landmarks) { //랜드마크
                for (int l = 0; l < 5; l++) {
                    Imgproc.circle(img_mat, new Point(tmp.landmarks[2 * l], tmp.landmarks[2 * l + 1]), 1, new Scalar(0, 255, 0), 2);
                }
            }
        }
    }

    public static Bitmap getBitmapFromAssets(Activity activity, String fileName) {

//      Asset폴더에 접근하기위해 사용하는 함수, 비트맵 이미지를 로드
        AssetManager assetManager = activity.getAssets();
        Bitmap bitmap;
        try {
            InputStream istr = assetManager.open(fileName);
            bitmap = BitmapFactory.decodeStream(istr);
        }catch(Exception e){
            Log.d("johnyi-utils","Exception "+e);
            bitmap = Bitmap.createBitmap(224,224, Bitmap.Config.ARGB_8888); // 비트맵객체에 들어갈 이미지 사이즈값을 적용
        }
        return bitmap;
    }
}