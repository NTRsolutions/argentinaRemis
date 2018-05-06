package com.apreciasoft.admin.asremis.Fracments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apreciasoft.admin.asremis.Activity.HomeActivity;
import com.apreciasoft.admin.asremis.Entity.InfoTravelEntity;
import com.apreciasoft.admin.asremis.Entity.token;
import com.apreciasoft.admin.asremis.Entity.tokenFull;
import com.apreciasoft.admin.asremis.Http.HttpConexion;
import com.apreciasoft.admin.asremis.R;
import com.apreciasoft.admin.asremis.Services.ServicesLoguin;
import com.apreciasoft.admin.asremis.Util.DataParser;
import com.apreciasoft.admin.asremis.Util.GlovalVar;
import com.apreciasoft.admin.asremis.Util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 04/01/2017.
 */

public class HomeFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {




    /* SOCKET MAPA */
    public static Socket SPCKETMAP;
    public static String URL_SOCKET_MAP =  HttpConexion.PROTOCOL+"://"+HttpConexion.ip+":"+HttpConexion.portWsWeb+"";
    public static String MY_EVENT_MAP = "init";
    /*++++++++++++*/

    private static View view;
    int _COUNT_CHANGUE = 0;
    public  boolean CONEXION_MAP_ERROR = false;
    public ServicesLoguin daoLoguin = null;
    public String TAG = "HomeFragment";
    public static List<LatLng> listPosition = new ArrayList<>();
    static GoogleMap mGoogleMap;
    LocationRequest mLocationRequest;
    static GoogleApiClient mGoogleApiClient;
    public static  Location mLastLocation;
    public static  String nameLocation;
    public static   PolylineOptions options;
    public static   PolylineOptions optionReturnActive;
    Marker mCurrLocationMarker;
    //public boolean isFistLocation = true;
    public boolean isReadyDrawingRouting = false;
    public static ArrayList<LatLng> MarkerPoints;
    public static GlovalVar gloval;
    public static TextView txt_client_info = null;
    public static TextView txt_observationFlight = null;
    public static TextView txt_date_info = null;
    public static TextView txt_destination_info = null;
    public static TextView txt_origin_info = null;
    public static TextView txt_km_info = null;
    public static TextView txt_calling_info = null;
    public static TextView txt_observationFromDriver = null;
    public static TextView txt_amount_info = null;
    public static TextView txt_pasajeros_info,infoGneral = null;
    public static TextView txt_lote = null;
    public static TextView txt_flete = null;
    public static TextView txt_piso_dialog = null;
    public static TextView txt_dpto_dialog = null;
    public static TextView txt_distance_real = null;
    //public static LinearLayout content_ditanceReal = null;
    public static  SharedPreferences.Editor editor;
    public static  SharedPreferences pref;



    public MapFragment  mMap;
    public static int PARAM_26  = 0;


    public static Location getmLastLocation() {
        return mLastLocation;
    }

