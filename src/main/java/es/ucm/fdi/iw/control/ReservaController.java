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
import es.ucm.fdi.iw.model.User.Role;


/**
 * User-administration controller
 * 
 * @author mfreire
 */
@Controller()
@RequestMapping("reserva")
public class ReservaController {
	
	private static final Logger log = LogManager.getLogger(RootController.class);

	@Autowired
    private EntityManager entityManager;
    
    @Autowired
	private LocalData localData;

	@GetMapping("/")
	 public String getReserva(Model model, HttpSession session) 			
	 		throws JsonProcessingException {		
	 	
	 	return "nuevaReserva";
	}

	@PostMapping("/{id}/cancelar")
	@Transactional
	public String cancelarReserva(
		HttpServletResponse response,
		@PathVariable long id, 
		Model model, HttpSession session) throws IOException {
	
		Reserva target = entityManager.find(Reserva.class, id);
		User requester = (User)session.getAttribute("u");

		if(!compruebaPropietario(requester, target)){		
			return "redirect:/user/"+ requester.getId();
		}

		model.addAttribute("r", target);

        target.setEstado(Reserva.Estado.CANCELADA);

		// update user session so that changes are persisted in the session, too
		session.setAttribute("r", target);
			

		return "redirect:/user/"+requester.getId();
	}
	
	@PostMapping("/{id}/habilitar")
	@Transactional
	public String habilitarReserva(
		HttpServletResponse response,
		@PathVariable long id, 
		Model model, HttpSession session) throws IOException {		
			
			Reserva target = entityManager.find(Reserva.class, id);
			User requester = (User)session.getAttribute("u");

			if (requester.getId() != target.getNegocio().getPropietario().getId() &&
				!requester.hasRole(Role.ADMIN)) {
			
				log.warn("El usuario " + requester.getUsername() + " esta intentando realizar una accion que no esta permitida en la reserva " + target.getId() + " del negocio " + target.getNegocio().getNombre() + " hecha por el usuario " + target.getUsuario().getUsername());

				return "redirect:/user/"+ requester.getId();
			}

			model.addAttribute("r", target);
			
			target.setNumPersonas(0);
			target.setEstado(Reserva.Estado.LIBRE);
			target.setUsuario(null);
	
			// update user session so that changes are persisted in the session, too
			session.setAttribute("r", target);

		return "redirect:/negocio/"+target.getNegocio().getId();
	}

	@GetMapping("/{id}")
	public String getReserva(@PathVariable long id, Model model, HttpSession session) 			
			throws JsonProcessingException {		
		
		Reserva r = entityManager.find(Reserva.class, id);
		model.addAttribute("r", r);

		// construye y envía mensaje JSON
		// User requester = (User)session.getAttribute("u");
		// ObjectMapper mapper = new ObjectMapper();
		// ObjectNode rootNode = mapper.createObjectNode();
		// rootNode.put("text", requester.getUsername() + " is looking up " + id);
		// String json = mapper.writeValueAsString(rootNode);
		
		// messagingTemplate.convertAndSend("/topic/admin", json);

		return "reserva";
	}


	@PostMapping("/{id}")
	@Transactional
	public String postReserva(
			HttpServletResponse response,
			@PathVariable long id, 
			@RequestParam int numPersonas, 
			Model model, HttpSession session) throws IOException {
		
		Reserva target = entityManager.find(Reserva.class, id);
		model.addAttribute("r", target);
		
		User requester = (User)session.getAttribute("u");

      
        target.setNumPersonas(numPersonas);
        target.setEstado(Reserva.Estado.SOLICITADA);
		target.setUsuario(requester);
		

		// update user session so that changes are persisted in the session, too
		session.setAttribute("r", target);

		return "redirect:/negocio/"+target.getNegocio().getId();
	}

	public boolean compruebaPropietario(User req, Reserva r){
		if (req.getId() != r.getUsuario().getId() &&
				!req.hasRole(Role.ADMIN)) {
			
			log.warn("El usuario " + req.getUsername() + " esta intentando realizar una accion que no esta permitida en la reserva " + r.getId() + " del negocio " + r.getNegocio().getNombre() + " hecha por el usuario " + r.getUsuario().getUsername());

			return false;
		}

		return true;
	}
}
