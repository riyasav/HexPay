package io.thoughtbox.hamdan.alerts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Objects;

import io.thoughtbox.hamdan.Adapters.BottomSheetClickListner;
import io.thoughtbox.hamdan.Adapters.DetailedBottomSheet;
import io.thoughtbox.hamdan.Adapters.DetailedItemSelectionListener;
import io.thoughtbox.hamdan.Adapters.MasterChildAdapter;
import io.thoughtbox.hamdan.Adapters.SelectionBottomSheetAdapter;
import io.thoughtbox.hamdan.Adapters.SelectionListener;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.ListviewBottomsheetBinding;
import io.thoughtbox.hamdan.model.bankBenModel.DetailData;
import io.thoughtbox.hamdan.model.transferModel.SelectionModal;

public class BottomLists implements BottomSheetClickListner, DetailedBottomSheet {
    private Context context;
    private BottomSheetDialog mBottomSheetDialog;
    private UiInteractions uiInteractions;
    private SelectionListener selectionListener;
    private DetailedItemSelectionListener detailedItemSelectionListener;
    private String viewType = null;
    private MasterChildAdapter detailedAdapter;

    public BottomLists(Context context, SelectionListener selectionListener) {
//        DaggerApiComponents.create().inject(this);
        this.context = context;
        uiInteractions = new UiInteractions(context);
        this.selectionListener = selectionListener;
    }

    public BottomLists(Context context, DetailedItemSelectionListener detailedItemSelectionListener, String viewType) {
        this.context = context;
        this.detailedItemSelectionListener = detailedItemSelectionListener;
        this.viewType = viewType;
        uiInteractions = new UiInteractions(context);
    }

    public void bottomViewSelection(String tag, ArrayList<SelectionModal> datalist) {
        SelectionBottomSheetAdapter adapter;
        mBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);

        ListviewBottomsheetBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.listview_bottomsheet, null, false);
        dialogBinding.setClickers(uiInteractions);
        Objects.requireNonNull(mBottomSheetDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setContentView(dialogBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);

        dialogBinding.title.setText(tag);

        RecyclerView recyclerView = dialogBinding.list;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        if (datalist != null) {
            adapter = new SelectionBottomSheetAdapter(this, datalist, tag);
            recyclerView.setAdapter(adapter);
            final SearchView searchView = mBottomSheetDialog.findViewById(R.id.search_view);
            searchView.setMaxWidth(Integer.MAX_VALUE);

            // listening to search query text change
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // filter recycler view when query submitted
                    adapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    adapter.getFilter().filter(query);
                    return false;
                }
            });

        } else {
            Toast.makeText(context, "No Data available", Toast.LENGTH_SHORT).show();
        }

        mBottomSheetDialog.show();
    }

    public void childBottomViewSelection(String tag, ArrayList<DetailData> detaildatalist) {
        SelectionBottomSheetAdapter adapter;
        mBottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialog);

        ListviewBottomsheetBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.listview_bottomsheet, null, false);
        dialogBinding.setClickers(uiInteractions);
        Objects.requireNonNull(mBottomSheetDialog.getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mBottomSheetDialog.setContentView(dialogBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);

        dialogBinding.title.setText(tag);

        RecyclerView recyclerView = dialogBinding.list;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        if (detaildatalist != null) {
            detailedAdapter = new MasterChildAdapter(this, detaildatalist, tag);
            recyclerView.setAdapter(detailedAdapter);


//            SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
            final SearchView searchView = mBottomSheetDialog.findViewById(R.id.search_view);
//          searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            if (searchView != null) {
                searchView.setMaxWidth(Integer.MAX_VALUE);
            }

            // listening to search query text change
            assert searchView != null;
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // filter recycler view when query submitted
                    detailedAdapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    detailedAdapter.getFilter().filter(query);
                    return false;
                }
            });

        } else {
            Toast.makeText(context, "No Data available", Toast.LENGTH_SHORT).show();
        }

        mBottomSheetDialog.show();
    }

    @Override
    public void BottomItemClickListner(String name, String Id, String Tag) {
        mBottomSheetDialog.dismiss();
        selectionListener.onBottomSheetSelected(name, Id, Tag);
    }

    @Override
    public void BottomDetailedItemClickListner(DetailData detailData, String tag) {
        mBottomSheetDialog.dismiss();
        detailedItemSelectionListener.onBottomSheetDetailedItemSelected(detailData, tag);
    }

    public class UiInteractions {
        Context context;

        public UiInteractions(Context context) {
            this.context = context;
        }

        public void onCloseClicked(View view) {
            mBottomSheetDialog.dismiss();
        }
    }
}
