package com.cgen.repository;
 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.data.repository.query.Param;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Repository;

import com.cgen.model.User;


@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
	

	 @Query("SELECT u FROM User u WHERE u.username = :username")
	    public User getUserByUsername(@Param("username") String username);
	 /*
	public default User findByUsername(String username) throws SQLException {  //neuspeo pokusaj
		
		User user = null;
		
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/isa", "root", "Bambus*987")) {
            // create a Statement
            try (Statement stmt = conn.createStatement()) {
                //execute query
                try (ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE username = 'jovana' ")) {
                    //position result to first
                	
                    rs.first();
                                   
                    user.setId(rs.getLong("id"));
                    user.setPasswordHash(null);
                    
                    return user;
                }
            }
        }

		
	}
	*/
} 
