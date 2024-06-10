package com.alucintech.saci.helpers;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.alucintech.saci.connection.ConnectionClass;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.zxing.Result;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class ScanQRHelper {

    private static final int CAMERA_PERMISSION_REQUEST = 123;
    private Fragment fragment;
    private CodeScanner codeScanner;
    private CodeScannerView scannerView;
    String qrCode;
    String matricula;
    Connection connection;
    ConnectionClass connectionClass;

    public ScanQRHelper(Fragment fragment, CodeScannerView scannerView) {
        this.fragment = fragment;
        this.scannerView = scannerView;
        this.codeScanner = new CodeScanner(fragment.getContext(), scannerView);
        setupScanner();
    }

    private void setupScanner() {
        codeScanner.setDecodeCallback(new com.budiyev.android.codescanner.DecodeCallback() {
            @Override
            public void onDecoded(Result result) {
                fragment.requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        qrCode = result.getText();
                        String des = null;
                        try {
                            des = CifradorHelper.descifrar(qrCode);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        Log.e("Empezo scanner", String.valueOf(des));
                        Toast.makeText(fragment.getActivity(), "Código QR escaneado: " + qrCode, Toast.LENGTH_SHORT).show();
                        Bundle resultData = new Bundle();
                        resultData.putString("QR_CONTENT", qrCode);
                        fragment.getParentFragmentManager().setFragmentResult("SCAN_RESULT", resultData);
                    }
                });
            }
        });
    }

    public void startScan() {
        if (ContextCompat.checkSelfPermission(fragment.requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            codeScanner.startPreview();
        } else {
            ActivityCompat.requestPermissions(fragment.requireActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        }
    }

    public void resume() {
        if (ContextCompat.checkSelfPermission(fragment.requireActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            codeScanner.startPreview();
        }
    }

    public void pause() {
        codeScanner.releaseResources();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                codeScanner.startPreview();
            } else {
                Toast.makeText(fragment.requireActivity(), "El permiso de la cámara es necesario para escanear códigos QR", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void guardarDatosActividad() {
        SharedPreferences preferences = fragment.getActivity().getSharedPreferences("datosActividad", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("qrInicio", qrCode);
        editor.commit();
    }

    private void cargarDatosActividad() throws Exception {
        String flag = "No existe";
        SharedPreferences preferences = fragment.getActivity().getSharedPreferences("datosActividad", Context.MODE_PRIVATE);
        String qrIniciob = preferences.getString("qrInicio", "No existe");

        if (qrIniciob.equals(flag)) {
            guardarDatosActividad();
            Log.e("Termino scanner", String.valueOf(qrCode));
        } else {
            String codigo1 = CifradorHelper.descifrar(qrIniciob);
            String codigo2 = CifradorHelper.descifrar(qrCode.substring(0, qrIniciob.length()));
            Log.e("Codigo descifrado", String.valueOf(codigo2));
            String idActividad = codigo1;
            if (!validarHorario(idActividad)) {
                connection = connectionClass.CONN();
                Statement statement = connection.createStatement();
                cargarPreferencias();
                ResultSet resultSetFolio = statement.executeQuery("SELECT numFolio FROM carnet where estadoCarnet = 'en Proceso' AND matriculaAlumno = " + matricula);
                ResultSet resultSetidSello = statement.executeQuery("SELECT idSello FROM sello where idActividad = " + idActividad);
                statement.executeUpdate("INSERT INTO tienesello (idSello, numFolioCarnet) VALUES (" +
                        resultSetidSello.getString(1) + "," + resultSetFolio.getString(1) + ")");
                connection.close();
                borrarDatosActividad();
            }
        }
    }

    public boolean validarHorario(String idActividad) throws SQLException {
        boolean error = false;
        connection = connectionClass.CONN();
        Log.e("paso", "paso");
        Statement statement = connection.createStatement();
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        Calendar calendar3 = Calendar.getInstance();
        calendar.get(Calendar.DATE);
        calendar.get(Calendar.MINUTE);
        calendar.get(Calendar.HOUR_OF_DAY);

        ResultSet resultHorario = statement.executeQuery("SELECT horarioInicioActividad, horarioFinActividad, fechaActividad FROM actividad where idActividad = " + idActividad);
        calendar2.set(Calendar.HOUR_OF_DAY, resultHorario.getTime(1).getHours());
        calendar2.set(Calendar.MINUTE, resultHorario.getTime(1).getMinutes());
        calendar2.set(Calendar.DATE, resultHorario.getDate(3).getDate());

        calendar3.set(Calendar.HOUR_OF_DAY, resultHorario.getTime(2).getHours());
        calendar3.set(Calendar.MINUTE, resultHorario.getTime(2).getMinutes());
        calendar3.set(Calendar.DATE, resultHorario.getDate(3).getDate());

        if (calendar.before(calendar2) || calendar.after(calendar3)) {
            error = true;
        }
        connection.close();
        return error;
    }

    public void cargarPreferencias() {
        SharedPreferences preferences = fragment.getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        matricula = preferences.getString("matricula", "No existe");
    }

    public void borrarDatosActividad() {
        SharedPreferences preferences = fragment.getActivity().getSharedPreferences("datosActividad", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
