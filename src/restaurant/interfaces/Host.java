package restaurant.interfaces;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;
import restaurant.HostAgent.MyCustomer;
import restaurant.HostAgent.Table;
import restaurant.HostAgent.MyWaiter;


public interface Host {
	public abstract void msgIWantToEat(Customer cust);

	public abstract void msgWakeUp();
	
	public abstract void msgCanIGoOnBreak(MyWaiter askingWaiter);
	
	public abstract void msgWaiterOnBreak(MyWaiter waiter);

	public abstract void msgAddWaiter(MyWaiter waiter);

	public abstract void msgTableIsFree(int tableNumber);

}
