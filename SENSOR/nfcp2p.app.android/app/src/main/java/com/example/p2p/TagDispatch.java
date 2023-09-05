package com.example.p2p;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

public class TagDispatch extends Activity{

    private TextView textView;
    private NfcAdapter mNfcAdapter; //NfcAdapter 를 선언
    Intent intent; // PendingIntent 용 intent
    private PendingIntent mPendingIntent; // PendingIntent 선언
    private IntentFilter[] mIntentFilters; // 이건.. 필터라고 되어있으니, 받은 NdefMessage를 적당히 필터해주는 역할인 듯?
    String userName, userMsg, userMsg2;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_p2_pget);
        textView = (TextView) findViewById(R.id.tv);



        // 역시나 NFC 가 사용가능한지 체크

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter != null) {
            textView.setText("Read an NFC tag");
        } else {
            textView.setText("This phone is not NFC enabled.");
        }



        // PendingIntent 선언 부분

        // 참고로 PendingIntent는 그 뭐라드라.. 미리 intent를 선언하고 나중에 어떤 이벤트가 발생할 때, intent가 실행되게 하는 것이다. 예약 intent라고 생각하면 편하다. 여기서는 onResume()에 사용되었다

        intent = new Intent(getApplicationContext(), TagDispatch.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                intent, 0);

        // set an intent filter for all MIME data
        IntentFilter ndefIntent = new IntentFilter(
                NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndefIntent.addDataType("*/*");
            mIntentFilters = new IntentFilter[] { ndefIntent };
        } catch (Exception e) {
            Log.e("TagDispatch", e.toString());
        }

    }



    @Override
    public void onResume() {
        super.onResume();

  //PendingIntent 사용 허용
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
        Log.d("onResume", "onResume");
    }

    @Override
    public void onPause(){
        super.onPause();

  //PendingIntent 사용차단
        mNfcAdapter.disableForegroundDispatch(this);
    }



// onNewIntent 라는 것은.. 현재 액티비티가 재 호출 될때 발생한다.

// onCreate가 새로 생성될 때 일어나는 이벤트라고 하면, onNewIntent는 현재 액티비티가 생성은 되어 있는데, 재호출 될때 동작한다. onCreate 에서 intent 선언할 때 intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) 와 같이 플래그를 주게 되면 발생한다.

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        textView = (TextView) findViewById(R.id.tv);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                Ndef ndef = Ndef.get(tag);
                if (ndef != null) {
                    try {
                        ndef.connect();
                    } catch (Exception e) {
                        Log.e("TagWriting", e.toString());
                    } finally {
                        try {
                            ndef.close();
                        } catch (Exception e) {
                        }
                    }
                }
            }
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                }
                // Process the messages array.
                Parcelable[] rawMsgs = intent
                        .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                // only one message sent during the beam
                NdefMessage msg = (NdefMessage) rawMsgs[0];
                // record 0 contains the MIME type, record 1 is the AAR, if present
                String strSite = new String(msg.getRecords()[0].getPayload());
                if (strSite != null && !strSite.equals("")) {
                    userName = strSite.substring(strSite.indexOf("userName") + 9,
                            strSite.indexOf("\n"));
                    userMsg = strSite.substring(
                            strSite.indexOf("userMsg") + 8,
                            strSite.indexOf("\n", strSite.indexOf("userMsg") + 8));
                    userMsg2 = strSite.substring(strSite.indexOf("userMsg2") + 9,
                            strSite.length());
                }

                textView.setText("userName = "+userName +"\n"+
                        "userMsg = "+userMsg +"\n"+
                        "userMsg2 = " +userMsg2);
                Log.d("onNewIntent", "onNewIntent");
                setIntent(intent);

                return;
            }
        }

    }

}