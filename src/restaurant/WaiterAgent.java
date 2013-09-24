package restaurant;

import agent.Agent;
import restaurant.HostAgent.Table;
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
public class WaiterAgent extends Agent {
	//	public List<CustomerAgent> waitingCustomers = new ArrayList<CustomerAgent>();
	static int NTABLES=3;//a global for the number of tables.
	public Collection<Table> tables;
	public enum CustomerState
	{nothing, waiting, seated, askedForOrder, ordered, delivered, eating, done};
	public class myCustomer
	{
		CustomerAgent cust;
		int tableNumber;
		String choice;
		CustomerState state = CustomerState.nothing;
		myCustomer(CustomerAgent c, int table, CustomerState s) {
			cust=c;
			tableNumber=table;
			state=s;
		}
	}
	List<myCustomer> myCustomers = new ArrayList<myCustomer>();
	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	public boolean isServing=false;
	public HostGui hostGui = null;

	public WaiterAgent(String name) {
		super();
		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		int xPos = 200;
		for (int ix = 1; ix <= NTABLES; ix++) {
			Table newTable = new Table(ix);
			newTable.xPos = xPos;
			tables.add(newTable);//how you add to a collections
			xPos+=150;
		}
	}
	

	//Messages

	public void msgPleaseSeatCustomer(CustomerAgent cust, int tableNumber)
	{
		myCustomers.add(new myCustomer(cust, tableNumber, CustomerState.nothing));
	}

	public void msgLeavingTable(CustomerAgent cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		atTable.release();// = true;
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	
//	for (myCustomer cust : customers) 
//	{
//		if(cust.s==waiting)
//			seatCustomer(cust);
//	}
//	
//	for (myCustomer cust : customers) 
//	{
//		if(cust.s==readyToOrder)
//			takeOrder(cust);
//	}
//	
//	for (myCustomer cust : customers) 
//	{
//		if(cust.s==askedForOrder)
//			giveCook(cust);
//	}
//	
//	for (myCustomer cust : customers) 
//	{
//		if(cust.s==deliver)
//			deliver(cust);
//	}
//	
//	for (myCustomer cust : customers) 
//	{
//		if(cust.s==done)
//			cleanUp(cust);
//	}
//	
	
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		if (isServing==false)
		{
			//isServing=true;
			for (Table table : tables) {
				if (!table.isOccupied()) {
					if (!waitingCustomers.isEmpty()) {
						seatCustomer(waitingCustomers.get(0), table);//the action
						return true;//return true to the abstract agent to reinvoke the scheduler.
					}
				}
			}
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}
	
	public int tableNumber()
	{
		int tableNum=0;
		for (Table table : tables) 
		{
			if (!table.isOccupied()) 
			{
				tableNum=table.tableNumber;
				break;
			}
		}
		return tableNum;
	}

	// Actions

	private void seatCustomer(CustomerAgent customer, Table table) {
		customer.msgSitAtTable();
//		customer.c.followme(this, new menu);
		DoSeatCustomer(customer, table);
//		customer.s=seated;
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
		hostGui.DoLeaveCustomer();
	}
	
//	private void takeOrder(CustomerAgent customer)
//	{
//		DoGoToTable(customer.tableNumber);
//		customer.c.whatwouldyoulike();
//		customer.s=askedToOrder;
//		customer.choice=customerchoice;
//	}
//	
//	giveCook(CustomerAgent customer)
//	{
//		doGiveToCook(customer);
//		hereIsAnOrder(this, c.choice, c.table);
//		customer.s=ordered;
//	}
//	
//	Deliver(CustomerAgent customer)
//	{
//		DoGoToCustomer(customer);
//	}
//	
//	CleanUp(CustomerAgent customer)
//	{
//		customer.s=done;
//		tableIsFree(c.table);
//	}
	

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

//	private class myCustomer
//	{
//		Customer c;
//		int tableNumber;
//		string choice;
//		customerstate s;
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

