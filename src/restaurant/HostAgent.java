package restaurant;

import agent.Agent;
import restaurant.WaiterAgent.WaiterState;
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
	static int NTABLES=4;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<CustomerAgent> waitingCustomers = new ArrayList<CustomerAgent>();
	public Collection<Table> myTables;
	public List<WaiterAgent> waiterList = new ArrayList<WaiterAgent>();
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
    private int xPosition=100;
    private int yPosition=250;
	private String name; 
	public class Table {
		int tableNumber;
		int xPos;
		boolean isOccupied=false;
		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}
	}
	private Semaphore atTable = new Semaphore(0,true);
	public WaiterGui hostGui = null;
	public HostAgent(String name) {
		super();
		this.name = name;
		// make some tables
		myTables = new ArrayList<Table>(NTABLES);
		int xPos = 200;
		for (int ix = 1; ix <= NTABLES; ix++) {
			Table newTable = new Table(ix);
			newTable.xPos = xPos;
			myTables.add(newTable);//how you add to a collections
			xPos+=150;
		}
	}
	
	
	//MAPPING TABLE NUMBERS TO COORDINATES
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
		waitingCustomers.add(cust);
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
		if (!waitingCustomers.isEmpty())
		{
			for (Table table : myTables)
			{
				if (!table.isOccupied && waiterList.get(0).state==WaiterState.available)
				{
					callWaiter(waitingCustomers.get(0), table);
					waitingCustomers.remove(0);
					return true;
				}
			}
		}
		return false;
	}
	

//	for (Customer cust : waitingCustomers) 
//	{
//		for (Table t : myTable) 
//		{
//			if (cust.s=waiting && t==NULL && !w.isBusy())
//				CallWaiter(cust, t.tableNumber);
//		}
//	}

	// Actions


	private void callWaiter(CustomerAgent cust, Table table)
	{
		print("Host is sending message to the waiter to sit customer " + cust.name);
		cust.setWaiter(waiterList.get(0));
		Point location=tableMap.get(table.tableNumber);
		waiterList.get(0).msgPleaseSeatCustomer(cust, table.tableNumber, location); //grabbing the only waiter
		table.isOccupied=true;
	}
	
	
	// The animation DoXYZ() routines
//	private void DoSeatCustomer(CustomerAgent customer, Table table) {
//		//Notice how we print "customer" directly. It's toString method will do it.
//		//Same with "table"
//		print("Seating " + customer + " at " + table);
//		hostGui.DoBringToTable(customer);
//
//	}

	//utilities

	public int tableNumber()
	{
		int tableNum=0;
		for (Table table : myTables) 
		{
			if (!table.isOccupied) 
			{
				tableNum=table.tableNumber;
				break;
			}
		}
		return tableNum;
	}
	
	public void setGui(WaiterGui gui) {
		hostGui = gui;
	}

	public WaiterGui getGui() {
		return hostGui;
	}

	public void setWaiter(WaiterAgent waiter)
	{
		waiterList.add(waiter);
	}
}

