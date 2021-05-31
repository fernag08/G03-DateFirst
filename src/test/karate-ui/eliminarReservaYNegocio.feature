Feature: Login, acceso a perfil, crear un nuevo negocio, acceso al negocio y generar nuevas reservas para ese negocio

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
  * call read('crearNegocio.feature')

Scenario: search business and delete Bookings  using chrome
  * click("a[id=botonLogin]")
  * input('#username', 'Fernango99')
  * input('#password', 'aa')
  * submit().click("button[type=submit]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()

  # Click en titulo para ir a la pagina principal
  * click("a[id=index]")
  * match html('title') contains 'DateFirst'
  * driver.screenshot()

  # Busca un negocio
  * input('#criterio', 'Madrid')
  * select('#selectCriterio', 'city')
  * click("input[id=buscarNegocio]")
  * match html('title') contains 'Negocios buscados'
  * driver.screenshot()

  # Seleccionar negocio
  * click("a[id=negocio_3]")
  * match html('title') contains 'Negocio'
  * driver.screenshot()

  # Eliminar reservas
  * click("a[id=eliminarReservas]")
  * match html('title') contains 'Eliminar Reservas'
  * input('#Finicio', '30/05/2021')
  * input('#inicio', '20:00')
  * input('#Ffin', '31/05/2021')
  * input('#fin', '23:30')
  * click("button[id=eReserva]")
  * match html('title') contains 'Negocio'
  * driver.screenshot()

  # Eliminar negocio desde el perfil de usuario
  # voy al perfil si pulso en su foto
  * click("img[class=userthumb]")
  * match html('title') contains 'Perfil'
  * click("button[id=ne_3]")
  * match html('title') contains 'Perfil'
