package io.thoughtbox.hamdan.views.remittance;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import io.thoughtbox.hamdan.Adapters.SelectionListener;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.alerts.AuthenticationView;
import io.thoughtbox.hamdan.alerts.BottomLists;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityTransferViewBinding;
import io.thoughtbox.hamdan.databinding.GetRateBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.bankBenModel.BankBeneficiaryResponseData;
import io.thoughtbox.hamdan.model.cashPickUpModel.CashPickUpResponseData;
import io.thoughtbox.hamdan.model.invoice.ConfirmPaymentResponseData;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.model.transferModel.GetRateResponseData;
import io.thoughtbox.hamdan.model.transferModel.RateRequest;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.model.transferModel.TransferRequestModel;
import io.thoughtbox.hamdan.model.transferModel.TransferResponseData;
import io.thoughtbox.hamdan.receipts.PaymentReceipt;
import io.thoughtbox.hamdan.transfers.PaymentGateWay;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.utls.OnResendOtp;
import io.thoughtbox.hamdan.viewModel.OtpViewModel;
import io.thoughtbox.hamdan.viewModel.TransferViewModel;

public class TransferView extends AppCompatActivity implements SelectionListener, OnResendOtp {
    @Inject
    Dictionary dictionary;
    boolean isRepeat = false;
    private ActivityTransferViewBinding binding;
    private TransferViewModel transferViewModel;
    private OtpViewModel otpViewModel;
    private BankBeneficiaryResponseData responseData;
    private CashPickUpResponseData cashResponseData;
    private AuthenticationView authenticationView;
    private ArrayList<SelectionModal> currencyList;
    private ArrayList<SelectionModal> modeList;
    private ArrayList<SelectionModal> sourceList;
    private ArrayList<SelectionModal> purposeList;
    private ArrayList<SelectionModal> bankList;
    private String fcCurrencyCode;
    private String lcCurrencyCode;
    private String txnType;
    private String benId;
    private String LC;
    private String transferFcAmount;
    private String selectedCurrency, modeId, sourceId, purposeId, bankId, otpRefNum;
    private boolean isBank = false;
    private String Amount = "0.00";
    private Dialog progressDialog;
    TextView termsAndCondition;
    private String modeCode, isPaymentGateWay;
    private String costrate, userRate, servicecharge, sessionid,agentsessionid;
    private NotificationAlerts alerts;
    private BottomLists bottomLists;

