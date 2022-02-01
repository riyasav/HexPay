package io.thoughtbox.hamdan.views.branches;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Objects;

import io.thoughtbox.hamdan.R;
import io.thoughtbox.hamdan.databinding.BranchDetailsBinding;
import io.thoughtbox.hamdan.databinding.FragmentShowBranchesBinding;
import io.thoughtbox.hamdan.model.mapModel.Data;
import io.thoughtbox.hamdan.utls.Loader;


public class ShowBranches extends Fragment implements OnMapReadyCallback, LocationClickListener {
    FragmentShowBranchesBinding binding;

    Dialog progressDialog;
    ArrayList<Data> locationList;
    Data locationCoordinators;
    private GoogleMap mMap;
    String ContactNumber;

    public ShowBranches() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_show_branches, container, false);
        binding.setLifecycleOwner(this);
        binding.setClickers(this);


        MapView mapFragment = binding.map;
        mapFragment.getMapAsync(this);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        init();


        return binding.getRoot();
    }

    private void init() {
        Loader loader = new Loader(getContext());
        progressDialog = loader.progress();
    }


    private void getBranches() {
        locationList = new ArrayList<>();
        locationList.add(new Data("SALALAH MAIN - HEADOFFICE", 17.01979706, 54.06125204, "Hamdan Complex, Robat Road,Salalah","23211258"));
        locationList.add(new Data("SALALAH SALAM STREET", 17.01334991, 54.09430833, "Salalah Salam Street, Salalah Souq,Salalah", "23296904"));
        locationList.add(new Data("Muscat-Ruwi", 23.58941517, 58.54562511, "Ruwi High-Street,Muscat", "24832801"));
        locationList.add(new Data("KHABOURA", 23.97172965, 57.0912932, "Main Round About,Khaboura", "26801246"));
        locationList.add(new Data("SAHAM", 24.14406983, 56.87640768, "Behind Shell Petrol Pump,Saham", "26855491"));
        locationList.add(new Data("SAMAIL", 23.30146571, 57.95426825, "Near Bank Muscat,Madrah,Samail", "25350334"));
        locationList.add(new Data("RUSTAQ", 23.39699547, 57.42219728, "Near National Bank of Oman,Ainy Kaswah,Rustaq", "22588989"));
        locationList.add(new Data("BURAIMI", 24.25542518, 55.76926922, "Near Safeer Market, Power House Road,Al Buraimi", "25652688"));
        locationList.add(new Data("KHADRA", 23.85876118, 57.32957156, "Opposite Bank Muscat,Khadra", "26172210"));
        locationList.add(new Data("THARMAD", 23.78731955, 57.5200469, "Near Al Ain Centre,Tharmad", "26810115"));
        locationList.add(new Data("GHALA", 23.58131313, 58.38095807, "Near Sultan Qaboos Mosque Signal, Ghala Bausher,Ghala", "24527444"));
        locationList.add(new Data("AL HAIL", 23.62859891, 58.2328284, "Near Zam Zam Hyper Market, Al Hail north,Al Hail", "24184377"));
        locationList.add(new Data("AL KAMIL", 22.2246951, 59.19438325, "Near Bank Dhofar,Al Kamil", "25558444"));
        locationList.add(new Data("LIWA", 24.51900985, 56.56347674, "Near Round About New Liwa,Liwa", "26762673"));
        locationList.add(new Data("QURIYAT ", 23.261042, 58.91544864, "Near Oman Arab Bank,Souk road,Quriyat", "24847755"));
        locationList.add(new Data("NAKHAL", 23.40876079, 57.82018198, "Near Bank Muscat,Nakhal", "26781269"));
        locationList.add(new Data("MAHOUT", 20.76578812,58.28727187, "Near Shell Petrol Pump,Mahout", "25427226"));
        locationList.add(new Data("YANQUL", 23.59268973, 56.55184898, "Near Bank Dhofar,Yanqul", "25672466"));
        locationList.add(new Data("MABELLA II", 23.64079028, 58.09944846, "Adjacent to Babil Hypermarket,Mabellah", "24052549"));
        locationList.add(new Data("FALAJ AL QABAIL", 24.4374317, 56.6077423, "Near Al Maha Petrol Pump,Falaj Al Qabail", "26750690"));
        locationList.add(new Data("SALALAH SANAYAH", 17.02218261, 54.05131538, "Sanaya GTC Road, Near Mosque,Salalah Sanaya", "23229922"));
        locationList.add(new Data("BAHLA", 22.94788468, 57.27496956, "Al Karama Hypermarket,Bahla", "25225978"));
        locationList.add(new Data("GADFHAN", 24.4639897, 56.6052961, "Near Al Wafa Commercial Centre,Gadhfan","26762507"));
        locationList.add(new Data("AL KHOUD", 23.63234182, 58.19924997, "Near Babil Hypermarket & Red Tag, Al Khoud Souq,Al Khoud", "26762507"));
        locationList.add(new Data("OUHI", 24.38374175, 56.6466185, "Near Al Majeesco Shopping centre,Ouhi Sanaya", "26647115"));
        locationList.add(new Data("GARDENS MALL", 17.02428166, 54.06637614, "Inside Salalah Gardens Mall, Salalah city,Salalah ", "23284420"));
        locationList.add(new Data("MAWALEH", 23.59455084, 58.22512598, "Opposite to Mawaleh vegetable market,Mawaleh", "24075066"));
        locationList.add(new Data("MABELLAH-4", 23.65771292, 58.0993617, "Near Mehmood Masjid,Mabelah 4", "24266683"));
        locationList.add(new Data("SAADAH", 17.05164958, 54.15573353, "Near to bank muscat ,Al mashoor super market way,Saadah", "24857578"));


        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < locationList.size(); i++) {
//                plotBranchOnMap(locations.get(i).getLatitude(), locations.get(i).getLongitude(), locations.get(i).getName());

            final LatLng position = new LatLng(locationList.get(i).getLat(), locationList.get(i).getLon());
            final MarkerOptions options = new MarkerOptions().position(position).title(locationList.get(i).getName()).icon(bitmapDescriptorFromVector(getContext()));
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5.0f));
            builder.include(position);
        }
    }

    private void plotBranchOnMap(double lat, double lon, String name) {
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();
        final LatLng position = new LatLng(lat, lon);
        final MarkerOptions options = new MarkerOptions().position(position).title(name).icon(bitmapDescriptorFromVector(getContext()));
        mMap.addMarker(options);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 13));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(position)           // Sets the center of the map to location user
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5.0f));
        builder.include(position);
