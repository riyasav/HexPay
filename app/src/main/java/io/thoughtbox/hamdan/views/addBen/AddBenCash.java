package io.thoughtbox.hamdan.views.addBen;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import io.thoughtbox.hamdan.databinding.ActivityAddBenCashBinding;
import io.thoughtbox.hamdan.databinding.CreateSuccessDialogBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.bankBenModel.BenCreate;
import io.thoughtbox.hamdan.model.bankBenModel.CheckOutResponseData;
import io.thoughtbox.hamdan.model.bankBenModel.DetailData;
import io.thoughtbox.hamdan.model.beneficiary.BanksRequest;
import io.thoughtbox.hamdan.model.cashPickUpModel.CashPickUpResponseData;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.transfers.CashPickUp;
import io.thoughtbox.hamdan.viewModel.AddFastCashBenViewModel;
import io.thoughtbox.hamdan.viewModel.OtpViewModel;
import io.thoughtbox.hamdan.views.remittance.TransferView;

public class AddBenCash extends AppCompatActivity implements SelectionListener, DetailedItemSelectionListener, OnResendOtp {
    ActivityAddBenCashBinding binding;
    AddFastCashBenViewModel benViewModel;
    OtpViewModel otpViewModel;
    Loader loader;
    Dialog progressDialog;

    @Inject
    Dictionary dictionary;

    ArrayList<SelectionModal> idTypeList;
    ArrayList<SelectionModal> userTypeList;
    ArrayList<SelectionModal> countryList;
    ArrayList<SelectionModal> transferTypeList;
    ArrayList<DetailData> bankList;

    ArrayList<SelectionModal> relationList;
    ArrayList<SelectionModal> nationalityList;

    ArrayList<SelectionModal> stateList;
    ArrayList<SelectionModal> cityList;
    ArrayList<SelectionModal> branchList;


