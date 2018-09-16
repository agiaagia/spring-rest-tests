package com.test.recruitment.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

	private AccountService accountService;

	private TransactionRepository transactionRepository;

	@Autowired
	public TransactionService(AccountService accountService,
							  TransactionRepository transactionRepository) {
		this.accountService = accountService;
		this.transactionRepository = transactionRepository;
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
		return new PageImpl<TransactionResponse>(transactionRepository
				.getTransactionsByAccount(accountId, p).getContent().stream()
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
		transactionRepository.removeTransactionByAccount(transactionId);
	}

	/**
	 * Check if a transaction exists
	 *
	 * @param transactionId
	 *            the transaction id
	 * @return true if the transaction exists
	 */
	public boolean isTransactionExist(String transactionId) {
		return transactionRepository.exists(transactionId);
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
		return transactionRepository.isTransactionLinkedToAccount(accountId, transactionId);
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
		transactionRepository.createTransaction(accountId, transaction);
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
