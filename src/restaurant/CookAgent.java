package restaurant;

import agent.Agent;
import restaurant.HostAgent.Table;
import restaurant.gui.HostGui;

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
	
	public class Order
	{
		WaiterAgent waiter;
		String choice;
		int tableNumber;
		CookState state;
		Order(WaiterAgent waiter, String choice, int tableNumber)
		{
			this.waiter=waiter;
			this.choice=choice;
			this.tableNumber=tableNumber;
		}
	}
	public List<Order> orders = new ArrayList<Order>();
	public enum CookState
	{pending, cooking, done, finished};
	Map<String, Integer> cookingTimes = new HashMap<String, Integer>();

	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private String name; 
	private boolean isServing=false;
	public HostGui hostGui = null;

	public CookAgent(String name) {
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
	
	public void HereIsOrder(WaiterAgent waiter, String choice, int tableNumber)
	{
		orders.add(new Order(waiter, choice, tableNumber));
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

//	CookIt(Order o)
//	{
//		doCooking(o);
//	}
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
	
	private void seatCustomer(CustomerAgent customer, Table table) {
		customer.msgSitAtTable();
		DoSeatCustomer(customer, table);
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

