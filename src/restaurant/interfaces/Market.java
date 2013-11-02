package restaurant.interfaces;

import java.util.HashMap;

public interface Market {
	public abstract void msgOrderRestock(Cook cook, HashMap<String, Integer> groceryList);
	
	public abstract String getName();
	
	public abstract  void msgHereIsPayment(int bill);
}
