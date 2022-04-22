package io.thoughtbox.hamdan.views;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;


import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.global.Universal;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityFeedbackBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.utls.Loader;

public class Feedback extends AppCompatActivity {
    ActivityFeedbackBinding binding;
    @Inject
    Dictionary dictionary;
    NotificationAlerts alerts;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback);
        init();
        binding.imageView2.setOnClickListener(view -> onBackPressed());
        binding.submit.setOnClickListener(view -> {
            if (!binding.editText.getText().toString().isEmpty() && binding.ratingBar.getRating() > 0) {
                sendMail("Greetings from " + Universal.getInstance().getLoginResponsedata().getUsername() + "\n" + binding.editText.getText().toString(), String.valueOf(binding.ratingBar.getRating()));
            } else {
                Toast.makeText(this, "rate to proceed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitFeedback(String feedback, String rating) {
        String[] emailList = {"hexpay@hamdanexchange.com"};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, emailList);
        intent.putExtra(Intent.EXTRA_SUBJECT, Universal.getInstance().getLoginResponsedata().getUsername() + " Rated " + rating);
        intent.putExtra(Intent.EXTRA_TEXT, "Greetings... \n" + feedback);
        startActivity(Intent.createChooser(intent, "Choose an email app"));
    }

    private void init() {
        checkInternet();
        alerts = new NotificationAlerts(this);
        DaggerApiComponents.create().inject(this);
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        binding.setLanguage(dictionary);
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
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), SettingsGrid.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }

    public void sendMail(String feedback, String rating) {
        final String username = "hexpay@hamdanexchange.com";
        final String password = "CYugx121";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.office365.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

//        Session session=Session.getDefaultInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("hexpay@hamdanexchange.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("hexpay@hamdanexchange.com"));
            message.setSubject(Universal.getInstance().getLoginResponsedata().getEmail() + "(" + Universal.getInstance().getLoginResponsedata().getIdno() + ")" + " Rated " + rating);
            message.setText(feedback);

//            MimeBodyPart messageBodyPart = new MimeBodyPart();
//
//            Multipart multipart = new MimeMultipart();
//
//            messageBodyPart = new MimeBodyPart();
//            String file = "path of file to be attached";
//            String fileName = "attachmentName";
//            DataSource source = new FileDataSource(file);
//            messageBodyPart.setDataHandler(new DataHandler(source));
//            messageBodyPart.setFileName(fileName);
//            multipart.addBodyPart(messageBodyPart);
//
//            message.setContent(multipart);

            //Using Async Task
            AsyncTaskFeedBackTransport asyncTask = new AsyncTaskFeedBackTransport();
            asyncTask.execute(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private class AsyncTaskFeedBackTransport extends AsyncTask<Message, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Message... message) {
            String result = "Feedback submitted Successfully";
            try {
                Transport.send(message[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
                result = e.getMessage();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
            progressDialog.dismiss();
            Toast.makeText(Feedback.this, status, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Feedback.this, SettingsGrid.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }

}
