package com.example.pedidocompra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static SQLiteDatabase bancoDados;
    private  ListView listPedidos;
    public  ArrayAdapter adapter;
    public SearchView searchView;

    private  SQLConnection conexao;
    private Connection conn;
    private String fornecedor = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listPedidos = findViewById(R.id.listPedidos);
        searchView = findViewById(R.id.searchView);
        criarBanco();
        listarDados();
      //  verificaConexao();
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {
                fornecedor = s;
                listarDados();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                fornecedor = s;
                listarDados();
                return false;
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        listarDados();
        //verificaConexao();
    }



    public void criarBanco(){
        try{
            bancoDados = openOrCreateDatabase("bdPedidos",MODE_PRIVATE,null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS pedido("+
                    "cod_pedido integer primary key autoincrement,"+
                    "fornecedor varchar(200) not null,"+
                    "data_entrega date,"+
                    "prazo_pagto varchar(100),"+
                    "desconto varchar(100),"+
                    "obs varchar(1000)," +
                    "transmitido int, " +
                    "cod_pedido_tools int);"
            );
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS item_pedido("+
                    "cod_item integer primary key autoincrement,"+
                    "marca varchar(100) not null,"+
                    "tipo varchar(100),"+
                    "ref varchar(100),"+
                    "cor varchar(100),"+
                    "custo_liq decimal(9,2)," +
                    "custo decimal(9,2),"+
                    "preco_venda decimal(9,2),"+
                    "qtd integer,"+
                    "grade Varchar(100),"+
                    "loja varchar(100),"+
                    "foto blob,"+
                    "cod_pedido Integer not null,"+
                    "FOREIGN KEY(cod_pedido) REFERENCES artist(cod_pedido))"
            );
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS conexao(" +
                            "ip varchar(100)," +
                            "port integer," +
                            "db varchar(100)," +
                            "usuario varchar(100)," +
                            "senha varchar(100));");

            bancoDados.close();



        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(),
                    Toast.LENGTH_LONG).show();
        }
    }
    private  ArrayList<Pedidos> addList(){
        ArrayList<Pedidos> pedidos = new ArrayList<Pedidos>();
          try{

            bancoDados = openOrCreateDatabase("bdPedidos",MODE_PRIVATE,null);
            Cursor cursor = bancoDados.rawQuery("select cod_pedido,fornecedor,data_entrega from pedido where fornecedor like ?  "+
                    " order by cod_pedido desc",new String[]{"%"+fornecedor+"%"});
            cursor.moveToFirst();
            Pedidos p;
            while (cursor != null){

                p = new Pedidos(cursor.getString(0),cursor.getString(1),cursor.getString(2));
                pedidos.add(p);
                cursor.moveToNext();
            }
            fornecedor="";
          }catch(Exception e){
              e.printStackTrace();

          }

        return pedidos;
    }
    public void Transmitir(View view){
        Integer codPedidoTools=0;
        try {
            verificaConexao();
            Cursor cursor = bancoDados.rawQuery("select * from pedido where transmitido = 0 ",null);
            cursor.moveToFirst();
            while (cursor!=null){
                cursor.getString(0);
                try {
                    conn.setAutoCommit(false);
                    PreparedStatement stPedido;

                    String sqlPedido = ("delete from formas_pagto_pedido where cod_pedido_tools = ?");
                    stPedido = conn.prepareStatement(sqlPedido);
                    stPedido.setString(1, cursor.getString(7));
                    stPedido.executeUpdate();

                    sqlPedido = ("delete from item_pedido_compra_app where cod_pedido_tools = ?");
                    stPedido = conn.prepareStatement(sqlPedido);
                    stPedido.setString(1, cursor.getString(7));
                    stPedido.executeUpdate();

                    sqlPedido = ("delete from pedido_compra_app where cod_pedido_tools = ?");
                    stPedido = conn.prepareStatement(sqlPedido);
                    stPedido.setString(1, cursor.getString(7));
                    stPedido.executeUpdate();

                    sqlPedido = ("INSERT INTO pedido_compra_app(cod_pedido,fornecedor,data_entrega,prazo_pagto,desconto,obs,status_pedido" +
                            ") values(?,?,?,?,?,?,?) ");


                    stPedido = conn.prepareStatement(sqlPedido);
                    stPedido.setString(1, cursor.getString(0));
                    stPedido.setString(2, cursor.getString(1));
                    String dataped = cursor.getString(2);
                    String dia, mes, ano;
                    dia = dataped.substring(0, 2);
                    mes = dataped.substring(3, 5);
                    ano = dataped.substring(dataped.length() - 4);
                    stPedido.setString(3, mes + "/" + dia + "/" + ano);
                    stPedido.setString(4, cursor.getString(3));
                    stPedido.setString(5, cursor.getString(4));
                    stPedido.setString(6, cursor.getString(5));
                    stPedido.setString(7, "NOVO");

                    stPedido.executeUpdate();
                    sqlPedido = "SELECT max(cod_pedido_tools) cod from pedido_compra_app";
                    stPedido = conn.prepareStatement(sqlPedido);
                    ResultSet rs = stPedido.executeQuery();
                    if (rs.next()) {
                        codPedidoTools = rs.getInt(1);
                    }
                    try {
                        Cursor cursorItem = bancoDados.rawQuery("select * from item_pedido where  cod_pedido = ?", new String[]{cursor.getString(0)});
                        cursorItem.moveToFirst();
                        while (cursorItem != null) {
                            String sqlItem = "INSERT INTO item_pedido_compra_app(cod_item,marca,tipo,ref,cor,custo_liq,preco_venda,qtd,grade,loja," +
                                    "foto, cod_pedido_compra,cod_pedido_tools,custo) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                            PreparedStatement stItem = conn.prepareStatement(sqlItem);

                            stItem.setString(1, cursorItem.getString(0));
                            stItem.setString(2, cursorItem.getString(1));
                            stItem.setString(3, cursorItem.getString(2));
                            stItem.setString(4, cursorItem.getString(3));
                            stItem.setString(5, cursorItem.getString(4));
                            stItem.setString(6, cursorItem.getString(5));
                            stItem.setString(7, cursorItem.getString(7));
                            stItem.setString(8, cursorItem.getString(8));
                            stItem.setString(9, cursorItem.getString(9));
                            stItem.setString(10, cursorItem.getString(10));
                            if (!cursorItem.getBlob(11).toString().equals("") && cursorItem.getBlob(11) != null) {

                                stItem.setBytes(11, cursorItem.getBlob(11));
                            } else {
                                stItem.setString(11, cursorItem.getString(11));
                            }

                            stItem.setString(12, cursorItem.getString(12));
                            stItem.setInt(13, codPedidoTools);
                            stItem.setString(14, cursorItem.getString(6));

                            stItem.executeUpdate();

                            cursorItem.moveToNext();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                    }
                    String sql = "UPDATE pedido set transmitido=1, cod_pedido_tools=?" +
                            " where cod_pedido = ?";
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);

                    stmt.bindString(1, codPedidoTools.toString());
                    stmt.bindString(2, cursor.getString(0));
                    stmt.executeUpdateDelete();

                    sqlPedido = "update pedido_compra_app set fornecedor = fornecedor where cod_pedido_tools = ?";
                    stPedido = conn.prepareStatement(sqlPedido);
                    stPedido.setString(1, codPedidoTools.toString());
                    stPedido.executeUpdate();
                    conn.commit();
                    Toast.makeText(this, "Pedido: " + cursor.getString(0) + " transmitido com sucesso", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    conn.rollback();
                }
                cursor.moveToNext();
            }
        }catch(Exception e){
            e.printStackTrace();
            //Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public  void listarDados(){

        try {
            adapter = new PedidosAdapter(this,addList());
            listPedidos.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }catch(Exception e){
            e.printStackTrace();

        }

    }

    public void Pedido(View view){
        Intent intent = new Intent(this, Pedido.class);
        intent.putExtra("editar",0);
        startActivity(intent);
    }
    public void verificaConexao(){
        String Ip = "",Porta="",Db="",Usuario="",Senha="";
        try {

            conexao = new SQLConnection();


                Cursor cursor = bancoDados.rawQuery("select * from conexao", null);
                cursor.moveToFirst();
                if (cursor.getCount() > 0) {
                    Ip = cursor.getString(0);
                    Porta = cursor.getString(1);
                    Db = cursor.getString(2);
                    Usuario = cursor.getString(3);
                    Senha = cursor.getString(4);

                    conexao.setIp(cursor.getString(0));
                    conexao.setPort(cursor.getString(1));
                    conexao.setDb(cursor.getString(2));
                    conexao.setUn(cursor.getString(3));
                    conexao.setPassword(cursor.getString(4));


                } else {
                    String sql = "INSERT INTO conexao(ip,port,db,usuario,senha) values(?,?,?,?,?)";
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);

                    Ip = conexao.getIp();
                    Porta = conexao.getPort();
                    Db = conexao.getDb();
                    Usuario = conexao.getUn();
                    Senha = conexao.getPassword();


                    stmt.bindString(1, conexao.getIp());
                    stmt.bindString(2, conexao.getPort());
                    stmt.bindString(3, conexao.getDb());
                    stmt.bindString(4, conexao.getUn());
                    stmt.bindString(5, conexao.getPassword());
                    stmt.executeInsert();
                }

                conn = conexao.connect();
                if (!conexao.getLOG().equals("")) {
                    throw new Exception("erro ao conectar no banco de dados:");
                }

        }catch(Exception e){
            e.printStackTrace();
           // Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            if (e.getMessage().equals("erro ao conectar no banco de dados:")){
                Intent intent = new Intent(this,com.example.pedidocompra.conexao.class);
                intent.putExtra("IP",Ip);
                intent.putExtra("Porta",Porta);
                intent.putExtra("Bd",Db);
                intent.putExtra("Usuario",Usuario);
                intent.putExtra("Senha",Senha);
                startActivity(intent);

            }
        }
    }

}