package com.javainuse.entities;

import java.util.Date;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@NamedQuery(name="Order.findOrderByUserId", query="SELECT o FROM Order o WHERE o.user.id = :userId")
@NamedQuery(name="Order.countOrderByUserId", query="SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId")
@NamedQuery(name = "Order.findOrdersByUser", query = "SELECT o FROM Order o WHERE o.user = :user")

@NamedQuery(name="Order.countOrders", query="SELECT COUNT(o) FROM Order o")

public class Order {
  
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	
	 @ManyToOne(fetch = FetchType.EAGER, optional = false)
	    @JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
	    @OnDelete(action = OnDeleteAction.CASCADE)
	 private User user;

	  @Temporal(TemporalType.TIMESTAMP)
	    @Generated(GenerationTime.INSERT)
	    @ColumnDefault("CURRENT_TIMESTAMP")
	    @Column(name = "created_at", nullable = false, updatable = false)
	    private Date createdAt; 


public Order(User user) {
	this.user = user;
	 this.createdAt = new Date();
}

}