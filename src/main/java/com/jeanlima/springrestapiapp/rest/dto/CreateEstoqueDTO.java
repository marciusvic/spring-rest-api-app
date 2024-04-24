package com.jeanlima.springrestapiapp.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEstoqueDTO {
    private Integer id;
    private Integer quantidade;
    private ProdutoDTO produto;
}
