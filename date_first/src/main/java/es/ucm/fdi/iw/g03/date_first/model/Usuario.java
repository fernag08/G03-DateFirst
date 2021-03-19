package es.ucm.fdi.iw.g03.date_first.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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

}