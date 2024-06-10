package com.alucintech.saci.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alucintech.saci.connection.ConnectionClass;
import com.alucintech.saci.helpers.CifradorHelper;
import com.alucintech.saci.helpers.ScanQRHelper;
import com.alucintech.saci.objects.Carnet;
import com.alucintech.saci.R;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

public class InformacionCarnetFragment extends Fragment {

    ArrayList<Carnet> carnets;
    ConnectionClass connectionClass;
    Connection connection;
    String matriculaAlumno;
    private ScanQRHelper scanQRHelper;
    DecoratedBarcodeView barcodeView;
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    NavController navigation;

    ImageButton regresar, scanner;

    TextView twNombreAlumno, twMatricula, twPrograma;
    TextView twNumFolio, twCiclo, twFecha, twClave;

    int numFolio, claveCarnet, numCarnet;
    String nomAlumno, matricula, programa, ciclo, fecha;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_informacion_carnet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarPreferencias();
        connectionClass = new ConnectionClass();

        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        scanQRHelper = new ScanQRHelper(this, scannerView);

        toolbar = view.findViewById(R.id.topAppBar);
        toolbar.setTitleCentered(true);
        drawerLayout = view.findViewById(R.id.informacionCarnetDrawerLayout);
        navigationView = view.findViewById(R.id.nwInformacionCarnet);
        navigation = Navigation.findNavController(view);

        twNombreAlumno = view.findViewById(R.id.twNombreAlumnoInformacion);
        twMatricula = view.findViewById(R.id.twMatriculaAlumnoInformacion);
        twPrograma = view.findViewById(R.id.twPeInformacion);

        twNumFolio = view.findViewById(R.id.twFolio);
        twCiclo = view.findViewById(R.id.twCiclo);
        twFecha = view.findViewById(R.id.twFecha);
        twClave = view.findViewById(R.id.twClave);

        regresar = view.findViewById(R.id.imgbtRegresarInfo);
        scanner = view.findViewById(R.id.imgbtScannerInfo);

        toolbar.setTitle("Carnet #" + numCarnet);

        twNombreAlumno.setText(nomAlumno);
        twMatricula.setText(matricula);
        twPrograma.setText(programa);

        twNumFolio.setText(""+numFolio);
        twCiclo.setText(ciclo);
        twFecha.setText(fecha);
        twClave.setText(""+claveCarnet);

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                borrarCarnetTemp();
                navigation.navigate(R.id.action_informacionCarnetFragment_to_consultaCarnet);
            }
        });

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scannerView.setVisibility(View.VISIBLE);
                final Fragment currentFragment = InformacionCarnetFragment.this;
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
                        borrarCredencialesAlumno();
                        borrarCarnetTemp();
                        navigation.navigate(R.id.action_informacionCarnetFragment_to_inicioFragment);
                        break;
                }

                return true;
            }
        });

    }

    public void scanearQR(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

    public void borrarCredencialesAlumno(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void borrarCarnetTemp(){
        SharedPreferences preferences = getActivity().getSharedPreferences("carnetTemp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);

        nomAlumno = preferences.getString("nombre", "no");
        nomAlumno += " " + preferences.getString("apellidoPaterno","no");
        nomAlumno += " " + preferences.getString("apellidoMaterno","no");
        matricula = preferences.getString("matricula","no");
        programa = preferences.getString("programaEducativo","no");


        preferences = getActivity().getSharedPreferences("carnetTemp", Context.MODE_PRIVATE);

        numFolio = preferences.getInt("numFolio",9);
        ciclo = preferences.getString("ciclo", "n");
        fecha = preferences.getString("fecha","n");
        claveCarnet = preferences.getInt("clave",9);

        if(claveCarnet == 16981){
            numCarnet = 1;
        }
        if(claveCarnet == 16982){
            numCarnet = 2;
        }
        if(claveCarnet == 16983){
            numCarnet = 3;
        }
        if(claveCarnet == 18073){
            numCarnet = 4;
        }
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