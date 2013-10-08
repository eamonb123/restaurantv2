package restaurant;

import agent.Agent;
import restaurant.WaiterAgent.Customer;
import restaurant.gui.WaiterGui;

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
public class HostAgent extends Agent {
	static int NTABLES=3;
	public List<CustomerAgent> myWaitingCustomers = new ArrayList<CustomerAgent>();
	public List<WaiterAgent> myWaiters = new ArrayList<WaiterAgent>();
	public Collection<Table> myTables;
	public WaiterGui waiterGui = null;
    private int xPosition=100;
    private int yPosition=250;
    private String name;
	public class Waiter
	{
		WaiterAgent waiter;
		List<Customer> customers = new ArrayList<Customer>();
		boolean isBusy()
		{
			if (customers.size()!=0)
				return true;
			else 
				return false;
		}
	}
	public class Customer
	{
		CustomerAgent customer;
		
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
    
	public HostAgent(String name) {
		super();
		this.name = name;
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

	public void msgIWantToEat(CustomerAgent cust) {
		print("Host is adding customer " + cust.name + " to the waiting customer list");
		myWaitingCustomers.add(cust);
		stateChanged();
	}

	public void msgWakeUp()
	{
		stateChanged();
	}

	public void msgTableIsFree(int tableNumber) {//from animation
		//print("msgAtTable() called");
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
		if (!myWaitingCustomers.isEmpty())
		{
			if (myWaiters.isEmpty())
			{
				print("waiter list is empty. customers waiting in line");
			}
			else
			{
				WaiterAgent waiter = leastBusyWaiter(myWaiters);
				for (Table table : myTables)
				{
					if(!table.isOccupied)
					{
						CustomerAgent customer = myWaitingCustomers.get(0);
						customer.msgWakeUp();
						callWaiter(customer, waiter, table);
						myWaitingCustomers.remove(0);
						return true;
					}
				}
			}
		}
		return false;
	}
	


	// Actions

	private void callWaiter(CustomerAgent cust, WaiterAgent waiter, Table table)
	{
		print("Host is sending message to the waiter to sit customer " + cust.name);
		cust.setWaiter(waiter);
		Point location=tableMap.get(table.tableNumber);
		waiter.msgPleaseSeatCustomer(cust, table.tableNumber, location); //grabbing the only waiter
		table.isOccupied=true;
	}
	
	

	//utilities

	WaiterAgent leastBusyWaiter(List<WaiterAgent> waiterList)
	{
		if (waiterList.isEmpty()) 
			return null;
		else
		{
			int numOfCustomers=waiterList.get(0).myCustomers.size();
			WaiterAgent freeWaiter = new WaiterAgent("scott");
			freeWaiter = waiterList.get(0);
			for(WaiterAgent waiter : waiterList)
			{
				if (waiter.myCustomers.size()<numOfCustomers)
				{
					numOfCustomers=waiter.myCustomers.size();
					freeWaiter=waiter;
				}
			}
			return freeWaiter;
		}

	}
	
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}

	public void setWaiter(WaiterAgent waiter)
	{
		myWaiters.add(waiter);
	}
}

