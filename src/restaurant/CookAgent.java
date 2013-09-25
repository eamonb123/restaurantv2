package restaurant;

import agent.Agent;
import restaurant.HostAgent.Table;
import restaurant.gui.HostGui;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class CookAgent extends Agent {
	static int NTABLES=3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	//	public List<Order> orders;
//	Timer timer;
//	enum state
//	{
//		pending, cooking, done, finished
//	}
	WaiterAgent waiter;
	
	List<String> menuOptions = new ArrayList<String>();{
	    menuOptions.add("chicken");
	    menuOptions.add("beef");
	    menuOptions.add("lamb");
	}
	public class Order
	{
		WaiterAgent waiter;
		String choice;
		int tableNumber;
		state s;
		Order(WaiterAgent waiter, String choice, int tableNumber)
		{
			this.waiter=waiter;
			this.choice=choice;
			this.tableNumber=tableNumber;
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
			{
				cookingTime.put(choice, time);
				time+=1000;
			}
		}
    }



	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name; 
	private boolean isServing=false;
	public HostGui hostGui = null;
	
	
	//Messages
	
	public void HereIsOrder(WaiterAgent waiter, String choice, int tableNumber)
	{
		Order order = new Order(waiter, choice, tableNumber);
		order.s=state.pending;
		orders.add(order);
	}
	
	public void TimerDone(Order order)
	{
		order.s=state.done;
	}

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	
//	for (Order o : orders) 
//	{
//		if (o.s==done)
//		{
//			orderDone();
//			remove(o);
//		}
//	}
//	
//	for (Order o : orders) 
//	{
//		if (o.s==pending)
//		{
//			cookit(o);
//		}
//	}
			
			
			
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
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

	private void CookIt(Order order)
	{
		//DoCooking(order);
		CookingTimer(order);
		TimerDone(order);	
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
		order.waiter.msgOrderIsReady(order.choice, order.tableNumber);
		orders.remove(order);
	}
	
//	
//	timer.start(run(timerDone(o)))
//	{
//		o.state=cooking;
//		cookingtime.get(o.choice);
//	}
//	
//	plateIt(Order o)
//	{
//		doPlating(o);
//		o.w.orderDone(o.choice, o.table);
//		orders.remove(o);
//	}
	


	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

//	private class Order
//	{
//		Waiter w;
//		String choice;
//		int tableNum;
//		state s;
//	}
	
	private class Table {
		CustomerAgent occupiedBy;
		int tableNumber;
		int xPos;
		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(CustomerAgent cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		CustomerAgent getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
}

