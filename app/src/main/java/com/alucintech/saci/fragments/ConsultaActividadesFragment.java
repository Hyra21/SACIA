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

import com.alucintech.saci.connection.ConnectionClass;
import com.alucintech.saci.R;
import com.alucintech.saci.adapters.Actividad_rwAdapter;
import com.alucintech.saci.helpers.ScanQRHelper;
import com.alucintech.saci.objects.Actividades;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ConsultaActividadesFragment extends Fragment {
    ConnectionClass connectionClass;
    Connection connection;
    ArrayList<Actividades> actividades = new ArrayList<>();
    private ScanQRHelper scanQRHelper;
    DecoratedBarcodeView barcodeView;
    String  codigoProgramaEducativo;
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageButton imgbtRegresar, imgbtScanner;
    NavController navController;
    RecyclerView recyclerView;
    int[] idsActividad;
    int idEvento;
    Boolean flag = false;

    class Task extends AsyncTask<View, View, View> {

        @Override
        protected View doInBackground(View... views) {
            buscarActividadProgramaEducativo();
            buscarActividades(idsActividad);
            return null;
        }
        //Aquí crearemos la vista de la actividades
        @Override
        protected void onPostExecute(View view) {
            super.onPostExecute(view);
            Actividad_rwAdapter adapter = new Actividad_rwAdapter(getActivity(),actividades);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_consulta_actividades, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connectionClass = new ConnectionClass();
        cargarPreferencias();
        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        scanQRHelper = new ScanQRHelper(this, scannerView);

        toolbar = view.findViewById(R.id.topAppBar);
        drawerLayout = view.findViewById(R.id.actividadesDrawerLayout);
        navigationView = view.findViewById(R.id.nwConsultaActividades);
        imgbtRegresar = view.findViewById(R.id.imgbtRegresarActividades);
        imgbtScanner = view.findViewById(R.id.imgbtScannerActividades);
        recyclerView = view.findViewById(R.id.rwActividades);
        navController = Navigation.findNavController(view);
        new Task().execute();
        imgbtRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_consultaActividades_to_alumnoFragment);
            }
        });

        imgbtScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scannerView.setVisibility(View.VISIBLE);
                scanQRHelper.startScan();
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
                        navController.navigate(R.id.action_consultaActividades_to_inicioFragment);
                        break;
                }

                return true;
            }
        });

    }
    //Obtiene los datos de los archivos
    //Se obtiene el codigo del programa educativo del alumno y
    // el id del evento al que pertenecen las actividades que se mostrarán
    public void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        SharedPreferences preferencesEvento = getActivity().getSharedPreferences("eventoTemp", Context.MODE_PRIVATE);

        codigoProgramaEducativo = preferences.getString("codigoProgramaEducativo","No existe");
        idEvento = preferencesEvento.getInt("idEvento",0);
    }

    public void borrarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public void scanearQR(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

    public void buscarActividadProgramaEducativo(){

        try {
            connection = connectionClass.CONN();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT codigoProgramaEducativo, idActividad FROM afinprogramaseducativos  WHERE codigoProgramaEducativo="+codigoProgramaEducativo);

            resultSet.last();
            int rowCount = resultSet.getRow();
            resultSet.beforeFirst();

            idsActividad = new int[rowCount];
            int i = 0;
            while (resultSet.next()){
                if(resultSet.getString(1).equals(codigoProgramaEducativo)){
                    idsActividad[i] = resultSet.getInt(2);

                    i++;
                }
            }

            connection.close();
        } catch (Exception e){

        }
    }

    public void buscarActividades(int[] idsActividad){
        int id, espaciosDisponibles,idEvento,numEmpleadoAdministrador;
        String nombreActividad, descripcionActividad, tipoActividad, fechaActividad, horarioInicio, horarioFin, lugarActividad;
        String modalidadActividad, enlaceVirtual, imagenActividad, ponenteActividad, estadoActividad;

        try {
            connection = connectionClass.CONN();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT idActividad, nombreActividad, descripcionActividad, tipoActividad, fechaActividad, horarioInicioActividad, " +
                    "horarioFinActividad, direccionActividad, espaciosDisponiblesActividad, modalidadActividad, enlaceVirtual, imagenActividad, idEvento, " +
                    "numEmpleadoAdministradorActividad, ponenteActividad, estadoActividad FROM actividad");

            int i = 0;
            while (resultSet.next()){

                if(resultSet.getInt(1) == idsActividad[i]){
                    Log.e("Actividades", String.valueOf(idsActividad[i]));

                    id = resultSet.getInt(1);
                    nombreActividad = resultSet.getString(2);
                    descripcionActividad = resultSet.getString(3);
                    tipoActividad = resultSet.getString(4);
                    fechaActividad = resultSet.getString(5);
                    horarioInicio = resultSet.getString(6);
                    horarioFin = resultSet.getString(7);
                    lugarActividad = resultSet.getString(8);
                    espaciosDisponibles = resultSet.getInt(9);
                    modalidadActividad = resultSet.getString(10);
                    enlaceVirtual = resultSet.getString(11);
                    imagenActividad = android.util.Base64.encodeToString(resultSet.getString(12).getBytes(), android.util.Base64.DEFAULT);
                    ponenteActividad = resultSet.getString(15);
                    idEvento = resultSet.getInt(13);
                    numEmpleadoAdministrador = resultSet.getInt(14);
                    estadoActividad = resultSet.getString(16);

                    actividades.add(new Actividades(id,nombreActividad,descripcionActividad,tipoActividad,fechaActividad,horarioInicio,horarioFin,lugarActividad,
                            espaciosDisponibles,modalidadActividad,enlaceVirtual,imagenActividad,ponenteActividad,idEvento,numEmpleadoAdministrador,estadoActividad));
                    i++;
                }

            }
            connection.close();
        }catch (Exception e){
            Log.d(e.toString(),"falla");
        }
    }

}