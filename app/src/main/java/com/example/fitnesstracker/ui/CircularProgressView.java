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

import java.util.Locale;

public class CircularProgressView extends View {

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint stepsArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint caloriesArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint primaryTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint secondaryTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int currentSteps = 0;
    private int goalSteps = 10000;
    private int currentCalories = 0;
    private int goalCalories = 2200;

    public CircularProgressView(Context context) {
        super(context);
        init();
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setColor(Color.parseColor("#1A233A"));
        trackPaint.setStrokeWidth(24f);

        stepsArcPaint.setStyle(Paint.Style.STROKE);
        stepsArcPaint.setStrokeWidth(24f);
        stepsArcPaint.setStrokeCap(Paint.Cap.ROUND);

        caloriesArcPaint.setStyle(Paint.Style.STROKE);
        caloriesArcPaint.setStrokeWidth(20f);
        caloriesArcPaint.setStrokeCap(Paint.Cap.ROUND);

        primaryTextPaint.setColor(Color.parseColor("#FFFFFF"));
        primaryTextPaint.setTextSize(54f);
        primaryTextPaint.setTextAlign(Paint.Align.CENTER);
        primaryTextPaint.setFakeBoldText(true);

        secondaryTextPaint.setColor(Color.parseColor("#8E9BB0"));
        secondaryTextPaint.setTextSize(28f);
        secondaryTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setMetrics(int steps, int targetSteps, int calories, int targetCalories) {
        this.currentSteps = steps;
        this.goalSteps = Math.max(targetSteps, 1);
        this.currentCalories = calories;
        this.goalCalories = Math.max(targetCalories, 1);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float cx = width / 2f;
        float cy = height / 2f;

        if (width <= 0 || height <= 0) return;

        // Outer Ring - Steps Arc
        float outerRadius = Math.min(cx, cy) - 30f;
        RectF outerBounds = new RectF(cx - outerRadius, cy - outerRadius, cx + outerRadius, cy + outerRadius);

        trackPaint.setStrokeWidth(24f);
        canvas.drawCircle(cx, cy, outerRadius, trackPaint);

        float stepsRatio = Math.min((float) currentSteps / goalSteps, 1.0f);
        float stepsSweep = stepsRatio * 360f;

        stepsArcPaint.setShader(new LinearGradient(
                outerBounds.left, outerBounds.top, outerBounds.right, outerBounds.bottom,
                Color.parseColor("#00E676"), Color.parseColor("#00E5FF"),
                Shader.TileMode.CLAMP
        ));
        canvas.drawArc(outerBounds, -90f, stepsSweep, false, stepsArcPaint);

        // Inner Ring - Calories Arc
        float innerRadius = outerRadius - 36f;
        RectF innerBounds = new RectF(cx - innerRadius, cy - innerRadius, cx + innerRadius, cy + innerRadius);

        trackPaint.setStrokeWidth(20f);
        canvas.drawCircle(cx, cy, innerRadius, trackPaint);

        float calRatio = Math.min((float) currentCalories / goalCalories, 1.0f);
        float calSweep = calRatio * 360f;

        caloriesArcPaint.setShader(new LinearGradient(
                innerBounds.left, innerBounds.top, innerBounds.right, innerBounds.bottom,
                Color.parseColor("#FF9100"), Color.parseColor("#FF3D00"),
                Shader.TileMode.CLAMP
        ));
        canvas.drawArc(innerBounds, -90f, calSweep, false, caloriesArcPaint);

        // Center Info Text
        canvas.drawText(String.format(Locale.getDefault(), "%,d", currentSteps), cx, cy - 10f, primaryTextPaint);
        int percent = (int) (stepsRatio * 100);
        canvas.drawText(percent + "% of daily steps", cx, cy + 32f, secondaryTextPaint);
    }
}
