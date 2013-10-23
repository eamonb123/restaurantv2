package restaurant.interfaces;

import java.util.HashMap;
import java.util.Map;

import restaurant.WaiterAgent;
import restaurant.CookAgent.Food;
import restaurant.CookAgent.Order;
import restaurant.CookAgent.state;


public interface Cook {
	public abstract void msgHereIsOrder(Waiter waiter, String choice, int tableNumber);

	public abstract void msgFufilledCompleteOrder(HashMap<String, Integer> incomingOrder);
	
	public abstract void msgFufilledPartialOrder(HashMap<String, Integer> incomingOrder);
}
