package restaurant.interfaces;

import java.awt.Point;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;
import restaurant.HostAgent.MyCustomer;
import restaurant.HostAgent.Table;
import restaurant.HostAgent.MyWaiter;


public interface Host {
	public abstract void msgIWantToEat(Customer cust);

	public abstract void msgWakeUp();
	
	public abstract void msgPickedUpCustomer(Point loc);
	
	public abstract void msgCanIGoOnBreak(Waiter askingWaiter);
	
	public abstract void msgTableIsFree(Waiter waiter, Customer customer, int tableNumber);

	public abstract void setWaiter(Waiter waiter);

}
