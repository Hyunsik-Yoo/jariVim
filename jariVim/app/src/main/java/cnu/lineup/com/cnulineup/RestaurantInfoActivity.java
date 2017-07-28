package cnu.lineup.com.cnulineup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;

public class RestaurantInfoActivity extends AppCompatActivity {
    public static SupportMapFragment mapInfo;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        // 전달받은 인자저장 (식당이름)
        Bundle argument = getIntent().getExtras();
        if(argument != null)
            title = argument.getString("title");


        //title로 가게상세정보 모두 로드하기 필요(위도, 경도, 메뉴, 전화번호)
        mapInfo = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_info);

        mapInfo.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng restaurant = new LatLng(0, 0);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurant,16));
            }
        });
    }
}
