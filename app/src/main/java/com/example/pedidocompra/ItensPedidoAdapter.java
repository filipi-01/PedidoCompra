package com.example.pedidocompra;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ItensPedidoAdapter extends ArrayAdapter<ItensPedido> {
    private final Context context;
    private final ArrayList<ItensPedido> elementos;

    public ItensPedidoAdapter(Context context,  ArrayList<ItensPedido> elementos) {
        super(context, R.layout.linha_item,elementos);
        this.context = context;
        this.elementos = elementos;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.linha_item,parent, false);

        TextView txtMarca = (TextView) rowView.findViewById(R.id.edMarcaItem);
        LinearLayout ltVertical = (LinearLayout) rowView.findViewById(R.id.ltVerticalItem);
        TextView txtTipo = (TextView) rowView.findViewById(R.id.edTipoItem);
        TextView txtRef = (TextView) rowView.findViewById(R.id.edRefItem);
        TextView txtCusto = (TextView) rowView.findViewById(R.id.edCustoItem);
        TextView txtVenda = (TextView) rowView.findViewById(R.id.edVendaItem);
        ImageButton btExcluir = (ImageButton) rowView.findViewById(R.id.btExcluirItem);
        ImageView imgFoto = (ImageView) rowView.findViewById(R.id.imgFotoItem);

        txtMarca.setText(elementos.get(position).getMarca());
        txtTipo.setText(elementos.get(position).getTipo());
        txtRef.setText(elementos.get(position).getRef());
        txtCusto.setText(elementos.get(position).getCusto());
        txtVenda.setText(elementos.get(position).getVenda());

       // System.out.println(new String(elementos.get(position).getFoto(), StandardCharsets.UTF_8));
       // Bitmap bmp = BitmapFactory.decodeByteArray(elementos.get(position).getFoto(),0,elementos.get(position).getFoto().length);
        //imgFoto.setImageBitmap(Bitmap.createScaledBitmap(bmp,imgFoto.getMaxWidth(),imgFoto.getHeight(),false));
        //imgFoto.setImageBitmap(bmp);

        Glide.with(context).asBitmap().load(elementos.get(position).getFoto()).into(imgFoto);



        ltVertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    Cursor cursor = Pedido.bancoDados.rawQuery("select * from item_pedido where cod_item = ?",new String[] {elementos.get(position).getCodItem()});
                    cursor.moveToFirst();
                    Intent intent = new Intent(context, ItemPedido.class);
                    intent.putExtra("codItem",cursor.getInt(0));
                    intent.putExtra("marca",cursor.getString(1));
                    intent.putExtra("tipo",cursor.getString(2));
                    intent.putExtra("ref",cursor.getString(3));
                    intent.putExtra("desconto",Pedido.stDesconto);
                    intent.putExtra("cor",cursor.getString(4));
                    intent.putExtra("custo",cursor.getString(5));
                    intent.putExtra("custoLiq",cursor.getString(6));
                    intent.putExtra("venda",cursor.getString(7));
                    intent.putExtra("qtd",cursor.getString(8));
                    intent.putExtra("grade",cursor.getString(9));

                    intent.putExtra("foto",elementos.get(position).getFoto());
                    intent.putExtra("idPedido",cursor.getString(11));


                    intent.putExtra("editar",1);
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
                        String sql= "delete from item_pedido where cod_item = ? ";
                        SQLiteStatement stmt = Pedido.bancoDados.compileStatement(sql);

                        stmt.bindString(1,elementos.get(position).getCodItem());

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



        return rowView;
    }
}
