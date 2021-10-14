package com.itheima.mobilesafe.home.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.itheima.mobilesafe.R;

import java.io.InputStream;

public class ArcProgressBar extends View {
    private Paint paint;
    private int textColor;
    private float textSize;
    private int max;
    private int progress;
    private boolean isDisplayText;
    private String title;
    private Bitmap bmpTemp = null;
    private int degrees;

    public ArcProgressBar(Context context) {
        this(context, null);
    }

    public ArcProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        degrees = 0;
        paint = new Paint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XCRoundProgressBar);
        textColor = typedArray.getColor(R.styleable.XCRoundProgressBar_textColor, Color.RED);
        textSize = typedArray.getDimension(R.styleable.XCRoundProgressBar_textSize, 15);
        max = typedArray.getInteger(R.styleable.XCRoundProgressBar_max, 100);
        isDisplayText = typedArray.getBoolean(R.styleable.XCRoundProgressBar_textIsDisplayable, true);
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas){
        int width = getWidth();
        super.onDraw(canvas);
        int height = getHeight();
        int centerX = getWidth() / 2;
        int centerY = getHeight() /2;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(bitmap);
        bmpTemp = decodeCustomRes(getContext(), R.drawable.arc_bg);
        float dstWidth = (float) width;
        float dstHeight = (float) height;
        int srcWidth = bmpTemp.getWidth();
        int srcHeight = bmpTemp.getHeight();
        can.setDrawFilter(new PaintFlagsDrawFilter(0, paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        Bitmap bmpBg = Bitmap.createScaledBitmap(bmpTemp, width, height, true);
        can.drawBitmap(bmpBg, 0, 0, null);
        Matrix matrixProgress = new Matrix();
        matrixProgress.postScale(dstWidth / srcWidth, dstHeight / srcWidth);
        bmpTemp = decodeCustomRes(getContext(), R.drawable.arc_progress);
        Bitmap bmpProgress = Bitmap.createBitmap(bmpTemp, 0, 0, srcWidth, srcHeight, matrixProgress, true);
        degrees = progress * 270 / max - 270;
        can.save();
        can.rotate(degrees, centerX, centerY);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        can.drawBitmap(bmpProgress, 0, 0, paint);
        can.restore();
        if ((-degrees) >= 85) {
            int posX = 0;
            int posY = 0;
            if((-degrees) >= 270){
                posX = 0;
                posY = 0;
            }else if((-degrees) >= 225){
                posX = centerX / 2;
                posY = 0;
            }else if((-degrees) >= 180){
                posX = centerX;
                posY = 0;
            }else if((-degrees) >= 135){
                posX = centerX;
                posY = 0;
            }else if((-degrees) >= 85){
                posX = centerX;
                posY = centerX;
            }
            if((-degrees) >= 225){
                can.save();
                Bitmap dst = Bitmap.createBitmap(bitmap, 0, 0, centerX, centerX);
                paint.setAntiAlias(true);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                Bitmap src = bmpBg.createBitmap(bmpBg, 0, 0, centerX, centerX);
                can.drawBitmap(src, 0, 0, paint);
                can.restore();
                can.save();
                dst = bitmap.createBitmap(bitmap, centerX, 0, centerX, height);
                paint.setAntiAlias(true);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
                src = bmpBg.createBitmap(bmpBg, centerX, 0, centerX, height);
                can.drawBitmap(src, centerX, 0, paint);
                can.restore();
            }else {
                can.save();
                Bitmap dst = bitmap.createBitmap(bitmap, posX, posY, width - posX, height - posY);
                paint.setAntiAlias(true);
                paint.setXfermode(new PorterDuffXfermode (PorterDuff.Mode.SRC_ATOP));
                Bitmap src = bmpBg.createBitmap(bmpBg, posX, posY,width - posX, height - posY);
                can.drawBitmap(src, posX, posY, paint);
                can.restore();
            }
        }

        canvas.drawBitmap(bitmap, 0, 0, null);
        paint.reset();
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent = (int) (((float) progress / (float) max) * 100);
        float textWidth = paint.measureText(percent + "%");
        if (isDisplayText && percent != 0){
            canvas.drawText(percent + "%", centerX - textWidth / 2, centerX + textSize / 2 - 25, paint);
        }
        paint.setTextSize(textSize/2);
        textWidth = paint.measureText(title);
        canvas.drawText(title, centerX - textWidth / 2, height - textSize / 2, paint);
    }

    private static Bitmap decodeCustomRes(Context c, int res) {
        InputStream is = c.getResources().openRawResource(res);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 1;
        Bitmap bmp = BitmapFactory.decodeStream(is, null, options);
        return bmp;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public synchronized int getMax() {
        return max;
    }

    public synchronized void setMax(int max) {
        if(max < 0){
            throw new IllegalArgumentException("max must more than 0");
        }
        this.max = max;
    }

    public synchronized int getProgress() {
        return progress;
    }

    public synchronized void setProgress(int progress) {
        if(progress < 0){
            throw new IllegalArgumentException("progress must more than 0");
        }
        if(progress > max){
            this.progress = progress;
        }
        if(progress <= max){
            this.progress = progress;
            postInvalidate();
        }
    }

    public boolean isDisplayText() {
        return isDisplayText;
    }

    public void setDisplayText(boolean displayText) {
        isDisplayText = displayText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
