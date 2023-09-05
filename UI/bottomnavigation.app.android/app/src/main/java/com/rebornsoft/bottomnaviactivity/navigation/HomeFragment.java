package com.rebornsoft.bottomnaviactivity.navigation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.rebornsoft.bottomnaviactivity.MainActivity;
import com.rebornsoft.bottomnaviactivity.R;


public class HomeFragment extends Fragment implements MainActivity.OnBackPressedListener {
    private static final String TAG = "HomeFragment";
    private static Toast toast;
    private long backPressedTime = 0;
    Fragment homeFragment = HomeFragment.this;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home, container, false);

        return view;
    }

    //홈에서 백버튼 시 종료
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backPressedTime + 2000) {
            backPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(getActivity(), getString(R.string.toast_back_btn), Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backPressedTime + 2000) {
            try {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }catch (Exception e){
                Log.d(TAG, e.getMessage());
            }
            toast.cancel();
        }
    }

    @NonNull
    public void onMainBackPressed() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, homeFragment).commit();
        transaction.addToBackStack (null);
    }
}