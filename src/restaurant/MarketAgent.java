package restaurant;

import agent.Agent;
import restaurant.CookAgent.Food;
import restaurant.CookAgent.state;
import restaurant.CustomerAgent.AgentEvent;
import restaurant.CustomerAgent.AgentState;
import restaurant.HostAgent.Table;
import restaurant.gui.WaiterGui;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Restaurant Host Agent
 */

public class MarketAgent extends Agent {
	CookAgent cook;
	private String name; 
	public class IncomingOrder
	{
		CookAgent cook;
	    HashMap<String, Integer> incomingList = new HashMap<String, Integer>();
	    reStockingState state;
		IncomingOrder(CookAgent cook,  HashMap<String, Integer> incomingList, reStockingState state)
		{
			this.cook=cook;
			this.incomingList=incomingList;
			this.state=state;	
		}
	}
	public enum reStockingState
	{none, restocking, fufillingOrder, failedToFufillOrder};
	public List<IncomingOrder> orders = new ArrayList<IncomingOrder>();
	List<String> menuOptions = new ArrayList<String>();{
	    menuOptions.add("chicken");
	    menuOptions.add("beef");
	    menuOptions.add("lamb");
	}
    HashMap<String, Integer> inventory = new HashMap<String, Integer>();
    {
		for (String choice : menuOptions)
		{
			inventory.put(choice, 4);
		}
    }


	
	//Messages
	
	public void msgOrderRestock(CookAgent cook, HashMap<String, Integer> groceryList)
	{
		print("the market recieves the grocery list and changes it's state to restocking");
		orders.add(new IncomingOrder(cook, groceryList, reStockingState.restocking));
		stateChanged();
	}
	

	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */	
			
	protected boolean pickAndExecuteAnAction() {
		for (IncomingOrder order: orders)
		{
			if (order.state==reStockingState.restocking)
			{
				TryToShipOrder(order, inventory);
				return true;
			}
		}
		return false;
	}


	// Actions

	private void TryToShipOrder(IncomingOrder incomingOrder, HashMap<String, Integer> inventoryList)
	{
		boolean partialOrder = false;
		print("the market is now trying to ship the order");
		HashMap<String, Integer> groceryList = incomingOrder.incomingList;
		HashMap<String, Integer> outgoingList = groceryList;
		for (Map.Entry<String, Integer> groceryItem : incomingOrder.incomingList.entrySet())
		{
			for (Map.Entry<String, Integer> marketItem : inventoryList.entrySet())
			{
				if (groceryItem.getKey().equals(marketItem.getKey())) //if the two comparing items are the same
				{	
					if (marketItem.getValue()>groceryItem.getValue())//if the inventory has enough supplies for the order
					{
						marketItem.setValue(marketItem.getValue()-groceryItem.getValue());
						outgoingList.put(groceryItem.getKey(), groceryItem.getValue());
					}
					else if (marketItem.getValue()!=0) //inventory has some supplies but not enough
					{
						partialOrder=true;
						outgoingList.put(groceryItem.getKey(), marketItem.getValue());
						marketItem.setValue(0);
					}
				}	
			}
		}
		print("the market is shipping the food to the cook");
		if(partialOrder)
		{
			incomingOrder.state= reStockingState.failedToFufillOrder;
			incomingOrder.cook.msgFufilledPartialOrder(outgoingList);
		}
		else
		{
			incomingOrder.state= reStockingState.fufillingOrder;
			incomingOrder.cook.msgFufilledCompleteOrder(outgoingList);			
		}
	}
	


	//utilities


	public void setCook(CookAgent cook)
	{
		this.cook=cook;
	}
	
}

