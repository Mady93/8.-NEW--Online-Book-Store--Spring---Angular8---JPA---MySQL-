package com.javainuse.entities;

import java.io.Serializable;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
/*
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
*/

@Entity
@Table(name = "join_table")
@NamedQuery(name = "JoinTable.countJoinTablesByCompositeId", query = "SELECT COUNT(jt) FROM JoinTable jt WHERE jt.id = :joinTableId")
@NamedQuery(name = "JoinTable.countBooksByOrderId", query = "SELECT COUNT(jt.book) FROM JoinTable jt WHERE jt.id.orderId = :orderId")
@NamedQuery(name = "JoinTable.findJoinTablesByCompositeId", query = "SELECT jt FROM JoinTable jt WHERE jt.id = :joinTableId")
@NamedQuery(name = "JoinTable.findJoinTablesListByCompositeId", query = "SELECT jt FROM JoinTable jt WHERE jt.id = :joinTableId")
public class JoinTable {

	// istanza inner class senza argomenti
	@EmbeddedId
	private JoinTableId id = new JoinTableId();

	@ManyToOne
	@MapsId("orderId")
	private Order order;

	@ManyToOne
	@MapsId("bookId")
	private Book book;

	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@Column(name = "price", nullable = false)
	private double price;

	@CreationTimestamp
	@Column(name = "added_at", nullable = false)
	private Date addedAt;

	public JoinTableId getId() {
		return id;
	}

	public void setId(JoinTableId id) {
		this.id = id;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Date getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(Date addedAt) {
		this.addedAt = addedAt;
	}
	public JoinTable() {
	}
	
	public JoinTable(JoinTableId id, Integer quantity, double price) {
		this.id = id;
		this.quantity = quantity;
		this.price = price;
	}

	// inner class
	@Embeddable
	public static class JoinTableId implements Serializable {

		private static final long serialVersionUID = 1L;

		private Long orderId;
		private Long bookId;

		public Long getOrderId() {
			return orderId;
		}

		public void setOrderId(Long orderId) {
			this.orderId = orderId;
		}

		public Long getBookId() {
			return bookId;
		}

		public void setBookId(Long bookId) {
			this.bookId = bookId;
		}

		public JoinTableId() {
		}

		public JoinTableId(Long orderId, Long bookId) {
			super();
			this.orderId = orderId;
			this.bookId = bookId;
		}

	}

}
