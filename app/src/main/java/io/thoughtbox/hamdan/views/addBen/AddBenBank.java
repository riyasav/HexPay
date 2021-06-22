package io.thoughtbox.hamdan.views.addBen;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import javax.inject.Inject;

import io.thoughtbox.hamdan.Adapters.DetailedItemSelectionListener;
import io.thoughtbox.hamdan.Adapters.SelectionListener;
import io.thoughtbox.hamdan.DashBoard;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.utls.OnResendOtp;
import io.thoughtbox.hamdan.alerts.AuthenticationView;
import io.thoughtbox.hamdan.alerts.BottomLists;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityAddBenBankBinding;
import io.thoughtbox.hamdan.databinding.CreateSuccessDialogBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.bankBenModel.BankBeneficiaryResponseData;
import io.thoughtbox.hamdan.model.bankBenModel.BenCreate;
import io.thoughtbox.hamdan.model.bankBenModel.DetailData;
import io.thoughtbox.hamdan.model.beneficiary.BanksRequest;
import io.thoughtbox.hamdan.model.beneficiary.BranchRequest;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.transfers.BankTransfer;
import io.thoughtbox.hamdan.viewModel.AddBankBenViewModel;
import io.thoughtbox.hamdan.viewModel.OtpViewModel;
import io.thoughtbox.hamdan.views.remittance.TransferView;

public class AddBenBank extends AppCompatActivity implements SelectionListener, DetailedItemSelectionListener, OnResendOtp {
    ActivityAddBenBankBinding binding;
    AddBankBenViewModel benViewModel;
    OtpViewModel otpViewModel;
    Loader loader;
    Dialog progressDialog;
    ArrayList<SelectionModal> userTypeList;
    ArrayList<SelectionModal> countryList;
    ArrayList<SelectionModal> transferTypeList;
    ArrayList<DetailData> bankList;
    ArrayList<SelectionModal> branchList;
    ArrayList<SelectionModal> relationList;
    ArrayList<SelectionModal> nationalityList;
    @Inject
    Dictionary dictionary;
    String accountNumber, ifsc, selectedType, countryId, transferTypeId, bankId, branchId, relationId, nationalityId;
    boolean isTypeSelected, isCountrySelcted, isTransferTypeSelected, isBankSelected, isBranchSelected, isRelationSelected, isNationalitySelected = false;
    String countryName, transferTypeName, bankName, branchName, relationName, nationalityName;
    AuthenticationView authenticationView;

    String otpRefNum;
    NotificationAlerts alerts;

