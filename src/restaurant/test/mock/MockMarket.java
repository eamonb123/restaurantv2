package restaurant.test.mock;

import java.util.Map;

import restaurant.interfaces.Cook;
import restaurant.interfaces.Market;

public class MockMarket extends Mock implements Market{

	public EventLog log = new EventLog();

	public MockMarket(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgOrderRestock(Cook cook, Map<String, Integer> groceryList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsPayment(int bill) {
		log.add(new LoggedEvent("accepting payment"));
	}

}
