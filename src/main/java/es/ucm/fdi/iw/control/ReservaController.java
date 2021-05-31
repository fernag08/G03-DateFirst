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
import java.time.format.*;
import java.util.List;

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


@Controller()
@RequestMapping("reserva")
public class ReservaController {
	
	private static final Logger log = LogManager.getLogger(RootController.class);

	@Autowired
    private EntityManager entityManager;
    
    @Autowired
	private LocalData localData;


	/*Este metodo es llamado desde el negocio.html cuando pinchamos sobre un dia del calendario y nos lleva a listaReservas */
	@PostMapping("/listaReservas")
	@Transactional
	public String getListaReservas(
		HttpServletResponse response,
		@RequestParam String fecha, 
		@RequestParam long negocioBuscado, 
		Model model, HttpSession session) throws IOException {

			log.info("CONTROLER");
			Negocio n = entityManager.find(Negocio.class, negocioBuscado);

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

			log.warn("FECHA" + fecha);

			LocalDateTime inicioP = LocalDateTime.parse(fecha+" 00:00:00", formatter);
			LocalDateTime finP = LocalDateTime.parse(fecha+" 23:59:59", formatter);

			List<Reserva> lr = (List<Reserva>)entityManager.createNamedQuery(
					"Reserva.reservaByDia")
					.setParameter("negocioBuscado", n)
					.setParameter("diaBuscadaIni", inicioP)
					.setParameter("diaBuscadaFin", finP)
					.getResultList();

			model.addAttribute("listaR",lr);

			return "listaReservas";
	}

	

	/* Llamado desde el perfil del usuario cuando pincha sobre cancelar una reserva */
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

        target.setEstado(Reserva.Estado.CANCELADA);

		// update user session so that changes are persisted in the session, too
		session.setAttribute("r", target);
			

		return "redirect:/user/"+requester.getId();
	}
	
	/* Llamada por el propietario del negocio o el Admin para habilitar la reserva cancelada como reserva libre en el listaReserva.html*/
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

	
	/*Este metodo se encarga de cambiar el estado de una reserva a cancelada, es llamada desde la lista de reservas*/
	@PostMapping("/{id}/eliminar")
	@Transactional
	public String eliminarReserva(
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
			
			
			target.setEstado(Reserva.Estado.CANCELADA);
			
	
			// update user session so that changes are persisted in the session, too
			session.setAttribute("r", target);

		return "redirect:/negocio/"+target.getNegocio().getId();
	}
	
	/*Este metodo se encarga de cambiar el estado de una reserva a confirmada, es llamada desde la lista de reservas */
	@PostMapping("/{id}/confirmar")
	@Transactional
	public String confirmarReserva(
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
			
			target.setEstado(Reserva.Estado.CONFIRMADA);
	
			// update user session so that changes are persisted in the session, too
			session.setAttribute("r", target);

		return "redirect:/negocio/"+target.getNegocio().getId();
	}
	
	/* Llamada por el usuario clickeando boton solicitar para solicitar una reserva libre en listaReserva.html*/
	@GetMapping("/{id}")
	public String getReserva(@PathVariable long id, Model model, HttpSession session) 			
			throws JsonProcessingException {		
		
		Reserva r = entityManager.find(Reserva.class, id);
		model.addAttribute("r", r);

		return "reserva";
	}

	/*Se llama tras rellenar los datos de la reserva y pulsar sobre solicitar en reerva.html */
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

	// Este método comprueba que el usuario que quiere realizar una acción sobre una reserva es el usaurio logueado o el admin
	// Si es un usuario que no tiene permisos para realizar esa acción, se manda un aviso
	public boolean compruebaPropietario(User req, Reserva r){
		if (req.getId() != r.getUsuario().getId() &&
				!req.hasRole(Role.ADMIN)) {
			
			log.warn("El usuario " + req.getUsername() + " esta intentando realizar una accion que no esta permitida en la reserva " + r.getId() + " del negocio " + r.getNegocio().getNombre() + " hecha por el usuario " + r.getUsuario().getUsername());

			return false;
		}

		return true;
	}
}
