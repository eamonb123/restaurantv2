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
		int currentAmount;
		int lowThreshold;
		int capacity;
		OrderState orderState;
		Food(String type, int cookingTime, int amount, int lowThreshold, int capacity, OrderState orderstate)
		{
			this.type=type;
			this.cookingTime=cookingTime;
			this.currentAmount=amount;
			this.lowThreshold=lowThreshold;
			this.capacity=capacity;
			this.orderState= orderstate;
		}
	}
	public List<Order> orders = new ArrayList<Order>();
	public enum OrderState
	{nothing};
	public enum state
	{pending, cooking, orderingFood, done};
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
	
	
	public void msgShippingFood(HashMap<String, Integer> incomingOrder)
	{
		print("cook is getting the message that the market fufilled his order.");
		boolean incompleteShipping=false;
		for (Map.Entry<String, Food>  cookFood: foods.entrySet())
		{
			for (Map.Entry<String, Integer> marketFood: incomingOrder.entrySet())
			{
				if (cookFood.getKey().equals(marketFood.getKey()))
				{
					cookFood.getValue().currentAmount+=marketFood.getValue();
					if (cookFood.getValue().currentAmount<cookFood.getValue().capacity)
					{
						incompleteShipping=true;
					}
				}
			}
		}
		if (incompleteShipping)
		{
			print("not all the requested materials were shipped by the market");
			OrderFoodThatIsLow();
		}
		else
		{
			print("cook has completely resupplied his stock of food");
			stateChanged();
		}
		//stateChanged(); //!!KSAJDKSJDH
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
		if (f.currentAmount <= f.lowThreshold)
		{
			print("food is low. the cook is ordering food from the market to restock inventory");
			OrderFoodThatIsLow();
			order.s = state.orderingFood;
			return;
		}
		if (f.currentAmount==0)
		{
			print("the cook tells the waiter that they are out of " + order.choice);
			order.waiter.msgOutOfFood(order.choice, order.tableNumber);
			orders.remove(order);
			return;
		}
		f.currentAmount--;
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
		for (Map.Entry<String, Food> food : foods.entrySet()) 
		{
		    if (food.getValue().currentAmount < food.getValue().capacity)
		    {
		    	groceryList.put(food.getKey(), food.getValue().capacity-food.getValue().currentAmount);
		    }
		}
		System.out.println(groceryList);
		print("the cook sends a message to the market with the grocery list");
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

