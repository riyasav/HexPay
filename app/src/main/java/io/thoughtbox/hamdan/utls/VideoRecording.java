package io.thoughtbox.hamdan.utls;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import io.thoughtbox.hamdan.R;

public class VideoRecording extends AppCompatActivity implements SurfaceHolder.Callback {
    ImageView myButton;
    MediaRecorder mediaRecorder;
    SurfaceHolder surfaceHolder;
    boolean recording;
    Camera mCamera;
    TextView counterDown, note, title;
    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recording = false;
        mediaRecorder = new MediaRecorder();
        initMediaRecorder();

        setContentView(R.layout.activity_video_recording);
        SurfaceView myVideoView = findViewById(R.id.camera);
        surfaceHolder = myVideoView.getHolder();
        surfaceHolder.addCallback(this);
        counterDown = findViewById(R.id.timer);
        myButton = findViewById(R.id.record);
        note = findViewById(R.id.note);
        title = findViewById(R.id.title);
        myButton.setOnClickListener(view -> {
            note.setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            myButton.setClickable(false);
            startTimer();
            mediaRecorder.start();
            recording = true;
        });
    }


    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        // TODO Auto-generated method stub
        prepareMediaRecorder();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        // TODO Auto-generated method stub

    }

    private void initMediaRecorder() {
        mCamera = Camera.open(1);
        mCamera.setDisplayOrientation(90); // use for set the orientation of the preview
        mediaRecorder.setOrientationHint(270); // use for set the orientation of output video
        mediaRecorder.setCamera(mCamera);
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        CamcorderProfile camcorderProfile_HQ = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        mediaRecorder.setProfile(camcorderProfile_HQ);
        mediaRecorder.setOutputFile(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/selfievideo.mp4");
        mediaRecorder.setMaxDuration(5000); // Set max duration 3 sec.
        mediaRecorder.setMaxFileSize(500000); // Set max file size 0.5M


    }

    private void prepareMediaRecorder() {
        mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                String seconds = String.valueOf(millisUntilFinished / 1000);
                counterDown.setText(seconds);

            }

            public void onFinish() {
                if (recording) {
                    mediaRecorder.stop();
                    mCamera.lock();
                    mediaRecorder.release();
                    returnBackToActivity();
                }
            }
        };
        cTimer.start();
    }

    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        cancelTimer();
        mCamera.release();
        super.onDestroy();
    }

    private void returnBackToActivity() {
        String fileUrl = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/selfievideo.mp4";
        Intent returnIntent = new Intent();
        returnIntent.putExtra("path", fileUrl);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        super.onBackPressed();
    }

    public void dismissWithCheck(Dialog dialog) {
        if (dialog != null) {
            if (dialog.isShowing()) {

                //get the Context object that was used to great the dialog
                Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();

                // if the Context used here was an activity AND it hasn't been finished or destroyed
                // then dismiss it
                if (context instanceof Activity) {

                    // Api >=17
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                            dismissWithTryCatch(dialog);
                        }
                    } else {

                        // Api < 17. Unfortunately cannot check for isDestroyed()
                        if (!((Activity) context).isFinishing()) {
                            dismissWithTryCatch(dialog);
                        }
                    }
                } else
                    // if the Context used wasn't an Activity, then dismiss it too
                    dismissWithTryCatch(dialog);
            }
            dialog = null;
        }
    }

    public void dismissWithTryCatch(Dialog dialog) {
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
        } catch (final Exception e) {
            // Do nothing.
        } finally {
            dialog = null;
        }
    }
}

