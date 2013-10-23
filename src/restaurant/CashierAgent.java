package restaurant;

import agent.Agent;
import restaurant.HostAgent.Table;
import restaurant.gui.WaiterGui;
import restaurant.interfaces.Cashier;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import restaurant.interfaces.Waiter;
/**
 * Restaurant Host Agent
 */

public class CashierAgent extends Agent implements Cashier{
	public List<Waiter> waiters = new ArrayList<Waiter>();
	public List<Order> receipts = new ArrayList<Order>();
	HashMap<String, Integer> menu = new HashMap<String, Integer>();
	{
	    menu.put("beef", 15);
    	menu.put("chicken", 10);
    	menu.put("lamb", 5);
	}
	private String name; 
	public WaiterGui waiterGui = null;
	public class Order
	{
		Waiter waiter;
		String choice;
		int tableNumber;
		receiptState state;
		Order(Waiter waiter, String choice, int tableNumber, receiptState state)
		{
			this.waiter=waiter;
			this.choice=choice;
			this.tableNumber=tableNumber;
			this.state=state;
		}
	}
	enum receiptState
	{pending, complete};
	public List<Order> orders = new ArrayList<Order>();
	boolean incomplete=false;
	boolean ordering=false;
	public enum OrderState
	{nothing};



	
	//Messages
	
	public void msgComputeCheck(Waiter waiter, String choice, int tableNumber)
	{
		Order order = new Order(waiter, choice, tableNumber, receiptState.pending);
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
				order.state=receiptState.complete;
				CalculateReceipt(order);		
				return true;
			}
		}
		return false;
	}


	// Actions

	private void CalculateReceipt(Order order)
	{
		int bill = menu.get(order.choice);
		order.waiter.msgHereIsReceipt(bill, order.tableNumber);
	}
	
	


	//utilities

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	public void setWaiter(WaiterAgent waiter)
	{
		waiters.add(waiter);
	}
	
}

