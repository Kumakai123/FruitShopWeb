package org.xiangan.fruitshopweb.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 * 損耗表
 */
@Data
@Entity
@Table(name = "wastage")
public class Wastage {

	/**
	 * 主鍵
	 */
	@Column(
		nullable = false,
		updatable = false,
		length = 11
	)
	@Id
	private String id;

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
		nullable = false,
		precision = 1
	)
	private Double quantity;
	
	/**
	 * 日期
	 */
	@Basic(optional = false)
	@Column(
		name = "date",
		nullable = false
	)
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(
		pattern = "yyyy-MM-dd'T'HH:mm:ssX",
		timezone = "Asia/Taipei"
	)
	private Date date;

	@PrePersist
	protected void genPrimaryKey() {
		if (id == null) {
			id = NanoIdUtils
				.randomNanoId(
					NanoIdUtils.DEFAULT_NUMBER_GENERATOR,
					NanoIdUtils.DEFAULT_ALPHABET,
					10)
				.replace("-", "");
		}
	}

	/**
	 * 默認構造函式
	 */
	public Wastage() {
		quantity = 0.0;
		date = new Date(
			System.currentTimeMillis()
		);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Wastage wastage = (Wastage) o;
		return Objects.equals(id, wastage.id) && Objects.equals(product, wastage.product) && Objects.equals(quantity, wastage.quantity) && Objects.equals(date, wastage.date);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, product, quantity, date);
	}
}
