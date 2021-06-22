package io.thoughtbox.hamdan.utls;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.print.PrintHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ShareView {
    Context context;

    public ShareView(Context context) {
        this.context = context;
    }

    private void shareBitmap(Bitmap bitmap, String shareMsg, String shareTitle) {
        Bitmap icon = bitmap;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.setType("image/*");
//        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "PaymentReceipt.jpg");
        try {
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
//        share.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "PaymentReceipt.jpg"));
        share.putExtra(Intent.EXTRA_TEXT, shareMsg);
        context.startActivity(Intent.createChooser(share, shareTitle));


    }

    private void shareBitmapImage(Bitmap bitmap, String shareMsg, String shareTitle) {

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/jpeg");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, shareTitle, null);
        Uri imageUri =  Uri.parse(path);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        share.putExtra(Intent.EXTRA_TEXT, shareMsg);
        context.startActivity(Intent.createChooser(share, shareTitle));
    }

//    private Bitmap CreateImage(View cardView) {
//        cardView.setDrawingCacheEnabled(true);
//
//// this is the important code :)
//// Without it the cardViewiew will hacardViewe a dimension of 0,0 and the bitmap will be null
//        cardView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        cardView.layout(0, 0, cardView.getMeasuredWidth(), cardView.getMeasuredHeight());
//
//        cardView.buildDrawingCache(true);
//
////        cardView.setDrawingCacheEnabled(true);
//
//        Bitmap bitmap = Bitmap.createBitmap(cardView.getDrawingCache());
//        cardView.setDrawingCacheEnabled(false);
//        return bitmap;
//    }

    public Bitmap loadBitmapFromView(View v) {
        DisplayMetrics dm = v.getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);

        return returnedBitmap;
    }

    public void share(View view, String shareMsg, String shareTitle) {
        shareBitmapImage(loadBitmapFromView(view), shareMsg, shareTitle);
    }

    public void printReceipt(View receipt, String jobName) {
        PrintHelper photoPrinter = new PrintHelper(context);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        receipt.setDrawingCacheEnabled(true);
        receipt.buildDrawingCache();
        Bitmap bitmap = receipt.getDrawingCache();
        photoPrinter.printBitmap(jobName, bitmap);
    }
}
