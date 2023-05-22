package com.alucintech.saci;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AlumnoFragment extends Fragment {

    TextView nombreAlumno, nombrePrograma;
    Button btnCarnets;
    String matricula="", nomAlumno="", nomPrograma="", apeMaterno="", apePaterno="";
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
        cargarPreferencias();
        nombreAlumno = view.findViewById(R.id.twNombreAlumno);
        nombrePrograma = view.findViewById(R.id.twNombrePrograma);
        String nombreCompleto = nomAlumno + " " + apePaterno + " " + apeMaterno;
        nombreAlumno.setText(nombreCompleto);
        nombrePrograma.setText(nomPrograma);

        showNotification(getContext(),"SACI", "Bienvenido alumno al sistema administrador del carnet institucional","https://ingenieria.mxl.uabc.mx/");
        btnCarnets = view.findViewById(R.id.btnCarnets);

        btnCarnets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigation = Navigation.findNavController(view);
                navigation.navigate(R.id.action_alumnoFragment_to_consultaCarnet);
            }
        });
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