package com.renova.renova.Pck_Controller;

import com.renova.renova.Pck_Model.Pck_Entities.ProdutoModel;
import com.renova.renova.Pck_Persistencia.ReservaPersistencia;
import com.renova.renova.Pck_Model.Pck_Entities.ReservaModel;
import com.renova.renova.Pck_Model.Pck_Request.ReservaRequest;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*")
public class ReservaControl {

    // POST /reservas (Removemos o verbo da URL)
    @PostMapping
    public boolean realizarReserva(@RequestBody ReservaRequest request){
        ReservaPersistencia persistencia = new ReservaPersistencia();
        return persistencia.finalizarPedido(request);
    }

    // GET /reservas
    // GET /reservas?nomeCliente=Sampaio
    @GetMapping
    public ArrayList<ReservaModel> listarReservas(@RequestParam(required = false) String nomeCliente) {
        ReservaPersistencia persistencia = new ReservaPersistencia();

        if (nomeCliente != null && !nomeCliente.isEmpty()) {
            return persistencia.buscarReservaPorNomeCliente(nomeCliente);
        }
        return persistencia.listarTodasReservas();
    }

    // GET /reservas/usuario/1
    @GetMapping("/usuario/{idUsuario}")
    public ArrayList<ReservaModel> listarReservasDoUsuario(@PathVariable int idUsuario){
        ReservaPersistencia persistencia = new ReservaPersistencia();
        return persistencia.listarReservasPorUsuario(idUsuario);
    }

    // GET /reservas/5/produtos
    @GetMapping("/{idReserva}/produtos")
    public ArrayList<ProdutoModel> listarProdutosPorReserva(@PathVariable int idReserva){
        ReservaPersistencia persistencia = new ReservaPersistencia();
        return persistencia.listarProdutosPorReserva(idReserva);
    }

    // GET /reservas/5
    // GET /reservas/5?idUsuario=1
    @GetMapping("/{idReserva}")
    public ReservaModel buscarReservaPorId(@PathVariable int idReserva, @RequestParam(required = false) Integer idUsuario) {
        ReservaPersistencia persistencia = new ReservaPersistencia();

        if (idUsuario != null) {
            return persistencia.buscarReservaPorId(idReserva, idUsuario);
        }
        return persistencia.buscarReservaGeralPorId(idReserva);
    }

    // PATCH /reservas/5/status?novoStatus=Concluída
    @PatchMapping("/{idReserva}/status")
    public void atualizarStatusReserva(@PathVariable int idReserva, @RequestParam("novoStatus") String novoStatus) {
        ReservaPersistencia persistencia = new ReservaPersistencia();
        persistencia.atualizarStatusReserva(idReserva, novoStatus);
    }
}