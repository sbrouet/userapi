package com.sbr.userapi.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Entity for mapping users to the database table "USER"
 * 
 * @author sbrouet
 *
 */
@Entity
@Table(name = "USER")
public class User {

	/**
	 * User id. Ids are generated by a sequence generator based on a sequence which
	 * is required to exist inside database
	 */
	@Id
	@SequenceGenerator(name = "userIdSequenceGenerator", sequenceName = "SEQ_USER_ID", initialValue = 10, allocationSize = 1)
	@GeneratedValue(generator = "userIdSequenceGenerator")
	private Long id;

	@Column(name = "first_name", nullable = false, length = 100)
	private String firstName;

	@Column(name = "email", nullable = false, length = 50)
	private String email;

	@Column(name = "password", nullable = false, length = 50)
	private String password;

	/**
	 * Default constructor. This is mandatory for the ORM layer (Hibernate) to be
	 * working
	 */
	public User() {

	}

	public User(final String firstName, final String email, final String password) {
		this.firstName = firstName;
		this.email = email;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Password is purposely changed to '***' characters to avoid exposing it
	 * 
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [id=").append(id).append(", firstName=").append(firstName).append(", email=").append(email)
				.append(", password=***]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(email, firstName, id, password);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		return Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(id, other.id) && Objects.equals(password, other.password);
	}

}