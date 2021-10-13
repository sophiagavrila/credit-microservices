INSERT INTO `customer` (`name`,`email`,`mobile_number`,`create_dt`)
 VALUES ('Larry King','larry@mail.com','9876548337',CURDATE());
 
 INSERT INTO `accounts` (`customer_id`, `account_number`, `account_type`, `branch_address`, `create_dt`)
 VALUES (1, 186576453, 'Savings', '123 Main Street, New York', CURDATE());