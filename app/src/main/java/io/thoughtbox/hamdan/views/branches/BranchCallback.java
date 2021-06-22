package io.thoughtbox.hamdan.views.branches;


import io.thoughtbox.hamdan.model.mapModel.Data;

public interface BranchCallback {
    void onCallClicked(String contact);
    void onLocateClicked(Data locationDetails);
}
