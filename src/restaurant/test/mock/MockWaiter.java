package restaurant.test.mock;
import java.awt.Point;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;


public class MockWaiter extends Mock implements Waiter {

	public EventLog log = new EventLog();

	public MockWaiter(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	

	
	@Override
	public void msgDoneEating(Customer customer) {
		log.add(new LoggedEvent("customer is done eating"));
	}

	@Override
	public void msgHereIsReceipt(int bill, int tableNumber) {
		log.add(new LoggedEvent("received receipt"));
		
	}
	
	@Override
	public void msgAtTable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTryToGoOnBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgYouCannotBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgYouCanBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPleaseSeatCustomer(Customer customer, Point Location, int tableNumber, Point loc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgReadyToOrder(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsChoice(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOutOfFood(String choice, int tableNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOrderIsReady(String choice, int tableNumber, Point foodLocation) {
		// TODO Auto-generated method stub
		
	}



	

	@Override
	public void msgSetHomeBase(Point location) {
		// TODO Auto-generated method stub
		
	}








	
}
