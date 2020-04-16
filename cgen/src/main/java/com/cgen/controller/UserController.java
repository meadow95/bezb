package com.cgen.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public void login(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {
		User user =userService.getUser(userDTO); 
		
		if (user != null) {
			httpSession.setAttribute("loggedUser", user);
		} else {
			throw new RuntimeException();
		}
	}
	@PostMapping("register")
	public void register(@Valid @RequestBody UserDTO userDTO) {
		userService.registerUser(userDTO);
		System.out.println("Registrovan korisnik");
		 
	}
	
	@GetMapping("logout") 
	public void logout(HttpServletRequest request) {
		httpSession.invalidate(); 
		System.out.println("Korisnik izlogovan");
	} 
	//provera da li je ulogovan
	@GetMapping("/isLogged")   
	public User isLoggedIn() {
		User user = (User) httpSession.getAttribute("loggedUser");
		if(user != null) {
			return user;
		}else {
			return null; 	 
		} 
	}
	
	
	
}
