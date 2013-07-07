var TOPLEFT = {};
var CURRENTBOX;

$(function() {
	bindToId("body");
});

// salvo la conf dei box
function savebox(sender) {
	var current = $(sender);

	var box = {
		left : current.position().left,
		top : current.position().top,
		width : current.width(),
		height : current.height()
	};

	url = BOX_URL;
	if (current.attr("id")) {
		url += "/" + current.attr("id");
	}

	$.ajax({
		url : url,
		type : "post",
		contentType : "application/json",
		data : JSON.stringify(box)
	}).done(function(box) {
		current.attr("id", box.id);
	});
}

function bindToId(name) {
	$(name).click(function(event) {
		console.log(event);
		if (TOPLEFT.x && TOPLEFT.y) {

			savebox(CURRENTBOX);

			// blocca quadrato
			CURRENTBOX = false;
			TOPLEFT = {};

			$(".box").click(function(event) {
				unbindToId(name);
				CURRENTBOX = $(this);
				alert("form aggiunta rss");
				return false;
			});

		} else {
			// set
			TOPLEFT.x = event.clientX;
			TOPLEFT.y = event.clientY;
			CURRENTBOX = $("<div>");
			CURRENTBOX.addClass("box");
			CURRENTBOX.css("top", TOPLEFT.y);
			CURRENTBOX.css("left", TOPLEFT.x);

			CURRENTBOX.appendTo("body");

			CURRENTBOX.draggable({
				stop : function() {
					savebox(this);
				}
			});
		}
	});

	$(".box").draggable({
		stop : function() {
			savebox(this);
		}
	});

	$(name).mousemove(function(event) {
		if (TOPLEFT.x && TOPLEFT.y && CURRENTBOX) {
			// disegna
			var width = event.clientX - TOPLEFT.x;
			var height = event.clientY - TOPLEFT.y;
			CURRENTBOX.css("width", width).css("height", height);
		}
	});
}

function unbindToId(name) {
	$(name).unbind('click');
	$(name).unbind('mousemove');
}