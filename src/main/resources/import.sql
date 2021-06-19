-- 
-- El contenido de este fichero se cargará al arrancar la aplicación, suponiendo que uses
-- 		application-default ó application-externaldb en modo 'create'
--

-- Usuario de ejemplo con username = admin y contraseña = aa  
INSERT INTO USER(id, address, age, city, enabled, first_name, last_name1, last_name2, password, postal_code, province, roles, tlf , username) VALUES (
	1, 'Calle Bravo Murillo, 25', 18, 'Madrid', 1, 'Antonio', 'Rodriguez', 'Saez',
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', '28015', 'Madrid',
	'USER,ADMIN', '603458291', 'admin'
);

-- Otro usuario de ejemplo con username = Fernango99 y contraseña = aa  
INSERT INTO USER(id, address , age , city , enabled , first_name, last_name1 , last_name2 , password, postal_code, province, roles, tlf, username) VALUES (
	2, 'Calle de Bustamante, 6', 22, 'Madrid', 1, 'Fernando', 'Gallego', 'Lopez',
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', '28045', 'Madrid',
    'USER', '678976568', 'Fernango99'
);

-- Otro usuario de ejemplo con username = Paulagarci2 y contraseña = aa  
INSERT INTO USER(id, address , age , city , enabled , first_name, last_name1 , last_name2 , password, postal_code, province, roles, tlf, username) VALUES (
	3, 'Calle Real, 2', 22, 'Valladolid', 1, 'Paula', 'Garcia', 'Ramos',
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', '21045', 'Valladolid',
    'USER', '684548978', 'Paulagarci2'
);

-- Otro usuario de ejemplo con username = cami y contraseña = aa  
INSERT INTO USER(id, address , age , city , enabled , first_name, last_name1 , last_name2 , password, postal_code, province, roles, tlf, username) VALUES (
	4, 'Calle Real, 2', 22, 'Madrid', 1, 'Camila', 'Perez', 'Moreno',
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', '21045', 'Valladolid',
    'USER', '45783597', 'cami'
);
 
INSERT INTO NEGOCIO(id, aforo_maximo, ciudad, codigo_postal, descripcion, direccion, enabled, latitud, longitud, nombre, provincia, telefono, propietario_id) VALUES 
(1, 35, 'Madrid', 28009, 'Sushi Running es nuestro restaurante japones donde podras organizar
 desde eventos de negocios, familiares, o con amigos. No dudes en contactar con nosotros y prepararemos el menu
  que necesitas para que todo tu evento sea un exito.', 'Calle de Alcala, 110', 1, '40.42555362512207', '-3.673552613736124', 'Sushi Running in Osaka', 'Madrid', '614563321', 3	
);

INSERT INTO NEGOCIO(id, aforo_maximo, ciudad, codigo_postal, descripcion, direccion, enabled, latitud, longitud, nombre, provincia, telefono, propietario_id) VALUES 
(2, 40, 'Madrid', 28015, 'Rio Nalon, Restaurante Argentino en Madrid, representa el concepto de Parrilla Argentina con caracter contemporaneo.
 Personalidad inconfundible y maestria a las brasas, con un punto de equilibrio que siempre sorprende a todo el que lo pruebe.',
  'Calle de Donoso Cortes, 8', 1, '40.43616155031258', '-3.7054870463800778', 'Meson Restaurante Rio Nalon', 'Madrid', '656789675', 3	
);

INSERT INTO NEGOCIO(id, aforo_maximo, ciudad, codigo_postal, descripcion, direccion, enabled, latitud, longitud, nombre, provincia, telefono, propietario_id) VALUES 
(3, 15, 'Madrid', 28001, 'Ya sea adornando el pelo con una horquilla con flores, llevando un panuelo a juego o presumiendo de bolso para
 salir, los accesorios de moda redondean cualquier look. Si necesitas renovar bolsos u otros accesorios, contactenos',
  'Calle del Conde de Aranda, 22, bajo derecha', 1, '40.40217667668157', '-3.8884685988487973', 'ES Fascinante', 'Madrid', '913369742', 2	
);

