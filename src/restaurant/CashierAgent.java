package restaurant;

import agent.Agent;
import restaurant.HostAgent.Table;
import restaurant.HostAgent.Waiter;
import restaurant.gui.WaiterGui;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Restaurant Host Agent
 */

public class CashierAgent extends Agent {
	public List<WaiterAgent> waiters = new ArrayList<WaiterAgent>();
	public List<Order> receipts = new ArrayList<Order>();
	HashMap<String, Integer> menu = new HashMap<String, Integer>();
	{
	    menu.put("beef", 15);
    	menu.put("chicken", 10);
    	menu.put("lamb", 5);
	}
	
	List<String> menuOptions = new ArrayList<String>();{
	    menuOptions.add("chicken");
	    menuOptions.add("beef");
	    menuOptions.add("lamb");
	}
	private String name; 
	private int marketIndex=0;
	public WaiterGui hostGui = null;
	public class Order
	{
		WaiterAgent waiter;
		CustomerAgent customer;
		String choice;
		receiptState state;
		Order(WaiterAgent waiter, CustomerAgent customer, String choice, receiptState state)
		{
			this.waiter=waiter;
			this.customer=customer;
			this.choice=choice;
			this.state=state;
		}
	}
	enum receiptState
	{pending, complete};
	public class Food
	{
		String type;
		int cookingTime;
		int currentAmount;
		int lowThreshold;
		int capacity;
		OrderState orderState;
		Food(String type, int cookingTime, int amount, int lowThreshold, int capacity, OrderState orderstate)
		{
			this.type=type;
			this.cookingTime=cookingTime;
			this.currentAmount=amount;
			this.lowThreshold=lowThreshold;
			this.capacity=capacity;
			this.orderState= orderstate;
		}
	}
	public List<Order> orders = new ArrayList<Order>();
	boolean incomplete=false;
	boolean ordering=false;
	public enum OrderState
	{nothing};
	public enum state
	{pending, cooking, orderingFood, done};
	HashMap<String, Integer> cookingTimes = new HashMap<String, Integer>();
    {
    	int time=2000;
		for (String choice : menuOptions)
		{
			cookingTimes.put(choice, time);
			time+=2000;
		}
    }
    HashMap<String, Food> foods = new HashMap<String, Food>();
    {
    	for (String choice : menuOptions)
		{
			foods.put(choice, new Food(choice, cookingTimes.get(choice), 0, 3, 10, OrderState.nothing));
		}
    }


	
	//Messages
	
	public void msgComputeCheck(WaiterAgent waiter, CustomerAgent customer, String choice)
	{
		Order order = new Order(waiter, customer, choice, receiptState.pending);
		orders.add(order);
		stateChanged();
	}
	
	
	

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */	
			
	protected boolean pickAndExecuteAnAction() {
		for (Order order : orders) 
		{	
			if (order.state==receiptState.pending)
			{
				CalculateReceipt(order)		;		
				return true;
			}
		}
		return false;
	}


	// Actions

	private void CalculateReceipt(Order order)
	{
		int bill = menu.get(order.choice);
		order.waiter.msgHereIsReceipt(bill, order.customer);
	}
	
	


	//utilities

	public void setGui(WaiterGui gui) {
		hostGui = gui;
	}

	public WaiterGui getGui() {
		return hostGui;
	}
	
	public void setWaiter(WaiterAgent waiter)
	{
		waiters.add(waiter);
	}
	
//	public void setMarket(MarketAgent market)
//	{
//		markets.add(market);
//	}
	
}

