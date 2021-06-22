package io.thoughtbox.hamdan.viewModel;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import io.thoughtbox.hamdan.model.rewardsModel.RewardsResponseData;
import io.thoughtbox.hamdan.repos.RewardsRepo;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;

public class RewardsViewModel extends ViewModel {
    private RewardsRepo repo;

    public RewardsViewModel() {
        repo = new RewardsRepo();
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

    public MutableEventLiveData<Boolean> getIsSessionValid() {
        return repo.getIsSessionValid();
    }

    public void getRewards() {
        repo.getAllRewards();
    }

    public MutableEventLiveData<ArrayList<RewardsResponseData>> getRewardsLiveData() {
        return repo.getRewardsLiveData();
    }

}
