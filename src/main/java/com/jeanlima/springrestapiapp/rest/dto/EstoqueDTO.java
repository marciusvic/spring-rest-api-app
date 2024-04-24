package com.jeanlima.springrestapiapp.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder 
public class EstoqueDTO {
    private Integer id;
    private Integer quantidade;
    private ProdutoDTO produto;
}
