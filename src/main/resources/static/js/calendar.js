const c = {
    calendar: (lista, lista2, admin, userId, propId) => {
        console.log("proceso de creacion del calendario");
            
            var dias = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo']
            var mes = ['ENERO', 'FEBRERO', 'MARZO', 'ABRIL', 'MAYO', 'JUNIO', 'JULIO', 'AGOSTO', 'SEPTIEMBRE', 'OCTUBRE', 'NOVIEMBRE', 'DICIEMBRE']

            //creamos objeto fecha y calculamos los días que tiene el mes, al usar miFecha.getFullYear() ya tendremos correcto el año bisiesto
            var miFecha = new Date();
            var diasDelMes = (Date.UTC(miFecha.getFullYear(), miFecha.getMonth() + 1, 1) - Date.UTC(miFecha.getFullYear(), miFecha.getMonth(), 1)) / 1000 / 60 / 60 / 24;

            //creamos variables para usarlas como nodos y redimensionamos la tabla
            var th, tr, td, text;
            var table = document.getElementsByTagName("table")[0];

            //CAPTION como cabecera de la tabla, donde irá "MES AÑO"
            var caption = document.createElement("caption");
            var captionText = document.createTextNode(mes[miFecha.getMonth()] + " " + miFecha.getFullYear());
            caption.appendChild(captionText);
            table.appendChild(caption);

            //creamos los días de la tabla
            tr = document.createElement("tr");
            for (var i = 0; i < 7; i++) {
                th = document.createElement("th");
                text = document.createTextNode(dias[i]);
                th.appendChild(text);
                tr.appendChild(th);
            }
            table.appendChild(tr);

            //Creamos calendario, num serán los días y dateTemp una fecha temporal para saber por cual día empieza el mes miFecha.getMonth() del año miFecha.getFullYear() (para saber bisiestos)

            var num = 1;
            var dateTemp = new Date(miFecha.getFullYear() + '-' + (miFecha.getMonth() + 1) + '-1');

            //Quitamos el primer caracter y el ultimo del array, es decir, los corchetes
            lista = lista.substring(1);
            lista = lista.substring(0, lista.length - 1);

            lista2 = lista2.substring(1);
            lista2 = lista2.substring(0, lista2.length - 1);

            //Pasamos la cadena "lista" a un array
            let array = lista.split(', ');
            let array2 = lista2.split(', ');

            //Indice para recorrer todos los días del mes
            var posicion = 0;
            
            for (var i = 0; i < 5; i++) {
                tr = document.createElement("tr");
                for (var j = 1; j < 8; j++) {
                    let mes = dateTemp.getMonth() + 1;
                    td = document.createElement("td");
                    let n, m;

                    if (num < 10)
                        n = "0" + num;
                    else
                        n = num;

                    if ((miFecha.getMonth() + 1) < 10)
                        m = "0" + (miFecha.getMonth() + 1);
                    else
                        m = miFecha.getMonth() + 1;

                    td.id = "t"+ miFecha.getFullYear() + "-" + m + "-" + n;
                    console.log(td.id);

                    let isNotRealDay = (((j < dateTemp.getDay()) || (dateTemp.getDay() == 0 && j != 7)) && i == 0) ||
                        (num > diasDelMes);

                    let spanDia = document.createElement("span");
                    let spanTexto = document.createElement("span");
                    spanTexto.className = "spanTexto";

                    if (isNotRealDay) {
                        text = document.createTextNode("");
                    } else {
                        var spanContent;

                        if(array[posicion] == 0){
                            if(array2[posicion] != 0 && (userId == propId)){
                                spanContent = "Hay " + array2[posicion] + " reservas por revisar";
                                spanTexto.style.color = "#ffcf3d";
                            }
                            else{
                                spanContent = "No hay reservas";
                                spanTexto.style.color = "red";
                            }
                        }
                        else{
                            spanContent = "Reservas disponibles: " + array[posicion];
                            spanTexto.style.color = "green";
                        }

                        text = document.createTextNode((num++));
                        spanText = document.createTextNode(spanContent);
                        
                        spanDia.appendChild(text);
                        spanTexto.appendChild(spanText);
                        
                    
                        let condicion = (array[posicion] != 0 ) || ((userId == propId) && (array2[posicion] != 0)) || (admin == "true");
                        console.log (admin);
                        console.log (condicion);
                        if (condicion == true) {
                            td.addEventListener('click', (e) => {
                                
                                let idValido = e.target.closest("td").id.substring(1);
                                form = document.getElementById("formCalendar");
                                input = document.getElementById("fecha");
                                input.setAttribute("value", idValido);

                                form.submit();
                               
                            });
                        }
                       

                        posicion++;
                    }

                    td.appendChild(spanDia);
                    td.appendChild(spanTexto);
                    tr.appendChild(td);
                }
                table.appendChild(tr);
            }
            console.log("fin calendario");
    },

}