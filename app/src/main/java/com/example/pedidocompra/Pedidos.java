package com.example.pedidocompra;

public class Pedidos {
    private String codpedido;
    private String fornecedor;
    private String entrega;



    public Pedidos(String codpedido, String fornecedor, String entrega) {
        this.codpedido = codpedido;
        this.fornecedor = fornecedor;
        this.entrega = entrega;
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
