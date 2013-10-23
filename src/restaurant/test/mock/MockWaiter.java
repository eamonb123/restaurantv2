package restaurant.test.mock;
import java.awt.Point;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;


public class MockWaiter extends Mock implements Waiter {

	public MockWaiter(String name) {
		super(name);
		// TODO Auto-generated constructor stub
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
	public void msgPleaseSeatCustomer(Customer customer,
			int tableNumber, Point loc) {
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
	public void msgOrderIsReady(String choice, int tableNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneEating(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsReceipt(int bill, int tableNumber) {
		// TODO Auto-generated method stub
		
	}








	
}
