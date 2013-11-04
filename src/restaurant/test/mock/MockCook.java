package restaurant.test.mock;

import java.util.Map;

import restaurant.interfaces.Cook;
import restaurant.interfaces.Waiter;


public class MockCook extends Mock implements Cook {

	public MockCook(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgHereIsOrder(Waiter waiter, String choice, int tableNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFufilledCompleteOrder(String name,
			Map<String, Integer> incomingOrder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFufilledPartialOrder(String name,
			Map<String, Integer> incomingOrder) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPickedUpOrder(String order) {
		// TODO Auto-generated method stub
		
	}

	public void CheckInitialFood() {
		// TODO Auto-generated method stub
		
	}



}
