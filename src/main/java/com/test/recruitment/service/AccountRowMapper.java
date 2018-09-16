package com.test.recruitment.service;

import com.test.recruitment.entity.Account;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRowMapper implements RowMapper{
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        Account account = new Account();
        account.setId(rs.getString("id"));
        account.setNumber(rs.getString("number"));
        account.setBalance(rs.getBigDecimal("balance"));
        account.setCreationDate(rs.getDate("creationDate"));
        account.setType(rs.getString("type"));
        account.setActive(rs.getBoolean("isActive"));
        return account;
    }
}
