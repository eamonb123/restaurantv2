package restaurant.interfaces;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;
import restaurant.HostAgent.MyCustomer;
import restaurant.HostAgent.Table;
import restaurant.HostAgent.MyWaiter;


public interface Host {
	public abstract void msgIWantToEat(Customer cust);

	public abstract void msgWakeUp();
	
	public abstract void msgCanIGoOnBreak(Waiter askingWaiter);
	
	public abstract void msgWaiterOnBreak(Waiter waiter);

	public abstract void msgTableIsFree(int tableNumber);

	public abstract void setWaiter(Waiter waiter);

}
