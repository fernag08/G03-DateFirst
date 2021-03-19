
package es.ucm.fdi.iw.g03.date_first.model;

import java.time.LocalDateTime;

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
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Usuario usuario; 
    @ManyToOne
    private Negocio negocio;

    /* Puede convertirse de-a "2021-03-19T11:16:45.633Z", que también viene de JS vía new Date().toISOString();*/

    private boolean solicitada;
    private String inicio;
    private String fin;
    private int capacidad;
    private int ocupadas;
    
    @OneToMany
    @JoinColumn(name="reserva_id")
    private List<Mensaje> mensajes = new ArrayList<>();

    /*public Reserva(long id, Negocio ng, LocalDateTime inicio, LocalDateTime fin, int capacidad, int ocupadas){
        this.id = id;
        this.usuario = null;
        this.negocio = ng;
        this.solicitada = false;
        this.inicio = inicio;
        this.fin = fin;
        this.capacidad = capacidad;
        this.ocupadas = ocupadas;
    }

    public List<Reserva> genera(LocalDateTime inicio, LocalDateTime fin, int cuantas, int duracionMinutos, int capacidadEnCadaUna, Negocio negocio) {
        List<Reserva> listaReservas = new ArrayList<>();
        Reserva reserva;
        LocalDateTime iniAux; 

        for(int i = 0; i < cuantas; i++)
        {
            iniAux = inicio + DateTime(i*duracionMinutos);
            reserva =  new Reserva(i, negocio, iniAux, iniAux + DateTime(duracionMinutos), capacidadEnCadaUna, 0);
            listaReservas.add(reserva);
        }

        return listaReservas;
    }*/

}
