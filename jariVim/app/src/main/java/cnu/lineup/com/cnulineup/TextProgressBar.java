package cnu.lineup.com.cnulineup;

import android.content.Context;
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
    private String text_Subject;
    private String text_Peoloe;
    private Paint paint_Subject;
    private Paint paint_People;


    public TextProgressBar(Context context){
        super(context);
        text_Subject = "가게이름";
        paint_Subject = new Paint();
        paint_Subject.setColor(Color.BLACK);
        paint_Subject.setTextSize(40);

        text_Peoloe = "사람%";
        paint_People = new Paint();
        paint_People.setColor(Color.BLACK);
        paint_People.setTextSize(40);

    }

    public TextProgressBar(Context context, AttributeSet attrs){
        super(context,attrs);
        text_Subject = "가게이름";
        paint_Subject = new Paint();
        paint_Subject.setColor(Color.BLACK);
        paint_Subject.setTextSize(40);

        text_Peoloe = "사람%";
        paint_People = new Paint();
        paint_People.setColor(Color.BLACK);
        paint_People.setTextSize(40);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        text_Subject = "가게이름";
        paint_Subject = new Paint();
        paint_Subject.setColor(Color.BLACK);
        paint_Subject.setTextSize(40);

        text_Peoloe = "사람%";
        paint_People = new Paint();
        paint_People.setColor(Color.BLACK);
        paint_People.setTextSize(40);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //가게이름 텍스트 그리기
        Rect rect_Subject = new Rect();
        paint_Subject.getTextBounds(text_Subject,0, text_Subject.length(), rect_Subject);

        int x = canvas.getClipBounds().left;
        int y = getHeight() / 2 - rect_Subject.centerY();
        canvas.drawText(text_Subject, x, y, paint_Subject);

        //사람% 텍스트 그리기
        Rect rect_People = new Rect();
        paint_People.getTextBounds(text_Peoloe, 0, text_Peoloe.length(), rect_People);
        x = canvas.getClipBounds().right-150;
        y = getHeight() / 2 - rect_People.centerY();
        canvas.drawText(text_Peoloe, x, y, paint_People);
    }

    public synchronized void setText(String text) {
        this.text_Subject = text;
        drawableStateChanged();
    }

    public void setTextColor(int color) {
        paint_Subject.setColor(color);
        drawableStateChanged();
    }
}
