package com.javainuse.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "emails")
public class Email {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull(message = "From cannot be null")
	@Column(name = "sender")
	private String from;
	
	@NotNull(message = "To cannot be null")
	@Column(name = "recipient")
	private String to;

	@Column(name = "subject")
	private String subject;
	
	@Lob
	@NotNull(message = "Body cannot be null")
	@Column(name = "body")
	private String body;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Generated(GenerationTime.INSERT)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date sendedAt; 
	
	public Email(String from,String to, String subject, String body) {
		
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
		this.sendedAt = new Date();
	} 
	
}
