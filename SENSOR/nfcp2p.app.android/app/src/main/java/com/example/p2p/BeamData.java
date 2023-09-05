package com.example.p2p;

import android.app.Activity;
import android.content.Context;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;

//Activity 로 만들되, CreateNdefMessageCallback 과 OnNdefPushCompleteCallback 인터페이스를 추가해줍니다.

public class BeamData extends Activity implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    private static final int MESSAGE_SENT = 1; //추후 Handler 메시지에 사용
    private NfcAdapter mNfcAdapter; //NfcAdapter 를 선언
    private TextView mTextView;
    Context context;
    String userName, userMsg, userMsg2;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Nfc 로 전송할 메시지 선언
        userName = "TestUser";
        userMsg = "NFC test";
        userMsg2 = "BEAM test.";

        setContentView(R.layout.activity_p2_p);
        mTextView = (TextView) findViewById(R.id.textView);



        // nfc 가 사용가능한지 체크

     mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            mTextView.setText("Tap to beam to another NFC device");
        } else {
            mTextView.setText("This phone is not NFC enabled.");
        }



        // 아래 2줄은 안드로이드빔을 성공적으로 전송했을 경우 이벤트 호출을 위해서 작성

        mNfcAdapter.setNdefPushMessageCallback(this, this);
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);

    }



    // NFC 전송 타입을 설정한다... 라고 해야 하나. 아무튼 어떤 타입의 데이터를 전송할 꺼다 라고 선언하는 부분

    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                mimeBytes, new byte[0], payload);
        Log.d("createMimeRecord", "createMimeRecord");

        return mimeRecord;
    }



// 실질적으로 Nfc 메시지(정확히는 NdefMessage라고 함) 를 생성하는 부분

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("userName:" + userName +"\n"+"userMsg:"+ userMsg + "\n"+"userMsg2:"+userMsg);
        NdefMessage msg = new NdefMessage(new NdefRecord[] { createMimeRecord(
                "com.example.p2p", text.getBytes())
                /**
                 * The Android Application Record (AAR) is commented out. When a device
                 * receives a push with an AAR in it, the application specified in the
                 * AAR is guaranteed to run. The AAR overrides the tag dispatch system.
                 * You can add it back in to guarantee that this activity starts when
                 * receiving a beamed message. For now, this code uses the tag dispatch
                 * system.
                */
                // ,NdefRecord.createApplicationRecord("com.example.android.beam")
        });
        Log.d("createNdefMessage", "createNdefMessage");
        return msg;
    }



//안드로이드 빔 보내는 작업이 성공적으로 끝날 경우 호출

// 단, 메인 쓰레드에서 동작하는게 아니기 때문에, handler를 통해서 다른 작업(Toast나 ui 변경 작업)을 해야함

    @Override
    public void onNdefPushComplete(NfcEvent arg0) {
        Log.d("onNdefPushComplete", "onNdefPushComplete");

        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SENT:
                    Toast.makeText(getApplicationContext(), "send message!!!",Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };
}