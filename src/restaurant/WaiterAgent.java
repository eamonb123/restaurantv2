package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentState;
import restaurant.HostAgent.Table;
import restaurant.gui.WaiterGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */

public class WaiterAgent extends Agent implements Waiter{
	//public Collection<Table> tables;
	private Cashier cashier;
	private Cook cook;
	private Host host;
	public WaiterGui waiterGui = null;
	public List<MyCustomers> myCustomers = new ArrayList<MyCustomers>();
	//List<Customer> myCustomers = Collections.synchronizedList(new ArrayList<Customer>());
	public enum CustomerState
	{nothing, waiting, seated, readyToOrder, takingOrder, ordered, reOrder, reOrdering, doneEating, waitingForReceipt, receivingReceipt, deliveredReceipt, sendOrderToCook, deliver, delivering};
	public enum WaiterState
	{nothing, continueWorking, askForBreak, asking, onBreak, returningToWork, backToWork, breaking, atSeat};
	public WaiterState waiterState = WaiterState.nothing;
	public class MyCustomers
	{
		Customer cust;
		Point customerlocation = new Point();
		int bill;
		int tableNumber;
		String choice;
		List<String> menuOptions = new ArrayList<String>();{
		    menuOptions.add("chicken");
		    menuOptions.add("beef");
		    menuOptions.add("lamb");
		}
		Point destination = new Point();
		CustomerState customerState = CustomerState.nothing;
		MyCustomers(Customer cust, Point customerlocation, int tableNumber, CustomerState state, Point location) {
			this.cust=cust;
			this.customerlocation=customerlocation;
			this.tableNumber=tableNumber;
			this.customerState=state;
			this.destination=location;
		}
	}
	private String name;
	private Semaphore atTable = new Semaphore(0);



	public WaiterAgent(String name) {
		super();
		this.name = name;
	}
	

