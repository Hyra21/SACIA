package com.alucintech.saci;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity {

    TextView text, errorText;
    Button show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text=(TextView) findViewById(R.id.textTest);
        errorText=(TextView) findViewById(R.id.textError);
        show=(Button) findViewById(R.id.buttonTest);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Task().execute();
            }
        });
    }

    class Task extends AsyncTask<Void, Void, Void> {
        String records="", error="";

        @Override
        protected Void doInBackground(Void... voids){
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://192.168.1.69:3307/sacibd","HYRA99","root");
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM actividad;");

                while(resultSet.next()){
                    records += resultSet.getString(1) + " " + resultSet.getString(2) + "\n";
                }
            } catch (Exception e){
                error = e.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            text.setText(records);
            if(error !="")
                errorText.setText(error);
            super.onPostExecute(aVoid);
        }
    }
}