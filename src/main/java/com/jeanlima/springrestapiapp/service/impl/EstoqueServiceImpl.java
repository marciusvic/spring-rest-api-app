package com.jeanlima.springrestapiapp.service.impl;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.jeanlima.springrestapiapp.model.Produto;
import com.jeanlima.springrestapiapp.model.Estoque;
import com.jeanlima.springrestapiapp.repository.ProdutoRepository;
import com.jeanlima.springrestapiapp.repository.EstoqueRepository;
import com.jeanlima.springrestapiapp.service.EstoqueService;

import jakarta.persistence.criteria.CriteriaBuilder.In;

import com.jeanlima.springrestapiapp.rest.dto.CreateEstoqueDTO;
import com.jeanlima.springrestapiapp.rest.dto.ProdutoDTO;
import com.jeanlima.springrestapiapp.exception.RegraNegocioException;

@Service
@RequiredArgsConstructor
public class EstoqueServiceImpl implements EstoqueService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Override
    public Estoque salvarEstoque(CreateEstoqueDTO createEstoqueDTO) {
        Integer quantidade = createEstoqueDTO.getQuantidade();
        Estoque estoque = new Estoque();
        ProdutoDTO produtoDTO = createEstoqueDTO.getProduto();
        Produto produto = produtoRepository.findById(produtoDTO.getId())
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));
        estoque.setProduto(produto);
        estoque.setQuantidade(quantidade);
        return estoqueRepository.save(estoque);
    }

    @Override
    public Estoque atualizarEstoque(Integer id, CreateEstoqueDTO createEstoqueDTO) {
        Estoque estoque = estoqueRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Estoque não encontrado."));
        ProdutoDTO produtoDTO = createEstoqueDTO.getProduto();
        Produto produto = produtoRepository.findById(produtoDTO.getId())
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));
        estoque.setProduto(produto);
        estoque.setQuantidade(createEstoqueDTO.getQuantidade());
        return estoqueRepository.save(estoque);
    }
    @Override
    public Estoque findByNome(String nome) {
        List<Estoque> estoques = estoqueRepository.findAll();
        String nomeSemAspas = nome.substring(1, nome.length() - 1);
        System.out.println(nomeSemAspas);
        Integer estoqueId = 0;
        for (Estoque estoque : estoques) {
            if (estoque.getProduto().getDescricao().equals(nomeSemAspas)) {
                estoqueId = estoque.getId();
                System.out.println(estoque.getProduto().getDescricao() + " " + estoqueId);
            }
        }
        return estoqueRepository.findById(estoqueId)
                .orElseThrow(() -> new RegraNegocioException("Estoque não encontrado."));
    }
}
