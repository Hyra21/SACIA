package com.alucintech.saci.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MultiAutoCompleteTextView;

import com.alucintech.saci.R;

public class informacionActividadFragment extends Fragment {

    MultiAutoCompleteTextView multiAutoCompleteTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_informacion_actividad, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        multiAutoCompleteTextView = view.findViewById(R.id.mtwNombreActividadInformacion);

        multiAutoCompleteTextView.setText("La semana de ingenieria presenta:\n" + "Seminario de pildoras i+d: fanta sabor sandia + frambruesa ");
    }
}