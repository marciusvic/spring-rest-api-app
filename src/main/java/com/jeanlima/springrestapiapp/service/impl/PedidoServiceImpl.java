package com.jeanlima.springrestapiapp.service.impl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.jeanlima.springrestapiapp.enums.StatusPedido;
import com.jeanlima.springrestapiapp.exception.PedidoNaoEncontradoException;
import com.jeanlima.springrestapiapp.exception.RegraNegocioException;
import com.jeanlima.springrestapiapp.model.Cliente;
import com.jeanlima.springrestapiapp.model.Estoque;
import com.jeanlima.springrestapiapp.model.ItemPedido;
import com.jeanlima.springrestapiapp.model.Pedido;
import com.jeanlima.springrestapiapp.model.Produto;
import com.jeanlima.springrestapiapp.repository.ClienteRepository;
import com.jeanlima.springrestapiapp.repository.EstoqueRepository;
import com.jeanlima.springrestapiapp.repository.ItemPedidoRepository;
import com.jeanlima.springrestapiapp.repository.PedidoRepository;
import com.jeanlima.springrestapiapp.repository.ProdutoRepository;
import com.jeanlima.springrestapiapp.rest.dto.ItemPedidoDTO;
import com.jeanlima.springrestapiapp.rest.dto.PedidoDTO;
import com.jeanlima.springrestapiapp.service.PedidoService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import com.jeanlima.springrestapiapp.enums.StatusPedido;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    
    private final PedidoRepository repository;
    private final ClienteRepository clientesRepository;
    private final ProdutoRepository produtosRepository;
    private final ItemPedidoRepository itemsPedidoRepository;
    private final EstoqueRepository estoqueRepository;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientesRepository
                .findById(idCliente)
                .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itemsPedido = converterItems(pedido, dto.getItems());
        for(ItemPedido item : itemsPedido){
            Estoque estoqueProduto = estoqueRepository
                    .findById(item.getProduto().getId())
                    .orElseThrow(() -> new RegraNegocioException("Produto não encontrado no estoque: "+ item.getProduto().getId()));
            if(estoqueProduto.getQuantidade() < item.getQuantidade()){
                throw new RegraNegocioException("Quantidade insuficiente no estoque para o produto: "+ item.getProduto().getDescricao());
            }
            estoqueProduto.setQuantidade(estoqueProduto.getQuantidade() - item.getQuantidade());
        }
        repository.save(pedido);
        itemsPedidoRepository.saveAll(itemsPedido);
        pedido.setItens(itemsPedido);
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itemsPedido) {
            Produto produto = item.getProduto();
            BigDecimal precoItem = produto.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
            total = total.add(precoItem);
        }
        pedido.setTotal(total);
        return pedido;
    }
    private List<ItemPedido> converterItems(Pedido pedido, List<ItemPedidoDTO> items){
        if(items.isEmpty()){
            throw new RegraNegocioException("Não é possível realizar um pedido sem items.");
        }

        return items
                .stream()
                .map( dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtosRepository
                            .findById(idProduto)
                            .orElseThrow(
                                    () -> new RegraNegocioException(
                                            "Código de produto inválido: "+ idProduto
                                    ));

                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());

    }
    private List<ItemPedido> converterItemsList(Pedido pedido, List<ItemPedido> items){
        if(items.isEmpty()){
            throw new RegraNegocioException("Não é possível realizar um pedido sem items.");
        }

        return items
        .stream()
        .map( dto -> {
            Integer idProduto = dto.getProduto().getId();
            Produto produto = produtosRepository
                    .findById(idProduto)
                    .orElseThrow(
                            () -> new RegraNegocioException(
                                    "Código de produto inválido: "+ idProduto
                            ));

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setQuantidade(dto.getQuantidade());
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            return itemPedido;
        }).collect(Collectors.toList());
    }
    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return repository.findByIdFetchItens(id);
    }
    @Override
    public void atualizaStatus(Integer id, StatusPedido statusPedido) {
        repository
        .findById(id)
        .map( pedido -> {
            pedido.setStatus(statusPedido);
            return repository.save(pedido);
        }).orElseThrow(() -> new PedidoNaoEncontradoException() );
    }
    @Override
    public void deletarPedido(Pedido pedido){
        for (ItemPedido item : pedido.getItens()) {
            itemsPedidoRepository.delete(item);
        }
        repository.delete(pedido);
    }
    @Override
    public void atualizarPedido(Integer id, PedidoDTO dto){
        Pedido pedido =  repository
            .findById(id)
            .orElseThrow(() -> new PedidoNaoEncontradoException());
        Cliente cliente = clientesRepository
            .findById(dto.getCliente())
            .orElseThrow(() -> new RegraNegocioException("Código de cliente inválido."));
        List<ItemPedido> itemsPedido = converterItemsList(pedido, pedido.getItens());
        for(ItemPedido item : itemsPedido){
            Estoque estoqueProduto = estoqueRepository
                .findById(item.getProduto().getId())
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado no estoque: "+ item.getProduto().getId()));
            estoqueProduto.setQuantidade(estoqueProduto.getQuantidade() + item.getQuantidade());
        }
        List<ItemPedido> itemsPedidoUpToDate = new ArrayList<>();
        for(ItemPedidoDTO itemDto: dto.getItems()) {
            ItemPedido item = new ItemPedido();
            item.setProduto(produtosRepository.findById(itemDto.getProduto()).orElseThrow(() -> new RegraNegocioException("Produto não encontrado: "+ itemDto.getProduto())));
            item.setQuantidade(itemDto.getQuantidade());
            itemsPedidoUpToDate.add(item);
        }
        for(ItemPedido item : itemsPedidoUpToDate){
            Estoque estoqueProduto = estoqueRepository
                .findById(item.getProduto().getId())
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado no estoque: "+ item.getProduto().getId()));
            if(estoqueProduto.getQuantidade() < item.getQuantidade()){
                throw new RegraNegocioException("Quantidade insuficiente no estoque para o produto: "+ item.getProduto().getDescricao());
            }
            estoqueProduto.setQuantidade(estoqueProduto.getQuantidade() - item.getQuantidade());
            System.out.println("Estoque atualizado: " + estoqueProduto);
            estoqueRepository.save(estoqueProduto);
        }
        itemsPedidoRepository.saveAll(itemsPedidoUpToDate);
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itemsPedidoUpToDate) {
            Produto produto = item.getProduto();
            BigDecimal precoItem = produto.getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
            total = total.add(precoItem);
        }
        for (ItemPedido item : pedido.getItens()) {
            itemsPedidoRepository.delete(item);
        }
        for (ItemPedido item : itemsPedidoUpToDate) {
            item.setPedido(pedido);
        }
        pedido.setCliente(cliente);
        pedido.setTotal(total);
        pedido.setItens(itemsPedidoUpToDate);
        pedido.setStatus(StatusPedido.REALIZADO);
        repository.save(pedido);
    }
    
}
