package io.thoughtbox.hamdan.utls;

public interface Constants {


    String BaseURL = "https://uatapi.hamdanexchange.com/";

//    String BaseURL = "https://ilxapijadeed.thoughtbox.io/";

    String PaymentGateWayURL = BaseURL + "onlinepayments/processpayment";

    String CheckUpdate = BaseURL + "deviceversion/getbyplatform";

    int MEDIA_TYPE_IMAGE = 100;

    int PHOTO_CAPTURE_CODE = 150;

    int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 101;

    int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 201;

    String Terms = "https://www.thoughtbox.io/AlJadeedExT&C.html";

    String Privacy = "https://www.thoughtbox.io/AlJadeedExPrivacyPolicy.html";

    String Faq = "https://www.thoughtbox.io/AlJadeedExFAQ.html";

    String Support = "https://aljadeedexchange.org/#contactus";

    String AboutUs = "https://aljadeedexchange.org";

    String logo = "https://aljadeedexchange.org";

    int WifiData = 1;

    int MobileData = 2;

    int VPN = 3;

}
