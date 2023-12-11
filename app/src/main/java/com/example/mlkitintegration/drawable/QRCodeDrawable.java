package com.example.mlkitintegration.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.mlkitintegration.model.QRCodeViewModel;

public class QRCodeDrawable extends Drawable {
    private final Paint boundingRectPaint;
    private final Paint contentRectPaint;
    private final Paint contentTextPaint;
    private final QRCodeViewModel qrCodeViewModel;
    private final int contentPadding;
    private int textWidth;

    public QRCodeDrawable(QRCodeViewModel qrCodeViewModel) {
        this.qrCodeViewModel = qrCodeViewModel;

        boundingRectPaint = new Paint();
        boundingRectPaint.setStyle(Paint.Style.STROKE);
        boundingRectPaint.setColor(Color.YELLOW);
        boundingRectPaint.setStrokeWidth(5F);
        boundingRectPaint.setAlpha(200);

        contentRectPaint = new Paint();
        contentRectPaint.setStyle(Paint.Style.FILL);
        contentRectPaint.setColor(Color.YELLOW);
        contentRectPaint.setAlpha(255);

        contentTextPaint = new Paint();
        contentTextPaint.setColor(Color.DKGRAY);
        contentTextPaint.setAlpha(255);
        contentTextPaint.setTextSize(36F);

        contentPadding = 25;
        textWidth = (int) contentTextPaint.measureText(qrCodeViewModel.getQrContent());
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(qrCodeViewModel.getBoundingRect(), boundingRectPaint);

        Rect contentRect = new Rect(
                qrCodeViewModel.getBoundingRect().left,
                qrCodeViewModel.getBoundingRect().bottom + contentPadding / 2,
                qrCodeViewModel.getBoundingRect().left + textWidth + contentPadding * 2,
                qrCodeViewModel.getBoundingRect().bottom + (int) contentTextPaint.getTextSize() + contentPadding);

        canvas.drawRect(contentRect, contentRectPaint);

        canvas.drawText(
                qrCodeViewModel.getQrContent(),
                qrCodeViewModel.getBoundingRect().left + contentPadding,
                qrCodeViewModel.getBoundingRect().bottom + contentPadding * 2,
                contentTextPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        boundingRectPaint.setAlpha(alpha);
        contentRectPaint.setAlpha(alpha);
        contentTextPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        boundingRectPaint.setColorFilter(colorFilter);
        contentRectPaint.setColorFilter(colorFilter);
        contentTextPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
