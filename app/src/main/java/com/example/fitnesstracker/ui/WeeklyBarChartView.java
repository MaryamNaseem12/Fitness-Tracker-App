package com.example.fitnesstracker.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class WeeklyBarChartView extends View {

    private final Paint barBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint barFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float[] values = new float[7]; // Sun - Sat
    private float maxValue = 1000f;
    private final String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private int currentDayIndex = 0;

    public WeeklyBarChartView(Context context) {
        super(context);
        init();
    }

    public WeeklyBarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeeklyBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barBgPaint.setColor(Color.parseColor("#1A233A"));
        barBgPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(Color.parseColor("#8E9BB0"));
        textPaint.setTextSize(32f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        Calendar calendar = Calendar.getInstance();
        currentDayIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sun
    }

    public void setWeeklyData(float[] newValues, float maxTarget) {
        if (newValues != null && newValues.length == 7) {
            System.arraycopy(newValues, 0, this.values, 0, 7);
        }
        if (maxTarget > 0) {
            this.maxValue = maxTarget;
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width <= 0 || height <= 0) return;

        float bottomPadding = 60f;
        float chartHeight = height - bottomPadding - 20f;
        float columnWidth = width / 7f;
        float barWidth = columnWidth * 0.45f;

        for (int i = 0; i < 7; i++) {
            float cx = columnWidth * i + columnWidth / 2f;
            float left = cx - barWidth / 2f;
            float right = cx + barWidth / 2f;
            float top = 20f;
            float bottom = height - bottomPadding;

            // Draw background bar track
            RectF bgRect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(bgRect, barWidth / 2f, barWidth / 2f, barBgPaint);

            // Draw progress bar fill
            float fillRatio = Math.min(values[i] / maxValue, 1.0f);
            if (fillRatio > 0.02f) {
                float fillTop = bottom - (chartHeight * fillRatio);
                RectF fillRect = new RectF(left, fillTop, right, bottom);

                int startColor = (i == currentDayIndex) ? Color.parseColor("#FF9100") : Color.parseColor("#00E676");
                int endColor = (i == currentDayIndex) ? Color.parseColor("#FF3D00") : Color.parseColor("#00E5FF");

                barFillPaint.setShader(new LinearGradient(
                        cx, fillTop, cx, bottom,
                        startColor, endColor,
                        Shader.TileMode.CLAMP
                ));

                canvas.drawRoundRect(fillRect, barWidth / 2f, barWidth / 2f, barFillPaint);
            }

            // Draw day text label
            if (i == currentDayIndex) {
                textPaint.setColor(Color.parseColor("#00E676"));
                textPaint.setFakeBoldText(true);
            } else {
                textPaint.setColor(Color.parseColor("#8E9BB0"));
                textPaint.setFakeBoldText(false);
            }

            canvas.drawText(days[i], cx, height - 12f, textPaint);
        }
    }
}
