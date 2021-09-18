/**
 * 
 */
package com.bank.accounts.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CustomerDetails {
	
	private Account accounts;
	private List<Loan> loans;
	private List<Card> cards;
	
	

}
