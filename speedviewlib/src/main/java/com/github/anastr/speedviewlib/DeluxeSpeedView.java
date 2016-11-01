package com.github.anastr.speedviewlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import java.util.Locale;

/**
 * this Library build By Anas Altair
 * see it on <a href="https://github.com/anastr/SpeedView">GitHub</a>
 */
public class DeluxeSpeedView extends Speedometer {

    private Path indicatorPath = new Path(),
            markPath = new Path(),
            smallMarkPath = new Path();
    private Paint centerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedometerPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            markPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            smallMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG),
            speedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF speedometerRect = new RectF(),
            speedBackgroundRect = new RectF();
    private int speedBackgroundColor = Color.WHITE;

    private boolean withEffects = true;

    public DeluxeSpeedView(Context context) {
        super(context);
        init();
    }

    public DeluxeSpeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttributeSet(context, attrs);
    }

    public DeluxeSpeedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttributeSet(context, attrs);
    }

    @Override
    protected void defaultValues() {
        setIndicatorColor(Color.parseColor("#00ffec"));
        setCenterCircleColor(Color.parseColor("#e0e0e0"));
        setLowSpeedColor(Color.parseColor("#37872f"));
        setMediumSpeedColor(Color.parseColor("#a38234"));
        setHighSpeedColor(Color.parseColor("#9b2020"));
        setTextColor(Color.WHITE);
        setBackgroundCircleColor(Color.parseColor("#212121"));
    }

    private void init() {
        speedometerPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStyle(Paint.Style.STROKE);
        smallMarkPaint.setStyle(Paint.Style.STROKE);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setWithEffects(withEffects);
    }

    private void initAttributeSet(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DeluxeSpeedView, 0, 0);

        speedBackgroundColor = a.getColor(R.styleable.DeluxeSpeedView_speedBackgroundColor, speedBackgroundColor);
        withEffects = a.getBoolean(R.styleable.DeluxeSpeedView_withEffects, withEffects);
        a.recycle();
        setWithEffects(withEffects);
        initAttributeValue();
    }

    private void initAttributeValue() {
        speedBackgroundPaint.setColor(speedBackgroundColor);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        float risk = getSpeedometerWidth()/2f;
        speedometerRect.set(risk, risk, w -risk, h -risk);

        float indW = w/32f;

        indicatorPath.reset();
        indicatorPath.moveTo(w/2f, h/5f);
        indicatorPath.lineTo(w/2f -indW, h*3f/5f);
        indicatorPath.lineTo(w/2f +indW, h*3f/5f);
        RectF rectF = new RectF(w/2f -indW, h*3f/5f -indW, w/2f +indW, h*3f/5f +indW);
        indicatorPath.addArc(rectF, 0f, 180f);
        indicatorPath.moveTo(0f, 0f);

        float markH = h/28f;
        markPath.reset();
        markPath.moveTo(w/2f, 0f);
        markPath.lineTo(w/2f, markH);
        markPath.moveTo(0f, 0f);
        markPaint.setStrokeWidth(markH/3f);

        float smallMarkH = h/20f;
        smallMarkPath.reset();
        smallMarkPath.moveTo(w/2f, getSpeedometerWidth());
        smallMarkPath.lineTo(w/2f, getSpeedometerWidth() + smallMarkH);
        smallMarkPath.moveTo(0f, 0f);
        smallMarkPaint.setStrokeWidth(3);
    }

    private void initDraw() {
        indicatorPaint.setColor(getIndicatorColor());
        speedometerPaint.setStrokeWidth(getSpeedometerWidth());
        markPaint.setColor(getMarkColor());
        smallMarkPaint.setColor(getMarkColor());
        centerCirclePaint.setColor(getCenterCircleColor());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDraw();

        speedometerPaint.setColor(getLowSpeedColor());
        canvas.drawArc(speedometerRect, 135f, 160f, false, speedometerPaint);
        speedometerPaint.setColor(getMediumSpeedColor());
        canvas.drawArc(speedometerRect, 135f+160f, 75f, false, speedometerPaint);
        speedometerPaint.setColor(getHighSpeedColor());
        canvas.drawArc(speedometerRect, 135f+160f+75f, 35f, false, speedometerPaint);

        canvas.save();
        canvas.rotate(135f+90f, getWidth()/2f, getHeight()/2f);
        for (int i=135; i <= 345; i+=30) {
            canvas.rotate(30f, getWidth()/2f, getHeight()/2f);
            canvas.drawPath(markPath, markPaint);
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(135f+90f, getWidth()/2f, getHeight()/2f);
        for (int i=135; i < 395; i+=10) {
            canvas.rotate(10f, getWidth()/2f, getHeight()/2f);
            canvas.drawPath(smallMarkPath, smallMarkPaint);
        }
        canvas.restore();

        canvas.save();
        canvas.rotate(90f +getDegree(), getWidth()/2f, getHeight()/2f);
        canvas.drawPath(indicatorPath, indicatorPaint);
        canvas.restore();
        canvas.drawCircle(getWidth()/2f, getHeight()/2f, getWidth()/12f, centerCirclePaint);

        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("00", getWidth()/5f, getHeight()*6/7f, textPaint);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.format(Locale.getDefault(), "%d", getMaxSpeed()), getWidth()*4/5f, getHeight()*6/7f, textPaint);
        String sSpeedUnit = String.format(Locale.getDefault(), "%.1f"
                , getCorrectSpeed()) +getUnit();
        speedBackgroundRect.set(getWidth()/2f - (speedTextPaint.measureText(sSpeedUnit)/2f) -5
                , speedometerRect.bottom - speedTextPaint.getTextSize()
                , getWidth()/2f + (speedTextPaint.measureText(sSpeedUnit)/2f) +5
                , speedometerRect.bottom + 4);
        canvas.drawRect(speedBackgroundRect, speedBackgroundPaint);
        canvas.drawText(sSpeedUnit, getWidth()/2f, speedometerRect.bottom, speedTextPaint);
    }

    public boolean isWithEffects() {
        return withEffects;
    }

    public void setWithEffects(boolean withEffects) {
        this.withEffects = withEffects;
        if (withEffects) {
            indicatorPaint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));
            markPaint.setMaskFilter(new BlurMaskFilter(5, BlurMaskFilter.Blur.SOLID));
            speedBackgroundPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.SOLID));
            centerCirclePaint.setMaskFilter(new BlurMaskFilter(10, BlurMaskFilter.Blur.SOLID));
        }
        else {
            indicatorPaint.setMaskFilter(null);
            markPaint.setMaskFilter(null);
            speedBackgroundPaint.setMaskFilter(null);
            centerCirclePaint.setMaskFilter(null);
        }
        invalidate();
    }

    public int getSpeedBackgroundColor() {
        return speedBackgroundColor;
    }

    public void setSpeedBackgroundColor(int speedBackgroundColor) {
        this.speedBackgroundColor = speedBackgroundColor;
        speedBackgroundPaint.setColor(speedBackgroundColor);
        invalidate();
    }
}
