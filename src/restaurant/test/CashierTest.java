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
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);                
        assertEquals("The cashier should know of no waiters. It does", cashier.waiters.size(),0);
        cashier.msgComputeCheck(waiter, "beef", 3);
        assertEquals("Cashier should have order size of 1", cashier.orders.size(), 1);
        assertTrue("Cashier's scheduler should have returned true because there is 1 order", cashier.pickAndExecuteAnAction());
        assertEquals("Cashier should be trying to calculate the price of beef", cashier.log.getLastLoggedEvent().toString(), "beef");
        assertEquals("The cashier should have only one order. It doesn't", cashier.orders.size(),1);
	}
    
	public void testGivingChange()
	{
		assertEquals("Cashier should have 0 orders in it. It doesn't.",cashier.orders.size(), 0);                
        assertEquals("The cashier should know of no waiters. It does", cashier.waiters.size(),0);
        cashier.msgComputeCheck(waiter, "beef", 3);
        assertEquals("Cashier should have order size of 1", cashier.orders.size(), 1);
        assertTrue("Cashier's scheduler should have returned true because there is 1 order", cashier.pickAndExecuteAnAction());
        assertEquals("Cashier should be trying to calculate the price of beef", cashier.log.getLastLoggedEvent().toString(), "beef");
        assertEquals("The cashier should have only one order. It doesn't", cashier.orders.size(),1);
        assertEquals("Cashier should have 0 market payments in it. It doesn't.", cashier.payments.size(), 0);  
        cashier.msgPayBill(customer, 20, 15);
        assertEquals("Cashier should have payment size of 1", cashier.payments.size(), 1);
        assertTrue("Cashier's scheduler should have returned true because there is 1 payment", cashier.pickAndExecuteAnAction());
        assertEquals("Cashier should be giving correct amount of change", cashier.log.getLastLoggedEvent().toString(), "giving change 5");
        
	}
	
	public void testPayOneMarket()
	{
		MockMarket market = new MockMarket("market1");
		assertTrue("Market bills list should  be zero before adding markets", cashier.marketBills.isEmpty());                
		cashier.setMarket(market);
		assertTrue("Market bills list should not be zero after adding the market", !cashier.marketBills.isEmpty());                
		cashier.msgHereIsMarketBill(market, 20, null);
        assertTrue("Cashier's scheduler should have returned true because there is an unpaid payment", cashier.pickAndExecuteAnAction());
        assertEquals("Cashier should have enough money to pay the market bill", cashier.log.getLastLoggedEvent().toString(), "have enough money");
        market.msgHereIsPayment(20);
        assertEquals("Market should have accepted the cashier's full payment", market.log.getLastLoggedEvent().toString(), "accepted full payment");
	}
	
	public void testCannotPayOneMarket()
	{
		MockMarket market = new MockMarket("market1");
		assertTrue("Market bills list should  be zero before adding markets", cashier.marketBills.isEmpty());                
		cashier.setMarket(market);
		assertTrue("Market bills list should not be zero after adding the market", !cashier.marketBills.isEmpty());                
		cashier.msgHereIsMarketBill(market, 50000, null);
        assertTrue("Cashier's scheduler should have returned true because there is an unpaid payment", cashier.pickAndExecuteAnAction());
        assertEquals("Cashier should not have enough money to pay the market bill", cashier.log.getLastLoggedEvent().toString(), "don't have enough money");
        market.msgHereIsPayment(cashier.money);
        assertEquals("Market should have accepted the cashier's full payment", market.log.getLastLoggedEvent().toString(), "accepted full payment");
	}
	
	
	public void testPayTwoMarkets()
	{
		MockMarket market1 = new MockMarket("market1");
		MockMarket market2 = new MockMarket("market2");
		
		//step 1 with the first market
		assertTrue("Market bills list should  be zero before adding markets", cashier.marketBills.isEmpty());                
		cashier.setMarket(market1);
		cashier.setMarket(market2);
		assertTrue("Market bills list should not be zero after adding the 2 markets", !cashier.marketBills.isEmpty());                
		cashier.msgHereIsMarketBill(market1, 20, null);
        assertTrue("Cashier's scheduler should have returned true because there is an unpaid payment", cashier.pickAndExecuteAnAction());
        assertEquals("Cashier should have enough money to pay the market bill", cashier.log.getLastLoggedEvent().toString(), "have enough money");
        market1.msgHereIsPayment(20);
        assertEquals("Market should have accepted the cashier's full payment", market1.log.getLastLoggedEvent().toString(), "accepted full payment");
        
        
        //step 2 with the second market
        assertFalse("Cashier's scheduler should have returned false because we haven't run the message the second time", cashier.pickAndExecuteAnAction());
		cashier.msgHereIsMarketBill(market2, 40, null);
        assertTrue("Cashier's scheduler should now return true because we ran the message the second time", cashier.pickAndExecuteAnAction());
        assertEquals("Cashier should have enough money to pay the market bill", cashier.log.getLastLoggedEvent().toString(), "have enough money");
        market2.msgHereIsPayment(40);
        assertEquals("Market should have accepted the cashier's full payment", market1.log.getLastLoggedEvent().toString(), "accepted full payment");
	}
	
	

