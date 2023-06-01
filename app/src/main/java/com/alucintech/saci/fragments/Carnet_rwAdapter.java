package com.alucintech.saci.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alucintech.saci.Carnet;
import com.alucintech.saci.R;

import java.util.ArrayList;

public class Carnet_rwAdapter extends RecyclerView.Adapter<Carnet_rwAdapter.MyViewHolder> {

    Context context;
    ArrayList<Carnet> carnets;

    public Carnet_rwAdapter(Context context, ArrayList<Carnet> carnets){
        this.context = context;
        this.carnets = carnets;
    }

    // Aqui es donde se inflara el layout (Lo que le dara la vista a cada una de las filas/carnets)
    @NonNull
    @Override
    public Carnet_rwAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.carnet, parent, false);
        return new Carnet_rwAdapter.MyViewHolder(view);

    }

    // Aqui se asignan todos los valores a todas las filas/carnets
    @Override
    public void onBindViewHolder(@NonNull Carnet_rwAdapter.MyViewHolder holder, int position) {


        int pos = position + 1;
        holder.twNumCarnet.setText("Carnet #"+ pos);

        //Aqui tambien iran los botonos

    }

    // Este simplemente nos regresa la cantidad de carnets que tiene el alumno
    @Override
    public int getItemCount() {
        return carnets.size();
    }

    // Esta clase nos permite obtener todos los componentes del layout carnet.xml y asi poder reutilizarlos y asignarle los datos de cada carnet
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        Button btnSello1, btnSello2, btnSello3, btnSello4, btnSello5, btnSello6, btnSello7, btnSello8;
        TextView twNumCarnet;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            btnSello1 = itemView.findViewById(R.id.btnSello1);
            btnSello2 = itemView.findViewById(R.id.btnSello2);
            btnSello3 = itemView.findViewById(R.id.btnSello3);
            btnSello4 = itemView.findViewById(R.id.btnSello4);
            btnSello5 = itemView.findViewById(R.id.btnSello5);
            btnSello6 = itemView.findViewById(R.id.btnSello6);
            btnSello7 = itemView.findViewById(R.id.btnSello7);
            btnSello8 = itemView.findViewById(R.id.btnSello8);
            twNumCarnet = itemView.findViewById(R.id.twNumCarnet);

        }
    }
}
