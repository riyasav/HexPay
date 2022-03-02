package io.thoughtbox.hamdan.views;

import android.app.Dialog;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

import io.thoughtbox.hamdan.Adapters.SelectionListener;
import io.thoughtbox.hamdan.DashBoard;
import io.thoughtbox.hamdan.MainActivity;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.alerts.BottomLists;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityRateCheckerBinding;
import io.thoughtbox.hamdan.databinding.CurrencyListBottomsheetBinding;
import io.thoughtbox.hamdan.fragments.rates.CurrencyRatesListener;
import io.thoughtbox.hamdan.fragments.rates.CurrencySelectionBottomSheetAdapter;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.model.CurrencyModel;
import io.thoughtbox.hamdan.model.CurrencyRateModel;
import io.thoughtbox.hamdan.model.rateModels.RateResponse;
import io.thoughtbox.hamdan.model.rateModels.RateResponseData;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.viewModel.HomeViewModel;
import io.thoughtbox.hamdan.viewModel.RateCheckerViewModel;

public class RateChecker extends AppCompatActivity implements CurrencyRatesListener, SelectionListener {

    ActivityRateCheckerBinding rateCheckerBinding;
    NotificationAlerts alerts;
    private BottomLists bottomLists;
    String selectedCurrencyRate = "1";
    CurrencyModel currencyModel;
    RateCheckerViewModel viewModel;
    ArrayList<SelectionModal> txnTypes;
    ArrayList<RateResponseData> currencyList;
    EditText lc;
    TextView fc;
    boolean isToggled = false;
    double rate;
    private Vibrator hapticFeedback;
    ArrayList<RateResponseData> txnTypeList;
    String countryCurrencyCode = "OMR";
    BottomSheetDialog mBottomSheetDialog;
    Dialog progressDialog;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rateCheckerBinding= DataBindingUtil.setContentView(this,R.layout.activity_rate_checker);
        rateCheckerBinding.setLifecycleOwner(this);
        rateCheckerBinding.setClicker(this);
        init();



        LiveData<ArrayList<RateResponseData>> rateData = viewModel.getTokenLessCheckRateLiveData();
        rateData.observe(this, rateResponseData -> {
        txnTypeList = rateResponseData;
        txnTypes = filterTxnTypes(txnTypeList);
        if (!txnTypes.isEmpty()) {
            setTxnType(txnTypes.get(0).getName(), "");
        }
    });

