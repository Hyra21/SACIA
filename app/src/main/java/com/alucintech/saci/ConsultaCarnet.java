package com.alucintech.saci;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConsultaCarnet extends Fragment {

    ImageButton btnRegresar, btnScanner, btnMenu;
    NavController navController;

    class Task extends AsyncTask<View, View, View>{
        //Aquí subimos todos los datos a la base de datos
        @Override
        protected View doInBackground(View... views) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd", "HYRA99", "root");
                Statement statement = connection.createStatement();

                ResultSet resultSet = statement.executeQuery("");
            } catch (Exception e){

            }
            return null;
        }
        //Aquí crearemos la vista de los carnets del alumno
        @Override
        protected void onPostExecute(View view) {
            super.onPostExecute(view);
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

        new Task().execute();

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
    }

    public void scanearQR(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }




}