package com.alucintech.saci;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;



public class InicioFragment extends Fragment {
    TextInputEditText Correo , Contrasena;
    Button IniSesion;
    TextView text;
    Boolean CorreoEncontrado=false, ContraEncontrada=false;

    class Task extends AsyncTask<Void, Void, Void>{
        String CorreoS=Correo.getText().toString(), ContrasenaS=Contrasena.getText().toString(), error="";


        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //Conexion con la base de datos y obtencion de los datos
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd","HYRA99","root");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT correo, contrasenaUsuario FROM usuarios");

                //Busqueda de credenciales
                while(resultSet.next()){
                    if(resultSet.getString(1).equals(CorreoS) ){
                        CorreoEncontrado = true;
                        if(resultSet.getString(2).equals(ContrasenaS)){
                            ContraEncontrada = true;
                            return null;
                        }
                    }

                }

            } catch (Exception e){
                error = e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            //Mensajes por si el correo o la contraseña son incorrectos
            if(CorreoEncontrado==false){
                Toast.makeText(getActivity(),"Correo incorrecto o no existe",Toast.LENGTH_LONG).show();
                CorreoEncontrado = false;
            }
            if(ContraEncontrada==false){
                Toast.makeText(getActivity(),"Contraseña incorrecta",Toast.LENGTH_LONG).show();
                ContraEncontrada = false;
            }
            super.onPostExecute(unused);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IniSesion = view.findViewById(R.id.buttonInicioSesion);
        Correo = view.findViewById(R.id.textInputCorreo);
        Contrasena = view.findViewById(R.id.textInputContrasena);


        IniSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Correo.getText().toString().isEmpty()) {
                    Toast.makeText(getContext(),"Debes ingresar un correo",Toast.LENGTH_LONG).show();
                }
                if (Contrasena.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(),"Debes ingresar una contrasenia",Toast.LENGTH_LONG).show();
                } else {
                    new Task().execute();
                }

            }
        });
    }
}