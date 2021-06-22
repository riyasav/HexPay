package io.thoughtbox.hamdan.fragments.rates;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.CurrencyListBottomsheetBinding;
import io.thoughtbox.hamdan.databinding.FragmentCashPickUpRateBinding;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.model.rateModels.RateResponseData;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.viewModel.HomeViewModel;

public class CashPickUpRateFragment extends Fragment implements CurrencyRatesListener {
    FragmentCashPickUpRateBinding binding;
    HomeViewModel viewModel;
    private NotificationAlerts alerts;
    public Dialog progressDialog;
    ArrayList<RateResponseData> rateDetails;
    ArrayList<RateResponseData> cashPickupRateDetails;
    BottomSheetDialog mBottomSheetDialog;
    String selectedCurrencyRate = "1";
    String currencyCode = Universal.getInstance().getLoginResponsedata().getNatcurrencycode();
    boolean isSwapped = false;
    String lcCurrencyCode ="QAR";
    String lcCurrencyFlag =  "https://www.thoughtbox.io/countryflags/om.png";;
    String fcFlag;

    public CashPickUpRateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cash_pick_up_rate, container, false);
        binding.setLifecycleOwner(this);
        init();

        Glide.with(this)
                .load(lcCurrencyFlag)
                .into(new CustomTarget<Drawable>(35, 35) {

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.lcCode.setCompoundDrawablesWithIntrinsicBounds(resource, null, null, null);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        binding.lcCode.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null);
                    }
                });

        binding.lcCode.setText(lcCurrencyCode);

//        @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(R.drawable.down_white);
//        Glide.with(this)
//                .load(Universal.getInstance().getLoginResponsedata().getNationalityflag())
//                .into(new CustomTarget<Drawable>(35, 35) {
//
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        binding.fcCode.setCompoundDrawablesWithIntrinsicBounds(resource, null, d, null);
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        binding.fcCode.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null);
//                    }
//                });
//        binding.fcCode.setText(Universal.getInstance().getLoginResponsedata().getNationalitycurrencycode());

        LiveData<Boolean> isSessionValid = viewModel.getSessionStatus();
        isSessionValid.observe(getViewLifecycleOwner(), isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });
        LiveData<ArrayList<RateResponseData>> rateData = viewModel.getRateData();
        rateData.observe(getViewLifecycleOwner(), rateResponseData -> {
            rateDetails = rateResponseData;

            cashPickupRateDetails = new ArrayList<>();
            for (int i = 0; i < rateResponseData.size(); i++) {
                if (rateResponseData.get(i).getTxntype().toUpperCase().equals("FAST CASH TRANSFER")) {
                    if (currencyCode.equals(rateResponseData.get(i).getCurrencycode())) {
                        selectedCurrencyRate = rateResponseData.get(i).getUserrate();
                        binding.fcCode.setText(rateResponseData.get(i).getCurrencycode());
                        binding.fc.setText(calculateRate(rateResponseData.get(i).getUserrate()));
                        fcFlag= rateResponseData.get(i).getCountryflag();
                        setFlagOnFC(fcFlag);
                    }
                    cashPickupRateDetails.add(rateResponseData.get(i));
                }
            }

        });

        LiveData<Boolean> isLoadingData = viewModel.getIsLoading();
        isLoadingData.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
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

        binding.lc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
//                    binding.lc.setText("1");
                    binding.lc.setSelection( binding.lc.getText().length());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() != 0) {
                    calculateValue(s.toString());
                }
            }
        });

        return binding.getRoot();
    }

    private void init() {
        alerts = new NotificationAlerts(getContext());
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        Loader progressLoader = new Loader(getContext());
        progressDialog = progressLoader.progress();
        binding.setClickers(this);
        viewModel.getUserRate();
    }

    public void onFcClicked(View view) {
        currencyBottomView(cashPickupRateDetails);
    }

    public void currencyBottomView(ArrayList<RateResponseData> rateDetails) {
        String tag = "Currency";
        CurrencySelectionBottomSheetAdapter adapter;
        mBottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);

        CurrencyListBottomsheetBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.currency_list_bottomsheet, null, false);
