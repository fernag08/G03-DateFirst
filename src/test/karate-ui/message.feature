Feature: login, acceso a perfil, ver mensajes + enviar y recibir un mensaje via WS

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
    
Scenario: login using chrome

  Given driver 'http://localhost:8080/login'
  * input('#username', 'Fernango99')
  * input('#password', 'aa')
  * submit().click("button[type=submit]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()

  # voy a mensajes si pulso en el buzon
  * click("a[id=received]")
  * match html('title') contains 'Mensajes'
  
  # envio un mensaje al propietario de un negocio (via ajax) con un número aleatorio
  * def mensaje = script("'el número secreto es el ' + Math.floor(Math.random() * 1000)")
  * input('#message', mensaje)
  # ojo: lo envío sin submit(), porque es ajax y no hay recarga de página
  * click("button[id=sendmsg]")
  # retardo de 500 ms para dar tiempo a que se envíe, y que el servidor responda via WS
  * delay(500) 
  * driver.screenshot()
  * click("button[id=botonLogout]")

  # login con el propietario para leer el resultado 
  * input('#username', 'Paulagarci2')
  * input('#password', 'aa')
  * submit().click("button[type=submit]")
  * match html('title') contains 'Perfil'

  # voy a mensajes si pulso en el buzon
  * click("a[id=received]")
  * match html('title') contains 'Mensajes'
  * match html('#datatable') contains mensaje
  * driver.screenshot()
  

  
