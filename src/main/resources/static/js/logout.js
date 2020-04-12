window.onload = function generateMenu(){
	const navbar = document.getElementById('rg');
	
	$.ajax({
    	url: "../api/user/isLogged",
    	method : "GET",
    	success: function(user){
    		if(user.username != null){
	    		navbar.innerHTML = '';
	    		navbar.innerHTML += `<ul class="navbar-nav ml-auto" id="regnav">
	                	<li class="nav-item">
	                    	<a class="nav-link" href="#">User: `+user.username+`</a>
	                    </li>
	                    <li class="nav-item">
	                    	<a class="nav-link" id="logOut" href="#">Logout</a>
	                    </li>
	                </ul>`
    		}
    	},
    	error: function(){ 
    		alert("error");
    	}
    });
	
};
  
$(document).on('click','#logOut',function(){
    $.ajax({
    	url: "../api/user/logout",
    	method : "GET", 
    	success: function(){
    		location.reload();
    		sessionStorage.removeItem('loggedUser');
    	},
    	error: function(){
    		alert("error");
    	}
    });
});