Feature: Registra un nuevo usuario

Background:
  # para escribir tus propias pruebas, lee https://github.com/intuit/karate/tree/master/karate-core
  # driver: chromium bajo linux; si usas google-chrome, puedes quitar executable (que es lo que usaría por defecto)
  * configure driver = { type: 'chrome', showDriverLog: true }
  * call read('crearNegocio.feature')

Scenario: new user using chrome
  # Crea el nuevo usuario
  * click("a[id=botonRegistro]")
  * match html('title') contains 'Registro'
  * input('#firstName', 'Antonio')
  * input('#lastName1', 'García')
  * input('#lastName2', 'Fernández')
  * input('#age', '35')
  * input('#tlf', '658958879')
  * input('#address', 'Calle de la Ciudadanía')
  * input('#city', 'Madrid')
  * input('#province', 'Madrid')
  * input('#postalCode', '28041')
  * input('#username', 'antoGarci')
  * input('#password', 'antogarci9')
  * input('#pass2', 'antogarci9')
  * click("button[id=btnCrearUser]")
  * match html('title') contains 'Login'
  * driver.screenshot()  

  # Inciar sesion con ese usuario
  * input('#username', 'antoGarci')
  * input('#password', 'antogarci9')
  * submit().click("button[type=submit]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()