package com.alucintech.saci;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import com.alucintech.saci.fragments.CarnetFragment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ConsultaCarnet extends Fragment {

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    int numCarnetSemestre=0, numCarnetCarrera=0, folioActual = 0, claveCarnet=0, numSellos=0;
    String matriculaAlumno="", carnetsCompletos="", estadoCarnet="", cicloEscolar="", fechaCreacion="";
    ImageButton btnRegresar, btnScanner;
    NavController navController;
    List<Fragment> list = new ArrayList<>();
    View vista;

    class Task extends AsyncTask<View, View, View>{
        //Aquí subimos todos los datos a la base de datos
        @Override
        protected View doInBackground(View... views) {

            carnetsActuales();
            if(numCarnetSemestre==0 && numCarnetCarrera==0){
                generarCarnet();
            }else if(numCarnetCarrera < 4){
                carnetActual();
                if(numCarnetSemestre < 2 && estadoCarnet.equals("Completado")){
                    generarCarnet();
                }
            }

            return null;
        }
        //Aquí crearemos la vista de los carnets del alumno
        @Override
        protected void onPostExecute(View view) {

            super.onPostExecute(view);

            mostrarCarnet();
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
        btnRegresar = view.findViewById(R.id.imgbtRegresar);
        btnScanner = view.findViewById(R.id.imgbtScanner);
        vista = view;

        cargarPreferencias();
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_consultaCarnet_to_alumnoFragment);
            }
        });

        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanearQR(view);
            }
        });

        new Task().execute();
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
    public void carnetsActuales(){
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
    public void folioActual(){
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
    public void carnetActual(){
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
    public void generarCarnet(){
        try{
            folioActual();
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd", "HYRA99", "root");
            Statement statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO carnet VALUES (" + folioActual + "," + 0 + ",'2023-1','2023/05/22'," + matriculaAlumno + ",'En Proceso'," + 16981 + ")");

            numCarnetSemestre++;

            statement.executeUpdate("UPDATE alumno set numcarnetSemestre = '" + numCarnetSemestre + "' WHERE matriculaAlumno = '" + matriculaAlumno + "'");
            connection.close();
        }catch (Exception e){

        }
    }

    public void buscarCarnet(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd", "HYRA99", "root");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT numFolio, numeroSellosCarnet, cicloEscolarCarnet, fechaCreacionCarnet, matriculaAlumno, estadoCarnet, codigoCarnet FROM carnet");

            while(resultSet.next()){
                if(resultSet.getString(5).equals(matriculaAlumno)){
                    folioActual = resultSet.getInt(1);
                    numSellos = resultSet.getInt(2);
                    cicloEscolar = resultSet.getString(3);
                    fechaCreacion = resultSet.getString(4);
                    estadoCarnet = resultSet.getString(6);
                    claveCarnet = resultSet.getInt(7);
                }
            }
            connection.close();
        }catch (Exception e){

        }
    }

    //Metodo para agregar carnet al view pager de los carnets
    public void mostrarCarnet() {
        buscarCarnet();
        list.add(new CarnetFragment());

        pager = vista.findViewById(R.id.vpCarnets);
        pagerAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager(), list);
        pager.setAdapter(pagerAdapter);
    }
}