package com.renova.renova.features.products;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("/produtos")
public class ProductController {

    @PostMapping("")
    public void CadastrarProduto(@RequestBody Product v_produto){

        Product produto = new Product();
        produto.setNomeProduto(v_produto.getNomeProduto());
        produto.setDescricao(v_produto.getDescricao());
        produto.setPreco(v_produto.getPreco());
        produto.setStatus(v_produto.getStatus());

        ProductService persistencia = new ProductService();
        persistencia.cadastrarProduto(produto);
    }

    // GET /produtos  (lista todos)
    // GET /produtos?nome=Camisa (filtra por nome)
    @GetMapping("")
    public ArrayList<Product> listarProdutos(@RequestParam(value = "nome", required = false) String nome) throws Exception {
        ProductService persistencia = new ProductService();

        // se o usuário mandou um nome na URL, faz a busca filtrada
        if (nome != null && !nome.isEmpty()) {
            return persistencia.buscarProdutosNome(nome);
        }

        // se não mandou nada, lista todos
        return persistencia.listarProduto();
    }

    // GET /produtos/1 (filtra por id)
    @GetMapping("/{id}")
    public Product buscarProdutoID(@PathVariable int id) throws Exception{
        ProductService persistencia = new ProductService();
        return persistencia.buscarProdutosID(id);
    }

//    @GetMapping("")
//    public ArrayList<ProdutoModel> buscarProdutoNome(String pesquisa) throws Exception{
//        ProdutoPersistencia persistencia = new ProdutoPersistencia();
//
//        return persistencia.buscarProdutosNome(pesquisa);
//    }
}
