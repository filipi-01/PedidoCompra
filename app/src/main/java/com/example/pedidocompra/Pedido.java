package com.example.pedidocompra;



import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.BundleCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.util.ArrayList;


public class Pedido extends AppCompatActivity {
    public static SQLiteDatabase bancoDados;
    private Spinner spQuinzena;
    private EditText edFornecedor;
    private EditText edDate;
    private EditText edPrazo;
    private EditText edDesconto;
    private EditText edObs;
    private EditText edLoja;
    private  EditText edWhats;
    private ListView listItem;

    private  long newId;
    private int editar;
    private String codPedido,fornecedor,entrega,prazo,desconto,obs,loja;
    public  ArrayAdapter adapterList;
    public static String stDesconto;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        spQuinzena =(Spinner) findViewById(R.id.spQuinzena);
        edFornecedor=(EditText) findViewById(R.id.edFornecedor);
        edDate=(EditText) findViewById(R.id.edDate);
        edPrazo=(EditText) findViewById(R.id.edPrazo);
        edDesconto=(EditText) findViewById(R.id.edDesconto);
        edObs=(EditText) findViewById(R.id.edObs);
        edLoja=findViewById(R.id.edLoja);
        edWhats=findViewById(R.id.edWhats);
        listItem = findViewById(R.id.listItens);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lista_quizena, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spQuinzena.setAdapter(adapter);
        stDesconto = "";
        Bundle Extras = getIntent().getExtras();
        editar=Extras.getInt("editar");
        if (editar == 1){
            codPedido = Extras.getString("codPedido");
            fornecedor =Extras.getString("fornecedor");
            entrega = Extras.getString("entrega");
            prazo = Extras.getString("prazo");
            desconto = Extras.getString("desconto");
            obs = Extras.getString("obs");
            loja =Extras.getString("loja") ;
            stDesconto = desconto;

            edFornecedor.setText(fornecedor);
            if (!entrega.equals("")) {
                if (entrega.substring(0, 2).equals("01")) {
                    spQuinzena.setSelection(0);
                } else {
                    spQuinzena.setSelection(1);
                }
                edDate.setText(entrega.substring(3, 10));
            }
            edPrazo.setText(prazo);
            edDesconto.setText(desconto);
            edObs.setText(obs);
            edLoja.setText(loja);
            edWhats.setText(Extras.getString("whats"));

            ListarItens();
        }
        edDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (!edDate.getText().toString().equals("")) {
                        if (!edDate.getText().toString().contains("/")) {
                            String mes = "";
                            String ano = "";

                            mes = edDate.getText().toString().substring(0, 2);
                            ano = edDate.getText().toString().substring(2, 6);

                            edDate.setText(mes+"/"+ano);

                        }
                    }
                }
            }
        });

        bancoDados = openOrCreateDatabase("bdPedidos",MODE_PRIVATE,null);
        bancoDados.beginTransaction();

    }
    public void ItemPedido(View view){

        if (!edFornecedor.getText().toString().equals("") && !spQuinzena.getSelectedItem().toString().equals("")
        && !edDate.getText().toString().equals("") && !edPrazo.getText().toString().equals("")
        && !edDesconto.getText().toString().equals("") && !edLoja.getText().toString().equals("")){
            Intent intent = new Intent(this, ItemPedido.class);
            if (editar == 1) {

                intent.putExtra("idPedido",codPedido);
            }
            else{
                InserirPedido();
                intent.putExtra("idPedido",codPedido);
            }
            intent.putExtra("editar",0);
            intent.putExtra("desconto",edDesconto.getText().toString());



            startActivity(intent);
        }else {
            Toast.makeText(Pedido.this, "Preencha todos os dados", Toast.LENGTH_LONG).show();
        }
    }

    public void InserirPedido(){
        try {
            String dataEntrega;
            dataEntrega = "";
            String sql= "INSERT INTO pedido(fornecedor,data_entrega,prazo_pagto,desconto,obs,loja,transmitido,whats_rep) values(?,?,?,?,?,?,0,?)";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);

            stmt.bindString(1,edFornecedor.getText().toString().trim());
            if (spQuinzena.getSelectedItem().toString()=="1ª Quinzena" || spQuinzena.getSelectedItem().toString().equals("1ª Quinzena") ) {
                dataEntrega = "10/"+edDate.getText().toString();
            } else if (spQuinzena.getSelectedItem().toString()=="2ª Quinzena"|| spQuinzena.getSelectedItem().toString().equals("2ª Quinzena")) {
                dataEntrega = "20/"+edDate.getText().toString();
            }
            stmt.bindString(2,dataEntrega);
            stmt.bindString(3,edPrazo.getText().toString());
            stmt.bindString(4,edDesconto.getText().toString());

            stmt.bindString(5,edObs.getText().toString().trim());
            stmt.bindString(6,edLoja.getText().toString());
            stmt.bindString(7,edWhats.getText().toString());
            newId= stmt.executeInsert();
            codPedido = ""+newId;
            editar = 1;

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
    public void AlterarPedido(){
        try {
            String dataEntrega;
            dataEntrega = "";
            String sql= "UPDATE pedido set fornecedor=?,data_entrega=?,prazo_pagto=?,desconto=?,obs=?,loja=?,transmitido=0, whats_rep =?" +
                    " where cod_pedido = ?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);

            stmt.bindString(1,edFornecedor.getText().toString().trim());
            if (spQuinzena.getSelectedItem().toString()=="1ª Quinzena" || spQuinzena.getSelectedItem().toString().equals("1ª Quinzena") ) {
                dataEntrega = "10/"+edDate.getText().toString();
            } else if (spQuinzena.getSelectedItem().toString()=="2ª Quinzena"|| spQuinzena.getSelectedItem().toString().equals("2ª Quinzena")) {
                dataEntrega = "20/"+edDate.getText().toString();
            }
            stmt.bindString(2,dataEntrega);
            stmt.bindString(3,edPrazo.getText().toString());
            stmt.bindString(4,edDesconto.getText().toString());

            stmt.bindString(5,edObs.getText().toString().trim());
            stmt.bindString(6,edLoja.getText().toString());
            stmt.bindString(7,edWhats.getText().toString());
            stmt.bindString(8,codPedido);
            stmt.executeUpdateDelete();

            editar = 1;

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(),
                    Toast.LENGTH_LONG).show();

        }
    }
    public void voltar(View view){
        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Sair");
        msgBox.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        msgBox.setMessage("Deseja sair dessa tela sem salvar?");
        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bancoDados.endTransaction();
                finish();

            }
        });
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        msgBox.show();
    }
    public void Salvar(View view){
        AlterarPedido();
        bancoDados.setTransactionSuccessful();
        bancoDados.endTransaction();

        finish();
    }
    private  ArrayList<ItensPedido> addList(){
        ArrayList<ItensPedido> itenspedidos = new ArrayList<ItensPedido>();
        try{

            //bancoDados = openOrCreateDatabase("bdPedidos",MODE_PRIVATE,null);
            Cursor cursor = bancoDados.rawQuery("select * from item_pedido where cod_pedido = ?",new String[]{codPedido});
            cursor.moveToFirst();
            ItensPedido i;
            while (cursor != null){

                i = new ItensPedido(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getBlob(10),String.format("%.2f",cursor.getFloat(6)),cursor.getString(7),cursor.getString(8),cursor.getString(9));
                itenspedidos.add(i);
                cursor.moveToNext();
            }
        }catch(Exception e){
            e.printStackTrace();

        }

        return itenspedidos;
    }
    public void ListarItens(){
        try {

            adapterList = new ItensPedidoAdapter(this,addList());
            listItem.setAdapter(adapterList);
            adapterList.notifyDataSetChanged();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListarItens();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Sair");
        msgBox.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        msgBox.setMessage("Deseja sair dessa tela sem salvar?");
        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                bancoDados.endTransaction();
                finish();

            }
        });
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        msgBox.show();


    }
}