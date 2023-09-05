package com.rebornsoft.rbskakaoapisample;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class MyAlert {
    private static int MyXml = R.layout.alertxml;
    public static void MyDialog(Context context, String title, String content, View.OnClickListener event, View.OnClickListener event2) {
        try {
            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(MyXml, null);

            TextView Mytitle = (TextView) view.findViewById(R.id.tv_title);
            TextView Mycontent = (TextView) view.findViewById(R.id.tv_content);
            Button btnOk = (Button) view.findViewById(R.id.btn_ok);
            Button btnAgree = (Button) view.findViewById(R.id.btn_agree);
            Button btnReject = (Button) view.findViewById(R.id.btn_reject);

            Mytitle.setText(title);
            Mycontent.setText(content);
            btnOk.setVisibility(View.GONE);
            btnAgree.setOnClickListener((View.OnClickListener) event);


            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setView(view);
            AlertDialog dialog = builder.create();

            if (event2 == null){
                event2 = (v)->dialog.dismiss();
            }
            btnReject.setOnClickListener((View.OnClickListener) event2);
            dialog.show();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        catch (Exception e) {
            Log.d("showAlert", "MyAlert: error " + e.getMessage());
        }
    }
}
