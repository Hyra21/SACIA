package com.alucintech.saci.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.alucintech.saci.R;
import com.alucintech.saci.helpers.ScanQRHelper;
import com.alucintech.saci.objects.Actividades;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class informacionActividadFragment extends Fragment {

    String nombreEvento, cicloEscolar, fechaInicio, fechaFin, descripcion, imagen;
    MultiAutoCompleteTextView mtwNombreEvento, mtwDescripcion;
    ImageView imwEvento;
    TextView twCiclo, twFechaInicio, twFechaFin;

    Actividades actividades;
    private ScanQRHelper scanQRHelper;
    DecoratedBarcodeView barcodeView;
    int posicion;
    MultiAutoCompleteTextView mtwNombreActividad, mtwDescripcionActividad;
    ImageButton imgbtRegresar, imgbtScanner, imgbtLink;
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    NavController navController;
    ImageView  imwActividad;
    TextView twTipoActividadInfo, twFechaInfo,twHorarioInfo, twModalidadInfo, twEspaciosInfo, twPonente;

    int id;
    String nombreActividad;
    String descripcionActividad;
    String tipoActividad;
    String fechaActividad;
    String horarioInicio;
    String horarioFin;
    String lugarActividad;
    int espaciosDisponibles;
    String modalidadActividad;
    String enlaceVirtual;
    String imagenActividad;
    String ponenteActividad;
    int idEvento;
    int numEmpleadoAdministrador;
    String estadoActividad;

    Boolean flag = false;

    class Task extends AsyncTask<View, View, View> {
        //Aquí subimos todos los datos a la base de datos
        @Override
        protected View doInBackground(View... views) {
            return null;
        }
        //Aquí crearemos la vista de los carnets del alumno en el recyclerView
        @Override
        protected void onPostExecute(View view) {
            super.onPostExecute(view);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_informacion_actividad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarPreferencias();

        CodeScannerView scannerView = view.findViewById(R.id.scanner_view);
        scanQRHelper = new ScanQRHelper(this, scannerView);

        toolbar = view.findViewById(R.id.topAppBar);
        drawerLayout = view.findViewById(R.id.informacionActividadDrawerLayout);
        navigationView = view.findViewById(R.id.nwConsultaActividad);
        mtwNombreActividad = view.findViewById(R.id.mtwNombreActividadInformacion);
        navController = Navigation.findNavController(view);
        imgbtRegresar = view.findViewById(R.id.imgbtRegresarActividad);
        imgbtScanner = view.findViewById(R.id.imgbtScannerActividad);

        mtwDescripcionActividad = view.findViewById(R.id.mtwDescripcionActividad);
        imwActividad = view.findViewById(R.id.imwActividad);
        twTipoActividadInfo = view.findViewById(R.id.twTipoActividadInfo);
        twFechaInfo = view.findViewById(R.id.twFechaInfo);
        twHorarioInfo = view.findViewById(R.id.twHorarioInfo);
        twModalidadInfo = view.findViewById(R.id.twModalidadInfo);
        twEspaciosInfo = view.findViewById(R.id.twEspaciosInfo);
        twPonente = view.findViewById(R.id.twPonente);
        new Task().execute();

        Toast.makeText(getActivity(),Integer.toString(idEvento),Toast.LENGTH_LONG).show();

        imgbtLink = view.findViewById(R.id.imgbtLinkInfo);

        // Ingresamos los datos de la actividad en los componentes
        mtwNombreActividad.setText(nombreActividad);

        // Metodo para obtener la imagen de la actividad
        byte[] imagenDecodificada = android.util.Base64.decode(imagenActividad, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagenDecodificada,0,imagenDecodificada.length);
        imwActividad.setImageBitmap(bitmap);

        twTipoActividadInfo.setText(tipoActividad);
        twFechaInfo.setText(fechaActividad);
        twHorarioInfo.setText(horarioInicio + " - " + horarioFin);
        twModalidadInfo.setText(modalidadActividad);

        if(modalidadActividad.equals("Virtual")){
            imgbtLink.setVisibility(View.VISIBLE);
            imgbtLink.setClickable(true);
        }

        twEspaciosInfo.setText(Integer.toString(espaciosDisponibles));
        twPonente.setText(ponenteActividad);

        mtwDescripcionActividad.setText(descripcionActividad);
        mtwNombreActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog dialog = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
                dialog.setContentView(R.layout.mostrar_evento);

                // Cerrar el diálogo al tocar fuera de él
                dialog.setCanceledOnTouchOutside(true);

                dialog.show();
            }
        });

        imgbtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(enlaceVirtual));
                startActivity(intent);
            }
        });

        imgbtRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_informacionActividadFragment_to_consultaActividades);
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
                        navController.navigate(R.id.action_informacionActividadFragment_to_inicioFragment);
                        break;
                }

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        scanQRHelper.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        scanQRHelper.pause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        scanQRHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("actividadTemp",Context.MODE_PRIVATE);
        posicion = preferences.getInt("posicion",1000);

        nombreActividad = preferences.getString("nombreActividad", "no");
        descripcionActividad = preferences.getString("descripcionActividad", "no");
        tipoActividad = preferences.getString("tipoActividad", "no");
        fechaActividad = preferences.getString("fecha", "no");
        horarioInicio = preferences.getString("horarioInicio", "no");
        horarioFin = preferences.getString("horarioFin", "no");
        lugarActividad = preferences.getString("lugar", "no");
        espaciosDisponibles = preferences.getInt("espacios", 0);
        modalidadActividad = preferences.getString("modalidad", "no");
        enlaceVirtual = preferences.getString("enlace", "no");
        imagenActividad = preferences.getString("imagen", "no");
        ponenteActividad = preferences.getString("ponente", "no");
        idEvento = preferences.getInt("idEvento", 0);
        numEmpleadoAdministrador = preferences.getInt("numEmpleado", 0);
        estadoActividad = preferences.getString("estado", "no");
    }

    public void borrarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }




}