Feature: Registra un nuevo usuario, selecciona un negocio y solicita una reserva

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
  * call read('registrarUsuario.feature')
    
Scenario: new reserva using chrome
  # Click en titulo para ir a la pagina principal
  * click("a[id=index]")
  * match html('title') contains 'DateFirst'
  * driver.screenshot()

  # Click sobre un negocio
  * click("a[id=negocio_3]")
  * match html('title') contains 'Negocio'
  * driver.screenshot()

  # Solicitar una reserva
  * click("a[id=reserva_1]")
  * match html('title') contains 'Solicitar Reserva'
  * input('#numPersonas', '3')
  * click("button[id=solicitar]")
  * match html('title') contains 'Negocio'
  * driver.screenshot()

  # Ver la reserva en el perfil de usuario (pulsando en la foto)
  * click("img[class=userthumb]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()