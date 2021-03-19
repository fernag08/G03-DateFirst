package es.ucm.fdi.iw.g03.date_first.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Administrador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;
    private String contrasena;

    @OneToMany
    @JoinColumn(name="administrador_id")
    private List<Negocio> negocios = new ArrayList<>();

    @OneToMany
    @JoinColumn(name="administrador_id")
    private List<Usuario> usuarios = new ArrayList<>();
}
