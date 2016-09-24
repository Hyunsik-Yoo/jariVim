package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;

public class MainActivity extends Activity {

    Button btn_bob, btn_noddle, btn_cafe, btn_drink, btn_fastfood, btn_fork;
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = (TabHost)findViewById(R.id.footer);

        btn_bob = (Button)findViewById(R.id.btn_bob);
        btn_bob.setOnClickListener(listener_category);
        btn_noddle = (Button)findViewById(R.id.btn_noddle);
        btn_noddle.setOnClickListener(listener_category);
        btn_cafe = (Button)findViewById(R.id.btn_cafe);
        btn_cafe.setOnClickListener(listener_category);
        btn_drink = (Button)findViewById(R.id.btn_drink);
        btn_drink.setOnClickListener(listener_category);
        btn_fastfood = (Button)findViewById(R.id.btn_fastfood);
        btn_fastfood.setOnClickListener(listener_category);
        btn_fork = (Button)findViewById(R.id.btn_meat);
        btn_fork.setOnClickListener(listener_category);

        ImageView tab_home_icon = new ImageView(this);
        tab_home_icon.setImageResource(R.drawable.selector_tab_home);
        ImageView tab_info_icon = new ImageView(this);
        tab_info_icon.setImageResource(R.drawable.selector_tab_info);
        ImageView tab_account_icon = new ImageView(this);
        tab_account_icon.setImageResource(R.drawable.selector_tab_account);
        ImageView tab_statistics_icon = new ImageView(this);
        tab_statistics_icon.setImageResource(R.drawable.selector_tab_statistics);


        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Tab1").setContent(R.id.tab1)
                .setIndicator(tab_home_icon);
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Tab2").setContent(R.id.tab2)
                .setIndicator(tab_info_icon);
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Tab3").setContent(R.id.tab3)
                .setIndicator(tab_statistics_icon);
        TabHost.TabSpec tab4 = tabHost.newTabSpec("Tab4").setContent(R.id.tab4)
                .setIndicator(tab_account_icon);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);

    }

    Button.OnClickListener listener_category = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent category_intent = new Intent(MainActivity.this, List_Activity.class);
            String category_name=null;
            switch (view.getId()){
                case R.id.btn_bob:
                    category_name = "bob";
                    break;
                case R.id.btn_noddle:
                    category_name = "noddle";
                    break;
                case R.id.btn_fastfood:
                    category_name = "fastfood";
                    break;
                case R.id.btn_meat:
                    category_name = "meat";
                    break;
                case R.id.btn_cafe:
                    category_name = "cafe";
                    break;
                case R.id.btn_drink:
                    category_name = "drink";
                    break;
            }
            category_intent.putExtra("category",category_name);
            startActivity(category_intent);
        }
    };

}
