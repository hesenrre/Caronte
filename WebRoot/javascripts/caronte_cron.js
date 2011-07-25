/*
*
* Author: Hesenrre
*/

(function( $, undefined ) {
 
	var loadButtonListener = function(){
		$("#schdeletb").click(function(){
			
		});
	}
	
	var openFormModal = function(){		
		var modalform = $("<div/>",{id: "modalform"});
		$("body").append(modalform);		
	}
	
	var initialize = function() {
		loadButtonListener(); 
	}
	
	$( document ).ready(initialize);
})( jQuery );