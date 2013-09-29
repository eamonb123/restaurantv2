package restaurant;

import agent.Agent;
import restaurant.HostAgent.Table;
import restaurant.gui.WaiterGui;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Restaurant Host Agent
 */

public class CookAgent extends Agent {
	WaiterAgent waiter;
	List<String> menuOptions = new ArrayList<String>();{
	    menuOptions.add("chicken");
	    menuOptions.add("beef");
	    menuOptions.add("lamb");
	}
	private String name; 
	public WaiterGui hostGui = null;
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
	public List<Order> orders = new ArrayList<Order>();
	public enum state
	{pending, cooking, done, finished};
	HashMap<String, Integer> cookingTime = new HashMap<String, Integer>();
    {
    	int time=2000;
		for (String choice : menuOptions)
		{
			cookingTime.put(choice, time);
			time+=2000;
		}
    }


	
	//Messages
	
	public void msgHereIsOrder(WaiterAgent waiter, String choice, int tableNumber)
	{
		
		Order order = new Order(waiter, choice, tableNumber, state.pending);
		print("the cook recieves the order " + order.choice + " and puts it on a list of orders");
		orders.add(order);
		stateChanged();
	}
	

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */	
			
	protected boolean pickAndExecuteAnAction() {
		for (Order order : orders) 
		{	
			if (order.s==state.pending)
			{
				CookIt(order);
				return true;
			}
		}
		for (Order order : orders) 
		{
			if (order.s==state.done)
			{
				PlateIt(order);
				return true;
			}
		}
		return false;
	}


	// Actions

	private void CookIt(Order order) //can cook multiple things at a time with no decrease in speed
	{
		//DoCooking(order);
		print("the cook begins cooking the " + order.choice);
		order.s = state.cooking; //put this inside timer class when u implement it
		CookingTimer(order);
		print("the cook is done cooking the " + order.choice);
		order.s = state.done;
	}
	
	private void CookingTimer(Order order)
	{
		int time = cookingTime.get(order.choice);
		try
		{
			Thread.sleep(time);
		}
		catch(Exception e)
		{
			System.out.println("Exception caught");
		}
	}
	
	private void PlateIt(Order order)
	{
		//DoPlating(order);
		print("the cook notifies the waiter that the " + order.choice + " is ready to be served to the customer");
		order.waiter.msgOrderIsReady(order.choice, order.tableNumber);
		orders.remove(order);
	}
	


	//utilities

	public void setGui(WaiterGui gui) {
		hostGui = gui;
	}

	public WaiterGui getGui() {
		return hostGui;
	}

	
	
}

