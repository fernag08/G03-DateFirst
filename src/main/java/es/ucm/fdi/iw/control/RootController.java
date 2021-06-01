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

	/*Te lleva a DateFirst.html */
    @GetMapping("/")            // <-- en qué URL se expone, y por qué métodos (GET)        
    public String index(        // <-- da igual, sólo para desarrolladores
            Model model)        // <-- hay muchos, muchos parámetros opcionales
    {
		model.addAttribute("activeProfiles", env.getActiveProfiles());
		model.addAttribute("basePath", env.getProperty("es.ucm.fdi.base-path"));
		model.addAttribute("debug", env.getProperty("es.ucm.fdi.debug"));
		model.addAttribute("negocios", entityManager.createQuery(
				"SELECT n FROM Negocio n").getResultList());

		log.info("Redirigiendo a la pagina principal...");
		
	 return "DateFirst";
	}

	/*Te lleva a negocioBuscado.html o el unico negocio encontrado segun el filtro de busqueda en DateFirst.html */
	@PostMapping("/buscar")              
	@Transactional 
    public String busca(        
            Model model,
			@RequestParam String consulta,
			@RequestParam String filtro)        
	{	
		List<Negocio> ln = null;

		log.info("Buscando negocios para la consulta {} con el filtro indicado...", consulta);

		try {
			if(filtro.equals("name")){
				ln = (List<Negocio>)entityManager.createNamedQuery(
				"negocioByNombre")
				.setParameter("negName", consulta).getResultList();
			}
			else if(filtro.equals("city")){
				ln = (List<Negocio>)entityManager.createNamedQuery(
			"negocioByCiudad")
			.setParameter("negCity", consulta).getResultList();
			}
			else if(filtro.equals("province")){
				ln = (List<Negocio>)entityManager.createNamedQuery(
			"negocioByProvincia")
			.setParameter("negProvince", consulta).getResultList();
			}
			else if(filtro.equals("postalCode")){
				
				ln = (List<Negocio>)entityManager.createNamedQuery(
				"negocioByCodigoPostal")
				.setParameter("negPostCode", consulta).getResultList();
			}

		}catch(Exception e){
			return "redirect:/";
		}
		
		if (ln.size() == 1) {
			log.info("Redirigiendo al unico negocio encontrado...");

			return "redirect:/negocio/" + ln.get(0).getId(); 
		}
		else {
			log.info("Redirigiendo a la lista de negocios resultado de la busqueda...");

			model.addAttribute("listNegocios", ln); 
			return "negocioBuscado"; 
		}
		
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


