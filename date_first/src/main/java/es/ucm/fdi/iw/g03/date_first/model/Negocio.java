package es.ucm.fdi.iw.g03.date_first.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Entity
@Data
public class Negocio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String nombre;
    private String descripcion;
    private String direccion;
    private String ciudad;
    
    private String provincia;
    private int codigoPostal;

    private int plazasDisponibles;
    private int aforoMaximo;
    
    private int telefono;

    @ManyToOne
    private Usuario propietario;

    @OneToMany
    @JoinColumn(name="negocio_id")
    private List<Reserva> reservas = new ArrayList<>();
}