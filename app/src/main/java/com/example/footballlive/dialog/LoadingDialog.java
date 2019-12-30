package com.example.footballlive.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDialog;

import com.example.footballlive.R;

public class LoadingDialog  {
    private static LoadingDialog loadingDialog;
    AppCompatDialog dialog;

    public static LoadingDialog getInstance(){
        return loadingDialog;
    }

    public void progressON(Activity activity){

        if(activity == null || activity.isFinishing()){
            return;
        }

        if(dialog == null){
            dialog = new AppCompatDialog(activity);
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.progress_loading);
            dialog.show();
        }

        ImageView image_loading_frame = dialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable)image_loading_frame.getBackground();
        image_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });


    }

    public void progressOFF(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }



}
