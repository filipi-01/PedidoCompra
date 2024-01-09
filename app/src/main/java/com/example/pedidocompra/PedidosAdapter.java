package com.example.pedidocompra;




import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pedidocompra.MainActivity;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PedidosAdapter extends ArrayAdapter<Pedidos> {
    private final Context context;
    private final ArrayList<Pedidos> elementos;


    public PedidosAdapter( Context context,ArrayList<Pedidos> elementos) {
        super(context, R.layout.linha,elementos);
        this.context = context;
        this.elementos = elementos;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.linha,parent, false);

        TextView txtCod = (TextView) rowView.findViewById(R.id.txtCodigo);
        TextView txtFornecedor = (TextView) rowView.findViewById(R.id.txtFornecedor);
        LinearLayout ltVertical = (LinearLayout) rowView.findViewById(R.id.ltVertical);
        ImageButton btExcluir = (ImageButton) rowView.findViewById(R.id.btExcluir);
        TextView txtEntrega = (TextView) rowView.findViewById(R.id.txtEntrega);
        ImageButton btDuplicar=(ImageButton) rowView.findViewById(R.id.btDuplicar);
        TextView txtPares = (TextView) rowView.findViewById(R.id.txtPares);
        TextView txtItens = (TextView) rowView.findViewById(R.id.txtItens);
        TextView txtLoja = (TextView) rowView.findViewById(R.id.txtLoja);
        TextView txtValTotal = (TextView) rowView.findViewById(R.id.txtValTotal);

        txtCod.setText(elementos.get(position).getCodpedido());
        txtFornecedor.setText(elementos.get(position).getFornecedor());
        txtEntrega.setText(elementos.get(position).getEntrega());
        txtPares.setText(elementos.get(position).getPares());
        txtItens.setText(elementos.get(position).getItens());
        txtLoja.setText(elementos.get(position).getLoja());
        txtValTotal.setText(elementos.get(position).getTotal());


        if (elementos.get(position).getTransmitido().equals("1")){
            txtFornecedor.setTextColor(Color.parseColor("#2a8a0f"));
        }

        ltVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    Cursor cursor = MainActivity.bancoDados.rawQuery("select * from pedido where cod_pedido = ?",new String[] {txtCod.getText().toString()});
                    cursor.moveToFirst();
                    Intent intent = new Intent(context, Pedido.class);
                    intent.putExtra("codPedido",cursor.getString(0));
                    intent.putExtra("fornecedor",cursor.getString(1));
                    intent.putExtra("entrega",cursor.getString(2));
                    intent.putExtra("prazo",cursor.getString(3));
                    intent.putExtra("desconto",cursor.getString(4));
                    intent.putExtra("obs",cursor.getString(5));
                    intent.putExtra("loja",cursor.getString(6));
                    intent.putExtra("editar",1);
                    intent.putExtra("whats",cursor.getString(9));
                    context.startActivity(intent);



                }catch(Exception e){
                    e.printStackTrace();

                }

            }
        });

        btExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder msgBox = new AlertDialog.Builder(context);
                msgBox.setTitle("Excluir");
                msgBox.setIcon(android.R.drawable.ic_menu_delete);
                msgBox.setMessage("Deseja excluir esse Pedido?");
                msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql= "delete from item_pedido where cod_pedido = ? ";
                        SQLiteStatement stmt = MainActivity.bancoDados.compileStatement(sql);

                        stmt.bindString(1,txtCod.getText().toString());

                        stmt.executeUpdateDelete();

                        sql= "delete from pedido where cod_pedido = ? ";
                        stmt = MainActivity.bancoDados.compileStatement(sql);

                        stmt.bindString(1,txtCod.getText().toString());

                        stmt.executeUpdateDelete();

                        elementos.remove(position);

                        notifyDataSetChanged();




                    }
                });
                msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                msgBox.show();



            }
        });

        btDuplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder msgBox = new AlertDialog.Builder(context);
                msgBox.setTitle("Duplicar");
                //msgBox.setIcon(android.R.attr.actionModeCopyDrawable);
                msgBox.setMessage("Deseja Duplicar esse Pedido?");
                msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Long newId = null;
                        Cursor cursor = MainActivity.bancoDados.rawQuery("select * from pedido where cod_pedido = ?", new String[]{txtCod.getText().toString()});
                        cursor.moveToFirst();

                        try {

                            String sql = "INSERT INTO pedido(fornecedor,data_entrega,prazo_pagto,desconto,obs,loja,transmitido,whats_rep) values(?,?,?,?,?,?,0,?)";
                            SQLiteStatement stmt = MainActivity.bancoDados.compileStatement(sql);

                            stmt.bindString(1, cursor.getString(1));
                            stmt.bindString(2, cursor.getString(2));
                            stmt.bindString(3, cursor.getString(3));
                            stmt.bindString(4, cursor.getString(4));

                            stmt.bindString(5, cursor.getString(5));
                            stmt.bindString(6, cursor.getString(6));
                            stmt.bindString(7, cursor.getString(9));
                            newId = stmt.executeInsert();


                            Cursor cursorItem = MainActivity.bancoDados.rawQuery("select * from item_pedido where cod_pedido = ?", new String[]{cursor.getString(0)});
                            cursorItem.moveToFirst();

                            while (cursorItem != null) {
                                sql = ("INSERT INTO item_pedido (marca,tipo,ref,cor,custo_liq,custo,preco_venda,qtd,grade," +
                                        "foto, cod_pedido) values(?,?,?,?,?,?,?,?,?,?,?) ");
                                stmt = MainActivity.bancoDados.compileStatement(sql);
                                stmt.bindString(1, cursorItem.getString(1));
                                stmt.bindString(2, cursorItem.getString(2));
                                stmt.bindString(3, cursorItem.getString(3));
                                stmt.bindString(4, cursorItem.getString(4));
                                stmt.bindString(5, cursorItem.getString(5));
                                stmt.bindString(6, cursorItem.getString(6));
                                stmt.bindString(7, cursorItem.getString(7));
                                stmt.bindString(8, cursorItem.getString(8));
                                stmt.bindString(9, cursorItem.getString(9));
                                //stmt.bindString(10,new String( getBytes(bitmap), StandardCharsets.UTF_8));
                                if (cursorItem.getBlob(10).length > 1) {
                                    stmt.bindBlob(10, cursorItem.getBlob(10));
                                } else {
                                    stmt.bindString(10, "");
                                }
                                stmt.bindString(11, "" + newId);
                                stmt.executeInsert();

                                cursorItem.moveToNext();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                        Intent intent = new Intent(context, Pedido.class);
                        intent.putExtra("codPedido", "" + newId);
                        intent.putExtra("fornecedor", cursor.getString(1));
                        intent.putExtra("entrega", cursor.getString(2));
                        intent.putExtra("prazo", cursor.getString(3));
                        intent.putExtra("desconto", cursor.getString(4));
                        intent.putExtra("obs", cursor.getString(5));
                        intent.putExtra("loja", cursor.getString(6));
                        intent.putExtra("editar", 1);
                        intent.putExtra("whats", cursor.getString(9));


                        context.startActivity(intent);
                    }
                });
                msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                msgBox.show();
            }
        });



        return rowView;
    }
}
