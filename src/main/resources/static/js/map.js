const m = {	
    map: (latitud, longitud, nombre) => {
        let map;
        map = new google.maps.Map(document.getElementById('map'), {
            center: {
                lat: parseFloat(latitud),
                lng: parseFloat(longitud)
            },
            zoom: 16,
        });

        var marker = new google.maps.Marker({
            position: {
                lat: parseFloat(latitud),
                lng: parseFloat(longitud)
            },
            map: map,
            title: nombre
        });
    },
    
}