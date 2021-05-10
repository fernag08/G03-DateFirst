-- 
-- El contenido de este fichero se cargará al arrancar la aplicación, suponiendo que uses
-- 		application-default ó application-externaldb en modo 'create'
--

-- Usuario de ejemplo con username = a y contraseña = aa  
INSERT INTO USER(id, address, age, city, enabled, first_name, last_name1, last_name2, password, postal_code, province, roles, tlf , username) VALUES (
	1, 'Calle Bravo Murillo, 25',18,'Madrid',1,'Antonio','Rodriguez','Saez',
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u','28015','Madrid',
	'USER,ADMIN', '603458291', 'a'
);

-- Otro usuario de ejemplo con username = b y contraseña = aa  
INSERT INTO USER(id, address , age , city , enabled , first_name, last_name1 , last_name2 , password, postal_code, province, roles, tlf, username) VALUES (
	2, 'Calle de Bustamante, 6', 22, 'Madrid', 1, 'Maria', 'Gallego', 'Lopez',
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', '28045', 'Madrid',
    'USER', '678976568', 'b'
);
 
INSERT INTO NEGOCIO(id, aforo_maximo, ciudad, codigo_postal, descripcion, direccion, latitud, longitud, nombre, provincia, telefono,propietario_id) VALUES 
(1, 35, 'Madrid', 28009, 'Sushi Running es nuestro restaurante japones donde podras organizar
 desde eventos de negocios, familiares, o con amigos. No dudes en contactar con nosotros y prepararemos el menu
  que necesitas para que todo tu evento sea un exito.', 'Calle de Alcala, 110', '40.42555362512207', '-3.673552613736124', 'Sushi Running in Osaka', 'Madrid', '614563321', 2	
);

INSERT INTO NEGOCIO(id, aforo_maximo, ciudad, codigo_postal, descripcion, direccion, latitud, longitud, nombre, provincia, telefono,propietario_id) VALUES 
(2, 40, 'Madrid', 28015, 'Rio Nalon, Restaurante Argentino en Madrid, representa el concepto de Parrilla Argentina con caracter contemporaneo.
 Personalidad inconfundible y maestria a las brasas, con un punto de equilibrio, que siempre sorprende.',
  'Calle de Donoso Cortes, 8', '40.43616155031258', '-3.7054870463800778', 'Meson Restaurante Rio Nalon', 'Madrid', '656789675', 2	
);

<!--INSERT INTO RESERVA(id, capacidad, estado, fin, inicio, num_personas, ocupadas, negocio_id, usuario_id) VALUES (
	1, 35, 1, '2021-04-07 10:00:00.0','2021-04-07 11:00:00.0', 25, 1, 1, 1    
);-->

-- -- Unos pocos auto-mensajes de prueba
INSERT INTO MESSAGE VALUES(1,NULL,'2020-03-23 10:48:11.074000','probando 1',1,1,1);
INSERT INTO MESSAGE VALUES(2,NULL,'2020-03-23 10:48:15.149000','probando 2',1,1,1);
INSERT INTO MESSAGE VALUES(3,NULL,'2020-03-23 10:48:18.005000','probando 3',1,1,1);
INSERT INTO MESSAGE VALUES(4,NULL,'2020-03-23 10:48:20.971000','probando 4',1,1,1);
INSERT INTO MESSAGE VALUES(5,NULL,'2020-03-23 10:48:22.926000','probando 5',1,1,1);




