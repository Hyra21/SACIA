package com.alucintech.saci.helpers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.alucintech.saci.ConnectionClass.ConnectionClass;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import com.alucintech.saci.helpers.CifradorHelper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

public class ScanQRHelper {

    private static final int CAMERA_PERMISSION_REQUEST = 123;
    private Fragment fragment;
    private DecoratedBarcodeView barcodeView;
    String qrCode;
    String matricula;
    Connection connection;
    ConnectionClass connectionClass;
    public ScanQRHelper(DecoratedBarcodeView barcodeView) {
        this.barcodeView = barcodeView;
    }

    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            // Manejar el resultado del escaneo del código QR

            qrCode = result.getText();
            String des = null;
            try {
                des = CifradorHelper.descifrar(qrCode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Log.e("Empezo scanner", String.valueOf(des));
            Toast.makeText(fragment.getActivity(), "Código QR escaneado: " + qrCode, Toast.LENGTH_SHORT).show();
            try {
                cargarDatosActividad();
            } catch (Exception e) {

            }

            // Enviar los datos escaneados a la actividad o fragmento que llamó
            Intent resultIntent = new Intent();
            resultIntent.putExtra("QR_CONTENT", qrCode);
            fragment.getActivity().setResult(AppCompatActivity.RESULT_OK, resultIntent);
            //fragment.getActivity().finish(); // Cerrar la actividad

            // Si deseas reiniciar la cámara para escanear más códigos QR en lugar de cerrar la actividad
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
    private void guardarDatosActividad(){

        //Linea de codigo para crear el archivo credencialesAlumno.xml
        SharedPreferences preferences = fragment.getActivity().getSharedPreferences("datosActividad", Context.MODE_PRIVATE);

        //Aqui editamos el archivo creado y le agregamos los datos
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("qrInicio", qrCode);

        //Con este commit terminamos de almacenar el archivo en el sistema
        editor.commit();
    }
    //Lectura del archivo para obtener las credenciales almacenadas
    private void cargarDatosActividad() throws Exception {
        String flag = "No existe";

        //Linea de codigo para buscar el archivo credencialesAlumno.xml
        SharedPreferences preferences = fragment.getActivity().getSharedPreferences("datosActividad", Context.MODE_PRIVATE);

        //Aqui obtenemos el codigo de inicio almacenado, si no existen se guarda "No existe"
        String qrIniciob = preferences.getString("qrInicio","No existe");

        //Aqui se realiza la validacion de que existen las credenciales
        if(qrIniciob.equals(flag)){
            guardarDatosActividad();
            Log.e("Termino scanner", String.valueOf(qrCode));
        }else{
            //Se decifra el qr de inicio y el de fin
            String codigo1 = CifradorHelper.descifrar(qrIniciob);
            String codigo2 = CifradorHelper.descifrar(qrCode);
            Log.e("Codigo descifrado", String.valueOf(codigo2));
            //Se obtiene el id de la actividad
            String idActividad = codigo1;
            validarHorario(idActividad);

            //Valida si los códigos son de la misma actividad
            if(codigo1.concat("asistio").compareTo(qrCode) == 0){

                connection = connectionClass.CONN();
                Statement statement = connection.createStatement();
                //Obtiene la matrícula del alumno
                cargarPreferencias();
                //Obtiene el numero de folio del carnet
                ResultSet resultSetFolio = statement.executeQuery("SELECT  numFolio FROM carnet where estadoCarnet = 'en Proceso' AND matriculaAlumno = "+matricula);
                //Obtiene el id del sello
                ResultSet resultSetidSello = statement.executeQuery("SELECT  idSello FROM sello where idActividad = "+idActividad);
                //Se inserta el id de sello y el numero de folio en la tabla teiesello para completar el registro
                statement.executeQuery("INSERT INTO tienesello (idSello, numFolioCarnet) VALUES " +
                        "("+resultSetidSello.getString(0)+","+resultSetFolio.getString(0)+")");
                connection.close();
                borrarDatosActividad();
            }
        }
    }
    public boolean validarHorario(String idActividad) throws SQLException {
        boolean error = false;
        connection = connectionClass.CONN();
        Statement statement = connection.createStatement();
        // Obtener instancia de Calendar
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        Calendar calendar3 = Calendar.getInstance();
        // Obtener la fecha del sistema
        calendar.get(Calendar.DATE);
        calendar.get(Calendar.MINUTE);
        calendar.get(Calendar.HOUR_OF_DAY);

        ResultSet resultHorario = statement.executeQuery("SELECT  horarioInicioActividad, horarioFinActividad, fechaActividad FROM actividad where idActividad = "+idActividad);
        calendar2.set(Calendar.HOUR_OF_DAY,resultHorario.getTime(1).getHours());
        calendar2.set(Calendar.MINUTE,resultHorario.getTime(1).getMinutes());
        calendar2.set(Calendar.DATE,resultHorario.getDate(3).getDate());

        calendar3.set(Calendar.HOUR_OF_DAY,resultHorario.getTime(2).getHours());
        calendar3.set(Calendar.MINUTE,resultHorario.getTime(2).getMinutes());
        calendar3.set(Calendar.DATE,resultHorario.getDate(3).getDate());

        if(calendar.before(calendar2) || calendar.after(calendar3)) {
            error = true;
        }
        connection.close();
        return error;
    }

    public void cargarPreferencias(){
        SharedPreferences preferences = fragment.getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        matricula = preferences.getString("matricula","No existe");
    }
    public void borrarDatosActividad(){
        SharedPreferences preferences = fragment.getActivity().getSharedPreferences("datosActividad", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
