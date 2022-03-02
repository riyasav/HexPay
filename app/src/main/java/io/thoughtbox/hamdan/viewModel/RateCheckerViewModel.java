package io.thoughtbox.hamdan.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;

import io.thoughtbox.hamdan.model.rateModels.RateResponseData;
import io.thoughtbox.hamdan.repos.HomeRepo;
import io.thoughtbox.hamdan.repos.RateCheckerRepo;
import io.thoughtbox.hamdan.utls.MutableEventLiveData;

public class RateCheckerViewModel  extends AndroidViewModel {
    RateCheckerRepo rateCheckerRepo;


    public RateCheckerViewModel(@NonNull Application application) {
        super(application);
        rateCheckerRepo = new RateCheckerRepo();

    }

    public void getCheckerRate() {
        rateCheckerRepo.getCheckRaterRate();
    }

    public MutableEventLiveData<ArrayList<RateResponseData>> getTokenLessCheckRateLiveData() {
       return rateCheckerRepo.getTokenLessRateLiveData();
    }

   public MutableEventLiveData<String>  getLoaderError(){
        return rateCheckerRepo.getLoadingError();
   }

   public  MutableEventLiveData<Boolean> getIsSessionValid(){
        return rateCheckerRepo.getIsSessionValid();
   }



}
