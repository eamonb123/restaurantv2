package restaurant;

import agent.Agent;
import restaurant.HostAgent.Table;
import restaurant.gui.WaiterGui;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Restaurant Host Agent
 */

public class CookAgent extends Agent implements Cook{
	public List<Market> markets = new ArrayList<Market>();
	List<String> menuOptions = new ArrayList<String>();{
	    menuOptions.add("chicken");
	    menuOptions.add("beef");
	    menuOptions.add("lamb");
	}
	private String name; 
	private int marketIndex=0;
	public WaiterGui waiterGui = null;
	public class Order
	{
		Waiter waiter;
		String choice;
		int tableNumber;
		state s;
		Order(Waiter waiter, String choice, int tableNumber, state s)
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
	boolean incomplete=false;
	boolean ordering=false;
	boolean opening=false;
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
			foods.put(choice, new Food(choice, cookingTimes.get(choice), 1, 3, 10, OrderState.nothing));
		}
    }


	
	//Messages
	
	public void msgHereIsOrder(Waiter waiter, String choice, int tableNumber)
	{
		Order order = new Order(waiter, choice, tableNumber, state.pending);
		print("the cook recieves the order " + order.choice + " and puts it on a list of orders");
		orders.add(order);
		stateChanged();
	}
	
	
	public void msgFufilledCompleteOrder(HashMap<String, Integer> incomingOrder)
	{
		print("cook is getting the message that the market fufilled the order.");
		for (Map.Entry<String, Food>  currentFood: foods.entrySet())
		{
			for (Map.Entry<String, Integer> incomingFood: incomingOrder.entrySet())
			{
				if (currentFood.getKey().equals(incomingFood.getKey()))
				{
					currentFood.getValue().currentAmount+=incomingFood.getValue();
				}
			}
		}
		print("cook has completely resupplied his stock of food");
		ordering=false;
		marketIndex=0;
		stateChanged();
		//stateChanged(); //!!KSAJDKSJDH
	}
	
	public void msgFufilledPartialOrder(HashMap<String, Integer> incomingOrder)
	{
		print("cook is getting the message that the market could NOT fully fufill the order.");
	    HashMap<String, Integer> newOutgoingList = new HashMap<String, Integer>();
		for (Map.Entry<String, Food>  currentFood: foods.entrySet())
		{
			for (Map.Entry<String, Integer> incomingFood: incomingOrder.entrySet())
			{
				if (currentFood.getKey().equals(incomingFood.getKey()))
				{
					currentFood.getValue().currentAmount+=incomingFood.getValue();
					if (currentFood.getValue().currentAmount<currentFood.getValue().capacity)
					{
						newOutgoingList.put(currentFood.getKey(), currentFood.getValue().capacity-currentFood.getValue().currentAmount);
					}
				}
			}
		}
		print("new outgoing list: ");
		System.out.println(newOutgoingList);
		if (marketIndex==markets.size()-1)
		{
			print("no more markets left. The cook grabbed all the items he could but still has some he needs");
			ordering=false;
		}
		else
		{
			marketIndex++;
			SendOrder(newOutgoingList);
		}
		stateChanged();
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
		if (f.currentAmount==0)
		{
			print("the cook tells the waiter that they are out of " + order.choice);
			order.waiter.msgOutOfFood(order.choice, order.tableNumber);
			orders.remove(order);
			return;
		}
		f.currentAmount--;
		if (f.currentAmount <= f.lowThreshold)
		{
			print("food is low. the cook is ordering food from the market to restock inventory");
			OrderFoodThatIsLow();
			order.s = state.orderingFood;
		}
		//DoCooking(order);
		print("the cook begins cooking the " + order.choice);
		order.s = state.cooking; //put this inside timer class when u implement it
		CookingTimer(order);
		print("the cook is done cooking the " + order.choice);
		
	}

	private void OrderFoodThatIsLow()
	{
		ordering=true;
		HashMap<String, Integer> groceryList = new HashMap<String, Integer>();
		if (markets.isEmpty())
		{
			print("no markets to order from to begin with. stop the cook");
			return;
		}
		for (Map.Entry<String, Food> food : foods.entrySet()) 
		{
		    if (food.getValue().currentAmount < food.getValue().lowThreshold)
		    {
		    	groceryList.put(food.getKey(), food.getValue().capacity-food.getValue().currentAmount);
		    }
		}
		print("the cook needs this many items from the market: " + groceryList);
		SendOrder(groceryList);
	}
	
	private void SendOrder(HashMap<String, Integer> groceryList)
	{
		if (groceryList.isEmpty())
		{
			print("GROCERY LIST IS EMPTY. something went wrong");
			return;
		}
		print("the cook sends a message to the market with the grocery list");
		markets.get(marketIndex).msgOrderRestock(this, groceryList);
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
		order.s = state.done;
	}

	//utilities

	public void CheckInitialFood()
	{
		OrderFoodThatIsLow();
	}
	
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}
	
	
	public void setMarket(MarketAgent market)
	{
		markets.add(market);
	}
	
}

