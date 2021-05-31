package es.ucm.fdi.iw.control;

import java.io.File;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Negocio;

/**
 * Admin-only controller
 * @author mfreire
 */
@Controller()
@RequestMapping("admin") 
public class AdminController {
	
	private static final Logger log = LogManager.getLogger(AdminController.class);
	
	@Autowired 
	private EntityManager entityManager;
	
	@Autowired
	private LocalData localData;
	
	@Autowired
	private Environment env;
	
	/* Este método es llamado cuando haces login o cuando pinchas en el botón de administrar */
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("activeProfiles", env.getActiveProfiles());
		model.addAttribute("basePath", env.getProperty("es.ucm.fdi.base-path"));
		model.addAttribute("debug", env.getProperty("es.ucm.fdi.debug"));

		model.addAttribute("users", entityManager.createQuery(
				"SELECT u FROM User u").getResultList());

		model.addAttribute("negocios", entityManager.createQuery(
			"SELECT n FROM Negocio n").getResultList());
		
		return "admin";
	}

	/* Método que deshabilita o habilita un usuario */
	@PostMapping("/toggleuser")
	@Transactional
	public String delUser(Model model,	@RequestParam long id) {
		User target = entityManager.find(User.class, id);
		if (target.getEnabled() == 1) {
			// remove profile photo
			File f = localData.getFile("user", ""+id);
			if (f.exists()) {
				f.delete();
			}
			// disable user
			target.setEnabled((byte)0); 
		} else {
			// enable user
			target.setEnabled((byte)1);
		}
		return index(model);
	}	


	// Igual que el método anterior pero para los negocios. Habilita o deshabilita un negocio.
	// Si deshabilitas un negocio desaparecerá para los usuarios que no sean el propietario.
	@PostMapping("/toggleBusiness")
	@Transactional
	public String delBusiness(Model model,	@RequestParam long id) {
		Negocio target = entityManager.find(Negocio.class, id);
		if (target.getEnabled() == 1) {
			
			// disable negocio
			target.setEnabled((byte)0); 
			
		} else {
			// enable negocio
			target.setEnabled((byte)1);
		}
		return index(model);
	}
}
