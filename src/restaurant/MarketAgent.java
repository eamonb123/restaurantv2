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
	    HashMap<String, Integer> groceryList = new HashMap<String, Integer>();
	    reStockingState state;
		IncomingOrder(CookAgent cook,  HashMap<String, Integer> groceryList, reStockingState state)
		{
			this.cook=cook;
			this.groceryList=groceryList;
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
		print("trying to ship order");
		HashMap<String, Integer> groceryList = incomingOrder.groceryList;
		for (Map.Entry<String, Integer> grocery : groceryList.entrySet())
		{
			for (Map.Entry<String, Integer> inventory : inventoryList.entrySet())
			{
				if (grocery.getKey().equals(inventory.getKey())) //if the grocerylist item equals the inventory item
				{	
					if (inventory.getValue()>grocery.getValue())//if the inventory has enough supplies for the order
					{
						grocery.setValue(grocery.getValue());
						inventory.setValue(inventory.getValue()-grocery.getValue());
					}
					else //inventory does not have enough supplies
					{
						grocery.setValue(grocery.getValue()-inventory.getValue());
						incomingOrder.cook.msgFufilledPartialOrder(groceryList);
					}
				}	
			}
		}
		incomingOrder.cook.msgFufilledCompleteOrder(groceryList);
	}
	


	//utilities


	public void setCook(CookAgent cook)
	{
		this.cook=cook;
	}
	
}

