package com.alucintech.saci;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class AlumnoFragment extends Fragment {

    TextView nombreAlumno, nombrePrograma;
    String matricula="", nomAlumno="", nomPrograma="", apeMaterno="", apePaterno="";


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

    }

    private void cargarPreferencias(){

        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno", Context.MODE_PRIVATE);
        nomPrograma = preferences.getString("programaEducativo", "No existe");
        nomAlumno = preferences.getString("nombre", "No existe");
        apeMaterno = preferences.getString("apellidoMaterno", "No existe");
        apePaterno = preferences.getString("apellidoPaterno", "No existe");

    }


}