	//Messages

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		atTable.release();
//		print("releasing");
		stateChanged();
	}
	
	public void msgTryToGoOnBreak()
	{
		print("the waiter wants to go on break");
		waiterState = WaiterState.askForBreak;
		AskToGoOnBreak();
	}
	
	public void msgYouCannotBreak()
	{
		print("the waiter continues to work like normal as his request to break is denied");
		waiterGui.onBreak=false;
		stateChanged();
	}
	
	public void msgYouCanBreak()
	{
		print("the waiter begins his break");
		waiterState = WaiterState.onBreak;
		stateChanged();
	}
	
	public void msgBackToWork()
	{
		print("the waiter gets back to work");
		waiterState = WaiterState.returningToWork;
		stateChanged();
	}
	
	public void msgPleaseSeatCustomer(Customer cust, Point customerlocation, int tableNumber, Point loc)
	{
		System.out.println("YOYOYO" + customerlocation);
		print("waiter is adding customer to the list of waiting customers");
		myCustomers.add(new MyCustomers(cust, customerlocation ,tableNumber, CustomerState.waiting, loc));
		stateChanged();
		//System.out.println("test");
	}

	
	public void msgReadyToOrder(Customer cust)
	{
		for (MyCustomers c : myCustomers)
		{
			if (c.cust==cust)
			{
				print("change customer state to readyToOrder");
				c.customerState=CustomerState.readyToOrder;
			}
		}
		stateChanged();
	}
	
	public void msgHereIsChoice(Customer cust)
	{
		for (MyCustomers c : myCustomers)
		{
			if (c.cust==cust && c.cust.getChoice().equals(cust.getChoice()))
			{
				print("the waiter assigns the customer's choice " + c.cust.getChoice() + " to customer " + cust.getName());
				c.choice=cust.getChoice();
				waiterGui.order=c.choice;
				c.customerState=CustomerState.ordered;
			}
		}
		stateChanged();
	}
	
	public void msgOutOfFood(String choice, int tableNumber)
	{
		for (MyCustomers c : myCustomers)
		{
			if (c.choice.equals(choice) && c.tableNumber==tableNumber)
			{
				print("the waiter receives a message from the cook saying they are out of " + choice);
				c.menuOptions.remove(choice);
				c.customerState=CustomerState.reOrder;
			}
		}
		stateChanged();
	}
	
	public void msgOrderIsReady(String choice, int tableNumber)
	{
		for (MyCustomers c : myCustomers)
		{
			if (c.choice.equals(choice) && c.tableNumber==tableNumber)
			{
				c.customerState=CustomerState.deliver;
			}
		}
		stateChanged();
	}
	
	public void msgDoneEating(Customer cust)
	{
		for (MyCustomers c : myCustomers)
		{
			if (c.cust==cust)
			{
				c.customerState=CustomerState.doneEating;
			}
		}
		stateChanged();
	}
	
	public void msgHereIsReceipt(int bill, int tableNumber)
	{
		for (MyCustomers c : myCustomers)
		{
			if (c.tableNumber==tableNumber)
			{
				c.bill=bill;
				c.customerState=CustomerState.receivingReceipt;
			}
		}
		stateChanged();
	}

	
	

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	
	protected boolean pickAndExecuteAnAction() {
		//synchronized(customers){
		if (waiterState == WaiterState.askForBreak)
		{
			waiterState = WaiterState.asking;
			AskToGoOnBreak();
			return true;
		}
		if (waiterState == WaiterState.onBreak)
		{
			waiterState = WaiterState.breaking;
			GoOnBreak();
			return true;
		}
		if (waiterState == WaiterState.returningToWork)
		{
			waiterState = WaiterState.backToWork;
			ReAddWaiterToHost();
			return true;
		}
		for (MyCustomers cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.waiting)
			{
				SeatCustomer(cust);
				return true;
			}
		}
		//}
		for (MyCustomers cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.readyToOrder)
			{
				cust.customerState = CustomerState.takingOrder;
				TakeOrder(cust);
				return true;
			}
		}
		for (MyCustomers cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.ordered)
			{
				cust.customerState=CustomerState.sendOrderToCook;
				GiveCook(cust);
				return true;
			}
		}
		for (MyCustomers cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.reOrder)
			{
				cust.customerState=CustomerState.reOrdering;
				reOrder(cust);
				return true;
			}
		}
		for (MyCustomers cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.deliver)
			{
				cust.customerState=CustomerState.delivering;
				Deliver(cust);
				return true;
			}
		}
		for (MyCustomers cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.doneEating)
			{
				cust.customerState=CustomerState.waitingForReceipt;
				GrabReceiptFromCashier(cust);
				return true;
			}
		}
		for (MyCustomers cust : myCustomers) 
		{
			if (cust.customerState==CustomerState.receivingReceipt)
			{
				cust.customerState=CustomerState.deliveredReceipt;
				DeliverReceiptAndCleanUp(cust);
				return true;
			}
		}
		return false;
	}
	


	// Actions

	private void AskToGoOnBreak()
	{
		print("the waiter asks the host if he can go on break");
		host.msgCanIGoOnBreak(this);
	}
	
	private void GoOnBreak()
	{
		print("waiter is going on break until told otherwise...");
		waiterGui.stayAtBreak=true;
		waiterGui.DoGoToBreakSpot();
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("the waiter has reached the break location");
	}
	
	private void ReAddWaiterToHost()
	{
		print("the waiter is being readded to the host list of waiters");
		waiterGui.stayAtBreak=false;
		waiterGui.goToHome();
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		host.setWaiter(this);
	}

	private void SeatCustomer(MyCustomers c) 
	{
		print("waiter is now currently busy helping customer " + c.cust.getName());
		print("waiter is asking customer " + c.cust.getName() + " to follow him to table " + c.tableNumber);
		//state = WaiterState.busy;
		waiterGui.PickUpCustomer(c.customerlocation.x-20, c.customerlocation.y-20);
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		host.msgPickedUpCustomer(c.customerlocation);
		System.out.println("CUSTOMER LOCATION" + c.customerlocation);
		c.cust.msgFollowMeToTable(this, c.menuOptions, c.tableNumber, c.destination);
		waiterGui.DoSeatCustomer(c.destination);
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.customerState=CustomerState.seated;
	}
	
	
	private void TakeOrder(MyCustomers c)
	{
		print("waiter " + name + " is taking customer " + c.cust.getName() + " order");
		waiterGui.DoGoToTable(c.tableNumber);
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		c.cust.msgWhatWouldYouLike(c.menuOptions);
		//c.state = CustomerState.ordered;
	}
	
	private void reOrder(MyCustomers c)
	{
		print("the waiter is now approaching the customer asking him to reorder");
		waiterGui.text="currently out of " + waiterGui.order;
		waiterGui.WalkingToReorderingCustomer(c.tableNumber, c.choice);
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (c.menuOptions.isEmpty())
		{
			waiterGui.text="";
		}
		c.cust.msgReOrder(c.menuOptions);
	}
	
	private void GiveCook(MyCustomers c)
	{
		waiterGui.DoGiveCook();
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("the waiter gives customer " + c.cust.getName() + " order to the cook to prepare");
		cook.msgHereIsOrder(this, c.choice, c.tableNumber);
	
	}
	
	private void Deliver(MyCustomers c)
	{
		waiterGui.DoPickUpOrder();
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.text=waiterGui.order;
		waiterGui.DoGoToCustomer(c.tableNumber, c.choice);
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.text="";
		print("waiter " + name + " delivers the " + c.choice + " to customer " + c.cust.getName());
		c.cust.msgHereIsYourFood(c.choice);
	}
	
	private void GrabReceiptFromCashier(MyCustomers customer)
	{
		waiterGui.DoGoToCashier();
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cashier.msgComputeCheck(this, customer.choice, customer.tableNumber);
	}
	
	private void DeliverReceiptAndCleanUp(MyCustomers customer)
	{
		waiterGui.check=customer.bill;
		waiterGui.text= "delivering check of $" + waiterGui.check;
		waiterGui.DoDeliverReceipt(customer.tableNumber);
		try {
//			print("acquiring");
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.text="";
		customer.cust.msgHereIsBill(customer.bill);
		print("the waiter lets the host know that the table which customer " + customer.cust.getName() + " sat at is now empty");
		host.msgTableIsFree(this, customer.cust, customer.tableNumber);
		print("the waiter is now available to help the next customer");
	}
	
	



	//utilities

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}
	
	public String getName()
	{
		return this.name;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	public void setCashier (CashierAgent cashier)
	{
		this.cashier=cashier;
	}
	
	public void setCook(CookAgent cook)
	{
		this.cook=cook;
	}
	
	public void setHost(HostAgent host)
	{
		this.host=host;
	}
	
	public Host getHost()
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
