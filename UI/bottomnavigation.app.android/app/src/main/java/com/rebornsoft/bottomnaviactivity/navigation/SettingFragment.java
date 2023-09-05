package com.rebornsoft.bottomnaviactivity.navigation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.rebornsoft.bottomnaviactivity.R;

public class SettingFragment extends Fragment{

    private static final String TAG = "SettingActivity";

    Fragment settingFragment = SettingFragment.this;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_setting, container, false);

        return view;
    }


}