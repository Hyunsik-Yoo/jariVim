package cnu.lineup.com.cnulineup;

import android.widget.Button;
import android.widget.SeekBar;

/**
 * Created by macgongmon on 7/18/16.
 */

public class Child {
    private SeekBar seekBar;
    private Button confirm;
    private Button cancle;

    public SeekBar getSeekBar() {
        return seekBar;
    }

    public Button getConfirm() {
        return confirm;
    }

    public Button getCancle() {
        return cancle;
    }

    public void setSeekBar(SeekBar seekBar)
    {
        this.seekBar = seekBar;
    }

    public void setConfirm(Button confirm) {
        this.confirm = confirm;
    }

    public void setCancle(Button cancle) {
        this.cancle = cancle;
    }
}
