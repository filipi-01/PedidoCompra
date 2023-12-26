package com.example.pedidocompra;

public class ItensPedido {


    private String codItem;

    private String Marca;
    private String Tipo;
    private String Ref;
    private byte[] Foto;
    private String Custo;
    private String Venda;

    public String getCodItem() {
        return codItem;
    }

    public void setCodItem(String codItem) {
        this.codItem = codItem;
    }
    public String getMarca() {
        return Marca;
    }

    public void setMarca(String marca) {
        Marca = marca;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public String getRef() {
        return Ref;
    }

    public void setRef(String ref) {
        Ref = ref;
    }

    public byte[] getFoto() {
        return Foto;
    }

    public void setFoto(byte[] foto) {
        Foto = foto;
    }

    public String getCusto() {
        return Custo;
    }

    public void setCusto(String custo) {
        Custo = custo;
    }

    public String getVenda() {
        return Venda;
    }

    public void setVenda(String venda) {
        Venda = venda;
    }



    public ItensPedido(String codItem,String marca, String tipo, String ref, byte[] foto, String custo, String venda) {
        this.codItem = codItem;
        Marca = marca;
        Tipo = tipo;
        Ref = ref;
        Foto = foto;
        Custo = custo;
        Venda = venda;

    }
}
