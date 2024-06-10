package com.alucintech.saci.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.alucintech.saci.R;
import com.alucintech.saci.connection.ConnectionClass;
import com.alucintech.saci.helpers.CifradorHelper;
import com.alucintech.saci.helpers.ScanQRHelper;
import com.alucintech.saci.objects.Actividades;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

public class informacionActividadFragment extends Fragment {
    ConnectionClass connectionClass;
    Connection connection;
    String matriculaAlumno;

    String nombreEvento, cicloEscolar, fechaInicio, fechaFin, descripcion, imagen;
    MultiAutoCompleteTextView mtwNombreEvento, mtwDescripcion;
    ImageView imwEvento;
    TextView twCiclo, twFechaInicio, twFechaFin;

    Actividades actividades;
    private ScanQRHelper scanQRHelper;
    DecoratedBarcodeView barcodeView;
    int posicion;
    MultiAutoCompleteTextView mtwNombreActividad, mtwDescripcionActividad;
    ImageButton imgbtRegresar, imgbtScanner, imgbtLink;
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    NavController navController;
    ImageView  imwActividad;
    TextView twTipoActividadInfo, twFechaInfo,twHorarioInfo, twModalidadInfo, twEspaciosInfo, twPonente;

    int id;
    String nombreActividad;
    String descripcionActividad;
    String tipoActividad;
    String fechaActividad;
    String horarioInicio;
    String horarioFin;
    String lugarActividad;
    int espaciosDisponibles;
    String modalidadActividad;
    String enlaceVirtual;
    String imagenActividad;
    String ponenteActividad;
    int idEvento;
    int numEmpleadoAdministrador;
    String estadoActividad;

    Boolean flag = false;

