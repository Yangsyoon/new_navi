package com.example.bottomnavi;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

public class Frag4 extends Fragment implements OnMapReadyCallback {

    private ArrayList<String> selectedPlaces = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private GoogleMap googleMap;
    private Location currentLocation;

    private static final String TAG = "Frag4";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag4, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        loadSelectedPlaces();
        getCurrentLocation();

        Button btnCurrentLocation = view.findViewById(R.id.btnCurrentLocation);
        btnCurrentLocation.setOnClickListener(v -> {
            if (currentLocation != null) {
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            }
        });

        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        Log.d(TAG, "GoogleMap is ready");

        LatLng defaultLocation = new LatLng(-34, 151);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(defaultLocation));

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
            if (location != null) {
                currentLocation = location;
                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                addCurrentLocationMarker(currentLatLng);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));

                // 선택된 장소에 마커 추가
                addMarkers();
            } else {
                Log.d(TAG, "Current location is null");
            }
        });

        // 공공데이터로부터 관광지 정보 받아오기
        loadTouristPlaces();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void loadSelectedPlaces() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("selectedPlaces", Context.MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet("selectedPlaces", null);
        if (set != null) {
            selectedPlaces = new ArrayList<>(set);
            showToast("Selected places loaded");
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        Task<Location> locationResult = fusedLocationClient.getLastLocation();
        locationResult.addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                Location lastKnownLocation = task.getResult();
                if (lastKnownLocation != null) {
                    currentLocation = lastKnownLocation;
                    // 현재 위치 마킹
                    addCurrentLocationMarker(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    // 현재 위치를 기준으로 방향 받아오기
                    double latitude = lastKnownLocation.getLatitude();
                    double longitude = lastKnownLocation.getLongitude();
                    getDirections(latitude, longitude);
                    showToast("Current location retrieved");
                } else {
                    Log.d(TAG, "Last known location is null");
                }
            } else {
                Log.d(TAG, "Task to get last location failed");
            }
        });
    }

    private void getDirections(double latitude, double longitude) {
        // 방향 정보를 가져와서 지도에 표시
        // 이전에 작성한 getDirections() 메서드의 내용을 여기에 복사해서 사용합니다.
    }

    private void addCurrentLocationMarker(LatLng currentLatLng) {
        try {
            googleMap.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));
            Log.d(TAG, "Current location marker added at: " + currentLatLng.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error adding current location marker: ", e);
        }
    }

    private void addMarkers() {
        for (String place : selectedPlaces) {
            // 장소 이름을 위도와 경도로 변환하여 마커 추가
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(place, 1);
                assert addresses != null;
                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(place));
                    Log.d(TAG, "Marker added for place: " + place + " at: " + latLng.toString());
                } else {
                    Log.d(TAG, "No addresses found for place: " + place);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error adding marker for place: " + place, e);
            }
        }
    }

    private void loadTouristPlaces() {
        // 공공 데이터 API 엔드포인트
        String url = "https://www.data.go.kr/download/15021141/standard.do";
        String apiKey = "M4q3CWc0OP6VctrSKmKMdcNJAY3CWOj5XmhvM7WF2GkyXgdKb2IpCrGO8LRWl9Wl9986gSB%2Bi6t29viXcyV58g%3D%3D"; // 여기에 공공 데이터에서 발급한 API 키를 입력합니다.
        String requestUrl = url + "?dataType=xml&ServiceKey=" + apiKey + "&pageNo=1&numOfRows=100";


        // API 요청
        StringRequest request = new StringRequest(Request.Method.GET, requestUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 응답을 로그로 출력하여 확인
                        Log.d(TAG, "API Response: " + response);

                        // 응답의 시작 부분을 확인하여 XML 형식인지 확인
                        if (response.trim().startsWith("<")) {
                            // XML 응답을 처리하는 메서드 호출
                            processXmlResponse(response);
                            showToast("Tourist places loaded successfully");
                        } else {
                            showToast("Received non-XML response");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 오류 처리
                        Toast.makeText(getContext(), "Error loading tourist places", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error loading tourist places", error);
                    }
                });

        // 요청을 큐에 추가
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        queue.add(request);
    }

    private void processXmlResponse(String response) {
        try {
            // XML 파싱
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(response));

            // XML 문서를 읽으면서 관광지 정보를 추출하고 지도에 마커를 추가
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("record")) {
                    // 각 record 태그마다 관광지 정보 추출
                    String placeName = "";
                    double latitude = 0.0;
                    double longitude = 0.0;

                    while (!(eventType == XmlPullParser.END_TAG && parser.getName().equals("record"))) {
                        if (eventType == XmlPullParser.START_TAG) {
                            String tagName = parser.getName();
                            switch (tagName) {
                                case "관광지명":
                                    placeName = parser.nextText();
                                    break;
                                case "위도":
                                    latitude = Double.parseDouble(parser.nextText());
                                    break;
                                case "경도":
                                    longitude = Double.parseDouble(parser.nextText());
                                    break;
                                default:
                                    break;
                            }
                        }
                        eventType = parser.next();
                    }

                    // 마커 추가
                    LatLng latLng = new LatLng(latitude, longitude);
                    try {
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
                        Log.d(TAG, "Tourist place marker added for: " + placeName + " at: " + latLng.toString());
                    } catch (Exception e) {
                        Log.e(TAG, "Error adding marker for tourist place: " + placeName, e);
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            // XML 파싱 예외 처리
            Log.e(TAG, "Error parsing XML response", e);
            showToast("Error parsing XML response");
        } catch (IOException e) {
            // IO 예외 처리
            Log.e(TAG, "IO Exception occurred", e);
            showToast("IO Exception occurred");
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
