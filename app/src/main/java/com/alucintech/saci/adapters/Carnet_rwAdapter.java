package com.alucintech.saci.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.alucintech.saci.objects.Carnet;
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

    // Aqui se asignan todos los valores a los componenetes del layout que se esta reutilizando
    @Override
    public void onBindViewHolder(@NonNull Carnet_rwAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        int pos = position + 1;
        holder.twNumCarnet.setText("Carnet #"+ pos);


        if(carnets.get(position).getEstadoCarnet().equals("En Proceso")){
            holder.imageView.setImageResource(R.drawable.enproceso_carnet);
        }

        if(carnets.get(position).getEstadoCarnet().equals("Completado")){
            holder.imageView.setImageResource(R.drawable.completado_carnet);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Carnet carnet = carnets.get(position);
                guardarPreferenciasCarnet(carnet.getNumFolio(),carnet.getCicloEscolarCarnet(),carnet.getFechaCreacionCarnet(),carnet.getClaveCarnet());
                NavController navController = Navigation.findNavController((Activity) context, R.id.nav_host_fragment);
                navController.navigate(R.id.action_consultaCarnet_to_informacionCarnetFragment);
            }
        });

        //Aqui tambien iran los botonos

    }

    // Este simplemente nos regresa la cantidad de carnets que tiene el alumno
    @Override
    public int getItemCount() {
        return carnets.size();
    }

    // Esta clase nos permite obtener todos los componentes del layout carnet.xml y asi poder reutilizarlos y asignarle los datos de cada carnet
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        CardView cardView;
        Button btnSello1, btnSello2, btnSello3, btnSello4, btnSello5, btnSello6, btnSello7, btnSello8;
        TextView twNumCarnet;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.EstadoCarnet);
            cardView = itemView.findViewById(R.id.cwCarnet);
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

    //Metodo para guardar informacion del carnet seleccionado
    public void guardarPreferenciasCarnet(int folio, String cilo, String fecha, int clave){
        SharedPreferences preferences = context.getSharedPreferences("carnetTemp",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("numFolio",folio);
        editor.putString("ciclo",cilo);
        editor.putString("fecha",fecha);
        editor.putInt("clave",clave);

        editor.commit();

    }
}
