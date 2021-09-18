package com.bank.accounts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.bank.accounts.model.Account;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long>{

	Account findByCustomerId(int customerId); 
	
}
