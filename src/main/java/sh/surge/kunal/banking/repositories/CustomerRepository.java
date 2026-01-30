package sh.surge.kunal.banking.repositories;

import java.util.List;

import sh.surge.kunal.banking.models.Customer;

public interface CustomerRepository {
	
	Customer addCustomer(Customer customer);
	Customer getCustomerById(long accountNo);
	List<Customer> getAllCustomers();
	Customer updateCustomer(Customer customer);
	boolean deleteCustomer(long accountNo);

}
