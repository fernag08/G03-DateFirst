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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.script.*;

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
import es.ucm.fdi.iw.model.Transferable;
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

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

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
			@RequestParam String latitud,
			@RequestParam String longitud,
			@RequestParam String telefono,
			@RequestParam int codigoPostal,
			@RequestParam int aforoMaximo,
			 Model m) throws IOException {
				 
		Negocio n = new Negocio();
		model.addAttribute("n", n);

		User requester = (User)session.getAttribute("u");
		n.setNombre(nombre);
		n.setDescripcion(descripcion);
		n.setDireccion(direccion);
		n.setCiudad(ciudad);
		n.setProvincia(provincia);
		n.setTelefono(telefono);
		n.setAforoMaximo(aforoMaximo);
		n.setCodigoPostal(codigoPostal);
		n.setLatitud(latitud);
		n.setLongitud(longitud);
		n.setPropietario(requester);	
		entityManager.persist(n);
		entityManager.flush();
		session.setAttribute("n", n);
	     
	    return "redirect:/negocio/"+n.getId();
	}

	@GetMapping("/{id}")
	@Transactional
    public String getNegocio(@PathVariable long id, Model model, HttpSession session) 			
	 		throws JsonProcessingException {		
	 	Negocio n = entityManager.find(Negocio.class, id);
		model.addAttribute("n", n);
			
		// pasa a la vista todas las reservas de ese negocio
		model.addAttribute("reservas", new ArrayList<>(n.getReservas()));

	 	return "negocio";
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
		target.setLatitud(edited.getLatitud());
		target.setLongitud(edited.getLongitud());

		model.addAttribute("reservas", new ArrayList<>(target.getReservas()));

		// update user session so that changes are persisted in the session, too
		session.setAttribute("n", target);

		return "negocio";
	}

	@GetMapping("/{id}/editar")
	@Transactional
    public String editarNegocio(@PathVariable long id, Model model, HttpSession session) 			
	 		throws JsonProcessingException {		
	 	Negocio n = entityManager.find(Negocio.class, id);
		model.addAttribute("n", n);

	 	return "editarNegocio";
	}

	/* CAMILA
	
	@PostMapping("/eliminar")
	@Transactional
    public String eliminarNegocio(@RequestParam Long idOfTarge,  Model model, HttpSession session) 			
	 		throws JsonProcessingException {	

	 	Negocio n = entityManager.find(Negocio.class, idOfTarge);

		ArrayList<Reserva> reservas = new ArrayList<Reserva>(n.getReservas());
	
		for (Reserva r : reservas){
			entityManager.remove(r);
		}
		
		entityManager.remove(n);
		entityManager.flush();
		
		User u = (User)session.getAttribute("u");

		String nextUrl = u.hasRole(User.Role.ADMIN) ? 
		 	"admin/" :
		 	"user/" + u.getId();

		return "redirect:/"+nextUrl;
		
	}*/

	/*@PostMapping("/{id}/eliminar")
	@Transactional
	@ResponseBody // <-- "lo que devuelvo es la respuesta, tal cual"
    public String eliminarNegocio(@PathVariable long id, 
		@RequestBody JsonNode o, Model model, HttpSession session) 			
	 		throws JsonProcessingException {	

	 	Negocio n = entityManager.find(Negocio.class, id);
		ArrayList<Reserva> reservas = new ArrayList<Reserva>(n.getReservas());
		for (Reserva r : reservas){
			entityManager.remove(r);
		}
		entityManager.remove(n);
		entityManager.flush();
		User u = (User)session.getAttribute("u");

		//String nextUrl = u.hasRole(User.Role.ADMIN) ? 
		// 	"admin/" :
		// 	"user/" + u.getId();

		//return "redirect:/"+nextUrl;
		return "{\"result\": \"message sent.\"}";
	}*/

	@PostMapping("/{id}/eliminar")
	@ResponseBody
	@Transactional
    public String eliminarNegocio(@PathVariable long id, Model model, HttpSession session) 			
	 		throws JsonProcessingException {	

	 	Negocio n = entityManager.find(Negocio.class, id);

		ArrayList<Reserva> reservas = new ArrayList<Reserva>(n.getReservas());
	
		for (Reserva r : reservas){
			entityManager.remove(r);
		}
		
		entityManager.remove(n);
		entityManager.flush();
		
		User u = (User)session.getAttribute("u");

		// construye json
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		/*rootNode.put("from", sender.getUsername());
		rootNode.put("to", u.getUsername());
		rootNode.put("text", text);
		rootNode.put("id", m.getId());*/
		String json = mapper.writeValueAsString(rootNode); //crear json vacio??
		
		log.info("Borrando un negocio...", id, json);

		messagingTemplate.convertAndSend("/user/"+u.getUsername()+"/queue/updates", json);
		return "{ \"id\": " + id + "}";
	}

	@GetMapping(path = "/list", produces = "application/json")
	@Transactional // para no recibir resultados inconsistentes
	@ResponseBody  // para indicar que no devuelve vista, sino un objeto (jsonizado)
	public List<Negocio.Transfer> retrieveMessages(HttpSession session) {
		long userId = ((User)session.getAttribute("u")).getId();		
		User u = entityManager.find(User.class, userId);
		log.info("Generating negocios list for user {} ({} negocios)", 
				u.getUsername(), u.getNegocios().size());

		return  u.getNegocios().stream().map(Transferable::toTransfer).collect(Collectors.toList());
	}

	@GetMapping("/{id}/genera")
	@Transactional
	public String generaReservas(@PathVariable long id, Model model, HttpSession session) 
		throws JsonProcessingException {

		Negocio n = entityManager.find(Negocio.class, id);
		model.addAttribute("n", n);

		return "generarReservas";
	}

	@PostMapping("/{id}/genera")
	@Transactional
	public String postGeneraReservas(@PathVariable long id, Model model, // ECHAR UN OJO
					HttpSession session,
					@RequestParam String Finicio, 
					@RequestParam String inicio,
					@RequestParam String Ffin,
					@RequestParam String fin, 
					@RequestParam int cuantas,
					@RequestParam int duracionEnMinutos,
					@RequestParam int capacidadEnCadaUna) 
		throws JsonProcessingException {
		
		Negocio n = entityManager.find(Negocio.class, id);
		User u = (User)session.getAttribute("u");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime inicioP = LocalDateTime.parse(Finicio+" "+inicio+":00", formatter);
		LocalDateTime finP = LocalDateTime.parse(Ffin+" "+fin+":00", formatter);

		//LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
	 	//LocalDateTime end = start.plusHours(5);

		for (Reserva r : Reserva.generaReserva(inicioP, finP, cuantas, duracionEnMinutos, capacidadEnCadaUna, n, u)) {
	 		entityManager.persist(r);
	 	}
		
	 	entityManager.flush();

	 	return "redirect:/negocio/"+n.getId();
	}

	@GetMapping("/{id}/eliminarReservas")
	@Transactional
	public String eliminarReservas(@PathVariable long id, Model model, HttpSession session) 
		throws JsonProcessingException {

		Negocio n = entityManager.find(Negocio.class, id);
		model.addAttribute("n", n);

		return "eliminarReservas";
	}

	@PostMapping("/{id}/eliminarReservas")
	@Transactional
	public String postEliminaReservas(@PathVariable long id, Model model, // ECHAR UN OJO
					HttpSession session,
					@RequestParam String Finicio, 
					@RequestParam String inicio,
					@RequestParam String Ffin,
					@RequestParam String fin) 
		throws JsonProcessingException {
		
		Negocio n = entityManager.find(Negocio.class, id);
		User u = (User)session.getAttribute("u");

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime inicioP = LocalDateTime.parse(Finicio+" "+inicio+":00", formatter);
		LocalDateTime finP = LocalDateTime.parse(Ffin+" "+fin+":00", formatter);

		//LocalDateTime start = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
	 	//LocalDateTime end = start.plusHours(5);

		for (Reserva r : n.getReservas()) {
			if((r.getInicio().isBefore(finP) || r.getInicio().equals(finP)) && (r.getInicio().isAfter(inicioP) || r.getInicio().equals(inicioP)))
	 			entityManager.remove(r);
	 	}
		
	 	entityManager.flush();

	 	return "redirect:/negocio/"+n.getId();
	}

	/*@GetMapping(value="/{id}/photo")
	public StreamingResponseBody getPhoto(@PathVariable long id, Model model) throws IOException {		
		File f = localData.getFile("negocio", ""+id);
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			in = new BufferedInputStream(getClass().getClassLoader()
					.getResourceAsStream("static/img/unknown-user.jpg"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}

	@PostMapping("/{id}/photo")
	public String postPhoto(
			HttpServletResponse response,
			@RequestParam("photo") MultipartFile photo,
			@PathVariable("id") String id, Model model, HttpSession session) throws IOException {
		
		Negocio n = entityManager.find(Negocio.class, id);
		model.addAttribute("n", n);


		User target = entityManager.find(User.class, Long.parseLong(id));
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		if (requester.getId() != target.getId() &&
				! requester.hasRole(Role.ADMIN)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"No eres administrador, y éste no es tu perfil");
				return "editarNegocio";
		}
		
		log.info("Updating photo for negocio {}", id);
		File f = localData.getFile("negocio", id);
		if (photo.isEmpty()) {
			log.info("failed to upload photo: emtpy file?");
		} else {
			try (BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = photo.getBytes();
				stream.write(bytes);
			} catch (Exception e) {
				log.warn("Error uploading " + id + " ", e);
			}
			log.info("Successfully uploaded photo for {} into {}!", id, f.getAbsolutePath());
		}
		return "editarNegocio";
	}*/
	
}
