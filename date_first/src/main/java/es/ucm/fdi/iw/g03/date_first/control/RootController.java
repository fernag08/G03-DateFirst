package es.ucm.fdi.iw.g03.date_first.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;

import es.ucm.fdi.iw.g03.date_first.model.Usuario;
import es.ucm.fdi.iw.g03.date_first.model.Negocio;
import es.ucm.fdi.iw.g03.date_first.model.Reserva;


@Controller
public class RootController {

    @Autowired
    private EntityManager entityManager;

    @GetMapping("/")            // <-- en qué URL se expone, y por qué métodos (GET)        
    public String index(        // <-- da igual, sólo para desarrolladores
            Model model)        // <-- hay muchos, muchos parámetros opcionales
    {
        return "DateFirst";
    }

    @GetMapping("/usuario")
    public String usuario(Model model) //@PathVariable String id,
    {
       	Usuario u = entityManager.getReference(Usuario.class, 1);

       	model.addAttribute("u", u);
        
       	return "perfilUsuario";
    }

    @GetMapping("/negocio")                  
    public String negocio(Model model)
    {
        Negocio n = entityManager.getReference(Negocio.class, 1);

       	model.addAttribute("n", n);

        return "vistaNegocio";
    }

    
   /* @GetMapping("/administrador")                  
    public String administrador(          
            Model model)        
    {
        
        return "vistaAdmin";
    }*/

    @GetMapping("/reserva")                  
    public String reserva(         
            Model model)        
    {
		Reserva r = entityManager.getReference(Reserva.class, 1);

       	model.addAttribute("r", r);

        return "vistaReserva";
    }

}