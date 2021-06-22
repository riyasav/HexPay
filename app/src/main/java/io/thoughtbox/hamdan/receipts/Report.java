package io.thoughtbox.hamdan.receipts;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.thoughtbox.hamdan.Adapters.ReportAdapter;
import io.thoughtbox.hamdan.Adapters.ReportListener;
import io.thoughtbox.hamdan.DashBoard;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.model.bankBenModel.BankBeneficiaryResponseData;
import io.thoughtbox.hamdan.model.cashPickUpModel.CashPickUpResponseData;
import io.thoughtbox.hamdan.model.invoice.ConfirmPaymentResponseData;
import io.thoughtbox.hamdan.model.invoice.InvoiceRequestModel;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityReportBinding;
import io.thoughtbox.hamdan.databinding.TrackViewBinding;
import io.thoughtbox.hamdan.model.reportModel.ReportResponseData;
import io.thoughtbox.hamdan.viewModel.InvoiceViewModel;
import io.thoughtbox.hamdan.viewModel.ReportViewModel;
import io.thoughtbox.hamdan.views.remittance.TransferView;

public class Report extends AppCompatActivity implements ReportListener {
    ActivityReportBinding binding;
    ReportViewModel viewModel;
    Calendar myCalendar, myCalendar2;
    TextView day1, day2, month1, month2, year1, year2;
    String start_date, end_date = null;
    DatePickerDialog.OnDateSetListener date1, date2;
    RecyclerView recyclerView;
    ReportAdapter adapter;
    Loader loader;
    Dialog progressDialog;
    TrackViewBinding trackBinding;
    private NotificationAlerts alerts;
    InvoiceViewModel invoiceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        binding.setLifecycleOwner(this);
        init();

