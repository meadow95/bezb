package com.cgen.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cgen.model.Role;
import com.cgen.model.User;
import com.cgen.model.UserDTO;
import com.cgen.service.UserService;


@RequestMapping("api/user")
@RestController
public class UserController {

	private UserService userService;
	private HttpSession httpSession;
	
	@Autowired
	public UserController( HttpSession httpSession, UserService accountService) {
		this.httpSession = httpSession;
		this.userService = accountService; 
	}
	
	@PostMapping("login") 
	public Collection<String> login(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request, HttpServletResponse response) throws SQLException {
		User user =userService.getUser(userDTO); 
		Collection<Role> roles = new HashSet<>();
		
		System.out.println(user);
		
		if (user != null) {
			request.getSession().setAttribute("loggedUser", user);
			
			httpSession.setAttribute("loggedUser", user);
			
//			System.out.println("**********************" + request.isRequestedSessionIdFromURL());
			
			roles=user.getRoles();
			
			Collection<String> list = new ArrayList<String>();
		    
		    for (Role user_list : roles) {
		    	
		    	String name = user_list.getName();
		    	
		    	list.add(name);	     
				
	        }

			
			return list;
		} else {
//			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
			
		}
	}
	
	
	@RequestMapping(value = "register", method = { RequestMethod.GET, RequestMethod.POST })
	public void register(@Valid @RequestBody UserDTO userDTO) {
		userService.registerUser(userDTO);
		System.out.println("Registrovan korisnik");
		 
	}
	
	@GetMapping("/logout") 
	public void logout(HttpServletRequest request) {
		httpSession.invalidate();
		request.getSession().invalidate();
	} 
	
	
	//provera da li je ulogovan
	@GetMapping("/isLoggedAdmin")   
	public int isLoggedInAdmin() {
		User user = (User) httpSession.getAttribute("loggedUser");
		if(user != null) {
			Collection<Role> roles = new HashSet<>();
			roles=user.getRoles();
			String name = "ADMIN";
 
		    for (Role user_list : roles) {
		    	
		    	if(name.equals(user_list.getName())) {
		    		
		    		return 1;
		    		
		    	}
				
	        }
			
			return 2;
		}else {
			return 0; 	 
		} 
	}
	
	@GetMapping("/isLoggedUser")   
	public int isLoggedInUser() {
		User user = (User) httpSession.getAttribute("loggedUser");
		if(user != null) {
			Collection<Role> roles = new HashSet<>();
			roles=user.getRoles();
			String nameA = "ADMIN";
			String nameU = "USER";
 
		    for (Role user_list : roles) {
		    	
		    	if(nameA.equals(user_list.getName()) || nameU.equals(user_list.getName())) {
		    		
		    		return 1;
		    		
		    	}
				
	        }
			
			return 2;
		}else {
			return 0; 	 
		} 
	}
	
	
	
}
