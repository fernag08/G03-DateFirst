package es.ucm.fdi.iw.g03.date_first.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.JoinColumn;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Entity
@Data
public class Mensaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String mensaje;

    @OneToOne
    private Usuario emisor;
    @OneToOne
    private Usuario receptor;

}