package es.ucm.fdi.iw.model;

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
    private long id;

    @ManyToOne
    private User usuario; 
    @ManyToOne
    private Negocio negocio;

    /* Puede convertirse de-a "2021-03-19T11:16:45.633Z", que también viene de JS vía new Date().toISOString();*/

    private int solicitada;
    private int numPersonas;
    private String inicio;
    private String fin;
    private int capacidad;
    private int ocupadas;
    
    @OneToMany
    @JoinColumn(name="reserva_id")
    private List<Message> mensajes = new ArrayList<>();

    /*
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