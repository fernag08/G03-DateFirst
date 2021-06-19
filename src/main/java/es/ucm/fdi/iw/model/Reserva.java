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


@NamedQueries({
    @NamedQuery(name="Reserva.reservaByDia",
                query="SELECT r FROM Reserva r "
                        + "WHERE r.negocio = :negocioBuscado AND r.inicio >= :diaBuscadaIni AND r.fin <= :diaBuscadaFin"), 
    @NamedQuery(name="Reserva.delEsteDia",
            query="SELECT COUNT(r) "
                    + "FROM Reserva r "
                    + "WHERE r.negocio = :negocioBuscado AND r.inicio >= :diaBuscadaIni AND r.fin <= :diaBuscadaFin AND r.estado = 0"),
    @NamedQuery(name="Reserva.libSolCan",
            query="SELECT COUNT(r) "
                    + "FROM Reserva r "
                    + "WHERE r.negocio = :negocioBuscado AND r.inicio >= :diaBuscadaIni AND r.fin <= :diaBuscadaFin AND r.estado != 0")
})
   
@Entity
@Data
public class Reserva {       

    public enum Estado {LIBRE, SOLICITADA, CANCELADA, CONFIRMADA};

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User usuario; 
    @ManyToOne
    private Negocio negocio;

    /* Puede convertirse de-a "2021-03-19T11:16:45.633Z", que también viene de JS vía new Date().toISOString();*/

    private Estado estado;
    private int capacidad;
    private int numPersonas;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    
    public Reserva() {}
    public Reserva(long id, User usuario, Negocio negocio, Estado estado, 
        int numPersonas, LocalDateTime inicio, LocalDateTime fin, int capacidad) {
        this.id = id;
        this.usuario = usuario;
        this.negocio = negocio;
        this.estado = estado;
        this.numPersonas = numPersonas;
        this.inicio = inicio;
        this.fin = fin;
        this.capacidad = capacidad;
    }
   
    @OneToMany
    @JoinColumn(name="reserva_id")
    private List<Message> mensajes = new ArrayList<>();

    public static List<Reserva> generaReserva(LocalDateTime inicio, LocalDateTime fin, int cuantas, int duracionMinutos, int capacidadEnCadaUna, Negocio negocio, User user) {
        List<Reserva> listaReservas = new ArrayList<>();
        LocalDateTime prev = inicio;
        LocalDateTime post;
        LocalDateTime actual = inicio;
        int j = 0;
       
        post = prev.plusMinutes(duracionMinutos);
        while(fin.isAfter(post) || fin.equals(post)) 
        {
           
            for(int i = 0; i < cuantas; i++)
            {
                listaReservas.add(new Reserva(0L, null, negocio, Estado.LIBRE, 
                0, prev, post, capacidadEnCadaUna));
               
            }
            prev = prev.plusMinutes(duracionMinutos);
            post = prev.plusMinutes(duracionMinutos);
            actual = prev;
        }
        
        return listaReservas;
    }

    @Override
    public String toString() {
        
        return  "Hora de inicio: " + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(inicio) + "\n" +"Hora de fin: "
            + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(fin) + "\n" +"Capacidad: " + capacidad + "\n" +"Número de personas: " + numPersonas + "\n" +"Estado de la reserva: "+ estado;
    }
}