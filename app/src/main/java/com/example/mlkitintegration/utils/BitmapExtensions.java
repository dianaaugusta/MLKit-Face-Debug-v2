package com.example.mlkitintegration.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class BitmapExtensions {
    /**
     * The rotationDegrees parameter is the rotation in degrees clockwise from the original orientation.
     */
    public Bitmap rotateBitmap(Bitmap bitmap, int rotationDegrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(-rotationDegrees);
        matrix.postScale(-1f, -1f);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
