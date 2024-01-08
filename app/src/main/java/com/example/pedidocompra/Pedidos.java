package com.example.pedidocompra;

public class Pedidos {
    private String codpedido;
    private String fornecedor;
    private String entrega;
    private String transmitido;

    private String pares;
    private String itens;
    private String loja;
    private String total;


    public Pedidos(String codpedido, String fornecedor, String entrega, String transmitido, String pares, String itens, String loja, String total) {
        this.codpedido = codpedido;
        this.fornecedor = fornecedor;
        this.entrega = entrega;
        this.transmitido = transmitido;
        this.pares = pares;
        this.itens = itens;
        this.loja = loja;
        this.total = total;
    }



    public String getPares() {
        return pares;
    }

    public void setPares(String pares) {
        this.pares = pares;
    }

    public String getItens() {
        return itens;
    }

    public void setItens(String itens) {
        this.itens = itens;
    }

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTransmitido() {
        return transmitido;
    }

    public void setTransmitido(String transmitido) {
        this.transmitido = transmitido;
    }

    public String getEntrega() {
        return entrega;
    }

    public void setEntrega(String entrega) {
        this.entrega = entrega;
    }
    public String getCodpedido() {
        return codpedido;
    }

    public void setCodpedido(String codpedido) {
        this.codpedido = codpedido;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }
}
