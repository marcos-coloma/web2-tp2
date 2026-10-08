package com.example.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un favorito en la tabla "favoritos".
 * Se utiliza como modelo de persistencia y se convierte al dominio Favorito
 * mediante el FavoritoRepositoryAdapter.
 */
@Entity
@Table(name = "favoritos")
public class FavoritoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    private String nota;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAgregado;

    @ManyToOne
    @JoinColumn(name = "lista_id")
    private ListaEntity lista;

    public FavoritoEntity() {
    }

    public FavoritoEntity(
            Long id,
            Long productoId,
            String nota,
            LocalDateTime fechaAgregado,
            ListaEntity lista
    ) {
        this.id = id;
        this.productoId = productoId;
        this.nota = nota;
        this.fechaAgregado = fechaAgregado;
        this.lista = lista;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public LocalDateTime getFechaAgregado() {
        return fechaAgregado;
    }

    public void setFechaAgregado(LocalDateTime fechaAgregado) {
        this.fechaAgregado = fechaAgregado;
    }

    public ListaEntity getLista() {
        return lista;
    }

    public void setLista(ListaEntity lista) {
        this.lista = lista;
    }
}