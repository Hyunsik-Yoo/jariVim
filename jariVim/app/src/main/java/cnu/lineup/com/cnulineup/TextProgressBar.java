package cnu.lineup.com.cnulineup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by dbgus on 2016-07-27.
 */
public class TextProgressBar extends ProgressBar{
    private String text;
    private Paint textPaint;

    public TextProgressBar(Context context){
        super(context);
        text = "가게이름";
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
    }

    public TextProgressBar(Context context, AttributeSet attrs){
        super(context,attrs);
        text = "가게이름";
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        text = "가게이름";
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect bounds = new Rect();
        textPaint.getTextBounds(text,0,text.length(),bounds);
        int x = getWidth() / 2 - bounds.centerX();
        int y = getHeight() / 2 - bounds.centerY();
        canvas.drawText(text, x, y, textPaint);
    }

    public synchronized void setText(String text) {
        this.text = text;
        drawableStateChanged();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        drawableStateChanged();
    }
}
