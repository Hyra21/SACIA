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


import com.alucintech.saci.Carnet;
import com.alucintech.saci.adapters.Carnet_rwAdapter;
import com.alucintech.saci.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ConsultaCarnetFragment extends Fragment {

    ArrayList<Carnet> carnets = new ArrayList<>();
    int numCarnetSemestre=0, numCarnetCarrera=0, folioActual = 0, claveCarnet=0;
    String matriculaAlumno="", carnetsCompletos="", estadoCarnet="";
    ImageButton btnRegresar, btnScanner;
    NavController navController;
    RecyclerView recyclerView;
    View vista;
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Boolean flagNingunCarnet = false;

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
        //Aquí crearemos la vista de los carnets del alumno
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
        cargarPreferencias();
        toolbar = view.findViewById(R.id.topAppBar);
        drawerLayout = view.findViewById(R.id.carnetDrawerLayout);
        navigationView = view.findViewById(R.id.carnetNavigation_view);
        recyclerView = view.findViewById(R.id.rwCarnet);
        btnRegresar = view.findViewById(R.id.imgbtRegresar);
        btnScanner = view.findViewById(R.id.imgbtScanner);
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
                scanearQR(view);
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
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd", "HYRA99", "root");
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
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd", "HYRA99", "root");
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
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd", "HYRA99", "root");
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
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd", "HYRA99", "root");
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
        int folio;
        int sellos;
        String ciclo;
        String fechaC;
        int matricula;
        String estado;
        int clave;


        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd", "HYRA99", "root");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT numFolio, numeroSellosCarnet, cicloEscolarCarnet, fechaCreacionCarnet, matriculaAlumno, estadoCarnet, codigoCarnet FROM carnet");

            while (resultSet.next()){
                if(resultSet.getString(5).equals(matriculaAlumno)){

                    folio = resultSet.getInt(1);
                    sellos = resultSet.getInt(2);
                    ciclo = resultSet.getString(3);
                    fechaC = resultSet.getString(4);
                    matricula = resultSet.getInt(5);
                    estado = resultSet.getString(6);
                    clave = resultSet.getInt(7);

                    carnets.add(new Carnet(folio,sellos,ciclo,fechaC,matricula,estado,clave));

                }
            }
            connection.close();
        } catch (Exception e){
            Log.d(e.toString(),"falla");
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

}