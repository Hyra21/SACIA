package com.alucintech.saci;

import android.content.Context;
import android.content.SharedPreferences;
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

public class onboardingFragment1 extends Fragment {

    NavController navController;
    Button siguiente1, skip1;
    String primerInicio="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarPreferencias();
        navController = Navigation.findNavController(view);
        siguiente1 = view.findViewById(R.id.siguiente1);
        skip1 = view.findViewById(R.id.skip1);

        if(primerInicio.equals("si")){
            navController.navigate(R.id.action_onboardingFragment1_to_inicioFragment);
        }

        siguiente1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_onboardingFragment1_to_onboardingFragment2);
            }
        });

        skip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_onboardingFragment1_to_inicioFragment);
            }
        });
    }

    private void cargarPreferencias(){
        SharedPreferences preferences = getActivity().getSharedPreferences("credencialesAlumno",Context.MODE_PRIVATE);
        primerInicio = preferences.getString("primerInicio","");
    }
}