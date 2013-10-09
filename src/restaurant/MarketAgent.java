package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentEvent;
import restaurant.CustomerAgent.AgentState;
import restaurant.HostAgent.Table;
import restaurant.gui.WaiterGui;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Restaurant Host Agent
 */

public class MarketAgent extends Agent {
	CookAgent cook;
	

	private String name; 
	public class Order
	{
		WaiterAgent waiter;
		String choice;
		int tableNumber;
		state s;
		Order(WaiterAgent waiter, String choice, int tableNumber, state s)
		{
			this.waiter=waiter;
			this.choice=choice;
			this.tableNumber=tableNumber;
			this.s=s;
		}
	}
	public class Food
	{
		String type;
		int cookingTime;
		int amount;
		int lowThreshold;
		int capacity;
		OrderState orderState;
		Food(String type, int cookingTime, int amount, int lowThreshold, int capacity, OrderState orderstate)
		{
			this.type=type;
			this.cookingTime=cookingTime;
			this.amount=amount;
			this.lowThreshold=lowThreshold;
			this.capacity=capacity;
			this.orderState= orderstate;
		}
	}

	public List<Order> orders = new ArrayList<Order>();
	public enum OrderState
	{nothing};
	public enum state
	{pending, cooking, done, finished};
	public enum reStockingState
	{none, restocking};
	private reStockingState state = reStockingState.none;
	List<String> menuOptions = new ArrayList<String>();{
	    menuOptions.add("chicken");
	    menuOptions.add("beef");
	    menuOptions.add("lamb");
	}
    HashMap<String, Integer> inventory = new HashMap<String, Integer>();
    {
		for (String choice : menuOptions)
		{
			inventory.put(choice, 20);
		}
    }


	
	//Messages
	
	public void msgOrderRestock(HashMap<String, Integer> groceryList)
	{
		state = reStockingState.restocking;
		print("the market recieves the grocery list and changes it's state to restocking");
		stateChanged();
	}
	

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */	
			
	protected boolean pickAndExecuteAnAction() {
		if (state == reStockingState.restocking)
		{	
			ShipOrder();
			return true;
		}
		return false;
	}


	// Actions

	private void ShipOrder()
	{
		print("shipping order");
	}
	


	//utilities


	public void setCook(CookAgent cook)
	{
		this.cook=cook;
	}
	
}

