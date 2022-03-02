package io.thoughtbox.hamdan.viewModel;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import io.thoughtbox.hamdan.model.BannerResponse;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;
import io.thoughtbox.hamdan.model.dictionaryModel.DictionaryResponseData;
import io.thoughtbox.hamdan.repos.SplashRepo;

public class SplashViewModel extends ViewModel {
    private SplashRepo splashRepo;

    public SplashViewModel() {
        splashRepo = new SplashRepo();
    }

    public void clear() {
        splashRepo.clear();
    }

    public MutableEventLiveData<Boolean> getIsLoading() {
        return splashRepo.getIsLoading();
    }

    public MutableEventLiveData<String> getLoadingError() {
        return splashRepo.getLoadingError();
    }


    public void getDictionry(String lang) {
        splashRepo.getDictionary(lang);
    }

    public MutableEventLiveData<ArrayList<DictionaryResponseData>> getDictionaryLiveData() {
        return splashRepo.getDictionaryLiveData();
    }

    public void getBannerData(){
        splashRepo.getBannerData();
    }
    public MutableEventLiveData<BannerResponse> getBannerLiveData() {
        return splashRepo.getBannerLiveData();
    }


}
