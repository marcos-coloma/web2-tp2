package com.example.demo.repository;

import com.example.demo.entity.FavoritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoritoJpaRepository extends JpaRepository<FavoritoEntity, Long> {
}