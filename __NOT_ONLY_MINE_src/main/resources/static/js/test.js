/* Gombok és egyebek listener-jeit a document.ready-ben kötjük be */
$(document).ready(function () {

	/* Egy gomb ajax kérés után belerakja a labelbe a válaszul kapott adatot */
	$("#echoButton").click(function () {
		let message = $("#echoText").val();
		console.log(message);
		$.ajax({
			url: "/echo",
			type: "GET",
			dataType: "json",
			contentType: "application/json",
			async: true,
			data: {
				message: message
			},
			success: function (data) {
				$('#echoLabel').text(data.id + ": " + data.content);
			}
		});
	});

	/* Ugyanaz mint felül csak szépen kiszervezett függvénnyel */
	$("#listButton").click(getListToLabel)

	/* Elméletileg ez ugyn az, ha ez szimpatikusabb. */
	//getListToLabel |> $("#listButton").click

});

function getListToLabel() {
	$.ajax({
		url: "/list",
		type: "GET",
		dataType: "json",
		contentType: "application/json",
		async: true,
		success: function (data) {
			$('#listLabel').text("");
			for (let i = 0; i < data.length; i++) {
				$('#listLabel').append(data[i].id + " " + data[i].content + ", ");
			}
		}
	});
}
