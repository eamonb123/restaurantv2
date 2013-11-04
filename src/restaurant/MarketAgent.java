package restaurant;

import agent.Agent;
import restaurant.CookAgent.Food;
import restaurant.CookAgent.state;
import restaurant.CustomerAgent.AgentEvent;
import restaurant.CustomerAgent.AgentState;
import restaurant.HostAgent.Table;
import restaurant.gui.WaiterGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Market;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Restaurant Host Agent
 */
public class MarketAgent extends Agent implements Market{
	private Cashier cashier;
	private String name; 
	private int money=0;
	public MarketAgent(String name)
	{
		this.name=name;
	}
	Map<String, Integer> prices = Collections.synchronizedMap(new HashMap<String, Integer>());
	{
	    prices.put("beef", 7);
    	prices.put("chicken", 5);
    	prices.put("lamb", 2);
	}
	public class IncomingOrder
	{
		Cook cook;
		Map<String, Integer> incomingList = Collections.synchronizedMap(new HashMap<String, Integer>());
	    reStockingState state;
		IncomingOrder(Cook cook,  Map<String, Integer> incomingList, reStockingState state)
		{
			this.cook=cook;
			this.incomingList=incomingList;
			this.state=state;	
		}
	}
	public enum reStockingState
	{none, restocking, fufillingOrder, failedToFufillOrder};
	public List<IncomingOrder> orders = Collections.synchronizedList(new ArrayList<IncomingOrder>());
	List<String> menuOptions = Collections.synchronizedList(new ArrayList<String>());{
	    menuOptions.add("chicken");
	    menuOptions.add("beef");
	    menuOptions.add("lamb");
	}
	Map<String, Integer> inventory = Collections.synchronizedMap(new HashMap<String, Integer>());
    {
		for (String choice : menuOptions)
		{
			inventory.put(choice, 4);
		}
    }


	
	//Messages
	
	public void msgOrderRestock(Cook cook, Map<String, Integer> groceryList)
	{
		print("the market recieves the grocery list and changes it's state to restocking");
		orders.add(new IncomingOrder(cook, groceryList, reStockingState.restocking));
		stateChanged();
	}
	
	public void msgHereIsPayment(int bill)
	{
		money+=bill;
		print(name + " has $" + money);
		stateChanged();
	}

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */	
			
	protected boolean pickAndExecuteAnAction() {
		synchronized(orders)
		{
		for (IncomingOrder order: orders)
		{
			if (order.state==reStockingState.restocking)
			{
				TryToShipOrder(order, inventory);
				return true;
			}
		}
		}
		return false;
	}


	// Actions

	private void TryToShipOrder(IncomingOrder incomingOrder, Map<String, Integer> inventoryList)
	{
		boolean partialOrder = false;
		print("the market is now trying to ship the order");
		Map<String, Integer> groceryList = incomingOrder.incomingList;
		Map<String, Integer> outgoingList = groceryList;
		for (Map.Entry<String, Integer> groceryItem : groceryList.entrySet())
		{
			for (Map.Entry<String, Integer> marketItem : inventoryList.entrySet())
			{
				if (groceryItem.getKey().equals(marketItem.getKey())) //if the two comparing items are the same
				{	
					if (marketItem.getValue()>=groceryItem.getValue())//if the inventory has enough supplies for the order
					{
						marketItem.setValue(marketItem.getValue()-groceryItem.getValue());
						outgoingList.put(groceryItem.getKey(), groceryItem.getValue());
					}
					else if (marketItem.getValue()>0) //inventory has some supplies but not enough
					{
						partialOrder=true;
						outgoingList.put(groceryItem.getKey(), marketItem.getValue()); //take all the market's supplies
						marketItem.setValue(0);
					}
					else if (marketItem.getValue()==0)
					{
						outgoingList.put(groceryItem.getKey(), 0);
					}
				}	
			}
		}
		print("the market is shipping the food to the cook");
		if(partialOrder)
		{
			try
			{
				Thread.sleep(5000);
			}
			catch(Exception e)
			{
				System.out.println("Exception caught");
			}
			incomingOrder.state= reStockingState.failedToFufillOrder;
			cashier.msgHereIsMarketBill(this, CalculateBill(outgoingList), outgoingList);
			incomingOrder.cook.msgFufilledPartialOrder(name, outgoingList);
		}
		else
		{
			try
			{
				Thread.sleep(5000);
			}
			catch(Exception e)
			{
				System.out.println("Exception caught");
			}
			incomingOrder.state= reStockingState.fufillingOrder;
			cashier.msgHereIsMarketBill(this, CalculateBill(outgoingList), outgoingList);
			incomingOrder.cook.msgFufilledCompleteOrder(name, outgoingList);			
		}
	}
	
	private int CalculateBill(Map<String, Integer> outgoingList)
	{
		int bill = 0;
		for (Map.Entry<String, Integer> groceryItem : outgoingList.entrySet())
		{
			int quantity = groceryItem.getValue();
			int price = prices.get(groceryItem.getKey());
			int total = quantity * price;
			bill += total;
		}
		return bill;
	}
	
	//utilities

	public void setCashier(Cashier cashier)
	{
		this.cashier=cashier;
	}

	public String getName()
	{
		return name;
	}

}

