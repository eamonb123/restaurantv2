package restaurant.interfaces;

import java.util.HashMap;
import java.util.Map;

public interface Market {
	public abstract void msgOrderRestock(Cook cook, Map<String, Integer> groceryList);
	
	public abstract String getName();
	
	public abstract  void msgHereIsPayment(int bill);
}
