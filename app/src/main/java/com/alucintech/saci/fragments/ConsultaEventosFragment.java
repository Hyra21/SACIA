package com.alucintech.saci.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alucintech.saci.connection.ConnectionClass;
import com.alucintech.saci.R;
import com.alucintech.saci.adapters.Evento_rwAdapter;
import com.alucintech.saci.helpers.CifradorHelper;
import com.alucintech.saci.helpers.ScanQRHelper;
import com.alucintech.saci.objects.Eventos;
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

public class ConsultaEventosFragment extends Fragment {
    ConnectionClass connectionClass;
    Connection connection;
    private ScanQRHelper scanQRHelper;
    String matriculaAlumno;
    ArrayList<Eventos> eventos = new ArrayList<>();
    DecoratedBarcodeView barcodeView;
    String  codigoProgramaEducativo;
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton imgbtRegresar, btnScanner;
    NavController navController;
    RecyclerView recyclerView;
    int[] idsEventos;
    Boolean flag = false;

    class Task extends AsyncTask<View, View, View> {

        @Override
        protected View doInBackground(View... views) {
            //buscarEventosFacultad();
            buscarEventos();
            return null;
        }
        //Aquí crearemos la vista de los eventos
        @Override
        protected void onPostExecute(View view) {
            super.onPostExecute(view);
            Evento_rwAdapter adapter = new Evento_rwAdapter(getActivity(),eventos);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consulta_evento, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectionClass = new ConnectionClass();
        cargarPreferencias();

        toolbar = view.findViewById(R.id.topAppBar);
        drawerLayout = view.findViewById(R.id.eventoDrawerLayout);
        navigationView = view.findViewById(R.id.nwConsultaEventos);
        imgbtRegresar = view.findViewById(R.id.imgbtRegresarEventos);
        recyclerView = view.findViewById(R.id.rwEventos);
        btnScanner = view.findViewById(R.id.imgbtScannerEventos);
        navController = Navigation.findNavController(view);
        new ConsultaEventosFragment.Task().execute();

        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        scanQRHelper = new ScanQRHelper(this, scannerView);

        imgbtRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_consultaEventosFragment_to_alumnoFragment);
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scannerView.setVisibility(View.VISIBLE);
                final Fragment currentFragment = ConsultaEventosFragment.this;
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
                        navController.navigate(R.id.action_consultaEventosFragment_to_inicioFragment);
                        break;
                }

                return true;
            }
        });
    }
    public void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        codigoProgramaEducativo = preferences.getString("codigoProgramaEducativo","No existe");
    }

    public void borrarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
/*
    public void buscarEventosFacultad(){

        try {
            connection = connectionClass.CONN();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM evento" +
                    " INNER JOIN afinfacultad ON evento.idEvento = afinfacultad.idEvento" +
                    " INNER JOIN programaeducativo ON afinfacultad.idFacultad = programaeducativo.idFacultadProgramaEducativo" +
                    " WHERE programaeducativo.codigoProgramaEducativo = "+codigoProgramaEducativo);

            resultSet.last();
            int rowCount = resultSet.getRow();
            resultSet.beforeFirst();

            //Encuentra los eventos que pertenecen a la facultad del alumno
            idsEventos = new int[rowCount];
            int i = 0;
            while (resultSet.next()){
                if(resultSet.getString(1).equals(codigoProgramaEducativo)){
                    idsEventos[i] = resultSet.getInt(2);
                    i++;
                }
            }

            connection.close();
        } catch (Exception e){

        }
    }
*/
    public void buscarEventos(){

        try {
            connection = connectionClass.CONN();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT evento.idEvento, nombreEvento, cicloEscolarEvento, fechaInicioEvento, fechaFinEvento, descripcionEvento, estadoEvento, numEmpleadoAdministradorEvento, imagenEvento FROM evento" +
                    " INNER JOIN afinfacultad ON evento.idEvento = afinfacultad.idEvento" +
                    " INNER JOIN programaeducativo ON afinfacultad.idFacultad = programaeducativo.idFacultadProgramaEducativo" +
                    " WHERE programaeducativo.codigoProgramaEducativo = "+codigoProgramaEducativo);
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"+codigoProgramaEducativo);

            while (resultSet.next()){
                        flag = true;
                        int id = resultSet.getInt(1);
                        String nombreEvento = resultSet.getString(2);
                        String cicloEscolarEvento = resultSet.getString(3);
                        String fechaInicioEvento = resultSet.getString(4);
                        String fechaFinEvento = resultSet.getString(5);
                        String descripcionEvento = resultSet.getString(6);
                        String estadoEvento = resultSet.getString(7);
                        int numEmpleadoAdministradorEvento = resultSet.getInt(8);
                        byte[] imagenEvento = resultSet.getBytes(9);
                        eventos.add(new Eventos(id,nombreEvento, cicloEscolarEvento, fechaInicioEvento,
                                fechaFinEvento, descripcionEvento, estadoEvento, numEmpleadoAdministradorEvento, imagenEvento));
                        System.out.println(""+eventos.get(0).getNombreEvento());
            }
            connection.close();
        }catch (Exception e){
            Log.d(e.toString(),"falla");
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