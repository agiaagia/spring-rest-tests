package com.test.recruitment.controller.impl;

import com.test.recruitment.entity.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.test.recruitment.controller.TransactionController;
import com.test.recruitment.json.TransactionResponse;
import com.test.recruitment.service.TransactionService;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of {@link TransactionController}
 * 
 * @author A525125
 *
 */
@Slf4j
@RestController
public class TransactionControllerImpl implements TransactionController {

	private TransactionService transactionService;

	@Autowired
	public TransactionControllerImpl(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	@Override
	public ResponseEntity<Page<TransactionResponse>> getTransactionsByAccount(
			@PathVariable("accountId") String accountId,
			@PageableDefault Pageable p) {
		Page<TransactionResponse> page = transactionService
				.getTransactionsByAccount(accountId, p);
		if (null == page || page.getTotalElements() == 0) {
			log.debug("Cannot find transaction for account {}", accountId);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
		}
		return ResponseEntity.ok().body(page);
	}

	@Override
	public ResponseEntity<?> removeTransactionByAccount(
			@PathVariable("accountId") String accountId,
			@PathVariable("transactionId") String transactionId) {
		transactionService.removeTransactionByAccount(accountId, transactionId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Override
	public ResponseEntity<?> createTransaction(
			@PathVariable("accountId") String accountId,
			@RequestBody Transaction transaction){
		if (null == transaction){
			log.debug("Impossible to create the transaction. The request is malformed.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		transactionService.createTransaction(accountId, transaction);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<?> updateTransaction(
			@PathVariable("accountId") String accountId,
			@PathVariable("transactionId") String transactionId,
			@RequestBody Transaction transaction){
		if (null == transaction){
			log.debug("Impossible to update the transaction. The request is malformed.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		transactionService.updateTransaction(accountId, transactionId, transaction);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