    private String discountRedeemed;
    private String discountType;
    private String discountApplied;
    private String vat;
    String rebate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_transfer_view);
        binding.setLifecycleOwner(this);

        init();

        LiveData<Boolean> isSessionValid = transferViewModel.getSessionStatus();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });

        LiveData<Boolean> isLoadingData = transferViewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });

        transferViewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<ArrayList<SelectionModal>> modeLiveData = transferViewModel.getModesLiveData();
        modeLiveData.observe(this, modeData -> modeList = modeData);

        LiveData<ArrayList<SelectionModal>> purposeLiveData = transferViewModel.getPurposeLiveData();
        purposeLiveData.observe(this, purposeData -> {
            purposeList = purposeData;
            if (!isRepeat) {
                for (int i = 0; i < purposeList.size(); i++) {
                    String name = purposeList.get(i).getName();
                    if (name.toUpperCase().contains("FAMILY")) {
                        binding.purpose.setText(purposeList.get(i).getName());
                        purposeId = purposeList.get(i).getId();
                    }
                }
            }
        });


        LiveData<ArrayList<SelectionModal>> sourceLiveData = transferViewModel.getSourceLiveData();
        sourceLiveData.observe(this, sourceData -> {
            sourceList = sourceData;
            if (!isRepeat) {
                for (int i = 0; i < sourceList.size(); i++) {
                    String name = sourceList.get(i).getName();
                    if (name.toUpperCase().contains("SALARY")){
                        binding.source.setText(sourceList.get(i).getName());
                        sourceId = sourceList.get(i).getId();
                    }
                }
            }

        });

        LiveData<ArrayList<SelectionModal>> bankLiveData = transferViewModel.getBankLiveData();
        bankLiveData.observe(this, bankData -> bankList = bankData);

        LiveData<GetRateResponseData> rateLiveData = transferViewModel.getRateResponseLiveData();
        rateLiveData.observe(this, this::calculateRate);

        LiveData<TransferResponseData> transferResponseLiveData = transferViewModel.getTransferResponseLiveData();
        transferResponseLiveData.observe(this, this::showAuthenticationView);

        LiveData<String> otpLiveData = authenticationView.getOtpLiveData();
        otpLiveData.observe(this, otp -> {
            OtpRequestModel params = new OtpRequestModel("ANDROID", txnType, otp, otpRefNum);
            otpViewModel.validateOtp(params);
        });

        LiveData<Boolean> isOtpLoadingData = otpViewModel.getIsLoading();
        isOtpLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });

        otpViewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();

            }
        });

        LiveData<Boolean> isOtpValidated = otpViewModel.getOtpStatus();
        isOtpValidated.observe(this, isValid -> {
            if (isValid) {
                authenticationView.dismissView();
                navigateToPaymentWindow();
            } else {
                authenticationView.clearFields();
                Toast.makeText(this, "Invalid Pin Supplied", Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<Boolean> resendOtp = otpViewModel.getResendOtpLiveData();
        resendOtp.observe(this, isOtpResend -> {
            if (isOtpResend) {
                Toast.makeText(this, "Otp Send", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Otp resend failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void init() {
        checkInternet();
        alerts = new NotificationAlerts(this);
        DaggerApiComponents.create().inject(this);
        Loader loader = new Loader(this);
        bottomLists = new BottomLists(this, this);
        progressDialog = loader.progress();
        authenticationView = new AuthenticationView(this, this);
        Intent intent = getIntent();
        isRepeat = intent.getBooleanExtra("fromReport", false);
        isBank = intent.getBooleanExtra("isBank", false);
        termsAndCondition=binding.termsAndCondition;
        if (intent.hasExtra("Bank")) {
            txnType = "Aim2Bank";
            responseData = (BankBeneficiaryResponseData) intent.getSerializableExtra("Bank");
            benId = responseData != null ? responseData.getId() : null;
            binding.txntypeLbl.setText("Bank Transfer");
            binding.nameLbl.setText(responseData.getName());
            binding.accountNumberLbl.setText(responseData.getAccountno());
            binding.bankLbl.setText(responseData.getBank());
            binding.branchLbl.setText(responseData.getBranch());
            binding.branchLbl.setSelected(true);
        } else {
            txnType = "Aim2FastCash";
            cashResponseData = (CashPickUpResponseData) intent.getSerializableExtra("CashPickUp");
            benId = cashResponseData != null ? cashResponseData.getId() : null;
            if (cashResponseData != null) {
                binding.txntypeLbl.setText("FastCash Transfer");
                binding.nameLbl.setText(cashResponseData.getName());
                binding.accountNumberLbl.setText(cashResponseData.getAddress());
                binding.bankLbl.setText(cashResponseData.getAgent());
                binding.branchLbl.setText(cashResponseData.getAgentbranch());
                binding.address.setText("Address");
                binding.agent.setText("Agent");
                binding.branchLbl.setSelected(true);
            }

        }

        if (isRepeat) {
            //set fields from report
            ConfirmPaymentResponseData reportData = (ConfirmPaymentResponseData) intent.getSerializableExtra("reportDetails");
            if (reportData != null) {
                binding.amount.setText(reportData.getFcamount());
                binding.currency.setText(reportData.getBencurrencycode());
                binding.source.setText(reportData.getSource());
                binding.purpose.setText(reportData.getPurpose());
//                setMode(reportData.getPayementmode(), reportData.getPayementmodeid());
//                if (reportData.getAccounttransferbank() != null && !Objects.equals(reportData.getAccounttransferbank(), "")) {
//                    binding.bank.setText(reportData.getAccounttransferbank());
//                    binding.refNum.setText(reportData.getAccounttransferrefno());
//                    bankId = reportData.getAccounttransferbankid();
//                }

                sourceId = reportData.getSourceid();
                purposeId = reportData.getPurposeid();
                selectedCurrency = reportData.getBencurrency();

            }

        }
        binding.setClickers(this);
        binding.setLanguage(dictionary);
        transferViewModel = new ViewModelProvider(this).get(TransferViewModel.class);
        otpViewModel = new ViewModelProvider(this).get(OtpViewModel.class);

        getCurrencyList();
        transferViewModel.getModes();
        transferViewModel.getBanks();
        transferViewModel.getPurposes();
        transferViewModel.getSources();

         SpannableStringBuilder sb = new SpannableStringBuilder("By clicking submit you agree to the Terms and Conditions");
         StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD); // Span to make text bold
        sb.setSpan(bss, 35, 56, Spannable.SPAN_INCLUSIVE_INCLUSIVE); // make first 4 characters Bold
        termsAndCondition.setText(sb);
    }

    private void navigateToPaymentWindow() {
        if (isPaymentGateWay != null && isPaymentGateWay.equals("1")) {
            Intent intent = new Intent(getApplicationContext(), PaymentGateWay.class);
            intent.putExtra("refnum", otpRefNum);
            intent.putExtra("transfer_type", txnType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), PaymentReceipt.class);
            intent.putExtra("refnum", otpRefNum);
            intent.putExtra("transfer_type", txnType);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void showAuthenticationView(TransferResponseData transferResponseData) {
        otpRefNum = transferResponseData.getId();
        if (transferResponseData.getResult().toUpperCase().equals("TRUE")) {
            String pinMode = transferResponseData.getPinmode();
            if (pinMode.toUpperCase().equals("BIOMETRIC")) {
                authenticationView.fingerLayout();
            } else {
                authenticationView.OtpLayout().show();
            }
        }

    }

    private void calculateRate(GetRateResponseData responseData) {
        costrate = responseData.getCostrate();
        servicecharge = responseData.getServicecharge();
        userRate = responseData.getUserrate();
        sessionid = responseData.getSessionid();
        agentsessionid=responseData.getAgentsessionid();
        String rate = userRate;
        rebate=responseData.getRebate();
        String charge = servicecharge;
        String total="0.00";
        vat = responseData.getVatamount();
        if (selectedCurrency.equals(LC)) {
            if (!rate.equals("") && !rate.equals("0")){
                total = calculateFcTotal(rate, charge, Amount, vat,rebate);
                transferFcAmount = total;
            }else{
                Toast.makeText(this, "Transfer rate unavailable.Try later", Toast.LENGTH_SHORT).show();
            }

        } else {
            if (!rate.equals("") && !rate.equals("0")) {
                total = calculateLcTotal(rate, charge, Amount, vat,rebate);
                transferFcAmount = Amount;
            }else{
                Toast.makeText(this, "Transfer rate unavailable.Try later", Toast.LENGTH_SHORT).show();
            }
        }

        String discount = responseData.getDiscountredeemed();
        String offerMsg = responseData.getDiscountcodemessage();
        String discountStatus = responseData.getDiscountcodestatus();

        discountRedeemed=responseData.getDiscountredeemed();
        discountApplied=responseData.getDiscountcodeapplied() ;
        discountType=responseData.getDiscountcodetype();

        if (discountStatus.toUpperCase().equals("TRUE")) {
//          showAnimation(offerMsg);
            showRateSheet(charge, rate, total, discount, offerMsg, vat);
        }else{
            showRateSheet(charge, rate, total, "", "", vat);
        }



    }

    private void showRateSheet(String charge, String rate, String total, String offer, String msg, String vat) {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        GetRateBinding getRateBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.get_rate, null, false);
        getRateBinding.setClickers(TransferView.this);
        getRateBinding.setLanguage(dictionary);
        mBottomSheetDialog.setContentView(getRateBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.show();
        if (!offer.equals("")) {
            getRateBinding.offerLyt.setVisibility(View.VISIBLE);
            getRateBinding.ofrMsg.setText(msg);
            getRateBinding.discountAmount.setText("Total discount applied : " + offer);
        } else {
            getRateBinding.offerLyt.setVisibility(View.GONE);
        }
        getRateBinding.charge.setText(MessageFormat.format("{0} {1}", charge, Universal.getInstance().getLoginResponsedata().getCurrencycode()));
        getRateBinding.rate.setText(rate);
        getRateBinding.rebate.setText(MessageFormat.format("{0} {1}", rebate, Universal.getInstance().getLoginResponsedata().getCurrencycode()));
        getRateBinding.vat.setText(MessageFormat.format("{0} {1}", vat, Universal.getInstance().getLoginResponsedata().getCurrencycode()));
        if (selectedCurrency.equals(Universal.getInstance().getLoginResponsedata().getCurrency())) {
            getRateBinding.amount.setText(MessageFormat.format("{0} {1}", Amount, lcCurrencyCode));
            getRateBinding.total.setText(MessageFormat.format("{0} {1}", total, fcCurrencyCode));
        } else {
            getRateBinding.amount.setText(MessageFormat.format("{0} {1}", Amount, fcCurrencyCode));
            getRateBinding.total.setText(MessageFormat.format("{0} {1}", total, lcCurrencyCode));
        }
    }


//
//    private void showRateSheet(String charge, String rate, String total, String offer, String msg) {
//        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
//        GetRateBinding getRateBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.get_rate, null, false);
//        getRateBinding.setClickers(TransferView.this);
//        getRateBinding.setLanguage(dictionary);
//        mBottomSheetDialog.setContentView(getRateBinding.getRoot());
//        mBottomSheetDialog.setCancelable(true);
//        mBottomSheetDialog.show();
//        if (!offer.equals("")) {
//            getRateBinding.offerLyt.setVisibility(View.VISIBLE);
//            getRateBinding.ofrMsg.setText(msg);
//            getRateBinding.discountAmount.setText("Total discount applied : " + offer);
//        }else{
//            getRateBinding.offerLyt.setVisibility(View.GONE);
//        }
//        getRateBinding.charge.setText(MessageFormat.format("{0} {1}", charge, Universal.getInstance().getLoginResponsedata().getCurrencycode()));
//        getRateBinding.rate.setText(rate);
//        if (selectedCurrency.equals(Universal.getInstance().getLoginResponsedata().getCurrency())) {
//            getRateBinding.amount.setText(MessageFormat.format("{0} {1}", Amount, lcCurrencyCode));
//            getRateBinding.total.setText(MessageFormat.format("{0} {1}", total, fcCurrencyCode));
//        } else {
//            getRateBinding.amount.setText(MessageFormat.format("{0} {1}", Amount, fcCurrencyCode));
//            getRateBinding.total.setText(MessageFormat.format("{0} {1}", total, lcCurrencyCode));
//        }
//    }

    private String calculateLcTotal(String rate, String charge, String amount, String vat,String rebateValue) {
        double rate_ = Double.parseDouble(rate);
        double charge_ = Double.parseDouble(charge);
        double amount_ = Double.parseDouble(amount);
        double vat_ = Double.parseDouble(vat);
        double rebate_ = Double.parseDouble(rebateValue);
        double lcAmount = ((amount_ * rate_) + charge_ + vat_- rebate_);
        return new DecimalFormat("##.###").format(lcAmount);
    }

    private String calculateFcTotal(String rate, String charge, String amount, String vat,String rebateValue) {
        double rate_ = Double.parseDouble(rate);
        double charge_ = Double.parseDouble(charge);
        double amount_ = Double.parseDouble(amount);
        double vat_ = Double.parseDouble(vat);
        double rebate_ = Double.parseDouble(rebateValue);
        double charges = charge_ + vat_;
        double fcAmount = ((amount_ - charges + rebate_) / rate_);
        return new DecimalFormat("##.###").format(fcAmount);
    }

//    private String calculateLcTotal(String rate, String charge, String amount) {
//        double rate_ = Double.parseDouble(rate);
//        double charge_ = Double.parseDouble(charge);
//        double amount_ = Double.parseDouble(amount);
//        double lcAmount = ((amount_ * rate_) + charge_);
//        return new DecimalFormat("##.###").format(lcAmount);
//    }
//
//    private String calculateFcTotal(String rate, String charge, String amount) {
//        double rate_ = Double.parseDouble(rate);
//        double charge_ = Double.parseDouble(charge);
//        double amount_ = Double.parseDouble(amount);
//        double fcAmount = ((amount_ - charge_) / rate_);
//        return new DecimalFormat("##.###").format(fcAmount);
//    }

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
                alerts.dismissVpnDialog();
            }
        });
    }

    private void getCurrencyList() {
        String FC;
        if (isBank) {
            FC = responseData.getCurrency();
            fcCurrencyCode = responseData.getCurrencycode();
        } else {
            FC = cashResponseData.getCurrency();
            fcCurrencyCode = cashResponseData.getCurrencycode();
        }

        LC = Universal.getInstance().getLoginResponsedata().getCurrency();
        lcCurrencyCode = Universal.getInstance().getLoginResponsedata().getCurrencycode();

        if (Objects.equals(FC.toUpperCase(), LC.toUpperCase())) {
            currencyList = new ArrayList<>();
            SelectionModal modal = new SelectionModal("0", LC);
            currencyList.add(modal);
        } else {
            currencyList = new ArrayList<>();
            SelectionModal modal1 = new SelectionModal("0", LC);
            SelectionModal modal2 = new SelectionModal("1", FC);
            currencyList.add(modal1);
            currencyList.add(modal2);
        }
    }

    @Override
    public void onBottomSheetSelected(String name, String id, String tag) {
        switch (tag) {
            case "Currency":
                setCurrency(name, id);
                break;
            case "Mode":
                setMode(name, id);
                break;
            case "Bank":
                setBank(name, id);
                break;
            case "Purpose":
                setPurpose(name, id);
                break;
            case "Source":
                setSource(name, id);
                break;
        }
    }

    private void setSource(String name, String id) {
        binding.source.setText(name);
        sourceId = id;
    }

    private void setPurpose(String name, String id) {
        binding.purpose.setText(name);
        purposeId = id;
    }

    private void setBank(String name, String id) {
        binding.bank.setText(name);
        bankId = id;
    }

    private void setMode(String name, String id) {
        binding.mode.setText(name);
        if (id != null) {
            String[] idSeparated = id.split("\\|");
            if (idSeparated.length > 1) {
                modeId = idSeparated[0];
                modeCode = idSeparated[1];
                isPaymentGateWay = idSeparated[2];

                if (modeCode.equals("AT")) {
                    binding.bankGroup.setVisibility(View.VISIBLE);
                } else {
                    binding.bankGroup.setVisibility(View.GONE);
                }
            }

        }

    }

    private void setCurrency(String name, String id) {
        if (id.equals("0")) {
            binding.currency.setText(lcCurrencyCode);
        } else {
            binding.currency.setText(fcCurrencyCode);
        }
        selectedCurrency = name;
    }

    @Override
    public void onResendOtpClicked() {
        OtpRequestModel params = new OtpRequestModel(txnType, "", otpRefNum);
        otpViewModel.resendTransactionOtp(params);
    }

    public void onBackClicked(View view) {
        onBackPressed();
    }

    public void onCurrencyClicked(View view) {
        bottomLists.bottomViewSelection("Currency", currencyList);
    }

    public void onModeClicked(View view) {
        bottomLists.bottomViewSelection("Mode", modeList);
    }

    public void onBankClicked(View view) {
        bottomLists.bottomViewSelection("Bank", bankList);
    }

    public void onSourceClicked(View view) {
        bottomLists.bottomViewSelection("Source", sourceList);
    }

    public void onPurposeClicked(View view) {
        bottomLists.bottomViewSelection("Purpose", purposeList);
    }

    public void onSubmitClicked(View view) {
        if (validate()) {
            Amount = Objects.requireNonNull(binding.amount.getText()).toString();
            getRate();
        }
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
        if (!hasText(binding.amount)) check = false;
        if (!hasText(binding.currency)) check = false;
        if (!hasText(binding.mode)) check = false;
        if (!hasText(binding.source)) check = false;
        if (!hasText(binding.purpose)) check = false;
        if (modeCode != null && modeCode.equals("AT")) {
            if (!hasText(binding.bank)) check = false;
            if (!hasText(binding.refNum)) check = false;
        }
        return check;
    }

    private void getRate() {
        String lcAmount;
        String fcAmount;
        if (selectedCurrency.equals(LC)) {
            lcAmount = Amount;
            fcAmount = "0";
        } else {
            lcAmount = "0";
            fcAmount = Amount;
        }
        String offer = Objects.requireNonNull(binding.offercode.getText()).toString().trim();
        RateRequest params = new RateRequest(txnType, benId, fcAmount, lcAmount, "1", offer);
        transferViewModel.getRate(params);
    }

    public void onRateConfirmed(View view) {
        if (binding.mode.getText().toString().toUpperCase().contains("ACCOUNT")){
            Toast.makeText(this, "Successfully saved your transaction. You can always check the status from the Track menu.", Toast.LENGTH_LONG).show();
        }
        //create transfer
        TransferRequestModel params = new TransferRequestModel("ANDROID",
                txnType,
                benId,
                transferFcAmount,
                purposeId,
                sourceId,
                modeId,
                bankId,
                Objects.requireNonNull(binding.refNum.getText()).toString(),
                costrate,
                userRate,
                servicecharge,
                sessionid,
                discountType,
                discountApplied,
                discountRedeemed,
                vat,
                rebate,
                agentsessionid
        );

        if (txnType.equals("Aim2Bank")) {
            transferViewModel.createTransfer(params);
        } else if (txnType.equals("Aim2FastCash")) {
            transferViewModel.createFastCashTransfer(params);
        }

    }
}
