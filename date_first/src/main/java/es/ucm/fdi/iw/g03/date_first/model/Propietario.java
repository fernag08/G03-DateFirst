package es.ucm.fdi.iw.g03.date_first.model;

//import java.util.ArrayList;
//import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class Propietario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nombre;
    private String apellido1;
    private String apellido2;
    private String direccion;
    private String provincia;
    private String ciudad;
    private String nif;

    //@OneToMany
    //@JoinColumn(name="propietario_id")
    //private List<Negocio> negocios = new ArrayList<>();

}