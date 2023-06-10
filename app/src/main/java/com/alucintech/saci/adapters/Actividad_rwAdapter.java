package com.alucintech.saci.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.alucintech.saci.R;
import com.alucintech.saci.objects.Actividades;

import java.util.ArrayList;
import java.util.Base64;

public class Actividad_rwAdapter extends RecyclerView.Adapter<Actividad_rwAdapter.MyViewHolder>{

    Context context;
    ArrayList<Actividades> actividades;

    public Actividad_rwAdapter(Context context, ArrayList<Actividades> actividades) {
        this.context = context;
        this.actividades = actividades;
    }

    @NonNull
    @Override
    public Actividad_rwAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.actividad, parent, false);
        return new Actividad_rwAdapter.MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Actividad_rwAdapter.MyViewHolder holder, int position) {

        holder.mtwNombreActividad.setText(actividades.get(position).getNombreActividad());

        byte[] imagenDecodificada = android.util.Base64.decode(actividades.get(position).getImagenActividad(), android.util.Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagenDecodificada, 0, imagenDecodificada.length);
        holder.imwActividad.setImageBitmap(bitmap);

        holder.twTipoActividad.setText(actividades.get(position).getTipoActividad());
        holder.twFechaActividad.setText(actividades.get(position).getFechaActividad());
        holder.twHorarioActividad.setText(actividades.get(position).getHorarioInicio() + " - " + actividades.get(position).getHorarioFin());
        holder.twModalidadActividad.setText(actividades.get(position).getModalidadActividad());


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        CardView cwActividad;
        MultiAutoCompleteTextView mtwNombreActividad;
        ImageView imwActividad;
        TextView twTipoActividad, twFechaActividad, twHorarioActividad, twModalidadActividad;
        ImageButton imgbtLink;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cwActividad = itemView.findViewById(R.id.cwActividad);
            mtwNombreActividad = itemView.findViewById(R.id.mtwNombreActividad);
            imwActividad = itemView.findViewById(R.id.imwActividad);
            twTipoActividad = itemView.findViewById(R.id.twTipoActividad);
            twFechaActividad = itemView.findViewById(R.id.twHorarioActividad);
            twHorarioActividad = itemView.findViewById(R.id.twHorarioActividad);
            twModalidadActividad = itemView.findViewById(R.id.twModalidad);
            imgbtLink = itemView.findViewById(R.id.imgbtLink);

        }


    }
}
