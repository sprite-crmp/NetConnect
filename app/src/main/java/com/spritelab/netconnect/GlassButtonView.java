package com.spritelab.netconnect;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class GlassButtonView extends FrameLayout {

    private Paint bgPaint, borderPaint, highlightPaint;
    private RectF rect = new RectF();
    private float cornerRadius = 30f;
    private float pressScale = 1f;

    public GlassButtonView(Context context) {
        super(context);
        init();
    }

    public GlassButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GlassButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        setClickable(true);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.argb(80, 255, 255, 255)); // прозрачный фон

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);
        borderPaint.setColor(Color.argb(150, 255, 255, 255));

        highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // На Android 12+ можно добавить blur
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            setRenderEffect(android.graphics.RenderEffect.createBlurEffect(15f, 15f, Shader.TileMode.CLAMP));
        }

        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(pressScale, pressScale, getWidth() / 2f, getHeight() / 2f);

        rect.set(0, 0, getWidth(), getHeight());

        // Фон
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, bgPaint);

        // Блик сверху
        LinearGradient gradient = new LinearGradient(
                0, 0, 0, getHeight(),
                new int[]{Color.argb(180, 255, 255, 255),
                        Color.argb(60, 255, 255, 255),
                        Color.TRANSPARENT},
                null, Shader.TileMode.CLAMP
        );
        highlightPaint.setShader(gradient);
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, highlightPaint);

        // Рамка
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint);

        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pressScale = 0.95f;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                pressScale = 1f;
                invalidate();
                performClick();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}