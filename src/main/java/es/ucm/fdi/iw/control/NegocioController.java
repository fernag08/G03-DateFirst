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
import java.util.*;
import java.util.stream.Collectors;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Transferable;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Negocio;
import es.ucm.fdi.iw.model.Reserva;
import es.ucm.fdi.iw.model.User.Role;


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

	// Este método se llama al pulsar en crear negocio desde el perfil de un usuario,
	// que te redirige al formulario para crear un nuevo negocio
	@GetMapping("/")
	 public String nuevoNegocio(Model model, HttpSession session) 			
	 		throws JsonProcessingException {	
				 
		log.info("Cargando el formulario para añadir un nuevo negocio...");
	 	
	 	return "nuevoNegocio";
	}

	/* Esta función se llama desde nuevoNegocio.html para la creación del nuevo negocio */
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

		log.info("Añadiendo un nuevo negocio...");

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
		n.setEnabled((byte)0);
			
		entityManager.persist(n);
		entityManager.flush();
		
		session.setAttribute("n", n);

		log.info("Negocio {} añadido correctamente", nombre);
	     
	    return "redirect:/negocio/"+n.getId();
	}
	
	/* Este metodo es llamado para mostrar un negocio que seleccionado desde la pagina principal de DateFirst*/
	@GetMapping("/{id}")
	@Transactional
    public String getNegocio(@PathVariable long id, Model model, HttpSession session) 			
	 		throws JsonProcessingException {		

	 	Negocio n = entityManager.find(Negocio.class, id);
		 
		Calendar fecha = new GregorianCalendar();
		int mes = fecha.get(Calendar.MONTH);
		int anyo = fecha.get(Calendar.YEAR);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		ArrayList dias = new ArrayList<>();
		
		mes = mes + 1;
		String m="";
		if(mes<10) // Para el formato LocalDateTime el mes debe tener un 0 delante si es menor que 10
			 m="0"+mes;
		else
			m=""+mes;

		LocalDateTime inicioP;
		LocalDateTime finP;
		int dia=1;
		String d="";
		
		for(int i=0;i<31;i++)
		{
			if(dia<10) // Para el formato LocalDateTime el dia debe tener un 0 delante si es menor que 10
				d="0"+dia;
			else 
				d=""+dia;

			inicioP = LocalDateTime.parse(anyo+"-"+m+"-"+d+" 00:00:00", formatter);
			finP = LocalDateTime.parse(anyo+"-"+m+"-"+d+" 23:59:59", formatter);
			
			// Esta consulta devuelve el número de reservas que hay en un día determinado
			// El array dias se irá rellenando con ese número de cada día del mes
			long lr = (Long)entityManager.createNamedQuery(
					"Reserva.delEsteDia")
					.setParameter("negocioBuscado", n)
					.setParameter("diaBuscadaIni", inicioP)
					.setParameter("diaBuscadaFin", finP)
					.getSingleResult();
			
			dias.add(lr);
			dia++;
		}

		model.addAttribute("disponiblesDia", dias);	

		model.addAttribute("n", n);

		log.info("Obteniendo la información del negocio {}...", id);

	 	return "negocio";
	}

	/*Es llamado tras editar un negocio */
	@PostMapping("/{id}")
	@Transactional
	public String postNegocio(
			@PathVariable long id, 
			@ModelAttribute Negocio edited, 
			Model model, HttpSession session) throws IOException 
    {

		Negocio target = entityManager.find(Negocio.class, id);
		User requester = (User)session.getAttribute("u");

		Calendar fecha = new GregorianCalendar();
		int mes = fecha.get(Calendar.MONTH);
		int anyo = fecha.get(Calendar.YEAR);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		ArrayList dias=new ArrayList<>();

		if(!compruebaPropietario(requester, target)){		
			return "redirect:/negocio/"+ target.getId();
		}

		log.info("Editando el negocio {}...", id);
		
		mes = mes + 1;
		String m="";
		if(mes<10)
			 m="0"+mes;
		else
			m=""+mes;

		LocalDateTime inicioP;
		LocalDateTime finP;
		int dia=1;
		String d="";
		
		for(int i=0;i<31;i++)
		{
			if(dia<10)
				d="0"+dia;
			else 
				d=""+dia;

			inicioP = LocalDateTime.parse(anyo+"-"+m+"-"+d+" 00:00:00", formatter);
			finP = LocalDateTime.parse(anyo+"-"+m+"-"+d+" 23:59:59", formatter);
			
			long lr = (Long)entityManager.createNamedQuery(
					"Reserva.delEsteDia")
					.setParameter("negocioBuscado", target)
					.setParameter("diaBuscadaIni", inicioP)
					.setParameter("diaBuscadaFin", finP)
					.getSingleResult();
			
			dias.add(lr);
			dia++;
		}

		model.addAttribute("disponiblesDia", dias);	

		model.addAttribute("n", target);
		
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

		// update user session so that changes are persisted in the session, too
		session.setAttribute("n", target);

		log.info("Negocio {} con id {} editado correctamente...", edited.getNombre(), id);

		return "negocio";
	}
	
	/*Se llama cuando pulsas en el botón de editar un negocio desde el perfil de ese negocio */
	@GetMapping("/{id}/editar")
	@Transactional
    public String editarNegocio(@PathVariable long id, Model model, HttpSession session) 			
	 		throws JsonProcessingException {		
	 	Negocio n = entityManager.find(Negocio.class, id);
		model.addAttribute("n", n);

		log.info("Cargando el formulario para editar el negocio {}...", id);

	 	return "editarNegocio";
	}
	
	/* Metodo llamado desde el perfil del usuario cuando quiere eliminar un negocio*/
	@PostMapping("/{id}/eliminar")
	@Transactional
	@ResponseBody
    public String eliminarNegocio(@PathVariable long id, Model model, HttpSession session) 			
	 		throws JsonProcessingException {	

		Negocio n = entityManager.find(Negocio.class, id);
		User requester = (User)session.getAttribute("u");

		if(!compruebaPropietario(requester, n)){		
			return "DateFirst";
		}

		log.info("Borrando el negocio con id {}...", id);

		ArrayList<Reserva> reservas = new ArrayList<Reserva>(n.getReservas());
	
		for (Reserva r : reservas){
			entityManager.remove(r);
		}
		
		entityManager.remove(n);
		entityManager.flush();
		
		User u = (User)session.getAttribute("u");
		
		log.info("Negocio con id {} borrado correctamente...", id);

		messagingTemplate.convertAndSend("/user/"+u.getUsername()+"/queue/updates");

		return "{\"id\": " + id + "}";
	}

	/*Es llamado cuando pinchas en el botón de generar reservas que se encuentra en negocio.html */
	@GetMapping("/{id}/genera")
	@Transactional
	public String generaReservas(@PathVariable long id, Model model, HttpSession session) 
		throws JsonProcessingException {

		Negocio n = entityManager.find(Negocio.class, id);
		model.addAttribute("n", n);

		log.info("Cargando el formulario para generar reservas para el negocio {}...", id);

		return "generarReservas";
	}

	/*Es llamado desde generarReservas cuando envías el formulario */
	@PostMapping("/{id}/genera")
	@Transactional
	public String postGeneraReservas(@PathVariable long id, Model model, 
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

		if(!compruebaPropietario(u, n)){		
			return "redirect:/negocio/"+ n.getId();
		}

		log.info("Generando reservas para el negocio {}...", id);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime inicioP = LocalDateTime.parse(Finicio+" "+inicio+":00", formatter);
		LocalDateTime finP = LocalDateTime.parse(Ffin+" "+fin+":00", formatter);

		for (Reserva r : Reserva.generaReserva(inicioP, finP, cuantas, duracionEnMinutos, capacidadEnCadaUna, n, u)) {
	 		entityManager.persist(r);
	 	}
		
	 	entityManager.flush();

		log.info("Reservas generadas correctamente para el negocio {}...", id);

	 	return "redirect:/negocio/"+n.getId();
	}

	/*Es llamado cuando pinchas en el botón de eliminar reservas que se encuentra en negocio.html */
	@GetMapping("/{id}/eliminarReservas")
	@Transactional
	public String eliminarReservas(@PathVariable long id, Model model, HttpSession session) 
		throws JsonProcessingException {

		Negocio n = entityManager.find(Negocio.class, id);
		model.addAttribute("n", n);

		log.info("Cargando el formulario para eliminar reservas del negocio {}...", id);

		return "eliminarReservas";
	}

	/*Se llama cuando envías el formulario de eliminarReservas para eliminarlas */
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

		if(!compruebaPropietario(u, n)){		
			return "redirect:/negocio/"+ n.getId();
		}

		log.info("Eliminando las reservas del negocio {}...", id);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

		LocalDateTime inicioP = LocalDateTime.parse(Finicio+" "+inicio+":00", formatter);
		LocalDateTime finP = LocalDateTime.parse(Ffin+" "+fin+":00", formatter);

		for (Reserva r : n.getReservas()) {
			if((r.getInicio().isBefore(finP) || r.getInicio().equals(finP)) && (r.getInicio().isAfter(inicioP) || r.getInicio().equals(inicioP)))
	 			entityManager.remove(r);
	 	}
		
	 	entityManager.flush();

		log.info("Reservas del negocio {} eliminadas correctamente...", id);

	 	return "redirect:/negocio/"+n.getId();
	}

	/*Obtiene la foto correspondiente al negocio, se llama desde DateFirst.html*/
	@GetMapping(value="/{id}/photo")
	public StreamingResponseBody getPhoto(@PathVariable long id, Model model) throws IOException {		
		File f = localData.getFile("negocio", ""+id+".jpg");
		InputStream in;
		if (f.exists()) {
			in = new BufferedInputStream(new FileInputStream(f));
		} else {
			in = new BufferedInputStream(getClass().getClassLoader()
					.getResourceAsStream("static/img/logo.png"));
		}
		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}

	/*Se encarga de actualizar la foto del negocio cuando le das a actualizar desde editarNegocio.html */
	@PostMapping("/{id}/photo")
	public String postPhoto(
			HttpServletResponse response,
			@RequestParam("photo") MultipartFile photo,
			@PathVariable long id, Model model, HttpSession session) throws IOException {
		
		Negocio n = entityManager.find(Negocio.class, id);
		model.addAttribute("n", n);

		User prop = n.getPropietario();
		
		// check permissions
		User requester = (User)session.getAttribute("u");

		if (!compruebaPropietario(requester,n)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, 
					"No eres administrador, y éste no es tu perfil");
				return "editarNegocio";
		}
		
		log.info("Updating photo for negocio {}", id);
		File f = localData.getFile("negocio", ""+id+".jpg");
		if (photo.isEmpty()) {
			log.info("failed to upload photo: empty file?");
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
	}

	// Este método comprueba que el usuario que quiere realizar una acción sobre el negocio es el propietario de ese negocio o el admin
	// Si es un usuario que no tiene permisos para realizar esa acción, se manda un aviso
	public boolean compruebaPropietario(User u, Negocio n){
		if (u.getId() != n.getPropietario().getId() &&
				!u.hasRole(Role.ADMIN)) {
			
			log.warn("El usuario " + u.getUsername() + " esta intentando realizar una accion que no esta permitida en el negocio " + n.getNombre());

			return false;
		}

		return true;
	}
	
}
