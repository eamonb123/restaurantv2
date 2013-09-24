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
	private CookAgent cook;
	private HostAgent host;
	public enum CustomerState
	{nothing, waiting, seated, readyToOrder, askedForOrder, ordered, delivered, eating, done};
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
		Customer(CustomerAgent c, int table, CustomerState s) {
			cust=c;
			tableNumber=table;
			state=s;
		}
	}
	List<Customer> myCustomers = new ArrayList<Customer>();
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
		myCustomers.add(new Customer(cust, tableNumber, CustomerState.waiting));
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
	}
	
	public void msgHereIsChoice(CustomerAgent cust, String choice)
	{
		for (Customer c : myCustomers)
		{
			if (c.cust==cust && c.choice==choice)
			{
				c.state=CustomerState.ordered;
				c.choice=choice;
			}
		}
	}
	
	public void msgOrderIsReady(CustomerAgent cust, String choice, int tableNumber)
	{
		for (Customer c : myCustomers)
		{
			if (c.cust== cust && c.choice==choice && c.tableNumber==tableNumber)
			{
				c.state=CustomerState.delivered;
			}
		}
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
			if (cust.state==CustomerState.delivered)
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

	private void SeatCustomer(Customer c) 
	{
		c.cust.followMe(this, new Menu);
		//DoSeatCustomer(c);
		c.state=CustomerState.seated;
	}
	
	
	private void TakeOrder(Customer c)
	{
		//DoGoToTable(cust.tableNumber);
		c.cust.whatWouldYouLike();
		c.state=CustomerState.askedForOrder;
		String order= customerChoice();
		c.choice=CustomerChoice();
	}

	
	private String CustomerChoice()
	{
		Random random = new Random();
		int index = random.nextInt(menuOptions.size());
		return menuOptions.get(index);
	}
	
	private void GiveCook(Customer c)
	{
		//DoGiveCook(c);
		cook.msgHereIsAnOrder(this, c.choice, c.tableNumber);
		c.state=CustomerState.ordered;
	}
	
	private void Deliver(Customer c)
	{
		//DoGoToCustomer(c);
		c.msgHereIsYourFood(c.choice);
	}
	
	private void CleanUp(Customer c)
	{
		c.state=CustomerState.done;
		host.msgTableIsFree(c.tableNumber);
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

