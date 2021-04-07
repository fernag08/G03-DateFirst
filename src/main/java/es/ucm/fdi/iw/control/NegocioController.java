package es.ucm.fdi.iw.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Message;

import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Negocio;
import es.ucm.fdi.iw.model.Reserva;


/**
 * Negocio controller
 * 
 * @author mfreire
 */
@Controller()
@RequestMapping("negocio")
public class NegocioController {
	
	private static final Logger log = LogManager.getLogger(RootController.class);

	@Autowired
    private EntityManager entityManager;
    
    @Autowired
	private LocalData localData;

	@GetMapping("/")
	 public String getNegocio(Model model, HttpSession session) 			
	 		throws JsonProcessingException {		
	 	
	 	return "nuevoNegocio";
	}
	
	@PostMapping("/")
	@Transactional
	public String addNegocio(
			HttpServletResponse response, 
			Model model, HttpSession session,
			@RequestParam String nombre,
			@RequestParam String descripcion,
			@RequestParam String direccion,
			@RequestParam String ciudad,
			@RequestParam String provincia,
			@RequestParam String telefono,
			@RequestParam int codigoPostal,
			@RequestParam int aforoMaximo,
			@RequestParam int plazasDisponibles,
			 Model m) throws IOException {
				 
		Negocio n = new Negocio();
		model.addAttribute("n", n);

		entityManager.persist(n);
		entityManager.flush();

		User requester = (User)session.getAttribute("u");
		n.setNombre(nombre);
		n.setDescripcion(descripcion);
		n.setDireccion(direccion);
		n.setCiudad(ciudad);
		n.setProvincia(provincia);
		n.setTelefono(telefono);
		n.setAforoMaximo(aforoMaximo);
		n.setCodigoPostal(codigoPostal);
        n.setPlazasDisponibles(plazasDisponibles);
		n.setPropietario(requester); 
				
		session.setAttribute("n", n);
	     
	    return "redirect:/negocio/"+n.getId();
	}

	@GetMapping("/{id}/genera")
	@Transactional
	public String generaReservas(@PathVariable long id, Model model, HttpSession session) 
		throws JsonProcessingException {
		
		Negocio n = entityManager.find(Negocio.class, id);
		LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
		LocalDateTime end = start.plusHours(5);
		for (Reserva r : Reserva.generaReserva(start, end, 10, 30, 2, n)) {
			entityManager.persist(r);
		}
		entityManager.flush();

		return getNegocio(id, model, session);
	}

	@GetMapping("/{id}")
	@Transactional
    public String getNegocio(@PathVariable long id, Model model, HttpSession session) 			
	 		throws JsonProcessingException {		
	 	Negocio n = entityManager.find(Negocio.class, id);
		model.addAttribute("n", n);
			
		// pasa a la vista todas las reservas de ese negocio
		model.addAttribute("reservas", new ArrayList<>(n.getReservas()));

	 	return "vistaNegocio";
	}

	@PostMapping("/{id}")
	@Transactional
	public String postNegocio(
			HttpServletResponse response,
			@PathVariable long id, 
			@ModelAttribute Negocio edited, 
			Model model, HttpSession session) throws IOException 
    {
		Negocio target = entityManager.find(Negocio.class, id);
		model.addAttribute("n", target);

        User requester = (User)session.getAttribute("u");
		
        target.setNombre(edited.getNombre());
        target.setDireccion(edited.getDireccion());
        target.setCiudad(edited.getCiudad());
        target.setAforoMaximo(edited.getAforoMaximo());
		target.setCodigoPostal(edited.getCodigoPostal());
		target.setDescripcion(edited.getDescripcion());
        target.setDireccion(edited.getDireccion());
        target.setTelefono(edited.getTelefono());
        target.setProvincia(edited.getProvincia());
        target.setPlazasDisponibles(edited.getPlazasDisponibles());

		// update user session so that changes are persisted in the session, too
		session.setAttribute("n", target);

		return "vistaNegocio";
	}
}
