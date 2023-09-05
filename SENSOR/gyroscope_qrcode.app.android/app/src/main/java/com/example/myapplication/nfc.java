package com.example.myapplication;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.charset.Charset;
import java.util.Locale;
// NFC 테그에 읽고, 쓰는 기능 
public class nfc extends AppCompatActivity {
    Button nfc,nfc1;

    TextView text, text1; //텍스트 뷰 변수
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] mlntentFilters;
    private String[][] mNFCTechLists;

    Context context;
    Activity activity;
    private NdefMessage mNdeMessage; //NFC 전송 메시지
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nfc);
        activity = this;
        nfc = findViewById(R.id.nfc);
        nfc1 = findViewById(R.id.nfc1);
        text = findViewById(R.id.text);
        text1 = findViewById(R.id.text1);
        context = nfc.getContext();

        nfcAdapter = NfcAdapter.getDefaultAdapter(context); // nfc를 지원하지않는 단말기에서는 null을 반환.
        Intent intent = new Intent(this, second.class);
                //new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //new Intent(this, second.class);

        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0); //intent의 값을 넣는다. 이것을 다음 액티비티에 넘김으로써 다음 액티비티에서 처리가가능
        /*

         *  IntentFilter 지정

         */
        IntentFilter ndefIntent = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED); // Intent to start an activity when a tag with NDEF payload is discovered.
        try{
            ndefIntent.addDataType("*/*");
        }
        catch(Exception e) {
            Log.e("TagDispatch", e.toString());
        }
        mlntentFilters = new IntentFilter [] {ndefIntent,};

        mNFCTechLists = new String[][] { new String[]{ NfcF.class.getName()}};
        //쓰기기능
        nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(nfcAdapter.isEnabled() == true) {
                    text.setText("NFC 단말기를 접촉해주세요"+nfcAdapter+"");
                    nfcAdapter.setNdefPushMessage( mNdeMessage,activity);
                }
                else {
                    text.setText("NFC 기능이 꺼져있습니다. 켜주세요"+nfcAdapter+"");
                    Intent intent = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        intent = new Intent(Settings.ACTION_NFC_SETTINGS);
                    } else {
                        intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                    }
                    startActivity(intent);
                }
            }
        });
        //읽기기능
        nfc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nfcAdapter = NfcAdapter.getDefaultAdapter(context); // nfc를 지원하지않는 단말기에서는 null을 반환.

                if(nfcAdapter.isEnabled() == true) {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
                    filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
                    filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
                    nfcAdapter.enableForegroundDispatch(nfc.this, pendingIntent, null, null);
                }

            }
        });
        mNdeMessage=new NdefMessage(
                new NdefRecord[]{
                       // NdefRecord.createTextRecord("ko", "NFC테스트")     //텍스트 데이터
                        createNewTextRecord("이름 : 홍길동", Locale.ENGLISH, true),                        //텍스트 데이터

                        createNewTextRecord("전화번호 : 010-1234-5678", Locale.ENGLISH, true),    //텍스트 데이터

                        createNewTextRecord("자격증번호 : 123456", Locale.ENGLISH, true),           //텍스트 데이터

                        createNewTextRecord("전화번호 : 111-2222-3333", Locale.ENGLISH, true),    //텍스트 데이터

                        createNewTextRecord("된다!!!!", Locale.ENGLISH, true),                  //텍스트 데이터
                }
        );

    }
    @Override

    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
        }
    }
    @Override

    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundNdefPush(this);
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }
    public static NdefRecord createNewTextRecord(String text, Locale locale, boolean encodelnUtf8){
        byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("US-ASCII"));
        Charset utfEncoding = encodelnUtf8 ? Charset.forName("UTF-8"):Charset.forName("UTF-16");
        byte[] textBytes = text.getBytes(utfEncoding);
        int utfBit = encodelnUtf8 ? 0:(1<<7);
        char status = (char)(utfBit + langBytes.length);
        byte[] data = new byte[1 + langBytes.length + textBytes.length];
        data[0] = (byte)status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
        return new NdefRecord(NdefRecord.TNF_WELL_KNOWN,NdefRecord.RTD_TEXT, new byte[0], data);
    }

}
