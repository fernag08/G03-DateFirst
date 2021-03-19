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
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombreUsu;
    private String contrasena;

    private String nombre;
    private String apellido1;
    private String apellido2;
    private int edad;
    private int telefono;
    private String direccion;
    private String ciudad;
    private String provincia;
    private int codigoPostal;

    @OneToMany
    @JoinColumn(name="propietario_id")
    private List<Negocio> negocios = new ArrayList<>();

    @OneToMany
    @JoinColumn(name="usuario_id")
    private List<Reserva> reservas = new ArrayList<>();
}