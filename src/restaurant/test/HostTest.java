package restaurant.test;

import restaurant.CashierAgent;
import restaurant.HostAgent;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockWaiter;
import junit.framework.TestCase;

public class HostTest extends TestCase {
	HostAgent host;
	MockCustomer customer;
	
	public void setUp() throws Exception{
		super.setUp();		
		host = new HostAgent("host");		
		customer = new MockCustomer("mockcustomer");		
	}	
	
	public void testOneNormalCustomerScenario()
	{
		//setUp(); //runs first before this test!
		customer.setHost(host);//You can do almost anything in a unit test.
		customer.gotHungry();
		assertTrue(customer.log.getLastLoggedEvent().toString().equals("customer is hungry and host is adding customer to list"));
		assertTrue(!host.myWaitingCustomers.isEmpty());
	}
}
