package org.xiangan.fruitshopweb.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Objects;

/**
 * 營運狀況
 */
@Data
@Entity
@Table(name = "revenue")
public class Revenue {
	
	/**
	 * 主鍵
	 */
	@Basic(optional = false)
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private long id;
	
	/**
	 * 登記日
	 */
	@Basic(optional = false)
	@Column(
		name = "record_date",
		nullable = false
	)
	@NotNull
	@Temporal(TemporalType.DATE)
	@JsonFormat(
		pattern = "yyyy-MM-dd",
		timezone = "Asia/Taipei"
	)
	private Date recordDate;
	
	/**
	 * 總收入
	 */
	@Basic(optional = false)
	@Column(
		name = "gross_income",
		nullable = false,
		precision = 2
	)
	private BigDecimal grossIncome;
	
	/**
	 * 淨收入
	 */
	@Basic(optional = false)
	@Column(
		name = "net_income",
		nullable = false,
		precision = 2
	)
	private BigDecimal netIncome;
	
	/**
	 * 進貨成本
	 */
	@Basic(optional = false)
	@Column(
		name = "purchases_expense",
		nullable = false,
		precision = 2
	)
	private BigDecimal purchasesExpense;
	
	/**
	 * 人事成本
	 */
	@Basic(optional = false)
	@Column(
		name = "personnel_expenses",
		nullable = false,
		precision = 2
	)
	private BigDecimal personnelExpenses;
	
	/**
	 * 雜物成本
	 */
	@Basic(optional = false)
	@Column(
		name = "miscellaneous_expense",
		nullable = false,
		precision = 2
	)
	private BigDecimal miscellaneousExpense;
	
	/**
	 * 損耗
	 */
	@Basic(optional = false)
	@Column(
		name = "wastage",
		nullable = false,
		precision = 2
	)
	private BigDecimal wastage;
	
	public Revenue() {
		recordDate = new Date(
			System.currentTimeMillis()
		);
		grossIncome = BigDecimal.ZERO;
		netIncome = BigDecimal.ZERO;
		purchasesExpense = BigDecimal.ZERO;
		personnelExpenses = BigDecimal.ZERO;
		miscellaneousExpense = BigDecimal.ZERO;
		wastage = BigDecimal.ZERO;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Revenue revenue = (Revenue) o;
		return id == revenue.id && Objects.equals(recordDate, revenue.recordDate) && Objects.equals(grossIncome, revenue.grossIncome) && Objects.equals(netIncome, revenue.netIncome) && Objects.equals(purchasesExpense, revenue.purchasesExpense) && Objects.equals(personnelExpenses, revenue.personnelExpenses) && Objects.equals(miscellaneousExpense, revenue.miscellaneousExpense) && Objects.equals(wastage, revenue.wastage);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, recordDate, grossIncome, netIncome, purchasesExpense, personnelExpenses, miscellaneousExpense, wastage);
	}
}