    class Task extends AsyncTask<View, View, View> {
        //Aquí subimos todos los datos a la base de datos
        @Override
        protected View doInBackground(View... views) {
            return null;
        }
        //Aquí crearemos la vista de los carnets del alumno en el recyclerView
        @Override
        protected void onPostExecute(View view) {
            super.onPostExecute(view);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_informacion_actividad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarPreferencias();
        connectionClass = new ConnectionClass();
        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        scanQRHelper = new ScanQRHelper(this, scannerView);

        toolbar = view.findViewById(R.id.topAppBar);
        drawerLayout = view.findViewById(R.id.informacionActividadDrawerLayout);
        navigationView = view.findViewById(R.id.nwConsultaActividad);
        mtwNombreActividad = view.findViewById(R.id.mtwNombreActividadInformacion);
        navController = Navigation.findNavController(view);
        imgbtRegresar = view.findViewById(R.id.imgbtRegresarActividad);
        imgbtScanner = view.findViewById(R.id.imgbtScannerActividad);

        mtwDescripcionActividad = view.findViewById(R.id.mtwDescripcionActividad);
        imwActividad = view.findViewById(R.id.imwActividad);
        twTipoActividadInfo = view.findViewById(R.id.twTipoActividadInfo);
        twFechaInfo = view.findViewById(R.id.twFechaInfo);
        twHorarioInfo = view.findViewById(R.id.twHorarioInfo);
        twModalidadInfo = view.findViewById(R.id.twModalidadInfo);
        twEspaciosInfo = view.findViewById(R.id.twEspaciosInfo);
        twPonente = view.findViewById(R.id.twPonente);
        new Task().execute();

        Toast.makeText(getActivity(),Integer.toString(idEvento),Toast.LENGTH_LONG).show();

        imgbtLink = view.findViewById(R.id.imgbtLinkInfo);

        // Ingresamos los datos de la actividad en los componentes
        mtwNombreActividad.setText(nombreActividad);

        // Metodo para obtener la imagen de la actividad
        byte[] imagenDecodificada = android.util.Base64.decode(imagenActividad, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagenDecodificada,0,imagenDecodificada.length);
        imwActividad.setImageBitmap(bitmap);

        twTipoActividadInfo.setText(tipoActividad);
        twFechaInfo.setText(fechaActividad);
        twHorarioInfo.setText(horarioInicio + " - " + horarioFin);
        twModalidadInfo.setText(modalidadActividad);

        if(modalidadActividad.equals("Virtual")){
            imgbtLink.setVisibility(View.VISIBLE);
            imgbtLink.setClickable(true);
        }

        twEspaciosInfo.setText(Integer.toString(espaciosDisponibles));
        twPonente.setText(ponenteActividad);

        mtwDescripcionActividad.setText(descripcionActividad);
        mtwNombreActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
                dialog.setContentView(R.layout.mostrar_evento);

                // Cerrar el diálogo al tocar fuera de él
                dialog.setCanceledOnTouchOutside(true);

                dialog.show();
            }
        });

        imgbtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(enlaceVirtual));
                startActivity(intent);
            }
        });

        imgbtRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_informacionActividadFragment_to_consultaActividades);
            }
        });

        imgbtScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scannerView.setVisibility(View.VISIBLE);
                final Fragment currentFragment = informacionActividadFragment.this;
                scanQRHelper.startScan();
            }
        });

        getParentFragmentManager().setFragmentResultListener("SCAN_RESULT", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                if ("SCAN_RESULT".equals(requestKey)) {
                    String qrContent = result.getString("QR_CONTENT");
                    // Manejar el resultado del escaneo aquí
                    scannerView.setVisibility(View.INVISIBLE);
                    Log.d("Resultado SCAN", String.valueOf(result));
                    cargarDatosActividad(String.valueOf(result));
                }
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id){
                    case R.id.usuario:
                        Toast.makeText(getActivity(),"Para consultar ayuda contactarse al correo yhigaque@uabc.edu.mx", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.help:
                        Toast.makeText(getActivity(),"Para consultar ayuda contactarse al correo yhigaque@uabc.edu.mx", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.cerrarSesion:
                        borrarPreferencias();
                        navController.navigate(R.id.action_informacionActividadFragment_to_inicioFragment);
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        scanQRHelper.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        scanQRHelper.pause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        scanQRHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("actividadTemp",Context.MODE_PRIVATE);
        posicion = preferences.getInt("posicion",1000);

        nombreActividad = preferences.getString("nombreActividad", "no");
        descripcionActividad = preferences.getString("descripcionActividad", "no");
        tipoActividad = preferences.getString("tipoActividad", "no");
        fechaActividad = preferences.getString("fecha", "no");
        horarioInicio = preferences.getString("horarioInicio", "no");
        horarioFin = preferences.getString("horarioFin", "no");
        lugarActividad = preferences.getString("lugar", "no");
        espaciosDisponibles = preferences.getInt("espacios", 0);
        modalidadActividad = preferences.getString("modalidad", "no");
        enlaceVirtual = preferences.getString("enlace", "no");
        imagenActividad = preferences.getString("imagen", "no");
        ponenteActividad = preferences.getString("ponente", "no");
        idEvento = preferences.getInt("idEvento", 0);
        numEmpleadoAdministrador = preferences.getInt("numEmpleado", 0);
        estadoActividad = preferences.getString("estado", "no");
    }

    public void borrarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private void guardarDatosActividad(String qrCode){

        //Linea de codigo para crear el archivo credencialesAlumno.xml
        SharedPreferences preferences = getActivity().getSharedPreferences("datosActividad", Context.MODE_PRIVATE);

        //Aqui editamos el archivo creado y le agregamos los datos
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("qrInicio", qrCode);

        //Con este commit terminamos de almacenar el archivo en el sistema
        editor.commit();
    }
    //Lectura del archivo para obtener las credenciales almacenadas

    private void cargarDatosActividad(String qrContent){
        try {
            String flag = "No existe";
            String qrCode = limpiarCodigo(qrContent);
            //Linea de codigo para buscar el archivo credencialesAlumno.xml
            SharedPreferences preferences = getActivity().getSharedPreferences("datosActividad", Context.MODE_PRIVATE);

            //Aqui obtenemos el codigo de inicio almacenado, si no existen se guarda "No existe"
            String qrIniciob = preferences.getString("qrInicio", "No existe");
            System.out.println("LEÉ EL CODIGO DE INICIO QUE ES: " + qrIniciob);

            //Aqui se realiza la validacion de que existen las credenciales
            if (qrIniciob.compareTo(flag) == 0) {
                System.out.println("EL QR DE INICIO NO EXISTÍA: " + qrIniciob);
                guardarDatosActividad(qrCode);
                System.out.println("SE MANDÓ A GUARDARDATOSACTIVIDAD: " + qrIniciob);
            } else {
                System.out.println("EL CODIGO DE INICIO SÍ EXISTÍA Y ESTE ES EL 2DO CÓDIGO: " + qrCode);
                //Se decifra el qr de inicio y el de fin
                String codigo1 = CifradorHelper.descifrar(qrIniciob);
                System.out.println("EL SE DESCIFRARON LOS CODIGOS Y NOS DIÓ: " + codigo1);
                String codigo2 = CifradorHelper.descifrar(qrCode.substring(0, qrIniciob.length()));
                System.out.println("EL SE DESCIFRARON LOS CODIGOS Y NOS DIÓ: " + codigo1 + " Y " + codigo2);

                //Se obtiene el id de la actividad
                String idActividad = codigo1;
                System.out.println("EL ID DE LA ACTIVIDAD ES: " + idActividad);
                int[] errores = validarHorario(idActividad);
                System.out.println("SE MANDÓ IDACTIVIDAD A VALIDAR LOS ERRORES: " + idActividad);
                //Valida si los códigos son de la misma actividad, dentro del horario y si lo escaneado pertenece a una actividad
                if (codigo1.concat("asistio").compareTo(codigo2) == 0 && errores[0] != 1 && errores[1] != 1) {

                    connection = connectionClass.CONN();
                    Statement statement = connection.createStatement();
                    //Obtiene la matrícula del alumno
                    cargarPreferenciasMatricula();
                    //Obtiene el numero de folio del carnet
                    ResultSet resultSetFolio = statement.executeQuery("SELECT  numFolio FROM carnet where estadoCarnet = 'en Proceso' AND matriculaAlumno = " + matriculaAlumno);
                    //Obtiene el id del sello
                    ResultSet resultSetidSello = statement.executeQuery("SELECT  idSello FROM sello where idActividad = " + idActividad);
                    //Se inserta el id de sello y el numero de folio en la tabla teiesello para completar el registro
                    statement.executeQuery("INSERT INTO tienesello (idSello, numFolioCarnet) VALUES " +
                            "(" + resultSetidSello.getString(0) + "," + resultSetFolio.getString(0) + ")");
                    connection.close();
                    borrarDatosActividad();
                } else {
                    if (errores[1] == 1) {
                        System.out.println("Fuera del horario");
                    } else {
                        System.out.println("Codigo incorrecto");
                    }
                }
            }
        }catch (Exception e){
            Log.d(e.toString(),"falla");
        }
    }

    public int[] validarHorario(String idActividad) throws SQLException, ClassNotFoundException {
        int[] errores = new int[2];

        System.out.println("ANTES DE LA CONEXIÓN");
        connection = connectionClass.CONN();
        System.out.println("DESPUES DE LA CONEXIÓN");

        if(connectionClass.CONN() == null){
            Log.e("Connection Error", "CONNECTIONCLASS CONN ESTÁ VACIA.");
        }

        try{
            if (connection == null) {
                Log.e("Connection Error", "No se pudo establecer la conexión a la base de datos.");
                errores[0] = 1;
                return errores;
            }else {
                System.out.println("CONEXION ESTABLECIDA");
            }

            Statement statement = connection.createStatement();
            System.out.println("STATEMENT");

            ResultSet resultHorario = statement.executeQuery("SELECT  horarioInicioActividad, horarioFinActividad, fechaActividad FROM actividad where idActividad = " + idActividad);
            System.out.println("SE OBTIENEN LOS VALORES DE LA BASE DE DATOS SEGÚN: " + idActividad);

            if (resultHorario.wasNull()) {
                errores[0] = 1;
            }
            System.out.println("OBTENER INSTANCIA DE CALENDAR");

            // Obtener instancia de Calendar
            Calendar calendar = Calendar.getInstance();
            Calendar calendar2 = Calendar.getInstance();
            Calendar calendar3 = Calendar.getInstance();

            // Obtener la fecha del sistema
            calendar.get(Calendar.DATE);
            calendar.get(Calendar.MINUTE);
            calendar.get(Calendar.HOUR_OF_DAY);

            calendar2.set(Calendar.HOUR_OF_DAY, resultHorario.getTime(1).getHours());
            calendar2.set(Calendar.MINUTE, resultHorario.getTime(1).getMinutes());
            calendar2.set(Calendar.DATE, resultHorario.getDate(3).getDate());

            calendar3.set(Calendar.HOUR_OF_DAY, resultHorario.getTime(2).getHours());
            calendar3.set(Calendar.MINUTE, resultHorario.getTime(2).getMinutes());
            calendar3.set(Calendar.DATE, resultHorario.getDate(3).getDate());

            if (calendar.before(calendar2) || calendar.after(calendar3)) {
                errores[1] = 1;
            }
            connection.close();
        }catch (Exception e){
            Log.d(e.toString(),"falla");
        }
        return errores;
    }

    public static String limpiarCodigo(String input) {
        String regex = "QR_CONTENT=([^}]+)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public void cargarPreferenciasMatricula(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        matriculaAlumno = preferences.getString("matricula","No existe");
    }
    public void borrarDatosActividad(){
        SharedPreferences preferences = getActivity().getSharedPreferences("datosActividad", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}