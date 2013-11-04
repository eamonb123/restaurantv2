package restaurant;

import restaurant.gui.CustomerGui;
import restaurant.gui.RestaurantGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter;
import agent.Agent;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
 
/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent implements Customer{
	public String name;
	private int hungerLevel = 5;        // determines length of meal
    String choice;
    int money = 20;
    int bill = 0;
    int tableNumber;
	public Semaphore waiting = new Semaphore(0);
	Timer timer = new Timer();
	private Point location = new Point();
	private CustomerGui customerGui;
	public List<String> menuOptions = Collections.synchronizedList(new ArrayList<String>());
	public class Menu {
		String lamb;
		String beef;
		String chicken;
	}
	// agent correspondents
	private Host host;
	private Cashier cashier;
	//private WaiterAgent waiter = new WaiterAgent("bob");
	private Waiter waiter = null;
	
	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingToBeSeated, BeingSeated, Ordered, reOrder, finishing, payCashier, done, MakingPayment};
	AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent // you're doing the event, so you are "state". ex. you got hungry, so you arrive at the restaurant. you walked in the restaurant, so you are waiting to be seated
	{none, gotHungry, followHost, readyToOrder, readyToReorder, noMenuOptions, eating, doneEating, payingBill, leaving};
	AgentEvent event = AgentEvent.none;
	

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public CustomerAgent(String name){
		super();
		this.name = name;
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(Host host) {
		this.host = host;
	}

	public String getCustomerName() {
		return name;
	}
	
	
	
	// Messages

	
	public void gotHungry() {//from animation
		print(name + " is hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	
	public void msgSemaphoreRelease()
	{
//		print("RELEASING customer semaphore");
		waiting.release();
		stateChanged();
	}
	
	
	public void msgWaitInLine(Point location)
	{
		customerGui.xDestination=location.x;
		customerGui.yDestination=location.y;
		stateChanged();
	}
	
	
	public void msgFollowMeToTable(Waiter waiter, List<String> menuOptions, int tableNumber, Point loc)
	{
		print("customer " + name + " has recived the message to sit at table " + tableNumber);
		this.menuOptions=menuOptions;
		this.tableNumber=tableNumber;
		location=loc;
		event = AgentEvent.followHost;
		print("customer " + name + " following host to table " + tableNumber);
		stateChanged();
	}
	
	
	public void msgWhatWouldYouLike(List<String> menu)
	{
		choice = CustomerChoice(menu);
		print("customer " + name + " decides he wants " + choice);
		event = AgentEvent.readyToOrder;
		stateChanged();
	}
	
	
	public void msgReOrder(List<String> menu)
	{
		print("customer is reordering");
		if (menu.isEmpty())
		{
			customerGui.text="";
			event = AgentEvent.noMenuOptions;
		}
		else
		{
			choice = CustomerChoice(menu);
			event = AgentEvent.readyToReorder;
		}
		stateChanged();
	}
	
	
	private String CustomerChoice(List<String> menu)
	{
		Random random = new Random();
		int index = random.nextInt(menu.size());
		return menu.get(index);
	}
	
	public void msgHereIsYourFood(String food)
	{
		if (food==choice)
		{
			event = AgentEvent.eating;
		}
		else 
		{
			print("you got my order wrong!");
		}
		stateChanged();
	}
	
	public void msgHereIsBill(int bill)
	{
		print("customer received the bill");
		this.bill=bill;
		event = AgentEvent.payingBill;
		stateChanged();
	}
	
	public void msgHereIsChange(int change)
	{
		print("customer recieved $" + change + " change");
		money+=change;
		event = AgentEvent.leaving;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (event == AgentEvent.gotHungry && state == AgentState.DoingNothing ){
			state = AgentState.WaitingToBeSeated;
			goToRestaurant();
			return true;
		}
		if (event == AgentEvent.followHost && state == AgentState.WaitingToBeSeated ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (event == AgentEvent.readyToOrder && state == AgentState.BeingSeated ){
			state = AgentState.Ordered;
			OrderFood();
			return true;
		}
		if (state == AgentState.Ordered)
		{
			if (event == AgentEvent.readyToReorder)
			{
				event = AgentEvent.none;
				OrderFood();
				return true;
			}
			if (event == AgentEvent.eating)
			{
				state = AgentState.finishing;
				ConsumeFood();
				return true;
			}
			if (event == AgentEvent.noMenuOptions)
			{
				state = AgentState.done;
				LeaveRestaurant();
				return true;
			}
		}
		if (event == AgentEvent.doneEating && state == AgentState.finishing ){
			state = AgentState.done;
			FinishedEating();
			return true;
		}
		if (event == AgentEvent.payingBill && state == AgentState.done){
			state = AgentState.MakingPayment;
			MakePayment();
			return true;
		}
		if (event == AgentEvent.leaving && state == AgentState.MakingPayment){
			state = AgentState.DoingNothing;
			LeaveRestaurant();
			return true;
		}
		return false;
	}

	// Actions
	
	private void goToRestaurant() {
		print("Customer " + name + " is going to restaurant");
		print("Customer " + name + " is telling the host he is hungry");
		//msgWalkIntoRestaurant();
		host.msgIWantToEat(this);
		try {
			print("acquiring customer semaphore");
			waiting.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private void SitDown() {
		print("customer " + name + " is being seated and going to table " + tableNumber);
		customerGui.DoGoToSeat(location);
		try {
//			print("acquiring");
			waiting.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("the waiter hands " + name + " the menu");
		print(name + " is deciding what to order...");
		decidingOrder();
		print(name + " is ready to order");
		customerGui.text="decided order!";
		waiter.msgReadyToOrder(this);
	}
	
	private void decidingOrder()
	{
		try
		{
			Thread.sleep(3000);
		}
		catch(Exception e)
		{
			System.out.println("Exception caught");
		}
	}
	
	private void OrderFood()
	{
		print("customer " + name + " tells the waiter he wants " + choice);
		customerGui.order = choice;
		customerGui.text=choice + "?";
		waiter.msgHereIsChoice(this);
	}
	
	
	private void ConsumeFood() //currently every food takes same amount of time to eat
	{
		customerGui.text= "eating " + choice + "...";
		print("customer " + name + " eats the food for 5 seconds before being done");
		EatFood();
	}
	

	private void FinishedEating()
	{
		print("customer " + name + " notifies the waiter that he is done eating the " + choice);
		customerGui.text= "finished! check please...";
		waiter.msgDoneEating(this);
	}
	
	private void MakePayment()
	{
		customerGui.text="Going to cashier to pay $" + bill + " for " + choice;
		customerGui.bill=this.bill;
		customerGui.DoGoToCashier();
		try {
//			print("acquiring");
			waiting.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customerGui.text="";
		cashier.msgPayBill(this, money, bill);
	}
	
	private void LeaveRestaurant()
	{	
		print("customer " + name + " is now leaving the restaurant");
		customerGui.DoExitRestaurant();
	}

	private void EatFood() {
		Do("Eating Food");
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}


	// Accessors, etc.

	public boolean DoingNothing()
	{
		if (state == AgentState.DoingNothing)
			return true;
		else 
			return false;
	}
	
	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	
	public void setWaiter(Waiter w){
		waiter = w;
	}
	
	public void setCashier(Cashier c){
		cashier=c;
	}

	@Override
	public String getChoice() {
		return this.choice;
	}
}

