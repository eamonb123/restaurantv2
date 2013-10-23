package restaurant.test.mock;


import java.awt.Point;
import java.util.List;

import restaurant.HostAgent;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter;


/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public Host host;
	public EventLog log = new EventLog();
	private String choice;
	//public HostAgent host;
	
	public MockCustomer(String name) {
		super(name);
	}
	@Override
	public void gotHungry() {
		log.add(new LoggedEvent("customer is hungry and host is adding customer to list"));
		host.msgIWantToEat(this);
	}
	@Override
	public void msgSemaphoreRelease() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgFollowMeToTable(Waiter wait, List<String> menuOptions,
			int tableNumber, Point loc) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgWhatWouldYouLike(List<String> menu) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgReOrder(List<String> menu) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgHereIsYourFood(String food) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgHereIsReceipt(int bill) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getChoice(){
		return choice;
	}
	
	@Override
	public void setWaiter(Waiter w) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setHost(Host host) {
		this.host = host;
	}

//	@Override
//	public void HereIsYourTotal(double total) {
//		log.add(new LoggedEvent("Received HereIsYourTotal from cashier. Total = "+ total));
//
//		if(this.name.toLowerCase().contains("thief")){
//			//test the non-normative scenario where the customer has no money if their name contains the string "theif"
//			cashier.IAmShort(this, 0);
//
//		}else if (this.name.toLowerCase().contains("rich")){
//			//test the non-normative scenario where the customer overpays if their name contains the string "rich"
//			cashier.HereIsMyPayment(this, Math.ceil(total));
//
//		}else{
//			//test the normative scenario
//			cashier.HereIsMyPayment(this, total);
//		}
//	}
//
//	@Override
//	public void HereIsYourChange(double total) {
//		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ total));
//	}
//
//	@Override
//	public void YouOweUs(double remaining_cost) {
//		log.add(new LoggedEvent("Received YouOweUs from cashier. Debt = "+ remaining_cost));
//	}
	
	

}
