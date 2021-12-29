$(document).ready(function () {
    $("#searchAllPassanger").click(getFuvarokByDateToTableAllPassanger)
    $("#allPassanger").click(getAllFuvarToTableAllPassanger)
});

function getFuvarokByDateToTableAllPassanger() {
    let datum = $("#datum").val();
    console.log(datum);

    $.ajax({
        url: "/igenyek/filter",
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
                        $('<td>').text(data[i].utasokSzama),
                        $('<td><a href="/hirdetes/' + data[i].id + '" class="btn btn-primary">Részletek</a></td>')
                    )
                );
            }
            console.log(data);
        }
    });
}

function getAllFuvarToTableAllPassanger() {
    $.ajax({
        url: "/igenyek/filter",
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
                        $('<td>').text(data[i].utasokSzama),
                        $('<td><a href="/hirdetes/' + data[i].id + '" class="btn btn-primary">Részletek</a></td>')
                    )
                );
            }
        }
    });
}