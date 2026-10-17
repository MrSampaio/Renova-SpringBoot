package com.renova.renova.Pck_Controller;
import com.renova.renova.Pck_Model.ProdutoModel;
import com.renova.renova.Pck_Persistencia.ProdutoPersistencia;
import jakarta.websocket.server.PathParam;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/produtos")
public class ProdutoControl {

    @PostMapping("")
    public void CadastrarProduto(@RequestBody ProdutoModel v_produto){

        ProdutoModel produto = new ProdutoModel();
        produto.setNomeProduto(v_produto.getNomeProduto());
        produto.setDescricao(v_produto.getDescricao());
        produto.setPreco(v_produto.getPreco());
        produto.setStatus(v_produto.getStatus());

        ProdutoPersistencia persistencia = new ProdutoPersistencia();
        persistencia.cadastrarProduto(produto);
    }

    @GetMapping("")
    public ArrayList<ProdutoModel> listarProdutos() throws Exception {
        ProdutoPersistencia persistencia = new ProdutoPersistencia();
        return persistencia.listarProduto();
    }

    @GetMapping("/{id}")
    public ProdutoModel buscarProdutoID(@PathVariable int id) throws Exception{
        ProdutoPersistencia persistencia = new ProdutoPersistencia();
        return persistencia.buscarProdutosID(id);
    }

    public ArrayList<ProdutoModel> buscarProdutoNome(String pesquisa) throws Exception{
        ProdutoPersistencia persistencia = new ProdutoPersistencia();

        return persistencia.buscarProdutosNome(pesquisa);
    }


}
