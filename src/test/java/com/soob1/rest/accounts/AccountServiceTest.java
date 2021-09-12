package com.soob1.rest.accounts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

	@Autowired
	UserDetailsService accountService;

	@Autowired
	AccountRepository accountRepository;

	@Test
	@DisplayName("사용자 인증")
	public void findByUsername() {
		// given
		String email = "soob1@email.com";
		String password = "soob1";
		Account account = Account
				.builder()
				.email(email)
				.password(password)
				.roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
				.build();
		accountRepository.save(account);

		// when
		UserDetails userDetails = accountService.loadUserByUsername(email);

		// then
		assertThat(userDetails.getUsername()).isEqualTo(email);
		assertThat(userDetails.getPassword()).isEqualTo(password);
	}

	@Test
	@DisplayName("사용자 인증 실패")
	public void findByUsernameFail() {
		// given
		String email = "random@email.com";

		// when & then
		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
			accountService.loadUserByUsername(email);
		});
		assertThat(exception.getMessage()).contains(email);
	}
}