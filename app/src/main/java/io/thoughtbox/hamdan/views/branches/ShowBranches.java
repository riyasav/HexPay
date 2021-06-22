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
        locationList.add(new Data("HEAD OFFICE", 23.5935453571482, 58.4269483147075, "Corporate Office: 403, 4th Floor, Bank Sohar Bldg., Dohat Al Adab Street, Al-Khuwair, Muscat, Sultanate of Oman","91455455"));
        locationList.add(new Data("AL KHUWAIR", 23.591821, 58.428739, "Near Rawasco Hypermarket, Opp. The platinum Hotel, Alkhuwiar, Muscat", "99311695"));
        locationList.add(new Data("ALKHUWAIR DOHAT AL ADAB STREET", 23.5942655, 58.4239449, "Near Al Karama Hypermarket , Alkhuwair, Muscat", "99658287"));
        locationList.add(new Data("RUWI", 23.595111, 58.54507, "Mwasalat (ONTC) Bus Stand, Ruwi, Muscat", "99102412"));
        locationList.add(new Data("RUWI HIGH STREET", 23.593644, 58.544668, "Opp.Old Police Station Building, Ruwi High Street, Ruwi, Muscat", "99460091"));
        locationList.add(new Data("CITYCENTER SEEB", 23.599698, 58.248387, "Next to Carrefour Counter, Muscat City Center Seeb", "99102408"));
        locationList.add(new Data("SEEB SOUQ", 23.682115, 58.186078, "Opp. Bank Muscat, Seeb Souq, Seeb, Muscat", "99849409"));
        locationList.add(new Data("MABELAH", 23.662302, 58.129759, "Behind Bank Dhofar, Souq al Tijari , Mabelah, Muscat", "99102415"));
        locationList.add(new Data("BARKA SANAYYA", 23.615778, 57.86575, "Next to Oman oil and Bank Muscat, Barka Sanayya, Barka", "98568022"));
        locationList.add(new Data("MISFAH", 23.49455, 58.24972, "Opp Suhail Bhavan logistics, Misfa Industrial Area, Wadi Saal al Misfa, Muscat", "99879083"));
        locationList.add(new Data("MUSANNAH", 23.745632, 57.621987, "Musannah souq, Tareef, Musannah", "98049113"));
        locationList.add(new Data("FALAJ", 24.423306, 56.611295, "Muscat Road, Falaj Roundabout , Falaj al Qabail", "99102409"));
        locationList.add(new Data("IBRA", 22.716864, 58.528204, "Near AlFayha Hypermarket, Alayat, Ibra", "98049112"));
        locationList.add(new Data("SINAW", 22.504406, 58.03258, "Sinaw Roundabout, Near Wally office, Sinaw", "93893714"));
        locationList.add(new Data("IBRI", 23.21764, 56.489352, "Inside Ramez Hypermarket, Ibri", "99483971"));
        locationList.add(new Data("ADAM", 22.388266, 57.526433, "Next to Bank Muscat and Bank Dhofar, Adam Souq", "93608842"));
        locationList.add(new Data("KARSHA", 22.841535, 57.543565, "Near Karsha Round About, Karsha Industrial Area, Karsha", "98089306"));
        locationList.add(new Data("LEKHWAIR", 22.828889, 55.321167, "Business Center Building, Lekhwair pdo, Lekhwair PAC", "91298197"));
        locationList.add(new Data("CITYCENTER SUR", 22.576569, 59.508619, "Shop No 7, Opp to  Careefour, Sur City Center, Sur", "92657486"));
        locationList.add(new Data("AL ASHKARA", 21.849305, 59.568842, "Near HSBC ATM, Al Ashkara", "99311846"));
        locationList.add(new Data("SALALAH", 17.012751, 54.093058, "Salalah Chowk, Al Salam street, Salalah", "92625398"));
        locationList.add(new Data("SALALAH 23rd JULY STREET", 17.017278, 54.100705, "23rd July Street, Opp. Bank Muscat & GTC Bus Station, Salalah", "99102416"));
        locationList.add(new Data("AL SAADA, SALALAH", 17.067345, 54.151396, "18th November Street, Behind Bank Dhofar, Al Saada, Salalah","99102402"));
        locationList.add(new Data("SALALAH INTERNATIONAL AIRPORT", 17.049326, 54.087256, "Salalah International Airport", "93309614"));
        locationList.add(new Data("THUMRAIT", 17.610728, 54.035492, "Next to Bank Muscat, Thumrait Town, Thumrait", "99102407"));
        locationList.add(new Data("MARMUL", 18.179388, 55.20315, "Marmul Round About, Marmul", "99100206"));
        locationList.add(new Data("AL MAZYOUNA", 17.8435523, 52.663322, "Al Maha Petrol Station, Opp. Bank Muscat, Al Mazyouna", "98909783"));
        locationList.add(new Data("MABELA SANAYA", 23.6434960, 58.0959000, "Mabela Sanaya 9th Street T- Junction", "99427821"));
        locationList.add(new Data("DUQM", 19.6158800, 57.6301760, "Next to Ooredoo Store,Al Duqm", "92689809"));


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
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
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