package restaurant;
//hey
import restaurant.gui.CustomerGui;
import restaurant.gui.RestaurantGui;
import agent.Agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
 
/**
 * Restaurant customer agent.
 */
public class CustomerAgent extends Agent {
	public String name;
	private int hungerLevel = 5;        // determines length of meal
    String choice;
    int tableNumber;
	Timer timer = new Timer();
	private CustomerGui customerGui;
	public List<String> menuOptions = new ArrayList<String>();
	public class Menu {
		String lamb;
		String beef;
		String chicken;
	}
	// agent correspondents
	private HostAgent host;
	private WaiterAgent waiter = new WaiterAgent("bob");

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Ordered, finishing, leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followHost, readyToGiveOrder, eating, doneEating};
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
	public void setHost(HostAgent host) {
		this.host = host;
	}

	public String getCustomerName() {
		return name;
	}
	
	
	
	// Messages

	public void gotHungry() {//from animation
		print("Customer is hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMeToTable(WaiterAgent waiter, List<String> menuOptions, int tableNumber)
	{
		print("customer " + name + " has recived the message to sit at table " + tableNumber);
		this.menuOptions=menuOptions;
		this.tableNumber=tableNumber;
		event = AgentEvent.followHost;
		print("following host to table");
		stateChanged();
	}
	
	
	public void msgWhatWouldYouLike()
	{
		choice = CustomerChoice();
		print("customer " + name + " decides he wants " + choice);
		event = AgentEvent.readyToGiveOrder;
		stateChanged();
	}
	
	private String CustomerChoice()
	{
		Random random = new Random();
		int index = random.nextInt(menuOptions.size());
		return menuOptions.get(index);
	}
	
	public void msgHereIsYourFood(String food)
	{
		if (food==choice)
		{
			event = AgentEvent.eating;
		}
		stateChanged();
	}
	

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		//event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		//event = AgentEvent.doneLeaving;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (event == AgentEvent.gotHungry && state == AgentState.DoingNothing ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (event == AgentEvent.followHost && state == AgentState.WaitingInRestaurant ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (event == AgentEvent.readyToGiveOrder && state == AgentState.BeingSeated ){
			state = AgentState.Ordered;
			OrderFood();
			return true;
		}

		if (event == AgentEvent.eating && state == AgentState.Ordered ){
			state = AgentState.finishing;
			ConsumeFood();
			return true;
		}
		if (event == AgentEvent.doneEating && state == AgentState.finishing ){
			state = AgentState.leaving;
			LeaveTable();
			return true;
		}
		return false;
	}

	// Actions
	
	private void goToRestaurant() {
		print("Customer " + name + " is going to restaurant");
		print("Customer " + name + " is telling the host he is hungry");
		host.msgIWantToEat(this);
	}
	
	private void SitDown() {
		print("customer " + name + " is being seated and going to table " + tableNumber);
		//int number= host.tableNumber();
		customerGui.DoGoToSeat(tableNumber);
		print(" customer " + name + " is now ready to order");
		waiter.msgReadyToOrder(this);
	}
	
	private void OrderFood()
	{
		print("customer " + name + " tells the waiter he wants " + choice);
		waiter.msgHereIsChoice(this, choice);
	}
	
	
	private void ConsumeFood() //currently every food takes same amount of time to eat
	{
		print("customer " + name + " eats the food for 5 seconds before being done");
		EatFood();
	}
	
	private void LeaveTable()
	{
		print("customer " + name + "notifies the waiter that he is done eating the " + choice);
		waiter.msgDoneEating(this);
		print("leaving the restaurant");
		print("customer " + name + " is now leaving the restaurant");
		customerGui.DoExitRestaurant();
	}
	

	private void EatFood() {
		Do("Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
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
}

