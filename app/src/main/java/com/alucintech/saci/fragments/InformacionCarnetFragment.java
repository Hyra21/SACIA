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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alucintech.saci.helpers.ScanQRHelper;
import com.alucintech.saci.objects.Carnet;
import com.alucintech.saci.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.ArrayList;

public class InformacionCarnetFragment extends Fragment {

    ArrayList<Carnet> carnets;

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

        barcodeView = view.findViewById(R.id.barcodeViewCarnet);
        scanQRHelper = new ScanQRHelper(this, barcodeView);

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
                barcodeView.setVisibility(View.VISIBLE);
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
}