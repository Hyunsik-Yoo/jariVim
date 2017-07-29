package cnu.lineup.com.cnulineup;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestaurantInfoActivity extends AppCompatActivity {
    public static SupportMapFragment mapInfo;
    private Typeface customFont;
    TextView textPhone;
    String title, phoneNumber, menu;
    double longitude, latitude;
    int proportion;
    private int lastExpandedPosition = -1;
    private ExpandListAdapter expAdapter;
    private ExpandableListView ListMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_info);

        customFont = Typeface.createFromAsset(getAssets(),"fonts/BMHANNA.ttf");
        textPhone = (TextView)findViewById(R.id.phone_number);
        ListMain = (ExpandableListView)findViewById(R.id.list_main);

        // 전달받은 인자저장 (식당이름)
        Bundle argument = getIntent().getExtras();
        if (argument != null)
            title = argument.getString("title");

        JSONObject restInfo = UtilMethod.getOneRest(title);

        try {
            longitude = Double.parseDouble(restInfo.getString("longitude"));
            latitude = Double.parseDouble(restInfo.getString("latitude"));
            phoneNumber = restInfo.getString("phone_number");
            menu = restInfo.getString("menu");
            proportion = restInfo.getInt("proportion");
        }
        catch (JSONException e){
            longitude=0;
            latitude=0;
            phoneNumber="번호없음";
            menu="";
            proportion=0;
            e.printStackTrace();
        }

        Group group = new Group();
        group.setName(title);
        group.setProportion(proportion);
        ArrayList<Group> list_group = new ArrayList<Group>();
        list_group.add(group);
        setExpandListAdapter(ListMain, list_group);

        textPhone.setText("전화번호 : " + phoneNumber);
        textPhone.setTypeface(customFont);


        //title로 가게상세정보 모두 로드하기 필요(위도, 경도, 메뉴, 전화번호)
        mapInfo = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_info);

        mapInfo.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng restaurant = new LatLng(longitude, latitude);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurant, 16));
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(longitude,latitude))
                        .title(title));
            }
        });
    }

    public void setExpandListAdapter(final ExpandableListView expandableListView, ArrayList<Group> ExpListItems) {
        expAdapter = new ExpandListAdapter(RestaurantInfoActivity.this, ExpListItems, expandableListView);
        expandableListView.setAdapter(expAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }
}
