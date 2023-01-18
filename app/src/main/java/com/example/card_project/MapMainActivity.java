package com.example.card_project;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.card_project.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;


import noman.googleplaces.NRPlaces;
import noman.googleplaces.Place;
import noman.googleplaces.PlaceType;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;
public class MapMainActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, PlacesListener {

    String data;
    private GoogleMap mMap;
    private Marker currentMarker = null;
    Button handle_btn;
    EditText edit;
    String getedit; //약국 동이름으로 검색하는 edittext

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 1000; //권한 설정을 한 activity에 request값으로 받아올 변수 설정
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션 정의
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소

    Location mCurrentLocatiion;
    LatLng currentPosition;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private View mLayout;  // Snackbar 사용하기 위해서 View가 필요
    List<Marker> previous_marker = null; //google place에서 얻어온 약국 마커 표시

    @Override
    public void onBackPressed() {
        // 버튼을 누르면 메인화면으로 이동
        myStartActivity();
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.map_activity_main);

        previous_marker = new ArrayList<Marker>();




        mLayout = findViewById(R.id.layout_main);
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // 홈으로 이동하는 버튼 객체 생성
        ImageButton btn_home = findViewById(R.id.gohome);

        // 홈 버튼 onclicklistener 생성
        btn_home.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 버튼을 누르면 메인화면으로 이동
                myStartActivity();
            }

        });

    }


    //맵이 실행됐을때 처리 과정
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 위치 퍼미션을 가지고 있는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            startLocationUpdates(); // 이미 퍼미션 가지고 있다면 위치 업데이트 시작
        }

        else {  //퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요.

            // 사용자가 퍼미션 거부를 한 적이 있는 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                //요청을 진행하기 전에 사용자에게 접근 권한이 필요함을 알림
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        //사용자게에 퍼미션 요청을 함 요청 결과는 onRequestPermissionResult에서 수신
                        ActivityCompat.requestPermissions(MapMainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();

            } else {
                //사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 함
                //요청 결과는 onRequestPermissionResult에서 수신
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }
        //결과 맵에 띄우기
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                LatLng seoulstation = new LatLng(37.555200, 126.970771);
                mMap.addMarker(new MarkerOptions().position(seoulstation).title("Marker in seoulstation"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(seoulstation));

                LatLng hospital = new LatLng(37.474533785051094, 126.6311918103229);
                mMap.addMarker(new MarkerOptions().position(hospital).title("가천의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(hospital));

                LatLng a = new LatLng(36.5050676658604, 127.27136011904662);
                mMap.addMarker(new MarkerOptions().position(a).title("단국대학교 세종치과병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(a));

                LatLng b = new LatLng(36.84206751718471, 127.1722729656092);
                mMap.addMarker(new MarkerOptions().position(b).title("단국대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(b));

                LatLng c = new LatLng(36.833335587175235, 127.16647327116407);
                mMap.addMarker(new MarkerOptions().position(c).title("단국대학교 치과대학병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(c));

                LatLng d = new LatLng(35.93509811309539, 128.5495330343903);
                mMap.addMarker(new MarkerOptions().position(d).title("대구가톨릭대학교 칠곡가톨릭병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(d));

                LatLng e = new LatLng(35.84369478794844, 128.56799787366438);
                mMap.addMarker(new MarkerOptions().position(e).title("대구가톨릭대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(e));

                LatLng f = new LatLng(35.8377221388787, 128.60732279629707);
                mMap.addMarker(new MarkerOptions().position(f).title("대구한의대학부속대구한방병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(f));

                LatLng g = new LatLng(36.35534428106782, 127.38194362698444);
                mMap.addMarker(new MarkerOptions().position(g).title("을지대학교 대전병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(g));

                LatLng h = new LatLng(35.85843173521344, 129.19675654232813);
                mMap.addMarker(new MarkerOptions().position(h).title("동국대학교 경주병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(h));

                LatLng i = new LatLng(35.85839695374296, 129.19671362698446);
                mMap.addMarker(new MarkerOptions().position(i).title("동국대학교 경주병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(i));

                LatLng j = new LatLng(37.367389483485454, 127.12782279629708);
                mMap.addMarker(new MarkerOptions().position(j).title("동국대학교 분당한방병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(j));

                LatLng k = new LatLng(37.676317983239, 126.80672545767185);
                mMap.addMarker(new MarkerOptions().position(k).title("동국대학교 일산병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(k));

                LatLng l = new LatLng(37.67635194970533, 126.80676837301553);
                mMap.addMarker(new MarkerOptions().position(l).title("동국대학교의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(l));

                LatLng m = new LatLng(35.12045196079766, 129.01797754232817);
                mMap.addMarker(new MarkerOptions().position(m).title("동아대학교대신요양병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(m));

                LatLng n = new LatLng(35.120170063906826, 129.01754354232818);
                mMap.addMarker(new MarkerOptions().position(n).title("동아대학교"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(n));

                LatLng o = new LatLng(35.169905702653494, 129.07685737301554);
                mMap.addMarker(new MarkerOptions().position(o).title("동의의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(o));

                LatLng p = new LatLng(37.279193964486396, 127.04647617577712);
                mMap.addMarker(new MarkerOptions().position(p).title("아주대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(p));

                LatLng q = new LatLng(37.278884442017905, 127.04908092580312);
                mMap.addMarker(new MarkerOptions().position(q).title("아주대학교요양병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(q));

                LatLng qs = new LatLng(37.27907446388876, 127.04739307472934);
                mMap.addMarker(new MarkerOptions().position(qs).title("아주대학교의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qs));

                LatLng r = new LatLng(35.32803354944057, 129.0063255629193);
                mMap.addMarker(new MarkerOptions().position(r).title("양산부산대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(r));

                LatLng s = new LatLng(37.562363493011226, 126.94086522259674);
                mMap.addMarker(new MarkerOptions().position(s).title("연세대학교 의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(s));

                LatLng t = new LatLng(35.847332363561414, 128.5848885690168);
                mMap.addMarker(new MarkerOptions().position(t).title("영남대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(t));

                LatLng u = new LatLng(35.95740101551223, 128.9131940905551);
                mMap.addMarker(new MarkerOptions().position(u).title("영남대학교영천병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(u));

                LatLng v = new LatLng(35.84732694758766, 128.58488915718786);
                mMap.addMarker(new MarkerOptions().position(v).title("영남대학교의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(v));

                LatLng w = new LatLng(37.27106349485342, 127.14822246120855);
                mMap.addMarker(new MarkerOptions().position(w).title("용인세브란스병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(w));

                LatLng x = new LatLng(35.52016772678639, 129.42896009762777);
                mMap.addMarker(new MarkerOptions().position(x).title("울산대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(x));

                LatLng y = new LatLng(37.35939855421916, 126.93359729963028);
                mMap.addMarker(new MarkerOptions().position(y).title("원광대학교 산본병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(y));

                LatLng z = new LatLng(37.359858513235665, 126.9332927314581);
                mMap.addMarker(new MarkerOptions().position(z).title("원광대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(z));

                LatLng qw = new LatLng(37.34793204558687, 127.94586554630652);
                mMap.addMarker(new MarkerOptions().position(qw).title("원주세브란스기독병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qw));

                LatLng we = new LatLng(37.63671419242891, 127.07023606854756);
                mMap.addMarker(new MarkerOptions().position(we).title("을지대학교의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(we));

                LatLng er = new LatLng(37.751503899961314, 127.05087027388949);
                mMap.addMarker(new MarkerOptions().position(er).title("의정부을지대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(er));

                LatLng rt = new LatLng(37.536460580731685, 126.88642524809966);
                mMap.addMarker(new MarkerOptions().position(rt).title("이화여자대학교 목동병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(rt));

                LatLng qa = new LatLng(37.53641449485869, 126.88641261383484);
                mMap.addMarker(new MarkerOptions().position(qa).title("이화여자대학교 서울병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qa));

                LatLng ty = new LatLng(37.53657291492781, 126.88698290008978);
                mMap.addMarker(new MarkerOptions().position(ty).title("이화여자대학교 의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ty));

                LatLng yu = new LatLng(35.16837063116074, 129.06301653158596);
                mMap.addMarker(new MarkerOptions().position(yu).title("인제대학교 백중앙의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yu));

                LatLng ui = new LatLng(35.14631513659804, 129.02083371110916);
                mMap.addMarker(new MarkerOptions().position(ui).title("인제대학교 부산백병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ui));

                LatLng io = new LatLng(35.14568115114319, 129.020794514194);
                mMap.addMarker(new MarkerOptions().position(io).title("인제대학교 상계백병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(io));

                LatLng op = new LatLng(37.56511988066596, 126.98873485620007);
                mMap.addMarker(new MarkerOptions().position(op).title("인제대학교 서울백병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(op));

                LatLng pa = new LatLng(37.674443584050714, 126.75032844002035);
                mMap.addMarker(new MarkerOptions().position(pa).title("인제대학교 일산백병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pa));

                LatLng as = new LatLng(35.17366857759985, 129.1819788976177);
                mMap.addMarker(new MarkerOptions().position(as).title("인제대학교 해운대백병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(as));

                LatLng sd = new LatLng(37.45895594289111, 126.63416011302904);
                mMap.addMarker(new MarkerOptions().position(sd).title("인하대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sd));

                LatLng df = new LatLng(35.14214872624027, 126.92167892990967);
                mMap.addMarker(new MarkerOptions().position(df).title("전남대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(df));

                LatLng fg = new LatLng(35.846924265872474, 127.14111385650872);
                mMap.addMarker(new MarkerOptions().position(fg).title("전북대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(fg));

                LatLng gh = new LatLng(33.4671950311254, 126.54568079571816);
                mMap.addMarker(new MarkerOptions().position(gh).title("제주대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(gh));

                LatLng hj = new LatLng(35.13877137922619, 126.92666716878065);
                mMap.addMarker(new MarkerOptions().position(hj).title("조선대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(hj));

                LatLng jk = new LatLng(35.144617575238875, 126.92683385528856);
                mMap.addMarker(new MarkerOptions().position(jk).title("조선대학교치과병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(jk));

                LatLng kl = new LatLng(37.50684998729248, 126.96076373034322);
                mMap.addMarker(new MarkerOptions().position(kl).title("중앙대학교광명병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(kl));

                LatLng lz = new LatLng(37.50686392306179, 126.96081143126987);
                mMap.addMarker(new MarkerOptions().position(lz).title("중앙대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(lz));

                LatLng zx = new LatLng(37.5068519956157, 126.96080930037243);
                mMap.addMarker(new MarkerOptions().position(zx).title("중앙대학교의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(zx));

                LatLng xc = new LatLng(36.11482267771799, 128.34073678777318);
                mMap.addMarker(new MarkerOptions().position(xc).title("차의과학대학교 부속 구미차병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(xc));

                LatLng cv = new LatLng(37.410222961504225, 127.1254905266734);
                mMap.addMarker(new MarkerOptions().position(cv).title("차의과학대학교 분당차병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(cv));

                LatLng vb = new LatLng(37.65447774425658, 126.77587000215385);
                mMap.addMarker(new MarkerOptions().position(vb).title("차의과학대학교 일산차병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(vb));

                LatLng bn = new LatLng(35.19883848887895, 128.70812002255872);
                mMap.addMarker(new MarkerOptions().position(bn).title("창원경상국립대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(bn));

                LatLng nm = new LatLng(36.31681678704056, 127.41622305605928);
                mMap.addMarker(new MarkerOptions().position(nm).title("충남대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(nm));

                LatLng mq = new LatLng(36.62398589264294, 127.46156839008383);
                mMap.addMarker(new MarkerOptions().position(mq).title("충북대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mq));

                LatLng qe = new LatLng(35.956807618765005, 128.56435843703073);
                mMap.addMarker(new MarkerOptions().position(qe).title("칠곡경북대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qe));

                LatLng qr = new LatLng(37.352185823017486, 127.12315819768223);
                mMap.addMarker(new MarkerOptions().position(qr).title("분당서울대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qr));

                LatLng qt = new LatLng(37.3519043793778, 127.12266467123142);
                mMap.addMarker(new MarkerOptions().position(qt).title("서울대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qt));

                LatLng qy = new LatLng(35.24302195353328, 128.59190729761968);
                mMap.addMarker(new MarkerOptions().position(qy).title("성균관대학교 삼성창원병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qy));

                LatLng qu = new LatLng(36.97102857054493, 127.91529718232711);
                mMap.addMarker(new MarkerOptions().position(qu).title("세명대학교 충주한방병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qu));

                LatLng qi = new LatLng(37.49283535611663, 127.04652429514333);
                mMap.addMarker(new MarkerOptions().position(qi).title("세브란스병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qi));

                LatLng qo = new LatLng(36.51924056241414, 127.25827832363952);
                mMap.addMarker(new MarkerOptions().position(qo).title("세종충남대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qo));

                LatLng qp = new LatLng(37.37817127333138, 126.6886071303941);
                mMap.addMarker(new MarkerOptions().position(qp).title("송도세브란스병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qp));

                LatLng qd = new LatLng(36.10312319155305, 128.3827615553165);
                mMap.addMarker(new MarkerOptions().position(qd).title("순천향대학교 부속 구미병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qd));

                LatLng qf = new LatLng(37.4985508945808, 126.76287505535859);
                mMap.addMarker(new MarkerOptions().position(qf).title("순천향대학교 부속 부천병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qf));

                LatLng qg = new LatLng(37.53408011375758, 127.004312140016);
                mMap.addMarker(new MarkerOptions().position(qg).title("순천향대학교 부속 서울병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qg));

                LatLng qj = new LatLng(36.80302661987815, 127.1363642976658);
                mMap.addMarker(new MarkerOptions().position(qj).title("순천향대학교 부속 천안병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qj));

                LatLng qh = new LatLng(36.76994457725081, 126.93175279766474);
                mMap.addMarker(new MarkerOptions().position(qh).title("순천향대학교 중앙의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qh));

                LatLng qk = new LatLng(37.45243770120189, 126.707491299712);
                mMap.addMarker(new MarkerOptions().position(qk).title("가천대학교 의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qk));

                LatLng ql = new LatLng(37.542654938599235, 126.68377758234456);
                mMap.addMarker(new MarkerOptions().position(ql).title("가톨릭관동대학교 국제성모병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ql));

                LatLng qz = new LatLng(36.322935274527474, 127.42024539765129);
                mMap.addMarker(new MarkerOptions().position(qz).title("가톨릭대학교 대전성모병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qz));

                LatLng qx = new LatLng(37.48711367831946, 126.79229316699934);
                mMap.addMarker(new MarkerOptions().position(qx).title("가톨릭대학교 부천성모병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qx));

                LatLng qc = new LatLng(37.501829582003964, 127.00478130134366);
                mMap.addMarker(new MarkerOptions().position(qc).title("가톨릭대학교 서울성모병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qc));

                LatLng qv = new LatLng(37.57951372829324, 127.0434540265251);
                mMap.addMarker(new MarkerOptions().position(qv).title("가톨릭대학교 성바오로병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qv));

                LatLng qb = new LatLng(37.278284145433034, 127.02776734000835);
                mMap.addMarker(new MarkerOptions().position(qb).title("가톨릭대학교 성빈센트병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qb));

                LatLng qn = new LatLng(37.517899460674535, 126.9355333186582);
                mMap.addMarker(new MarkerOptions().position(qn).title("가톨릭대학교 여의도성모병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qn));

                LatLng qm = new LatLng(37.6340202232243, 126.91655919769084);
                mMap.addMarker(new MarkerOptions().position(qm).title("가톨릭대학교 은평성모병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(qm));

                LatLng wr = new LatLng(37.75876532343073, 127.07771409769472);
                mMap.addMarker(new MarkerOptions().position(wr).title("가톨릭대학교 의정부성모병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wr));

                LatLng wt = new LatLng(37.485023562785436, 126.72490041302987);
                mMap.addMarker(new MarkerOptions().position(wt).title("가톨릭대학교 인천성모병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wt));

                LatLng wy = new LatLng(37.4929062313324, 127.04626609768663);
                mMap.addMarker(new MarkerOptions().position(wy).title("강남세브란스병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wy));

                LatLng wu = new LatLng(37.519408987512236, 127.02858200052462);
                mMap.addMarker(new MarkerOptions().position(wu).title("강남을지대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wu));

                LatLng wi = new LatLng(37.553603158906824, 127.15763606885248);
                mMap.addMarker(new MarkerOptions().position(wi).title("강동경희대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wi));

                LatLng wo = new LatLng(37.765267969608864, 128.86978978667543);
                mMap.addMarker(new MarkerOptions().position(wo).title("강릉원주대학교치과병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wo));

                LatLng wp = new LatLng(37.874972356569415, 127.74503051304168);
                mMap.addMarker(new MarkerOptions().position(wp).title("강원대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wp));

                LatLng wa = new LatLng(36.98008778859172, 127.9285384972932);
                mMap.addMarker(new MarkerOptions().position(wa).title("건국대학교 충주병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wa));

                LatLng ws = new LatLng(37.54097760747986, 127.07226381303161);
                mMap.addMarker(new MarkerOptions().position(ws).title("건국대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ws));

                LatLng wd = new LatLng(36.30693790646374, 127.34225029765086);
                mMap.addMarker(new MarkerOptions().position(wd).title("건양대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wd));

                LatLng wf = new LatLng(36.275883820341505, 126.9016747823064);
                mMap.addMarker(new MarkerOptions().position(wf).title("건양대학교부여병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wf));

                LatLng wg = new LatLng(36.30782512367509, 127.34276304327267);
                mMap.addMarker(new MarkerOptions().position(wg).title("건양대학교의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wg));

                LatLng wh = new LatLng(35.207058657674835, 128.710201325149);
                mMap.addMarker(new MarkerOptions().position(wh).title("경상국립대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wh));

                LatLng wj = new LatLng(37.59401149534728, 127.05136641303329);
                mMap.addMarker(new MarkerOptions().position(wj).title("경희의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wj));

                LatLng wk = new LatLng(35.84569123493184, 129.20943539763726 );
                mMap.addMarker(new MarkerOptions().position(wk).title("계명대학교 경주동산병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wk));

                LatLng wl = new LatLng(35.84567384140391, 129.20943539763726 );
                mMap.addMarker(new MarkerOptions().position(wl).title("계명대학교 경주동산요양병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wl));

                LatLng wz = new LatLng(35.86878996492923, 128.58292049763793 );
                mMap.addMarker(new MarkerOptions().position(wz).title("계명대학교 대구동산병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wz));

                LatLng wx = new LatLng(35.86880735338609, 128.58292049763793 );
                mMap.addMarker(new MarkerOptions().position(wx).title("계명대학교 동산병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wx));

                LatLng wc = new LatLng(37.49233645674476, 126.88479759398412);
                mMap.addMarker(new MarkerOptions().position(wc).title("고려대학교 구로병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wc));

                LatLng wv = new LatLng(37.31921860312447, 126.82530801302485);
                mMap.addMarker(new MarkerOptions().position(wv).title("고려대학교 안산병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wv));

                LatLng wb = new LatLng(37.587126598003934, 127.02658311303303);
                mMap.addMarker(new MarkerOptions().position(wb).title("고려대학교 안암병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wb));

                LatLng wn = new LatLng(37.58717760888321, 127.02652946885358);
                mMap.addMarker(new MarkerOptions().position(wn).title("고려대학교의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wn));

                LatLng ey = new LatLng(35.08043439994699, 129.01591976877904);
                mMap.addMarker(new MarkerOptions().position(ey).title("고신대학교 복음병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ey));

                LatLng wm = new LatLng(37.63632900000001, 127.06983574603065);
                mMap.addMarker(new MarkerOptions().position(wm).title("노원을지대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(wm));

                LatLng et = new LatLng(37.52342693170839, 126.9098727553759);
                mMap.addMarker(new MarkerOptions().position(et).title("한림대학교 강남성심병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(et));

                LatLng eu = new LatLng(37.216834558708015, 127.08012089767823);
                mMap.addMarker(new MarkerOptions().position(eu).title("한림대학교 동탄성심병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(eu));

                LatLng ei = new LatLng(37.21681747084113, 127.08014235535);
                mMap.addMarker(new MarkerOptions().position(ei).title("한림대학교 성심병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ei));

                LatLng eo = new LatLng(37.21798798077442, 127.07786784214188);
                mMap.addMarker(new MarkerOptions().position(eo).title("한림대학교 의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(eo));

                LatLng ep = new LatLng(37.884203260836536, 127.73987088235504);
                mMap.addMarker(new MarkerOptions().position(ep).title("한림대학교 춘천성심병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ep));

                LatLng ea = new LatLng(37.884194793065696, 127.73976359399616);
                mMap.addMarker(new MarkerOptions().position(ea).title("한림대학교 한강성심병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ea));

                LatLng es = new LatLng(37.601337525599405, 127.13242196953568);
                mMap.addMarker(new MarkerOptions().position(es).title("한양대학교 구리병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(es));

                LatLng ed = new LatLng(37.55449285636909, 127.04658957440795);
                mMap.addMarker(new MarkerOptions().position(ed).title("한양대학교 국제병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ed));

                LatLng ef = new LatLng(37.55993706454316, 127.04478158581819);
                mMap.addMarker(new MarkerOptions().position(ef).title("한양대학교 류마티스병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ef));

                LatLng eg = new LatLng(37.559690421434205, 127.04395546545487);
                mMap.addMarker(new MarkerOptions().position(eg).title("한양대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(eg));

                LatLng eh = new LatLng(37.55950756308124, 127.04390965520179);
                mMap.addMarker(new MarkerOptions().position(eh).title("한양대학교의료원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(eh));

                LatLng ej = new LatLng(35.05938857438505, 127.00264284496242);
                mMap.addMarker(new MarkerOptions().position(ej).title("화순전남대학교병원"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ej));





            }

        });
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);

                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);
                mCurrentLocatiion = location;
            }
        }
    };

    //gps서비스 상태 파악, 현재 위치 업데이트
    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();//gps비활성화 상태일때 서비스 세팅 메서드 호출
        }
        else {
            //gps활성화 되어있으면 퍼미션 확인
            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {
                return;
            }

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }

    }

    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    // 지오코더로 gps를 주소로 변환함. 위도와 경도를 받아온 후에 onMap에서 실행해주면 현재 위치로 이동함
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    //gps서비스 상태를 반환하는 메서드
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //현재 위치 받고 맵에 마커생성 메서드
    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        if (currentMarker != null) currentMarker.remove();

        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        currentMarker = mMap.addMarker(markerOptions);

        //현재 위치를 중심으로 카메라 이동
        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        //mMap.moveCamera(cameraUpdate);

    }

    //지도 실행됐을때 1초정도 보이는 기본 위치
    public void setDefaultLocation() {

        //디폴트 위치, 서울역으로 지정
        LatLng DEFAULT_LOCATION = new LatLng(37.553321, 126.972627); //서울역의 위도, 경도
        String markerTitle = "서울역";
        String markerSnippet = "현재위치를 보려면 현재위치 버튼을 클릭하세요!";

        if (currentMarker != null) currentMarker.remove();


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15); //zoom 설정 1~21까지
        mMap.moveCamera(cameraUpdate);

    }

    //런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }


    //ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메서드
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 퍼미션 허용됐는지 체크
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작
                startLocationUpdates();
            } else {

                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료함
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    //사용자가 퍼미션을 거부했을때 뜨는 메시지
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // 버튼을 누르면 메인화면으로 이동
                            myStartActivity();
                        }
                    }).show();

                } else {

                    //사용자가 "다시 묻지 않음"을 누르고 퍼미션을 거부하면 설정-앱 정보에서 퍼미션을 허용해야 사용 가능함을 알림
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 활성화하려면 설정-앱 정보 에서 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            // 버튼을 누르면 메인메뉴화면으로 이동
                            myStartActivity();
                        }
                    }).show();
                }
            }

        }
    }


    //GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapMainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성화 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;

                        return;
                    }
                }
                break;
        }
    }


    //PlaceListener가 요구하는 메서드 4개
    @Override
    public void onPlacesFailure(PlacesException e) { }

    @Override
    public void onPlacesStart() { }

    @Override //구글 플레이스에서 가져온 정보 지도에 표시하기
    public void onPlacesSuccess(final List<Place> places) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (noman.googleplaces.Place place : places) {

                    LatLng latLng
                            = new LatLng(place.getLatitude()
                            , place.getLongitude());

                    String markerSnippet = getCurrentAddress(latLng);

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.title(place.getName());
                    markerOptions.snippet(markerSnippet);

                    //약국 마커 아이콘 바꾸기
                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_baseline_add_location_24);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 100, 150, false);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    Marker item = mMap.addMarker(markerOptions);

                    previous_marker.add(item);
                }

                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);

            }
        });

    }

    @Override
    public void onPlacesFinished() { }

    public void showPlaceInformaiton(LatLng location)
    {
        mMap.clear();//지도 클리어

        if (previous_marker != null)
            previous_marker.clear();//지역정보 마커 클리어

        new NRPlaces.Builder()
                .listener(MapMainActivity.this)
                .key("AIzaSyDd9ZoPqAJSM9VH0Sbun-MKeS2JMl--wBs")
                .latlng(location.latitude, location.longitude)//현재 위치
                .radius(2500)// 반경
                .type(PlaceType.PHARMACY) //약국
                .build()
                .execute();
    }





    //메인화면으로 스택이 쌓이지 않고 이동하는 메서드
    private void myStartActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}