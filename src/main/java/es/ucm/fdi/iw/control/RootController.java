package es.ucm.fdi.iw.control;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import javax.persistence.EntityManager;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Negocio;
import es.ucm.fdi.iw.model.Reserva;

/**
 * Landing-page controller
 * 
 * @author mfreire
 */
@Controller
public class RootController {
	
	private static final Logger log = LogManager.getLogger(RootController.class);

	@Autowired
    private EntityManager entityManager;

	
	@Autowired
	private LocalData localData;

	@Autowired
	private Environment env;

    @GetMapping("/")            // <-- en qué URL se expone, y por qué métodos (GET)        
    public String index(        // <-- da igual, sólo para desarrolladores
            Model model)        // <-- hay muchos, muchos parámetros opcionales
    {
		model.addAttribute("activeProfiles", env.getActiveProfiles());
		model.addAttribute("basePath", env.getProperty("es.ucm.fdi.base-path"));
		model.addAttribute("debug", env.getProperty("es.ucm.fdi.debug"));
		model.addAttribute("negocios", entityManager.createQuery(
				"SELECT n FROM Negocio n").getResultList());
	 return "DateFirst";
	}

	@PostMapping("/buscar")            // <-- en qué URL se expone, y por qué métodos (GET)       
	@Transactional 
    public String busca(        // <-- da igual, sólo para desarrolladores
            Model model,
			@RequestParam String question)        // <-- hay muchos, muchos parámetros opcionales
    { 
		

		Negocio n = null;
		try {
			n = (Negocio)entityManager.createNamedQuery(
			"negocioByNombre")
			.setParameter("negName", question).getSingleResult();
			
		}catch(Exception e){
			return "redirect:/";
		}
		return "redirect:/negocio/"+n.getId();
	}

	
	@GetMapping("/chat")
	public String chat(Model model, HttpServletRequest request) {
		return "chat";
	}
	
	@GetMapping("/error")
	public String error(Model model) {
		return "error";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
}


