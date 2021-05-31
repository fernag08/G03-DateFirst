Feature: login con usuario normal

Background:
  # chromium bajo linux; 
  # si usas google-chrome, puedes quitar toda la parte de executable
  * configure driver = { type: 'chrome', showDriverLog: true }
    
  # descarga geckodriver de https://github.com/mozilla/geckodriver/releases para probar bajo firefox
  # * configure driver = { type: 'geckodriver', executable: './geckodriver', showDriverLog: true }

  # drivers que no he probado
  # * configure driver = { type: 'chrome', showDriverLog: true }
  # * configure driverTarget = { docker: 'justinribeiro/chrome-headless', showDriverLog: true }
  # * configure driverTarget = { docker: 'ptrthomas/karate-chrome', showDriverLog: true }
  # * configure driver = { type: 'chromedriver', showDriverLog: true }
  # * configure driver = { type: 'safaridriver', showDriverLog: true }
  # * configure driver = { type: 'iedriver', showDriverLog: true, httpConfig: { readTimeout: 120000 } }
  
Scenario: login using chrome

  Given driver 'http://localhost:8080/'
  * click("a[id=botonLogin]")
  * input('#username', 'Fernango99')
  * input('#password', 'aa')
  * submit().click("button[type=submit]")
  * match html('title') contains 'Perfil'
  * driver.screenshot()