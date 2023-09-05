package com.example.p2p;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Card extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        TextView text = (TextView) findViewById(R.id.tag);
        String str = "";

        // 인텐트 취득
        Intent intent = getIntent();

        // 액션 명 취득
        String intentAction = intent.getAction();

        // TAG 검출시의 인텐트인지 아닌지 여부 체크
        if (intentAction != null ){
            if ( intentAction.equals( NfcAdapter.ACTION_TAG_DISCOVERED )){
                Log.i("NFC01", NfcAdapter.ACTION_TAG_DISCOVERED);

                // NFC에서 취득한 ID 취득
                byte[] ids = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                for( int i=0; i < ids.length; i++ ){
                    Log.i("NFC01：ID", Integer.toHexString( ids[i] ));
                    str += Integer.toHexString( ids[i]) + "\n";
                }
                text.setText(str);
            }
        }

    }
}