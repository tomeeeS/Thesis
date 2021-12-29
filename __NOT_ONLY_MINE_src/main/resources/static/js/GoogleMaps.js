// This example adds a search box to a map, using the Google Place Autocomplete
// feature. People can enter geographical searches. The search box will return a
// pick list containing a mix of places and predicted search terms.
// This example requires the Places library. Include the libraries=places
// parameter when you first load the API. For example:
// <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY&libraries=places">
function initAutocomplete() {
    const map = new google.maps.Map(document.getElementById("map"), {
        center: {lat: 47.497913, lng: 19.040236},
        zoom: 13,
        mapTypeId: "roadmap",
    });
    // Create the search box and link it to the UI element.
    const input1 = document.getElementById("pac-input1");
    const input2 = document.getElementById("pac-input2");
    const searchBox1 = new google.maps.places.SearchBox(input1);
    const searchBox2 = new google.maps.places.SearchBox(input2);
    const geocoder = new google.maps.Geocoder();
    const telkiPlaceId = "ChIJC01pqgZ0akcRgKYeDCnEAAQ";
    map.controls[google.maps.ControlPosition.TOP_CENTER].push(input1);
    map.controls[google.maps.ControlPosition.BOTTOM_CENTER].push(input2);
    // Bias the SearchBox results towards current map's viewport.
    map.addListener("bounds_changed", () => {
        searchBox1.setBounds(map.getBounds());
        searchBox2.setBounds(map.getBounds());
    });
    let markers = [];
    // Listen for the event fired when the user selects a prediction and retrieve
    // more details for that place.
    searchBox1.addListener("places_changed", () => {
        const places = searchBox1.getPlaces();

        if (places.length == 0) {
            return;
        }
        // Clear out the old markers.
        markers.forEach((marker) => {
            marker.setMap(null);
        });
        markers = [];
        // For each place, get the icon, name and location.
        const bounds = new google.maps.LatLngBounds();
        places.forEach((place) => {
            if (!place.geometry) {
                console.log("Returned place contains no geometry");
                return;
            }
            const icon = {
                url: place.icon,
                size: new google.maps.Size(71, 71),
                origin: new google.maps.Point(0, 0),
                anchor: new google.maps.Point(17, 34),
                scaledSize: new google.maps.Size(25, 25),
            };
            // Create a marker for each place.
            markers.push(
                new google.maps.Marker({
                    map,
                    icon,
                    title: place.name,
                    position: place.geometry.location,
                })
            );

            if (place.geometry.viewport) {
                // Only geocodes have viewport.
                bounds.union(place.geometry.viewport);
            } else {
                bounds.extend(place.geometry.location);
            }

            document.getElementById("startAddress").value = place.formatted_address;
            document.getElementById("startLat").value = place.geometry.location.lat();
            document.getElementById("startLng").value = place.geometry.location.lng();
            document.getElementById("startPlaceID").value = place.place_id;

            geocoder.geocode({ placeId: telkiPlaceId}, (results, status) => {
                var telkiPlace = results[0];
                document.getElementById("pac-input2").value = telkiPlace.formatted_address;
                document.getElementById("destinationAddress").value = telkiPlace.formatted_address;
                document.getElementById("destinationLat").value = telkiPlace.geometry.location.lat();
                document.getElementById("destinationLng").value = telkiPlace.geometry.location.lng();
                document.getElementById("destinationPlaceID").value = telkiPlace.place_id;
            });
        });
        map.fitBounds(bounds);
    });

    searchBox2.addListener("places_changed", () => {
        const places = searchBox2.getPlaces();

        if (places.length == 0) {
            return;
        }
        // Clear out the old markers.
        markers.forEach((marker) => {
            marker.setMap(null);
        });
        markers = [];
        // For each place, get the icon, name and location.
        const bounds = new google.maps.LatLngBounds();
        places.forEach((place) => {
            if (!place.geometry) {
                console.log("Returned place contains no geometry");
                return;
            }
            const icon = {
                url: place.icon,
                size: new google.maps.Size(71, 71),
                origin: new google.maps.Point(0, 0),
                anchor: new google.maps.Point(17, 34),
                scaledSize: new google.maps.Size(25, 25),
            };
            // Create a marker for each place.
            markers.push(
                new google.maps.Marker({
                    map,
                    icon,
                    title: place.name,
                    position: place.geometry.location,
                })
            );

            if (place.geometry.viewport) {
                // Only geocodes have viewport.
                bounds.union(place.geometry.viewport);
            } else {
                bounds.extend(place.geometry.location);
            }

            document.getElementById("destinationAddress").value = place.formatted_address;
            document.getElementById("destinationLat").value = place.geometry.location.lat();
            document.getElementById("destinationLng").value = place.geometry.location.lng();
            document.getElementById("destinationPlaceID").value = place.place_id;

            geocoder.geocode({ placeId: telkiPlaceId}, (results, status) => {
                var telkiPlace = results[0];
                document.getElementById("pac-input1").value = telkiPlace.formatted_address;
                document.getElementById("startAddress").value = telkiPlace.formatted_address;
                document.getElementById("startLat").value = telkiPlace.geometry.location.lat();
                document.getElementById("startLng").value = telkiPlace.geometry.location.lng();
                document.getElementById("startPlaceID").value = telkiPlace.place_id;
            });
        });
        map.fitBounds(bounds);
    });
}