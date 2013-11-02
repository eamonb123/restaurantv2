package restaurant.interfaces;

import java.util.HashMap;


public interface Cashier {
	public abstract void msgComputeCheck(Waiter waiter, String choice, int tableNumber);
	
	public abstract void msgPayBill(Customer customer, int money, int bill);
	
	public abstract void msgHereIsMarketBill(Market market, int bill, HashMap<String, Integer> outgoingList);
}
