package io.thoughtbox.hamdan.utls;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by Muhammed Riyas A V on 7/29/2018.
 */

public class FingerIdentification implements FingerAuthenticationCallBack {
    private static final String KEY_NAME = "RemitranzKey";
    FingerAuthentication onAuthenticate;
    BottomSheetDialog bottomSheetDialog;
    FingerprintHandler helper;
    private Cipher cipher;
    private KeyStore keyStore;
    private Context context;
    private String errorMessage;
    private boolean is_keygenerated = false;

    public FingerIdentification(Context context) {
        this.context = context;
        onAuthenticate = (FingerAuthentication) context;
    }

    public FingerIdentification(Context context, FingerAuthentication onAuthenticate) {
        this.context = context;
        this.onAuthenticate = onAuthenticate;
    }

    public FingerIdentification(Context context, BottomSheetDialog bottomSheetDialog) {
        this.context = context;
        this.bottomSheetDialog = bottomSheetDialog;
        onAuthenticate = (FingerAuthentication) context;
    }

    public void InitFinger() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            helper = new FingerprintHandler(this, context);
            //Get an instance of KeyguardManager and FingerprintManager//
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);

            //Check whether the device has a fingerprint sensor//
            if (!fingerprintManager.isHardwareDetected()) {
                // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
                errorMessage = "Your device doesn't support fingerprint authentication";
                onAuthenticate.onDeviceFailedToAuthenticate(errorMessage);
                return;
            }
            //Check whether the user has granted your app the USE_FINGERPRINT permission//
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // If your app doesn't have this permission, then display the following text//
                errorMessage = "Please enable the fingerprint permission";
                onAuthenticate.onDeviceFailedToAuthenticate(errorMessage);
                return;
            }

            //Check that the user has registered at least one fingerprint//
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // If the user hasn’t configured any fingerprints, then display the following message//
                errorMessage = "No fingerprint configured. Please register at least one fingerprint in your device's Settings";
                onAuthenticate.onDeviceFailedToAuthenticate(errorMessage);
                return;
            }

            //Check that the lockscreen is secured//
            if (!keyguardManager.isKeyguardSecure()) {
                // If the user hasn’t secured their lockscreen with a PIN password or pattern, then display the following text//
                errorMessage = "Please enable lockscreen security in your device's Settings";
                onAuthenticate.onDeviceFailedToAuthenticate(errorMessage);
            } else {
                try {
                    generateKey();
                    is_keygenerated = true;
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }
                if (is_keygenerated) {
                    if (initCipher()) {
                        //If the cipher is initialized successfully, then create a CryptoObject instance//
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                        // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                        // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                        helper.startAuth(fingerprintManager, cryptoObject);
                    }
                }

            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier ("AndroidKeystore")
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new

                    //Specify the operation(s) this key can be used for//

                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    @Override
    public void onFingerAuthenticated() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
        }
        try {
            @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            onAuthenticate.onSuccessfulFingerAuthentication(android_id);
        } catch (Exception e) {
            Log.e("fp", e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFingerFailedToAuthenticate() {
        if (bottomSheetDialog != null) {
            bottomSheetDialog.dismiss();
        }
        onAuthenticate.onDeviceFailedToAuthenticate("");

    }

    public void stopBiometricSensor() {
        helper.stopFingerAuth();
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }
}
