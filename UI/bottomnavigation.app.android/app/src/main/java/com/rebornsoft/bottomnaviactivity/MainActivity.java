package com.rebornsoft.bottomnaviactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rebornsoft.bottomnaviactivity.navigation.CalendarFragment;
import com.rebornsoft.bottomnaviactivity.navigation.HomeFragment;
import com.rebornsoft.bottomnaviactivity.navigation.PlusFragment;
import com.rebornsoft.bottomnaviactivity.navigation.SettingFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment homeFragment = new HomeFragment();
    Fragment plusFragment = new PlusFragment();
    Fragment calendarFragment = new CalendarFragment();
    Fragment settingFragment = new SettingFragment();
    Menu menu;
    public static BottomNavigationView bottom_navi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findviewByIdFunc();
        bottom_navi.setOnNavigationItemSelectedListener(this);
        menu = bottom_navi.getMenu();
        //첫화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_content, homeFragment).commit();
        bottom_navi.setOnNavigationItemSelectedListener(this);
    }
    //백버튼 실행 시 홈 화면으로
    public interface OnBackPressedListener {
        void onBackPressed();
    }
    public static void setHomeItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.home);
    }
    public static void setApplyItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.plus);
    }
    public static void setCalenderItem(Activity activity) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                activity.findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.calendar);
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navi);
        int seletedItemId = bottomNavigationView.getSelectedItemId();
        if (R.id.home != seletedItemId) {
            setHomeItem(MainActivity.this);
        } else {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            if (!fragmentList.isEmpty()) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, homeFragment).commit();

                for(Fragment fragment : fragmentList){
                    if(fragment instanceof OnBackPressedListener){
                        ((OnBackPressedListener)fragment).onBackPressed();
                    }
                }
            }
        }
    }
    private void findviewByIdFunc() {
        bottom_navi = findViewById(R.id.bottom_navi);


    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.home:

                menu.findItem(R.id.home).setIcon(R.drawable.home);
                menu.findItem(R.id.plus).setIcon(R.drawable.plus);
                menu.findItem(R.id.calendar).setIcon(R.drawable.calendar);
                menu.findItem(R.id.setting).setIcon(R.drawable.setting);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, homeFragment).commit();
                return true;

            case R.id.plus:
                item.setIcon(R.drawable.plus);
                menu.findItem(R.id.calendar).setIcon(R.drawable.calendar);
                menu.findItem(R.id.setting).setIcon(R.drawable.setting);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, plusFragment).commit();

                return true;

            case R.id.calendar:
                item.setIcon(R.drawable.calendar);
                menu.findItem(R.id.plus).setIcon(R.drawable.plus);
                menu.findItem(R.id.setting).setIcon(R.drawable.setting);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, calendarFragment).commit();

                return true;

            case R.id.setting:
                item.setIcon(R.drawable.setting);
                menu.findItem(R.id.plus).setIcon(R.drawable.plus);
                menu.findItem(R.id.calendar).setIcon(R.drawable.calendar);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, settingFragment).commit();
                return true;

        }

        return false;
    }
}