INSERT INTO NEGOCIO(id, aforo_maximo, ciudad, codigo_postal, descripcion, direccion, enabled, latitud, longitud, nombre, provincia, telefono, propietario_id) VALUES 
(4, 40, 'Madrid', 28015, 'Los Montes gallegos, gran seleccion, buen espacio de ambiente actual e intimo que sirve especialidades gallegas, 
ademas de cocteles y copas. De la sabia combinacion de estos productos resultan los exquisitos platos preparados, gracias a su calidad.',
  'Calle de Azcona, 46', 1, '40.43616155032637', '-3.7054870463806857', 'Los Montes gallegos', 'Madrid', '698547589', 2	
);

INSERT INTO NEGOCIO(id, aforo_maximo, ciudad, codigo_postal, descripcion, direccion, enabled, latitud, longitud, nombre, provincia, telefono, propietario_id) VALUES 
(5, 32, 'Madrid', 28038, 'El autentico sabor de Luisiana. La receta de nuestro pollo encuentra sus raices en la cocina cajun y criolla. 
El mejor sabor con influencias de las culturas espanola, francesa, italiana, britanica, alemana, africana y nativa americana.',
  'Av. de la Albufera, 20', 1, '40.39752183009516', '-3.6678742825609643', 'Popeyes Louisiana Kitchen', 'Madrid', '699342331', 1
);

INSERT INTO NEGOCIO(id, aforo_maximo, ciudad, codigo_postal, descripcion, direccion, enabled, latitud, longitud, nombre, provincia, telefono, propietario_id) VALUES 
(6, 10, 'Madrid', 28041, 'Disfruta de lo mejor de la cocina turca de todo Madrid pidiendo a domicilio al restaurante kebabish. 
Disfruta de lo mejor de la cocina turca al mejor precio. Aqui encontraras numerosos menus que se ajusten a tu gusto con combinaciones novedosas.', 'Carretera de Villaverde a Vallecas, 29', 1, '40.35803319519468', '-3.685741134378008440', 'Kebabish Villaverde', 'Madrid', '917729384', 3
);


-- Reservas de prueba generadas para el negocio 1
INSERT INTO RESERVA(id, capacidad, estado, fin, inicio, num_personas, negocio_id, usuario_id) VALUES (
	1, 2, 0, '2021-06-30 13:29:00','2021-06-30 12:59:00', 0, 1, null    
);
INSERT INTO RESERVA(id, capacidad, estado, fin, inicio, num_personas, negocio_id, usuario_id) VALUES (
	2, 2, 0, '2021-06-30 13:29:00','2021-06-30 12:59:00', 0, 1, null    
);
INSERT INTO RESERVA(id, capacidad, estado, fin, inicio, num_personas, negocio_id, usuario_id) VALUES (
	3, 2, 0, '2021-06-30 13:59:00','2021-06-30 13:29:00', 0, 1, null    
);
INSERT INTO RESERVA(id, capacidad, estado, fin, inicio, num_personas, negocio_id, usuario_id) VALUES (
	4, 2, 0, '2021-06-30 13:59:00','2021-06-30 13:29:00', 0, 1, null    
);


-- -- Unos pocos auto-mensajes de prueba
INSERT INTO MESSAGE VALUES(1,NULL,'2020-03-23 10:48:11.074000','probando 1',1,1,1);
INSERT INTO MESSAGE VALUES(2,NULL,'2020-03-23 10:48:15.149000','probando 2',1,1,1);
INSERT INTO MESSAGE VALUES(3,NULL,'2020-03-23 10:48:18.005000','probando 3',1,1,1);
INSERT INTO MESSAGE VALUES(4,NULL,'2020-03-23 10:48:20.971000','probando 4',1,1,1);
INSERT INTO MESSAGE VALUES(5,NULL,'2020-03-23 10:48:22.926000','probando 5',1,1,1);