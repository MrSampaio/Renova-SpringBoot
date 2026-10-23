package com.renova.renova.features.booking;

import com.renova.renova.features.booking.dto.BookingRequest;
import com.renova.renova.features.products.Product;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "*")
public class BookingController {

    // POST /reservas (Removemos o verbo da URL)
    @PostMapping
    public boolean realizarReserva(@RequestBody BookingRequest request){
        BoookingService persistencia = new BoookingService();
        return persistencia.finalizarPedido(request);
    }

    // GET /reservas
    // GET /reservas?nomeCliente=Sampaio
    @GetMapping
    public ArrayList<Booking> listarReservas(@RequestParam(required = false) String nomeCliente) {
        BoookingService persistencia = new BoookingService();

        if (nomeCliente != null && !nomeCliente.isEmpty()) {
            return persistencia.buscarReservaPorNomeCliente(nomeCliente);
        }
        return persistencia.listarTodasReservas();
    }

    // GET /reservas/usuario/1
    @GetMapping("/usuario/{idUsuario}")
    public ArrayList<Booking> listarReservasDoUsuario(@PathVariable int idUsuario){
        BoookingService persistencia = new BoookingService();
        return persistencia.listarReservasPorUsuario(idUsuario);
    }

    // GET /reservas/5/produtos
    @GetMapping("/{idReserva}/produtos")
    public ArrayList<Product> listarProdutosPorReserva(@PathVariable int idReserva){
        BoookingService persistencia = new BoookingService();
        return persistencia.listarProdutosPorReserva(idReserva);
    }

    // GET /reservas/5
    // GET /reservas/5?idUsuario=1
    @GetMapping("/{idReserva}")
    public Booking buscarReservaPorId(@PathVariable int idReserva, @RequestParam(required = false) Integer idUsuario) {
        BoookingService persistencia = new BoookingService();

        if (idUsuario != null) {
            return persistencia.buscarReservaPorId(idReserva, idUsuario);
        }
        return persistencia.buscarReservaGeralPorId(idReserva);
    }

    // PATCH /reservas/5/status?novoStatus=Concluída
    @PatchMapping("/{idReserva}/status")
    public void atualizarStatusReserva(@PathVariable int idReserva, @RequestParam("novoStatus") String novoStatus) {
        BoookingService persistencia = new BoookingService();
        persistencia.atualizarStatusReserva(idReserva, novoStatus);
    }
}