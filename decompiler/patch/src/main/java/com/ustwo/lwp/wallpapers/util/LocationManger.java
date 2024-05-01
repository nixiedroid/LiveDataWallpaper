package com.ustwo.lwp.wallpapers.util;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Looper;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.ustwo.lwp.App;
import com.ustwo.lwp.util.L;

import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;
import static com.google.android.gms.common.ConnectionResult.SUCCESS;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class LocationManger {
    private static final LocationManger instance = new LocationManger();
    private final Coord lastKnownLocation = new Coord();
    protected long previousResultTime;
    LocationManager locationManager;
    FusedLocationProviderClient fusedLocationClient;
    private boolean isGoogleAvailable;

    private LocationManger() {
        try {
            int checkResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(App.get());
            isGoogleAvailable = (checkResult == SUCCESS);
        } catch (NoClassDefFoundError e) {
            isGoogleAvailable = false;
        }
        if (isGoogleAvailable) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(App.get());
        } else {
            locationManager = (LocationManager) App.get().getSystemService(LOCATION_SERVICE);
        }
        updateLocation();
    }

    public static LocationManger get() {
        return instance;
    }

    private void updateLocation() {

        if (isGoogleAvailable) {
            if (!(ContextCompat.checkSelfPermission(App.get(),ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED)) {
                return;
            }
            updateLocationGoogle();
        } else {
            if (!(ContextCompat.checkSelfPermission(App.get(),ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED)) {
                return;
            }
            updateLocationNonGoogle();
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationNonGoogle() {
        if (locationManager != null) {
            List<String> providers = locationManager.getProviders(true);
            for (String provider : providers) {
                setLastKnownLocation(locationManager.getLastKnownLocation(provider));
            }
            getActiveLocation(LocationManager.NETWORK_PROVIDER);
        }
    }
    @SuppressWarnings({"deprecation","SameParameterValue"})
    @SuppressLint("MissingPermission")
    private void getActiveLocation(String provider) {
        if (locationManager.isProviderEnabled(provider)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
                L.v("Using legacy code, as newer not implemented");
            }
            locationManager.requestSingleUpdate(provider, this::setLastKnownLocation, Looper.getMainLooper());
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationGoogle() {
        if (fusedLocationClient != null) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this::setLastKnownLocation);
        }
    }

    private void setLastKnownLocation(Location l) {
        if (l != null) {
            App.preferences().edit().putString("current_location", l.getLatitude() + " " + l.getLongitude()).apply();
            L.v("LocationManger: lat=" + l.getLatitude() + " lon=" + l.getLongitude());
            lastKnownLocation.setLon(l.getLongitude());
            lastKnownLocation.setLat(l.getLatitude());
            previousResultTime = System.currentTimeMillis();
        }
        L.d("Location is null. Probably, disabled");
    }

    public Coord getCoord() {
        if (TimeUtil.elapsedRealTimeSince(previousResultTime) < 30 * 60 * 1000) {
            L.v("Location Throttling");
            L.v("LocationManger: lat=" + lastKnownLocation.getLat() + " lon=" + lastKnownLocation.getLon());
            return lastKnownLocation;
        }
        updateLocation();
        return lastKnownLocation;
    }
}
