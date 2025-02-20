package org.xiangan.fruitshopweb.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.xiangan.fruitshopweb.enumType.LevelEnum;
import org.xiangan.fruitshopweb.enumType.LevelEnumConverter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 貨主
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
		name = "person",
		uniqueConstraints = {
				@UniqueConstraint(
						columnNames = {
								"number",
								"company"
						}
				)
		}
)
public class Person implements UserDetails {

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
	 * 暱稱/稱呼
	 */
	@Column(name = "nick_name")
	private String nickName;

	/**
	 * 名字
	 */
	@Basic(optional = false)
	@Column(name = "name")
	@NotNull
	private String name;

	/**
	 * 身分等級
	 */
	@Basic(optional = false)
	@Column(name = "level",nullable = false)
	@Convert(converter = LevelEnumConverter.class)
	@Enumerated(EnumType.STRING)
	@NotNull
	private LevelEnum level;

	/**
	 * 信箱
	 */
	@Column(name = "email")
	private String email;

	/**
	 * 密碼
	 */
	@Basic(optional = false)
	@Column(name = "password",nullable = false)
	@NotNull
	private String password;

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
	 *
	 * @param nickName    姓氏
	 * @param name   	  名字
	 * @param level       身分等級
	 * @param email       信箱
	 * @param password    密碼
	 * @param phoneNumber 連絡電話
	 * @param company     公司行號/統編
	 */
	public Person(String nickName, String name, LevelEnum level, String email, String password,
		String phoneNumber, String company) {
		this.nickName = nickName;
		this.name = name;
		this.level = level;
		this.email = email;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.company = company;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Person person = (Person) o;
		return id == person.id && Objects.equals(nickName, person.nickName) &&
				Objects.equals(name, person.name) && Objects.equals(phoneNumber, person.phoneNumber)
				&& Objects.equals(company, person.company);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, nickName, name, phoneNumber, company);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(level.name()));
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	/**
	 * 用來判斷使用者的帳戶是否過期
	 * @return 如果帳戶已過期，返回false，表示使用者不應該被授權，反之則true。
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * 用來判斷使用者的帳戶是否被鎖定
	 * @return 如果帳戶被鎖定，返回false，表示使用者不應該被授權。
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/**
	 * 用來判斷使用者的認證信息是否過期，例如密碼是否過期
	 * @return 如果認證信息已過期，返回false，表示使用者不應該被授權。
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * 用來判斷使用者是否啟用，如果使用者已被禁用
	 * @return 返回false，表示使用者不應該被授權。
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
}
