package io.thoughtbox.hamdan.utls;

import android.app.Service;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.ActivityMainBinding;
import io.thoughtbox.hamdan.model.loginModel.OtpRequestModel;

public class OtpViewHandler implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    public MutableEventLiveData<String> otpLiveData = new MutableEventLiveData<>();
    private ActivityMainBinding mainBinding;
    private Context context;
    private Button otpbtn;
    private TextInputEditText mPinFirstDigitEditText;
    private TextInputEditText mPinSecondDigitEditText;
    private TextInputEditText mPinThirdDigitEditText;
    private TextInputEditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;
    private Button resendOtp;

    public OtpViewHandler(ActivityMainBinding mainBinding, Context context) {
        this.mainBinding = mainBinding;
        this.context = context;
        init();
    }

    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    public void clearFields() {
        mPinFirstDigitEditText.getText().clear();
        mPinSecondDigitEditText.getText().clear();
        mPinThirdDigitEditText.getText().clear();
        mPinForthDigitEditText.getText().clear();
        mPinHiddenEditText.getText().clear();
        mPinFirstDigitEditText.requestFocus();
    }

    private void init() {
        mPinFirstDigitEditText = mainBinding.otp.pinFirstEdittext;
        mPinSecondDigitEditText = mainBinding.otp.pinSecondEdittext;
        mPinThirdDigitEditText = mainBinding.otp.pinThirdEdittext;
        mPinForthDigitEditText = mainBinding.otp.pinForthEdittext;
        mPinHiddenEditText = mainBinding.otp.pinHiddenEdittext;
        setPINListeners();
//        otpbtn = findViewById(R.id.otp);
//        resendOtp =  mainBinding.otp.resend;
    }

    private String GetOtpValue() {
        return mPinFirstDigitEditText.getText().toString()
                + mPinSecondDigitEditText.getText().toString()
                + mPinThirdDigitEditText.getText().toString()
                + mPinForthDigitEditText.getText().toString();
    }

    public void hideSoftKeyboard(EditText editText) {
        if (editText == null)
            return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    public void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);


        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() == 0) {
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");

        } else if (s.length() == 2) {
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");

        } else if (s.length() == 3) {
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");

        } else if (s.length() == 4) {
            mPinForthDigitEditText.setText(s.charAt(3) + "");
            hideSoftKeyboard(mPinForthDigitEditText);
            verifyOTP(GetOtpValue());
        }
    }

    private void verifyOTP(String getOtpValue) {
        OtpRequestModel params = new OtpRequestModel("Login", getOtpValue, "");
        otpLiveData.setValue(getOtpValue);
    }


    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pin_first_edittext:

            case R.id.pin_second_edittext:

            case R.id.pin_third_edittext:

            case R.id.pin_forth_edittext:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0)
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));

                        return true;
                    }

                    break;

                default:
                    return false;
            }
        }

        return false;
    }


}
