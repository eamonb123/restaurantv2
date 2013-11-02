package restaurant;

import agent.Agent;
import restaurant.gui.RestaurantPanel;
import restaurant.gui.WaiterGui;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HostAgent extends Agent implements Host{
	static int NTABLES=3;
	private String name;
	private RestaurantPanel restPanel;
	public List<MyCustomer> myWaitingCustomers = new ArrayList<MyCustomer>();
	public List<MyWaiter> myWaiters = new ArrayList<MyWaiter>();
	public Collection<Table> myTables;
	public WaiterGui waiterGui = null;
    private int xPosition=100;
    private int yPosition=250;
	private enum WaiterState
	{working, wantsToGoOnBreak, waitingForBreak, onBreak};
	public class MyWaiter
	{
		Waiter waiter;
//		boolean wantsToGoOnBreak=false;
		WaiterState state; 
		List<MyCustomer> customers = new ArrayList<MyCustomer>();
		MyWaiter(Waiter waiter, WaiterState state)
		{
			this.waiter=waiter;
			this.state=state;
		}
	}
	public class MyCustomer
	{
		Customer cust;
		int tableNumber;
		MyCustomer(Customer cust)
		{
			this.cust=cust;
		}
	}
    public class Table 
    {
		int tableNumber;
		int xPos;
		boolean isOccupied=false;
		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}
	}
    
	public HostAgent(String name, RestaurantPanel restPanel) {
		super();
		this.name = name;
		this.restPanel=restPanel;
		myTables = new ArrayList<Table>(NTABLES);
		int xPos = 200;
		for (int ix = 1; ix <= NTABLES; ix++) {
			Table newTable = new Table(ix);
			newTable.xPos = xPos;
			myTables.add(newTable);//how you add to a collections
			xPos+=150;
		}
	}
	
    HashMap<Integer, Point> tableMap = new HashMap<Integer, Point>();
    {
    	for (int i=1; i<=NTABLES; i++)
    	{
    		Point location = new Point(xPosition, yPosition);
    		tableMap.put(i,location);
    		xPosition+=150;
    	}
    }
    
    
	// Messages

	public void msgIWantToEat(Customer cust) {
		print("Host is adding customer to the waiting customer list");
		myWaitingCustomers.add(new MyCustomer(cust));
		stateChanged();
	}

	public void msgWakeUp()
	{
		stateChanged();
	}
	
	public void msgCanIGoOnBreak(Waiter askingWaiter)
	{
		print("the host recieved the message from the waiter asking to go on break and is considering...");
		for (MyWaiter w: myWaiters)
		{
			if (w.waiter==askingWaiter)
			{
				print("found him");
				w.state=WaiterState.wantsToGoOnBreak;
			}
		}
		stateChanged();
	}

	
	
	public void msgTableIsFree(Waiter waiter, Customer customer, int tableNumber) {//from animation
		MyCustomer cust = new MyCustomer(null);
		for (MyWaiter wait: myWaiters)
		{
			for (MyCustomer c: wait.customers)
			{
				if (c.cust==customer)
				{
					cust = c;
				}
			}
			wait.customers.remove(cust);
		}
		for (Table table : myTables) 
		{
			if (table.tableNumber==tableNumber)
			{
				table.isOccupied=false;
			}
		}
		stateChanged();
	}
	
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for (MyWaiter waiter : myWaiters)
		{
			if (waiter.state==WaiterState.wantsToGoOnBreak)
			{
				print("the host is deciding whether the waiter should go on break");
				DecideIfWaiterCanBreak(waiter);
				return true;
			}
		}
		for (MyWaiter waiter : myWaiters)
		{
			if (waiter.state==WaiterState.waitingForBreak && waiter.customers.isEmpty())
			{
				print("the waiter is done serving all his customers and can now go on break");
				DecideIfWaiterCanBreak(waiter);
				return true;
			}
		}
		if (!myWaitingCustomers.isEmpty())
		{
			if (myWaiters.isEmpty())
			{
				print("waiter list is empty. customers waiting in line");
			}
			else
			{
				MyWaiter leastBusyWaiter = leastBusyWaiter(myWaiters);
				for (Table table : myTables)
				{
					if(!table.isOccupied)
					{
						MyCustomer customer = myWaitingCustomers.get(0);
						customer.cust.msgSemaphoreRelease();
						leastBusyWaiter.customers.add(customer);
						callWaiter(customer.cust, leastBusyWaiter.waiter, table);
						myWaitingCustomers.remove(0);
						return true;
					}
				}
			}
		}
		return false;
	}
	


	// Actions

	private void DecideIfWaiterCanBreak(MyWaiter w)
	{
		if (myWaiters.size()<=1)
		{
			print("there is not enough waiters currently working for the waiter to go on break");
			w.state=WaiterState.working;
			w.waiter.msgYouCannotBreak();
			if (restPanel!=null)
			{
				restPanel.showInfo("Waiters", w.waiter.getName());
			}
		}
		else if (!w.customers.isEmpty())
		{
			w.state=WaiterState.waitingForBreak;
			w.waiter.msgYouCannotBreak();
		}
		else
		{
			print("the host allows the waiter to go on break");
			w.state=WaiterState.onBreak;
			w.waiter.msgYouCanBreak();
			myWaiters.remove(w);
		}
	}
	
	
	private void callWaiter(Customer cust, Waiter waiter, Table table)
	{
		print("Host is sending message to the waiter to sit customer");
		cust.setWaiter(waiter);
		Point location=tableMap.get(table.tableNumber);
		waiter.msgPleaseSeatCustomer(cust, table.tableNumber, location); //grabbing the only waiter
		table.isOccupied=true;
	}
	
	

	//utilities


	
	
	MyWaiter leastBusyWaiter(List<MyWaiter> waiterList)
	{
		if (waiterList.isEmpty()) 
			return null;
		else
		{
			List<MyWaiter> waiters = new ArrayList<MyWaiter>();
			for (MyWaiter waiter: waiterList)
			{
				if (waiter.state!=WaiterState.waitingForBreak)
				{
					waiters.add(waiter);
				}
			}
			int numOfCustomers=waiters.get(0).customers.size();
			MyWaiter freeWaiter = waiters.get(0);
			for(MyWaiter waiter : waiters)
			{
				if (waiter.customers.size()<numOfCustomers)
				{
					numOfCustomers=waiter.customers.size();
					freeWaiter=waiter;
				}
			}
			return freeWaiter;
		}

	}
	
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public String getName()
	{
		return name;
	}
	
	public WaiterGui getGui() {
		return waiterGui;
	}

	public void setWaiter(Waiter waiter)
	{
		myWaiters.add(new MyWaiter(waiter, WaiterState.working));
	}

}

