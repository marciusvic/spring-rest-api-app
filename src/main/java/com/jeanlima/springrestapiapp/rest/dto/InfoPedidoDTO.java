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
public class InfoPedidoDTO {
    private Integer codigo;
    private String dataPedido;
    private List<InformacaoItemPedidoDTO> itens;
}
