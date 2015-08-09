package com.star.cityfinder;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private GoogleMap mGoogleMap;
    private LatLng mMyLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        readCities();
                        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                if (mMyLocation != null) {
                                    LatLng markerLatLng = marker.getPosition();
                                    mGoogleMap.addPolyline(new PolylineOptions()
                                            .add(mMyLocation)
                                            .add(markerLatLng));
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        });

                        mMyLocation = getMyLocation();

                        if (mMyLocation == null) {
                            Toast.makeText(MainActivity.this,
                                    "Unable to access your location. " +
                                            "Consider enabling Location in your device's Settings.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            mGoogleMap.addMarker(new MarkerOptions()
                                    .position(mMyLocation)
                                    .title("ME!"));
                        }
                    }
                });
            }
        });
    }

    public LatLng getMyLocation() {

        LocationManager locationManager = (LocationManager)
                getSystemService(LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        }

        if (location == null) {
            return new LatLng(31, 121);
        } else {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            return new LatLng(latitude, longitude);
        }
    }

    private void readCities() {

        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.cities));

        while (scanner.hasNext()) {
            String name = scanner.nextLine();

            if (TextUtils.isEmpty(name)) {
                break;
            }

            double latitude = Double.parseDouble(scanner.nextLine());
            double longitude = Double.parseDouble(scanner.nextLine());

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(name));
        }
    }
}
