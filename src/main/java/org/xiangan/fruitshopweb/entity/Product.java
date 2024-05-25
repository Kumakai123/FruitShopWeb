package org.xiangan.fruitshopweb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 產品
 */
@Data
@Entity
@Table(
	name = "product",
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {
				"product_name",
				"unit_price"
			}
		)
	}
)
public class Product {
	
	/**
	 * 主鍵
	 */
	@Basic(optional = false)
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private long id;
	
	/**
	 * 產品名稱
	 */
	@Basic(optional = false)
	@Column(name = "product_name", nullable = false)
	@NotNull
	private String productName;
	
	/**
	 * 成本單價
	 */
	@Column(name = "unit_price", precision = 2)
	private BigDecimal unitPrice;
	
	/**
	 * 產品類型(列舉)
	 */
	@Basic(optional = false)
	@Column(name = "type", nullable = false)
	@NotNull
	private String type;
	
	/**
	 * 單位(列舉)
	 */
	@Basic(optional = false)
	@Column(name = "unit_type", nullable = false)
	@NotNull
	private String unitType;
	
	/**
	 * 貨主
	 */
	@JoinColumn(
		name = "consignor",
		nullable = false,
		referencedColumnName = "id"
	)
	@ManyToOne(optional = false)
	private Consignor consignor;
	
	/**
	 * 庫存
	 */
	@Basic(optional = false)
	@Column(name = "inventory", nullable = false)
	@NotNull
	private double inventory;
	
	/**
	 * 默認構造函式
	 */
	public Product() {
		unitPrice = BigDecimal.ZERO;
		inventory = 0;
	}
	
	/**
	 * @param productName 產品名稱
	 * @param type        產品類型(列舉)
	 * @param unitType    單位(列舉)
	 * @param consignor   貨主
	 */
	public Product(String productName, String type, String unitType, Consignor consignor) {
		this();
		this.productName = productName;
		this.type = type;
		this.unitType = unitType;
		this.consignor = consignor;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Product product = (Product) o;
		return consignor == product.consignor && Double.compare(inventory, product.inventory) == 0 && Objects.equals(id, product.id) && Objects.equals(productName, product.productName) && Objects.equals(unitPrice, product.unitPrice) && Objects.equals(type, product.type) && Objects.equals(unitType, product.unitType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, productName, unitPrice, type, unitType, consignor, inventory);
	}
}