    public static void setmLastLocation(Location mLastLocation) {
        HomeFragment.mLastLocation = mLastLocation;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {


        this.gloval = ((GlovalVar) getActivity().getApplicationContext());
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }


        try {
            view = inflater.inflate(R.layout.fragment_home,container,false);
            Log.d("YA","2.1");
        } catch (InflateException e) {
            /* map is already there, just return view as it is */
        }



        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pref = getActivity().getSharedPreferences(HttpConexion.instance, 0); // 0 - for private mode
        editor = pref.edit();

        MapFragment fr = (MapFragment)getChildFragmentManager().findFragmentById(R.id.gmap);


        if(fr == null)
        {
            mMap = ((MapFragment) this.getFragmentManager().findFragmentById(R.id.gmap));
            mMap.getMapAsync(this);
            Log.d("YA","4");


        }
        else
        {
            fr.getMapAsync(this);
            //  Log.d("YA","5");
        }





        HomeFragment.txt_date_info = (TextView) getActivity().findViewById(R.id.txt_date_info);
        HomeFragment.txt_client_info = (TextView) getActivity().findViewById(R.id.txt_client_info);
        HomeFragment.txt_destination_info = (TextView) getActivity().findViewById(R.id.txt_destination_info);
        HomeFragment.txt_origin_info = (TextView) getActivity().findViewById(R.id.txt_origin_info);
        HomeFragment.txt_lote = (TextView) getActivity().findViewById(R.id.txt_lote);
        HomeFragment.txt_flete = (TextView) getActivity().findViewById(R.id.txt_flete);

        HomeFragment.txt_piso_dialog = (TextView) getActivity().findViewById(R.id.txt_piso_dialog);
        HomeFragment.txt_dpto_dialog = (TextView) getActivity().findViewById(R.id.txt_dpto_dialog);

        HomeFragment.txt_km_info = (TextView) getActivity().findViewById(R.id.txt_km_info);
        HomeFragment.txt_amount_info = (TextView) getActivity().findViewById(R.id.txt_amount_info);
        HomeFragment.txt_calling_info = (TextView) getActivity().findViewById(R.id.txt_calling_info);
        HomeFragment.txt_observationFromDriver = (TextView) getActivity().findViewById(R.id.txt_observationFromDriver);
        HomeFragment.txt_observationFlight = (TextView) getActivity().findViewById(R.id.txt_observationFlight);

        HomeFragment.txt_pasajeros_info = (TextView) getActivity().findViewById(R.id.txt_pasajeros_info);

        HomeFragment.txt_distance_real = (TextView) getActivity().findViewById(R.id.txt_distance_real);









        infoGneral = (TextView) getActivity().findViewById(R.id.infoGneral);
        infoGneral.setText("SERVICIO ACTIVO");


     //   infodistance = (TextView) getActivity().findViewById(R.id.infodistance);



        // content_ditanceReal = (LinearLayout) getActivity().findViewById(R.id.content_ditance_real);
        // content_ditanceReal.setVisibility(View.INVISIBLE);



    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            //stop location updates when Activity is no longer active
            if (mGoogleApiClient != null) {
                Log.d("YA","6");
                // HomeFragment.SPCKETMAP.disconnect();
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            }
            Log.d("YA","7");

        }catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();


/*
        try {
            //stop location updates when Activity is no longer active
            if (mGoogleApiClient != null) {
                Log.d("YA","6");
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
            Log.d("YA","7");

        }catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }*/
    }



    @Override
    public void onResume() {
        super.onResume();


    }




    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        Log.d(TAG,"T-0");


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Log.d(TAG,"T-1");

            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

                Log.d(TAG,"2");

            } else {
                //Request Location Permission
                checkLocationPermission();
                Log.d(TAG,"T-3");
            }

        }
        else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

            Log.d(TAG,"T-4");
        }


        // Setting onclick event listener for the map
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {


            }
        });





    }





    /*SETEAR LA DIRECION DE EL VIAJE */
    public  void setDirection(LatLng point)
    {

        // Already two locations
        if (MarkerPoints.size() > 1) {
            MarkerPoints.clear();
            mGoogleMap.clear();
        }

        // Adding new item to the ArrayList
        MarkerPoints.add(point);

        // Creating MarkerOptions
        MarkerOptions optionsDriving = new MarkerOptions();

        // Setting the position of the marker
        optionsDriving.position(point);

        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */
        if (MarkerPoints.size() == 1) {
            optionsDriving.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else if (MarkerPoints.size() == 2) {
            optionsDriving.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }


        // Add new marker to the Google Map Android API V2
        mGoogleMap.addMarker(optionsDriving);

        // Checks, whether start and end locations are captured
        if (MarkerPoints.size() >= 2) {
            LatLng origin = MarkerPoints.get(0);
            LatLng dest = MarkerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getUrl(origin, dest);
            Log.d("onMapClick", url.toString());
            FetchUrl FetchUrl = new FetchUrl();

            // Start downloading json data from Google Directions API
            FetchUrl.execute(url);
            //move map camera
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        }

    }

    private static String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


        try{
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                //  conexionSocketMap();
            }

        }catch (Exception E)
        {

        }



    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void conexionSocketMap(double la ,double lo) {



        try{
            /* Instance object socket */
            // SPCKETMAP = IO.socket(URL_SOCKET_MAP);




            if(HttpConexion.PROTOCOL == "https")
            {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new SecureRandom());
                IO.setDefaultSSLContext(sc);
                //  HttpsURLConnection.setDefaultHostnameVerifier(new RelaxedHostNameVerifier());


                IO.Options options = new IO.Options();
                options.sslContext = sc;
                options.secure = true;
                options.port = HttpConexion.portWsWeb;


                if(SPCKETMAP == null) {
                    SPCKETMAP = IO.socket(URL_SOCKET_MAP, options);
                }
            }
            else
            {
                if(SPCKETMAP == null) {
                    SPCKETMAP = IO.socket(URL_SOCKET_MAP);
                }
            }

            Log.d("SOCK MAP","va a conectar: "+URL_SOCKET_MAP);

            SPCKETMAP.on(Socket.EVENT_CONNECT, new Emitter.Listener(){
                @Override
                public void call(Object... args) {
                    /* Our code */
                    Log.d("SOCK MAP","CONECT");
                    _COUNT_CHANGUE = 1;

                    sendSocketId();
                    CONEXION_MAP_ERROR = false;


                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener(){
                @Override
                public void call(Object... args) {
                    /* Our code */
                    Log.d("SOCK MAP","DISCONESCT");
                    _COUNT_CHANGUE = 0;
                    CONEXION_MAP_ERROR = true;
                    SPCKETMAP = null;

                }
            })
                    .on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener(){
                        @Override
                        public void call(Object... args) {
                            /* Our code */
                            Log.d("SOCK MAP","EVENT_RECONNECT_ERROR");
                            _COUNT_CHANGUE = 0;
                            CONEXION_MAP_ERROR = true;
                            SPCKETMAP = null;

                        }
                    });




            JSONObject obj = new JSONObject();

            double[] latLong = new double[2];
            latLong[0] = la;
            latLong[1] = lo;

            JSONArray jsonAraay = null;

            try {
                jsonAraay = new JSONArray(latLong);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Log.d("SOCK MAP", String.valueOf(jsonAraay));

            try {
                obj.put("isDriver", "true");
                obj.put("latLong", jsonAraay);
            } catch (JSONException e) {
                e.printStackTrace();
            }




            //SPCKETMAP.emit(HomeFragment.MY_EVENT_MAP, obj, new Ack() {



            // @Override
            // public void call(Object... args) {
            /* Our code */

            //    Log.d("SOCK MAP","EMITIO EVENTO");
            //   }
            // });


            SPCKETMAP.connect();




        }catch (URISyntaxException e){
            Log.d("SOCK MAP ",e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.d("SOCK MAP ",e.getMessage());
        } catch (KeyManagementException e) {
            Log.d("SOCK MAP ",e.getMessage());
        }catch (Exception e) {
            Log.d("SOCK MAP ERROR",e.getMessage());

        }
    }


    private TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[] {};
        }

        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }
    } };

   /* public static class RelaxedHostNameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }*/



    public void sendSocketId()
    {

        if(SPCKETMAP != null) {

            if (SPCKETMAP.id() != null) {

                this.daoLoguin = HttpConexion.getUri().create(ServicesLoguin.class);


                try {

                    Log.d("SOCK MAP", SPCKETMAP.id().toString());


                    if (SPCKETMAP.id() != null) {


                        token T = new token();
                        T.setToken(new tokenFull(gloval.getGv_user_id(), SPCKETMAP.id().toString()));

                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        Log.d("Response JSON", gson.toJson(T));

                        Call<Boolean> call = this.daoLoguin.updateSocketWeb(T);

                        call.enqueue(new Callback<Boolean>() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                Log.d("Response request", call.request().toString());
                                Log.d("Response request header", call.request().headers().toString());
                                Log.d("Response raw header", response.headers().toString());
                                Log.d("Response raw", String.valueOf(response.raw().body()));
                                Log.d("Response code", String.valueOf(response.code()));

                            }

                            public void onFailure(Call<Boolean> call, Throwable t) {


                                Log.d("ERROR", t.getMessage());
                            }
                        });
                    }

                } finally {
                    this.daoLoguin = null;
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onLocationChanged(Location location)
    {




        Log.d("onLocationChanged","onLocationChanged");


        if(getActivity() != null) {

            Geocoder gCoder = new Geocoder(getActivity());
            List<android.location.Address> addresses = null;


            try {
                addresses = gCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5



                /*
                 * SOCKET MAPA
                 * NODE JS
                 * */



                if(this.getActivity().getApplicationContext() != null){
                    if( Utils.verificaConexion(this.getActivity().getApplicationContext()) == false){
                        if(SPCKETMAP != null){
                            SPCKETMAP.disconnect();
                            _COUNT_CHANGUE = 0;

                        }
                    }

                }


                if(this.getActivity().getApplicationContext() != null) {

                    if (_COUNT_CHANGUE == 0 &&
                            Utils.verificaConexion(this.getActivity().getApplicationContext()) == true) {
                        if(SPCKETMAP == null){
                            if(addresses.size() > 0) {
                                conexionSocketMap(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
                            }

                        }
                    }
                }



                if(SPCKETMAP != null) {


                    try {
                        JSONObject location_ = new JSONObject();
                        location_.put("log", addresses.get(0).getLatitude());
                        location_.put("lat", addresses.get(0).getLongitude());


                    /*double[] latLong = new double[2];
                    latLong[0] = addresses.get(0).getLatitude();
                    latLong[1] = addresses.get(0).getLongitude();


                    JSONArray jsonAraay = null;

                    try {
                        jsonAraay = new JSONArray(latLong);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    Log.d("SOCK MAP", String.valueOf(jsonAraay));

                    */
                        JSONObject obj = new JSONObject();
                        obj.put("location", location_);
                        obj.put("fullNameDriver", gloval.getGv_user_name());
                        obj.put("nrDriver",  gloval.getGv_nr_driver());

                        Gson gson = new Gson();
                        String json = gson.toJson(HomeActivity.currentTravel);

                        if(HomeActivity.currentTravel != null) {
                            obj.put("currentTravel",json );
                        }



                        SPCKETMAP.emit("newlocation", obj, new Ack() {


                            @Override
                            public void call(Object... args) {
                                /* Our code */

                                Log.d("SOCK MAP", "newlocation ACTIVE");
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


                /*-----------*/


                if(addresses.size() > 0) {



                    android.location.Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    String strAdd = returnedAddress.getAddressLine(0);
                    HomeFragment.nameLocation = strAdd.toString();


                    mLastLocation = location;
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }


                    if (HomeActivity.currentTravel != null) {
                        if (!isReadyDrawingRouting) {
                            // Initializing
                            MarkerPoints = new ArrayList<>();

                            LatLng origuin = new LatLng(Double.parseDouble(HomeActivity.currentTravel.getLatOrigin()), Double.parseDouble(HomeActivity.currentTravel.getLonOrigin()));
                            setDirection(origuin);


                            if (HomeActivity.currentTravel.getLatDestination() != null) {
                                if (HomeActivity.currentTravel.getLonDestination() != null) {

                                    if (HomeActivity.currentTravel.getLatDestination() != "") {
                                        if (HomeActivity.currentTravel.getLonDestination() != "") {
                                            LatLng desination = new LatLng(Double.parseDouble(HomeActivity.currentTravel.getLatDestination()), Double.parseDouble(HomeActivity.currentTravel.getLonDestination()));
                                            setDirection(desination);
                                            isReadyDrawingRouting = true;
                                        }
                                    }
                                }
                            }
                        }


                    } else {

                        if (MarkerPoints != null) {
                            MarkerPoints.clear();
                        }

                        if (mGoogleMap != null) {
                            mGoogleMap.clear();
                        }

                        isReadyDrawingRouting = false;
                    }

                    //Place current location marker
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);


                    int height = 45;
                    int width = 40;
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.auto);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));


                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);


                    if (HomeActivity.currentTravel != null) {
                        if (HomeActivity.currentTravel.getIdSatatusTravel() == 4 ||
                                HomeActivity.currentTravel.getIdSatatusTravel() == 5
                                ) {
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                        }
                    } else {
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(18));
                    }





                    // SI POSEE UN VIAJE DIBUAMOS LA RUTA QUE ESTA RECORRINEDO EL CHOFER //
                    if (HomeActivity.currentTravel != null) {
                        if(HomeActivity.currentTravel.getIdSatatusTravel() == 5) {


                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(20));

                            HomeFragment.listPosition.add(new LatLng(latLng.latitude, latLng.longitude));
                            HomeFragment.options = new
                                    PolylineOptions()
                                    .width(5)
                                    .color(Color.TRANSPARENT)
                                    .geodesic(true);

                            Log.d("totalDistance listPosition", String.valueOf(HomeFragment.listPosition.size()));


                            for (int z = 0; z < HomeFragment.listPosition.size(); z++) {
                                LatLng point = HomeFragment.listPosition.get(z);

                                if (HomeActivity.isRoundTrip && optionReturnActive == null)//VERIFICAMOS SI ESTA ACTIVA LA VUETA PARA SABER DESDE QUE UBUCACION SE REALIZO
                                {
                                    optionReturnActive = new
                                            PolylineOptions()
                                            .width(5)
                                            .color(Color.TRANSPARENT)
                                            .geodesic(true);
                                    optionReturnActive.add(point);
                                    //Log.d("totalDistance", "'RETORNO ADD");

                                }else {
                                    HomeFragment.options.add(point);
                                   // Log.d("totalDistance HP", String.valueOf(HomeFragment.options.getPoints().size()));


                                }


                            }
                        }else {
                            if(HomeFragment.options != null){
                                HomeFragment.options.getPoints().clear();
                            }
                            HomeFragment.options = null;
                            HomeFragment.listPosition.clear();
                            HomeFragment.listPosition = new ArrayList<>();
                        }



                        double DISTANCE = Utils.round(HomeFragment.calculateMiles(true)[0]* 0.001,2);
                        HomeFragment.txt_distance_real.setText(Utils.round(DISTANCE,2)+"Km");



                        //Polyline line = mGoogleMap.addPolyline(options);
                        //line.setColor(Color.parseColor("#579ea8"));
                        //content_ditanceReal.setVisibility(View.VISIBLE);

                    }else
                    {
                        if(HomeFragment.options != null){
                            HomeFragment.options.getPoints().clear();
                        }
                        HomeFragment.options = null;
                        HomeFragment.listPosition.clear();
                        HomeFragment.listPosition = new ArrayList<>();

                        Log.d("CODUCE NO", String.valueOf(HomeFragment.listPosition.size()));
                        //content_ditanceReal.setVisibility(View.INVISIBLE);
                        HomeFragment.txt_distance_real.setText(0.0+"Km");


                    }


                    //optionally, stop location updates if only current location is needed
                    ///if (mGoogleApiClient != null) {
                    // LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    //}
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }



    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission(){


        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions((Activity) getActivity().getApplicationContext(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) getActivity().getApplicationContext(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity().getApplicationContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public static float[] calculateMiles(boolean IS_CHAGUE_LOCATION) {
        float totalDistance =  0;
        float totalDistanceVuelta =  0;




        if(HomeFragment.options != null)
        {
            for(int i = 1; i < HomeFragment.options.getPoints().size(); i++) {


                Location currLocation = new Location("this");
                currLocation.setLatitude(HomeFragment.options.getPoints().get(i).latitude);
                currLocation.setLongitude(HomeFragment.options.getPoints().get(i).longitude);

                Location lastLocation = new Location("this");
                lastLocation.setLatitude(HomeFragment.options.getPoints().get(i-1).latitude);
                lastLocation.setLongitude(HomeFragment.options.getPoints().get(i-1).longitude);

                totalDistance += lastLocation.distanceTo(currLocation);

                // VERIFICAMOS SI ACTIVO EL RETORNO PARA LA IDA Y VUELTA //
                if(HomeActivity.isRoundTrip) {
                    if (optionReturnActive.getPoints().get(0) != null)
                    {
                        if (optionReturnActive.getPoints().get(0).latitude == HomeFragment.options.getPoints().get(i).latitude) {
                            if (optionReturnActive.getPoints().get(0).longitude == HomeFragment.options.getPoints().get(i).longitude) {
                                Log.d("totalDistance 2", "retorno");

                                totalDistanceVuelta += lastLocation.distanceTo(currLocation);
                            }
                        }
                    }
                }

            }

        }


        /* salvar conexion */
       // double DISTANCE = Utils.round(HomeFragment.calculateMiles(true)[0]* 0.001,2);

      //  float newDistanceSave = 0;
      //  float DistanceSave =  (float) Utils.round(pref.getFloat("distanceTravel", 0),2);

      //  Log.d("DistanceSave ", String.valueOf(totalDistance));
       // Log.d("DistanceSave *", String.valueOf(DistanceSave));

        /*if (totalDistance < DistanceSave) {
            newDistanceSave = (float)(totalDistance + DistanceSave);

            infoGneral.setVisibility(View.VISIBLE);
            infoGneral.setText("Distancia guardada  ("+DistanceSave+")Km ");

        }else {
            infoGneral.setVisibility(View.INVISIBLE);

        }

        if (totalDistance > DistanceSave) {
            newDistanceSave =  (float)Utils.round(DistanceSave,2) + (float) Utils.round(totalDistance -  DistanceSave,2);
        }*/

       /* if(DistanceSave > 0) {
            infodistance.setVisibility(View.VISIBLE);
            infodistance.setText("Distancia guardada  (" + DistanceSave + ")Km ");
        }


        if ((float)Utils.round(totalDistance* 0.001,2) > DistanceSave) {
            newDistanceSave = (float)Utils.round(totalDistance* 0.001,2);
        }

        editor.putFloat("distanceTravel",  newDistanceSave);
        editor.commit();*/


        return  new float[]{totalDistance, totalDistanceVuelta};
    }

    public static void setInfoTravel(InfoTravelEntity currentTravel)
    {



       /* if(currentTravel.isFleetTravelAssistance > 0) {
            infoGneral.setVisibility(View.VISIBLE);
            infoGneral.setText("Necesitas ("+currentTravel.isFleetTravelAssistance+") Ayudantes!");
        }else {
            infoGneral.setText(currentTravel.getNameStatusTravel());
        }*/



        HomeFragment.txt_date_info.setText(currentTravel.getMdate().toString());
        HomeFragment.txt_client_info.setText(currentTravel.getClient());
        HomeFragment.txt_client_info.setText(currentTravel.getClient());


        int param49 = Integer.parseInt(gloval.getGv_param().get(48).getValue());// SE PUEDE VER TELEFONO DE PASAJEROS

        if(currentTravel.getPhoneNumber() != null && param49 == 1) {
            HomeFragment.txt_calling_info.setText(currentTravel.getPhoneNumber());
        }

        HomeFragment.txt_observationFromDriver.setText(currentTravel.getObservationFromDriver());

        HomeFragment.txt_observationFlight.setText(currentTravel.getObsertavtionFlight());

        HomeFragment.txt_pasajeros_info.setText(currentTravel.getPasajero());



        if(currentTravel.getMultiDestination() != null){
            HomeFragment.txt_destination_info.setText(currentTravel.getMultiDestination());
        }else {
            HomeFragment.txt_destination_info.setText(currentTravel.getNameDestination());
        }




        int numOrigin = 0;
        String multiOrigen =
                " 1) "+currentTravel.getOriginMultipleDesc1()+" - "+
                        " 2) "+currentTravel.getOriginMultipleDesc2()+" - "+
                        " 3) "+currentTravel.getOriginMultipleDesc3()+" - "+
                        " 4) "+ currentTravel.getOriginMultipleDesc4();

        if(currentTravel.getOriginMultipleDesc1() != null && currentTravel.getOriginMultipleDesc1() != ""){
            numOrigin = 1;
        }

        if(currentTravel.getOriginMultipleDesc2() != null && currentTravel.getOriginMultipleDesc1() != ""){
            numOrigin = 1;
        }

        if(currentTravel.getOriginMultipleDesc3() != null && currentTravel.getOriginMultipleDesc1() != ""){
            numOrigin = 1;
        }

        if(currentTravel.getOriginMultipleDesc4() != null && currentTravel.getOriginMultipleDesc1() != ""){
            numOrigin = 1;
        }


        if(numOrigin > 0){
            HomeFragment.txt_origin_info.setText(multiOrigen);
        }else {
            HomeFragment.txt_origin_info.setText(currentTravel.getNameOrigin());
        }


        HomeFragment.txt_lote.setText(currentTravel.getLot());
        HomeFragment.txt_flete.setText(String.valueOf(currentTravel.getIsFleetTravelAssistance()));
        HomeFragment.txt_dpto_dialog.setText(currentTravel.getDepartment());
        HomeFragment.txt_piso_dialog.setText(currentTravel.getFLOOR());

        HomeFragment.txt_km_info.setText(currentTravel.getDistanceLabel());

        if(currentTravel.isFleetTravelAssistance > 0) {
            infoGneral.setText("Necesitas ("+currentTravel.isFleetTravelAssistance+") Ayudantes!");
        }else {
            infoGneral.setText(currentTravel.getNameStatusTravel());
        }



        if(currentTravel.getRoundTrip() != null) {
            if(currentTravel.getRoundTrip()) {
                if (currentTravel.getRoundTrip()) {
                    infoGneral.setText("Vuelta Activada!");

                }
            }
        }

        getPick(currentTravel.getIdUserClient());

        infoGneral.setTextColor(Color.parseColor(currentTravel.getClassColorTwo()));

        PARAM_26 =  Integer.parseInt(gloval.getGv_param().get(25).getValue());// PRECIO DE LISTA
        if(currentTravel.getIdSatatusTravel() == 6)
        {
            if(PARAM_26 == 1)
            {
                HomeFragment.txt_amount_info.setText("$"+currentTravel.getTotalAmount());
            }
            else
            {
                HomeFragment.txt_amount_info.setText("$0");
            }

        }else
        {
            if(PARAM_26 == 1)
            {
                HomeFragment.txt_amount_info.setText("$"+currentTravel.getAmountCalculate());
            }
            else
            {
                HomeFragment.txt_amount_info.setText("$0");
            }
        }


    }

    public static void clearInfo() {
        HomeFragment.txt_client_info.setText("No se cargo informacion");
        HomeFragment.txt_calling_info.setText("No se cargo informacion");
        HomeFragment.txt_observationFromDriver.setText("No se cargo informacion");
        HomeFragment.txt_destination_info.setText("No se cargo informacion");
        HomeFragment.txt_origin_info.setText("No se cargo informacion");
        HomeFragment.txt_km_info.setText("0Km");
        HomeFragment.txt_amount_info.setText("$0");
        HomeFragment.txt_pasajeros_info.setText("No se cargo informacion");
        HomeFragment.txt_date_info.setText("");

        infoGneral.setText("SERVICIO ACTIVO");

        }


    // Fetches data from url passed
    private static class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        /**
         * A method to download json data from url
         */
        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();
                Log.d("downloadUrl", data.toString());
                br.close();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }

    }

    private static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color( Color.parseColor( "#5990e9" ) );



            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mGoogleMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }


    /*CARGAMOS LAS IMAGEN DE EL CLIENTE */

    public String getStringImage(Bitmap bmp)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    /* DESCARGAR IMAGEN PERFIL CLIENTE*/

    // METODO OBTENER FOTO DE CLIENTE //
    public static void getPick(int idUserClient)
    {
        HomeFragment.DowloadImg dwImg = new HomeFragment.DowloadImg();
        dwImg.execute(HttpConexion.BASE_URL+HttpConexion.base+"/Frond/img_users/"+idUserClient);

    }





    public static class DowloadImg extends AsyncTask<String, Void, Bitmap> {



        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub
            Log.i("doInBackground" , "Entra en doInBackground");
            String url = params[0]+".JPEG";
            Bitmap imagen = descargarImagen(url);
            return imagen;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);


            ImageView your_imageView = (ImageView) view.findViewById(R.id.img_face_client);

            if(result != null)
            {
                your_imageView.setImageBitmap(result);

            }else
            {
                your_imageView.setImageResource(R.drawable.noimg);
            }



        }

        private Bitmap descargarImagen (String imageHttpAddress){
            URL imageUrl = null;
            Bitmap imagen = null;
            try{
                imageUrl = new URL(imageHttpAddress);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.connect();
                imagen = BitmapFactory.decodeStream(conn.getInputStream());
            }catch(IOException ex){
                ex.printStackTrace();
            }

            return imagen;
        }

    }
    /*******************/

}