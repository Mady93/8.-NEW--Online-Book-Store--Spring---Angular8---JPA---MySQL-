package com.javainuse.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
//@NamedQuery(name = "Email.getEmailByUserId", query = "SELECT m FROM Email m WHERE m.user.id = :userId")
@NamedQuery(name = "Email.getEmailByUserId", query = "SELECT DISTINCT m FROM Email m, Order o WHERE m.order.user.id = :userId")
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

	/*
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;
	*/

	@OneToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "orderId", referencedColumnName = "id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private Order order;
	
	@Temporal(TemporalType.TIMESTAMP)
    @Generated(GenerationTime.INSERT)
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date sendedAt; 
	
	public Email(String from,String to, String subject, String body/*, User user*/, Order order) {
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.body = body;
		//this.user = user;
		this.order = order;
		this.sendedAt = new Date();
	} 
	
}
