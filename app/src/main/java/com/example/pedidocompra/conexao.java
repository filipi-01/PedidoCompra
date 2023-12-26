package com.example.pedidocompra;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import  com.example.pedidocompra.MainActivity;

public class conexao extends AppCompatActivity {

    private EditText edIp,edPorta,edBd,edUsuario,edSenha;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexao);
        edIp = findViewById(R.id.edIp);
        edPorta = findViewById(R.id.edPorta);
        edBd = findViewById(R.id.edBd);
        edUsuario = findViewById(R.id.edUsuario);
        edSenha = findViewById(R.id.edSenha);
        Bundle extras = getIntent().getExtras();

        edIp.setText(extras.getString("IP"));
        edPorta.setText(extras.getString("Porta"));
        edBd.setText(extras.getString("Bd"));
        edUsuario.setText(extras.getString("Usuario"));
        edSenha.setText(extras.getString("Senha"));

    }
    public void salvarConexao(View view){
        try{
            String sql;

            sql= "delete from conexao";
            SQLiteStatement stmt = MainActivity.bancoDados.compileStatement(sql);
            stmt.executeUpdateDelete();

            sql= "INSERT INTO conexao(ip,port,db,usuario,senha) values(?,?,?,?,?)";
            stmt = MainActivity.bancoDados.compileStatement(sql);

            stmt.bindString(1,edIp.getText().toString());
            stmt.bindString(2,edPorta.getText().toString());
            stmt.bindString(3,edBd.getText().toString());
            stmt.bindString(4,edUsuario.getText().toString());
            stmt.bindString(5,edSenha.getText().toString());
            stmt.executeInsert();
            finish();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }
}