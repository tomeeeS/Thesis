$(document).ready(function () {
    $("#searchButton").click(sendSearchDTO)
});

function sendSearchDTO() {
    let advType = $("#advType").val();
    let startLat = $("#startLat").val();
    let startLng = $("#startLng").val();
    let startAddress = $("#startAddress").val();
    let startPlaceId = $("#startPlaceId").val();
    let destinationLat = $("#destinationLat").val();
    let destinationLng = $("#destinationLng").val();
    let destinationAddress = $("#destinationAddress").val();
    let destinationPlaceId = $("#destinationPlaceId").val();
    let startTime = $("#datum").val();
    let startHour = $("#ora").val();
    let startMinute = $("#perc").val();
    console.log(advType + ", " + startAddress + ", " + destinationAddress);

    // Float arrivalFelxibility;        //[hour]
    // Float maxDistanceAfterParting;   //[m]
    //Float startFelxibility;    		//[hour]
    //Float maxDistanceBeforePickUp; 	//[m]

    if (advType === "Sofőr") {
        $.ajax({
            url: "/fuvarok/search",
            type: "GET",
            dataType: "json",
            contentType: "application/json",
            data: {
                startDateString: startTime,
                startHour: startHour,
                startMinute: startMinute,
                startLat: startLat,
                startLng: startLng,
                startAddress: startAddress,
                startPlaceId: startPlaceId,
                destinationLat: destinationLat,
                destinationLng: destinationLng,
                destinationAddress: destinationAddress,
                destinationPlaceId: destinationPlaceId
            },
            async: true,
            success: function (data) {
                $("#thead_data").html("");
                $("#tbody_data").html("");
                $("#thead_data").append(
                    $('<tr>').append(
                        $('<th scope="col">').text("Indulás időpontja"),
                        $('<th scope="col">').text("Honnan"),
                        $('<th scope="col">').text("Hova"),
                        $('<th scope="col">').text("Szabad hely / Összes hely"),
                        $('<th scope="col">').text("Részletek")
                    )
                );
                for (let i = 0; i < data.length; i++) {
                    if (startAddress === "Telki, 2089 Magyarország") {
                        if (data[i].startAddress === "Telki, 2089 Magyarország") {
                            $("#tbody_data").append(
                                $('<tr>').append(
                                    $('<td>').text(data[i].indulasiIdoString),
                                    $('<td>').text(data[i].startAddress),
                                    $('<td>').text(data[i].destinationAddress),
                                    $('<td>').text(data[i].szabadHely + " / " + data[i].capacity),
                                    $('<td><a href="/fuvar/ + ' + data[i].id + ' " class="btn btn-primary">Részletek</a></td>')
                                )
                            );
                        }
                    }
                    if (destinationAddress === "Telki, 2089 Magyarország") {
                        if (data[i].destinationAddress === "Telki, 2089 Magyarország") {
                            $("#tbody_data").append(
                                $('<tr>').append(
                                    $('<td>').text(data[i].indulasiIdoString),
                                    $('<td>').text(data[i].startAddress),
                                    $('<td>').text(data[i].destinationAddress),
                                    $('<td>').text(data[i].szabadHely + " / " + data[i].capacity),
                                    $('<td><a href="/fuvar/ + ' + data[i].id + ' " class="btn btn-primary">Részletek</a></td>')
                                )
                            );
                        }
                    }
                }
                console.log(data);
            }
        });
    }
}