package com.alucintech.saci;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
    Boolean CorreoEncontrado=false, ContraEncontrada=false;
    NavController navController;
    View viewNav;
    private String Matricula="", codigoPrograma="", nomPrograma="";
    private String nombreAlumno="", apellidoMAlumno="", apellidoPAlumno="";

    class Task extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            buscarCredenciales();
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {

            //Mensajes por si el correo o la contraseña son incorrectos
            if(CorreoEncontrado==false){
                Toast.makeText(getActivity(),"Correo no existe",Toast.LENGTH_LONG).show();
                CorreoEncontrado = false;
            }
            if(ContraEncontrada==false){
                Toast.makeText(getActivity(),"Contraseña incorrecta",Toast.LENGTH_LONG).show();
                ContraEncontrada = false;
            }else{
                Correo.setText("");
                Contrasena.setText("");
                //Aqui realizamos la navegacion hacia la siguiente pantalla
                navController = Navigation.findNavController(viewNav);
                navController.navigate(R.id.action_inicioFragment_to_alumnoFragment);
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
        if(cargarPreferencias()!=true){
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
                        //Aqui almacenamos view obtenido en el onCreateView() para luego utilizarlo en el navController
                        viewNav = view;
                        new Task().execute();

                    }

                }
            });
        }else{
            navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_inicioFragment_to_alumnoFragment);
        }



    }
    //Creacion del archivo para almacenar credenciales
    private void guardarPreferencias(){

        //Linea de codigo para crear el archivo credencialesAlumno.xml
            SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno",Context.MODE_PRIVATE);

        //Aqui almacenamos las credenciales obtenidas de los inputText de correo y contrasena en distintos Strings
        String usuario = Correo.getText().toString();
        String contrasena = Contrasena.getText().toString();

        //Aqui editamos el archivo creado y le agregamos los datos
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", usuario);
        editor.putString("password", contrasena);
        editor.putString("matricula", Matricula);
        editor.putString("programaEducativo", nomPrograma);
        editor.putString("nombre", nombreAlumno);
        editor.putString("apellidoMaterno", apellidoMAlumno);
        editor.putString("apellidoPaterno", apellidoPAlumno);
        editor.putString("primerInicio","si");

        //Con este commit terminamos de almacenar el archivo en el sistema
        editor.commit();
    }
    //Lectura del archivo para obtener las credenciales almacenadas
    private boolean cargarPreferencias(){
        String flag = "No existe";

        //Linea de codigo para buscar el archivo credencialesAlumno.xml
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno",Context.MODE_PRIVATE);

        //Aqui obtenemos las credenciales almacenadas, si no existen se guarda "No existe"
        String usuario = preferences.getString("user","No existe");
        String contrasena = preferences.getString("password","No existe");

        //Aqui se realiza la validacion de que existen las credenciales
        if(usuario.equals(flag) || contrasena.equals(flag)){
            return false;
        }
        return true;
    }

    private void buscarCredenciales(){
        String CorreoS=Correo.getText().toString(), ContrasenaS=Contrasena.getText().toString();

        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd", "HYRA99", "root");
            Statement statement = connection.createStatement();

            //Query para  obtener la tabla de usuarios
            ResultSet resultSet = statement.executeQuery("SELECT correo, nombreUsuario, apellidoMaternoUsuario, apellidoPaternoUsuario, contrasenaUsuario, tipoUsuario FROM usuarios");
            while (resultSet.next()) {
                if (resultSet.getString(6).equals("Alumno")) {
                    if (resultSet.getString(1).equals(CorreoS)) {
                        CorreoEncontrado = true;
                        if (resultSet.getString(5).equals(ContrasenaS)) {
                            nombreAlumno = resultSet.getString(2);
                            apellidoMAlumno = resultSet.getString(3);
                            apellidoPAlumno = resultSet.getString(4);
                            ContraEncontrada = true;
                            buscarInformacionAlumno(CorreoS);
                        }
                    }
                }
            }
            connection.close();
        } catch (Exception e){

        }
    }

    private void buscarInformacionAlumno(String CorreoS){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd", "HYRA99", "root");
            Statement statement = connection.createStatement();

            //Query para obtener las tabla que identifican a alumno
            ResultSet resultSet = statement.executeQuery("SElECT matricula, correoAlumno FROM identificaAlumno");
            //Validacion de credenciales

            while (resultSet.next()) {
                if (resultSet.getString(2).equals(CorreoS)) {
                    Matricula = resultSet.getString(1);
                }
            }

            resultSet = statement.executeQuery("SELECT matriculaAlumno, codigoProgramaEducativoAlumno FROM alumno");
            while (resultSet.next()) {
                if (resultSet.getString(1).equals(Matricula)) {
                    codigoPrograma = resultSet.getString(2);
                }
            }

            resultSet = statement.executeQuery("SELECT codigoProgramaEducativo, nombreProgramaEducativo FROM programaEducativo");
            while (resultSet.next()) {
                if (resultSet.getString(1).equals(codigoPrograma)) {
                    nomPrograma = resultSet.getString(2);
                    guardarPreferencias();
                }
            }
            connection.close();
        } catch (Exception e){

        }
    }
}