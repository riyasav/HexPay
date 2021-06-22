package io.thoughtbox.hamdan;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;

import javax.inject.Inject;

import io.thoughtbox.hamdan.Adapters.SelectionListener;
import io.thoughtbox.hamdan.alerts.BottomLists;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityDashBoardBinding;
import io.thoughtbox.hamdan.databinding.LogoutBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.CurrencyModel;
import io.thoughtbox.hamdan.model.CurrencyRateModel;
import io.thoughtbox.hamdan.model.rateModels.RateResponseData;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.receipts.Report;
import io.thoughtbox.hamdan.transfers.BankTransfer;
import io.thoughtbox.hamdan.transfers.CashPickUp;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Constants;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.viewModel.HomeViewModel;
import io.thoughtbox.hamdan.views.Policy;
import io.thoughtbox.hamdan.views.Profile;
import io.thoughtbox.hamdan.views.SettingsGrid;
import io.thoughtbox.hamdan.views.branches.BranchPager;

public class DashBoard extends AppCompatActivity implements  SelectionListener {
    public Dialog progressDialog;
    ActivityDashBoardBinding dashBoardBinding;
    HomeViewModel homeViewModel;
    boolean doubleBackToExitPressedOnce = false;
    NotificationAlerts alerts;
    @Inject
    Dictionary dictionary;
    BottomNavigationView bottomNavigationView;
    ArrayList<RateResponseData> txnTypeList;
    ArrayList<SelectionModal> txnTypes;
    ArrayList<SelectionModal> currencyList;
    String countryCurrencyCode = Universal.getInstance().getLoginResponsedata().getCurrencycode().toUpperCase();
    String selectedCurrencyRate = "1";
    CurrencyModel currencyModel;
    EditText lc;
    TextView fc;
    boolean isToggled = false;
    double rate;
    private Vibrator hapticFeedback;
    private BottomLists bottomLists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board);
        init();

        LiveData<Boolean> isSessionValid = homeViewModel.getSessionStatus();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });

        LiveData<Boolean> isLoadingData = homeViewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
//            if (isLoading) {
//                loader.animate();
//            } else {
//                loader.clearAnimation();
//            }
        });

        homeViewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
