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

import javax.inject.Inject;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.alerts.PassOtpPin;
import io.thoughtbox.hamdan.databinding.PinOtpSheetBinding;
import io.thoughtbox.hamdan.databinding.TokenlessOtpSheetBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;

public class AuthenticationHelper implements View.OnFocusChangeListener, View.OnKeyListener, TextWatcher {
    PassOtpPin passOtpPin;
    @Inject
    Dictionary dictionary;
    private PinOtpSheetBinding mainBinding;
    TokenlessOtpSheetBinding tokenLessBinding;

    private Context context;
    private Button otpbtn;
    private TextInputEditText mPinFirstDigitEditText;
    private TextInputEditText mPinSecondDigitEditText;
    private TextInputEditText mPinThirdDigitEditText;
    private TextInputEditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;
    private Button resendOtp;


    public AuthenticationHelper(PinOtpSheetBinding mainBinding, Context context, PassOtpPin passOtpPin) {
        DaggerApiComponents.create().inject(this);
        this.mainBinding = mainBinding;
        this.context = context;
        this.passOtpPin = passOtpPin;
        init();
    }

    public AuthenticationHelper(PinOtpSheetBinding mainBinding, Context context, PassOtpPin passOtpPin, String mode) {
        this.mainBinding = mainBinding;
        this.context = context;
        this.passOtpPin = passOtpPin;
        init2();
    }

    public AuthenticationHelper(TokenlessOtpSheetBinding tokenLessBinding, Context context, PassOtpPin passOtpPin, String mode) {
        this.tokenLessBinding = tokenLessBinding;
        this.context = context;
        this.passOtpPin = passOtpPin;
        init3();
    }

    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    private void init2() {
        String MODE = "OTP";
        switch (MODE) {
            case "Fixed":
                mainBinding.titileLblOtp.setText("fixedPin");
                mainBinding.otpMsg.setText("Provide the four digit fixed pin you created.");
                mainBinding.resend.setVisibility(View.GONE);
                break;
            case "OTP":
                mainBinding.titileLblOtp.setText("OTP");
                mainBinding.otpMsg.setText(R.string.otp_message);
                mainBinding.resend.setVisibility(View.VISIBLE);
                break;
        }
        mPinFirstDigitEditText = mainBinding.pinFirstEdittext;
        mPinSecondDigitEditText = mainBinding.pinSecondEdittext;
        mPinThirdDigitEditText = mainBinding.pinThirdEdittext;
        mPinForthDigitEditText = mainBinding.pinForthEdittext;
        mPinHiddenEditText = mainBinding.pinHiddenEdittext;
        setPINListeners();
//        otpbtn = findViewById(R.id.otp);
//        resendOtp =  mainBinding.otp.resend;
    }

    private void init3() {
        String MODE = "OTP";
        switch (MODE) {
            case "Fixed":
                tokenLessBinding.titileLblOtp.setText("fixedPin");
                tokenLessBinding.otpMsg.setText("Provide the four digit fixed pin you created.");
                tokenLessBinding.resend.setVisibility(View.GONE);
                break;
            case "OTP":
                tokenLessBinding.titileLblOtp.setText("OTP");
                tokenLessBinding.otpMsg.setText(R.string.otp_message);
                tokenLessBinding.resend.setVisibility(View.VISIBLE);
                break;
        }
        mPinFirstDigitEditText = tokenLessBinding.pinFirstEdittext;
        mPinSecondDigitEditText = tokenLessBinding.pinSecondEdittext;
        mPinThirdDigitEditText = tokenLessBinding.pinThirdEdittext;
        mPinForthDigitEditText = tokenLessBinding.pinForthEdittext;
        mPinHiddenEditText = tokenLessBinding.pinHiddenEdittext;
        setPINListeners();
//        otpbtn = findViewById(R.id.otp);
//        resendOtp =  mainBinding.otp.resend;
    }

    private void init() {
        String MODE = Universal.getInstance().getLoginResponsedata().getPinmode();
        switch (MODE) {
            case "Fixed":
                mainBinding.titileLblOtp.setText(dictionary.get("fixedPin"));
                mainBinding.otpMsg.setText(dictionary.get("providePIN"));
                mainBinding.title2.setText(dictionary.get("verifyrPin"));
                mainBinding.resend.setVisibility(View.GONE);
                break;
            case "OTP":
                mainBinding.titileLblOtp.setText(dictionary.get("otp"));
                mainBinding.otpMsg.setText(dictionary.get("provideOTP"));
                mainBinding.title2.setText(dictionary.get("verifyOTP"));
                mainBinding.resend.setVisibility(View.VISIBLE);


                break;
        }
        mPinFirstDigitEditText = mainBinding.pinFirstEdittext;
        mPinSecondDigitEditText = mainBinding.pinSecondEdittext;
        mPinThirdDigitEditText = mainBinding.pinThirdEdittext;
        mPinForthDigitEditText = mainBinding.pinForthEdittext;
        mPinHiddenEditText = mainBinding.pinHiddenEdittext;
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
        passOtpPin.onPinListener(getOtpValue);
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

    public void clearFields() {
            mPinFirstDigitEditText.getText().clear();
            mPinSecondDigitEditText.getText().clear();
            mPinThirdDigitEditText.getText().clear();
            mPinForthDigitEditText.getText().clear();
            mPinHiddenEditText.getText().clear();
            mPinFirstDigitEditText.requestFocus();
    }
}
