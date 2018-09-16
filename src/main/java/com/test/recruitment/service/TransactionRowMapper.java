package com.test.recruitment.service;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.test.recruitment.entity.Transaction;
import org.springframework.jdbc.core.RowMapper;

public class TransactionRowMapper  implements RowMapper{
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getString("id"));
        transaction.setNumber(rs.getString("number"));
        transaction.setBalance(rs.getBigDecimal("balance"));
        transaction.setAccountId(rs.getString("accountId"));
        return transaction;
    }
}
