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
//	public enum OrderState
//	{nothing};
	public enum reStockingState
	{none, restocking, fufilledOrder, failedToFufillOrder};
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
			inventory.put(choice, 500);
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
		print("trying to ship order");
//		System.out.println(groceryList);
		HashMap<String, Integer> groceryList = incomingOrder.incomingList;
		for (Map.Entry<String, Integer> groceryItem : incomingOrder.incomingList.entrySet())
		{
			for (Map.Entry<String, Integer> marketItem : inventoryList.entrySet())
			{
				if (groceryItem.getKey().equals(marketItem.getKey())) //if the two comparing items are the same
				{	
					if (marketItem.getValue()>groceryItem.getValue())//if the inventory has enough supplies for the order
					{
						marketItem.setValue(marketItem.getValue()-groceryItem.getValue());
					}
					else //inventory does not have enough supplies
					{
						partialOrder=true;
						groceryItem.setValue(marketItem.getValue());
					}
				}	
			}
		}
		if(partialOrder)
		{
			incomingOrder.cook.msgFufilledPartialOrder(groceryList);
			incomingOrder.state= reStockingState.failedToFufillOrder;
		}
		else
		{
			incomingOrder.cook.msgFufilledCompleteOrder(groceryList);
			incomingOrder.state= reStockingState.fufilledOrder;
		}
		
	}
	


	//utilities


	public void setCook(CookAgent cook)
	{
		this.cook=cook;
	}
	
}

