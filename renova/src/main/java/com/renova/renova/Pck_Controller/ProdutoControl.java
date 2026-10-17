package com.renova.renova.Pck_Controller;
import com.renova.renova.Pck_Model.ProdutoModel;
import com.renova.renova.Pck_Persistencia.ProdutoPersistencia;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ProdutoControl {

    @PostMapping("/cadastrar-produto")
    public void CadastrarProduto(@RequestBody ProdutoModel v_produto){

        ProdutoModel produto = new ProdutoModel();
        produto.setNomeProduto(v_produto.getNomeProduto());
        produto.setDescricao(v_produto.getDescricao());
        produto.setPreco(v_produto.getPreco());
        produto.setStatus(v_produto.getStatus());

        ProdutoPersistencia persistencia = new ProdutoPersistencia();
        persistencia.cadastrarProduto(produto);
    }

    public ArrayList<ProdutoModel> listarProdutos() throws Exception {
        ProdutoPersistencia persistencia = new ProdutoPersistencia();
        return persistencia.listarProduto();
    }

    public ProdutoModel buscarProdutoID(int pesquisa) throws Exception{
        ProdutoPersistencia persistencia = new ProdutoPersistencia();
        return persistencia.buscarProdutosID(pesquisa);
    }

    public ArrayList<ProdutoModel> buscarProdutoNome(String pesquisa) throws Exception{
        ProdutoPersistencia persistencia = new ProdutoPersistencia();

        return persistencia.buscarProdutosNome(pesquisa);
    }


}
