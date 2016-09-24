package cnu.lineup.com.cnulineup;

import android.widget.Button;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by macgongmon on 7/18/16.
 */

public class Group {

    private String name;
    private int proportion;
    private Child items;

    public String getName(){
        return name;
    }

    public int getProportion(){
        return proportion;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setProportion(int proportion) { this.proportion = proportion;}

    public Child getItems(){
        return items;
    }

    public void setItems(Child items){
        this.items = items;
    }
}
