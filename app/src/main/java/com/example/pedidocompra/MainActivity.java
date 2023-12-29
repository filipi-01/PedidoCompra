package com.example.pedidocompra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.Manifest;

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
    private CheckBox cbTodos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listPedidos = findViewById(R.id.listPedidos);
        searchView = findViewById(R.id.searchView);
        cbTodos = findViewById(R.id.cbTodos);
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

        cbTodos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                listarDados();
            }
        });

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.

        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.CAMERA)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected, and what
            // features are disabled if it's declined. In this UI, include a
            // "cancel" or "no thanks" button that lets the user continue
            // using your app without granting the permission.

        } else {
            // You can directly ask for the permission.
            requestPermissions(/*getApplicationContext(),*/
                    new String[] { Manifest.permission.CAMERA },10001);
        }
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
                    "loja varchar(100)," +
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
                    "foto blob," +
                    "cod_produto int," +
                    "cod_pedido Integer not null,"+
                    "FOREIGN KEY(cod_pedido) REFERENCES artist(cod_pedido))"
            );
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS conexao(" +
                            "ip varchar(100)," +
                            "port integer," +
                            "db varchar(100)," +
                            "usuario varchar(100)," +
                            "senha varchar(100)," +
                            "item int," +
                            "ativo int);");
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS produto(" +
                    "cod integer primary key autoincrement ," +
                    "marca varchar(100)," +
                    "ref varchar(100)," +
                    "cor varchar(100)," +
                    "id int);");

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
              Cursor cursor = null;
           if (cbTodos.isChecked()) {
                cursor = bancoDados.rawQuery("select cod_pedido,fornecedor,data_entrega,transmitido from pedido where fornecedor like ?  " +
                       " order by cod_pedido desc", new String[]{"%" + fornecedor + "%"});
           }else{
               cursor = bancoDados.rawQuery("select cod_pedido,fornecedor,data_entrega,transmitido from pedido where fornecedor like ? and transmitido = 0 " +
                       " order by cod_pedido desc", new String[]{"%" + fornecedor + "%"});
           }
            cursor.moveToFirst();
            Pedidos p;
            while (cursor != null){

                p = new Pedidos(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
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
            Cursor cursor;
            try {
                cursor = bancoDados.rawQuery("select * from produto where id is null ", null);
                cursor.moveToFirst();
                while(cursor!=null){
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
                        SQLiteStatement stmt = bancoDados.compileStatement(sqlProd);
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


            cursor = bancoDados.rawQuery("select * from pedido where transmitido = 0 ",null);
            cursor.moveToFirst();
            while (cursor!=null){
                cursor.getString(0);
                try {
                    conn.setAutoCommit(false);
                    PreparedStatement stPedido;

                    String sqlPedido = ("delete from formas_pagto_pedido where cod_pedido_tools = ?");
                    stPedido = conn.prepareStatement(sqlPedido);
                    stPedido.setString(1, cursor.getString(8));
                    stPedido.executeUpdate();

                    sqlPedido = ("delete from item_pedido_compra_app where cod_pedido_tools = ?");
                    stPedido = conn.prepareStatement(sqlPedido);
                    stPedido.setString(1, cursor.getString(8));
                    stPedido.executeUpdate();

                    sqlPedido = ("delete from pedido_compra_app where cod_pedido_tools = ?");
                    stPedido = conn.prepareStatement(sqlPedido);
                    stPedido.setString(1, cursor.getString(8));
                    stPedido.executeUpdate();

                    sqlPedido = ("INSERT INTO pedido_compra_app(cod_pedido,fornecedor,data_entrega,prazo_pagto,desconto,obs,status_pedido," +
                            "data_pedido,loja) values(?,?,?,?,?,?,?,GETDATE(),?) ");


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
                    stPedido.setString(8, cursor.getString(6));

                    stPedido.executeUpdate();
                    sqlPedido = "SELECT max(cod_pedido_tools) cod from pedido_compra_app";
                    stPedido = conn.prepareStatement(sqlPedido);
                    ResultSet rs = stPedido.executeQuery();
                    if (rs.next()) {
                        codPedidoTools = rs.getInt(1);
                    }
                    try {
                        Cursor cursorItem = bancoDados.rawQuery(
                                "select i.cod_item,i.marca,i.tipo,i.ref,i.cor,i.custo_liq,i.custo,i.preco_venda,i.qtd,grade,i.foto,p.id,i.cod_pedido from item_pedido i " +
                                " left join produto p on p.cod=i.cod_produto "+
                                " where  cod_pedido = ?", new String[]{cursor.getString(0)});
                        cursorItem.moveToFirst();
                        while (cursorItem != null) {
                            String sqlItem = "INSERT INTO item_pedido_compra_app(cod_item,marca,tipo,ref,cor,custo_liq,preco_venda,qtd,grade," +
                                    "foto, cod_pedido_compra,cod_pedido_tools,custo,id_produto) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
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
                            if (!cursorItem.getBlob(10).toString().equals("") && cursorItem.getBlob(10) != null) {

                                stItem.setBytes(10, cursorItem.getBlob(10));
                            } else {
                                stItem.setString(10, cursorItem.getString(10));
                            }

                            stItem.setString(11, cursorItem.getString(12));
                            stItem.setInt(12, codPedidoTools);
                            stItem.setString(13, cursorItem.getString(6));
                            stItem.setString(14, cursorItem.getString(11));

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
            listarDados();
        }catch(Exception e){
            e.printStackTrace();
            listarDados();
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
        String Ip = "",Porta="",Db="",Usuario="",Senha="",Item="";
        try {

            conexao = new SQLConnection();


            Cursor cursor = bancoDados.rawQuery("select * from conexao where ativo = 1", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                Ip = cursor.getString(0);
                Porta = cursor.getString(1);
                Db = cursor.getString(2);
                Usuario = cursor.getString(3);
                Senha = cursor.getString(4);
                Item = cursor.getString(5);

                    conexao.setIp(cursor.getString(0));
                    conexao.setPort(cursor.getString(1));
                    conexao.setDb(cursor.getString(2));
                    conexao.setUn(cursor.getString(3));
                    conexao.setPassword(cursor.getString(4));


                } else {
                String sql = "INSERT INTO conexao(ip,port,db,usuario,senha,item,ativo) values(?,?,?,?,?,1,1)";
                SQLiteStatement stmt = bancoDados.compileStatement(sql);

                Ip = conexao.getIp();
                Porta = conexao.getPort();
                Db = conexao.getDb();
                Usuario = conexao.getUn();
                Senha = conexao.getPassword();
                Item = "1";


                stmt.bindString(1, conexao.getIp());
                stmt.bindString(2, conexao.getPort());
                stmt.bindString(3, conexao.getDb());
                stmt.bindString(4, conexao.getUn());
                stmt.bindString(5, conexao.getPassword());
                stmt.executeInsert();

                sql = "INSERT INTO conexao(ip,port,db,usuario,senha,item,ativo) values(?,?,?,?,?,2,0)";
                stmt = bancoDados.compileStatement(sql);



                stmt.bindString(1, "sys.shoemix.com.br");
                stmt.bindString(2, "15120");
                stmt.bindString(3, "FINANCEIRO");
                stmt.bindString(4, "sa");
                stmt.bindString(5, "S3rv3r");
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
                Intent intent = new Intent(this,conexao.class);
                intent.putExtra("IP",Ip);
                intent.putExtra("Porta",Porta);
                intent.putExtra("Bd",Db);
                intent.putExtra("Usuario",Usuario);
                intent.putExtra("Senha",Senha);
                intent.putExtra("item",Item);
                startActivity(intent);

            }
        }
    }
    public void ConfiguraConexao(View view){
        conexao = new SQLConnection();
        String Ip = "",Porta="",Db="",Usuario="",Senha="",Item="";
        Cursor cursor = bancoDados.rawQuery("select * from conexao where ativo = 1", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            Ip = cursor.getString(0);
            Porta = cursor.getString(1);
            Db = cursor.getString(2);
            Usuario = cursor.getString(3);
            Senha = cursor.getString(4);
            Item = cursor.getString(5);
            //Ativo = cursor.getString(6);




        } else {
            String sql = "INSERT INTO conexao(ip,port,db,usuario,senha,item,ativo) values(?,?,?,?,?,1,1)";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);

            Ip = conexao.getIp();
            Porta = conexao.getPort();
            Db = conexao.getDb();
            Usuario = conexao.getUn();
            Senha = conexao.getPassword();
            Item = "1";


            stmt.bindString(1, conexao.getIp());
            stmt.bindString(2, conexao.getPort());
            stmt.bindString(3, conexao.getDb());
            stmt.bindString(4, conexao.getUn());
            stmt.bindString(5, conexao.getPassword());
            stmt.executeInsert();

             sql = "INSERT INTO conexao(ip,port,db,usuario,senha,item,ativo) values(?,?,?,?,?,2,0)";
             stmt = bancoDados.compileStatement(sql);



            stmt.bindString(1, "sys.shoemix.com.br");
            stmt.bindString(2, "15120");
            stmt.bindString(3, "FINANCEIRO");
            stmt.bindString(4, "sa");
            stmt.bindString(5, "S3rv3r");
            stmt.executeInsert();
        }
        Intent intent = new Intent(this,conexao.class);
        intent.putExtra("IP",Ip);
        intent.putExtra("Porta",Porta);
        intent.putExtra("Bd",Db);
        intent.putExtra("Usuario",Usuario);
        intent.putExtra("Senha",Senha);
        intent.putExtra("item",Item);
        startActivity(intent);
    }

}