package restaurant.test.mock;

import java.util.Map;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;

public class MockCashier extends Mock implements Cashier{

	public MockCashier(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgComputeCheck(Waiter waiter, String choice, int tableNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPayBill(Customer customer, int money, int bill) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsMarketBill(Market market, int bill,
			Map<String, Integer> outgoingList) {
		// TODO Auto-generated method stub
		
	}

}
