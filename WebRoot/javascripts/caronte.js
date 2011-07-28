/*
*
* Author: Hesenrre
*/

(function( $, undefined ) {
 
	var appname = null;
	
	var loadAppName = function(){
		appname = $("#appname").text();
	}
	
	var buildContentServerForm = function(){
		var main = $("<div/>", {id:"cd"});
		$("#toolbar").append(main);
		main.append($("<h3/>",{text: "Add Content Distributor"}));
		var div = $("<div/>",{id: "fields"});
		main.append(div);
		var form = $("<form/>", {id: "contentdistform"});
		div.append(form);
		
		var pserver = $("<span/>",{class:"server"});
		pserver.append("Server: ");
		pserver.append($("<input/>",{type:"text", name:"server", size: 13}));
		form.append(pserver);
		
		var pport = $("<span/>",{class:"port"});
		pport.append("Port: ");
		pport.append($("<input/>",{type:"text", name:"port", size: 3}));
		form.append(pport);
		
		button = $("<input/>",{type:"button", value:"Add"});
		form.append(button);
		button.click(function(){
			$.ajax({
				url: appname+"/flow/connection/AddContentDistributor",
				type: "POST",
				data: form.serialize(),
				success: function(){
					form.each(function(){
						this.reset();
					});
					loadContentDistributors();
				}
			});
		});
		
	}

	var buildServerForm = function(){
		$("#toolbar").append(serverform = $("<form/>",{
			id: "serverform"
		}));
		var title = $("<h3/>",{text: "Add Search Server"});
		serverform.append(title);		
		var div = $("<div/>",{id: "fields"});
		serverform.append(div);
		var pserver = $("<span/>",{class:"server"});
		pserver.append("Server: ");
		pserver.append($("<input/>",{type:"text", name:"server", size: 13}));
		
		var pport = $("<span/>",{class:"port"});
		pport.append("Port: ");
		pport.append($("<input/>",{type:"text", name:"port", size: 3}));
		pport.keypress(function(e){			
			return e.which === 0 || e.which === 8 || /[0-9]+/.test(String.fromCharCode(e.which));
		});
		
		div.append(pserver);
		div.append(pport);
		button = $("<input/>",{type:"button", value:"Add"});
		div.append(button);
		button.click(function(){
			$.ajax({
				url: appname+"/flow/connection/AddServer",
				type: "POST",
				data: $("#serverform").serialize(),
				success: function(){
					$("#serverform").each(function(){
						this.reset();
					});
					loadConnectedServers();
				}
			});
		});		
	}
	
	var drawServers = function(servers){		
		var regServers = $("<div/>",{id: "regServers"});
		regServers.append($("<h3/>",{text: "Registered Servers"}));
		var regServersForm = $("<form/>",{id: "regServersForm"});
		regServers.append(regServersForm);
		$("#toolbar").prepend(regServers);
		var serverList = $("<ul/>",{class: "serverList"});
		regServersForm.append(serverList);
		for(var i = 0; i < servers.length; i++){
			var li = $("<li/>");
			li.append($("<input/>", {type:"checkbox", name:"server", value:servers[i].server +":"+servers[i].port}));
			li.append(servers[i].server +" : "+servers[i].port);
			serverList.append(li);
		}
		var update = $("<input/>",{type: "button", value: "add to current"});
		var remove = $("<input/>",{type: "button", value: "remove servers"});
		regServersForm.append(update);
		regServersForm.append(remove);
		update.click(function(){			
			$.ajax({
				url: appname+"/flow/connection/UpdateCurrentServers",
				type: "POST",
				data: $("#regServersForm").serialize(),
				success: function(){
					$("#regServersForm").each(function(){
						this.reset();
					});
					loadCurrentServers();
				}
			});
		});
		remove.click(function(){
			$.ajax({
				url: appname+"/flow/connection/RemoveServers",
				type: "POST",
				data: $("#regServersForm").serialize(),
				success: function(){
					console.debug("it worked");
					loadConnectedServers();
				}
			});
		});
	}
	
	var drawCurrentServers = function(servers){
		
		var currServers = $("<div/>",{id: "currServers"});
		currServers.append($("<h3/>",{text: "Selected Servers for Search"}));
		var currServersForm = $("<form/>",{id: "currServersForm"});
		currServers.append(currServersForm);
		$("#toolbar").prepend(currServers);
		var serverList = $("<ul/>",{class: "serverList"});
		currServersForm.append(serverList);
		for(var i = 0; i < servers.length; i++){
			var li = $("<li/>");
			var check = $("<input/>", {type:"checkbox", name:"server", value:servers[i].server +":"+servers[i].port, checked: "checked"}); 
			li.append(check);
			li.append(servers[i].server +" : "+servers[i].port);
			serverList.append(li);
			check.click(function(){
				$.ajax({
					url: appname+"/flow/connection/RemoveCurrentServer",
					type: "POST",
					data: "server="+$(this).val(),
					success: function(){						
						loadCurrentServers();
					}
				});
			});
		}
	}
	
	var drawContentDistributors = function(servers){

		var contentdist = $("<div/>",{id: "contentdist"});
		contentdist.append($("<h3/>",{text: "Content Distributors"}));
		var contentdistForm = $("<form/>",{id: "contentdistForm"});
		contentdist.append(contentdistForm);
		$("#toolbar").prepend(contentdist);
		var serverList = $("<ul/>",{class: "serverList"});
		contentdistForm.append(serverList);
		for(var i = 0; i < servers.length; i++){
			var li = $("<li/>");
			var check = $("<input/>", {type:"checkbox", name:"contentDist", value:servers[i].server +":"+servers[i].port}); 
			li.append(check);
			li.append(servers[i].server +" : "+servers[i].port);
			serverList.append(li);			
		}
	}

	var drawCurrentViews = function(){
		var views = $("<div/>",{id:"views"});
		$("#toolbar").prepend(views);
		$.getJSON(appname+"/flow/search/GetAvailableViews", function(json){
			
			views.append($("<h3/>",{text: "Views for current servers"}));
			if(json.length <= 0){
				views.append("no views to show");
			}else{
				var form = $("<form/>",{id: "viewsform"});
				var select = $("<select/>",{name:"searchview", size:12, class: "views"});
				for(var i = 0; i < json.length; i++){
					var opt = $("<option/>",{value: json[i], text: json[i]});
					if(i === 0){
						opt.attr("selected", "selected");
					}
					select.append(opt);
				}
				views.append(form);
				form.append(select);
			}
		}).error(function(e){
			views.append("Failure loading views");
		});
	}
	
	var loadConnectedServers = function(){
		if($("#regServers").length > 0){
			$("#regServers").remove();
		}
		$.getJSON(appname+"/flow/connection/GetConnectedServers", function(json){
			if(json.length > 0){
				drawServers(json);
			}			
		});
	}
	
	var loadContentDistributors = function(){
		if($("#contentdist").length > 0){
			$("#contentdist").remove();
		}
		$.getJSON(appname+"/flow/connection/GetContentDistributors", function(json){
			if(json.length > 0){
				drawContentDistributors(json);
			}			
		});
	}
	
	var loadCurrentServers = function(){
		console.debug("loading current servers");
		$.getJSON(appname+"/flow/connection/GetCurrentServers", function(json){			
			if($("#currServers").length > 0){
				$("#currServers").remove();
			}
			if($("#views").length > 0){
				$("#views").remove();
			}
			if(json.length > 0){
				drawCurrentServers(json);
				console.debug("loading currentviews");
				drawCurrentViews();
			}			
		});
	}
	
	var loadSearchBox = function(){
		var main = $("<div/>",{id: "searchArea"});
		$("#content").append(main);
		var form = $("<form/>", {id: "searchform"});
		main.append(form);
		var searchbox = $("<textarea/>",{name:"searchbox", cols:80, rows: 5});
		var button = $("<input/>",{type: "button", value: "search"});
		form.append(searchbox);
		form.append($("<br/>"));
		form.append(button);
		button.click(function(){
			if($("#viewsform").length <= 0){
				alert("Please set a current server");
				return;
			}
			if($("#viewsform").serialize() === ""){
				alert("Please select a view");
				return;
			}
			$.ajax({
				url: appname+"/flow/search/Search",
				type: "POST",
				data: form.serialize()+"&"+$("#viewsform").serialize(),
				success: function(e){					
					$("#results").html(e);
					$(".showmore").click(function(){
						$("#"+$(this).attr("doc")).slideToggle();
					});					
					loadDeleteEvents();
				},
				beforeSend: function(){
					$("#results").html("");
					$("#modal").text("Searching ...");
					$("#modal").append($("<br/>"));
					$("#modal").append($("<img/>",{src: appname+"/images/loading.gif"}));
					$("#modal").modal({
						 overlay:100,
						 overlayCss: {backgroundColor:"#000"},
				         escClose: false
					});
				},
				complete: function(){
					setTimeout(function(){
						$.modal.close();
					}, 1000);
				}
			});
		});
	}
	
	var loadResultArea = function(){
		$("#content").append($("<div/>",{id: "results"}));
	}
	
	var loadDeleteEvents = function(){
		$("#deleteb").click(function(){
			if($("#contentdistForm").serialize() === ""){
				alert("Please select a content distributor to execute deletion");
				return;
			}
			var del = confirm("This process will delete all info matched with the current displayed search; please review the info you capture." +
					"\n\nThe info on the search box will be used to execute the deletion." +
					"\n\nAre you sure to procedure?");
			if (del){
				$.ajax({
					url: appname+"/flow/content/Delete",
					type: "POST",
					data: $("#contentdistForm").serialize()+"&"+$("#viewsform").serialize()+"&"+$("#currServersForm").serialize()+"&"+$("#searchform").serialize(),
					success: function(){
						$("#modal").text("delete was completed");
					},
					beforeSend: function(){
						$("#results").html("");
						$("#modal").text("deleting, please wait ...");
						$("#modal").append($("<br/>"));
						$("#modal").append($("<img/>",{src: appname+"/images/loading.gif"}));
						$("#modal").modal({
							overlay:100,
							overlayCss: {backgroundColor:"#000"},
							escClose: false
						});
					},
					error: function(){
						$("#modal").text("delete could not be completed");
					},
					complete: function(){
						setTimeout(function(){
							$.modal.close();
						}, 3000);
					}
				});
			}
		});
		$("#schdeletb").click(function(){
			
		});
	}
	
    var initialize = function() {
        console.debug("loading initialize");
        $("#content").append($("<div/>",{id:"toolbar"}));
        buildServerForm();
        buildContentServerForm();
        loadAppName();
        loadCurrentServers();
        loadConnectedServers();
        loadContentDistributors();
        loadSearchBox();
        loadResultArea();
        $("#content").append($("<div/>",{id:"modal"}));
        $("#modal").hide();
    };
    

    $( document ).ready(initialize);
})( jQuery );