//                loader.clearAnimation();
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<String> contactNumber = homeViewModel.getContactLiveData();
        contactNumber.observe(this, customerCareNumber -> {
            if (customerCareNumber != null) {
                progressDialog.dismiss();
                makePhoneCall(customerCareNumber);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    if (item.getItemId() == R.id.track) {
                        Intent intent = new Intent(getApplicationContext(), Report.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    } else if (item.getItemId() == R.id.menu) {
                        Intent intent2 = new Intent(getApplicationContext(), SettingsGrid.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent2);
                    } else if (item.getItemId() == R.id.locate) {
                        Intent intent = new Intent(getApplicationContext(), BranchPager.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                    return true;
                });

        LiveData<ArrayList<RateResponseData>> rateData = homeViewModel.getRateData();
        rateData.observe(this, rateResponseData -> {
            txnTypeList = rateResponseData;
            txnTypes = filterTxnTypes(txnTypeList);
            if (!txnTypes.isEmpty()) {
                setTxnType(txnTypes.get(0).getName(), "");
            }
        });

        dashBoardBinding.lc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    lc.setHint("0.0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                lc.setSelection(lc.length());
                if (isToggled) {
                    calculateLcValue();
                } else {
                    calculateFcValue();
                }
            }
        });

    }

    private void makePhoneCall(String customerCareNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", customerCareNumber, null));
        startActivity(intent);
    }

    private void init() {
        alerts = new NotificationAlerts(this);
        checkInternet();
        DaggerApiComponents.create().inject(this);
        Interactions interactions = new Interactions();
        hapticFeedback = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        Loader progressLoader = new Loader(this);
        progressDialog = progressLoader.progress();
        bottomLists = new BottomLists(this, this);
        dashBoardBinding.setClickers(interactions);
        dashBoardBinding.setInfo(Universal.getInstance());
        dashBoardBinding.setLanguage(dictionary);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        Menu menu = bottomNavigationView.getMenu();
        menu.findItem(R.id.locate).setTitle(dictionary.get("locateUs"));
        menu.findItem(R.id.menu).setTitle(dictionary.get("menu"));
        menu.findItem(R.id.track).setTitle(dictionary.get("track"));

        getRateData();

        lc = dashBoardBinding.lc;
        fc = dashBoardBinding.fc;
        currencyModel = new CurrencyModel("", "");
        dashBoardBinding.setValues(currencyModel);

        Animation animation = new AlphaAnimation(4, 5); //to change visibility from visible to invisible
        animation.setDuration(3000); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        dashBoardBinding.whatsapp.startAnimation(animation); //to start animation
    }

    private void getRateData() {
        homeViewModel.getUserRate();
//        dotAnimation();
    }

    @Override
    protected void onDestroy() {
        homeViewModel.clear();
        super.onDestroy();
    }

    private void logout() {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        LogoutBinding logoutBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.logout, null, false);
        logoutBinding.setLanguage(dictionary);
        mBottomSheetDialog.setContentView(logoutBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);
        logoutBinding.cancel.setOnClickListener(view -> mBottomSheetDialog.dismiss());
        logoutBinding.done.setOnClickListener(view -> {
            mBottomSheetDialog.dismiss();
            Intent mainIntent = new Intent(DashBoard.this, SplashScreen.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(mainIntent);
        });
        mBottomSheetDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
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
    public void onBottomSheetSelected(String name, String id, String tag) {
        if (tag.equals("Mode of Transfer")) {
            setTxnType(name, id);
            dashBoardBinding.lc.setText("");
            dashBoardBinding.fc.setText("");
        } else if (tag.equals("Currency")) {
            dashBoardBinding.lc.setText("");
            dashBoardBinding.fc.setText("");
            selectedCurrencyRate = id;
            dashBoardBinding.rate.setText(MessageFormat.format("1 {0} = {1} {2}", countryCurrencyCode, id, name));
            if (isToggled) {
                dashBoardBinding.lcCode.setText(name);
            } else {
                dashBoardBinding.fcCode.setText(name);
            }
        }
    }

    public class Interactions {

        public Interactions() {

        }

        public void onUpdateClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), Profile.class);
            startActivity(intent);
        }

        public void onBankClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), BankTransfer.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onCashPickUpClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), CashPickUp.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onSettingsClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), SettingsGrid.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        public void onAboutUsClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), Policy.class);
            intent.putExtra("url", Constants.AboutUs);
            intent.putExtra("title", dictionary.get("aboutUs"));
            startActivity(intent);
        }

        public void onWhatsappClicked(View view) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/96891455455")));
        }

        public void onShareClicked(View view) {
            hapticFeedback.vibrate(50);
            shareAppLink();
        }


        public void onLogoutClicked(View view) {
            logout();
        }

        public void onProfilePicUpdated(View view) {

        }

        // Rate calculator Clicks

        public void onTxtTypeClicked(View view) {
            bottomLists.bottomViewSelection("Mode of Transfer", txnTypes);
        }

        public void swapValues(View view) {
            hapticFeedback.vibrate(50);
            isToggled = !isToggled;
            String temp = fc.getText().toString();
            fc.setText(lc.getText().toString());
            lc.setText(temp);

            String tempCode = dashBoardBinding.fcCode.getText().toString();
            dashBoardBinding.fcCode.setText(dashBoardBinding.lcCode.getText().toString());
            dashBoardBinding.lcCode.setText(tempCode);
        }

        public void onLcCurrencyClicked(View view) {
            if (isToggled) {
                if (currencyList != null && !currencyList.isEmpty()) {
                    bottomLists.bottomViewSelection("Currency", currencyList);
                } else {
                    Toast.makeText(DashBoard.this, "No other currency available", Toast.LENGTH_SHORT).show();
                }
            } else {
                dashBoardBinding.lcCode.setClickable(false);
            }
        }

        public void onFcCurrencyClicked(View view) {
            if (!isToggled) {
                if (currencyList != null && !currencyList.isEmpty()) {
                    bottomLists.bottomViewSelection("Currency", currencyList);
                } else {
                    Toast.makeText(DashBoard.this, "No other currency available", Toast.LENGTH_SHORT).show();
                }

            } else {
                dashBoardBinding.fcCode.setClickable(false);
            }
        }

    }
    private void shareAppLink() {
        ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setChooserTitle("Share App Link")
                .setText("Download HexPay mobile app \n https://play.google.com/store/apps/details?id=io.thoughtbox.hamdan")
                .startChooser();
    }
    // Rate calculator configurations

    public void calculateFcValue() {
        rate = Double.parseDouble(selectedCurrencyRate);
        try {
            double lcValue = Double.parseDouble(currencyModel.getLcAmount());
            double fcValue = (lcValue * rate);
            Double rounded = new BigDecimal(fcValue).setScale(2, RoundingMode.HALF_UP).doubleValue();
            fc.setText(new DecimalFormat("##.##").format(rounded));
        } catch (NumberFormatException e) {
            e.getMessage();
        }
    }

    public void calculateLcValue() {
        rate = Double.parseDouble(selectedCurrencyRate);
        try {
            double lcValue = Double.parseDouble(currencyModel.getLcAmount());
            double fcValue = lcValue * (1 / rate);
            Double rounded = new BigDecimal(fcValue).setScale(2, RoundingMode.HALF_UP).doubleValue();
            fc.setText(new DecimalFormat("##.##").format(rounded));
        } catch (NumberFormatException e) {
            e.getMessage();
        }
    }

    private ArrayList<CurrencyRateModel> filterRatesByTxnTypes(String type) {
        ArrayList<CurrencyRateModel> ratesList = new ArrayList<>();
        for (int i = 0; i < txnTypeList.size(); i++) {
            if (txnTypeList.get(i).getTxntype().equals(type)) {
                CurrencyRateModel model = new CurrencyRateModel(
                        txnTypeList.get(i).getCountryflag(),
                        txnTypeList.get(i).getCurrencycode(),
                        txnTypeList.get(i).getCurrency(),
                        txnTypeList.get(i).getUserrate(),
                        txnTypeList.get(i).getCharges()
                );
                ratesList.add(model);
            }
        }
        return ratesList;
    }

    private void setTxnType(String name, String id) {
        dashBoardBinding.mode.setText(name);
        setRateAndCurrency(name);
    }

    private void setRateAndCurrency(String name) {
        ArrayList<CurrencyRateModel> ratesList;
        String nationalityCurrencyCode = Universal.getInstance().getLoginResponsedata().getNationalitycurrencycode().toUpperCase();
        if (!nationalityCurrencyCode.equals(countryCurrencyCode)) {
            ratesList = filterRatesByTxnTypes(name);
            currencyList = getCurrencyListFromRateList(ratesList);
            CurrencyRateModel currencyDetails = getCurrencyCodeFromList(ratesList, nationalityCurrencyCode);
            selectedCurrencyRate = currencyDetails.getCurrencyRate();
            if (!isToggled) {
                dashBoardBinding.fcCode.setText(currencyDetails.getCurrencyCode());
            } else {
                dashBoardBinding.lcCode.setText(currencyDetails.getCurrencyCode());
            }
            dashBoardBinding.rate.setText(MessageFormat.format("1 {0} = {1} {2}", countryCurrencyCode, selectedCurrencyRate, currencyDetails.getCurrencyCode()));
        }

    }

    private ArrayList<SelectionModal> getCurrencyListFromRateList(ArrayList<CurrencyRateModel> ratesList) {
        ArrayList<SelectionModal> curList = new ArrayList<>();
        for (int i = 0; i < ratesList.size(); i++) {
            SelectionModal modal = new SelectionModal(ratesList.get(i).getCurrencyRate(), ratesList.get(i).getCurrencyCode());
            curList.add(modal);
        }
        return curList;
    }

    private CurrencyRateModel getCurrencyCodeFromList(ArrayList<CurrencyRateModel> ratesList, String curCode) {
        for (int i = 0; i < ratesList.size(); i++) {
            if (ratesList.get(i).getCurrencyCode().equals(curCode)) {
                return ratesList.get(i);
            }
        }
        return ratesList.get(0);
    }

    private ArrayList<SelectionModal> filterTxnTypes(ArrayList<RateResponseData> txnTypeList) {
        ArrayList<SelectionModal> txnTypes = new ArrayList<>();
        ArrayList<String> transferTypes = new ArrayList<>();
        for (int i = 0; i < txnTypeList.size(); i++) {
            String type = txnTypeList.get(i).getTxntype();
            if (txnTypes.isEmpty()) {
                transferTypes.add(txnTypeList.get(i).getTxntype());
                SelectionModal model = new SelectionModal(String.valueOf(i), txnTypeList.get(i).getTxntype());
                txnTypes.add(model);
            } else {
                if (!transferTypes.contains(type)) {
                    transferTypes.add(txnTypeList.get(i).getTxntype());
                    SelectionModal model = new SelectionModal(String.valueOf(i), type);
                    txnTypes.add(model);
                }

//                for (int j = 0; j < txnTypes.size(); j++) {
//                    if (txnTypes.get(j).getName().contains(type)) {
//                        break;
//                    }else{
//                        SelectionModal model = new SelectionModal(String.valueOf(i), type);
//                        txnTypes.add(model);
//                    }
//                }
            }
        }
        return txnTypes;
    }

}
