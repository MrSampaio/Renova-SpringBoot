package com.renova.renova.Pck_Controller;
import com.renova.renova.Pck_Model.Pck_Entities.ProdutoModel;
import com.renova.renova.Pck_Persistencia.ProdutoPersistencia;
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

    // GET /produtos  (lista todos)
    // GET /produtos?nome=Camisa (filtra por nome)
    @GetMapping("")
    public ArrayList<ProdutoModel> listarProdutos(@RequestParam(value = "nome", required = false) String nome) throws Exception {
        ProdutoPersistencia persistencia = new ProdutoPersistencia();

        // se o usuário mandou um nome na URL, faz a busca filtrada
        if (nome != null && !nome.isEmpty()) {
            return persistencia.buscarProdutosNome(nome);
        }

        // se não mandou nada, lista todos
        return persistencia.listarProduto();
    }

    // GET /produtos/1 (filtra por id)
    @GetMapping("/{id}")
    public ProdutoModel buscarProdutoID(@PathVariable int id) throws Exception{
        ProdutoPersistencia persistencia = new ProdutoPersistencia();
        return persistencia.buscarProdutosID(id);
    }

//    @GetMapping("")
//    public ArrayList<ProdutoModel> buscarProdutoNome(String pesquisa) throws Exception{
//        ProdutoPersistencia persistencia = new ProdutoPersistencia();
//
//        return persistencia.buscarProdutosNome(pesquisa);
//    }
}
