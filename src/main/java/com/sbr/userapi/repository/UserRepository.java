package com.sbr.userapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import com.sbr.userapi.model.User;

/**
 * A {@link JpaRepository} that allows operations on {@link User users}
 * 
 * @author sbrouet
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, QueryByExampleExecutor<User> {
	public List<User> findByFirstName(String firstName);
}
