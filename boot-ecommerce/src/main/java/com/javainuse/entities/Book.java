package com.javainuse.entities;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

//import com.javainuse.validators.ByteArrayNotEmpty;
//import com.javainuse.validators.MaxLength;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Book {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "Name cannot be null")
	@NotBlank(message = "Name cannot be blank")
	@Column(name = "name")
	private String name;

	@NotNull(message = "Author cannot be null")
	@NotBlank(message = "Author cannot be blank")
	//@Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Author can only contain letters and spaces")
	@Pattern(regexp = "^[\\p{L}\\s.,'’‘-]*$", message = "Author can only contain characters, spaces, special characters and accented characters")
	@Column(name = "author")
	private String author;
	
	@DecimalMin(value = "0.1", inclusive = false, message = "Price must be a positive double number > 0.0")
	@DecimalMax(value = "1000000.0", message = "Price must be less than or equal to 1,000,000")
	@Column(name = "price")
	private double price;

	//@ByteArrayNotEmpty(message = "Byte array image cannot be null or empty")
	//@MaxLength(value = 1000000, message = "Byte array image length exceeds the maximum allowed")
	@Column(name = "picByte", length = 100000)
	private byte[] picByte;

	
	public Book(String name, String author, double price, byte[] picByte) {
		
		this.name = name;
		this.author = author;
		this.price = price;
		this.picByte = picByte;
	}


}
