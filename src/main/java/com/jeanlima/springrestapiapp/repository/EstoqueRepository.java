package com.jeanlima.springrestapiapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jeanlima.springrestapiapp.model.Estoque;

public interface EstoqueRepository extends JpaRepository<Estoque, Integer> {

}