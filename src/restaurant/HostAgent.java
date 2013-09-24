package restaurant;

import agent.Agent;
import restaurant.gui.HostGui;

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
	static int NTABLES=3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<CustomerAgent> waitingCustomers = new ArrayList<CustomerAgent>();
	public Collection<Table> myTables;
	private WaiterAgent waiter;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name; 
	public class Table {
		CustomerAgent cust;
		int tableNumber;
		int xPos;
		boolean isOccupied=false;
		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}
	}
	private Semaphore atTable = new Semaphore(0,true);
	public boolean isServing=false;
	public HostGui hostGui = null;
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
	
	
	// Messages
	
	public void msgIWantToEat(CustomerAgent cust) {
		waitingCustomers.add(cust);
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
	}
	
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	
	protected boolean pickAndExecuteAnAction() {
//		 Think of this next rule as:
//            Does there exist a table and customer,
//            so that table is unoccupied and customer is waiting.
//            If so seat him at the table.
		for (CustomerAgent cust : waitingCustomers)
		{
			for (Table table : myTables)
			{
				if (!table.isOccupied && !waiter.isServing)
				{
					callWaiter(waitingCustomers.get(0), table);
					waitingCustomers.remove(0);
					return true;
				}
			}
		}
		return false;
	}
	
	public int tableNumber()
	{
		int tableNum=0;
		for (Table table : myTables) 
		{
//			if (!table.isOccupied()) 
//			{
//				tableNum=table.tableNumber;
//				break;
//			}
		}
		return tableNum;
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
		table.cust=cust;
		table.isOccupied=true;
		waiter.msgPleaseSeatCustomer(cust, table.tableNumber);
	}
	
	
	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at " + table);
		hostGui.DoBringToTable(customer);

	}

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

}

