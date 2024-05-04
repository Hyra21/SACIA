package com.alucintech.saci.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.alucintech.saci.R;
import com.alucintech.saci.objects.Eventos;

import java.util.ArrayList;

public class Evento_rwAdapter extends RecyclerView.Adapter<Evento_rwAdapter.MyViewHolder>{

    Context context;
    ArrayList<Eventos> eventos;

    public Evento_rwAdapter(Context context, ArrayList<Eventos> eventos) {
        this.context = context;
        this.eventos = eventos;
    }

    @NonNull
    @Override
    public Evento_rwAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.evento, parent, false);
        return new Evento_rwAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Evento_rwAdapter.MyViewHolder holder,@SuppressLint("RecyclerView") int position) {
        int pos = position;

        holder.mtwNombreEvento.setText(eventos.get(position).getNombreEvento());

        byte[] imagenDecodificada = android.util.Base64.decode(eventos.get(position).getImagenEvento(), android.util.Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagenDecodificada, 0, imagenDecodificada.length);
        holder.imwEvento.setImageBitmap(bitmap);

        holder.twFechaInicioEvento.setText(eventos.get(position).getFechaInicioEvento());
        holder.twFechaFinEvento.setText(eventos.get(position).getFechaFinEvento());
        holder.twDescripcion.setText(eventos.get(position).getDescripcionEvento());
        System.out.println("PPPPPOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO"+holder.twDescripcion.getText().toString());
        holder.cwEvento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarPreferenciasEvento(pos);
                NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment);
                navController.navigate(R.id.action_consultaEventosFragment_to_consultaActividades);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.e("CantidadEventos", String.valueOf(eventos.size()));
        return eventos.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cwEvento;
        MultiAutoCompleteTextView mtwNombreEvento;
        ImageView imwEvento;
        TextView twFechaInicioEvento,twFechaFinEvento, twDescripcion;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cwEvento = itemView.findViewById(R.id.cwEvento);
            mtwNombreEvento = itemView.findViewById(R.id.mtwNombreEvento);
            imwEvento = itemView.findViewById(R.id.imwEvento);
            twFechaInicioEvento= itemView.findViewById(R.id.twFechaInicioEvento);
            twFechaFinEvento= itemView.findViewById(R.id.twFechaFinEvento);
            twDescripcion = itemView.findViewById(R.id.twDescripcion);
        }
    }
    //Metodo para guardar informacion del evento seleccionado
    public void guardarPreferenciasEvento(int pos){
        SharedPreferences preferences = context.getSharedPreferences("eventoTemp",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("nombreEvento", eventos.get(pos).getNombreEvento());
        editor.putString("descripcionEvento", eventos.get(pos).getDescripcionEvento());
        editor.putString("fechaInicioEvento", eventos.get(pos).getFechaInicioEvento());
        editor.putString("fechaFinEvento", eventos.get(pos).getFechaFinEvento());
        editor.putString("imagen", eventos.get(pos).getImagenEvento());
        editor.putInt("idEvento", eventos.get(pos).getIdEvento());

        editor.commit();
    }
}
