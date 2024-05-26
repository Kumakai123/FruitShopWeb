package org.xiangan.fruitshopweb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

/**
 * 進貨
 */
@Data
@Entity
@Table(name = "purchase")
public class Purchase {
	
	/**
	 * 主鍵
	 */
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private long id;
	
	/**
	 * 產品
	 */
	@JoinColumn(
		name = "product",
		nullable = false,
		referencedColumnName = "id"
	)
	@ManyToOne(optional = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	@JsonIdentityInfo(
		generator = ObjectIdGenerators.PropertyGenerator.class,
		property = "id"
	)
	@JsonIdentityReference(alwaysAsId = true)
	private Product product;
	
	/**
	 * 數量
	 */
	@Basic(optional = false)
	@Column(
		name = "quantity",
		nullable = false
	)
	private Double quantity;
	
	/**
	 * 開單日期
	 */
	@Column(name = "order_date")
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(
		pattern = "yyyy-MM-dd'T'HH:mm:ssX",
		timezone = "Asia/Taipei"
	)
	private Date orderDate;
	
	/**
	 * 進貨日期
	 */
	@Basic(optional = false)
	@Column(
		name = "receiving_date",
		nullable = false
	)
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(
		pattern = "yyyy-MM-dd'T'HH:mm:ssX",
		timezone = "Asia/Taipei"
	)
	private Date receivingDate;
	
	/**
	 * 默認構造函式
	 */
	public Purchase() {
		orderDate = new Date(
			System.currentTimeMillis()
		);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Purchase purchase = (Purchase) o;
		return Objects.equals(id, purchase.id) && Objects.equals(product, purchase.product) && Objects.equals(quantity, purchase.quantity) && Objects.equals(orderDate, purchase.orderDate);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, product, quantity, orderDate);
	}
}