//        dialogBinding.setClickers(this);
        Objects.requireNonNull(mBottomSheetDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setContentView(dialogBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);

        dialogBinding.title.setText(tag);

        RecyclerView recyclerView = dialogBinding.list;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (rateDetails != null) {
            adapter = new CurrencySelectionBottomSheetAdapter(getContext(), this, rateDetails, tag);
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
            Toast.makeText(getContext(), "No Data available", Toast.LENGTH_SHORT).show();
        }

        mBottomSheetDialog.show();
    }

    @Override
    public void onCurrencyListClickListener(RateResponseData data) {
        mBottomSheetDialog.dismiss();
        binding.fcCode.setText(data.getCurrencycode());
        binding.fc.setText(calculateRate(data.getUserrate()));
        setFlagOnFC(data.getCountryflag());
        selectedCurrencyRate = data.getUserrate();
    }

    private String calculateRate(String userrate) {
        double roundOff = 0.00;
        String lc = binding.lc.getText().toString();
        try {
            double rate = Double.parseDouble(userrate);
            double calculatedRate = rate * Double.parseDouble(lc);
            roundOff = Math.round(calculatedRate * 100.0) / 100.0;
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
        return String.valueOf(roundOff);
    }

    private void setFlagOnFC(String flag) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(R.drawable.down_white);
        Glide.with(this)
                .load(flag)
                .into(new CustomTarget<Drawable>(35, 35) {

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.fcCode.setCompoundDrawablesWithIntrinsicBounds(resource, null, d, null);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        binding.fcCode.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null);
                    }
                });
    }

    private void setFlagOnLC(String flag) {
        @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getResources().getDrawable(R.drawable.down_white);
        Glide.with(this)
                .load(flag)
                .into(new CustomTarget<Drawable>(35, 35) {

                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        binding.lcCode.setCompoundDrawablesWithIntrinsicBounds(resource, null, d, null);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        binding.lcCode.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null);
                    }
                });
    }

    public void calculateValue(String inputValue) {
        try {
            double rate = Double.parseDouble(selectedCurrencyRate);
            double lcValue = Double.parseDouble(inputValue);
            double fcValue = lcValue * rate;
            Double rounded = new BigDecimal(fcValue).setScale(2, RoundingMode.HALF_UP).doubleValue();
            binding.fc.setText(new DecimalFormat("##.##").format(rounded));
        } catch (NumberFormatException e) {
            e.getMessage();
        }
    }

    public void onChangeClicked(View view) {
//        rotateAnimation();
//        if (isSwapped){
//            swapCurrency(lcFlag, lcAmount, lcCode,fcCode, fcFlag, fcAmount);
//
//        }else{
        swapCurrency(lcCurrencyFlag,binding.lc.getText().toString(),lcCurrencyCode,  binding.fcCode.getText().toString(), fcFlag,  binding.fc.getText().toString());
//        }

    }

    private void rotateAnimation() {

    }

    private void swapCurrency(String lcFlag, String lcAmount, String lcCode, String fcCode, String fcFlag, String fcAmount) {
        isSwapped = true;
        // Flag Swap
        String tempFlag = lcFlag;
        lcFlag = fcFlag;
        fcFlag = tempFlag;
        // Code Swap
        String tempCode = lcCode;
        lcCode = fcCode;
        fcCode = tempCode;
        // Amount Swap
        String tempCurrency = lcAmount;
        lcAmount = fcAmount;
        fcAmount = tempCurrency;
        // Setting Swapped Data

        setFlagOnFC(fcFlag);
        setFlagOnLC(lcFlag);

        binding.fcCode.setText(fcCode);
        binding.lcCode.setText(lcCode);

        binding.lc.setText(lcAmount);
        binding.fc.setText(calculateLcAmount(fcAmount));

    }

    private String calculateLcAmount(String lcvalue) {
        double amount = 0.0;
        try {
            double lc = Double.parseDouble(lcvalue);
            double rate = Double.parseDouble(selectedCurrencyRate);
            amount = rate * lc;
        } catch (Exception e) {
            Log.e("conversionError", e.getMessage());
        }
        return String.valueOf(amount);
    }

}