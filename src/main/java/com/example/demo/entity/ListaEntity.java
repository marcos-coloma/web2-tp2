package com.example.demo.entity;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa una lista en la tabla "listas".
 * Se utiliza como modelo de persistencia y se convierte al dominio Lista
 * mediante el ListaRepositoryAdapter.
 */

@Entity
@Table(name = "listas")
public class ListaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;

    public ListaEntity() {
    }

    public ListaEntity(Long id, String nombre) {
        this(id, nombre, null);
    }

    public ListaEntity(Long id, String nombre, UsuarioEntity usuario) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }
}