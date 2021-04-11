package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
//@NoArgsConstructor
/*@NamedQueries({
        @NamedQuery(name="Negocio.byNombre",
                query="SELECT n FROM Negocio n "
                        + "WHERE n.nombre = :nombre"),
        @NamedQuery(name="Negocio.hasNombre",
                query="SELECT COUNT(n) "
                        + "FROM Negocio n "
                        + "WHERE n.nombre = :nombre")
})*/

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
    
    private String telefono;

    @ManyToOne
    private User propietario;

    @OneToMany
    @JoinColumn(name="negocio_id")
    private List<Reserva> reservas = new ArrayList<>();

    @Override
    public String toString() {
        return nombre + " " + descripcion;
    }

}