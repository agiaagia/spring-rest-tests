package com.test.recruitment.tests;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Account test
 *
 * @author A525125
 *
 */
public class TransactionTest extends AbstractTest {

	@Test
	public void getTransactions() throws Exception {
		mockMvc.perform(get("/accounts/1/transactions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElements", is(3)))
				.andExpect(jsonPath("$.content[0].number", is("12151885120")))
				.andExpect(jsonPath("$.content[0].balance", is(42.12)));
	}

	@Test
	public void getTransactionsNoContent() throws Exception {
		mockMvc.perform(get("/accounts/2/transactions")).andExpect(
				status().isNoContent());
	}

	@Test
	public void getTransactionsOnUnexistingAccount() throws Exception {
		mockMvc.perform(get("/accounts/3/transactions"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errorCode", is("NOT_FOUND_ACCOUNT")));
	}

	@Test
	public void createTransaction() throws Exception {
		String request = getRequest("createOk");

		mockMvc.perform(post("/accounts/1/transactions")
				.contentType(MediaType.APPLICATION_JSON)
				.content(request))
				.andExpect(status().isCreated());
	}

	@Test
	public void removeUnexistingTransaction() throws Exception {
		mockMvc.perform(delete("/accounts/1/transactions/4"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errorCode", is("NOT_FOUND_TRANSACTION")));
	}

	/**
	 * Get json request from test file
	 *
	 * @param name
	 *            the filename
	 * @return the request
	 * @throws IOException
	 */
	private String getRequest(String name) throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("json/" + name + ".json"), writer);
		return writer.toString();
	}
}