        LiveData<Boolean> isSessionValid = viewModel.getSessionStatus();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });


        date1 = (view, year, monthOfYear, dayOfMonth) -> {
            try {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
                if (!day2.getText().toString().equals("End")) {
                    validateDate();
                }
            } catch (Exception e) {
                Log.d("Exception:", e.toString());
            }

        };
        date2 = (view, year, monthOfYear, dayOfMonth) -> {
            try {
                myCalendar2.set(Calendar.YEAR, year);
                myCalendar2.set(Calendar.MONTH, monthOfYear);
                myCalendar2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel2();
                if (!day1.getText().toString().equals("Start")) {
                    validateDate();
                }
            } catch (Exception e) {
                Log.d("Exception:", e.toString());
            }
        };

        LiveData<Boolean> isLoadingData = viewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });
        LiveData<String> error = viewModel.getLoadingError();
        error.observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.d("invoice", errorMsg);
            }
        });

        LiveData<ArrayList<ReportResponseData>> reports = viewModel.getReportLiveData();
        reports.observe(this, reportResponseData -> {
            if (reportResponseData!=null && reportResponseData.size()>0){
                adapter = new ReportAdapter(this, reportResponseData);
                recyclerView.setAdapter(adapter);
            }else{
                recyclerView.setAdapter(null);
                Toast.makeText(this, "No data available", Toast.LENGTH_SHORT).show();

            }

        });

        LiveData<String[]> status = viewModel.getStatusLiveData();
        status.observe(this, strings -> {
            trackBinding.group.setVisibility(View.VISIBLE);
            if (strings[0].toUpperCase().equals("SUCCESS")) {
                trackBinding.status.setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                trackBinding.status.setBackgroundColor(getResources().getColor(R.color.red));
            }
            trackBinding.status.setText(strings[0]);
            trackBinding.corStatus.setText(strings[1]);
        });

        LiveData<Boolean> isInvoiceSessionValid = invoiceViewModel.getSessionStatus();
        isInvoiceSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });

        LiveData<Boolean> isInvoiceLoadingData = invoiceViewModel.getIsLoading();
        isInvoiceLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });

        LiveData<String> invoiceerror = invoiceViewModel.getLoadingError();
        invoiceerror.observe(this, errorMsg -> {
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
            Intent intent = new Intent(this, TransferView.class);
            String transferType = confirmPaymentResponseData.getTxntype();
            if (transferType.equals("Bank Transfer")) {
                intent.putExtra("Bank", setBankBenData(confirmPaymentResponseData));
                intent.putExtra("isBank", true);
            } else {
                intent.putExtra("CashPickUp", setFastCashBenData(confirmPaymentResponseData));
                intent.putExtra("isBank", false);
            }
            intent.putExtra("fromReport", true);
            intent.putExtra("reportDetails", confirmPaymentResponseData);
            startActivity(intent);
        });
    }

    private CashPickUpResponseData setFastCashBenData(ConfirmPaymentResponseData confirmPaymentResponseData) {
        CashPickUpResponseData cashPickUpResponseData = new CashPickUpResponseData();
        cashPickUpResponseData.setAddress(confirmPaymentResponseData.getBenaddress());
        cashPickUpResponseData.setContact(confirmPaymentResponseData.getContact());
        cashPickUpResponseData.setAgent(confirmPaymentResponseData.getBenagent());
        cashPickUpResponseData.setAgentbranch(confirmPaymentResponseData.getBenbranch());
        cashPickUpResponseData.setCountry(confirmPaymentResponseData.getBencountry());
        cashPickUpResponseData.setName(confirmPaymentResponseData.getBenname());
        cashPickUpResponseData.setCurrency(confirmPaymentResponseData.getBencurrency());
        cashPickUpResponseData.setCurrencycode(confirmPaymentResponseData.getBencurrencycode());
        cashPickUpResponseData.setId(confirmPaymentResponseData.getBenid());
        return cashPickUpResponseData;
    }

    private BankBeneficiaryResponseData setBankBenData(ConfirmPaymentResponseData confirmPaymentResponseData) {
        BankBeneficiaryResponseData bankBeneficiaryResponseData = new BankBeneficiaryResponseData();
        bankBeneficiaryResponseData.setAccountno(confirmPaymentResponseData.getBenaccountno());
        bankBeneficiaryResponseData.setContact(confirmPaymentResponseData.getContact());
        bankBeneficiaryResponseData.setBank(confirmPaymentResponseData.getBenagent());
        bankBeneficiaryResponseData.setBranch(confirmPaymentResponseData.getBenbranch());
        bankBeneficiaryResponseData.setCountry(confirmPaymentResponseData.getBencountry());
        bankBeneficiaryResponseData.setName(confirmPaymentResponseData.getBenname());
        bankBeneficiaryResponseData.setCurrency(confirmPaymentResponseData.getBencurrency());
        bankBeneficiaryResponseData.setCurrencycode(confirmPaymentResponseData.getBencurrencycode());
        bankBeneficiaryResponseData.setId(confirmPaymentResponseData.getBenid());
        return bankBeneficiaryResponseData;
    }

    private void init() {
        viewModel = new ViewModelProvider(this).get(ReportViewModel.class);
        invoiceViewModel = new ViewModelProvider(this).get(InvoiceViewModel.class);
        UiInteractions interactions = new UiInteractions(this);
        alerts = new NotificationAlerts(this);
        binding.setClickers(interactions);
        myCalendar = Calendar.getInstance();
        myCalendar2 = Calendar.getInstance();
        loader = new Loader(this);
        progressDialog = loader.progress();

        day1 = binding.day1;
        day2 = binding.day2;
        month1 = binding.month1;
        month2 = binding.month2;
        year1 = binding.year1;
        year2 = binding.year2;
        preSetDates();
        recyclerView = binding.reports;
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();
    }

    @Override
    public void onItemClick(ReportResponseData responseData) {
        Intent intent = new Intent(this, PaymentReceipt.class);
        intent.putExtra("transfer_type", responseData.getTxntype());
        intent.putExtra("refnum", responseData.getId());
        intent.putExtra("sourcePage", "Report");
        startActivity(intent);
    }

    @Override
    public void onRepeatClick(ReportResponseData responseData) {
        getTransferDetails(responseData.getTxntype(), responseData.getId());
    }

    private void getTransferDetails(String transferType, String refNum) {

        invoiceViewModel.getInvoice(new InvoiceRequestModel(transferType, refNum));
    }

    private void preSetDates() {

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date pastdate = calendar.getTime();

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        start_date = sdf.format(calendar.getTime());
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MMM-dd");
        String Month = new SimpleDateFormat("MMM").format(pastdate);
        String Date = new SimpleDateFormat("dd").format(pastdate);
        String Year = new SimpleDateFormat("yyyy").format(pastdate);

//        String dateOutput = format.format(pastdate);

        day1.setText(Date);
        month1.setText(Month);
        year1.setText(Year);


        Calendar calendar2 = Calendar.getInstance();
        Date today = calendar2.getTime();
//        String date = new SimpleDateFormat("yyyy-MMM-dd").format(today);

        end_date = sdf.format(calendar2.getTime());

        String Month2 = new SimpleDateFormat("MMM").format(today);
        String Date2 = new SimpleDateFormat("dd").format(today);
        String Year2 = new SimpleDateFormat("yyyy").format(today);
        day2.setText(Date2);
        month2.setText(Month2);
        year2.setText(Year2);
        validateDate();
    }

