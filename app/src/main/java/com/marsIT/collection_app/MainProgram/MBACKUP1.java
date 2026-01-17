//package com.marsIT.collection_app.MainProgram;
//
//import android.Manifest;
//import android.annotation.SuppressLint;
//import android.app.AlertDialog;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.net.Uri;
//import android.util.Log;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.core.app.ActivityCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.location.Priority;
//import com.marsIT.collection_app.R;
//
//public class FDFDF {
//
//    // =========================================================
//    /** CLEAR ALL DATA BUTTON LOGIC */
//    // =========================================================
//
//    AFTER THIS /** CLEAR ALL DATA BUTTON LOGIC */ ....
//
//    // =========================================================
//    /** BUTTONS */
//    // =========================================================
//    //TODO: function for google maps - opens google maps with a route (walking mode) - shows distance & path
//    Button btnViewOnMap = findViewById(R.id.btnViewOnMap);
//        btnViewOnMap.setOnClickListener(v -> {
//        syncCustomerDistance(); //TODO: fetch distance first
//        openGoogleMaps(); //TODO: open google maps with stored customer location
//    });
//    // =========================================================
//    /** BUTTONS */
//    // =========================================================
//
//
//
//
//
//    AFTER THIS protected void onPause() ....
//
//
//    // =========================================================
//    /** this function gets the current location and opens Google Maps for navigation. */
//    // =========================================================
//    private void openGoogleMaps() {
//        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
//            return;
//        }
//
//        // Use getCurrentLocation for more accurate data
//        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
//                .addOnSuccessListener(location -> {
//                    if (location != null) {
//                        double currentLat = location.getLatitude();
//                        double currentLng = location.getLongitude();
//
//                        Log.d("DEBUG", "Current Location: Lat=" + currentLat + ", Lng=" + currentLng);
//                        Log.d("DEBUG", "Destination Location: Lat=" + endLatitude + ", Lng=" + endLongitude);
//
//                        if (endLatitude == 0.0 || endLongitude == 0.0) {
////                            Toast.makeText(this, "No valid customer location found!", Toast.LENGTH_SHORT).show();
//
//                            new AlertDialog.Builder(MainActivity.this)
//                                    .setIcon(R.drawable.mars_logo)
//                                    .setTitle("Location Missing")
//                                    .setMessage("No valid customer location found!")
//                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                                    .show();
//                            return;
//                        }
//
//                        float[] results = new float[1];
//                        Location.distanceBetween(currentLat, currentLng, endLatitude, endLongitude, results);
//                        float distance = results[0];
//
////                        String distanceText = distance >= 1000 ? (distance / 1000) + " KM" : distance + " M";
//                        @SuppressLint("DefaultLocale") String distanceText = distance >= 1000
//                                ? String.format("%.2f KM", distance / 1000)
//                                : String.format("%.0f M", distance);
//
//                        Toast.makeText(this, "Distance to Customer " + distanceText, Toast.LENGTH_LONG).show();
//
////                        new AlertDialog.Builder(MainActivity.this)
////                                .setIcon(R.drawable.mars_logo)
////                                .setTitle("Distance to Customer")
////                                .setMessage("Distance " + distanceText)
////                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
////                                .show();
//
//                        // Open Google Maps Immediately
//                        String uri = "https://www.google.com/maps/dir/?api=1&origin=" + currentLat + "," + currentLng
//                                + "&destination=" + endLatitude + "," + endLongitude + "&travelmode=walking";
//
//                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
//                        intent.setPackage("com.google.android.apps.maps");
//                        startActivity(intent);
//                    } else {
////                        Toast.makeText(this, "Failed to get current location!", Toast.LENGTH_SHORT).show();
//
//                        new AlertDialog.Builder(MainActivity.this)
//                                .setIcon(R.drawable.mars_logo)
//                                .setTitle("Location Failed")
//                                .setMessage("Failed to get current location!")
//                                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
//                                .show();
//                    }
//                })
//                .addOnFailureListener(e -> Log.e("ERROR", "Failed to get current location", e));
//    }
//    // =========================================================
//    /** this function gets the current location and opens Google Maps for navigation. */
//    // =========================================================
//
//
//}
