package com.example.pedidocompra;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import  com.example.pedidocompra.MainActivity;

public class conexao extends AppCompatActivity {

    private EditText edIp,edPorta,edBd,edUsuario,edSenha;
    Spinner spConexao;
    private String Item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexao);
        edIp = findViewById(R.id.edIp);
        edPorta = findViewById(R.id.edPorta);
        edBd = findViewById(R.id.edBd);
        edUsuario = findViewById(R.id.edUsuario);
        edSenha = findViewById(R.id.edSenha);
        spConexao = findViewById(R.id.spConexao);
        Bundle extras = getIntent().getExtras();

        edIp.setText(extras.getString("IP"));
        edPorta.setText(extras.getString("Porta"));
        edBd.setText(extras.getString("Bd"));
        edUsuario.setText(extras.getString("Usuario"));
        edSenha.setText(extras.getString("Senha"));


        Item = extras.getString("item");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lista_conexao, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        spConexao.setAdapter(adapter);

        if (extras.getString("item").equals("1")){
            spConexao.setSelection(0);
        }else{
            spConexao.setSelection(1);
        }
        spConexao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spConexao.getSelectedItem().toString().equals("Local")){
                    Cursor cursor = MainActivity.bancoDados.rawQuery("select * from conexao where item = 1", null);
                    cursor.moveToFirst();
                    if (cursor.getCount() > 0) {
                        edIp.setText(cursor.getString(0));
                        edPorta.setText( cursor.getString(1));
                        edBd.setText(cursor.getString(2));
                        edUsuario.setText(cursor.getString(3));
                        edSenha.setText(cursor.getString(4));
                        Item = cursor.getString(5);


                    }
                }else{
                    Cursor cursor = MainActivity.bancoDados.rawQuery("select * from conexao where item = 2", null);
                    cursor.moveToFirst();
                    if (cursor.getCount() > 0) {
                        edIp.setText(cursor.getString(0));
                        edPorta.setText( cursor.getString(1));
                        edBd.setText(cursor.getString(2));
                        edUsuario.setText(cursor.getString(3));
                        edSenha.setText(cursor.getString(4));
                        Item = cursor.getString(5);


                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

      /*  spConexao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (spConexao.getSelectedItem().toString().equals("Local")){
                    Cursor cursor = MainActivity.bancoDados.rawQuery("select * from conexao where item = 1", null);
                    cursor.moveToFirst();
                    if (cursor.getCount() > 0) {
                        edIp.setText(cursor.getString(0));
                        edPorta.setText( cursor.getString(1));
                        edBd.setText(cursor.getString(2));
                        edUsuario.setText(cursor.getString(3));
                        edSenha.setText(cursor.getString(4));
                        Item = cursor.getString(5);


                    }
                }else{
                    Cursor cursor = MainActivity.bancoDados.rawQuery("select * from conexao where item = 2", null);
                    cursor.moveToFirst();
                    if (cursor.getCount() > 0) {
                        edIp.setText(cursor.getString(0));
                        edPorta.setText( cursor.getString(1));
                        edBd.setText(cursor.getString(2));
                        edUsuario.setText(cursor.getString(3));
                        edSenha.setText(cursor.getString(4));
                        Item = cursor.getString(5);


                    }
                }
            }
        });*/



    }

    public void salvarConexao(View view){
        try{
            String sql;

            /* sql= "delete from conexao";
            SQLiteStatement stmt = MainActivity.bancoDados.compileStatement(sql);
            stmt.executeUpdateDelete();*/

            sql= "UPDATE conexao set ip=?,port=?,db=?,usuario=?,senha=?,ativo = 1 where item= ?";
            SQLiteStatement stmt = MainActivity.bancoDados.compileStatement(sql);

            stmt.bindString(1,edIp.getText().toString());
            stmt.bindString(2,edPorta.getText().toString());
            stmt.bindString(3,edBd.getText().toString());
            stmt.bindString(4,edUsuario.getText().toString());
            stmt.bindString(5,edSenha.getText().toString());
            stmt.bindString(6,Item);
            stmt.executeUpdateDelete();

            sql= "UPDATE conexao set ativo = 0 where item <> ?";
            stmt = MainActivity.bancoDados.compileStatement(sql);
            stmt.bindString(1,Item);
            stmt.executeUpdateDelete();

            finish();
        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }
}