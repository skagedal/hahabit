package tech.skagedal.hahabit.repository;

import org.springframework.data.repository.CrudRepository;
import tech.skagedal.hahabit.model.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
