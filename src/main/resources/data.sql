INSERT INTO account (id, number, type, balance, creationDate, isActive) VALUES
  ('1', '01000251215', 'SAVING', '4210.42', CURRENT_DATE, true),
  ('2', '01000251216', 'CURRENT', '25.12', CURRENT_DATE, false);

INSERT INTO transaction (id, accountId, number, balance) VALUES
  ('1', '1', '12151885120', '42.12'),
  ('2', '1', '12151885121', '456.00'),
  ('3', '1', '12151885122', '-12.12');