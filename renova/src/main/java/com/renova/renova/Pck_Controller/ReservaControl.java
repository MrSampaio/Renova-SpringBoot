package com.renova.renova.Pck_Controller;

import com.renova.renova.Pck_Model.ProdutoModel;
import com.renova.renova.Pck_Persistencia.ReservaPersistencia;
import com.renova.renova.Pck_Model.ReservaModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ReservaControl {

    @PostMapping("/realizar-reserva")
    public boolean realizarReserva(@RequestBody ReservaModel reserva, ArrayList<ProdutoModel> carrinho){
        ReservaPersistencia persistencia = new ReservaPersistencia();
        return persistencia.finalizarPedido(reserva, carrinho);
    }

    public ArrayList<ReservaModel> listarReservas(int idUsuario){
        ReservaPersistencia persistencia = new ReservaPersistencia();
        return persistencia.listarReservasPorUsuario(idUsuario);
    }

    public ArrayList<ProdutoModel> listarProdutosPorReserva(int idReserva){
        ReservaPersistencia persistencia = new ReservaPersistencia();
        return persistencia.listarProdutosPorReserva(idReserva);
    }

    public ReservaModel buscarReservaPorId(int idReserva, int idUsuario) {
        ReservaPersistencia persistencia = new ReservaPersistencia();
        return persistencia.buscarReservaPorId(idReserva, idUsuario);
    }

    public ArrayList<ReservaModel> listarTodasReservas() {
        ReservaPersistencia persistencia = new ReservaPersistencia();
        return persistencia.listarTodasReservas();
    }

    public ReservaModel buscarReservaGeralPorId(int idReserva) {
        ReservaPersistencia persistencia = new ReservaPersistencia();
        return persistencia.buscarReservaGeralPorId(idReserva);
    }

    public ArrayList<ReservaModel> buscarReservaPorNomeCliente(String nomeCliente) {
        ReservaPersistencia persistencia = new ReservaPersistencia();
        return persistencia.buscarReservaPorNomeCliente(nomeCliente);
    }

    public void atualizarStatusReserva(int idReserva, String novoStatus) {
        ReservaPersistencia persistencia = new ReservaPersistencia();
        persistencia.atualizarStatusReserva(idReserva, novoStatus);
    }
}