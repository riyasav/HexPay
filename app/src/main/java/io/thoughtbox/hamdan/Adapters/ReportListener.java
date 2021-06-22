package io.thoughtbox.hamdan.Adapters;

import io.thoughtbox.hamdan.model.reportModel.ReportResponseData;

public interface ReportListener {
    void onItemClick(ReportResponseData responseData);

    void onRepeatClick(ReportResponseData responseData);
}
