package io.thoughtbox.hamdan.utls;

public interface FingerAuthentication {
    void onSuccessfulFingerAuthentication(String androidKey);

    void onDeviceFailedToAuthenticate(String message);
}
