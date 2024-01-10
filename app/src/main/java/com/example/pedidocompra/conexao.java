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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class conexao extends AppCompatActivity {

    private EditText edIp,edPorta,edBd,edUsuario,edSenha;
    Spinner spConexao;
    private String Item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexao);
        //Amarra componentens do xml na classe
        edIp = findViewById(R.id.edIp);
        edPorta = findViewById(R.id.edPorta);
        edBd = findViewById(R.id.edBd);
        edUsuario = findViewById(R.id.edUsuario);
        edSenha = findViewById(R.id.edSenha);
        spConexao = findViewById(R.id.spConexao);

        //puxa informações da tela anterior
        Bundle extras = getIntent().getExtras();
        edIp.setText(extras.getString("IP"));
        edPorta.setText(extras.getString("Porta"));
        edBd.setText(extras.getString("Bd"));
        edUsuario.setText(extras.getString("Usuario"));
        edSenha.setText(extras.getString("Senha"));
        Item = extras.getString("item");

        //prepara lista do spinner(combobox)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lista_conexao, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        spConexao.setAdapter(adapter);
        //verificar o que esta selecionado no spinner
        if (extras.getString("item").equals("1")){
            //conexao local
            spConexao.setSelection(0);
        }else{
            //conexao externa
            spConexao.setSelection(1);
        }
        //evento de seleção do spinner
        spConexao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //verifica dados da conexao local
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
                //verifica dados da conexao externa
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




    }
    public void sincronizarProduto(View view){
        try {
            //conecta ao banco de dados externo
            SQLConnection conexao = new SQLConnection();

            conexao.setIp(edIp.getText().toString());
            conexao.setPort(edPorta.getText().toString());
            conexao.setDb(edBd.getText().toString());
            conexao.setUn(edUsuario.getText().toString());
            conexao.setPassword(edSenha.getText().toString());

            Connection conn = conexao.connect();

            //verifica produtos que estao com campo id vazios no banco de dados local
            Cursor cursor = MainActivity.bancoDados.rawQuery("select * from produto where id is null ", null);
            cursor.moveToFirst();
            while(cursor!=null){
                //busca o id do produto no banco de dados externo e insere no banco local
                PreparedStatement stProduto;
                String sqlProduto = "SELECT max(id) id from produto_app where marca= ? and ref = ? and cor = ?  ";
                stProduto = conn.prepareStatement(sqlProduto);
                stProduto.setString(1,cursor.getString(1));
                stProduto.setString(2,cursor.getString(2));
                stProduto.setString(3,cursor.getString(3));
                ResultSet rs = stProduto.executeQuery();
                if (rs.next()) {
                    String sqlProd = "UPDATE produto set id=? " +
                            " where cod = ?";
                    SQLiteStatement stmt = MainActivity.bancoDados.compileStatement(sqlProd);
                    if  (rs.getString(1) != null) {
                        stmt.bindString(1, rs.getString(1));
                        stmt.bindString(2, cursor.getString(0));
                        stmt.executeUpdateDelete();
                    }
                }

                cursor.moveToNext();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        Toast.makeText(this, "Sincronizado!", Toast.LENGTH_LONG).show();
    }

    public void salvarConexao(View view){
        try{
            String sql;

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