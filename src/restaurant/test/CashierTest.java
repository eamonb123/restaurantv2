package restaurant.test;

import restaurant.CashierAgent;
import restaurant.test.mock.MockCustomer;
import restaurant.test.mock.MockMarket;
import restaurant.test.mock.MockWaiter;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent();		
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testCalculatingPayment()
	{
		//step 1 of cashier calculating payment
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);                
        assertEquals("The cashier should know of no waiters. It does", cashier.waiters.size(),0);
        cashier.msgComputeCheck(waiter, "beef", 3);
        assertEquals("Cashier should have order size of 1", cashier.orders.size(), 1);
        assertTrue("Cashier's scheduler should have returned true because there is 1 order", cashier.pickAndExecuteAnAction());
        assertEquals("Cashier should be trying to calculate the price of beef", cashier.log.getLastLoggedEvent().toString(), "beef");
        assertEquals("The cashier should have only one order. It doesn't", cashier.orders.size(),1);
        
        
        //step 2 of cashier getting money from customer
        assertEquals("Cashier should have 0 market payments in it. It doesn't.", cashier.payments.size(), 0);  
        cashier.msgPayBill(customer, 20, 15);
        assertEquals("Cashier should have payment size of 1", cashier.payments.size(), 1);
        assertTrue("Cashier's scheduler should have returned true because there is 1 payment", cashier.pickAndExecuteAnAction());
        assertEquals("Cashier should be giving correct amount of change", cashier.log.getLastLoggedEvent().toString(), "giving change 5");
        
	}
	
	public void testPayMarketFull()
	{
		MockMarket market1 = new MockMarket("market1");
		MockMarket market2 = new MockMarket("market2");
		assertTrue("Market bills list should  be zero before adding markets", cashier.marketBills.isEmpty());                
		cashier.setMarket(market1);
		cashier.setMarket(market2);
		assertTrue("Market bills list should not be zero after adding the 2 markets", !cashier.marketBills.isEmpty());                
		cashier.msgHereIsMarketBill(market1, 20, null);
        assertTrue("Cashier's scheduler should have returned true because there is an unpaid payment", cashier.pickAndExecuteAnAction());
        assertEquals("Cashier should have enough money to pay the market bill", cashier.log.getLastLoggedEvent().toString(), "have enough money");
        market1.msgHereIsPayment(20);
	}
		//setUp() runs first before this test!
//		cashier.msgComputeCheck(waiter, "beef", 3);	
//		assertTrue(customer.log.getLastLoggedEvent().toString().equals("calculating the receipt for beef"));
		//check preconditions
//		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.bills.size(), 0);		
//		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsBill is called. Instead, the Cashier's event log reads: "
//						+ cashier.log.toString(), 0, cashier.log.size());
//		
		//step 1 of the test
		//public Bill(Cashier, Customer, int tableNum, double price) {
//		Bill bill = new Bill(cashier, customer, 2, 7.98);
//		cashier.HereIsBill(bill);//send the message from a waiter

		//check postconditions for step 1 and preconditions for step 2
//		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
//						+ waiter.log.toString(), 0, waiter.log.size());
//		
//		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.bills.size(), 1);
//		
//		assertFalse("Cashier's scheduler should have returned false (no actions to do on a bill from a waiter), but didn't.", cashier.pickAndExecuteAnAction());
//		
//		assertEquals(
//				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
//						+ waiter.log.toString(), 0, waiter.log.size());
//		
//		assertEquals(
//				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
//						+ waiter.log.toString(), 0, waiter.log.size());
		
		//step 2 of the test
//		cashier.ReadyToPay(customer, bill);
		
		//check postconditions for step 2 / preconditions for step 3
//		assertTrue("CashierBill should contain a bill with state == customerApproached. It doesn't.",
//				cashier.bills.get(0).state == cashierBillState.customerApproached);
//		
//		assertTrue("Cashier should have logged \"Received ReadyToPay\" but didn't. His log reads instead: " 
//				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received ReadyToPay"));
//
//		assertTrue("CashierBill should contain a bill of price = $7.98. It contains something else instead: $" 
//				+ cashier.bills.get(0).bill.netCost, cashier.bills.get(0).bill.netCost == 7.98);
//		
//		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
//					cashier.bills.get(0).bill.customer == customer);
//		
		
//		//step 3
//		//NOTE: I called the scheduler in the assertTrue statement below (to succintly check the return value at the same time)
//		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
//					cashier.pickAndExecuteAnAction());
//		
//		//check postconditions for step 3 / preconditions for step 4
//		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourTotal\" with the correct balance, but his last event logged reads instead: " 
//				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourTotal from cashier. Total = 7.98"));
//	
//			
//		assertTrue("Cashier should have logged \"Received HereIsMyPayment\" but didn't. His log reads instead: " 
//				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received HereIsMyPayment"));
//		
//		
//		assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" 
//				+ cashier.bills.get(0).changeDue, cashier.bills.get(0).changeDue == 0);
//		
//		
//		
//		//step 4
//		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
//					cashier.pickAndExecuteAnAction());
//		
//		//check postconditions for step 4
//		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
//				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourChange from cashier. Change = 0.0"));
//	
//		
//		assertTrue("CashierBill should contain a bill with state == done. It doesn't.",
//				cashier.bills.get(0).state == cashierBillState.done);
//		
//		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
//				cashier.pickAndExecuteAnAction());
//		
	
	}//end one normal customer scenario
	
	

