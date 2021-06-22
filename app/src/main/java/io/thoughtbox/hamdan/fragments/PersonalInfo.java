package io.thoughtbox.hamdan.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import io.thoughtbox.hamdan.Adapters.SelectionListener;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.model.DateTimeResponseDate;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.alerts.BottomLists;
import io.thoughtbox.hamdan.databinding.FragmentPersonalInfoBinding;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;
import io.thoughtbox.hamdan.viewModel.RegisterViewModel;
import io.thoughtbox.hamdan.views.CheckUser;
import io.thoughtbox.hamdan.views.Signup;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalInfo extends Fragment implements SelectionListener {
    private FragmentPersonalInfoBinding binding;
    private RegisterViewModel viewModel;
    private ArrayList<SelectionModal> countryList;
    private ArrayList<SelectionModal> proffessionsList;
    private ArrayList<SelectionModal> salaryList;
    private ArrayList<SelectionModal> userTypeList;
    private ArrayList<SelectionModal> genderList;

    private Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener dobDate;

    private Dialog progressDialog;
    private BottomLists bottomLists;

    private String professionId;
    private String salaryId;
    private String userType;
    private String nationalityID;
    private String dob;
    private String gender;
    private String RcountryId="150";
    long currentDateTime=0;
    public PersonalInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_info, container, false);
        binding.setLifecycleOwner(this);
        dobDate = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDobLabel();
        };

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

        LiveData<ArrayList<SelectionModal>> countryLiveData = viewModel.getCountriesLiveData();
        countryLiveData.observe(getViewLifecycleOwner(), countries -> countryList = countries);

        LiveData<ArrayList<SelectionModal>> proffessions = viewModel.getProffessionLivedata();
        proffessions.observe(getViewLifecycleOwner(), proffession -> proffessionsList = proffession);

        LiveData<ArrayList<SelectionModal>> salary = viewModel.getSalaryLiveData();
        salary.observe(getViewLifecycleOwner(), salarylist -> salaryList = salarylist);

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

    private void init() {
        Loader loader = new Loader(getContext());
        progressDialog = loader.progress();
        bottomLists = new BottomLists(getContext(), this);
        binding.setClickers(this);
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        viewModel.getCountries();
        viewModel.getProffessions();
        viewModel.getSalaryData();

        getGenderList();
        getUserType();
        getDate();
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

    private void getDate() {
        viewModel.getDate();
    }

    private boolean validate() {
        boolean check = true;
        if (!hasText(binding.fname)) check = false;
        if (!hasText(binding.lname)) check = false;
        if (!hasText(binding.email)) check = false;
        if (!hasText(binding.mobile)) check = false;
        if (!hasText(binding.employer)) check = false;
        if (!hasText(binding.address1)) check = false;
        if (!hasText(binding.address2)) check = false;
        if (!hasText(binding.usertype)) check = false;
        if (!hasText(binding.dob)) check = false;
        if (!hasText(binding.gender)) check = false;
        if (!hasText(binding.idCountry)) check = false;
        if (!hasText(binding.nationality)) check = false;
        if (!hasText(binding.profession)) check = false;
        if (!hasText(binding.salary)) check = false;

        return check;
    }

    private void getUserType() {
        userTypeList = new ArrayList<>();
        SelectionModal modal1 = new SelectionModal("0", "INDIVIDUAL");
        userTypeList.add(modal1);

    }

    private void getGenderList() {
        genderList = new ArrayList<>();
        SelectionModal male = new SelectionModal("0", "MALE");
        SelectionModal female = new SelectionModal("1", "FEMALE");
        genderList.add(male);
        genderList.add(female);
    }

    private void updateDobLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        binding.dob.setText(sdf.format(myCalendar.getTime()));
        dob = Objects.requireNonNull(binding.dob.getText()).toString();
    }

    public void onUserTypeClicked(View view) {
        bottomLists.bottomViewSelection("User Type", userTypeList);
    }

    public void onGenderClicked(View view) {
        bottomLists.bottomViewSelection("Gender", genderList);
    }

    public void onDobClicked(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(getContext()), dobDate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(currentDateTime - 568025136000L);

        datePickerDialog.show();
//        new DatePickerDialog(Objects.requireNonNull(getContext()), dobDate, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void onNationalityClicked(View view) {
        bottomLists.bottomViewSelection("Nationality", countryList);
    }

    public void onRCountryClicked(View view) {
        bottomLists.bottomViewSelection("Countries", countryList);
    }

    public void onSalaryClicked(View view) {
        bottomLists.bottomViewSelection("Salary", salaryList);
    }

    public void onProfessionClicked(View view) {
        bottomLists.bottomViewSelection("Profession", proffessionsList);
    }

    public void onBackClicked(View view) {
        Intent intent = new Intent(getContext(), CheckUser.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public void onProceedClicked(View view) {
        if (validate()) {
            if (binding.mobile.getText().toString().length()!=8) {
                binding.mobile.setError("8 digit required");
            }else if (isValidEmail(binding.email.getText())) {
            Signup.userData.put("userType", userType);
            Signup.userData.put("fname", Objects.requireNonNull(binding.fname.getText()).toString());
            Signup.userData.put("mname", Objects.requireNonNull(binding.mname.getText()).toString());
            Signup.userData.put("lname", Objects.requireNonNull(binding.lname.getText()).toString());
            Signup.userData.put("email", Objects.requireNonNull(binding.email.getText()).toString());
            Signup.userData.put("mobile", Objects.requireNonNull(binding.mobile.getText()).toString());
            Signup.userData.put("address1", Objects.requireNonNull(binding.address1.getText()).toString());
            Signup.userData.put("address2", Objects.requireNonNull(binding.address2.getText()).toString());
            Signup.userData.put("idCountry", RcountryId);
            Signup.userData.put("nationality", nationalityID);
            Signup.userData.put("dob", dob);
            Signup.userData.put("gender", gender);
            Signup.userData.put("profession", professionId);
            Signup.userData.put("employer", Objects.requireNonNull(binding.employer.getText()).toString());
            Signup.userData.put("salary", salaryId);

                IDFragment idFragment = new IDFragment();
                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, idFragment).addToBackStack(null);
                fragmentTransaction.commit();
                fragmentTransaction.addToBackStack(null);

//            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
//            ft.replace(R.id.container, idFragment);
//            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//            ft.commit();

            } else{
                binding.email.setError("Invalid Email ID");
            }
        }
    }

    @Override
    public void onBottomSheetSelected(String name, String id, String tag) {
        switch (tag) {
            case "Nationality":
                nationalityID = id;
                binding.nationality.setText(name);
                break;
            case "Profession":
                professionId = id;
                binding.profession.setText(name);
                break;

            case "Salary":
                salaryId = id;
                binding.salary.setText(name);
                break;

            case "Countries":
                RcountryId = id;
                binding.idCountry.setText(name);
                break;
            case "User Type":
                userType = name;
                binding.usertype.setText(userType);
                break;

            case "Gender":
                gender = name;
                binding.gender.setText(gender);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        viewModel.clear();
        super.onDestroyView();
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}
