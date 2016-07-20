package cnu.lineup.com.cnulineup;

import android.widget.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by macgongmon on 7/18/16.
 */

public class Group {

    private String name;
    private Child items;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Child getItems(){
        return items;
    }

    public void setItems(Child items){
        this.items = items;
    }
}
