package restaurant;

import agent.Agent;
import restaurant.HostAgent.Table;
import restaurant.HostAgent.Waiter;
import restaurant.gui.WaiterGui;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Restaurant Host Agent
 */

public class CookAgent extends Agent {
	//WaiterAgent waiter;
	MarketAgent market;
	List<String> menuOptions = new ArrayList<String>();{
	    menuOptions.add("chicken");
	    menuOptions.add("beef");
	    menuOptions.add("lamb");
	}
	private String name; 
	public WaiterGui hostGui = null;
	public class Order
	{
		WaiterAgent waiter;
		String choice;
		int tableNumber;
		state s;
		Order(WaiterAgent waiter, String choice, int tableNumber, state s)
		{
			this.waiter=waiter;
			this.choice=choice;
			this.tableNumber=tableNumber;
			this.s=s;
		}
	}
	public class Food
	{
		String type;
		int cookingTime;
		int amount;
		int lowThreshold;
		int capacity;
		OrderState orderState;
		Food(String type, int cookingTime, int amount, int lowThreshold, int capacity, OrderState orderstate)
		{
			this.type=type;
			this.cookingTime=cookingTime;
			this.amount=amount;
			this.lowThreshold=lowThreshold;
			this.capacity=capacity;
			this.orderState= orderstate;
		}
	}
	public List<Order> orders = new ArrayList<Order>();
	public enum OrderState
	{nothing};
	public enum state
	{pending, cooking, done, finished};
	HashMap<String, Integer> cookingTimes = new HashMap<String, Integer>();
    {
    	int time=2000;
		for (String choice : menuOptions)
		{
			cookingTimes.put(choice, time);
			time+=2000;
		}
    }
    HashMap<String, Food> foods = new HashMap<String, Food>();
    {
    	for (String choice : menuOptions)
		{
			foods.put(choice, new Food(choice, cookingTimes.get(choice), 0, 2, 10, OrderState.nothing));
		}
    }


	
	//Messages
	
	public void msgHereIsOrder(WaiterAgent waiter, String choice, int tableNumber)
	{
		Order order = new Order(waiter, choice, tableNumber, state.pending);
		print("the cook recieves the order " + order.choice + " and puts it on a list of orders");
		orders.add(order);
		stateChanged();
	}
	
	public void msgFufilledPartialOrder(HashMap<String, Integer> groceryList)
	{
		stateChanged();
	}
	
	public void msgFufilledCompleteOrder(HashMap<String, Integer> groceryList)
	{
		print("cook is getting the message that the market fufilled his order.");
		for (Map.Entry<String, Food>  food: foods.entrySet())
		{
			for (Map.Entry<String, Integer> list: groceryList.entrySet())
			{
				if (food.getKey().equals(list.getKey()))
				{
					food.getValue().amount=list.getValue();
				}
			}
		}
		print("cook has completely resupplied his stock of food");
		stateChanged();
	}

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */	
			
	protected boolean pickAndExecuteAnAction() {
		for (Order order : orders) 
		{	
			if (order.s==state.pending)
			{
				TryToCookFood(order);
				return true;
			}
		}
		for (Order order : orders) 
		{
			if (order.s==state.done)
			{
				PlateIt(order);
				return true;
			}
		}
		return false;
	}


	// Actions

	private void TryToCookFood(Order order) //can cook multiple things at a time with no decrease in speed
	{
		Food f = foods.get(order.choice);
		if (f.amount==0)
		{
			print("the cook tells the waiter that they are out of " + order.choice);
			order.waiter.msgOutOfFood(order.choice, order.tableNumber);
			orders.remove(order);
			return;
		}
		f.amount--;
		if (f.amount <= f.lowThreshold)
		{
			print("food is low. the cook is ordering food from the market to restock inventory");
			OrderFoodThatIsLow();
			return;
		}
		//DoCooking(order);
		print("the cook begins cooking the " + order.choice);
		order.s = state.cooking; //put this inside timer class when u implement it
		CookingTimer(order);
		print("the cook is done cooking the " + order.choice);
		order.s = state.done;
	}

	
	private void OrderFoodThatIsLow()
	{
	    HashMap<String, Integer> groceryList = new HashMap<String, Integer>();
		for (Map.Entry<String, Food> entry : foods.entrySet()) 
		{
			int foodCurrentAmount = entry.getValue().amount;
			int foodLowThreshold = entry.getValue().lowThreshold;
		    if (foodCurrentAmount < foodLowThreshold)
		    {
		    	String foodName = entry.getValue().type;
		    	int foodOrderSize = entry.getValue().capacity;
		    	groceryList.put(foodName, foodOrderSize);
		    }
		}
		print("the cook sends a message to the market and sends the grocery list over");
		market.msgOrderRestock(this, groceryList);
	}
	
	
	private void PlateIt(Order order)
	{
		//DoPlating(order);
		print("the cook notifies the waiter that the " + order.choice + " is ready to be served to the customer");
		order.waiter.msgOrderIsReady(order.choice, order.tableNumber);
		orders.remove(order);
	}
	
	
	private void CookingTimer(Order order)
	{
		int time = cookingTimes.get(order.choice);
		try
		{
			Thread.sleep(time);
		}
		catch(Exception e)
		{
			System.out.println("Exception caught");
		}
	}

	//utilities

	public void setGui(WaiterGui gui) {
		hostGui = gui;
	}

	public WaiterGui getGui() {
		return hostGui;
	}
	
	
	public void setMarket(MarketAgent market)
	{
		this.market=market;
	}
	
}