//    private void showTrackView() {
//        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
//        trackBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.track_view, null, false);
//        mBottomSheetDialog.setContentView(trackBinding.getRoot());
//        mBottomSheetDialog.setCancelable(true);
//        mBottomSheetDialog.show();
//
//        LottieAnimationView animationview = trackBinding.animationview;
//        animationview.useHardwareAcceleration(true);
//        animationview.enableMergePathsForKitKatAndAbove(true);
//        animationview.playAnimation();
//
//        trackBinding.toggler.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                try {
//                    if (seekBar.getProgress() >= 50) {
//                        seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//                        seekBar.setProgress(100);
//                        selectedOption = 1;
//                    } else if (seekBar.getProgress() < 50) {
//                        seekBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//                        seekBar.setProgress(0);
//                        selectedOption = 0;
//                    }
//                } catch (Exception e) {
//                    Log.d("TrackException", e.getMessage());
//                }
//            }
//        });
//        trackBinding.submit.setOnClickListener(view -> {
//            if (!Objects.requireNonNull(trackBinding.refno.getText()).toString().isEmpty()) {
//                String refno = trackBinding.refno.getText().toString();
//                if (selectedOption == 0) {
//                    viewModel.trackBank(refno);
//                } else {
//                    viewModel.trackCash(refno);
//                }
//            } else {
//                Toast.makeText(this, "Enter a reference number to track", Toast.LENGTH_SHORT).show();
//            }
//
//        });
//    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        start_date = sdf.format(myCalendar.getTime());

        Date date = myCalendar.getTime();
        String Month = new SimpleDateFormat("MMM").format(date);
        String Date = new SimpleDateFormat("dd").format(date);
        String Year = new SimpleDateFormat("yyyy").format(date);

        day1.setText(Date);
        month1.setText(Month);
        year1.setText(Year);


    }

    private void updateLabel2() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        end_date = sdf.format(myCalendar2.getTime());

        Date date = myCalendar2.getTime();
        String Month = new SimpleDateFormat("MMM").format(date);
        String Date = new SimpleDateFormat("dd").format(date);
        String Year = new SimpleDateFormat("yyyy").format(date);

        day2.setText(Date);
        month2.setText(Month);
        year2.setText(Year);


    }

    private void validateDate() {
//        if (start_date.equals("start date") && end_date.equals("end date")) {
//            Toast.makeText(this, "please select dates", Toast.LENGTH_SHORT).show();
//        } else
            if (start_date.equals("start date") && !end_date.equals("end date")) {
            Toast.makeText(this, "please select start date", Toast.LENGTH_SHORT).show();
        } else if (!start_date.equals("start date") && end_date.equals("end date")) {
            Toast.makeText(this, "please select end date", Toast.LENGTH_SHORT).show();
        } else if (!dateValidation(start_date, end_date)) {
            Toast.makeText(this, "Please select correct dates", Toast.LENGTH_SHORT).show();
        } else {

            uploadDates(start_date, end_date);
        }
    }

    private boolean dateValidation(String Stringdate1, String Stringdate2) {
        boolean datecomparison = false;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date_a = formatter.parse(Stringdate1);
            Date date_b = formatter.parse(Stringdate2);

            datecomparison = date_a.compareTo(date_b) <= 0;

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return datecomparison;
    }

    private void uploadDates(String datea, String dateb) {
        JSONObject params = new JSONObject();
        try {
            params.put("fromdate", datea);
            params.put("todate", dateb);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewModel.getReport(params);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Report.this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }

    public class UiInteractions {
        Context context;

        public UiInteractions(Context context) {
            this.context = context;
        }

        public void onStartDateClicked(View view) {
            new DatePickerDialog(Report.this, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

            Date date = myCalendar.getTime();
            String Month = new SimpleDateFormat("MMM").format(date);
            String Date = new SimpleDateFormat("dd").format(date);
            String Year = new SimpleDateFormat("yyyy").format(date);

            day1.setText(Date);
            month1.setText(Month);
            year1.setText(Year);
        }

        public void onEndtDateClicked(View view) {
            new DatePickerDialog(Report.this, date2, myCalendar2.get(Calendar.YEAR), myCalendar2.get(Calendar.MONTH), myCalendar2.get(Calendar.DAY_OF_MONTH)).show();
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//                end.setText(sdf.format(myCalendar2.getTime()));
            Date date = myCalendar.getTime();
            String Month = new SimpleDateFormat("MMM").format(date);
            String Date = new SimpleDateFormat("dd").format(date);
            String Year = new SimpleDateFormat("yyyy").format(date);

            day2.setText(Date);
            month2.setText(Month);
            year2.setText(Year);
        }

        public void onCloseClicked(View view) {
            Intent intent = new Intent(Report.this, DashBoard.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

//        public void onTrackClicked(View view) {
//            showTrackView();
//        }
    }

}