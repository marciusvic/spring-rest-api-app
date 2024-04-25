package com.jeanlima.springrestapiapp.service;

import com.jeanlima.springrestapiapp.model.Estoque;
import com.jeanlima.springrestapiapp.rest.dto.CreateEstoqueDTO;
import com.jeanlima.springrestapiapp.rest.dto.EstoqueDTO;


public interface EstoqueService {
    Estoque salvarEstoque(CreateEstoqueDTO createEstoqueDTO);
    Estoque atualizarEstoque(Integer id, CreateEstoqueDTO createEstoqueDTO);
    EstoqueDTO findByNome(String nome);
}