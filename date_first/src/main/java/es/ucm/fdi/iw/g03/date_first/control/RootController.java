package es.ucm.fdi.iw.g03.date_first.control;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
//import org.springframework.ui.ModelMap;
//import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/*import es.ucm.fdi.iw.g03.date_first.model.Administrador;
import es.ucm.fdi.iw.g03.date_first.model.Usuario;
import es.ucm.fdi.iw.g03.date_first.model.Propietario;
import es.ucm.fdi.iw.g03.date_first.model.Negocio;
import es.ucm.fdi.iw.g03.date_first.model.Reserva;*/

@Controller
public class RootController {

    @Autowired
	private EntityManager entityManager;

    private static final Logger log = LogManager.getLogger(RootController.class);

    // @PostMapping("/addUsuario1")
	// @Transactional 
	// public String addUsuario1(
	// 		@RequestParam String nombreUsu,
	// 		@RequestParam String contrasena,
    //         @RequestParam String nombre,
    //         @RequestParam String apellido1,
    //         @RequestParam String apellido2,
    //         @RequestParam int edad,
    //         @RequestParam int telefono,
    //         @RequestParam String direccion,
    //         @RequestParam String ciudad,
    //         @RequestParam String provincia,
    //         @RequestParam int codigoPostal, Model m) {
	// 	Usuario user = new Usuario();
       
	// 	user.setNombreUsu(nombreUsu);
    //     user.setContrasena(contrasena);
    //     user.setNombre(nombre);
    //     user.setApellido1(apellido1);
    //     user.setApellido2(apellido2);
    //     user.setEdad(edad);
    //     user.setTelefono(telefono);
    //     user.setDireccion(direccion);
    //     user.setCiudad(ciudad);
    //     user.setProvincia(provincia);
    //     user.setCodigoPostal(codigoPostal);

	// 	entityManager.persist(user);
	     
	//     //Do Something
	//     return dump(m);
	// }


    @PostMapping("/")
	@Transactional
	public String mod(Model model, 
			@RequestParam String tableName, 
			@RequestParam(required=false) Long id, 
			HttpServletRequest request) {
			
		if (id >= 0) {
			boolean isNewObject = (id == 0);
			Object o = isNewObject ? 
					newObjectByName(tableName) : 
					existingObjectById(tableName, id);
			
			for (Map.Entry<String, String[]> e : request.getParameterMap().entrySet()) {
				if (e.getKey().equals("tableName|id")) {
					continue; // not a valid field name
				}
				setObjectProperty(o, e.getKey(), String.join(",", e.getValue()));		
			}
			if (isNewObject) {
				entityManager.persist(o); // tells the entityManager to actively manage this object
			}
		} else {
			// I am using negative numbers to erase stuff. So id==10 modifies #10, id==-10 deletes it.
			entityManager.remove(existingObjectById(tableName, -id));
		}

		entityManager.flush();    // make the change immediately visible (so 'dump()' can see it)
		return dump(model);
	}

	@GetMapping("/")
	public String dump(Model model) {
		// list of all Objects to scan. 
		for (String tableName : "Usuario Propietario Negocio Reserva".split(" ")) {
			// queries all objects
			List<?> results = entityManager.createQuery(
					"select x from " + tableName + " x").getResultList();
			
			// dumps them via log
			log.info("Dumping table {}", tableName);
			for (Object o : results) {
				log.info("\t{}", o);
			}
			
			// adds them to model
			model.addAttribute(tableName, results);
			// adds id-to-text map to model, too
			Map<String, String> idsToText = new HashMap<>();
			for (Object o : results) {
				idsToText.put(getObjectId(o), o.toString());
			}
			model.addAttribute(tableName+"Map", idsToText);
		}
				
		return "dump";
	}

    private Object existingObjectById(String className, long id) {
		try {
			Class<?> clazz = getClass().getClassLoader().loadClass(className); 
			return entityManager.find(clazz, id);
		} catch (Exception e) {
			log.warn("Error retrieving object of class " + className + " with ID " + id, e);
			return null;
		}
	}

    /**
	 * Sets any property of an object.
	 * @param o object to write to
	 * @param propertyName to use. For references, use '_id' at the end. 
	 * @param propertyValue to use. For references, use the id(s) of the object(s) to reference
	 *     Only knows how to handle a few literals. To add more, convert them from String 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setObjectProperty(Object o, String propertyName, String propertyValue) {
		boolean ok = true;
		try {
			Class<?> clazz = o.getClass();
			if ("tableName".equals(propertyName)) {
				return;
			}
			if (propertyName.endsWith("_id")) {
				propertyName = propertyName.substring(
						0, propertyName.length()-"_id".length()); // ignore the trailing '_id'
				Field f = o.getClass().getDeclaredField(propertyName);
				if (List.class.isAssignableFrom(f.getType())) {
					// add a list of references
					Method getter = getAccessor(clazz, true, propertyName);
					Class<?> inner = getter.getAnnotation(OneToMany.class) != null ?
							getter.getAnnotation(OneToMany.class).targetEntity() : 
							getter.getAnnotation(ManyToMany.class).targetEntity();
					List list = (List)getter.invoke(o);
					list.clear(); // remove previous values
					for (String id : propertyValue.split(",")) {
						list.add(entityManager.find(inner, 
								Long.parseLong(id))); 
					}
				} else {
					// set one reference
					Method setter = getAccessor(clazz, false, propertyName);
					setter.invoke(o, entityManager.find(f.getType(), 
							Long.parseLong(propertyValue))); 
				}
			} else {
				// set a literal value
				Method setter = getAccessor(clazz, false, propertyName);
				Class<?> type = setter.getParameters()[0].getType();
				if (type.equals(String.class)) {
					setter.invoke(o, propertyValue);
				} else if (type.isPrimitive()) {
					// rely on Spring - as per https://stackoverflow.com/a/15973019/15472
					PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(o);
					accessor.setPropertyValue(propertyName, propertyValue);
				//} else if (type.isEnum()) {
				//	setter.invoke(o, Wheel.Position.valueOf(propertyValue));
				}
                 else {
					throw new UnsupportedOperationException("do not know how to set a " + type);
				}
			}
		} catch (Exception e) {
			log.warn("Error setting property {} to {} in a {}", propertyName, 
					propertyValue, o.getClass().getSimpleName());
			log.warn("... exception was:",  e);
			ok = false;
		}
		if (ok) {
			log.info("Correctly set property {} to {} in a {}", propertyName, 
				propertyValue, o.getClass().getSimpleName());
		}
	}

    private Method getAccessor(Class<?> clazz, boolean read, String propertyName) throws Exception {
		for (PropertyDescriptor prop : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
			if (prop.getName().equals(propertyName)) {
				return read ? prop.getReadMethod() : prop.getWriteMethod();
			}
		}
		throw new IllegalArgumentException(
				"No " + (read?"read":"write") + " accessor for " 
						+ propertyName + " in " + clazz.getSimpleName());
	}
	
	private Object newObjectByName(String className) {
		try {
			Class<?> clazz = getClass().getClassLoader().loadClass(className);
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			log.warn("Error instantiating object of class " + className, e);
			return null;
		}
	}

    private String getObjectId(Object o) {
		try {
			Field f = o.getClass().getDeclaredField("id");
			f.setAccessible(true);
			return ""+f.get(o);
		} catch (Exception e) {
			log.warn("Error retrieving id of class " + o.getClass().getSimpleName(), e);
			return null;
		}
	}
    

}