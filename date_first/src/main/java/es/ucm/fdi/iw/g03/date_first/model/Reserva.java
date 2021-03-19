
package es.ucm.fdi.iw.g03.date_first.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Usuario usuario;
    @ManyToOne
    private Negocio negocio;

    private int anio;
    private int mes;
    private int dia;
    private int hora;
    private int minuto;
    
    
}
