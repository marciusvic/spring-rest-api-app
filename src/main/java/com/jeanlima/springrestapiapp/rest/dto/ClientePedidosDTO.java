package com.jeanlima.springrestapiapp.rest.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientePedidosDTO {
    private String cpf;
    private String nomeCliente;
    private List<InfoPedidoDTO> pedidos;
}
