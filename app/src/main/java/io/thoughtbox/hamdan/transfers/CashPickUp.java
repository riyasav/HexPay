package io.thoughtbox.hamdan.transfers;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import io.thoughtbox.hamdan.Adapters.CashPickUpAdapter;
import io.thoughtbox.hamdan.Adapters.OnCashPickUpClicked;
import io.thoughtbox.hamdan.DashBoard;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityCashPickUpBinding;
import io.thoughtbox.hamdan.databinding.ConfirmDeleteBinding;
import io.thoughtbox.hamdan.global.Dictionary;
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import io.thoughtbox.hamdan.model.cashPickUpModel.CashPickUpResponseData;
import io.thoughtbox.hamdan.viewModel.BankBeneficiaryViewModel;
import io.thoughtbox.hamdan.views.addBen.AddBenCash;
import io.thoughtbox.hamdan.views.remittance.TransferView;

public class CashPickUp extends AppCompatActivity implements OnCashPickUpClicked {

    @Inject
    Dictionary dictionary;
    private BankBeneficiaryViewModel cashPickupBeneficiaryViewModel;
    private ActivityCashPickUpBinding cashPickupBenBinding;
    private Dialog progressDialog;
    private CashPickUpAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<CashPickUpResponseData> benList;
    private NotificationAlerts alerts;
    BottomNavigationView bottomNavigationView;
    boolean isVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cashPickupBenBinding = DataBindingUtil.setContentView(this, R.layout.activity_cash_pick_up);
        cashPickupBenBinding.setLifecycleOwner(this);
        init();


        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    if (item.getItemId() == R.id.addben) {
                        Intent intent = new Intent(this, AddBenCash.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    } else if (item.getItemId() == R.id.search) {
                        if (isVisible) {
                            isVisible=false;
                            cashPickupBenBinding.searchBar.setVisibility(View.GONE);
                        } else {
                            isVisible=true;
                            cashPickupBenBinding.searchBar.setVisibility(View.VISIBLE);
                        }

                    }
                    return true;
                });

        LiveData<Boolean> isSessionValid = cashPickupBeneficiaryViewModel.getSessionStatus();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });

        cashPickupBenBinding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                showConfirmationDialog(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);


        LiveData<Boolean> delete = cashPickupBeneficiaryViewModel.getCashBenDeleted();
        delete.observe(this, isdeleted -> {
            if (isdeleted) {
                finish();
                startActivity(getIntent());
            } else {
                Toast.makeText(this, "deletion failed", Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<Boolean> isLoadingData = cashPickupBeneficiaryViewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
                Log.d("Loader", "stated new loader");
            } else {
                progressDialog.dismiss();
                Log.d("Loader", "stopped a loader");
            }
        });

        cashPickupBeneficiaryViewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<ArrayList<CashPickUpResponseData>> cashpickupBenListLiveData = cashPickupBeneficiaryViewModel.getCashPickUpBenData();
        cashpickupBenListLiveData.observe(this, cashPickUpBeneficiaryResponseData -> {
            benList = new ArrayList<>();
            benList = cashPickUpBeneficiaryResponseData;
            if (benList!=null && benList.size()>0){
                adapter = new CashPickUpAdapter(this, benList);
                recyclerView.setAdapter(adapter);
            }

        });
    }

    @Override
    public void onItemclicked(CashPickUpResponseData cashPickUpResponseData) {
        Intent intent = new Intent(this, TransferView.class);
        intent.putExtra("CashPickUp", cashPickUpResponseData);
        intent.putExtra("isBank", false);
        startActivity(intent);
    }

    private void init() {
        checkInternet();
        alerts = new NotificationAlerts(this);
        DaggerApiComponents.create().inject(this);
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        UiInteractions uiInteractions = new UiInteractions();
        cashPickupBenBinding.setClickers(uiInteractions);
        cashPickupBenBinding.setLanguage(dictionary);
        cashPickupBeneficiaryViewModel = new ViewModelProvider(this).get(BankBeneficiaryViewModel.class);
        getCashPickUpBenificiaryList();
        recyclerView = cashPickupBenBinding.rv;
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();
        bottomNavigationView = cashPickupBenBinding.bottomNavigation;
    }

    private void checkInternet() {
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(this);
        connectionLiveData.observe(this, connection -> {
            if (!connection.getIsConnected()) {
                alerts.noInternet();
            }
            if (connection.getIsVpnConnected()) {
                Toast.makeText(this, "VPN Connected", Toast.LENGTH_SHORT).show();
                alerts.vpnAlert();
            } else {
                alerts.dismissVpnDialog();
            }
        });
    }

    private void showConfirmationDialog(int position) {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        ConfirmDeleteBinding successDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.confirm_delete, null, false);
        mBottomSheetDialog.setContentView(successDialogBinding.getRoot());
        mBottomSheetDialog.setCancelable(false);
        successDialogBinding.cancel.setOnClickListener(view -> mBottomSheetDialog.dismiss());
        successDialogBinding.done.setOnClickListener(view -> deleteBenef(position));
        mBottomSheetDialog.show();
    }

    private void deleteBenef(int pos) {
        String id = benList.get(pos).getId();
        JSONObject params = new JSONObject();
        try {
            params.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cashPickupBeneficiaryViewModel.deleteCashBen(params);
    }

    private void getCashPickUpBenificiaryList() {
        cashPickupBeneficiaryViewModel.getCashPickupBenList();
    }

    @Override
    protected void onDestroy() {
        cashPickupBeneficiaryViewModel.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    public class UiInteractions {


        public UiInteractions() {
        }

        public void onBackClicked(View view) {
            onBackPressed();
        }

        public void onAddBenClicked(View view) {
            Intent intent = new Intent(getApplicationContext(), AddBenCash.class);
            startActivity(intent);
        }
    }


}