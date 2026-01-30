package sh.surge.kunal.banking.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import sh.surge.kunal.banking.configurations.AppConfig;
import sh.surge.kunal.banking.models.Customer;
import sh.surge.kunal.banking.models.FullName;
import sh.surge.kunal.banking.services.CustomerService;
import com.github.javafaker.Faker;

public class CustomerApp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        Faker faker = new Faker();
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		CustomerService customerService=context.getBean(CustomerService.class);
		
		  List<Long> accountNos=customerService.getAllCustomers()
				  .stream().map(c->c.getAccountNo()).toList();
		  long randomNo= accountNos.get(new Random().nextInt(accountNos.size()));
		  Customer fetchedCustomer=customerService.getCustomerById(randomNo);
		  System.out.println("Fetched Customer Details:");
		  System.out.println(fetchedCustomer);		
	        context.close();
		
	}
	
	public static List<Customer> getAllCustomers(){
		List<Customer> customers=new ArrayList<>();
		for(int i=1;i<=5;i++) {
			Customer customer=new Customer();
			FullName fullName=new FullName();
			Faker faker = new Faker();
			customer.setAccountNo(faker.number().numberBetween(1000000000L, 9999999999L));
			customer.setFullName(fullName);
			customer.getFullName().setFirstName(faker.name().firstName());
			customer.getFullName().setMiddleName(faker.name().nameWithMiddle());
			customer.getFullName().setLastName(faker.name().lastName());
			customer.setEmail(faker.internet().emailAddress());
			customer.setContactNo(Long.parseLong(faker.phoneNumber().subscriberNumber(10)));
			customer.setPassword(faker.internet().password(8, 10, true, true, true));
			customers.add(customer);
		}
		return customers;
	}
	
	
	

}
