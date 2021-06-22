package io.thoughtbox.hamdan.transfers;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import io.thoughtbox.hamdan.injections.DaggerApiComponents;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.inject.Inject;

import io.thoughtbox.hamdan.Adapters.BankTransferAdapter;
import io.thoughtbox.hamdan.Adapters.OnItemClicked;
import io.thoughtbox.hamdan.DashBoard;
import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Loader;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.ActivityBankTransferBinding;
import io.thoughtbox.hamdan.databinding.ConfirmDeleteBinding;
import io.thoughtbox.hamdan.global.Dictionary;

import io.thoughtbox.hamdan.model.bankBenModel.BankBeneficiaryResponseData;
import io.thoughtbox.hamdan.viewModel.BankBeneficiaryViewModel;
import io.thoughtbox.hamdan.views.addBen.AddBenBank;
import io.thoughtbox.hamdan.views.remittance.TransferView;

public class BankTransfer extends AppCompatActivity implements OnItemClicked {
    @Inject
    Dictionary dictionary;
    private BankBeneficiaryViewModel bankBeneficiaryViewModel;
    private ActivityBankTransferBinding bankTransferBinding;
    private Dialog progressDialog;
    private BankTransferAdapter adapter;
    private RecyclerView recyclerView;
    private ArrayList<BankBeneficiaryResponseData> benList;
    private NotificationAlerts alerts;
    BottomNavigationView bottomNavigationView;
    boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bankTransferBinding = DataBindingUtil.setContentView(this, R.layout.activity_bank_transfer);
        bankTransferBinding.setLifecycleOwner(this);
        init();

        LiveData<Boolean> isSessionValid = bankBeneficiaryViewModel.getSessionStatus();
        isSessionValid.observe(this, isTokenValid -> {
            if (!isTokenValid) {
                alerts.timeOut();
            }
        });

        bankTransferBinding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
//                deleteBenef(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        LiveData<Boolean> isLoadingData = bankBeneficiaryViewModel.getIsLoading();
        isLoadingData.observe(this, isLoading -> {
            if (isLoading) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        });
        bankBeneficiaryViewModel.getLoadingError().observe(this, errorMsg -> {
            if (errorMsg != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                Toast.makeText(this, "" + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        LiveData<ArrayList<BankBeneficiaryResponseData>> bankBenListLiveData = bankBeneficiaryViewModel.getBankBenData();
        bankBenListLiveData.observe(this, bankBeneficiaryResponseData -> {
            benList = new ArrayList<>();
            benList = bankBeneficiaryResponseData;
            adapter = new BankTransferAdapter(this, benList);
            recyclerView.setAdapter(adapter);
        });

        LiveData<Boolean> delete = bankBeneficiaryViewModel.getIsBenDeleted();
        delete.observe(this, isdeleted -> {
            if (isdeleted) {
                finish();
                startActivity(getIntent());
            } else {
                Toast.makeText(this, "deletion failed", Toast.LENGTH_SHORT).show();
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    if (item.getItemId() == R.id.addben) {
                        Intent intent = new Intent(this, AddBenBank.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    } else if (item.getItemId() == R.id.search) {
                        if (isVisible) {
                            isVisible=false;
                            bankTransferBinding.searchBar.setVisibility(View.GONE);
                        } else {
                            isVisible=true;
                            bankTransferBinding.searchBar.setVisibility(View.VISIBLE);
                        }

                    }
                    return true;
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
        bankBeneficiaryViewModel.deleteBankBen(params);
    }

    private void init() {
        alerts = new NotificationAlerts(this);
        checkInternet();
        DaggerApiComponents.create().inject(this);
        Loader loader = new Loader(this);
        progressDialog = loader.progress();
        bankTransferBinding.setClickers(new UiInteractions());
        bankTransferBinding.setLanguage(dictionary);
        bankBeneficiaryViewModel = new ViewModelProvider(this).get(BankBeneficiaryViewModel.class);
        getBankBeneficiaryList();
        recyclerView = bankTransferBinding.rv;
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();
        bottomNavigationView = bankTransferBinding.bottomNavigation;
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

    private void getBankBeneficiaryList() {
        bankBeneficiaryViewModel.getBankBenList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bankBeneficiaryViewModel.clear();
    }

    @Override
    public void onBenItemClicked(BankBeneficiaryResponseData bankBeneficiaryResponseData) {
        Intent intent = new Intent(this, TransferView.class);
        intent.putExtra("Bank", bankBeneficiaryResponseData);
        intent.putExtra("isBank", true);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), DashBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        super.onBackPressed();
    }

    public class UiInteractions {
        public UiInteractions() {
        }
        public void onBackClicked(View view) {
            onBackPressed();
        }

    }
}