package com.javainuse.entities;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
//@NamedQuery(name = "Discount.findByNotDeleted", query = "SELECT d FROM Discount d WHERE d.isActive is true")
//@NamedQuery(name = "Discount.countByNotDeleted", query = "SELECT count(d) FROM Discount d WHERE d.isActive is true")
@NamedQuery(name = "Discount.findByNotDeleted", query = "SELECT d FROM Discount d WHERE d.isActive = true")
@NamedQuery(name = "Discount.countByNotDeleted", query = "SELECT count(d) FROM Discount d WHERE d.isActive = true")

public class Discount {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Generated(GenerationTime.INSERT)
	@ColumnDefault("CURRENT_TIMESTAMP")
	@Column(name = "start_percentage", nullable = false, updatable = false)
	private Date startPercentage;

	@Column(name = "end_percentage", nullable = false, updatable = false)
	private Date endPercentage;

	@Min(value = 5, message = "Percentage must be at least 5%")
	@Max(value = 50, message = "Percentage cannot be greater than 50%")
	@Column(name = "percentage", nullable = false, updatable = false)
	private int percentage;
	
	@Column(name = "is_active", nullable = false, updatable = false)
	private boolean isActive;

	public Discount(int percentage) {

		this.percentage = percentage;
		this.isActive = true;
		
		// Imposta la data di inizio sconto sulla data corrente
		this.startPercentage = new Date();

		// Imposta la data di fine sconto sulla data corrente + 3 giorni
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.startPercentage);
		calendar.add(Calendar.DAY_OF_MONTH, 3);
		this.endPercentage = calendar.getTime();

	}

	// verificare se uno sconto è attivo
	public boolean isDiscountActive() {
		Date currentDate = new Date();
		return currentDate.after(startPercentage) && currentDate.before(endPercentage);
	}

	// terminare uno sconto prima dei 3 giorni previsti
	public void endDiscount() {
		this.endPercentage = new Date();
	}

	// verificare se uno sconto è scaduto
	public boolean isDiscountExpired() {
		Date currentDate = new Date();
		return currentDate.after(endPercentage);
	}

// verificare la validità dello sconto
	public boolean isValid() {
		if (percentage >= 5 && percentage <= 50 && startPercentage.before(endPercentage)) {
			return true;
		} else {
			return false;
		}
	}
	

}


