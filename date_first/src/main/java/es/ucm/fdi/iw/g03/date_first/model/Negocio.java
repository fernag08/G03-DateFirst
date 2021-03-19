package es.ucm.fdi.iw.g03.date_first.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import lombok.Data;

@Entity
@Data
public class Negocio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private String descripcion;
    private int aforoMaximo;
    private String direccion;
    private String ciudad;
    private String provincia;
    private int codigoPostal;
    private int plazasDisponibles;
    private int telefono;

    @OneToOne
    private Propietario propietario;

}