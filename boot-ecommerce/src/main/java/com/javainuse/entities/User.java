package com.javainuse.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

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
public class User {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Name cannot be null")
	@NotBlank(message = "Name cannot be blank")
	@Column(name = "name")
	private String name;

	@NotBlank(message = "Email cannot be blank")
	@NotNull(message = "Email cannot be null")
	@Pattern(regexp = "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\." + "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
			+ "(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9]"
			+ "(?:[A-Za-z0-9-]*[A-Za-z0-9])?", message = "Email format not valid")
	@Column(name = "email", unique = true)
	private String email;
	
	@NotNull(message = "Password cannot be null")
	@NotBlank(message = "Password cannot be blank")
	@Pattern(regexp = "^.{8,}$", message = "Password must be at least 8 characters long")
	@Column(name = "password")
	private String password;

	@NotNull(message = "Role type cannot be null")
	@NotBlank(message = "Role type cannot be blank")
	@Pattern(regexp = "^[A-Za-z.-]+$", message = "Role type must not contain numbers, special characters, or spaces")
	@Column(name = "type")
	private String type;

	
	
	public User(String name, String email, String password, String type) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.type = type;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



}
