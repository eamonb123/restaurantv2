package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentState;
import restaurant.HostAgent.Table;
import restaurant.gui.WaiterGui;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */

public class WaiterAgent extends Agent {
	public Collection<Table> tables;
	private CookAgent cook;
	private HostAgent host;
	//private Point location = new Point();
	public List<Customer> myCustomers = new ArrayList<Customer>();
	public enum CustomerState
	{nothing, waiting, seated, readyToOrder, takingOrder, ordered, sendOrderToCook, deliver, delivering, eating, cleaningUp, done};
	public enum WaiterState
	{nothing, atSeat};
	public WaiterState waiterState = WaiterState.nothing;
	public boolean isBusy()
	{
		if (myCustomers.size()!=0)
			return true;
		else 
			return false;
	}
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
		Point location = new Point();
		CustomerState customerState = CustomerState.nothing;
		Customer(CustomerAgent cust, int tableNumber, CustomerState state, Point location) {
			this.cust=cust;
			this.tableNumber=tableNumber;
			this.customerState=state;
			this.location=location;
		}
	}
	private String name;
	private Semaphore atTable = new Semaphore(0);
	public WaiterGui waiterGui = null;

	public WaiterAgent(String name) {
		super();
		this.name = name;
	}
	

	//Messages

	public void msgAtTable() {//from animation
		print("msgAtTable() called");
		//CustomerState.readyToOrder;s
		atTable.release();
//		print("releasing");
		stateChanged();
	}
	
	
	public void msgPleaseSeatCustomer(CustomerAgent cust, int tableNumber, Point loc)
	{
		print("waiter is adding " + cust.name + " to the list of waiting customers");
		myCustomers.add(new Customer(cust, tableNumber, CustomerState.waiting, loc));
		stateChanged();
		//System.out.println("test");
	}

	
	public void msgReadyToOrder(CustomerAgent cust)
	{
		for (Customer c : myCustomers)
		{
			if (c.cust==cust)
			{
				print("change customer " + cust.name + " state to readyToOrder");
				c.customerState=CustomerState.readyToOrder;
			}
		}
		stateChanged();
	}
	
	public void msgHereIsChoice(CustomerAgent cust)
	{
		System.out.println("A:" + cust);
		for (Customer c : myCustomers)
		{
			if (c.cust==cust && c.cust.choice.equals(cust.choice))
			{
				print("the waiter assigns the customer's choice " + c.cust.choice + " to customer " + cust.name);
				c.customerState=CustomerState.ordered;
				c.choice=cust.choice;
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
				c.customerState=CustomerState.deliver;
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
				c.customerState=CustomerState.done;
			}
		}
		stateChanged();
	}


	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	
	protected boolean pickAndExecuteAnAction() {
		for (Customer cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.waiting)
			{
				SeatCustomer(cust);
				return true;
			}
		}
		for (Customer cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.readyToOrder)
			{
				cust.customerState = CustomerState.takingOrder;
				TakeOrder(cust);
				return true;
			}
		}
		for (Customer cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.ordered)
			{
				cust.customerState=CustomerState.sendOrderToCook;
				GiveCook(cust);
				return true;
			}
		}
		for (Customer cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.deliver)
			{
				cust.customerState=CustomerState.delivering;
				Deliver(cust);
				return true;
			}
		}
		for (Customer cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.done)
			{
				cust.customerState=CustomerState.cleaningUp;
				CleanUp(cust);
				return true;
			}
		}
		//waiterGui.goToHome();
		return false;
	}
	


	// Actions

	private void SeatCustomer(Customer c) 
	{
		print("waiter is now currently busy helping customer " + c.cust.name);
		print("waiter is asking customer " + c.cust.name + " to follow him to table " + c.tableNumber);
		//state = WaiterState.busy;
		waiterGui.PickUpCustomer();
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.cust.msgFollowMeToTable(this, menuOptions, c.tableNumber, c.location);
		waiterGui.DoSeatCustomer(c.location);
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.customerState=CustomerState.seated;
	}
	
	
	private void TakeOrder(Customer c)
	{
		print("waiter " + name + " is taking customer " + c.cust.name + " order");
		waiterGui.DoGoToTable(c.tableNumber);
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.cust.msgWhatWouldYouLike();
		//c.state = CustomerState.ordered;
	}
	
	private void GiveCook(Customer c)
	{
		waiterGui.DoGiveCook();
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("the waiter gives customer " + c.cust.name + " order to the cook to prepare");
		cook.msgHereIsOrder(this, c.choice, c.tableNumber);
	
	}
	
	private void Deliver(Customer c)
	{
		waiterGui.PickUpOrder();
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.DoGoToCustomer(c.tableNumber);
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("waiter " + name + " delivers the " + c.choice + " to customer " + c.cust.name);
		c.cust.msgHereIsYourFood(c.choice);
	}
	
	private void CleanUp(Customer c)
	{
		print("the waiter lets the host know that the table which customer " + c.cust.name + " sat at is now empty");
		host.msgTableIsFree(c.tableNumber);
		print("the waiter is now available to help the next customer");
		//state = WaiterState.available;
	}
	



	//utilities

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	public void setCook(CookAgent cook)
	{
		this.cook=cook;
	}
	
	public void setHost(HostAgent host)
	{
		this.host=host;
	}
	
	public HostAgent getHost()
	{
		return host;
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

