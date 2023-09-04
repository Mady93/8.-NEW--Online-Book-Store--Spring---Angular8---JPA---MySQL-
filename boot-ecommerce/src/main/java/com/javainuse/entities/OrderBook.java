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

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

/*@Data
@AllArgsConstructor
@ToString */
@Entity
@Table(name = "order_books")
@NamedQuery(name = "OrderBook.getOrderBooksByOrderId", query = "SELECT jt FROM OrderBook jt WHERE jt.id.orderId = :orderId AND jt.isActive is true")
public class OrderBook {

	// istanza inner class senza argomenti
	@EmbeddedId
	private OrderBooksId id = new OrderBooksId();

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

	@Column(name = "isActive")
	private boolean isActive;

	public OrderBooksId getId() {
		return id;
	}

	public void setId(OrderBooksId id) {
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

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}


	@JsonProperty("isActive")
	boolean getIsActive() {
		return this.isActive;
	}

	public OrderBook() {
	}

	public OrderBook(OrderBooksId id, Integer quantity, double price) {
		this.id = id;
		this.quantity = quantity;
		this.price = price;
	}

	// inner class
	@Embeddable
	public static class OrderBooksId implements Serializable {

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

		public OrderBooksId() {
		}

		public OrderBooksId(Long orderId, Long bookId) {
			super();
			this.orderId = orderId;
			this.bookId = bookId;
		}

	}

}
