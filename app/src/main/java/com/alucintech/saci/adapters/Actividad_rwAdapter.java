package com.alucintech.saci.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
    public void onBindViewHolder(@NonNull Actividad_rwAdapter.MyViewHolder holder,@SuppressLint("RecyclerView") int position) {

        int pos = position;
        holder.mtwNombreActividad.setText(actividades.get(position).getNombreActividad());

        byte[] imagenDecodificada = android.util.Base64.decode(actividades.get(position).getImagenActividad(), android.util.Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagenDecodificada, 0, imagenDecodificada.length);
        holder.imwActividad.setImageBitmap(bitmap);

        holder.twTipoActividad.setText(actividades.get(position).getTipoActividad());
        holder.twFechaActividad.setText(actividades.get(position).getFechaActividad());
        holder.twHorarioActividad.setText(actividades.get(position).getHorarioInicio() + " - " + actividades.get(position).getHorarioFin());
        holder.twModalidadActividad.setText(actividades.get(position).getModalidadActividad());
        if(actividades.get(pos).getModalidadActividad().equals("Virtual")){
            holder.imgbtLink.setVisibility(View.VISIBLE);
            holder.imgbtLink.setClickable(true);
        }

        holder.imgbtLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(actividades.get(pos).getEnlaceVirtual()));
                context.startActivity(intent);
            }
        });
        holder.cwActividad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarPreferenciasActividad(pos);
                NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment);
                navController.navigate(R.id.action_consultaActividades_to_informacionActividadFragment);
            }
        });

    }

    @Override
    public int getItemCount() {
        return actividades.size();
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

    public void guardarPreferenciasActividad(int pos){
        SharedPreferences preferences = context.getSharedPreferences("actividadTemp",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("nombreActividad", actividades.get(pos).getNombreActividad());
        editor.putString("descripcionActividad", actividades.get(pos).getDescripcionActividad());
        editor.putString("tipoActividad", actividades.get(pos).getTipoActividad());
        editor.putString("fecha", actividades.get(pos).getFechaActividad());
        editor.putString("horarioInicio", actividades.get(pos).getHorarioInicio());
        editor.putString("horarioFin", actividades.get(pos).getHorarioFin());
        editor.putString("lugar", actividades.get(pos).getLugarActividad());
        editor.putInt("espacios", actividades.get(pos).getEspaciosDisponibles());
        editor.putString("modalidad", actividades.get(pos).getModalidadActividad());
        editor.putString("enlace", actividades.get(pos).getEnlaceVirtual());
        editor.putString("imagen", actividades.get(pos).getImagenActividad());
        editor.putString("ponente", actividades.get(pos).getPonenteActividad());
        editor.putInt("idEvento", actividades.get(pos).getIdEvento());
        editor.putInt("numEmpleado", actividades.get(pos).getNumEmpleadoAdministrador());
        editor.putString("estado", actividades.get(pos).getEstadoActividad());

        editor.commit();

    }
}
