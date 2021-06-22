package io.thoughtbox.hamdan.utls;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import io.thoughtbox.hamdan.R;


public class Loader {
    private Context context;

    public Loader(Context context) {
        this.context = context;

    }

    public Dialog progress() {
        Dialog progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);

        return progressDialog;
    }

}
