package telran.java2022.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import telran.java2022.accounting.dao.UserRepository;
import telran.java2022.accounting.exceptions.UserNotFoundException;
import telran.java2022.accounting.model.User;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	
	final UserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		User user = repository.findByUserEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
		User user = null;
		if (repository.existsByUserLogin(username)) {
			user = repository.findByUserLogin(username).orElseThrow(() -> new UserNotFoundException());
		} else if (repository.existsByUserEmail(username)) {
				user = repository.findByUserEmail(username).orElseThrow(() -> new UserNotFoundException());
		} else {
			throw new UserNotFoundException();
		}
		String[] roles = user.getRoles().stream()
						.map(r -> "ROLE_" + r.toUpperCase())
						.toArray(String[]::new);
		return new UserProfile(username, user.getPassword(), AuthorityUtils.createAuthorityList(roles));
	}

}
