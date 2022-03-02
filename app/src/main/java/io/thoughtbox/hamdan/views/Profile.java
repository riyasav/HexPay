package io.thoughtbox.hamdan.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.inject.Inject;


import io.thoughtbox.hamdan.Adapters.SelectionListener;
import io.thoughtbox.hamdan.BuildConfig;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.alerts.BottomLists;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityProfileBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.DateTimeResponseDate;
import io.thoughtbox.hamdan.model.profile.ProfileResponseData;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Constants;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.viewModel.HomeViewModel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Profile extends AppCompatActivity implements SelectionListener {
    ActivityProfileBinding binding;
    HomeViewModel homeViewModel;
    NotificationAlerts alerts;
    private Dialog progressDialog;
    private Calendar myCalendar = Calendar.getInstance();
    private Calendar passportExpCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener idExpry, passportExpiry;
    long currentDateTime = 0;
    private ArrayList<SelectionModal> proffessionsList;
    private ArrayList<SelectionModal> employerTypeList;
    private ArrayList<SelectionModal> salaryList;
    private BottomLists bottomLists;
    private String professionId = "";
    private String employerTypeId = "";
    String profileID = "";
    private final String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Inject
    Dictionary dictionary;

    private String filepath_1 = "ImageEmpty";
    private String filepath_2 = "ImageEmpty";
    private String filepath = null;
    private Uri fileUri;
    private boolean is_clicked_1 = false;
    private boolean is_clicked_2 = false;

    int sms, email, phone, whatsapp = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        binding.setLifecycleOwner(this);
        init();
        binding.back.setOnClickListener(v -> onBackPressed());

        idExpry = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        passportExpiry = (view, year, monthOfYear, dayOfMonth) -> {
            passportExpCalendar.set(Calendar.YEAR, year);
            passportExpCalendar.set(Calendar.MONTH, monthOfYear);
            passportExpCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updatePassportLabel();
        };

        LiveData<DateTimeResponseDate> currentDate = homeViewModel.getDateTimeLiveData();
        currentDate.observe(this, dateTime -> {
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

        LiveData<Boolean> isSessionValid = homeViewModel.getSessionStatus();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });

        LiveData<Boolean> isLoadingData = homeViewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });

        LiveData<String> error = homeViewModel.getLoadingError();
        error.observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.d("invoice", errorMsg);
            }
        });

        LiveData<ProfileResponseData> profileData = homeViewModel.getProfileLiveData();
        profileData.observe(this, data -> {
            binding.setInfo(data);
            setConsentData(data);
            professionId = data.getProfessionid();
            employerTypeId = data.getEmployertypeid();
            profileID = data.getId();
            if (data.getIdfiles() != null) {
                homeViewModel.getIDFile(data.getIdfiles().get(0).getId());
                homeViewModel.getIDFile(data.getIdfiles().get(1).getId());
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_add)
                        .placeholder(R.drawable.loading)
                        .into(binding.imageView40);
                Glide.with(this)
                        .load(R.drawable.ic_add)
                        .placeholder(R.drawable.loading)
                        .into(binding.imageView41);

            }

        });


        LiveData<String> idImage = homeViewModel.getIdImage();
        idImage.observe(this, data -> {
            if (data != null) {
                byte[] imageByteArray = Base64.decode(data, Base64.DEFAULT);

                if (binding.imageView40.getDrawable() == null) {
                    Glide.with(this)
                            .asBitmap()
                            .load(imageByteArray)
                            .placeholder(R.drawable.loading)
                            .into(binding.imageView40);
                } else {
                    Glide.with(this)
                            .asBitmap()
                            .load(imageByteArray)
                            .placeholder(R.drawable.loading)
                            .into(binding.imageView41);
                }
            } else {
                Glide.with(this)
                        .asBitmap()
                        .load(R.drawable.ic_add)
                        .into(binding.imageView41);

            }
        });

        LiveData<ArrayList<SelectionModal>> proffessions = homeViewModel.getProffessionLivedata();
        proffessions.observe(this, proffession -> proffessionsList = proffession);

        LiveData<ArrayList<SelectionModal>> employerType = homeViewModel.getEmployerTypeLiveData();
        employerType.observe(this, empType -> employerTypeList = empType);

        LiveData<ArrayList<SelectionModal>> salary = homeViewModel.getSalaryLiveData();
        salary.observe(this, salarylist -> salaryList = salarylist);

        LiveData<String> dpImage = homeViewModel.getDpImage();
        dpImage.observe(this, data -> {
            if (data != null) {
                byte[] imageByteArray = Base64.decode(data, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                if (bitmap!=null){
                    rotateImage(bitmap);
                }

//                    Glide.with(this)
//                            .asBitmap()
//                            .load(imageByteArray)
//                            .circleCrop()
//                            .placeholder(R.drawable.ic_avatar)
//                            .into(dashBoardBinding.displayPic);
            }
        });

    }

    private void setConsentData(ProfileResponseData data) {
        binding.switch1.setChecked(!data.getIssmsenabled().equals("0"));
        binding.switch2.setChecked(!data.getIsemailenabled().equals("0"));
        binding.switch3.setChecked(!data.getIsphoneenabled().equals("0"));
        binding.switch4.setChecked(!data.getIsphoneenabled().equals("0"));
    }

    private void init() {
        checkInternet();
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        DaggerApiComponents.create().inject(this);
        alerts = new NotificationAlerts(this);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.getProfile();
        binding.setClickers(this);
        binding.setLanguage(dictionary);
        bottomLists = new BottomLists(this, this);
        getDate();
        homeViewModel.getProffessions();
        homeViewModel.getSalaryData();
        homeViewModel.getEmployerType();
        homeViewModel.getProfilePic(Universal.getInstance().getLoginResponsedata().getId());

    }

    private void consentUpdate() {
        if (binding.switch1.isChecked()) {
            sms = 1;
        } else {
            sms = 0;
        }


        if (binding.switch2.isChecked()) {
            email = 1;
        } else {
            email = 0;
        }

        if (binding.switch3.isChecked()) {
            phone = 1;
        } else {
            phone = 0;
        }

        if (binding.switch4.isChecked()) {
            whatsapp = 1;
        } else {
            whatsapp = 0;
        }


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

    private void updatePassportLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.passportExpiry.setText(sdf.format(passportExpCalendar.getTime()));
        sdf.format(passportExpCalendar.getTime());
    }

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.idExpiry.setText(sdf.format(myCalendar.getTime()));
        String idExpiry = sdf.format(myCalendar.getTime());
    }

    private void getDate() {
        homeViewModel.getDate();
    }

    public void onIdExpiryClicked(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, idExpry, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(currentDateTime + (1000 * 60 * 60 * 24));
        datePickerDialog.show();
    }

    public void onPassportExpired(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, passportExpiry, passportExpCalendar.get(Calendar.YEAR), passportExpCalendar.get(Calendar.MONTH), passportExpCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(currentDateTime + (1000 * 60 * 60 * 24));
        datePickerDialog.show();
    }

    public void onEmployerTypeClicked(View view) {
        bottomLists.bottomViewSelection("EmployerType", employerTypeList);
    }

    public void onSalaryClicked(View view) {
        bottomLists.bottomViewSelection("Salary", salaryList);
    }

    public void onProfessionClicked(View view) {
        bottomLists.bottomViewSelection("Profession", proffessionsList);
    }

    private void checkInternet() {
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(this);
        connectionLiveData.observe(this, connection -> {
            if (!connection.getIsConnected()) {
                alerts.noInternet();
            }
            if (connection.getIsVpnConnected()) {
                Toast.makeText(this, "VPN Connected", Toast.LENGTH_SHORT).show();
                alerts.vpnAlert();
            } else {
                alerts.dismissDialog();
            }
        });
    }

    public void updateTextData(View view) {
        progressDialog.show();
        consentUpdate();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (!filepath_1.equals("ImageEmpty") && !filepath_2.equals("ImageEmpty")) {
            File frontImage = new File(filepath_1);
            File backImage = new File(filepath_2);
            builder.addFormDataPart("idfiles0", "idfiles0", RequestBody.create(MediaType.parse("multipart/form-data"), frontImage));
            builder.addFormDataPart("idfiles1", "idfiles1", RequestBody.create(MediaType.parse("multipart/form-data"), backImage));
            builder.addFormDataPart("idfilescount", "2");
        }

        builder.addFormDataPart("platform", "ANDROID");
        builder.addFormDataPart("id", profileID);

        builder.addFormDataPart("issmsenabled", String.valueOf(sms));
        builder.addFormDataPart("isemailenabled", String.valueOf(email));
        builder.addFormDataPart("isphoneenabled", String.valueOf(phone));
        builder.addFormDataPart("iswhatsappenabled", String.valueOf(whatsapp));

        builder.addFormDataPart("idexpiry", binding.idExpiry.getText().toString());
        builder.addFormDataPart("isonlineupdate", "1");
        builder.addFormDataPart("city", binding.address1.getText().toString());
        builder.addFormDataPart("state", binding.address2.getText().toString());
        builder.addFormDataPart("passportno", binding.passportnumber.getText().toString());
        builder.addFormDataPart("passportexpiry", binding.passportExpiry.getText().toString());
        builder.addFormDataPart("employertype", employerTypeId);
        builder.addFormDataPart("employer", binding.employer.getText().toString());
        builder.addFormDataPart("professiondetail", binding.profession.getText().toString());
        builder.addFormDataPart("salary", binding.salary.getText().toString());


        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = builder.build();


        Request request = new Request.Builder()
                .url(Constants.BaseURL + "usercontrol/updateuserdetails")
                .post(requestBody)
                .addHeader("Authorization", "Bearer " + Universal.getInstance().getLoginResponsedata().getToken())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                call.cancel();
            }


            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                progressDialog.dismiss();
                final String myResponse = response.body().string();

                runOnUiThread(() -> {
                    try {
                        JSONObject respons = new JSONObject(myResponse);
                        String responsDesc = respons.getString("responsedescription");
                        String responsstatus = respons.getString("responsestatus");
                        JSONObject responsData = respons.getJSONObject("responsedata");
                        String token = respons.getString("renewedtoken");
                        if (!token.equals("")) {
                            Universal.getInstance().getLoginResponsedata().setToken(token);
                            if (responsstatus.toUpperCase().equals("TRUE")) {

                                String otpRefNum = responsData.getString("id");
                                Toast.makeText(Profile.this, "Data updated", Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(Profile.this, responsDesc, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
            }

        });
    }

    @Override
    public void onBottomSheetSelected(String name, String id, String tag) {
        switch (tag) {
            case "Profession":
                professionId = id;
                binding.profession.setText(name);
                break;

            case "Salary":
                binding.salary.setText(name);
                break;

            case "EmployerType":
                employerTypeId = id;
                binding.employerType.setText(name);
                break;

        }
    }

    private void rotateImage(Bitmap bitmapOrg) {
        Matrix matrix = new Matrix();

        matrix.postRotate(-90);

//        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapOrg, 65, 65, true);

        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix, true);

        Glide.with(this)
                .asBitmap()
                .load(rotatedBitmap)
                .circleCrop()
                .placeholder(R.drawable.ic_avatar)
                .into(binding.displayPic);
    }

    public void onFrontImageClicked(View view) {
        final int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, Constants.PHOTO_CAPTURE_CODE);
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
            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, Constants.PHOTO_CAPTURE_CODE);
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

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(Constants.MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, Constants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private Uri getOutputMediaFileUri(int type) {
        return FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
    }

    private File getOutputMediaFile(int type) {

        // External sdcard location
        String IMAGE_DIRECTORY_NAME = "Register Assets";
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
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
            Toast.makeText(this, "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
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

            Glide.with(this)
                    .asBitmap()
                    .load(bitmap_preview)
                    .placeholder(R.drawable.loading)
                    .into(binding.imageView40);

        } else if (is_clicked_2) {
            filepath_2 = saveCompressedImage(compressedImage(filepath), "ID Images");
            bitmap_preview = BitmapFactory.decodeFile(filepath_2, options);

//            image2.setImageBitmap(bitmap_preview);
            Glide.with(this)
                    .asBitmap()
                    .load(bitmap_preview)
                    .placeholder(R.drawable.loading)
                    .into(binding.imageView41);
        }

    }

    private byte[] compressedImage(String imageFile) {
        Bitmap bitmapImage = BitmapFactory.decodeFile(imageFile);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (bitmapImage != null) {
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        }

        return stream.toByteArray();
    }

    private String saveCompressedImage(byte[] image, String fileType) {
        File storageDir = null;
        switch (fileType) {
            case "Signature":
                storageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Signature");
                break;
            case "ID Images":
                storageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "IdFiles");
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
                Toast.makeText(this, "Please give permission to proceed.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(this, "Out of Memory:" + exp.getMessage(), Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(this, "User cancelled image capture", Toast.LENGTH_SHORT).show();

            } else {
                // failed to capture image
                Toast.makeText(this, "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}