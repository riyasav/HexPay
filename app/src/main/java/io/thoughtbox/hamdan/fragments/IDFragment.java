package io.thoughtbox.hamdan.fragments;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.thoughtbox.hamdan.Adapters.SelectionListener;
import io.thoughtbox.hamdan.BuildConfig;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.model.DateTimeResponseDate;
import io.thoughtbox.hamdan.utls.Constants;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.utls.VideoRecording;
import io.thoughtbox.hamdan.alerts.BottomLists;
import io.thoughtbox.hamdan.databinding.FragmentIdBinding;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.viewModel.RegisterViewModel;
import io.thoughtbox.hamdan.views.Signup;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class IDFragment extends Fragment implements SelectionListener {
    private FragmentIdBinding binding;
    private RegisterViewModel viewModel;
    private ArrayList<SelectionModal> idtypeList;
    private Dialog progressDialog;
    private BottomLists bottomLists;
    private String idTypeId;
    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener idExpry;
    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    private String filepath_1 = "ImageEmpty";
    private String filepath_2 = "ImageEmpty";
    private String video_filepath = "VideoEmpty";

    private boolean is_clicked_1 = false;
    private boolean is_clicked_2 = false;
    private boolean isVideoClicked = false;
    private ImageView image1, image2;
    private VideoView videoView;
    private Uri fileUri;
    private String filepath = null;

    long currentDateTime=0;


    public IDFragment() {
        // Required empty public constructor
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_id, container, false);
        binding.setLifecycleOwner(this);
        init();
        LiveData<Boolean> isLoadingData = viewModel.getIsLoading();
        isLoadingData.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressDialog.show();
                Log.d("Loader", "stated new loader");
            } else {
                progressDialog.dismiss();
                Log.d("Loader", "stopped a loader");
            }
        });

        viewModel.getLoadingError().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                } 
                Toast.makeText(getContext(), "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<ArrayList<SelectionModal>> idTypes = viewModel.getIdTypeLiveData();
        idTypes.observe(getViewLifecycleOwner(), idType -> idtypeList = idType);

        idExpry = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        LiveData<DateTimeResponseDate>currentDate=viewModel.getDateTimeLiveData();
        currentDate.observe(getViewLifecycleOwner(),dateTime ->{
            String toParse = dateTime.getDate() + " " + dateTime.getTime(); // Results in "2-5-2012 20:43"
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm"); // I assume d-M, you may refer to M-d for month-day instead.
            Date date = null; // You will need try/catch around this
            try {
                date = formatter.parse(toParse);
                if (date != null) {
                    currentDateTime = date.getTime();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });

        return binding.getRoot();
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.idExpiry.setText(sdf.format(myCalendar.getTime()));
        String idExpiry = sdf.format(myCalendar.getTime());
    }

    private void init() {
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        Loader loader = new Loader(getContext());
        progressDialog = loader.progress();
        viewModel.getIdtype();
        bottomLists = new BottomLists(getContext(), this);
        binding.setClickers(this);
        videoView = binding.videoView;
        image1 = binding.front;
        image2 = binding.back;
        if (Universal.getInstance().getIdNumber() != null) {
            binding.idNumber.setText(Universal.getInstance().getIdNumber());
        }

        viewModel.getDate();
    }

    @Override
    public void onBottomSheetSelected(String name, String id, String tag) {
        if (tag.equals("ID Types")) {
            idTypeId = id;
            binding.idtype.setText(name);
        }
    }

    public void onIdExpiryClicked(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()), idExpry, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(currentDateTime + (1000 * 60 * 60 * 24));
        datePickerDialog.show();
    }

    public void onIdTypeClicked(View view) {
        bottomLists.bottomViewSelection("ID Types", idtypeList);
    }

    private boolean hasText(TextInputEditText editText) {
        String text = Objects.requireNonNull(editText.getText()).toString().trim();
        editText.setError(null);

        if (text.length() == 0) {
            editText.setError("Field cannot be empty");
            return false;
        }

        return true;
    }

    private boolean validate() {
        boolean check = true;
        if (!hasText(binding.idNumber)) check = false;
        if (!hasText(binding.idExpiry)) check = false;
        if (!hasText(binding.idtype)) check = false;
        return check;
    }

    public void onFrontImageClicked(View view) {
        final int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!hasPermissions(getContext(), PERMISSIONS)) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), PERMISSIONS, Constants.PHOTO_CAPTURE_CODE);
            } else {
                is_clicked_1 = true;
                is_clicked_2 = false;
                captureImage();
            }
        } else {
            is_clicked_1 = true;
            is_clicked_2 = false;
            captureImage();
        }
    }

    public void onBackImageClicked(View view) {
        final int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!hasPermissions(getContext(), PERMISSIONS)) {
                ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), PERMISSIONS, Constants.PHOTO_CAPTURE_CODE);
            } else {
                is_clicked_1 = false;
                is_clicked_2 = true;
                captureImage();
            }
        } else {
            is_clicked_1 = false;
            is_clicked_2 = true;
            captureImage();
        }
    }

    public void onVideoClicked(View view) {
        isVideoClicked = true;
        takeVideo();
    }

    public void onSubmitClicked(View view) {
        if (validate()) {
            Signup.userData.put("idNumber", Objects.requireNonNull(binding.idNumber.getText()).toString());
            Signup.userData.put("idExpiry", Objects.requireNonNull(binding.idExpiry.getText()).toString());
            Signup.userData.put("idType", idTypeId);

            if (!filepath_1.equals("ImageEmpty") && !filepath_2.equals("ImageEmpty")) {
                File frontImage = new File(filepath_1);
                File backImage = new File(filepath_2);
                Signup.imageData.put("front", frontImage);
                Signup.imageData.put("back", backImage);
                if (isVideoClicked) {
                    File selfieVideo = new File(video_filepath);
                    Signup.imageData.put("selfie", selfieVideo);
                    getSignature();
                }else{
                    Toast.makeText(getContext(), "selfie video is mandatory to proceed", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getContext(), "ID files are mandatory to proceed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getSignature() {
        SignaturePad signPad = new SignaturePad();
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, signPad).addToBackStack(null);
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack(null);

//        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//        ft.replace(R.id.container, signPad);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        ft.commit();
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(Constants.MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private Uri getOutputMediaFileUri(int type) {
        return FileProvider.getUriForFile(Objects.requireNonNull(getContext()), BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        String IMAGE_DIRECTORY_NAME = "Register Assets";
        File mediaStorageDir = new File(Objects.requireNonNull(getContext()).getExternalFilesDir(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("REGISTER", "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == Constants.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void SetImagePreview() {
        filepath = fileUri.getPath();
        if (filepath != null) {
            // Displaying the image or video on the screen
            PreviewMedia();

        } else {
            Toast.makeText(getContext(), "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }
    }

    private void PreviewMedia() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        Bitmap bitmap_preview = null;
        if (is_clicked_1) {
            filepath_1 = saveCompressedImage(compressedImage(filepath), "ID Images");
            bitmap_preview = BitmapFactory.decodeFile(filepath_1, options);
//            image1.setImageBitmap(bitmap_preview);
            Glide.with(this).load(bitmap_preview).into(image1);
        } else if (is_clicked_2) {
            filepath_2 = saveCompressedImage(compressedImage(filepath), "ID Images");
            bitmap_preview = BitmapFactory.decodeFile(filepath_2, options);
            Glide.with(this).load(bitmap_preview).into(image2);
//            image2.setImageBitmap(bitmap_preview);
        }

    }

    private byte[] compressedImage(String imageFile) {
        Bitmap bitmapImage = BitmapFactory.decodeFile(imageFile);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        return stream.toByteArray();
    }

    private String saveCompressedImage(byte[] image, String fileType) {
        File storageDir = null;
        switch (fileType) {
            case "Signature":
                storageDir = new File(Objects.requireNonNull(getContext()).getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Signature");
                break;
            case "ID Images":
                storageDir = new File(Objects.requireNonNull(getContext()).getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IdFiles");
                break;
        }
        return createFile(storageDir, fileType, image);

    }

    private String createFile(File folder, String fileType, byte[] image) {
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
        imageFile = new File(folder.getPath() + File.separator + fileType + timeStamp + ".jpg");

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PHOTO_CAPTURE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(getContext(), "Please give permission to proceed.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {


            if (resultCode == RESULT_OK) {
                try {
                    SetImagePreview();
                } catch (OutOfMemoryError exp) {
                    Toast.makeText(getContext(), "Out of Memory:" + exp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();

            } else {
                // failed to capture image
                Toast.makeText(getContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }


        } else if (requestCode == Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    setVideoPreview(data.getStringExtra("path"));
                } catch (OutOfMemoryError exp) {
                    Toast.makeText(getContext(), "Out of Memory", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getContext(), "User cancelled video capture", Toast.LENGTH_SHORT).show();

            } else {
                // failed to capture image
                Toast.makeText(getContext(), "Sorry! Failed to capture video", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void takeVideo() {
        Intent i = new Intent(getContext(), VideoRecording.class);
        startActivityForResult(i, Constants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    private void setVideoPreview(String videPath) {
        video_filepath = videPath;
        Uri previewUri = Uri.parse(videPath);
        videoView.setVideoURI(previewUri);
        videoView.start();
    }

    @Override
    public void onDestroyView() {
        viewModel.clear();
        super.onDestroyView();
    }

    public void onBackClicked(View view) {
        getActivity().getSupportFragmentManager().popBackStack();
//        PersonalInfo personalInfo = new PersonalInfo();
//        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//        ft.replace(R.id.container, personalInfo);
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        ft.commit();
    }

}
