package com.example.soundpoolex;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    SoundPool soundf, sounds;
    Button btn;
    int tom, tomm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        soundf = new SoundPool(1,  // 최대 음악파일의 개수
                AudioManager.STREAM_MUSIC,  //스트림 타입
                0);   // 음질 - 기본값:0


        // 각각의 재생하고자하는 음악을 미리 준비한다.
        tom = soundf.load(this, // 현재 화면의 제어권자
                R.raw.success,  // 음악 파일
                1);  // 우선순위

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                soundf.play(tom,   // 준비한 soundID
                        1,      // 왼쪽 볼륨 float
                        1,      // 오른쪽 볼륨 float
                        0, // 우선순위 int
                        0,  // 반복횟수 int -1:무한반복, 0:반복안함
                        1); // 재생속도 float 0.5(절반속도) 2.0 (2배속)
            }
        });


        sounds = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        tomm = sounds.load(this, R.raw.sound_dingdong, 1);
        btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                sounds.play(tomm,1,1,0,0,1);
            }
        });

    }
}