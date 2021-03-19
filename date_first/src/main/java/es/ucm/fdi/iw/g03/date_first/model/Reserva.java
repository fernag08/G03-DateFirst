
package es.ucm.fdi.iw.g03.date_first.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import java.util.ArrayList;
import java.util.List;

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

    /* puede convertirse de-a "2021-03-19T11:16:45.633Z", que también viene de JS vía new Date().toISOString();
    */

    private LocalDateTime solicitada;
    
    private LocalDateTime inicio;
    
    private LocalDateTime fin;

    // podrían faltar mensajes


    public List<Reserva> genera(LocalDateTime inicio, int cuantas, int duracionMinutos, int capacidadEnCadaUna) {
        return new ArrayList<>(); // <-- implementa tu lo que falta
    }

}
