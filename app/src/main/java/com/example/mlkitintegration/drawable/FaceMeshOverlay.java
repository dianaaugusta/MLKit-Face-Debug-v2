package com.example.mlkitintegration.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mlkitintegration.drawable.GraphicOverlay;
import com.google.mlkit.vision.facemesh.FaceMesh;

public class FaceMeshOverlay extends GraphicOverlay.Graphic {

    private final FaceMesh face;
    private final Rect imageRect;

    public FaceMeshOverlay(
            @NonNull GraphicOverlay overlay,
            @NonNull FaceMesh face,
            @NonNull Rect imageRect
    ) {
        super(overlay);
        this.face = face;
        this.imageRect = imageRect;
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect rect = calculateRect(
                imageRect.height(),
                imageRect.width(),
                face.getBoundingBox()
        );
            canvas.drawRect(rect, getGreenPaint());
    }

    private Rect calculateRect(int imageHeight, int imageWidth, Rect boundingBox) {
        // Coordenadas da caixa delimitadora do rosto
        int left = boundingBox.left;
        int top = boundingBox.top;
        int right = boundingBox.right;
        int bottom = boundingBox.bottom;

        // Escala das coordenadas da caixa delimitadora para as dimensões da imagem
        float scaleX = (float) imageWidth / overlay.getWidth();
        float scaleY = (float) imageHeight / overlay.getHeight();

        // Ajustar as coordenadas da caixa delimitadora com base na escala
        left *= scaleX;
        top *= scaleY;
        right *= scaleX;
        bottom *= scaleY;

        // Criar e retornar um novo retângulo com as coordenadas ajustadas
        return new Rect((int) left, (int) top, (int) right, (int) bottom);
    }

    private Paint getGreenPaint() {
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        return paint;
    }


}
