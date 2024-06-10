package com.alucintech.saci.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


import com.alucintech.saci.connection.ConnectionClass;
import com.alucintech.saci.helpers.CifradorHelper;
import com.alucintech.saci.helpers.ScanQRHelper;
import com.alucintech.saci.objects.Carnet;
import com.alucintech.saci.adapters.Carnet_rwAdapter;
import com.alucintech.saci.R;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ConsultaCarnetFragment extends Fragment {
    ConnectionClass connectionClass;
    Connection connection;
    ArrayList<Carnet> carnets = new ArrayList<>();
    private ScanQRHelper scanQRHelper;
    int numCarnetSemestre=0, numCarnetCarrera=0, folioActual = 0, claveCarnet=0;
    String matriculaAlumno="", carnetsCompletos="";
    String estadoCarnet="";
    ImageButton btnRegresar, btnScanner;
    NavController navController;
    RecyclerView recyclerView;
    View vista;
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Boolean flagNingunCarnet = false;

    Carnet c;

    class Task extends AsyncTask<View, View, View>{
        //Aquí subimos todos los datos a la base de datos
        @Override
        protected View doInBackground(View... views) {

            carnetsActuales();

            if(numCarnetSemestre==0 && numCarnetCarrera==0){
                flagNingunCarnet = true;
                generarCarnet();
            }else if(numCarnetCarrera < 4){
                carnetActual();
                if(numCarnetSemestre < 2 && estadoCarnet.equals("Completado")){
                    generarCarnet();
                }
            }
            mostrarCarnet();

            return null;
        }
        //Aquí crearemos la vista de los carnets del alumno en el recyclerView
        @Override
        protected void onPostExecute(View view) {
            super.onPostExecute(view);
            Carnet_rwAdapter adapter = new Carnet_rwAdapter(getActivity(),carnets);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consulta_carnet, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectionClass = new ConnectionClass();
        cargarPreferencias();
        toolbar = view.findViewById(R.id.topAppBar);
        drawerLayout = view.findViewById(R.id.carnetDrawerLayout);
        navigationView = view.findViewById(R.id.nwConsultaCarnets);
        recyclerView = view.findViewById(R.id.rwCarnet);
        btnRegresar = view.findViewById(R.id.imgbtRegresar);
        btnScanner = view.findViewById(R.id.imgbtScanner);

        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        scanQRHelper = new ScanQRHelper(this, scannerView);

        vista = view;
        navController = Navigation.findNavController(view);
        new Task().execute();

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navController.navigate(R.id.action_consultaCarnet_to_alumnoFragment);
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
                        navController.navigate(R.id.action_consultaCarnet_to_inicioFragment);
                        break;
                }

                return true;
            }
        });

        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scannerView.setVisibility(View.VISIBLE);
                final Fragment currentFragment = ConsultaCarnetFragment.this;
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

    }

    public void borrarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void guardarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("crendencialesAlumno", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("carnetsCompletos", "Si");
        editor.commit();
    }

    //Metodo para obtener los datos del alumno matricula y carnets completos del archivo shared preferences "credencialesAlumno"
    public void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);

        matriculaAlumno = preferences.getString("matricula", "No existe");
        carnetsCompletos = preferences.getString("carnetsCompletos", "No");
    }

    public void scanearQR(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

    //Metodo para obtener los carnets del semestre actual y los carnets de toda la carrera del alumno
    private void carnetsActuales(){
        try{
            connection = connectionClass.CONN();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT matriculaAlumno, numcarnetSemestre, numCarnetsCarrera FROM alumno");
            while (resultSet.next()){
                if(resultSet.getString(1).equals(matriculaAlumno)){

                    numCarnetSemestre = resultSet.getInt(2);
                    numCarnetCarrera = resultSet.getInt(3);
                }
            }
            connection.close();

        } catch (Exception e){

        }

    }

    //Metodo para obtener el ultimo folio generado y generar uno nuevo para el carnet
    private void folioActual(){
        try{
            connection = connectionClass.CONN();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT numFolio FROM carnet");
            while (resultSet.next()){
                    folioActual = resultSet.getInt(1);
            }
            folioActual ++;
            connection.close();
        }catch (Exception e){

        }
    }

    //Metodo para obtener el ultimo carnet generado del alumno
    private void carnetActual(){
        try{
            connection = connectionClass.CONN();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT matriculaAlumno, estadoCarnet, codigoCarnet FROM carnet");

            while(resultSet.next()){
                if(resultSet.getString(1).equals(matriculaAlumno)){
                    estadoCarnet = resultSet.getString(2);
                    claveCarnet = resultSet.getInt(3);
                }
            }
            connection.close();
        }catch (Exception e){

        }
    }

    //Metodo para generar y almacenar carnets en la base de datos
    private void generarCarnet(){
        try{
            folioActual();
            connection = connectionClass.CONN();
            Statement statement = connection.createStatement();
            int numeroCarnets = numCarnetCarrera + numCarnetSemestre;
            int nuevaClave;
            if(numeroCarnets == 0){
                nuevaClave = 16981;
            }else{
                if(numeroCarnets < 3){
                    nuevaClave = claveCarnet + 1;
                }else{
                    nuevaClave = 18073;
                }
            }

            String fechaActual = obtenerFecha();
            String cicloActual = obtenerCicloEscolarActual(fechaActual);

            statement.executeUpdate("INSERT INTO carnet VALUES (" + folioActual + "," + 0 + ",'" + cicloActual + "','" + fechaActual + "'," + matriculaAlumno + ",'En Proceso'," + nuevaClave + ")");

            numCarnetSemestre++;

            statement.executeUpdate("UPDATE alumno set numcarnetSemestre = '" + numCarnetSemestre + "' WHERE matriculaAlumno = '" + matriculaAlumno + "'");
            connection.close();
        }catch (Exception e){

        }
    }

    private void mostrarCarnet(){

        int folio = 0;
        int sellos = 0;
        String ciclo = "";
        String fechaC = "";
        int matricula = 0;
        String estado = "";
        int clave = 0;

        c = new Carnet(folio,sellos,ciclo,fechaC,matricula,estado,clave);

        try {
            connection = connectionClass.CONN();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT numFolio, numeroSellosCarnet, cicloEscolarCarnet, fechaCreacionCarnet, matriculaAlumno, estadoCarnet, codigoCarnet FROM carnet");

            while (resultSet.next()){
                if(resultSet.getString(5).equals(matriculaAlumno)){

                    c.setNumFolio(resultSet.getInt(1));
                    c.setCicloEscolarCarnet(resultSet.getString(3));
                    c.setFechaCreacionCarnet( resultSet.getString(4));
                    c.setMatriculaAlumno(resultSet.getInt(5));
                    c.setEstadoCarnet(resultSet.getString(6));
                    c.setClaveCarnet(resultSet.getInt(7));

                }
            }
            resultSet = statement.executeQuery("SELECT sello.cantidad FROM sello inner join tienesello on sello.idSello = tienesello.idSello inner join carnet on tienesello.numFolioCarnet = carnet.numFolio where carnet.matriculaAlumno = "+ matriculaAlumno);
            resultSet.next();
            c.setNumeroSellosCarnet(resultSet.getInt(1));

            carnets.add(new Carnet(c.getNumFolio(),c.getNumeroSellosCarnet(),c.getCicloEscolarCarnet(),c.getFechaCreacionCarnet(),c.getMatriculaAlumno(),c.getEstadoCarnet(),c.getClaveCarnet()));
            connection.close();
        } catch (Exception e){
            Log.d(e.toString(),"falla1");
        }
    }



    private String obtenerFecha(){
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy/MM/dd");
        return formatoFecha.format(date);
    }

    private String obtenerCicloEscolarActual(String fechaActual){
        String[] fechaSeparada = fechaActual.split("/");
        int mes = Integer.parseInt(fechaSeparada[1]);

        if(mes <= 6){
            return fechaSeparada[0] + "-1";
        }

        if(mes <=12){
            return  fechaSeparada[0] + "-2";
        }

        return "nada";
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
        connectionClass = new ConnectionClass();
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