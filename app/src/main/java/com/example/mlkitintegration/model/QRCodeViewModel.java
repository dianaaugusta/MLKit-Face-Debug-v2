package com.example.mlkitintegration.model;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;

import com.google.mlkit.vision.barcode.common.Barcode;

public class QRCodeViewModel {
    private Rect boundingRect;
    private String qrContent;
    private QrCodeTouchCallback qrCodeTouchCallback;

    public QRCodeViewModel(Barcode barcode) {
        boundingRect = barcode.getBoundingBox();
        qrCodeTouchCallback = (v, e) -> false; // no-op

        switch (barcode.getValueType()) {
            case Barcode.TYPE_URL:
                qrContent = barcode.getUrl().getUrl();
                qrCodeTouchCallback = (v, e) -> {
                    if (e.getAction() == MotionEvent.ACTION_DOWN && boundingRect.contains((int) e.getX(), (int) e.getY())) {
                        Intent openBrowserIntent = new Intent(Intent.ACTION_VIEW);
                        openBrowserIntent.setData(Uri.parse(qrContent));
                        v.getContext().startActivity(openBrowserIntent);
                    }
                    return true; // return true from the callback to signify the event was handled
                };
                break;
            // Add other QR Code types here to handle other types of data,
            // like Wifi credentials.
            default:
                qrContent = "Unsupported data type: " + barcode.getRawValue();
                break;
        }
    }

    public Rect getBoundingRect() {
        return boundingRect;
    }

    public String getQrContent() {
        return qrContent;
    }

    public QrCodeTouchCallback getQrCodeTouchCallback() {
        return qrCodeTouchCallback;
    }

    public interface QrCodeTouchCallback {
        boolean onTouch(View v, MotionEvent e);
    }
}