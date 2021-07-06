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
        locationDataList.add(new Data("SALALAH MAIN - HEADOFFICE", 17.01979706, 54.06125204, "Hamdan Complex, Robat Road,Salalah","23211258"));
        locationDataList.add(new Data("SALALAH SALAM STREET", 17.01334991, 54.09430833, "Salalah Salam Street, Salalah Souq,Salalah", "23296904"));
        locationDataList.add(new Data("Muscat-Ruwi", 23.58941517, 58.54562511, "Ruwi High-Street,Muscat", "24832801"));
        locationDataList.add(new Data("KHABOURA", 23.97172965, 57.0912932, "Main Round About,Khaboura", "26801246"));
        locationDataList.add(new Data("SAHAM", 24.14406983, 56.87640768, "Behind Shell Petrol Pump,Saham", "26855491"));
        locationDataList.add(new Data("SAMAIL", 23.30146571, 57.95426825, "Near Bank Muscat,Madrah,Samail", "25350334"));
        locationDataList.add(new Data("RUSTAQ", 23.39699547, 57.42219728, "Near National Bank of Oman,Ainy Kaswah,Rustaq", "22588989"));
        locationDataList.add(new Data("BURAIMI", 24.25542518, 55.76926922, "Near Safeer Market, Power House Road,Al Buraimi", "25652688"));
        locationDataList.add(new Data("KHADRA", 23.85876118, 57.32957156, "Opposite Bank Muscat,Khadra", "26172210"));
        locationDataList.add(new Data("THARMAD", 23.78731955, 57.5200469, "Near Al Ain Centre,Tharmad", "26810115"));
        locationDataList.add(new Data("GHALA", 23.58131313, 58.38095807, "Near Sultan Qaboos Mosque Signal, Ghala Bausher,Ghala", "24527444"));
        locationDataList.add(new Data("AL HAIL", 23.62859891, 58.2328284, "Near Zam Zam Hyper Market, Al Hail north,Al Hail", "24184377"));
        locationDataList.add(new Data("AL KAMIL", 22.2246951, 59.19438325, "Near Bank Dhofar,Al Kamil", "25558444"));
        locationDataList.add(new Data("LIWA", 24.51900985, 56.56347674, "Near Round About New Liwa,Liwa", "26762673"));
        locationDataList.add(new Data("QURIYAT ", 23.261042, 58.91544864, "Near Oman Arab Bank,Souk road,Quriyat", "24847755"));
        locationDataList.add(new Data("NAKHAL", 23.40876079, 57.82018198, "Near Bank Muscat,Nakhal", "26781269"));
        locationDataList.add(new Data("MAHOUT", 20.76578812,58.28727187, "Near Shell Petrol Pump,Mahout", "25427226"));
        locationDataList.add(new Data("YANQUL", 23.59268973, 56.55184898, "Near Bank Dhofar,Yanqul", "25672466"));
        locationDataList.add(new Data("MABELLA II", 23.64079028, 58.09944846, "Adjacent to Babil Hypermarket,Mabellah", "24052549"));
        locationDataList.add(new Data("FALAJ AL QABAIL", 24.4374317, 56.6077423, "Near Al Maha Petrol Pump,Falaj Al Qabail", "26750690"));
        locationDataList.add(new Data("SALALAH SANAYAH", 17.02218261, 54.05131538, "Sanaya GTC Road, Near Mosque,Salalah Sanaya", "23229922"));
        locationDataList.add(new Data("BAHLA", 22.94788468, 57.27496956, "Al Karama Hypermarket,Bahla", "25225978"));
        locationDataList.add(new Data("GADFHAN", 24.4639897, 56.6052961, "Near Al Wafa Commercial Centre,Gadhfan","26762507"));
        locationDataList.add(new Data("AL KHOUD", 23.63234182, 58.19924997, "Near Babil Hypermarket & Red Tag, Al Khoud Souq,Al Khoud", "26762507"));
        locationDataList.add(new Data("OUHI", 24.38374175, 56.6466185, "Near Al Majeesco Shopping centre,Ouhi Sanaya", "26647115"));
        locationDataList.add(new Data("GARDENS MALL", 17.02428166, 54.06637614, "Inside Salalah Gardens Mall, Salalah city,Salalah ", "23284420"));
        locationDataList.add(new Data("MAWALEH", 23.59455084, 58.22512598, "Opposite to Mawaleh vegetable market,Mawaleh", "24075066"));
        locationDataList.add(new Data("MABELLAH-4", 23.65771292, 58.0993617, "Near Mehmood Masjid,Mabelah 4", "24266683"));
        locationDataList.add(new Data("SAADAH", 17.05164958, 54.15573353, "Near to bank muscat ,Al mashoor super market way,Saadah", "24857578"));



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
        FragmentManager fragmentManager =  Objects.requireNonNull(getActivity()).getSupportFragmentManager();
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