    String idTypeName, selectedType, countryId, transferTypeId, bankId, branchId, relationId, nationalityId;
    boolean isIdtypeSelected, isTypeSelected, isCountrySelcted, isTransferTypeSelected, isBankSelected, isStateSelected, isCitySelected, isBranchSelected, isRelationSelected, isNationalitySelected = false;
    String countryName, transferTypeName, bankName, branchName, relationName, nationalityName;
    boolean isStateCityRequired = true;
    AuthenticationView authenticationView;
    String cityId, cityName, stateId, stateName, idTypeId;
    String otpRefNum;
    CashPickUpResponseData cashPickUpResponseData;
    private NotificationAlerts alerts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_ben_cash);
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

        LiveData<ArrayList<SelectionModal>> getIdType = benViewModel.getIdTypeLiveData();
        getIdType.observe(this, idtypeData -> idTypeList = idtypeData);


        LiveData<ArrayList<SelectionModal>> relation = benViewModel.getRelationLivedata();
        relation.observe(this, relations -> relationList = relations);

        LiveData<ArrayList<SelectionModal>> nationality = benViewModel.getNationalityLiveData();
        nationality.observe(this, national -> nationalityList = national);

        LiveData<ArrayList<SelectionModal>> txntype = benViewModel.getTxntypeLiveData();
        txntype.observe(this, txntypeData -> {
            transferTypeList = txntypeData;
            binding.transfertypeView.setVisibility(View.VISIBLE);
        });

        LiveData<ArrayList<DetailData>> Agent = benViewModel.getAgentLiveData();
        Agent.observe(this, agent -> {
            bankList = agent;
            binding.agentView.setVisibility(View.VISIBLE);
        });

        LiveData<ArrayList<SelectionModal>> branch = benViewModel.getNormalBranchLiveData();
        branch.observe(this, agentBranch -> {
            branchList = agentBranch;
            binding.branchView.setVisibility(View.VISIBLE);
            binding.branch.setEnabled(true);
        });

        LiveData<CheckOutResponseData> checkOutOption = benViewModel.checkOutLiveData();
        checkOutOption.observe(this, checkOutResponseData -> {
            if (!checkOutResponseData.getId().equals("0") && !checkOutResponseData.getId().equals("")) {
                cityId = checkOutResponseData.getCityid();
                cityName = checkOutResponseData.getCity();
                stateId = checkOutResponseData.getStateid();
                stateName = checkOutResponseData.getState();
                branchName = checkOutResponseData.getBranchname();
                branchId = checkOutResponseData.getId();

                binding.stateView.setVisibility(View.VISIBLE);
                binding.cityView.setVisibility(View.VISIBLE);
                binding.branchView.setVisibility(View.VISIBLE);

                binding.state.setText(stateName);
                binding.city.setText(cityName);
                binding.branch.setText(branchName);

                binding.state.setEnabled(false);
                binding.branch.setEnabled(false);
                binding.city.setEnabled(false);


            } else {
                JSONObject params = new JSONObject();
                try {
                    params.put("country", countryId);
                    params.put("agentdata", bankId);
                    params.put("transfertype", transferTypeId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                benViewModel.getState(params);
            }
        });

        LiveData<ArrayList<SelectionModal>> states = benViewModel.getStateLiveData();
        states.observe(this, statelistData -> {
            stateList = statelistData;
            binding.stateView.setVisibility(View.VISIBLE);
            binding.state.setEnabled(true);
        });

        LiveData<ArrayList<SelectionModal>> cities = benViewModel.getCityLiveData();
        cities.observe(this, citylistData -> {
            cityList = citylistData;
            binding.cityView.setVisibility(View.VISIBLE);
            binding.city.setEnabled(true);
        });

        LiveData<ArrayList<SelectionModal>> branches = benViewModel.getTFBranchesLiveData();
        branches.observe(this, branchlistData -> {
            branchList = branchlistData;
            binding.branchView.setVisibility(View.VISIBLE);
            binding.branch.setEnabled(true);

        });


        LiveData<BenCreate> create = benViewModel.createFastCashBenLiveData();
        create.observe(this, createBenResponseData -> {
            otpRefNum = createBenResponseData.getId();
            createdBeneficiaryDetails(createBenResponseData);
            if (createBenResponseData.getResult().toUpperCase().equals("TRUE")) {
                if (createBenResponseData.getPinmode().toUpperCase().equals("BIOMETRIC")) {
                    //call Biometric view
                    authenticationView.fingerLayout();
                } else {
                    authenticationView.OtpLayout().show();
                }
            }
        });


        LiveData<String> otpLiveData = authenticationView.getOtpLiveData();
        otpLiveData.observe(this, otp -> {
            OtpRequestModel params = new OtpRequestModel("ANDROID", "FastCashBeneficiary", otp, otpRefNum);
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
//                navigateToFastCashBenList();
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
            navigateToFastCashBenList();
        });
        mBottomSheetDialog.show();
    }

    private void createdBeneficiaryDetails(BenCreate createdBenResponseData) {
        cashPickUpResponseData = new CashPickUpResponseData();
        cashPickUpResponseData.setId(createdBenResponseData.getId());
        cashPickUpResponseData.setName(createdBenResponseData.getName());
        cashPickUpResponseData.setAgent(createdBenResponseData.getAgent());
        cashPickUpResponseData.setAgentbranch(createdBenResponseData.getBranch());
        cashPickUpResponseData.setContact(createdBenResponseData.getCountry());
        cashPickUpResponseData.setContact(createdBenResponseData.getContact());
        cashPickUpResponseData.setCurrency(createdBenResponseData.getCurrency());
        cashPickUpResponseData.setCurrencycode(createdBenResponseData.getCurrencycode());
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToFastCashBenList() {
        Intent intent = new Intent(this, TransferView.class);
        intent.putExtra("CashPickUp", cashPickUpResponseData);
        intent.putExtra("isBank", false);
        startActivity(intent);
    }

    private void init() {
        checkInternet();
        alerts = new NotificationAlerts(this);

        DaggerApiComponents.create().inject(this);

        loader = new Loader(this);
        progressDialog = loader.progress();
        authenticationView = new AuthenticationView(this, this);

        benViewModel = new ViewModelProvider(this).get(AddFastCashBenViewModel.class);
        otpViewModel = new ViewModelProvider(this).get(OtpViewModel.class);

        UiInteractions interactions = new UiInteractions();
        binding.setClickers(interactions);
        binding.setLanguage(dictionary);

        getUserTypes();
        benViewModel.getIDtype();
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

    @Override
    public void onBottomSheetDetailedItemSelected(DetailData detailData, String tag) {
        if (tag.equals("Agent")) {
            setBank(detailData.getName(), detailData.getId());
            if (detailData.getBranches() != null && detailData.getBranches().size() > 0) {
                branchList = new ArrayList<>();
                for (int i = 0; i < detailData.getBranches().size(); i++) {
                    SelectionModal model = new SelectionModal(detailData.getBranches().get(i).getId(), detailData.getBranches().get(i).getName());
                    branchList.add(model);
                }
                isStateCityRequired = false;
                binding.branchView.setVisibility(View.VISIBLE);
            } else {
                checkCondition();
            }
        }
    }

    @Override
    public void onResendOtpClicked() {
        OtpRequestModel params = new OtpRequestModel("FastCashBeneficiary", "", "111");
        otpViewModel.resendTransactionOtp(params);
    }

    @Override
    public void onBottomSheetSelected(String name, String id, String tag) {
        switch (tag) {
            case "IDtype":
                setIDType(name, id);
                break;
            case "Type":
                setType(name);
                break;
            case "Country":
                setCountry(name, id);
                break;
            case "TxnType":
                setTxnType(name, id);
                break;
            case "Agent":
                setBank(name, id);
                break;
            case "State":
                setState(name, id);
                break;
            case "City":
                setCity(name, id);
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

    private void setIDType(String name, String id) {
        idTypeName = name;
        isIdtypeSelected = true;
        idTypeId = id;
        binding.idtype.setText(name);
    }

    private void setCity(String name, String id) {
        cityName = name;
        isCitySelected = true;
        cityId = id;
        binding.city.setText(name);

        getBranches();
    }

    private void setState(String name, String id) {
        stateName = name;
        isStateSelected = true;
        stateId = id;
        binding.state.setText(name);

        JSONObject params = new JSONObject();
        try {
            params.put("country", countryId);
            params.put("agentdata", bankId);
            params.put("transfertype", transferTypeId);
            params.put("state", stateId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        benViewModel.getCity(params);
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
        binding.bank.setText(name);
    }

    private void setTxnType(String name, String id) {
        transferTypeName = name;
        isTransferTypeSelected = true;
        transferTypeId = id;
        binding.txntype.setText(name);

        BanksRequest params = new BanksRequest(countryId, transferTypeId);
        benViewModel.getAgent(params);

        binding.branchView.setVisibility(View.GONE);
        binding.cityView.setVisibility(View.GONE);
        binding.stateView.setVisibility(View.GONE);

        branchList = new ArrayList<>();
        cityList = new ArrayList<>();
        stateList = new ArrayList<>();

        binding.bank.setText("");
        binding.state.setText("");
        binding.city.setText("");
        binding.branch.setText("");
    }

    private void setCountry(String name, String id) {
        countryName = name;
        isCountrySelcted = true;
        countryId = id;
        binding.country.setText(name);

        benViewModel.getTxntype(countryId);

        binding.branchView.setVisibility(View.GONE);
        binding.cityView.setVisibility(View.GONE);
        binding.stateView.setVisibility(View.GONE);
        binding.agentView.setVisibility(View.GONE);


        bankList = new ArrayList<>();
        branchList = new ArrayList<>();
        cityList = new ArrayList<>();
        stateList = new ArrayList<>();

        binding.txntype.setText("");
        binding.bank.setText("");
        binding.state.setText("");
        binding.city.setText("");
        binding.branch.setText("");

    }

    private void setType(String name) {
        selectedType = name;
        isTypeSelected = true;
        binding.type.setText(name);
    }

    private void getUserTypes() {
        userTypeList = new ArrayList<>();
        SelectionModal modal1 = new SelectionModal("0", "INDIVIDUAL");
        userTypeList.add(modal1);
    }

    @Override
    protected void onDestroy() {
        benViewModel.clear();
        otpViewModel.clear();
        super.onDestroy();
    }

    private void getBranches() {
        JSONObject params = new JSONObject();
        try {
            params.put("country", countryId);
            params.put("agentdata", bankId);
            params.put("transfertype", transferTypeId);
            params.put("state", stateId);
            params.put("city", cityId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        benViewModel.getTFbranch(params);
    }

    private JSONObject createFastCashBen() {

        JSONObject params = new JSONObject();
        JSONObject idtype = new JSONObject();
        JSONObject country = new JSONObject();
        JSONObject agent = new JSONObject();
        JSONObject agentbranch = new JSONObject();
        JSONObject nationality = new JSONObject();
        JSONObject ownerrelation = new JSONObject();

        try {
            params.put("platform", "ANDROID");
            idtype.put("id", idTypeId);
            country.put("id", countryId);
            agent.put("agentdata", bankId);
            agentbranch.put("branchdata", branchId);
            nationality.put("id", nationalityId);
            ownerrelation.put("id", relationId);

            params.put("name", Objects.requireNonNull(binding.name.getText()).toString().toUpperCase());
            params.put("idtype", idtype);
            params.put("idno", Objects.requireNonNull(binding.idNumber.getText()).toString());
            params.put("contact", Objects.requireNonNull(binding.mobile.getText()).toString());
            params.put("city", cityName);
            params.put("cityid", cityId);
            params.put("state", stateName);
            params.put("stateid", stateId);
            params.put("country", country);
            params.put("agent", agent);
            params.put("agentbranch", agentbranch);
            params.put("nationality", nationality);
            params.put("ownerrelation", ownerrelation);
            params.put("transfertypedetail", transferTypeId);
            params.put("type", selectedType);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    private void checkCondition() {
        if (bankName.toUpperCase().trim().equals("TRANSFAST")) {
            if (transferTypeName.toUpperCase().trim().equals("CASH PICK UP")) {
                checkPayOutOption();
            } else {
                JSONObject params = new JSONObject();
                try {
                    params.put("country", countryId);
                    params.put("agentdata", bankId);
                    params.put("transfertype", transferTypeId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                benViewModel.getState(params);
            }
        } else {
            JSONObject params = new JSONObject();
            try {
                params.put("benCountry", countryId);
                params.put("remitagent", bankId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isStateCityRequired = false;
            benViewModel.getNormalbranch(params);

        }

    }

    private void checkPayOutOption() {
        JSONObject params = new JSONObject();
        try {
            params.put("country", countryId);
            params.put("agentdata", bankId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        benViewModel.checkPayOut(params);
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
        if (!hasText(binding.name)) check = false;
        if (!hasText(binding.mobile)) check = false;
        if (!hasText(binding.idNumber)) check = false;
        if (!hasText(binding.idtype)) check = false;
        if (!hasText(binding.relation)) check = false;
        if (!hasText(binding.nationality)) check = false;
        if (!hasText(binding.type)) check = false;
        if (!hasText(binding.country)) check = false;
        if (!hasText(binding.txntype)) check = false;
        if (!hasText(binding.bank)) check = false;
        if (isStateCityRequired) {
            if (!hasText(binding.city)) check = false;
            if (!hasText(binding.state)) check = false;
        }
        if (!hasText(binding.branch)) check = false;
        return check;
    }

    public class UiInteractions {

        BottomLists bottomLists, detailedBottomsheet;

        public UiInteractions() {
            bottomLists = new BottomLists(AddBenCash.this, AddBenCash.this);

        }

        public void onBackClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), CashPickUp.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        public void onIdTypeClicked(View view) {
            bottomLists.bottomViewSelection("IDtype", idTypeList);
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

        public void onAgentClicked(View view) {
            detailedBottomsheet = new BottomLists(AddBenCash.this, AddBenCash.this, "CashPickUp");
            detailedBottomsheet.childBottomViewSelection("Agent", bankList);
        }

        public void onStateClicked(View view) {
            bottomLists.bottomViewSelection("State", stateList);
        }

        public void onCityClicked(View view) {
            bottomLists.bottomViewSelection("City", cityList);
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

        public void onSubmitted(View view) {
            if (validate()) {
                benViewModel.createBen(createFastCashBen());
            }
        }

    }
}
