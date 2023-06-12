package com.alucintech.saci.helpers;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.List;

public class ScanQRHelper {

    private static final int CAMERA_PERMISSION_REQUEST = 123;

    private Fragment fragment;
    private DecoratedBarcodeView barcodeView;

    public ScanQRHelper(DecoratedBarcodeView barcodeView) {
        this.barcodeView = barcodeView;
    }

    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            // Manejar el resultado del escaneo del código QR
            String qrCode = result.getText();
            Toast.makeText(fragment.getActivity(), "Código QR escaneado: " + qrCode, Toast.LENGTH_SHORT).show();

            // Reiniciar la cámara para escanear más códigos QR
            barcodeView.decodeSingle(this);
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            // Acciones adicionales relacionadas con los puntos de resultado posibles
        }
    };

    public ScanQRHelper(Fragment fragment, DecoratedBarcodeView barcodeView) {
        this.fragment = fragment;
        this.barcodeView = barcodeView;
    }

    public void startScan() {
        // Iniciar la vista de la cámara para escanear códigos QR
        if (ContextCompat.checkSelfPermission(fragment.requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
            barcodeView.decodeSingle(barcodeCallback);
        } else {
            // Solicitar permiso de cámara si no se ha concedido
            ActivityCompat.requestPermissions(fragment.requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }
    }

    public void resume() {
        // Iniciar la vista de la cámara
        if (ContextCompat.checkSelfPermission(fragment.requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            barcodeView.resume();
            barcodeView.decodeSingle(barcodeCallback);
        }
    }

    public void pause() {
        // Detener la vista de la cámara
        barcodeView.pause();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de cámara concedido, iniciar la vista de la cámara
                barcodeView.resume();
                barcodeView.decodeSingle(barcodeCallback);
            } else {
                // Permiso de cámara denegado, mostrar mensaje de error
                Toast.makeText(fragment.requireActivity(), "El permiso de la cámara es necesario para escanear códigos QR", Toast.LENGTH_LONG).show();
            }
        }
    }
}
