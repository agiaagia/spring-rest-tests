package com.test.recruitment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.hsqldb.types.Types;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.test.recruitment.dao.TransactionRepository;
import com.test.recruitment.entity.Transaction;
import com.test.recruitment.json.ErrorCode;
import com.test.recruitment.json.TransactionResponse;
import com.test.recruitment.exception.ServiceException;

/**
 * Transaction service
 *
 * @author A525125
 *
 */
@Service
public class TransactionService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private AccountService accountService;

	@Autowired
	public TransactionService(AccountService accountService,
							  TransactionRepository transactionRepository) {
		this.accountService = accountService;
	}

	/**
	 * Get transactions by account
	 *
	 * @param accountId
	 *            the account id
	 * @param p
	 *            the pageable object
	 * @return
	 */
	public Page<TransactionResponse> getTransactionsByAccount(String accountId,
															  Pageable p) {
		if (!accountService.isAccountExist(accountId)) {
			throw new ServiceException(ErrorCode.NOT_FOUND_ACCOUNT,
					"Account doesn't exist");
		}
		String sql = "select * from transaction where accountId = " + accountId;
		List<Transaction> transactions = jdbcTemplate.query(sql,
				new BeanPropertyRowMapper(Transaction.class));

		return new PageImpl<TransactionResponse>(new PageImpl<Transaction>(transactions.stream()
				.collect(Collectors.toList())).getContent().stream()
				.map(this::map).collect(Collectors.toList()));
	}

	/**
	 * Remove a transaction from an account
	 *
	 * @param accountId
	 *            the account id
	 * @param transactionId
	 *            the transaction id to remove
	 *
	 */
	public void removeTransactionByAccount(String accountId,
										   String transactionId) {
		if (!accountService.isAccountExist(accountId)) {
			throw new ServiceException(ErrorCode.NOT_FOUND_ACCOUNT,
					"Account doesn't exist");
		}
		if (!isTransactionExist(transactionId)) {
			throw new ServiceException(ErrorCode.NOT_FOUND_TRANSACTION,
					"Transaction doesn't exist");
		}
		if (!isTransactionLinkedToAccount(accountId, transactionId)) {
			throw new ServiceException(ErrorCode.FORBIDDEN_TRANSACTION,
					"Transaction is not linked to the account");
		}
		String sql = "delete from transaction where id = " + transactionId;
		jdbcTemplate.update(sql);
	}

	/**
	 * Check if a transaction exists
	 *
	 * @param transactionId
	 *            the transaction id
	 * @return true if the transaction exists
	 */
	public boolean isTransactionExist(String transactionId) {
		String sql = "select * from transaction where id = " + transactionId;
		try {
			Transaction transaction = (Transaction) jdbcTemplate.queryForObject(sql,
					new Transaction[]{}, new TransactionRowMapper());
			return true;
		} catch(EmptyResultDataAccessException e){
			return false;
		}
	}

	/**
	 * Check if a transaction is linked to a given account
	 *
	 * @param accountId
	 *            the account id
	 * @param transactionId
	 *            the transaction id
	 * @return true if the transaction is linked to the account
	 */
	public boolean isTransactionLinkedToAccount(String accountId, String transactionId) {
		String sql = "select * from transaction where id = " + transactionId + " and accountId = " + accountId;
		try {
			Transaction transaction = (Transaction) jdbcTemplate.queryForObject(sql,
					new Transaction[]{}, new TransactionRowMapper());
			return true;
		} catch (EmptyResultDataAccessException e){
			return false;
		}
	}

	/**
	 * Add a new transaction to an account
	 *
	 * @param accountId
	 *            the account id
	 * @param transaction
	 *            the transaction to create
	 *
	 */
	public void createTransaction(String accountId, Transaction transaction){
		if (!accountService.isAccountExist(accountId)) {
			throw new ServiceException(ErrorCode.NOT_FOUND_ACCOUNT,
					"Account doesn't exist");
		}
		String sql = "insert into transaction(id, accountId, balance, number) values (?,?,?,?)";
		Object[] params = new Object[] { transaction.getId(), transaction.getAccountId(), transaction.getBalance(), transaction.getNumber() };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.DECIMAL, Types.VARCHAR };

		jdbcTemplate.update(sql, params, types);
	}

	/**
	 * Update a transaction
	 *
	 * @param accountId
	 *            the account id
	 * @param transactionId
	 *            the transaction id
	 * @param transaction
	 *            the transaction to update
	 *
	 */
	public void updateTransaction(String accountId, String transactionId, Transaction transaction){
		if (!accountService.isAccountExist(accountId)) {
			throw new ServiceException(ErrorCode.NOT_FOUND_ACCOUNT,
					"Account doesn't exist");
		}
		if (!isTransactionExist(transactionId)) {
			throw new ServiceException(ErrorCode.NOT_FOUND_TRANSACTION,
					"Transaction doesn't exist");
		}
		if (!isTransactionLinkedToAccount(accountId, transactionId)) {
			throw new ServiceException(ErrorCode.FORBIDDEN_TRANSACTION,
					"Transaction is not linked to the account");
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("update transaction set ");
		boolean isSeparatorNeeded = false;
		if (null != transaction.getAccountId()){
			stringBuilder.append("accountId = '").append(transaction.getAccountId()).append("'");
			isSeparatorNeeded = true;
		}
		if (null != transaction.getBalance()){
			if (isSeparatorNeeded){
				stringBuilder.append(", ");
			}
			stringBuilder.append("balance = '").append(transaction.getBalance()).append("'");
			isSeparatorNeeded = true;
		}
		if (null != transaction.getNumber()){
			if (isSeparatorNeeded){
				stringBuilder.append(", ");
			}
			stringBuilder.append("number = '").append(transaction.getNumber()).append("'");
		}
		stringBuilder.append(" where id = '").append(transactionId).append("'");

		jdbcTemplate.update(stringBuilder.toString());
	}

	/**
	 * Map {@link Transaction} to {@link TransactionResponse}
	 *
	 * @param transaction
	 * @return
	 */
	private TransactionResponse map(Transaction transaction) {
		TransactionResponse result = new TransactionResponse();
		result.setBalance(transaction.getBalance());
		result.setId(transaction.getId());
		result.setNumber(transaction.getNumber());
		return result;
	}

}
