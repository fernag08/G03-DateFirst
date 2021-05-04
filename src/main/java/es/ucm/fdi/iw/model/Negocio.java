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
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name="negocioByNombre",
                query="SELECT n FROM Negocio n "
                        + "WHERE n.nombre like CONCAT(:negName, '%')"),
        @NamedQuery(name="negocioByCiudad",
                query="SELECT n FROM Negocio n "
                        + "WHERE n.ciudad like CONCAT(:negCity, '%')"),
        @NamedQuery(name="negocioByProvincia",
                query="SELECT n FROM Negocio n "
                        + "WHERE n.provincia like CONCAT(:negProvince, '%')"),
        @NamedQuery(name="negocioByCodigoPostal",
                query="SELECT n FROM Negocio n "
                        + "WHERE n.codigoPostal like CONCAT(:negPostCode, '%')")
    
})

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
    private int aforoMaximo;
    private String telefono;

    private String latitud;
    private String longitud;

    @ManyToOne
    private User propietario;

    @OneToMany
    @JoinColumn(name="negocio_id")
    private List<Reserva> reservas = new ArrayList<>();

    public int getNumReservas(){
        return reservas.size();
    }

    @Override
    public String toString() {
        return nombre + " " + descripcion;
    }

    /*public String coordsAsJson(){
        String s = "{lat: 'algo', lon: 'otracosa'}";
        JSONObject json = new JSONObject(cadenaJson);
        s = /*[[${n.coordsAsJson()}]] {"{lat: 'algo', lon: 'otracosa'}
        return "";
    }


    /*[[${session.user.name}]] 'Sebastian';

    /*[[${n.coordsAsJson()}]] {"{lat: 'algo', lon: 'otracosa'}*/
}