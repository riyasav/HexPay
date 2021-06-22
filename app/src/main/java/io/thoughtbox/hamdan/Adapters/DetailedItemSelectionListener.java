package io.thoughtbox.hamdan.Adapters;

import io.thoughtbox.hamdan.model.bankBenModel.DetailData;

public interface DetailedItemSelectionListener {
    void onBottomSheetDetailedItemSelected(DetailData detailData, String tag);
}
