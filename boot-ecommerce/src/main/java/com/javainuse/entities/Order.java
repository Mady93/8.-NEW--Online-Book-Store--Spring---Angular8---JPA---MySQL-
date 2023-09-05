package com.javainuse.entities;

import java.util.Date;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.annotation.JsonProperty;

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
import javax.validation.constraints.Pattern;

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
// da vedere se servono ancora queste 2
@NamedQuery(name = "Order.findOrdersByUser", query = "SELECT o FROM Order o WHERE o.user = :user AND o.isActive is true")
@NamedQuery(name = "Order.countOrders", query = "SELECT COUNT(o) FROM Order o")

// inbox
@NamedQuery(name = "Order.getOrdersInWorkingStateWithDetails", query = "SELECT o FROM Order o WHERE o.state = 'Working' AND o.isActive is true")
@NamedQuery(name = "Order.countTotalOrdersInWorkingState", query = "SELECT COUNT(o) FROM Order o WHERE o.state = 'Working' AND o.isActive is true")

// my orders
@NamedQuery(name = "Order.findByNotDeletedAndByUserId", query = "SELECT o FROM Order o WHERE o.user.id = :userId AND o.isActive is true AND o.state !='Cancelled'")
@NamedQuery(name = "Order.countNotDeletedAndByUserId", query = "SELECT count(o) FROM Order o WHERE o.user.id = :userId AND o.isActive is true AND o.state !='Cancelled'")

// inboxCancelled
@NamedQuery(name = "Order.countTotalOrdersInCancelledState", query = "SELECT COUNT(o) FROM Order o WHERE o.state = 'Cancelled' AND o.isActive is true")
@NamedQuery(name = "Order.getOrdersInCancelledStatWithDetails", query = "SELECT o FROM Order o WHERE o.state = 'Cancelled' AND o.isActive is true")
public class Order {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "userId", referencedColumnName = "id", nullable = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private User user;

	@Pattern(regexp = "^(Working|Send|Cancelled)$", message = "State must be one of: Working, Send, Cancelled")
	@Column(name = "state")
	private String state;

	@Temporal(TemporalType.TIMESTAMP)
	@Generated(GenerationTime.INSERT)
	@ColumnDefault("CURRENT_TIMESTAMP")
	@Column(name = "created_at", nullable = false, updatable = false)
	private Date createdAt;

	@Column(name = "isActive")
	private boolean isActive;

	@Column(name = "edit")
	private boolean edit;

	@Column(name = "edit_by")
	private String editBy;

	@Column(name = "edit_from")
	private String editFrom;

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "edit_date")
    private Date editDate;

	@Column(name = "reason")
	private String reason;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "cancelled_date")
	private Date cancelledDate;
	
	public Order(User user, String state) {
		this.user = user;
		this.state = state;
		this.createdAt = new Date();
	}

	// quando l'order apre un ordine e lo modifica viene notificato l'user
	public Order(User user, String state, boolean edit, String editBy, String editFrom) {
        this.user = user;
        this.state = state;
        this.createdAt = new Date();
        setEdit(edit, editBy, editFrom);
    }

	// anullamento dell'ordine da parte dell'user
	public Order(User user, String state, String reason) {
		this.user = user;
		this.state = state;
		this.reason = reason;
		this.createdAt = new Date();
		this.cancelledDate = new Date();
	}
	

	public void setEdit(boolean edit, String editBy, String editFrom) {
        if (edit && !this.edit) {
            this.edit = true;
            this.editBy = editBy;
            this.editFrom = editFrom;
            this.editDate = new Date();

			// NOTIFICARE L'UTENTE QUANDO L'ORDER APRE L'ORDINE, EDIT DIVENTA TRUE
			
        } else if (!edit) {
            this.edit = false;
            this.editBy = null;
            this.editFrom = null;
            this.editDate = null;
        }
    }

	@JsonProperty("isActive")
	boolean getIsActive() {
		return this.isActive;
	}

	public boolean getEdit() {
		return this.edit;
	}

	

}