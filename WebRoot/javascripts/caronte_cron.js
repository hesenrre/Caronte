/*
 *
 * Author: Hesenrre
 */

//crontype=d,f

//days = 2l,3m,4m,5j,6v,7s,1d
//startfrom=hh:mm

//freq = int
//freqtype = m,h
//startfrom=hh:mm


(function( $, undefined ) {

	var appname = null;
	
	var loadAppName = function(){
		appname = $("#appname").text();
	}
	
	var loadButtonListener = function(){
		$("#schdeletb").click(function(){
			openFormModal();
		});
	}

	var radios = function(){
		var div = $("<div/>",{class: "radios"});
		var by_days = $("<span/>",{text: "By days: "});
		var cd = $("<input/>",{type: "radio", name:"crontype", value: "d"});
		by_days.append(cd);
		div.append(by_days);
		div.append("&nbsp;");
		var by_freq = $("<span/>",{text: "By freq: "});
		var cf = $("<input/>",{type: "radio", name:"crontype", value: "f"});
		by_freq.append(cf);
		div.append(by_freq);
		div.append($("<div/>",{id: "cronoptions"}));
		cd.click(function(){
			$("#cronoptions").html("");
			$("#cronoptions").append(days());
		});
		cf.click(function(){
			$("#cronoptions").html("");
			$("#cronoptions").append(freqs());
		});
		return div;
	}

	var days = function(){
		var d = $("<p/>");
		d.append($("<label/>",{text: "Execute days:"}));
		d.append($("<input/>",{type: "hidden", name:"days", id:"hiddays"}));
		var dl = $("<ul/>",{id:"days"});
		d.append(dl);		
		$.each(["S", "M", "T", "W", "T", "F", "S"], function(i,v){
			var li = $("<li/>",{text: v, day: i+1});
			li.click(function(){				
				$(this).toggleClass("active");				
				$("#hiddays").val(
						$.map($(".active"), function(i){ 							
							return $(i).attr("day");
						}).join(",")
				);
			});
			dl.append(li);
		});
		
		return d;
	}

	var freqs = function(){
		var every = $("<p/>");
		every.append($("<label/>",{text: "Execute every:"}));
		every.append($("<br/>"));
		every.append($("<input/>",{type:"text", name:"freq", size: "4", value:"0"}));
		var select = $("<select/>",{name: "freqtype"});
		select.append($("<option/>", {value: "h", text: "Hours"}));
		select.append($("<option/>", {value: "m", text: "Minutes"}));
		every.append(select);
		return every;
	}

	var openFormModal = function(){
		if($("#modalform").length > 0){
			$("#modalform").remove();
		}
		var modalform = $("<div/>",{id: "modalform"});		
		$("#content").append(modalform);
		var form = $("<form/>",{id: "cronform"});
		modalform.append(form);
		form.append(radios());
		var startat = $("<p/>"); 
		form.append(startat);
		startat.append($("<label/>",{text: "Starting at:"}));
		startat.append($("<br/>"));
		startat.append($("<input/>",{type:"text", name:"startfrom", size: "4", value:"00:00"}));
		form.append($("<input/>",{type:"button", value:"Cancel", class: "simplemodal-close"}));;
		form.append($("<input/>",{type:"button", value:"Save", id:"savebutton"}));		
		$("#modalform").modal({
			overlay:100,
			overlayCss: {backgroundColor:"#000"},
			escClose: false
		});
		$("#savebutton").click(function(){
			if($("input:radio[value='d']").attr("checked") && $("#hiddays").val() === ""){ 
				 alert("please select a day");
				 return;
			}
			$.ajax({
				url: appname+"/flow/cron/Schedule",
				type: "POST",
				data: form.serialize()+"&"+$("#currServersForm").serialize()+"&"+$("#viewsform").serialize()+"&"+$("#contentdistForm").serialize()+"&"+$("#searchform").serialize(),
				success: function(e){					
					setTimeout(function(){
						$.modal.close();
						$("#modalform").remove();
					}, 1000);
				}
			});
		});
		$("input:radio[value='d']").click();
	}

	var initialize = function(){
		loadAppName();
		loadButtonListener();
	}

	$( document ).ready(initialize);
})( jQuery );
