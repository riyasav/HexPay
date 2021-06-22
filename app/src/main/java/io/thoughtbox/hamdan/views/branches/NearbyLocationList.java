package io.thoughtbox.hamdan.views.branches;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Objects;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.alerts.NotificationAlerts;
import io.thoughtbox.hamdan.databinding.FragmentNearbyLocationListBinding;
import io.thoughtbox.hamdan.model.mapModel.Data;
import io.thoughtbox.hamdan.utls.ConnectionLiveData;
import io.thoughtbox.hamdan.utls.Loader;


public class NearbyLocationList extends Fragment implements BranchCallback, LocationClickListener {
    FragmentNearbyLocationListBinding binding;
    private NotificationAlerts alerts;

    private RecyclerView recyclerView;
    private ArrayList<Data> locationDataList;
    String ContactNumber;
    LocationAdapter adapter;
    LocationClickListener locClickListener;

    public NearbyLocationList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nearby_location_list, container, false);
        binding.setLifecycleOwner(this);
        binding.setClickers(this);
        init();



        binding.search.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        return binding.getRoot();
    }

    private void init() {
        checkInternet();
        locClickListener = this;
//        routerInterface = (RouterInterface) this;
        alerts = new NotificationAlerts(getContext());
        Loader loader = new Loader(getContext());


        recyclerView = binding.rv;
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.hasFixedSize();
        getBranchLocation();
    }

    private void getBranchLocation() {
        locationDataList=new ArrayList<>();
        locationDataList.add(new Data("HEAD OFFICE", 23.5935453571482, 58.4269483147075, "Corporate Office: 403, 4th Floor, Bank Sohar Bldg., Dohat Al Adab Street, Al-Khuwair, Muscat, Sultanate of Oman","91455455"));
        locationDataList.add(new Data("AL KHUWAIR", 23.591821, 58.428739, "Near Rawasco Hypermarket, Opp. The platinum Hotel, Alkhuwiar, Muscat", "99311695"));
        locationDataList.add(new Data("ALKHUWAIR DOHAT AL ADAB STREET", 23.5942655, 58.4239449, "Near Al Karama Hypermarket , Alkhuwair, Muscat", "99658287"));
        locationDataList.add(new Data("RUWI", 23.595111, 58.54507, "Mwasalat (ONTC) Bus Stand, Ruwi, Muscat", "99102412"));
        locationDataList.add(new Data("RUWI HIGH STREET", 23.593644, 58.544668, "Opp.Old Police Station Building, Ruwi High Street, Ruwi, Muscat", "99460091"));
        locationDataList.add(new Data("CITYCENTER SEEB", 23.599698, 58.248387, "Next to Carrefour Counter, Muscat City Center Seeb", "99102408"));
        locationDataList.add(new Data("SEEB SOUQ", 23.682115, 58.186078, "Opp. Bank Muscat, Seeb Souq, Seeb, Muscat", "99849409"));
        locationDataList.add(new Data("MABELAH", 23.662302, 58.129759, "Behind Bank Dhofar, Souq al Tijari , Mabelah, Muscat", "99102415"));
        locationDataList.add(new Data("BARKA SANAYYA", 23.615778, 57.86575, "Next to Oman oil and Bank Muscat, Barka Sanayya, Barka", "98568022"));
        locationDataList.add(new Data("MISFAH", 23.49455, 58.24972, "Opp Suhail Bhavan logistics, Misfa Industrial Area, Wadi Saal al Misfa, Muscat", "99879083"));
        locationDataList.add(new Data("MUSANNAH", 23.745632, 57.621987, "Musannah souq, Tareef, Musannah", "98049113"));
        locationDataList.add(new Data("FALAJ", 24.423306, 56.611295, "Muscat Road, Falaj Roundabout , Falaj al Qabail", "99102409"));
        locationDataList.add(new Data("IBRA", 22.716864, 58.528204, "Near AlFayha Hypermarket, Alayat, Ibra", "98049112"));
        locationDataList.add(new Data("SINAW", 22.504406, 58.03258, "Sinaw Roundabout, Near Wally office, Sinaw", "93893714"));
        locationDataList.add(new Data("IBRI", 23.21764, 56.489352, "Inside Ramez Hypermarket, Ibri", "99483971"));
        locationDataList.add(new Data("ADAM", 22.388266, 57.526433, "Next to Bank Muscat and Bank Dhofar, Adam Souq", "93608842"));
        locationDataList.add(new Data("KARSHA", 22.841535, 57.543565, "Near Karsha Round About, Karsha Industrial Area, Karsha", "98089306"));
        locationDataList.add(new Data("LEKHWAIR", 22.828889, 55.321167, "Business Center Building, Lekhwair pdo, Lekhwair PAC", "91298197"));
        locationDataList.add(new Data("CITYCENTER SUR", 22.576569, 59.508619, "Shop No 7, Opp to  Careefour, Sur City Center, Sur", "92657486"));
        locationDataList.add(new Data("AL ASHKARA", 21.849305, 59.568842, "Near HSBC ATM, Al Ashkara", "99311846"));
        locationDataList.add(new Data("SALALAH", 17.012751, 54.093058, "Salalah Chowk, Al Salam street, Salalah", "92625398"));
        locationDataList.add(new Data("SALALAH 23rd JULY STREET", 17.017278, 54.100705, "23rd July Street, Opp. Bank Muscat & GTC Bus Station, Salalah", "99102416"));
        locationDataList.add(new Data("AL SAADA, SALALAH", 17.067345, 54.151396, "18th November Street, Behind Bank Dhofar, Al Saada, Salalah","99102402"));
        locationDataList.add(new Data("SALALAH INTERNATIONAL AIRPORT", 17.049326, 54.087256, "Salalah International Airport", "93309614"));
        locationDataList.add(new Data("THUMRAIT", 17.610728, 54.035492, "Next to Bank Muscat, Thumrait Town, Thumrait", "99102407"));
        locationDataList.add(new Data("MARMUL", 18.179388, 55.20315, "Marmul Round About, Marmul", "99100206"));
        locationDataList.add(new Data("AL MAZYOUNA", 17.8435523, 52.663322, "Al Maha Petrol Station, Opp. Bank Muscat, Al Mazyouna", "98909783"));
        locationDataList.add(new Data("MABELA SANAYA", 23.6434960, 58.0959000, "Mabela Sanaya 9th Street T- Junction", "99427821"));
        locationDataList.add(new Data("DUQM", 19.6158800, 57.6301760, "Next to Ooredoo Store,Al Duqm", "92689809"));



        adapter = new LocationAdapter(getContext(), this, locationDataList);
        recyclerView.setAdapter(adapter);

    }

    private void checkInternet() {
        ConnectionLiveData connectionLiveData = new ConnectionLiveData(getContext());
        connectionLiveData.observe(getViewLifecycleOwner(), connection -> {
            if (!connection.getIsConnected()) {
                alerts.noInternet();
            }
            if (connection.getIsVpnConnected()) {
                Toast.makeText(getContext(), "VPN Connected", Toast.LENGTH_SHORT).show();
                alerts.vpnAlert();
            } else {
                alerts.dismissDialog();
            }
        });
    }

    @Override
    public void onCallClicked(String contact) {
        ContactNumber = contact;
        makePhoneCall(contact);
    }

    @Override
    public void onLocateClicked(Data locationDetails) {
        locClickListener.onLocationData(locationDetails);
//        BranchPager.viewPager.setCurrentItem(1, true);
//        Fragment branches = new ShowBranches();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("location", locationDetails);
//        branches.setArguments(bundle);
//        changePage(branches);
    }

    private void changePage(Fragment fragment) {
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).addToBackStack(null);
        fragmentTransaction.commit();
        fragmentTransaction.addToBackStack(null);
    }

    private void makePhoneCall(String customerCareNumber) {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 105);
        } else {
            if (customerCareNumber!=null && !customerCareNumber.equals("")){
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", customerCareNumber, null));
                startActivity(intent);
            }else{
                Toast.makeText(getContext(), "Contact number not found", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 105:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makePhoneCall(ContactNumber);
                } else {
                    Toast.makeText(getContext(), "Call permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onLocationData(Data data) {
        EventBus.getDefault().post(data);
        BranchPager.viewPager.setCurrentItem(1, true);
    }
}