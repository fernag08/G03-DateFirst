package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;


/*@NamedQueries({
    @NamedQuery(name="User.byUsername",
            query="SELECT u FROM User u "
                    + "WHERE u.username = :username AND u.enabled = 1"),
    @NamedQuery(name="User.hasUsername",
            query="SELECT COUNT(u) "
                    + "FROM User u "
                    + "WHERE u.username = :username")
})*/

@Entity
@Data
public class Reserva {

    public enum Estado { LIBRE, SOLICITADA, CONFIRMADA, CANCELADA };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User usuario; 
    @ManyToOne
    private Negocio negocio;

    /* Puede convertirse de-a "2021-03-19T11:16:45.633Z", que también viene de JS vía new Date().toISOString();*/

    private Estado estado;
    private int numPersonas;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private int capacidad;
    private int ocupadas=0;
    
    public Reserva() {}
    public Reserva(long id, User usuario, Negocio negocio, Estado estado, 
        int numPersonas, LocalDateTime inicio, LocalDateTime fin, int capacidad, int ocupadas) {
        this.id = id;
        this.usuario = usuario;
        this.negocio = negocio;
        this.estado = estado;
        this.numPersonas = numPersonas;
        this.inicio = inicio;
        this.fin = fin;
        this.capacidad = capacidad;
        this.ocupadas = ocupadas;
    }
    
   
    @OneToMany
    @JoinColumn(name="reserva_id")
    private List<Message> mensajes = new ArrayList<>();

    public static List<Reserva> generaReserva(LocalDateTime inicio, LocalDateTime fin, int cuantas, int duracionMinutos, int capacidadEnCadaUna, Negocio negocio) {
        List<Reserva> listaReservas = new ArrayList<>();
        LocalDateTime prev = inicio;
        for(int i = 0; i < cuantas; i++)
        {
            listaReservas.add(new Reserva(
                0L, null, negocio, Estado.LIBRE, 0, 
                prev, prev.plusMinutes(duracionMinutos), capacidadEnCadaUna, 0));
            prev = prev.plusMinutes(duracionMinutos);
        }
        return listaReservas;
    }

    @Override
    public String toString() {
        
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(inicio) 
            + " " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(fin) + " x" + numPersonas + "Estado de la reserva:"+ estado;
    }
}