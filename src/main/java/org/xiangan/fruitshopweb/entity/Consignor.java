package org.xiangan.fruitshopweb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 貨主
 */
@Data
@Entity
@NoArgsConstructor
@Table(
		name = "consignor",
		uniqueConstraints = {
				@UniqueConstraint(
						columnNames = {
								"number",
								"company"
						}
				)
		}
)
public class Consignor {

	/**
	 * 主鍵
	 */
	@Basic(optional = false)
	@Column(
			name = "id",
			nullable = false
	)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@JsonFormat(shape = JsonFormat.Shape.STRING)
	private long id;

	/**
	 * 姓氏
	 */
	@Basic(optional = false)
	@Column(name = "lastName")
	@NotNull
	private String lastName;

	/**
	 * 名字
	 */
	@Basic(optional = false)
	@Column(name = "firstName")
	@NotNull
	private String firstName;

	/**
	 * 連絡電話
	 */
	@Basic(optional = false)
	@Column(name = "number")
	@NotNull
	private String phoneNumber;

	/**
	 * 公司行號/統編
	 */
	@Basic(optional = false)
	@Column(name = "company")
	@NotNull
	private String company;

	/**
	 * @param lastName    姓氏
	 * @param firstName   名字
	 * @param phoneNumber 連絡電話
	 * @param company     公司行號/統編
	 */
	public Consignor(
			String lastName,
			String firstName,
			String phoneNumber,
			String company
	) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.phoneNumber = phoneNumber;
		this.company = company;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Consignor consignor = (Consignor) o;
		return id == consignor.id && Objects.equals(lastName, consignor.lastName) &&
				Objects.equals(firstName, consignor.firstName) && Objects.equals(phoneNumber, consignor.phoneNumber)
				&& Objects.equals(company, consignor.company);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, lastName, firstName, phoneNumber, company);
	}
}
