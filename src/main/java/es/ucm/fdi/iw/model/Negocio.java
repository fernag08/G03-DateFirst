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
                        + "WHERE n.codigoPostal like CONCAT(:negPostCode, '%')"),
        @NamedQuery(name="idsNegociosHabilitados",
                query="SELECT n.id FROM Negocio n WHERE n.enabled = 1"),
        @NamedQuery(name="idsNegociosNoHabilitados",
                query="SELECT n.id FROM Negocio n WHERE n.enabled = 0")
})

public class Negocio implements Transferable<Negocio.Transfer> {
    
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
    private byte enabled;
   


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

    @Getter
    @AllArgsConstructor
	public static class Transfer {
                private long id;
                private String propietario;
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
                private byte enabled;
		
		public Transfer(Negocio n) {
                        this.id = n.getId();
                        this.propietario = n.getPropietario().getUsername();
                        this.nombre = n.getNombre();
                        this.descripcion = n.getDescripcion();
                        this.direccion = n.getDireccion();
                        this.ciudad = n.getCiudad();
                        this.provincia = n.getProvincia();
                        this.codigoPostal = n.getCodigoPostal();
                        this.aforoMaximo = n.getAforoMaximo();
                        this.telefono = n.getTelefono();
                    
                        this.latitud = n.getLatitud();
                        this.longitud = n.getLongitud();
                        this.enabled = n.getEnabled();
                        
		}
	}

	@Override
	public Transfer toTransfer() {
		return new Transfer(
                        id, propietario.getUsername(), nombre, descripcion, direccion, ciudad,
                        provincia, codigoPostal, aforoMaximo, telefono, latitud, longitud, enabled
                );
        }
}