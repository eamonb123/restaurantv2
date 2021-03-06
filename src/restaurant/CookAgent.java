package restaurant;

import agent.Agent;
import restaurant.CustomerAgent.AgentEvent;
import restaurant.HostAgent.Table;
import restaurant.HostAgent.WaitingSpot;
import restaurant.gui.CookGui;
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
	private CookGui cookGui=null;
	Timer timer = new Timer();
	public List<Market> markets = Collections.synchronizedList(new ArrayList<Market>());
	List<String> menuOptions = Collections.synchronizedList(new ArrayList<String>());{
	    menuOptions.add("chicken");
	    menuOptions.add("beef");
	    menuOptions.add("lamb");
	}
	private String name; 
	private int marketIndex=0;
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
    Map<String, Integer> outGoingList = Collections.synchronizedMap(new HashMap<String, Integer>());
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	boolean incomplete=false;
	boolean ordering=false;
	boolean opening=false;
	public enum OrderState
	{nothing};
	public enum state
	{pending, cooking, orderingFood, done};
	Map<String, Integer> cookingTimes = Collections.synchronizedMap(new HashMap<String, Integer>());
    {
    	int time=2000;
		for (String choice : menuOptions)
		{
			cookingTimes.put(choice, time);
			time+=2000;
		}
    }
    Map<String, Food> foods = Collections.synchronizedMap(new HashMap<String, Food>());
    {
    	for (String choice : menuOptions)
		{
			foods.put(choice, new Food(choice, cookingTimes.get(choice), 5, 3, 10, OrderState.nothing));
		}
    }
    public List<CookingArea> cookingAreas = Collections.synchronizedList(new ArrayList<CookingArea>());
    public class CookingArea 
    {
    	int panNumber;
		boolean isOccupied=false;
		Point location = new Point();
		CookingArea(int i)
		{
			this.isOccupied=false;
			this.location=cookingArea.get(i);
			this.panNumber=i;
		}
	}
    Map<Integer, Point> cookingArea = Collections.synchronizedMap(new HashMap<Integer, Point>());
    {
    	int yPosition=105;
    	for (int i=0; i<3; i++)
    	{
    		Point location = new Point(0, yPosition);
    		cookingArea.put(i,location);
    		yPosition+=30;
    	}
    }
    public List<PlatingArea> platingAreas = Collections.synchronizedList(new ArrayList<PlatingArea>());
    public class PlatingArea 
    {
    	int plateNumber;
    	String food="";
		boolean isOccupied=false;
		Point location = new Point();
		PlatingArea(int i)
		{
			this.isOccupied=false;
			this.location=platingArea.get(i);
			this.plateNumber=i;
		}
	}
    Map<Integer, Point> platingArea = Collections.synchronizedMap(new HashMap<Integer, Point>());
    {
    	int yPosition=155;
    	for (int i=0; i<3; i++)
    	{
    		Point location = new Point(0, yPosition);
    		platingArea.put(i,location);
    		yPosition+=30;
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
	
	public void msgPickedUpOrder(String order)
	{
		synchronized(platingAreas)
		{
		for (PlatingArea platingArea: platingAreas)
		{
			if (platingArea.food==order && platingArea.isOccupied)
			{
				ClearPlates(platingArea);
				platingArea.isOccupied=false;
			}
		}
		}
		stateChanged();
	}
	
	public void msgFufilledCompleteOrder(String name, Map<String, Integer> incomingOrder)
	{
		print("cook is getting the message that " + name + " fufilled the order.");
		synchronized(foods)
		{
		for (Map.Entry<String, Food>  currentFood: foods.entrySet())
		{
			synchronized(incomingOrder)
			{
			for (Map.Entry<String, Integer> incomingFood: incomingOrder.entrySet())
			{
				if (currentFood.getKey().equals(incomingFood.getKey()))
				{
					currentFood.getValue().currentAmount+=incomingFood.getValue();
				}
			}
			}
		}
		}
		print("cook has completely resupplied his stock of food");
		ordering=false;
		marketIndex=0;
		stateChanged();
	}
	
	public void msgFufilledPartialOrder(String name, Map<String, Integer> incomingOrder)
	{
		print("cook is getting the message that " + name + " could NOT fully fufill the order.");
	    Map<String, Integer> newOutgoingList = new HashMap<String, Integer>();
		synchronized(foods)
		{
		for (Map.Entry<String, Food>  currentFood: foods.entrySet())
		{
			synchronized(incomingOrder)
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
			outGoingList=newOutgoingList;
		}
		stateChanged();
	}
	

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */	
			
	protected boolean pickAndExecuteAnAction() {
		if (ordering)
		{
			SendOrder(outGoingList);
		}
		synchronized(orders)
		{
		for (Order order : orders) 
		{	
			synchronized(cookingAreas)
			{
			for(CookingArea cookingArea: cookingAreas)
			{
				synchronized(platingAreas)
				{
				for(PlatingArea platingArea: platingAreas)	
				{
					if (order.s==state.pending && !cookingArea.isOccupied)
					{
						TryToCookFood(order, cookingArea);
						return true;
					}
					if (order.s==state.done && !platingArea.isOccupied)
					{
						PlateIt(order, platingArea);
						return true;
					}
				}
				}
			}
			}
		}
		}
		return false;
	}


	// Actions

	private void TryToCookFood(Order order, CookingArea cookingArea) //can cook multiple things at a time with no decrease in speed
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
		cookingArea.isOccupied=true;
		DecidePan(cookingArea.location, order.choice);
		CookingTimer(order, cookingArea);
	}

	
	
	private void OrderFoodThatIsLow()
	{
		ordering=true;
		Map<String, Integer> groceryList = new HashMap<String, Integer>();
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
	
	private void SendOrder(Map<String, Integer> groceryList)
	{
		if (groceryList.isEmpty())
		{
			return;
		}
		print("the cook sends a message to the market with the grocery list");
		markets.get(marketIndex).msgOrderRestock(this, groceryList);
	}
	
	
	private void PlateIt(Order order, PlatingArea platingArea)
	{
		//DoPlating(order);
		print("the cook notifies the waiter that the " + order.choice + " is ready to be served to the customer");
		platingArea.isOccupied=true;
		platingArea.food=order.choice;
		DecidePlate(platingArea.location, order.choice);
		order.waiter.msgOrderIsReady(order.choice, order.tableNumber, platingArea.location);
		orders.remove(order);
	}
	
	private void DecidePan(Point cookingArea, String choice)
	{
		synchronized(cookingAreas)
		{
		for (CookingArea area: cookingAreas)
		{
			if (area.location.equals(cookingArea))
			{
				if (area.panNumber==0)
				{
					cookGui.firstPan="cooking " + choice + "...";
				}
				if (area.panNumber==1)
				{
					cookGui.secondPan="cooking " + choice + "...";
				}
				if (area.panNumber==2)
				{
					cookGui.thirdPan="cooking " + choice + "...";
				}
			}
		}
		}
	}
	
	private void DecidePlate(Point platingArea, String choice)
	{
		synchronized(platingAreas)
		{
		for (PlatingArea area: platingAreas)
		{
			if (area.location.equals(platingArea))
			{
				if (area.plateNumber==0)
				{
					cookGui.firstPlate="cooked " + choice;
				}
				if (area.plateNumber==1)
				{
					cookGui.secondPlate="cooked " + choice;
				}
				if (area.plateNumber==2)
				{
					cookGui.thirdPlate="cooked " + choice;
				}
			}
		}
		}
	}
	
	
	private void ClearCookingPan(CookingArea cookingArea)
	{
		if (cookingArea.panNumber==0)
		{
			cookGui.firstPan="";
		}
		else if (cookingArea.panNumber==1)
		{
			cookGui.secondPan="";
		}
		else if (cookingArea.panNumber==2)
		{
			cookGui.thirdPan="";
		}
	}
	
	private void ClearPlates(PlatingArea platingArea)
	{
		if (platingArea.plateNumber==0)
		{
			cookGui.firstPlate="";
		}
		else if (platingArea.plateNumber==1)
		{
			cookGui.secondPlate="";
		}
		else if (platingArea.plateNumber==2)
		{
			cookGui.thirdPlate="";
		}
	}
	
	
	private void CookingTimer(final Order order, final CookingArea cookingArea)
	{
		int time = cookingTimes.get(order.choice);
		timer.schedule(new TimerTask() {
			public void run() {
				cookingArea.isOccupied=false;
				ClearCookingPan(cookingArea);
				order.s = state.done;
				print("the cook is done cooking the " + order.choice);
				stateChanged();
			}
		},
		time);
	}

	//utilities

	public void CheckInitialFood()
	{
		OrderFoodThatIsLow();
	}
	
	public void InitializeAreas()
	{
		for (int i=0; i<3; i++)
		{
			cookingAreas.add(new CookingArea(i));
			platingAreas.add(new PlatingArea(i));
		}
	}
	
	public void setGui(CookGui cookGui)
	{
		this.cookGui=cookGui;
	}
	
	
	public void setMarket(MarketAgent market)
	{
		markets.add(market);
	}

	
}

