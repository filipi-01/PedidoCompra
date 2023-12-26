package com.example.pedidocompra;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import com.bumptech.glide.Glide;
import com.example.pedidocompra.Pedido;

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
    private EditText edGrade;
    private EditText edLoja;
    private Bitmap bitmap;
    private SQLiteDatabase bancoDados;
    private int codItem;
    private int editar;
    private String descontos;

    private String idPedido;

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
        edLoja = findViewById(R.id.edLoja);

        idPedido = extras.getString("idPedido");
        editar = extras.getInt("editar");
        if (editar ==1) {
            codItem = extras.getInt("codItem");
            edMarca.setText(extras.getString("marca"));
            edTipo.setText(extras.getString("tipo"));
            edRef.setText(extras.getString("ref"));
            edCor.setText(extras.getString("cor"));
            edCusto.setText(extras.getString("custo"));
            edVenda.setText(extras.getString("venda"));
            edQtd.setText(extras.getString("qtd"));
            edGrade.setText(extras.getString("grade"));
            edLoja.setText(extras.getString("loja"));

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


    }
    public void SalvarItem(View view){
        try {
            if (!edMarca.getText().toString().equals("") && !edTipo.getText().toString().equals("")
            && !edRef.getText().toString().equals("") && !edCor.getText().toString().equals("")
                    && !edCusto.getText().toString().equals("") && !edVenda.getText().toString().equals("")
                    && !edQtd.getText().toString().equals("") && !edGrade.getText().toString().equals("")
                    && !edLoja.getText().toString().equals("")) {
                Float CustoLiq= Float.parseFloat(edCusto.getText().toString());
                Integer Desconto=0;
                String Ndescontos ="";
                if (!descontos.equals("")) {
                    Ndescontos = descontos + '+';
                }

                while (Ndescontos.contains("+")){
                    Desconto =Integer.parseInt( Ndescontos.substring(0,Ndescontos.indexOf('+')));
                    CustoLiq = CustoLiq - ((CustoLiq * Desconto)/100);
                    Ndescontos = Ndescontos.substring(Ndescontos.indexOf('+')+1,Ndescontos.length());
                }
                if (editar != 1) {
                    //  bancoDados = openOrCreateDatabase("bdPedidos",MODE_PRIVATE,null);
                    bancoDados = Pedido.bancoDados;
                    String sql = ("INSERT INTO item_pedido (marca,tipo,ref,cor,custo_liq,custo,preco_venda,qtd,grade,loja," +
                            "foto, cod_pedido) values(?,?,?,?,?,?,?,?,?,?,?,?) ");
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);
                    stmt.bindString(1, edMarca.getText().toString());
                    stmt.bindString(2, edTipo.getText().toString());
                    stmt.bindString(3, edRef.getText().toString());
                    stmt.bindString(4, edCor.getText().toString());
                    stmt.bindString(5, edCusto.getText().toString());
                    stmt.bindString(6, CustoLiq.toString());
                    stmt.bindString(7, edVenda.getText().toString());
                    stmt.bindString(8, edQtd.getText().toString());
                    stmt.bindString(9, edGrade.getText().toString());
                    stmt.bindString(10, edLoja.getText().toString());
                    //stmt.bindString(10,new String( getBytes(bitmap), StandardCharsets.UTF_8));
                    if (bitmap != null) {
                        stmt.bindBlob(11, getBytes(bitmap));
                    } else {
                        stmt.bindString(11, "");
                    }
                    stmt.bindString(12, idPedido);
                    stmt.executeInsert();
                    Toast.makeText(this, "Salvo com Sucesso!",
                            Toast.LENGTH_LONG).show();
                    imFoto.setImageDrawable(null);
                    bitmap = null;
                    edMarca.requestFocus();

                } else {
                    bancoDados = Pedido.bancoDados;
                    String sql = ("UPDATE item_pedido set marca =?,tipo=?,ref=?,cor=?,custo_liq=?,custo=?, preco_venda=?,qtd=?,grade=?,loja=?," +
                            "foto=? where cod_item=?  ");
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);
                    stmt.bindString(1, edMarca.getText().toString());
                    stmt.bindString(2, edTipo.getText().toString());
                    stmt.bindString(3, edRef.getText().toString());
                    stmt.bindString(4, edCor.getText().toString());
                    stmt.bindString(5, edCusto.getText().toString());
                    stmt.bindString(6, CustoLiq.toString());
                    stmt.bindString(7, edVenda.getText().toString());
                    stmt.bindString(8, edQtd.getText().toString());
                    stmt.bindString(9, edGrade.getText().toString());
                    stmt.bindString(10, edLoja.getText().toString());
                    //stmt.bindString(10,new String( getBytes(bitmap), StandardCharsets.UTF_8));
                    BitmapDrawable drawable = (BitmapDrawable) imFoto.getDrawable();
                    if (drawable != null) {
                        stmt.bindBlob(11, getBytes(drawable.getBitmap()));
                    } else {
                        stmt.bindString(11, "");
                    }
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

        finish();
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
       /* ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        byteBuffer.rewind();
        return byteBuffer.array();*/
    }


    }
