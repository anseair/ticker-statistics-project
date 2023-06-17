package telran.java2022.accounting.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import telran.java2022.accounting.model.User;
import telran.java2022.accounting.model.UserId;

public interface UserRepository extends CrudRepository<User, UserId>{
	Optional<User> findByUserEmail(String email);
	Optional<User> findByUserLogin(String login);

	boolean existsByUserEmail(String email);
	boolean existsByUserLogin(String login);


}
