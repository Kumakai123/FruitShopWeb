package org.xiangan.fruitshopweb.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.xiangan.fruitshopweb.enumType.ProductTypeEnum;
import org.xiangan.fruitshopweb.enumType.ProductTypeEnumConverter;
import org.xiangan.fruitshopweb.enumType.UnitTypeEnum;
import org.xiangan.fruitshopweb.enumType.UnitTypeEnumConverter;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 產品
 *
 * @author kyle
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
	@Column(
		nullable = false,
		updatable = false,
		length = 11
	)
	@Id
	private String id;

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
	// TODO 修正成列舉類別
	@Basic(optional = false)
	@Column(name = "type", nullable = false)
	@Convert(converter = ProductTypeEnumConverter.class)
	@Enumerated(EnumType.STRING)
	@NotNull
	private ProductTypeEnum type;
	
	/**
	 * 單位(列舉)
	 */
	@Basic(optional = false)
	@Column(name = "unit_type", nullable = false)
	@Convert(converter = UnitTypeEnumConverter.class)
	@Enumerated(EnumType.STRING)
	@NotNull
	private UnitTypeEnum unitType;

	/**
	 * 貨主
	 */
	@JoinColumn(
		name = "person",
		nullable = false,
		referencedColumnName = "id"
	)
	@ManyToOne(optional = false)
	@JsonIgnoreProperties
		({    "nickName"
			, "level"
			, "company"
			, "enabled"
			, "username"
			, "authorities"
			, "accountNonExpired"
			, "credentialsNonExpired"
			, "accountNonLocked"
		})
	private Person person;
	
	/**
	 * 庫存
	 */
	@Basic(optional = false)
	@Column(name = "inventory", nullable = false)
	@NotNull
	private double inventory;

	@PrePersist
	protected void genPrimaryKey() {
		if (id == null) {
			id = NanoIdUtils.randomNanoId(
				ThreadLocalRandom.current(),
				"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray(),
				10
			);
		}
	}

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
	 * @param person   貨主
	 */
	public Product(String productName, ProductTypeEnum type, UnitTypeEnum unitType, Person person) {
		this();
		this.productName = productName;
		this.type = type;
		this.unitType = unitType;
		this.person = person;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Product product = (Product) o;
		return person == product.person && Double.compare(inventory, product.inventory) == 0 && Objects.equals(id, product.id) && Objects.equals(productName, product.productName) && Objects.equals(unitPrice, product.unitPrice) && Objects.equals(type, product.type) && Objects.equals(unitType, product.unitType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, productName, unitPrice, type, unitType, person, inventory);
	}
}
