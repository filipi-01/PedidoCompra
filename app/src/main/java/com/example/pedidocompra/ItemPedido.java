package com.example.pedidocompra;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import com.bumptech.glide.Glide;

public class ItemPedido extends AppCompatActivity {
    ImageView imFoto;
    ImageButton btFoto;
    private EditText edMarca;
    private EditText edTipo;
    private EditText edRef;
    private EditText edCor;
    private EditText edCusto;
    private EditText edVenda;
    private EditText edQtd;
    private EditText edGrade,edLiquido;
    private Bitmap bitmap;
    private SQLiteDatabase bancoDados;
    private int codItem;
    private int editar;
    private SearchView scMarca,scRef,scCor;
    private LinearLayout ltMarca,ltRef,ltCor;
    private ListView listMarca,listRef,listCor,listGrade;
    private String descontos;

    private String idPedido,marca;
    ArrayList<String> itenslist;
    ArrayList<String> arrayitens=new ArrayList<String>();
    private String codProduto,idProduto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_pedido);
        imFoto = findViewById(R.id.imFoto);
        btFoto = findViewById(R.id.btFoto);
        Bundle extras = getIntent().getExtras();


        edMarca = findViewById(R.id.edMarca);
        edTipo = findViewById(R.id.edTipo);
        edRef = findViewById(R.id.edRef);
        edCor = findViewById(R.id.edCor);
        edCusto = findViewById(R.id.edCustoLiq);
        edVenda = findViewById(R.id.edVenda);
        edQtd= findViewById(R.id.edQtd);
        edGrade = findViewById(R.id.edGrade);
        scMarca = findViewById(R.id.scMarca);
        scRef = findViewById(R.id.scRef);
        scCor = findViewById(R.id.scCor);
        ltMarca =findViewById(R.id.ltMarca);
        ltRef =findViewById(R.id.ltRef);
        ltCor =findViewById(R.id.ltCor);
        listMarca = findViewById(R.id.listMarca);
        listRef = findViewById(R.id.listRef);
        listCor = findViewById(R.id.listCor);
        listGrade = findViewById(R.id.listGrade);
        edLiquido = findViewById(R.id.edLiquido);

        idPedido = extras.getString("idPedido");
        editar = extras.getInt("editar");
        if (editar ==1) {
            codItem = extras.getInt("codItem");
            edMarca.setText(extras.getString("marca"));
            edTipo.setText(extras.getString("tipo"));
            edRef.setText(extras.getString("ref"));
            edCor.setText(extras.getString("cor"));
            edCusto.setText(extras.getString("custo"));
            edLiquido.setText(extras.getString("custoLiq"));
            edVenda.setText(extras.getString("venda"));
            edQtd.setText(extras.getString("qtd"));
            edGrade.setText(extras.getString("grade"));

            if (extras.getByteArray("foto").length > 1) {
                Glide.with(this).asBitmap().load(extras.getByteArray("foto")).into(imFoto);
            }
        }
        descontos=extras.getString("desconto");
        //System.out.println(descontos.substring(descontos.indexOf('+')+1,descontos.length()));
        btFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                activityResultLauncher.launch(intent);

            }
        });
    /*   scMarca.setOnQueryTextListener( new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {

              listarItens(listMarca,s,true,false,false);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                listarItens(listMarca,s,true,false,false);
                if (s.equals("")){
                    listMarca.setVisibility(View.GONE);
                }


                return false;
            }
        });*/
       listMarca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               edMarca.setText(itenslist.get(i));
               scMarca.setQuery("",true);
                scMarca.setIconified(true);
               itenslist.clear();
               listMarca.setVisibility(View.GONE);
               edRef.requestFocus();


           }
       });
       /* scRef.setOnQueryTextListener( new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {

                listarItens(listRef,s,false,true,false);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                listarItens(listRef,s,false,true,false);
                if (s.equals("")){
                    listRef.setVisibility(View.GONE);
                }


                return false;
            }
        });*/
        listRef.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edRef.setText(itenslist.get(i));
                scRef.setQuery("",true);
                scRef.setIconified(true);
                itenslist.clear();
                listRef.setVisibility(View.GONE);
                edCor.requestFocus();


            }
        });
        /*scCor.setOnQueryTextListener( new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {

                listarItens(listCor,s,false,false,true);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                listarItens(listCor,s,false,false,true);
                if (s.equals("")){
                    listCor.setVisibility(View.GONE);
                }


                return false;
            }
        });*/
        listCor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edCor.setText(itenslist.get(i));
                scCor.setQuery("",true);
                scCor.setIconified(true);
                itenslist.clear();
                listCor.setVisibility(View.GONE);
                edTipo.requestFocus();


            }
        });
        listGrade.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edGrade.setText(itenslist.get(i));
                itenslist.clear();
                listGrade.setVisibility(View.GONE);
            }
        });

       edMarca.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               listarItens(listMarca,charSequence.toString(),true,false,false,false);
           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
       edRef.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               listarItens(listRef,charSequence.toString(),false,true,false,false);
           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
       edCor.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               listarItens(listCor,charSequence.toString(),false,false,true,false);
           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
       edGrade.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               listarItens(listGrade,charSequence.toString(),false,false,false,true);
           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });

       edCusto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
           @Override
           public void onFocusChange(View view, boolean b) {
               if (!edCusto.getText().toString().equals("")) {
                   Float CustoLiq = Float.parseFloat(edCusto.getText().toString());
                   Integer Desconto = 0;
                   String Ndescontos = "";
                   if (!descontos.equals("")) {
                       Ndescontos = descontos + '+';
                   }

                   while (Ndescontos.contains("+")) {
                       Desconto = Integer.parseInt(Ndescontos.substring(0, Ndescontos.indexOf('+')));
                       CustoLiq = CustoLiq - ((CustoLiq * Desconto) / 100);
                       Ndescontos = Ndescontos.substring(Ndescontos.indexOf('+') + 1, Ndescontos.length());
                   }
                   edLiquido.setText(CustoLiq.toString());
               }
           }
       });

    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Sair");
        msgBox.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        msgBox.setMessage("Deseja sair dessa tela sem salvar?");
        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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

    public void listarItens(ListView listaItem, String variavel, Boolean bMarca, Boolean bRef, Boolean bCor,Boolean bGrade){
        itenslist = new ArrayList<String>();
        try {

            //itenslist.clear();
            String sql="";
            if(bMarca){
                 sql="select distinct marca from produto where marca like ? group by marca";
            }else if(bRef){
                sql="select distinct ref from produto where ref like ? group by ref";
            }else if(bCor){
                sql="select distinct cor from produto where cor like ? group by cor";
            }else if(bGrade){
                sql="select distinct grade from item_pedido where grade like ? group by grade";
            }

            // ArrayList<String> itens=new ArrayList<String>();

            Cursor cursor = Pedido.bancoDados.rawQuery(sql, new String[]{"%"+ variavel+"%"});
            cursor.moveToFirst();

            while (cursor != null) {
                itenslist.add(cursor.getString(0));
                arrayitens.add(cursor.getString(0));
                cursor.moveToNext();
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        listaItem.setVisibility(View.VISIBLE);
        //listaItem.setY(ltMarca.getY() + 60);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,itenslist);
        listaItem.setAdapter(arrayAdapter);
        arrayAdapter.setNotifyOnChange(true);

    }
    public void habilitarMarca(View view){
        edMarca.setEnabled(true);
        edMarca.requestFocus();
    }
    public void habilitarRef(View view){
        edRef.setEnabled(true);
        edRef.requestFocus();
    }
    public void habilitarCor(View view){
        edCor.setEnabled(true);
        edCor.requestFocus();
    }
    public void SalvarItem(View view){
        try {
            if (!edMarca.getText().toString().equals("") && !edTipo.getText().toString().equals("")
            && !edRef.getText().toString().equals("") && !edCor.getText().toString().equals("")
                    && !edCusto.getText().toString().equals("") && !edVenda.getText().toString().equals("")
                    && !edQtd.getText().toString().equals("") && !edGrade.getText().toString().equals("")
                    ) {

                bancoDados = Pedido.bancoDados;
                inserirProduto();
                if (editar != 1) {
                    //  bancoDados = openOrCreateDatabase("bdPedidos",MODE_PRIVATE,null);

                    String sql = ("INSERT INTO item_pedido (marca,tipo,ref,cor,custo_liq,custo,preco_venda,qtd,grade," +
                            "foto,cod_produto, cod_pedido) values(?,?,?,?,?,?,?,?,?,?,?,?) ");
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);
                    stmt.bindString(1, edMarca.getText().toString().trim());
                    stmt.bindString(2, edTipo.getText().toString().trim());
                    stmt.bindString(3, edRef.getText().toString().trim());
                    stmt.bindString(4, edCor.getText().toString().trim());
                    stmt.bindString(5, edCusto.getText().toString());
                    stmt.bindString(6, edLiquido.getText().toString());
                    stmt.bindString(7, edVenda.getText().toString());
                    stmt.bindString(8, edQtd.getText().toString());
                    stmt.bindString(9, edGrade.getText().toString());
                    //stmt.bindString(10,new String( getBytes(bitmap), StandardCharsets.UTF_8));
                    if (bitmap != null) {
                        stmt.bindBlob(10, getBytes(bitmap));
                    } else {
                        stmt.bindString(10, "");
                    }
                    stmt.bindString(11, codProduto);
                    stmt.bindString(12, idPedido);
                    stmt.executeInsert();
                    Toast.makeText(this, "Salvo com Sucesso!",
                            Toast.LENGTH_LONG).show();
                    imFoto.setImageDrawable(null);
                    bitmap = null;
                    edMarca.requestFocus();

                } else {

                    String sql = ("UPDATE item_pedido set marca =?,tipo=?,ref=?,cor=?,custo_liq=?,custo=?, preco_venda=?,qtd=?,grade=?," +
                            "foto=?,cod_produto=? where cod_item=?  ");
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);
                    stmt.bindString(1, edMarca.getText().toString().trim());
                    stmt.bindString(2, edTipo.getText().toString().trim());
                    stmt.bindString(3, edRef.getText().toString().trim());
                    stmt.bindString(4, edCor.getText().toString().trim());
                    stmt.bindString(5, edCusto.getText().toString());
                    stmt.bindString(6, edLiquido.getText().toString());
                    stmt.bindString(7, edVenda.getText().toString());
                    stmt.bindString(8, edQtd.getText().toString());
                    stmt.bindString(9, edGrade.getText().toString());
                    //stmt.bindString(10,new String( getBytes(bitmap), StandardCharsets.UTF_8));
                    BitmapDrawable drawable = (BitmapDrawable) imFoto.getDrawable();
                    if (drawable != null) {
                        stmt.bindBlob(10, getBytes(drawable.getBitmap()));
                    } else {
                        stmt.bindString(10, "");
                    }
                    stmt.bindString(11, codProduto);
                    stmt.bindString(12, "" + codItem);
                    stmt.executeUpdateDelete();
                    finish();
                }


            }else{
                Toast.makeText(this, "Preencha todos os campos",
                        Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(),
                    Toast.LENGTH_LONG).show();
        }

    }
    public void voltarItem(View view){
        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Sair");
        msgBox.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        msgBox.setMessage("Deseja sair dessa tela sem salvar?");
        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

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
    public void inserirProduto(){
        try {
            Cursor cursor = Pedido.bancoDados.rawQuery("select * from produto where marca = ? and ref = ? and cor = ? ",
                    new String[]{edMarca.getText().toString(),edRef.getText().toString(),edCor.getText().toString()});
             codProduto = "";
             idProduto="";
            cursor.moveToFirst();
            codProduto = cursor.getString(0);
            idProduto = cursor.getString(4);


        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            if (codProduto.equals("")) {
                String sql = ("INSERT INTO produto (marca,ref,cor) values(?,?,?) ");
                SQLiteStatement stmt = bancoDados.compileStatement(sql);
                stmt.bindString(1, edMarca.getText().toString());
                stmt.bindString(2, edRef.getText().toString());
                stmt.bindString(3, edCor.getText().toString());
                codProduto = String.valueOf(stmt.executeInsert());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode()== RESULT_OK && result.getData() != null){
                    imFoto.setImageDrawable(null);
                    Bundle bundle = result.getData().getExtras();
                    bitmap =(Bitmap)  bundle.get("data");
                    imFoto.setImageBitmap(bitmap);
                   // System.out.println(new String( getBytes(bitmap), StandardCharsets.UTF_8));
                }
             }
    );
    public byte[] getBytes(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,70,stream);
        return stream.toByteArray();

    }


    }
