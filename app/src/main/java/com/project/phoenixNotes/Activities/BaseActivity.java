package com.project.phoenixNotes.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.project.phoenixNotes.R;
import com.project.phoenixNotes.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    protected void getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Constants.sHeight = displayMetrics.heightPixels;
        Constants.sWidth = displayMetrics.widthPixels;
    }

    public void replaceFragment(int containerId, Fragment fragment, String tag, String backstackTag, Boolean animation) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (animation)
            fragmentTransaction.setCustomAnimations(R.anim.slide_up_fragment, R.anim.slide_down_fragment, R.anim.slide_up_fragment, R.anim.slide_down_fragment);
        if (backstackTag == null) {
            fragmentTransaction.replace(containerId, fragment, tag).commit();
        } else
            fragmentTransaction.replace(containerId, fragment, tag).addToBackStack("").commit();
    }

    public void addFragment(int containerId, Fragment fragment, String tag, String backstackTag, Boolean animation) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (animation)
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_right, R.anim.exit_to_left);
        if (backstackTag == null) {
            fragmentTransaction.add(containerId, fragment, tag).commit();
        } else
            fragmentTransaction.add(containerId, fragment, tag).addToBackStack("").commit();
    }

    public void removeFragment() {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (Fragment activeFragment : getSupportFragmentManager().getFragments()) {
            transaction.remove(activeFragment);
        }
        transaction.commitAllowingStateLoss();
    }


    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }

    public String getCurrentDateAndTime(String pattern) {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        String formattedDate = df.format(currentTime);
        return formattedDate;
    }
}
