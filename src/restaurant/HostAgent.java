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
	public List<MyCustomer> myWaitingCustomers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	public List<MyWaiter> myWaiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public List<Table> myTables;
	public WaiterGui waiterGui = null;
    private int xPosition=100;
    private int yPosition=250;
	private enum WaiterState
	{working, wantsToGoOnBreak, waitingForBreak, onBreak};
	public class MyWaiter
	{
		Waiter waiter;
		WaiterState state; 
		Point homeBase = new Point();
		boolean assignedHomeBase=false;
		List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
		MyWaiter(Waiter waiter, WaiterState state)
		{
			this.waiter=waiter;
			this.state=state;
		}
	}
	public class MyCustomer
	{
		Customer cust;
		Point waitingLocation = new Point();
		boolean waitingInLine = false;
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
	public List<WaitingSpot> waitingSpots = Collections.synchronizedList(new ArrayList<WaitingSpot>());
    public class WaitingSpot 
    {
		boolean isOccupied=false;
		Point location = new Point();
		WaitingSpot(int i)
		{
			this.isOccupied=false;
			this.location=waitingSpot.get(i);
		}
	}
    Map<Integer, Point> waitingSpot = new HashMap<Integer, Point>();
    {
    	int xPosition=20;
    	int yPosition=20;
    	for (int i=0; i<=10; i++)
    	{
    		Point location = new Point(xPosition, yPosition);
    		waitingSpot.put(i,location);
    		xPosition+=30;
    	}
    }
    Map<Integer, Point> tableMap = Collections.synchronizedMap(new HashMap<Integer, Point>());
    {
    	for (int i=1; i<=NTABLES; i++)
    	{
    		Point location = new Point(xPosition, yPosition);
    		tableMap.put(i,location);
    		xPosition+=150;
    	}
    }
    public List<WaiterHomeBase> waiterHomeBases = Collections.synchronizedList(new ArrayList<WaiterHomeBase>());
    class WaiterHomeBase
    {
    	boolean isOccupied=false;
    	Point location = new Point();
    	WaiterHomeBase(int i)
		{
			this.isOccupied=false;
			this.location=waiterHomeBase.get(i);
		}
    }
    Map<Integer, Point> waiterHomeBase = Collections.synchronizedMap(new HashMap<Integer, Point>());
    {	
    	int xPosition = 200;
    	int yPosition = 80;
    	for (int i=0; i<5; i++)
    	{
    		Point location = new Point(xPosition, yPosition);
    		waiterHomeBase.put(i,location);
    		xPosition+=30;
    	}
    	xPosition = 200;
    	yPosition += 30;
    	for (int i=5; i<10; i++)
    	{
    		Point location = new Point(xPosition, yPosition);
    		waiterHomeBase.put(i,location);
    		xPosition+=30;
    	}
    	xPosition = 200;
    	yPosition += 30;
    	for (int i=10; i<15; i++)
    	{
    		Point location = new Point(xPosition, yPosition);
    		waiterHomeBase.put(i,location);
    		xPosition+=30;
    	}
    	xPosition = 200;
    	yPosition += 30;
    	for (int i=15; i<20; i++)
    	{
    		Point location = new Point(xPosition, yPosition);
    		waiterHomeBase.put(i,location);
    		xPosition+=30;
    	}
    }
    
    
	public HostAgent(String name, RestaurantPanel restPanel) {
		super();
		this.name = name;
		this.restPanel=restPanel;
		myTables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
	    for (int i=0; i<10; i++)
	    {
	    	WaitingSpot spot = new WaitingSpot(i);
	    	waitingSpots.add(spot);
	    }
	    for (int i=0; i<20; i++)
	    {
	    	WaiterHomeBase homeBase = new WaiterHomeBase(i);
	    	waiterHomeBases.add(homeBase);
	    }
		int xPos = 200;
		for (int ix = 1; ix <= NTABLES; ix++) {
			Table newTable = new Table(ix);
			newTable.xPos = xPos;
			myTables.add(newTable);//how you add to a collections
			xPos+=150;
		}
	}

    
    
	// Messages

	public void msgIWantToEat(Customer cust) {
		print("Host is adding customer " + cust.getName() + " to the waiting customer list");
		myWaitingCustomers.add(new MyCustomer(cust));
		stateChanged();
	}
	
	public void msgPickedUpCustomer(Point loc)
	{
		print("host has reached customer and marks his location as available");
		synchronized(waitingSpots)
		{
		for (WaitingSpot waitingSpot: waitingSpots)
		{
			if (waitingSpot.location.equals(loc))
			{
				waitingSpot.isOccupied=false;
			}
		}
		}
		stateChanged();
	}

	public void msgWakeUp()
	{
		stateChanged();
	}
	
	public void msgCanIGoOnBreak(Waiter askingWaiter)
	{
		print("the host recieved the message from the waiter asking to go on break and is considering...");
		synchronized(myWaiters)
		{
		for (MyWaiter w: myWaiters)
		{
			if (w.waiter==askingWaiter)
			{
				print("found him");
				w.state=WaiterState.wantsToGoOnBreak;
			}
		}
		}
		stateChanged();
	}

	
	
	public void msgTableIsFree(Waiter waiter, Customer customer, int tableNumber) {//from animation
		MyCustomer cust = new MyCustomer(null);
		synchronized(myWaiters)
		{
		for (MyWaiter wait: myWaiters)
		{
			synchronized(wait.customers)
			{
			for (MyCustomer c: wait.customers)
			{
				if (c.cust==customer)
				{
					cust = c;
				}
			}
			}
			synchronized(waitingSpots)
			{
			for (WaitingSpot spot: waitingSpots)
			{
				if (spot.equals(cust.waitingLocation))
				{
					spot.isOccupied=false;
				}
			}
			}
			wait.customers.remove(cust);
		}
		}
		synchronized(myTables)
		{
		for (Table table : myTables) 
		{
			if (table.tableNumber==tableNumber)
			{
				table.isOccupied=false;
			}
		}
		}
		stateChanged();
	}
	
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		synchronized(myWaiters)
		{
		for (MyWaiter waiter : myWaiters)
		{
			if (waiter.state==WaiterState.wantsToGoOnBreak)
			{
				print("the host is deciding whether the waiter should go on break");
				DecideIfWaiterCanBreak(waiter);
				return true;
			}
		}
		}
		synchronized(myWaiters)
		{
		for (MyWaiter waiter : myWaiters)
		{
			if (waiter.state==WaiterState.waitingForBreak && waiter.customers.isEmpty())
			{
				print("the waiter is done serving all his customers and can now go on break");
				DecideIfWaiterCanBreak(waiter);
				return true;
			}
		}
		}
		synchronized(myWaiters)
		{
		for (MyWaiter waiter : myWaiters)
		{
			synchronized(waiterHomeBases)
			{
			for (WaiterHomeBase homeBase: waiterHomeBases)
			{
				if (!waiter.assignedHomeBase && !homeBase.isOccupied)
				{
					print("the waiter is assigned to the first home base location");
					GoToHomeBase(waiter, homeBase);
					return true;
				}			
			}
			}
		}
		}
		if (!myWaitingCustomers.isEmpty())
		{
			if (myWaiters.isEmpty())
			{
				synchronized(myWaitingCustomers)
				{
				for (MyCustomer customer : myWaitingCustomers)	
				{	
					synchronized(waitingSpots)
					{
					for (WaitingSpot waitingSpot: waitingSpots)
					{
						if (!waitingSpot.isOccupied && !customer.waitingInLine)
						{
							WaitInLine(customer, waitingSpot);
							return true;
						}
					}
					}
				}
				}
				print("waiter list is empty. customers waiting in line");
			}
			else
			{
				synchronized(myWaitingCustomers)
				{
				for (MyCustomer c: myWaitingCustomers)
				{
					synchronized(waitingSpots)
					{
					for (WaitingSpot w: waitingSpots)
					{
						if (!c.waitingInLine && !w.isOccupied)
						{
							WaitInLine(c, w);
						}
					}
					}
				}
				}
				MyWaiter leastBusyWaiter = leastBusyWaiter(myWaiters);
				synchronized(myTables)
				{
				for (Table table : myTables)
				{
					synchronized(waitingSpots)
					{
					for (WaitingSpot waitingSpot: waitingSpots)
					{
						if(!table.isOccupied && !waitingSpot.isOccupied)
						{
							MyCustomer customer = myWaitingCustomers.get(0);
							customer.cust.msgSemaphoreRelease();
							leastBusyWaiter.customers.add(customer);
							callWaiter(customer.cust, customer.waitingLocation, leastBusyWaiter.waiter, table);
							myWaitingCustomers.remove(0);
							return true;
						}
					}
					}
				}
				}
			}
		}
		return false;
	}
	


	// Actions

	private void GoToHomeBase(MyWaiter w, WaiterHomeBase homeBase)
	{
		w.assignedHomeBase=true;
		w.homeBase=homeBase.location;
		homeBase.isOccupied=true;
		w.waiter.msgSetHomeBase(homeBase.location);	
	}
	
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
			setUnoccupied(w);
			w.waiter.msgYouCanBreak();
			myWaiters.remove(w);
		}
	}
	
	private void setUnoccupied(MyWaiter waiter)
	{
		synchronized(waiterHomeBases)
		{
		for (WaiterHomeBase homeBase: waiterHomeBases)
		{
			if (homeBase.location.equals(waiter.homeBase))
			{
				homeBase.isOccupied=false;
			}
		}
		}
	}
	
	private void WaitInLine(MyCustomer customer, WaitingSpot waitingSpot)
	{
		waitingSpot.isOccupied=true;
		customer.waitingInLine=true;
		customer.waitingLocation=waitingSpot.location;
		customer.cust.msgWaitInLine(customer.waitingLocation);
	}
	
	
	private void callWaiter(Customer cust, Point location, Waiter waiter, Table table)
	{
		print("Host is sending message to the waiter to sit customer");
		cust.setWaiter(waiter);
		Point loc=tableMap.get(table.tableNumber);
		waiter.msgPleaseSeatCustomer(cust, location, table.tableNumber, loc); //grabbing the only waiter
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
		stateChanged();
	}

}

