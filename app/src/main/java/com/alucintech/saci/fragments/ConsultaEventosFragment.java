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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alucintech.saci.connection.ConnectionClass;
import com.alucintech.saci.R;
import com.alucintech.saci.adapters.Evento_rwAdapter;
import com.alucintech.saci.objects.Eventos;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ConsultaEventosFragment extends Fragment {
    ConnectionClass connectionClass;
    Connection connection;
    ArrayList<Eventos> eventos = new ArrayList<>();
    DecoratedBarcodeView barcodeView;
    String  codigoProgramaEducativo;
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton imgbtRegresar, imgbtScanner;
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
        navController = Navigation.findNavController(view);
        new ConsultaEventosFragment.Task().execute();
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
                        String imagenEvento = android.util.Base64.encodeToString(resultSet.getString(9).getBytes(), android.util.Base64.DEFAULT);
                        eventos.add(new Eventos(id,nombreEvento, cicloEscolarEvento, fechaInicioEvento,
                                fechaFinEvento, descripcionEvento, estadoEvento, numEmpleadoAdministradorEvento, imagenEvento));
                        System.out.println(""+eventos.get(0).getNombreEvento());
            }
            connection.close();
        }catch (Exception e){
            Log.d(e.toString(),"falla");
        }
    }
}