package com.example.mlkitintegration;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.facemesh.FaceMeshDetection;
import com.google.mlkit.vision.facemesh.FaceMeshDetector;
import com.google.mlkit.vision.facemesh.FaceMeshDetectorOptions;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};
    private PreviewView previewView;
    private MyCameraLifecycleOwner myCameraLifecycleOwner;
    private FaceMeshDetector faceMeshDetector;
    private Executor cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.viewFinder);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
        myCameraLifecycleOwner = new MyCameraLifecycleOwner();
        faceMeshDetector = createFaceMeshDetector();
    }

    private FaceMeshDetector createFaceMeshDetector() {
        FaceMeshDetectorOptions options =
                new FaceMeshDetectorOptions.Builder()
                        .build();

        return FaceMeshDetection.getClient(options);
    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
            InputImage inputImage = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

            faceMeshDetector.process(inputImage)
                    .addOnSuccessListener(faceMeshes -> {
                        if (faceMeshes != null && !faceMeshes.isEmpty()) {
                            Log.d("FaceMeshDetection", "Número de pontos da Face Mesh: " + faceMeshes.get(0).getAllPoints().size());
                        }
                        imageProxy.close();
                    })
                    .addOnFailureListener(e -> {
                        // Lidar com falha na detecção da Face Mesh
                        Log.e("FaceMeshDetection", "Erro na detecção da Face Mesh: " + e.getMessage());
                    })
                    .addOnCompleteListener(task -> {
                        //imageAnalysis.clearAnalyzer();
                        imageProxy.close();
                    });
           // imageProxy.close();
        });

        cameraProvider.bindToLifecycle(myCameraLifecycleOwner, cameraSelector, preview, imageAnalysis);
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                // Se as permissões não foram concedidas, você pode lidar com isso aqui.
                // Por exemplo, exibir uma mensagem para o usuário sobre a necessidade das permissões.
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static class MyCameraLifecycleOwner implements LifecycleOwner {
        private final LifecycleRegistry mLifecycleRegistry;

        public MyCameraLifecycleOwner() {
            mLifecycleRegistry = new LifecycleRegistry(this);
            mLifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
        }

        @NonNull
        @Override
        public Lifecycle getLifecycle() {
            return mLifecycleRegistry;
        }
    }
}
