package com.test.recruitment.service;

import java.util.List;
import java.util.stream.Collectors;

import com.test.recruitment.dao.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.test.recruitment.entity.Account;
import com.test.recruitment.json.AccountDetailsResponse;
import com.test.recruitment.json.AccountResponse;
import com.test.recruitment.json.ErrorCode;
import com.test.recruitment.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

/**
 * Account service
 * 
 * @author A525125
 *
 */
@Slf4j
@Service
public class AccountService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public AccountService() {
	}

	/**
	 * Get account by user
	 * 
	 * @param p
	 *            the pageable information
	 * @return the account list
	 */
	public Page<AccountResponse> getAccounts(Pageable p) {
		String sql = "select * from account";
		List<Account> accounts = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(Account.class));

		return new PageImpl<AccountResponse>(new PageImpl<Account>(accounts.stream()
				.collect(Collectors.toList()))
				.getContent().stream().map(this::mapToAccountResponse)
				.collect(Collectors.toList()));
	}

	/**
	 * Check if an account exists
	 * 
	 * @param accountId
	 *            the account id
	 * @return true if the account exists
	 */
	public boolean isAccountExist(String accountId) {
		String sql = "select * from account where id = '" + accountId + "'";
		try {
			Account account = (Account) jdbcTemplate.queryForObject(sql,
					new Account[]{}, new AccountRowMapper());
			return true;
		} catch (EmptyResultDataAccessException e){
			return false;
		}
	}

	/**
	 * Get account details
	 * 
	 * @param accountId
	 *            the account id
	 * @return
	 */
	public AccountDetailsResponse getAccountDetails(String accountId) {
		log.debug("Find account {}", accountId);

		String sql = "select * from account where id = '" + accountId + "'";
		try {
			Account account = (Account) jdbcTemplate.queryForObject(sql,
					new Account[]{}, new AccountRowMapper());
			return mapToAccountDetailsResponse(account);
		} catch (EmptyResultDataAccessException e){
			throw new ServiceException(ErrorCode.NOT_FOUND_ACCOUNT,
					"Account doesn't exist");
		}
	}

	/**
	 * Map {@link Account} to {@link AccountResponse}
	 * 
	 * @param account
	 *            the entity
	 * @return the response
	 */
	private AccountResponse mapToAccountResponse(Account account) {
		AccountResponse result = new AccountResponse();
		result.setBalance(account.getBalance());
		result.setId(account.getId());
		result.setNumber(account.getNumber());
		result.setType(account.getType());
		return result;
	}

	/**
	 * Map {@link Account} to {@link AccountDetailsResponse}
	 * 
	 * @param account
	 *            the entity
	 * @return the response
	 */
	private AccountDetailsResponse mapToAccountDetailsResponse(Account account) {
		AccountDetailsResponse result = new AccountDetailsResponse();
		result.setActive(account.isActive());
		result.setCreationDate(account.getCreationDate());
		result.setBalance(account.getBalance());
		result.setId(account.getId());
		result.setNumber(account.getNumber());
		result.setType(account.getType());
		return result;
	}

}
