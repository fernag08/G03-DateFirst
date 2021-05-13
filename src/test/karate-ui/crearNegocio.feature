Feature: Login, acceso a perfil, crear un nuevo negocio, acceso al negocio y generar nuevas reservas para ese negocio

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
  * call read('login.feature')
    
Scenario: new negocio using chrome

  # Crea el nuevo negocio
  * click("a[id=newNegocio]")
  * match html('title') contains 'Crear negocio'
  * input('#nombre', 'Bar El Portón')
  * input('#descripcion', 'Bar restaurante')
  * input('#direccion', 'Calle de Sáhara, 48, 28041 Madrid')
  * input('#ciudad', 'Madrid')
  * input('#provincia', 'Madrid')
  * input('#telefono', '912950851')
  * input('#codigoPostal', '28041')
  * input('#lat', '40.35439490729355')
  * input('#lon', '-3.6926154757038243')
  * input('#aforoMaximo', '45')
  * click("button[id=btnCrearNegocio]")
  * match html('title') contains 'Negocio'
  * driver.screenshot()

  # El usuario genera nuevas reservas
  * click("a[id=generarReservas]")
  * match html('title') contains 'Generar Reservas'
  * input('#Finicio', '15/03/2021')
  * input('#inicio', '20:00')
  * input('#Ffin', '15/03/2021')
  * input('#fin', '21:30')
  * input('#cuantas', '2')
  * input('#duracionEnMinutos', '30')
  * input('#capacidadEnCadaUna', '2')
  * click("button[id=btnGenerarReservas]")
  * match html('title') contains 'Negocio'
  * driver.screenshot()
  * click("button[id=botonLogout]")