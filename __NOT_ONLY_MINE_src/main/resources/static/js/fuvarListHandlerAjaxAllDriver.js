$(document).ready(function () {
    $("#searchAllDriver").click(getFuvarokByDateToTableAllDriver)
    $("#allDriver").click(getAllFuvarToTableAllDriver)
});

function getFuvarokByDateToTableAllDriver() {
    let datum = $("#datum").val();
    console.log(datum);

    $.ajax({
        url: "/fuvarok/filter",
        type: "GET",
        dataType: "json",
        contentType: "application/json",
        data: {date: datum, destination: ""},
        async: true,
        success: function (data) {
            $("#fuvar_data_table").html("");
            for (let i = 0; i < data.length; i++) {
                $("#fuvar_data_table").append(
                    $('<tr>').append(
                        $('<td>').text(data[i].indulasiIdoString),
                        $('<td>').text(data[i].indulasiHely),
                        $('<td>').text(data[i].uticel),
                        $('<td>').text(data[i].szabadHely + " / " + data[i].capacity),
                        $('<td><a href="/fuvar/ + ' + data[i].id + ' " class="btn btn-primary">Részletek</a></td>')
                    )
                );
            }
            console.log(data);
        }
    });
}

function getAllFuvarToTableAllDriver() {
    $.ajax({
        url: "/fuvarok/filter",
        type: "GET",
        dataType: "json",
        contentType: "application/json",
        data: {date: "", destination: ""},
        async: true,
        success: function (data) {
            $("#fuvar_data_table").html("");
            for (let i = 0; i < data.length; i++) {
                $("#fuvar_data_table").append(
                    $('<tr>').append(
                        $('<td>').text(data[i].indulasiIdoString),
                        $('<td>').text(data[i].indulasiHely),
                        $('<td>').text(data[i].uticel),
                        $('<td>').text(data[i].szabadHely + " / " + data[i].capacity),
                        $('<td><a href="/fuvar/ + ' + data[i].id + ' " class="btn btn-primary">Részletek</a></td>')
                    )
                );
            }
        }
    });
}