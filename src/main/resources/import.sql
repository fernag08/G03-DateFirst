-- 
-- El contenido de este fichero se cargará al arrancar la aplicación, suponiendo que uses
-- 		application-default ó application-externaldb en modo 'create'
--

-- Usuario de ejemplo con username = a y contraseña = aa  
INSERT INTO USER(id, address, age, city, enabled, first_name, last_name1, last_name2, password, postal_code, province, roles, tlf , username) VALUES (
	1, 'calle francisco S',18,'Madrid',1,'cam0','apel1','apel2',
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u','1526','Madrid',
	'USER,ADMIN', '606060606', 'a'
);

-- Otro usuario de ejemplo con username = b y contraseña = aa  
INSERT INTO USER(id, address , age , city , enabled , first_name, last_name1 , last_name2 , password, postal_code, province, roles, tlf, username) VALUES (
	2, 'Calle Loco', 22, 'Madrid', 1, 'Berta', 'Muestrez', 'Gallego',
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u', '29001', 'Madrid',
    'USER', '678976568', 'b'
);
 
INSERT INTO NEGOCIO(id, aforo_maximo, ciudad, codigo_postal, descripcion, direccion, latitud, longitud, nombre, provincia, telefono,propietario_id) VALUES 
(1, 35, 'Madrid', 40006, 'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium
 doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritati et quasi architecto
 beatae vitae dicta sunt explicabo.', 'Avenida Orovilla 58', 40.35804197541284, -3.6898172885699143, 'Comida Mexicana', 'Madrid', '614563321', 2	
);

INSERT INTO NEGOCIO(id, aforo_maximo, ciudad, codigo_postal, descripcion, direccion, latitud, longitud, nombre, provincia, telefono,propietario_id) VALUES 
(2, 40, 'Madrid', 40006, 'Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium
 doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritati et quasi architecto
 beatae vitae dicta sunt explicabo.', 'Calle Alcala 31', 40.41862270186089, -3.6983665022211523, 'Hamburguseria', 'Madrid', '656789675', 2	
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




