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
import java.util.ArrayList;
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
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
import es.ucm.fdi.iw.model.Negocio;
import es.ucm.fdi.iw.model.Reserva;
//import jdk.nashorn.internal.ir.debug.PrintVisitor;

/**
 * User-administration controller
 * 
 * @author mfreire
 */
@Controller()
@RequestMapping("user")
public class UserController {
	
	private static final Logger log = LogManager.getLogger(UserController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private LocalData localData;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	/**
	 * Tests a raw (non-encoded) password against the stored one.
	 * @param rawPassword to test against
 	 * @param encodedPassword as stored in a user, or returned y @see{encodePassword}
	 * @return true if encoding rawPassword with correct salt (from old password)
	 * matches old password. That is, true iff the password is correct  
	 */
	public boolean passwordMatches(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	/**
	 * Encodes a password, so that it can be saved for future checking. Notice
	 * that encoding the same password multiple times will yield different
	 * encodings, since encodings contain a randomly-generated salt.
	 * @param rawPassword to encode
	 * @return the encoded password (typically a 60-character string)
	 * for example, a possible encoding of "test" is 
	 * {bcrypt}$2y$12$XCKz0zjXAP6hsFyVc8MucOzx6ER6IsC1qo5zQbclxhddR1t6SfrHm
	 */
	public String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

    /* Crear un usuario nuevo cuando clickeas sobre boton registrar sin login en la barra de navegacion nav.html*/
	@GetMapping("/")
	 public String getUsuario(Model model, HttpSession session) 			
	 		throws JsonProcessingException {	
				 
		log.info("Cargando el formulario para añadir un nuevo usuario...");
	 	
	 	return "nuevoUsuario";
	}
	
	/* Se llama desde nuevoUsurio.html cuando insertamos un usuario*/
	@PostMapping("/")
	@Transactional
	public String addUsuario(
			HttpServletResponse response, 
			Model model, HttpSession session,
			@RequestParam String username,
			@RequestParam String password,
			@RequestParam String firstName,
			@RequestParam String lastName1,
			@RequestParam String lastName2,
			@RequestParam int age,
			@RequestParam String address,
			@RequestParam String tlf,
			@RequestParam String city,
			@RequestParam String province,
			@RequestParam String postalCode,
			 Model m) throws IOException {
				 
		User u = new User();
		model.addAttribute("user", u);

		log.info("Añadiendo un nuevo usuario con username {}...", username);

		u.setPassword(encodePassword(password));
		u.setUsername(username);
		u.setFirstName(firstName);
		u.setLastName1(lastName1);
		u.setLastName2(lastName2);
		u.setAge(age);
		u.setAddress(address);
		u.setTlf(tlf);
		u.setCity(city);
		u.setProvince(province);
		u.setPostalCode(postalCode);
		u.setRoles(Role.USER + "");
		u.setEnabled((byte)1);

		entityManager.persist(u);
		entityManager.flush();
		session.setAttribute("user", u);

		log.info("Usuario con username {} añadido correctamente, redirigiendo a login...", username);

	    return "login";
	}

    /*Para hacer la validacion del username en creacion de nuevo usuario en nuevoUsuario.html */
	@GetMapping("/username")
	@ResponseBody // <-- "lo que devuelvo es la respuesta, tal cual"
	public String getUsername(@RequestParam (required=false) String uname) {

		log.info("Obteniendo el numero de usuarios cuyo username es {}...", uname);

		long u = (Long)entityManager.createNamedQuery(
				"User.hasUsername")
				.setParameter("username", uname).getSingleResult();

		log.info("El numero de usuarios cuyo username es {} es {}...", uname, u);

		return "{ \"count\": " + u + "}";
	}
	
	/*Hace de intermediario para ir al perfil de usuario y se llama desde fragments/*/
	@GetMapping("/{id}")
	@Transactional
	public String getUser(@PathVariable long id, Model model, HttpSession session) 			
			throws JsonProcessingException {		
		User u = entityManager.find(User.class, id);

		log.info("Obteniendo la informacion del usuario con id {}...", id);

		model.addAttribute("user", u);
		model.addAttribute("negocios", new ArrayList<>(u.getNegocios()));
		model.addAttribute("reservas", new ArrayList<>(u.getReservas()));

		log.info("Informacion del usuario con id {} obtenida correctamente...", id);

		return "perfilUsuario";
	}	
	
	@ResponseStatus(
		value=HttpStatus.FORBIDDEN, 
		reason="No eres administrador, y éste no es tu perfil")  // 403
	public static class NoEsTuPerfilException extends RuntimeException {}

	@GetMapping("/{id}/editar")
	 public String editarUsuario(@PathVariable long id, Model model, HttpSession session) 			
	 		throws JsonProcessingException {		

		User u = entityManager.find(User.class, id);

		model.addAttribute("user", u);

		log.info("Cargando el formulario para editar el usuario con id {}...", id);

	 	return "editarUsuario";
	}

	@PostMapping("/{id}")
	@Transactional
	public String postUser(
			HttpServletResponse response,
			@PathVariable long id, 
			@ModelAttribute User edited,
			@RequestParam(required=false) String pass2, 
			Model model, HttpSession session) throws IOException 
    {
		
		User target = entityManager.find(User.class, id);
		User requester = (User)session.getAttribute("u");

		if(!compruebaPropietario(requester, target)){		
			return "redirect:/user/"+ requester.getId();
		}

		log.info("Editando el usuario con id {}...", id);

		model.addAttribute("u", target);
		model.addAttribute("negocios", new ArrayList<>(target.getNegocios()));
		model.addAttribute("reservas", new ArrayList<>(target.getReservas()));

		//target.setPassword(encodePassword(edited.getPassword()));
		target.setUsername(edited.getUsername());
        target.setFirstName(edited.getFirstName());
        target.setLastName1(edited.getLastName1());
		target.setLastName2(edited.getLastName2());
        target.setCity(edited.getCity());
		target.setPostalCode(edited.getPostalCode());
		target.setAge(edited.getAge());
		target.setAddress(edited.getAddress());
        target.setTlf(edited.getTlf());
        target.setProvince(edited.getProvince());

		if (edited.getPassword() != null && edited.getPassword().equals(pass2)) {
			// save encoded version of password
			target.setPassword(encodePassword(edited.getPassword()));
		}

		// update user session so that changes are persisted in the session, too
		entityManager.flush();
		session.setAttribute("u", target);

		log.info("Usuario con id {} editado correctamente, redirigiendo a su perfil...", id);

		return "perfilUsuario";
	}

	/* Es llamdo desde el perfil de usuario cuando se pula sobre eliminar cuenta */
	@PostMapping("/{id}/eliminar")
	@Transactional
    public String eliminarUsuario(@PathVariable long id, Model model, HttpSession session) 			
	 		throws JsonProcessingException {		
	 	User u = entityManager.find(User.class, id);
		User requester = (User)session.getAttribute("u");

		if(!compruebaPropietario(requester, u)){		
			return "DateFirst";
		}

		log.info("Eliminando el usuario con id {} y todos sus negocios y sus reservas...", id);

		ArrayList<Negocio> negocios = new ArrayList<Negocio>(u.getNegocios());
	
		for (Negocio n : negocios){
			ArrayList<Reserva> reservas = new ArrayList<Reserva>(n.getReservas());
	
			for (Reserva r : reservas){
				entityManager.remove(r);
			}
			
			entityManager.remove(n);
		}

		entityManager.remove(u);
		entityManager.flush();

		session.invalidate();
		SecurityContextHolder.clearContext();
		
		log.info("El usuario con id {} ha sido eliminado correctamente...", id);
		
		return "redirect:/";
	}
	
	@GetMapping(value="/{id}/photo")
	public StreamingResponseBody getPhoto(@PathVariable long id, Model model) throws IOException {		
		File f = localData.getFile("user", ""+id+".jpg");
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
	
	@PostMapping("/{id}/msg")
	@ResponseBody
	@Transactional
	public String postMsg(@PathVariable long id, 
			@RequestBody JsonNode o, Model model, HttpSession session) 
		throws JsonProcessingException {
		
		String text = o.get("message").asText();
		Negocio n=entityManager.find(Negocio.class, id);
		User u = entityManager.find(User.class, n.getPropietario().getId());
		User sender = entityManager.find(
				User.class, ((User)session.getAttribute("u")).getId());
		model.addAttribute("user", u);
		
		// construye mensaje, lo guarda en BD
		Message m = new Message();
		m.setRecipient(u);
		m.setSender(sender);
		m.setDateSent(LocalDateTime.now());
		m.setText(text);
		m.setNegocio(n.getNombre());
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit
		
		// construye json
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("from", sender.getUsername());
		rootNode.put("to", u.getUsername());
		rootNode.put("text", text);
		rootNode.put("id", m.getId());
		rootNode.put("negocio",m.getNegocio());
		String json = mapper.writeValueAsString(rootNode);
		
		log.info("Sending a message to {} with contents '{}'", id, json);

		messagingTemplate.convertAndSend("/user/"+u.getUsername()+"/queue/updates", json);
		return "{\"result\": \"message sent.\"}";
	}	

	@PostMapping("/{id}/resMsg")
	@ResponseBody
	@Transactional
	public String postMsgUser(@PathVariable long id, 
			@RequestBody JsonNode o, Model model, HttpSession session) 
		throws JsonProcessingException {
		
		String text = o.get("message").asText();
		User u = entityManager.find(User.class, id);
		User sender = entityManager.find(
				User.class, ((User)session.getAttribute("u")).getId());
		model.addAttribute("user", u);
		
		// construye mensaje, lo guarda en BD
		Message m = new Message();
		m.setRecipient(u);
		m.setSender(sender);
		m.setDateSent(LocalDateTime.now());
		m.setText(text);
		m.setNegocio("");
		entityManager.persist(m);
		entityManager.flush(); // to get Id before commit
		
		// construye json
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode rootNode = mapper.createObjectNode();
		rootNode.put("from", sender.getUsername());
		rootNode.put("to", u.getUsername());
		rootNode.put("text", text);
		rootNode.put("id", m.getId());
		//rootNode.put("negocio",null);
		String json = mapper.writeValueAsString(rootNode);
		
		log.info("Sending a message to {} with contents '{}'", id, json);

		messagingTemplate.convertAndSend("/user/"+u.getUsername()+"/queue/updates", json);
		return "{\"result\": \"message sent.\"}";
	}	
	
	@PostMapping("/{id}/photo")
	public String postPhoto(
			HttpServletResponse response,
			@RequestParam("photo") MultipartFile photo,
			@PathVariable("id") String id, Model model, HttpSession session) throws IOException {
		
		User target = entityManager.find(User.class, Long.parseLong(id));
		model.addAttribute("user", target);
		
		// check permissions
		User requester = (User)session.getAttribute("u");
		
		if(!compruebaPropietario(requester, target)){		
			return "redirect:/user/"+ requester.getId();
		}
		
		log.info("Updating photo for user {}", id);
		File f = localData.getFile("user", id+".jpg");
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
			log.info("Successfully uploaded photo for {} into {}", id, f.getAbsolutePath());
		}
		return "editarUsuario";
	}

	public boolean compruebaPropietario(User req, User u){
		if (req.getId() != u.getId() &&
				!req.hasRole(Role.ADMIN)) {
			
			log.warn("El usuario " + req.getUsername() + " esta intentando realizar una accion que no esta permitida con el usuario " + u.getUsername());

			return false;
		}

		return true;
	}

}
