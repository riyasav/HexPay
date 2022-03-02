package io.thoughtbox.hamdan.utls;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.ProgressDialogBinding;


public class Loader extends AppCompatActivity {




    private Context context;

    public Loader(Context context) {
        this.context = context;

    }


    ImageView loader;
    Animation pulse;

    public Dialog progress() {
        Dialog progressDialog = new Dialog(context);



        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        loader=progressDialog.findViewById(R.id.imageloader);
        pulse =  AnimationUtils.loadAnimation(context, R.anim.loader_anim);
        loader.startAnimation(pulse);







        return progressDialog;
    }

}
