package io.thoughtbox.hamdan.receipts;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.print.PrintHelper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Arrays;

import io.thoughtbox.hamdan.DashBoard;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.utls.ShareView;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityPaymentReceiptBinding;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.model.invoice.ConfirmPaymentResponseData;
import io.thoughtbox.hamdan.model.invoice.InvoiceRequestModel;
import io.thoughtbox.hamdan.viewModel.InvoiceViewModel;

public class PaymentReceipt extends AppCompatActivity {
    String transferType, refNum;
    InvoiceViewModel invoiceViewModel;
    ActivityPaymentReceiptBinding binding;
    Loader loader;
    Dialog progressDialog;
    ShareView shareData;
    String sourcePage = null;
    private NotificationAlerts alerts;

    //    ConfirmPaymentResponseData confirmPaymentResponseData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_receipt);
        binding.setLifecycleOwner(this);
        init();

        LiveData<Boolean> isSessionValid = invoiceViewModel.getSessionStatus();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });

        LiveData<Boolean> isLoadingData = invoiceViewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });

        LiveData<String> error = invoiceViewModel.getLoadingError();
        error.observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.d("invoice", errorMsg);
            }
        });

        LiveData<ConfirmPaymentResponseData> getInvoice = invoiceViewModel.getInvoiceData();
        getInvoice.observe(this, confirmPaymentResponseData -> {
            binding.setInfo(confirmPaymentResponseData);
            try {
                if (confirmPaymentResponseData.getRefno() != null) {
                    binding.barcode.setImageBitmap(createBarcodeBitmap(confirmPaymentResponseData.getRefno(), 80));
                }
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (sourcePage != null) {
                if (sourcePage.equals("Report")) {
                    binding.status.setText(confirmPaymentResponseData.getTransactionstatus());
                } else {
                    if (confirmPaymentResponseData.getIsconfirmed().equals("0")) {
                        binding.status.setText("FAILED");
                    } else {
                        binding.status.setText("SUCCESS");
                    }
                }
            }


        });

        binding.closebtn.setOnClickListener(view -> {
            Intent intent = new Intent(PaymentReceipt.this, DashBoard.class);
            startActivity(intent);
        });

        binding.print.setOnClickListener(view -> doPhotoPrint());

        binding.share.setOnClickListener(view -> {
            if (isStoragePermissionGranted()) {
                shareData.share(binding.view, "Payment Receipt", "Share Receipt");
            } else {
                Toast.makeText(this, "Enable permission to share", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        loader = new Loader(this);
        progressDialog = loader.progress();
        shareData = new ShareView(this);
        alerts = new NotificationAlerts(this);
        Intent intent = getIntent();
        transferType = intent.getStringExtra("transfer_type");
        refNum = intent.getStringExtra("refnum");
        sourcePage = intent.getStringExtra("sourcePage");
        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);
        invoiceViewModel.getInvoice(new InvoiceRequestModel(transferType, refNum));
        binding.fromName.setText(Universal.getInstance().getLoginResponsedata().getUsername());

    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            } else {
                return true;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                shareData.share(binding.card, "Payment Receipt", "Share Receipt");
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("TAG", "Storage permission denied");
            }
        }
    }


    private Bitmap createBarcodeBitmap(String data, int height) throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        String finalData = Uri.encode(data);

        // Use 1 as the height of the matrix as this is a 1D Barcode.
        if (!finalData.equals("")) {
            BitMatrix bm = writer.encode(finalData, BarcodeFormat.CODE_39, 300, 1);
            int bmWidth = bm.getWidth();

            Bitmap imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < bmWidth; i++) {
                // Paint columns of width 1
                int[] column = new int[height];
                Arrays.fill(column, bm.get(i, 0) ? Color.BLACK : Color.WHITE);
                imageBitmap.setPixels(column, 0, 1, i, 0, 1, height);
            }
            return imageBitmap;
        }


        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        invoiceViewModel.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DashBoard.class);
        startActivity(intent);
    }

    private void doPhotoPrint() {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
//        qr.invalidate();
//        BitmapDrawable drawable = (BitmapDrawable) qr.getDrawable();
//        Bitmap bitmap = Bitmap.createBitmap(frame.getDrawingCache());

        binding.view.setDrawingCacheEnabled(true);
        binding.view.buildDrawingCache();
        Bitmap bitmap = binding.view.getDrawingCache();
        photoPrinter.printBitmap("Payment Receipt", bitmap);
    }
}
