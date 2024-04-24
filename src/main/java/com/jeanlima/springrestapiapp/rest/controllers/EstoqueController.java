package com.jeanlima.springrestapiapp.rest.controllers;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.jeanlima.springrestapiapp.model.Estoque;
import com.jeanlima.springrestapiapp.repository.EstoqueRepository;
import com.jeanlima.springrestapiapp.service.EstoqueService;
import com.jeanlima.springrestapiapp.rest.dto.CreateEstoqueDTO;
import com.jeanlima.springrestapiapp.rest.dto.EstoqueDTO;
import com.jeanlima.springrestapiapp.rest.dto.ProdutoDTO;

@RestController
@RequestMapping("/api/estoques")
public class EstoqueController {

    @Autowired
    private EstoqueRepository repository;

    @Autowired
    private EstoqueService estoqueService;

    @PostMapping
    @ResponseStatus(CREATED)
    public Estoque save(@RequestBody CreateEstoqueDTO dto) {
        Estoque estoque = estoqueService.salvarEstoque(dto);
        return estoque;
    }

    @PutMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void update( @PathVariable Integer id, @RequestBody CreateEstoqueDTO dto){
      estoqueService.atualizarEstoque(id, dto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        repository.findById(id)
                  .map(e -> {
                      repository.delete(e);
                      return Void.TYPE;
                  })
                  .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado."));
    }

    @GetMapping("{id}")
    public EstoqueDTO getById(@PathVariable Integer id) {
        Estoque estoque = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estoque não encontrado."));
        ProdutoDTO produtoDTO = new ProdutoDTO(
                estoque.getProduto().getId(),
                estoque.getProduto().getDescricao(),
                estoque.getProduto().getPreco()
        );
        return EstoqueDTO.builder()
                .id(estoque.getId())
                .quantidade(estoque.getQuantidade())
                .produto(produtoDTO)
                .build();
    }


    @GetMapping
    public List<EstoqueDTO> find(EstoqueDTO filtro) {
    ExampleMatcher matcher = ExampleMatcher
            .matching()
            .withIgnoreCase()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

    Estoque estoqueFiltro = new Estoque();
    estoqueFiltro.setQuantidade(filtro.getQuantidade());
    Example<Estoque> example = Example.of(estoqueFiltro, matcher);
    List<Estoque> estoques = repository.findAll(example);
    List<EstoqueDTO> estoqueDTOs = new ArrayList<>();
    for (Estoque estoque : estoques) {
        ProdutoDTO produtoDTO = new ProdutoDTO(
            estoque.getProduto().getId(),
            estoque.getProduto().getDescricao(),
            estoque.getProduto().getPreco()
        );
        EstoqueDTO estoqueDTO = new EstoqueDTO(
            estoque.getId(),
            estoque.getQuantidade(),
            produtoDTO
        );
        estoqueDTOs.add(estoqueDTO);
    }
    
    return estoqueDTOs;
}


}
