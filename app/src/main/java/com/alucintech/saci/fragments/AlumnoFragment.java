package com.alucintech.saci.fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alucintech.saci.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AlumnoFragment extends Fragment {

    TextView nombreAlumno, nombrePrograma;
    Button btnCarnets, btnActividades;
    String matricula="", nomAlumno="", nomPrograma="", apeMaterno="", apePaterno="";
    MaterialToolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    NavController navigation;

    private static final String CHANNEL_ID = "MyChannelId";
    private  static final int NOTIFICATION_ID = 1;

    class Task extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            try {

            } catch (Exception e){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alumno, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.topAppBar);
        drawerLayout = view.findViewById(R.id.alumnoDrawerLayout);
        navigationView = view.findViewById(R.id.nwAlumno);
        cargarPreferencias();
        nombreAlumno = view.findViewById(R.id.twNombreAlumno);
        nombrePrograma = view.findViewById(R.id.twNombrePrograma);
        String nombreCompleto = nomAlumno + " " + apePaterno + " " + apeMaterno;
        nombreAlumno.setText(nombreCompleto);
        nombrePrograma.setText(nomPrograma);

        navigation = Navigation.findNavController(view);

        showNotification(getContext(),"SACI", "Bienvenido alumno al sistema administrador del carnet institucional","https://ingenieria.mxl.uabc.mx/");
        btnCarnets = view.findViewById(R.id.btnCarnets);
        btnActividades = view.findViewById(R.id.btnActividades);

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

                        break;
                    case R.id.help:
                        Toast.makeText(getActivity(),"Para consultar ayuda contactarse al correo yhigaque@uabc.edu.mx", Toast.LENGTH_LONG).show();
                        break;
                    case R.id.cerrarSesion:
                        borrarPreferencias();
                        navigation.navigate(R.id.action_alumnoFragment_to_inicioFragment);
                        break;
                }

                return true;
            }
        });

        btnCarnets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                navigation.navigate(R.id.action_alumnoFragment_to_consultaCarnet);
            }
        });

        btnActividades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigation.navigate(R.id.action_alumnoFragment_to_consultaActividades);
            }
        });
    }

    public void borrarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    private void cargarPreferencias(){

        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        nomPrograma = preferences.getString("programaEducativo", "No existe");
        nomAlumno = preferences.getString("nombre", "No existe");
        apeMaterno = preferences.getString("apellidoMaterno", "No existe");
        apePaterno = preferences.getString("apellidoPaterno", "No existe");

    }

    public static void showNotification(Context context, String title, String message, String url) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel (required for Android 8.0 Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MyChannel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_account_circle_24) // Set your notification icon here
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Dismiss the notification when clicked


        // Show the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


}