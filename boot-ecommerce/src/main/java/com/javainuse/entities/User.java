package com.javainuse.entities;

import java.security.NoSuchAlgorithmException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.security.MessageDigest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@NamedQuery(name = "User.existsByEmail", query = "SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email")
//aggiunti
@NamedQuery(name = "User.findUserByEmail", query = "SELECT u FROM User u WHERE u.email = :email")
@NamedQuery(name = "User.findUserByType", query = "SELECT u FROM User u WHERE u.type = :code")
@NamedQuery(name = "User.countByType", query = "SELECT COUNT(u) FROM User u WHERE u.type IN ('Admin')")

public class User {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Name cannot be null")
	@Column(name = "name")
	private String name;

	@Pattern(regexp = "^(Admin|User|Seller)$", message = "Role type must be one of: Admin, User, Seller")
    @Column(name = "type")
	private String type;



	@NotNull(message = "Email cannot be null")
	@Pattern(regexp = "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\." + "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
			+ "(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9]"
			+ "(?:[A-Za-z0-9-]*[A-Za-z0-9])?", message = "Email format not valid")
	@Column(name = "email", unique = true)
	private String email;
	
	@NotNull(message = "Password cannot be null")
	@Pattern(regexp = "^.{8,}$", message = "Password must be at least 8 characters long")
	@Column(name = "password")
	private String password;

	@Lob
	@Column(name = "token")
	private String token;



	public User(String name, String type, String email, String password) {
		this.name = name;
		this.type = type;
		this.email = email;
		this.setPassword(password);
		this.token = "";
	}


	public static String md5(String data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");

		// Add the input string to the MessageDigest
		md.update(data.getBytes());

		// Get the MD5 hash
		byte[] digest = md.digest();

		// Convert the byte array to a hexadecimal string
		StringBuilder hexString = new StringBuilder();
		for (byte b : digest) {
			String hex = Integer.toHexString(0xFF & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}

		return hexString.toString();
	}

	@JsonIgnore
	@JsonProperty(value = "password")
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		
		if (password == "") this.password = "";
		else
		{
			try {
				this.password = User.md5(password);
			} catch (Exception ex) {
				this.password = "";
			}
		}
	}


	@JsonIgnore
	@JsonProperty(value = "token")
	public String getToken() {
		return this.token;
	}

	

	public void setType(String type) {
        if (type == null || type.isEmpty()) {
            this.type = "User"; // Imposta il valore predefinito "User" se il valore è vuoto
        } else {
            this.type = type;
        }
    }




}