    BankBeneficiaryResponseData bankTransferResponseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_ben_bank);
        binding.setLifecycleOwner(this);
        init();
        LiveData<Boolean> isSessionValid = benViewModel.getSessionStatus();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });
        LiveData<Boolean> isLoadingData = benViewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });

        benViewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<ArrayList<SelectionModal>> country = benViewModel.getCountryLiveDat();
        country.observe(this, countryData -> countryList = countryData);

        LiveData<ArrayList<SelectionModal>> txntype = benViewModel.getTxntypeLiveData();
        txntype.observe(this, txntypeData -> {
            transferTypeList = txntypeData;
            binding.transfertypeView.setVisibility(View.VISIBLE);
        });

        LiveData<ArrayList<DetailData>> banks = benViewModel.getBankLiveData();
        banks.observe(this, bank -> {
            bankList = bank;
            binding.bankView.setVisibility(View.VISIBLE);

        });

        LiveData<ArrayList<SelectionModal>> branch = benViewModel.getBranchLiveData();
        branch.observe(this, branches -> {
            branchList = branches;
            binding.branchView.setVisibility(View.VISIBLE);
        });

        LiveData<ArrayList<SelectionModal>> relation = benViewModel.getRelationLivedata();
        relation.observe(this, relations -> relationList = relations);

        LiveData<ArrayList<SelectionModal>> nationality = benViewModel.getNationalityLiveData();
        nationality.observe(this, national -> nationalityList = national);

        LiveData<BenCreate> create = benViewModel.createBankBenLiveData();
        create.observe(this, createBenResponseData -> {
            otpRefNum = createBenResponseData.getId();
            createdBeneficiaryDetails(createBenResponseData);
            if (createBenResponseData.getResult().toUpperCase().equals("TRUE")) {
                if (createBenResponseData.getPinmode().toUpperCase().equals("BIOMETRIC")) {
                    authenticationView.fingerLayout();
                } else {
                    authenticationView.OtpLayout().show();
                }
            }
        });


        LiveData<String> otpLiveData = authenticationView.getOtpLiveData();
        otpLiveData.observe(this, otp -> {
            OtpRequestModel params = new OtpRequestModel("ANDROID", "BankBeneficiary", otp, otpRefNum);
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
                showSuccessView();
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

        binding.accountNumber.setCustomSelectionActionModeCallback(new android.view.ActionMode.Callback() {

            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(android.view.ActionMode actionMode) {
            }

        });

        binding.confirm.setCustomSelectionActionModeCallback(new android.view.ActionMode.Callback() {

            public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(android.view.ActionMode actionMode) {
            }

        });


    }

    private void createdBeneficiaryDetails(BenCreate createdBenResponseData) {
        bankTransferResponseData = new BankBeneficiaryResponseData();
        bankTransferResponseData.setId(createdBenResponseData.getId());
        bankTransferResponseData.setName(createdBenResponseData.getName());
        bankTransferResponseData.setBank(createdBenResponseData.getAgent());
        bankTransferResponseData.setBranch(createdBenResponseData.getBranch());
        bankTransferResponseData.setAccountno(createdBenResponseData.getAccountno());
        bankTransferResponseData.setContact(createdBenResponseData.getCountry());
        bankTransferResponseData.setContact(createdBenResponseData.getContact());
        bankTransferResponseData.setCurrency(createdBenResponseData.getCurrency());
        bankTransferResponseData.setCurrencycode(createdBenResponseData.getCurrencycode());
    }

    private void navigateToTransferPage() {
        Intent intent = new Intent(this, TransferView.class);
        intent.putExtra("Bank", bankTransferResponseData);
        intent.putExtra("isBank", true);
        startActivity(intent);
    }

    private void init() {
        checkInternet();
        alerts = new NotificationAlerts(this);
        DaggerApiComponents.create().inject(this);

        loader = new Loader(this);
        progressDialog = loader.progress();
        authenticationView = new AuthenticationView(this, this);

        benViewModel = new ViewModelProvider(this).get(AddBankBenViewModel.class);
        otpViewModel = new ViewModelProvider(this).get(OtpViewModel.class);

        UiInteractions uiInteractions = new UiInteractions();
        binding.setLanguage(dictionary);
        binding.setClickers(uiInteractions);
//        alerts.disableCopyPaste(binding.accountNumber);
//        alerts.disableCopyPaste(binding.confirm);
        binding.accountNumber.setLongClickable(false);
        binding.confirm.setTextIsSelectable(false);
        getUserTypes();
        benViewModel.getcountry();
        benViewModel.getNationality();
        benViewModel.getRelation();
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
                alerts.dismissVpnDialog();
            }
        });
    }

    private void getUserTypes() {
        userTypeList = new ArrayList<>();
        SelectionModal modal1 = new SelectionModal("0", "INDIVIDUAL");
        userTypeList.add(modal1);

    }

    @Override
    public void onBottomSheetDetailedItemSelected(DetailData detailData, String tag) {
        if (tag.equals("Bank")) {
            setBank(detailData.getName(), detailData.getId());
            if (detailData.getBranches() != null && detailData.getBranches().size() > 0) {
                branchList = new ArrayList<>();
                for (int i = 0; i < detailData.getBranches().size(); i++) {
                    SelectionModal model = new SelectionModal(detailData.getBranches().get(i).getId(), detailData.getBranches().get(i).getName());
                    branchList.add(model);
                    binding.branchView.setVisibility(View.VISIBLE);
                }
            } else {
//                if (branchList == null || branchList.size() < 1) {
                BranchRequest params = new BranchRequest(bankId, countryId, "", "1", transferTypeId);
                benViewModel.getbranch(params);
//                }
            }
        }
    }

    @Override
    public void onBottomSheetSelected(String name, String id, String tag) {
        switch (tag) {
            case "Type":
                setType(name);
                break;
            case "Country":
                setCountry(name, id);
                break;
            case "TxnType":
                setTxnType(name, id);
                break;
            case "Bank":
                setBank(name, id);
                break;
            case "Branch":
                setBranch(name, id);
                break;
            case "Relation":
                setRelation(name, id);
                break;
            case "Nationality":
                setNationality(name, id);
                break;
        }
    }

    private void setNationality(String name, String id) {
        nationalityName = name;
        isNationalitySelected = true;
        nationalityId = id;
        binding.nationality.setText(name);
    }

    private void setRelation(String name, String id) {
        relationName = name;
        isRelationSelected = true;
        relationId = id;
        binding.relation.setText(name);
    }

    private void setBranch(String name, String id) {
        branchName = name;
        isBranchSelected = true;
        branchId = id;
        binding.branch.setText(name);
    }

    private void setBank(String name, String id) {
        bankName = name;
        isBankSelected = true;
        bankId = id;
        binding.bank.setText(bankName);

        binding.branch.setText("");
        isBranchSelected = false;

        binding.branchView.setVisibility(View.VISIBLE);
    }

    private void setTxnType(String name, String id) {
        transferTypeName = name;
        isTransferTypeSelected = true;
        transferTypeId = id;
        binding.txntype.setText(name);

        binding.bank.setText("");
        binding.branch.setText("");
        isBankSelected = false;
        isBranchSelected = false;

        binding.bankView.setVisibility(View.GONE);
        binding.branchView.setVisibility(View.GONE);

        branchList = new ArrayList<>();
        BanksRequest params = new BanksRequest(countryId, transferTypeId);
        benViewModel.getbank(params);

    }

    private void setCountry(String name, String id) {
        countryName = name;
        isCountrySelcted = true;
        countryId = id;
        binding.country.setText(name);

        binding.txntype.setText("");
        binding.bank.setText("");
        binding.branch.setText("");

        isBankSelected = false;
        isBranchSelected = false;
        isTransferTypeSelected = false;
        binding.bankView.setVisibility(View.GONE);
        binding.branchView.setVisibility(View.GONE);
        binding.transfertypeView.setVisibility(View.GONE);

        benViewModel.getTxntype(countryId);
    }

    private void setType(String name) {
        selectedType = name;
        isTypeSelected = true;
        binding.type.setText(name);
    }

    @Override
    public void onResendOtpClicked() {
        OtpRequestModel params = new OtpRequestModel("BankBeneficiary", "", otpRefNum);
        otpViewModel.resendTransactionOtp(params);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), BankTransfer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        benViewModel.clear();
        otpViewModel.clear();
        super.onDestroy();
    }

    private JSONObject createBenRequest() {
        JSONObject params = new JSONObject();
        JSONObject country = new JSONObject();
        JSONObject nationality = new JSONObject();
        JSONObject ownerrelation = new JSONObject();
        JSONObject bank = new JSONObject();
        JSONObject branch = new JSONObject();

        try {
            params.put("platform", "ANDROID");
            params.put("name", Objects.requireNonNull(binding.name.getText()).toString());
            params.put("code", ifsc);
            params.put("accountno", accountNumber);
            params.put("contact", Objects.requireNonNull(binding.mobile.getText()).toString());
            params.put("type", selectedType);
            params.put("transfertypedetail", transferTypeId);
            country.put("id", countryId);
            nationality.put("id", nationalityId);
            ownerrelation.put("id", relationId);
            bank.put("bankdata", bankId);
            branch.put("branchdata", branchId);

            params.put("country", country);
            params.put("nationality", nationality);
            params.put("ownerrelation", ownerrelation);
            params.put("bank", bank);
            params.put("bankbranch", branch);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
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
        if (transferTypeName.contains("BANK") || transferTypeName.toUpperCase().contains("ACCOUNT")) {
            if (!hasText(binding.accountNumber)) check = false;
            if (!hasText(binding.confirm)) check = false;
        }
        if (!hasText(binding.name)) check = false;
        if (!hasText(binding.mobile)) check = false;
        if (!hasText(binding.type)) check = false;
        if (!hasText(binding.country)) check = false;
        if (!hasText(binding.txntype)) check = false;
        if (!hasText(binding.bank)) check = false;
        if (!hasText(binding.branch)) check = false;
        if (!hasText(binding.relation)) check = false;
        if (!hasText(binding.nationality)) check = false;
        return check;
    }


    private void showSuccessView() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        CreateSuccessDialogBinding successDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.create_success_dialog, null, false);
        mBottomSheetDialog.setContentView(successDialogBinding.getRoot());
        mBottomSheetDialog.setCancelable(false);
        successDialogBinding.title.setText(dictionary.get("successfulBankNewSet"));
        successDialogBinding.desc.setText(dictionary.get("benAddedFastCashNewSet"));
        successDialogBinding.backHome.setText(dictionary.get("homeFastCashNewSet"));
        successDialogBinding.done.setText(dictionary.get("transferFastCashNewSet"));

        successDialogBinding.backHome.setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();
            navigateToHome();
        });
        successDialogBinding.done.setOnClickListener(v -> {
            mBottomSheetDialog.dismiss();
            navigateToTransferPage();
        });
        mBottomSheetDialog.show();
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public class UiInteractions {
        BottomLists bottomLists, bottomChildLists;

        public UiInteractions() {
            bottomLists = new BottomLists(AddBenBank.this, AddBenBank.this);
        }

        private void onAccountSubmitted() {
            if (binding.accountNumber.getText().toString().equals(binding.confirm.getText().toString())) {
                accountNumber = binding.accountNumber.getText().toString();
                if (validate()) {
                    benViewModel.createBankBen(createBenRequest());
                }
            } else {
                binding.confirm.setError("Account Number mismatch");
            }
        }

        public void onSubmitted(View view) {
            onAccountSubmitted();

        }

        public void onUserTypeClicked(View view) {
            bottomLists.bottomViewSelection("Type", userTypeList);
        }

        public void onCountryClicked(View view) {
            bottomLists.bottomViewSelection("Country", countryList);
        }

        public void onTxnTypeClicked(View view) {
            bottomLists.bottomViewSelection("TxnType", transferTypeList);
        }

        public void onBankClicked(View view) {
            branchList = new ArrayList<>();
            bottomChildLists = new BottomLists(AddBenBank.this, AddBenBank.this, "ChildView");
            bottomChildLists.childBottomViewSelection("Bank", bankList);
        }

        public void onBranchClicked(View view) {
            bottomLists.bottomViewSelection("Branch", branchList);
        }

        public void onRelationClicked(View view) {
            bottomLists.bottomViewSelection("Relation", relationList);
        }

        public void onNationalityClicked(View view) {
            bottomLists.bottomViewSelection("Nationality", nationalityList);
        }


        public void onBackClicked(View view) {
            onBackPressed();
        }
    }
}
