package com.cgen.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cgen.model.Role;
import com.cgen.model.User;
import com.cgen.model.UserDTO;
import com.cgen.model.UserType;
import com.cgen.repository.UserRepository;



@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	 
	public void registerUser(UserDTO userDTO) {
		byte[] salt = KeysGenerator.generateSalt();
		byte[] hash = KeysGenerator.hashPassword(userDTO.getPassword(), salt);
		
		User user = new User(userDTO.getUsername(), UserType.ADMIN, salt, hash);
		
		Iterable<User> users = new ArrayList<User>();
		
		users = userRepository.findAll();
		
		for (User user_list : users) {
            
			if(user_list.getUsername().equals(user.getUsername())) {
								
				throw new RuntimeException("Username postoji!"); 
			}
			
        }
		
		try {				
			userRepository.save(user);
		} catch(Exception e) { 
			e.printStackTrace(); 
			throw new RuntimeException("Username postoji!"); 
		}
	}
	
	
	public User getUser(UserDTO userDTO) throws SQLException {
		
	//	User user = userRepository.findByUsername(userDTO.getUsername());
		
		Iterable<User> users = new ArrayList<User>();
		
		users = userRepository.findAll();
		
		User user = null;
		
		for (User user_list : users) {
            
			if(user_list.getUsername().equals(userDTO.getUsername())) {
				
				user = user_list;
				break;
			}
			
        }
		
		if (user == null) {
			
			return null;
		} 
		
		byte[] passHash = KeysGenerator.hashPassword(userDTO.getPassword(), user.getPasswordSalt());
		
	    if (!Arrays.equals(passHash, user.getPasswordHash())) {
			//throw new RuntimeException("Pogresan username ili lozinka!");
	    	return null;
	    }
	    else {
	    System.out.println("********************" + user.getUsername() + "*****************");
	    
	    Set<Role> roles = new HashSet<>();
	    
	    roles=user.getRoles();
	    
	    for (Role user_list : roles) {
            
	    		//System.out.println(user_list.getName());
			
        }
		
		return user;
	    }
	}
	

}
