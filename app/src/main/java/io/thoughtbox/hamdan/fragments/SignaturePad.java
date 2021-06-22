package io.thoughtbox.hamdan.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.FragmentSignaturePadBinding;
import io.thoughtbox.hamdan.utls.Constants;
import io.thoughtbox.hamdan.views.Policy;
import io.thoughtbox.hamdan.views.Signup;

public class SignaturePad extends Fragment {
    private FragmentSignaturePadBinding binding;
    private Signature mSignature;
    private FloatingActionButton clear;
    private LinearLayout pad;
    private byte[] sign;

    public SignaturePad() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signature_pad, container, false);
        binding.setLifecycleOwner(this);
        init();
        binding.proceed.setOnClickListener(v -> {
            if (binding.checkBox.isChecked() && binding.checkBox1.isChecked()){
                mSignature.save();
                String savedSignature = saveCompressedImage(sign);
                if (savedSignature != null) {
                    File signFile = new File(savedSignature);
                    Signup.imageData.put("sign", signFile);
                    if (sign!=null){
                        signupUser();
                    }else{
                        Toast.makeText(getContext(), "Signature required", Toast.LENGTH_SHORT).show();
                    }

                }
            }else{
                Toast.makeText(getContext(), "Check all boxes to proceed", Toast.LENGTH_SHORT).show();
            }

        });
        clear.setOnClickListener(v -> mSignature.clear());

        binding.terms.setOnClickListener((View.OnClickListener) v -> {
            Intent intent = new Intent(getContext(), Policy.class);
            intent.putExtra("url", Constants.Terms);
            intent.putExtra("title", "Terms & Conditions");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        binding.back.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().popBackStack();

//            IDFragment idInfo = new IDFragment();
//            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//            ft.replace(R.id.container, idInfo);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            ft.commit();

        });
        return binding.getRoot();
    }

    private void signupUser() {
        UploadRegisterData uploaddata = new UploadRegisterData();
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, uploaddata).addToBackStack(null);
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack(null);

//        UploadRegisterData uploaddata = new UploadRegisterData();
//        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//        ft.replace(R.id.container, uploaddata);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        ft.commit();
    }

    private void init() {
        clear = binding.clearall;
        mSignature = new Signature(getContext(), null);
        pad = binding.signPad;
        pad.addView(mSignature);
        binding.proceed.setEnabled(false);
    }

    private String saveCompressedImage(byte[] image) {
        File storageDir = new File(Objects.requireNonNull(getContext()).getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Signature");
        return createFile(storageDir, image);

    }

    private String createFile(File folder, byte[] image) {
        //Create a directory to save signature
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                Log.d("REGISTER", "Oops! Failed create " + "Signature" + " directory");
                return null;
            }
        }

        //Create a unique name for the file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File imageFile;
        imageFile = new File(folder.getPath() + File.separator + "Signature" + timeStamp + ".jpg");

        //Remove any signature file exist
        if (imageFile.exists()) {
            imageFile.delete();
        }

        if (image != null) {
            try {
                FileOutputStream fos = new FileOutputStream(imageFile.getPath());
                fos.write(image);
                fos.close();
            } catch (java.io.IOException e) {
                Log.e("sign", "Exception in photoCallback", e);
            }
        }

        String photoPath = imageFile.getPath();
        if (!photoPath.isEmpty()) {
            return photoPath;
        }
        return null;
    }

    public class Signature extends View {
        static final float STROKE_WIDTH = 10f;
        static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        final RectF dirtyRect = new RectF();
        Paint paint = new Paint();
        Path path = new Path();
        float lastTouchX;
        float lastTouchY;

        public Signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void clear() {
            path.reset();
            invalidate();
            binding.proceed.setEnabled(false);
        }

        public void save() {
            Bitmap signnatureLayout = Bitmap.createBitmap(pad.getWidth(), pad.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(signnatureLayout);
            Drawable bgDrawable = pad.getBackground();

            if (bgDrawable != null) {
                bgDrawable.draw(canvas);

            } else {
                canvas.drawColor(Color.WHITE);
                pad.draw(canvas);

                getActivity().runOnUiThread(() -> {

                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    signnatureLayout.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    sign = bs.toByteArray();
                });
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            binding.proceed.setVisibility(VISIBLE);
            canvas.drawPath(path, paint);

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            binding.proceed.setEnabled(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:

                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        path.lineTo(historicalX, historicalY);
                    }
                    path.lineTo(eventX, eventY);
                    break;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH), (int) (dirtyRect.top - HALF_STROKE_WIDTH), (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }
}
