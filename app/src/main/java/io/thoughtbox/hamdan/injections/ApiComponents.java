package io.thoughtbox.hamdan.injections;

import dagger.Component;
import io.thoughtbox.hamdan.Adapters.BankTransferAdapter;
import io.thoughtbox.hamdan.Adapters.CashPickUpAdapter;
import io.thoughtbox.hamdan.Adapters.RateAdapter;
import io.thoughtbox.hamdan.Adapters.ReportAdapter;
import io.thoughtbox.hamdan.DashBoard;
import io.thoughtbox.hamdan.MainActivity;
import io.thoughtbox.hamdan.SplashScreen;

import io.thoughtbox.hamdan.repos.RateCheckerRepo;
import io.thoughtbox.hamdan.repos.RewardsRepo;
import io.thoughtbox.hamdan.utls.AuthenticationHelper;
import io.thoughtbox.hamdan.alerts.AuthenticationView;
import io.thoughtbox.hamdan.alerts.BottomLists;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.repos.AddBenefciaryRepo;
import io.thoughtbox.hamdan.repos.AddFastCashBenRepo;
import io.thoughtbox.hamdan.repos.BankBeneficiaryRepo;
import io.thoughtbox.hamdan.repos.BiometricRepo;
import io.thoughtbox.hamdan.repos.CheckUserIdRepo;
import io.thoughtbox.hamdan.repos.HomeRepo;
import io.thoughtbox.hamdan.repos.InvoiceRepo;
import io.thoughtbox.hamdan.repos.LoginRepo;
import io.thoughtbox.hamdan.repos.NotificationRepo;
import io.thoughtbox.hamdan.repos.OtpRepo;
import io.thoughtbox.hamdan.repos.PaymentRepo;
import io.thoughtbox.hamdan.repos.RegisterRepo;
import io.thoughtbox.hamdan.repos.ReportRepo;
import io.thoughtbox.hamdan.repos.SettingsRepo;
import io.thoughtbox.hamdan.repos.SplashRepo;
import io.thoughtbox.hamdan.repos.TransferRepo;
import io.thoughtbox.hamdan.services.ServiceRequest;
import io.thoughtbox.hamdan.transfers.BankTransfer;
import io.thoughtbox.hamdan.transfers.CashPickUp;
import io.thoughtbox.hamdan.views.ChangePassword;
import io.thoughtbox.hamdan.views.Feedback;
import io.thoughtbox.hamdan.views.Language;
import io.thoughtbox.hamdan.views.ManageBiometric;
import io.thoughtbox.hamdan.views.Profile;
import io.thoughtbox.hamdan.views.PushNotifications;
import io.thoughtbox.hamdan.views.RateChecker;
import io.thoughtbox.hamdan.views.Rates;
import io.thoughtbox.hamdan.views.SettingsGrid;
import io.thoughtbox.hamdan.views.addBen.AddBenBank;
import io.thoughtbox.hamdan.views.addBen.AddBenCash;
import io.thoughtbox.hamdan.views.branches.BranchPager;
import io.thoughtbox.hamdan.views.remittance.TransferView;


@Component(modules = {ApiModule.class, DictionaryModule.class})
public interface ApiComponents {

    void inject(ServiceRequest serviceRequest);

    void inject(LoginRepo loginRepo);

    void inject(HomeRepo homeRepo);

    void inject(ChangePassword changePassword);

    void inject(BankBeneficiaryRepo bankBeneficiaryRepo);

    void inject(TransferRepo transferRepo);

    void inject(OtpRepo otpRepo);

    void inject(PaymentRepo paymentRepo);

    void inject(InvoiceRepo invoiceRepo);

    void inject(AddBenefciaryRepo addBenefciaryRepo);

    void inject(AddFastCashBenRepo addFastCashBenRepo);

    void inject(ReportRepo reportRepo);

    void inject(SettingsRepo settingsRepo);

    void inject(RegisterRepo registerRepo);

    void inject(BankTransfer bankTransfer);

    void inject(DashBoard dashBoardActivity);

    void inject(BankTransferAdapter bankTransferAdapter);

    void inject(CashPickUp cashPickUp);

    void inject(CashPickUpAdapter cashPickUpAdapter);

    void inject(ReportAdapter reportAdapter);

    void inject(RateAdapter rateAdapter);

    void inject(AuthenticationView authenticationView);

    void inject(AuthenticationHelper authenticationHelper);

    void inject(BottomLists BottomLists);

    void inject(NotificationAlerts notificationAlerts);

    void inject(Language language);

    void inject(ManageBiometric manageBiometric);

    void inject(BiometricRepo biometricRepo);

    void inject(NotificationRepo notificationRepo);

    void inject(PushNotifications pushNotifications);

    void inject(Feedback feedback);

    void inject(CheckUserIdRepo checkUserIdRepo);

    void inject(AddBenBank addBenBank);

    void inject(Rates rates);

    void inject(AddBenCash addBenCash);

    void inject(SplashRepo splashRepo);

    void inject(SplashScreen splashScreen);

    void inject(MainActivity mainActivity);

    void inject(TransferView transferView);

    void inject(RewardsRepo rewardsRepo);

    void inject(SettingsGrid settingsGrid);

    void inject(Profile profile);

    void inject(BranchPager branchPager);

    void inject(RateCheckerRepo rateCheckerRepo);
}