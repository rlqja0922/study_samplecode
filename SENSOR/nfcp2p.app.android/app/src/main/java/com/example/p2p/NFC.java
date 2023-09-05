package com.example.p2p;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NFC extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        TextView text = (TextView) findViewById(R.id.text);

        // NfcAdapter 인스턴스 취득
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(NFC.this);

        // 유효한지 그렇지 않은지를 확인
        if( adapter.isEnabled() ){
            text.setText("NFC is enable");
            Log.i("NFC04","NFC is enable");
        }else {
            text.setText("NFC is disable");
            Log.i("NFC04", "NFC is disable");
        }
    }
}