        viewModel.getLoaderError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });



        rateCheckerBinding.lc.addTextChangedListener(new TextWatcher() {
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

    private void init(){
        alerts = new NotificationAlerts(this);
        checkInternet();
       Interactions interactions = new Interactions();
        viewModel = new ViewModelProvider(this).get(RateCheckerViewModel.class);
        viewModel.getCheckerRate();
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        hapticFeedback = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
        rateCheckerBinding.setClickers(interactions);
        lc = rateCheckerBinding.lc;
        fc = rateCheckerBinding.fc;
        currencyModel=new CurrencyModel("","");
        rateCheckerBinding.setValues(currencyModel);
        txnTypeList=new ArrayList<>();
        bottomLists=new BottomLists(this,this);

    }


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

    @Override
    public void onCurrencyListClickListener(RateResponseData data) {

        mBottomSheetDialog.dismiss();
        rateCheckerBinding.lc.setText("");
        rateCheckerBinding.fc.setText("");
        selectedCurrencyRate = data.getUserrate();
        rateCheckerBinding.rate.setText(MessageFormat.format("1 {0} = {1} {2}", countryCurrencyCode, data.getUserrate(), data.getCurrencycode()));
        if (isToggled) {
            rateCheckerBinding.lcCode.setText(data.getCurrencycode());
        } else {
            rateCheckerBinding.fcCode.setText(data.getCurrencycode());

    }

    }

    @Override
    public void onBottomSheetSelected(String name, String id, String tag) {

        if (tag.equals("Mode of Transfer")) {
            setTxnType(name, id);
            rateCheckerBinding.lc.setText("");
            rateCheckerBinding.fc.setText("");
        } else if (tag.equals("Currency")) {
            rateCheckerBinding.lc.setText("");
            rateCheckerBinding.fc.setText("");
            selectedCurrencyRate = id;
            rateCheckerBinding.rate.setText(MessageFormat.format("1 {0} = {1} {2}", countryCurrencyCode, id, name));
            if (isToggled) {
                rateCheckerBinding.lcCode.setText(name);
            } else {
                rateCheckerBinding.fcCode.setText(name);
            }
        }
    }




    public class Interactions {

        public Interactions() {

        }

        public void swapValues(View view) {
            hapticFeedback.vibrate(50);
            isToggled = !isToggled;
            String temp = fc.getText().toString();
            fc.setText(lc.getText().toString());
            lc.setText(temp);

            String tempCode = rateCheckerBinding.fcCode.getText().toString();
            rateCheckerBinding.fcCode.setText(rateCheckerBinding.lcCode.getText().toString());
            rateCheckerBinding.lcCode.setText(tempCode);
        }

        public void onLcCurrencyClicked(View view) {
            if (isToggled) {
                if (currencyList != null && !currencyList.isEmpty()) {
                  currencyBottomView(currencyList);
                } else {
                    Toast.makeText(RateChecker.this, "No other currency available", Toast.LENGTH_SHORT).show();
                }
            } else {
                rateCheckerBinding.lcCode.setClickable(false);
            }
        }

        public void onFcCurrencyClicked(View view) {
            if (!isToggled) {
                if (currencyList != null && !currencyList.isEmpty()) {
                    currencyBottomView(currencyList);
                } else {
                    Toast.makeText(RateChecker.this, "No other currency available", Toast.LENGTH_SHORT).show();
                }

            } else {
                rateCheckerBinding.fcCode.setClickable(false);
            }
        }

        public void onTxtTypeClicked(View view) {
            bottomLists.bottomViewSelection("Mode of Transfer", txnTypes);
        }

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

    private void setTxnType(String name, String id) {
        rateCheckerBinding.mode.setText(name);
        setRateAndCurrency(name);
    }

    private void setRateAndCurrency(String name) {
        ArrayList<CurrencyRateModel> ratesList;
        String nationalityCurrencyCode="";

//        String nationalityCurrencyCode = Universal.getInstance().getLoginResponsedata().getNationalitycurrencycode().toUpperCase();
        if (!nationalityCurrencyCode.equals(countryCurrencyCode)) {
            ratesList = filterRatesByTxnTypes(name);
            currencyList = getCurrencyListFromRateList(ratesList);
            CurrencyRateModel currencyDetails = getCurrencyCodeFromList(ratesList,nationalityCurrencyCode);
            selectedCurrencyRate = currencyDetails.getCurrencyRate();
            if (!isToggled) {
                rateCheckerBinding.fcCode.setText(currencyDetails.getCurrencyCode());
            } else {
                rateCheckerBinding.lcCode.setText(currencyDetails.getCurrencyCode());
            }
            rateCheckerBinding.rate.setText(MessageFormat.format("1 {0} = {1} {2}", countryCurrencyCode, selectedCurrencyRate, currencyDetails.getCurrencyCode()));
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

    private ArrayList<RateResponseData> getCurrencyListFromRateList(ArrayList<CurrencyRateModel> ratesList) {
        ArrayList<RateResponseData> curList = new ArrayList<>();
        for (int i = 0; i < ratesList.size(); i++) {
            RateResponseData modal = new RateResponseData();
            modal.setCurrency(ratesList.get(i).getCurrencyName());
            modal.setUserrate(ratesList.get(i).getCurrencyRate());
            modal.setCountryflag(ratesList.get(i).getCurrencyFlag());
            modal.setCurrencycode(ratesList.get(i).getCurrencyCode());
            curList.add(modal);
        }
        return curList;
    }

    private CurrencyRateModel getCurrencyCodeFromList(ArrayList<CurrencyRateModel> ratesList,String curCode) {
        for (int i = 0; i < ratesList.size(); i++) {
            if (ratesList.get(i).getCurrencyCode().equals(curCode)) {
                return ratesList.get(i);
            }
        }
        return ratesList.get(0);
    }

    public void currencyBottomView(ArrayList<RateResponseData> rateDetails) {
        String tag = "Currency";
        CurrencySelectionBottomSheetAdapter adapter;
        mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);

        CurrencyListBottomsheetBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.currency_list_bottomsheet, null, false);
//       dialogBinding.setClickers(this);
        Objects.requireNonNull(mBottomSheetDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setContentView(dialogBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.show();
        dialogBinding.title.setText(tag);

        RecyclerView recyclerView = dialogBinding.list;
        LinearLayoutManager layoutManager = new LinearLayoutManager(RateChecker.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (rateDetails != null) {
            adapter = new CurrencySelectionBottomSheetAdapter(RateChecker.this, this, rateDetails, tag);
            recyclerView.setAdapter(adapter);

         final SearchView searchView = mBottomSheetDialog.findViewById(R.id.search_view);
            searchView.setMaxWidth(Integer.MAX_VALUE);

            // listening to search query text change
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // filter recycler view when query submitted
                    adapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    adapter.getFilter().filter(query);
                    return false;
                }
            });

        } else {
            Toast.makeText(RateChecker.this, "No Data available", Toast.LENGTH_SHORT).show();
        }


    }

//    @Override
//    protected void onDestroy() {
//       if (mBottomSheetDialog.isShowing()){
//           mBottomSheetDialog.dismiss();
//       }
//        super.onDestroy();
//    }


    public void onBackClicked(View view) {
       finish();

    }
}