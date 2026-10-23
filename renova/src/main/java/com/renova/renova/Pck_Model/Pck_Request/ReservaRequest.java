package com.renova.renova.Pck_Model.Pck_Request;

import com.renova.renova.Pck_Model.ProdutoModel;
import com.renova.renova.Pck_Model.ReservaModel;

import java.util.ArrayList;

public class ReservaRequest {
    private ReservaModel reserva;
    private ArrayList<ProdutoModel> carrinho;

    public ReservaModel getReserva() {
        return reserva;
    }

    public void setReserva(ReservaModel reserva) {
        this.reserva = reserva;
    }

    public ArrayList<ProdutoModel> getCarrinho() {
        return carrinho;
    }

    public void setCarrinho(ArrayList<ProdutoModel> carrinho) {
        this.carrinho = carrinho;
    }
}