//        showLocationDetails(locationCoordinators);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.location_icon);
        if (vectorDrawable != null) {
            vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        }
        Bitmap bitmap = null;
        if (vectorDrawable != null) {
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.draw(canvas);
        }
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        insertMarkers(googleMap);
    }

    private void insertMarkers(GoogleMap googleMap) {
        mMap = googleMap;
        getBranches();

        mMap.setOnMarkerClickListener(marker -> {

            String posKey = marker.getId().replace("m", "");
            int pos = Integer.parseInt(posKey);
            Log.d("position", "List Position " + pos);
            locationCoordinators = locationList.get(pos);
            showLocationDetails(locationCoordinators);

            return false;
        });
    }

    private void styleMap(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.mapstyle));

            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }
    }

//    private void showDetailedAddress(LocationResponseData selectedData) {
//        markerDetailsDialog(selectedData).show();
//    }
//
//
//    private Dialog markerDetailsDialog(LocationResponseData selectedData) {
//        Dialog locationDetailsDialog = new Dialog(getContext(), R.style.CustomDialogTheme);
//        LoactionDetailsBinding loactionDetailsBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.loaction_details, null, false);
//        locationDetailsDialog.setContentView(loactionDetailsBinding.getRoot());
//        loactionDetailsBinding.setInfo(selectedData);
//        loactionDetailsBinding.close.setOnClickListener(v -> locationDetailsDialog.dismiss());
//         ContactNumber=selectedData.getContact();
//        loactionDetailsBinding.call.setOnClickListener(v -> makePhoneCall(ContactNumber));
//        loactionDetailsBinding.navigation.setOnClickListener(v -> {
//            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + selectedData.getLatitude() + "," + selectedData.getLongitude());
//            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//            mapIntent.setPackage("com.google.android.apps.maps");
//            startActivity(mapIntent);
//        });
//        locationDetailsDialog.setCancelable(true);
//        locationDetailsDialog.show();
//        return locationDetailsDialog;
//    }

    private void showLocationDetails(Data data) {
        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);
        BranchDetailsBinding dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.branch_details, null, false);
        mBottomSheetDialog.setContentView(dialogBinding.getRoot());
        mBottomSheetDialog.setCancelable(true);

        dialogBinding.title.setText(data.getName());

        dialogBinding.address.setText(data.getAddress());

        dialogBinding.call.setOnClickListener(view -> {
            if (data.getContact() != null && !data.getContact().isEmpty())
                makePhoneCall(data.getContact());
        });

        dialogBinding.direction.setOnClickListener(view -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + data.getLat() + "," + data.getLon());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });

        dialogBinding.mail.setOnClickListener(view -> sendMail(getString(R.string.email)));

        dialogBinding.share.setOnClickListener(view -> shareData(data.getName(), data.getAddress() + "\n" + data.getContact() + "\n" + "http://www.google.com/maps/place/" + data.getLat() + "," + data.getLon()));

        mBottomSheetDialog.show();
    }

    private void sendMail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");

        startActivity(Intent.createChooser(intent, "Send Email"));

    }

    private void makePhoneCall(String customerCareNumber) {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 105);
        } else {
            if (customerCareNumber != null && !customerCareNumber.equals("")) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", customerCareNumber, null));
                startActivity(intent);
            } else {
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

    private void shareData(String subject, String body) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        /*The type of the content is text, obviously.*/
        intent.setType("text/plain");
        /*Applying information Subject and Body.*/
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
        /*Fire!*/
        startActivity(Intent.createChooser(intent, getString(R.string.share_using)));
    }

    @Override
    public void onLocationData(Data data) {
        if (data != null) {
            plotBranchOnMap(locationCoordinators.getLat(), locationCoordinators.getLon(), locationCoordinators.getName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultReceived(Data result) {
        locationCoordinators = result;
        plotBranchOnMap(result.getLat(), result.getLon(), result.getName());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}