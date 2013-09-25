package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentState;
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
	private CookAgent cook;
	private HostAgent host;
	public enum CustomerState
	{nothing, waiting, seated, readyToOrder, askedForOrder, orderComplete, deliver, eating, done};
	public enum WaiterState
	{available, busy};
	public WaiterState state = WaiterState.available;
	List<String> menuOptions = new ArrayList<String>();{
	    menuOptions.add("chicken");
	    menuOptions.add("beef");
	    menuOptions.add("lamb");
	}
	public class Customer
	{
		CustomerAgent cust;
		int tableNumber;
		String choice;
		CustomerState state = CustomerState.nothing;
		Customer(CustomerAgent cust, int tableNumber, CustomerState state) {
			this.cust=cust;
			this.tableNumber=tableNumber;
			this.state=state;
		}
	}
	List<Customer> myCustomers = new ArrayList<Customer>();
	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	//public boolean isServing=false;
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
		myCustomers.add(new Customer(cust, tableNumber, CustomerState.waiting));
		print("whats up");
		stateChanged();
	}
	
	public void msgReadyToOrder(CustomerAgent cust)
	{
		for (Customer c : myCustomers)
		{
			if (c.cust==cust)
			{
				c.state=CustomerState.readyToOrder;
			}
		}
		stateChanged();
	}
	
	public void msgHereIsChoice(CustomerAgent cust, String choice)
	{
		for (Customer c : myCustomers)
		{
			if (c.cust==cust && c.choice==choice)
			{
				c.state=CustomerState.orderComplete;
				c.choice=choice;
			}
		}
		stateChanged();
	}
	
	public void msgOrderIsReady(String choice, int tableNumber)
	{
		for (Customer c : myCustomers)
		{
			if (c.choice==choice && c.tableNumber==tableNumber)
			{
				c.state=CustomerState.deliver;
			}
		}
		stateChanged();
	}
	
	public void msgDoneEating(CustomerAgent cust)
	{
		for (Customer c : myCustomers)
		{
			if (c.cust==cust)
			{
				c.state=CustomerState.done;
			}
		}
		stateChanged();
	}

//	public void msgLeavingTable(CustomerAgent cust) {
//		for (Table table : tables) {
//			if (table.getOccupant() == cust) {
//				print(cust + " leaving " + table);
//				table.setUnoccupied();
//				stateChanged();
//			}
//		}
//	}

//	public void msgAtTable() {//from animation
//		//print("msgAtTable() called");
//		atTable.release();// = true;
//		stateChanged();
//	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	
	protected boolean pickAndExecuteAnAction() {
		for (Customer cust : myCustomers) 
		{
			if (cust.state==CustomerState.waiting)
			{
				SeatCustomer(cust);
				return true;
			}
		}
		for (Customer cust : myCustomers) 
		{
			if (cust.state==CustomerState.readyToOrder)
			{
				TakeOrder(cust);
				return true;
			}
		}
		for (Customer cust : myCustomers) 
		{
			if (cust.state==CustomerState.askedForOrder)
			{
				GiveCook(cust);
				return true;
			}
		}
		for (Customer cust : myCustomers) 
		{
			if (cust.state==CustomerState.deliver)
			{
				Deliver(cust);
				return true;
			}
		}
		for (Customer cust : myCustomers) 
		{
			if (cust.state==CustomerState.done)
			{
				CleanUp(cust);
				return true;
			}
		}
		return false;
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

	private void SeatCustomer(Customer c) 
	{
		state = WaiterState.busy;
		c.cust.msgFollowMeToTable(this, menuOptions, c.tableNumber);
		//DoSeatCustomer(c);
		c.state=CustomerState.seated;
	}
	
	
	private void TakeOrder(Customer c)
	{
		//DoGoToTable(cust.tableNumber);
		c.state=CustomerState.askedForOrder;
		c.cust.msgWhatWouldYouLike();
	}
	
	private void GiveCook(Customer c)
	{
		//DoGiveCook(c);
		cook.HereIsOrder(this, c.choice, c.tableNumber);
		c.state=CustomerState.orderComplete;
	}
	
	private void Deliver(Customer c)
	{
		//DoGoToCustomer(c);
		c.cust.msgHereIsYourFood(c.choice);
	}
	
	private void CleanUp(Customer c)
	{
		host.msgTableIsFree(c.tableNumber);
		state = WaiterState.available;
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

