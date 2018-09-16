package com.test.recruitment.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.test.recruitment.entity.Transaction;

/**
 * Transaction repository
 * 
 * @author A525125
 *
 */
public interface TransactionRepository {


	/**
	 * Get transaction by Id
	 *
	 * @param id
	 *            id of the transaction to get
	 * @return the transaction corresponding to the given id or null
	 */
	Transaction findById(String id);

	/**
	 * Get transactions by account
	 * 
	 * @param accountId
	 *            the account id
	 * @param p
	 *            the pageable information
	 * @return
	 */
	Page<Transaction> getTransactionsByAccount(String accountId, Pageable p);

	/**
	 * Check if a transaction exists
	 *
	 * @param transactionId
	 *            the transaction id
	 * @return true if the transaction exists
	 */
	boolean exists(String transactionId);

	/**
	 * Remove a transaction from an account
	 *
	 * @param transactionId
	 *            the transaction id to remove
	 *
	 */
	void removeTransactionByAccount(String transactionId);

	/**
	 * Check if a transaction is linked to a given account
	 *
	 * @param accountId
	 *            the account id
	 * @param transactionId
	 *            the transaction id
	 * @return true if the transaction is linked to the account
	 */
	boolean isTransactionLinkedToAccount(String accountId, String transactionId);
}
