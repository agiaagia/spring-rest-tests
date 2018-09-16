CREATE TABLE account(
  id  VARCHAR(100),
  number VARCHAR(100),
  type VARCHAR(100),
	balance NUMERIC(10,2),
	creationDate TIMESTAMP,
	isActive BOOLEAN
);

CREATE TABLE transaction(
  id  VARCHAR(100),
	accountId VARCHAR(100),
	number VARCHAR(100),
	balance NUMERIC(10,2)
);