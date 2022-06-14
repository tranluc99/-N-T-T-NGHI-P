package com.example.iotapplication.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.iotapplication.R;


public class CustomLoadingDialog {
    Activity activity;
    AlertDialog dialog;

    public CustomLoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialog_custom, null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
        dialog.dismiss();
    }

    public boolean isShowing() {
        return dialog.isShowing();
    }
}
