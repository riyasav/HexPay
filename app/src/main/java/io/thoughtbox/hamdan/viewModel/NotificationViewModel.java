package io.thoughtbox.hamdan.viewModel;

import androidx.lifecycle.ViewModel;

import org.json.JSONObject;

import java.util.ArrayList;

import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.notificationModel.NotificationResponseData;
import io.thoughtbox.hamdan.repos.NotificationRepo;

public class NotificationViewModel extends ViewModel {
    private NotificationRepo repo;

    public NotificationViewModel() {
        repo = new NotificationRepo();
    }

    public void clear() {
        repo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return repo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return repo.getLoadingError();
    }

    public MutableEventLiveData<Boolean> getSessionStatus() {
        return repo.getIsSessionValid();
    }

    public void getNotification(JSONObject params) {
        repo.getNotification(params);
    }

    public MutableEventLiveData<ArrayList<NotificationResponseData>> getNotificationLiveData() {
        return repo.getNotificationLiveData();
    }

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return repo.getIsSessionValid();
    }
}
