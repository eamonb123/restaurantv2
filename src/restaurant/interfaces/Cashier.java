package restaurant.interfaces;


public interface Cashier {
	public abstract void msgComputeCheck(Waiter waiter, String choice, int tableNumber);
	
	public abstract void msgPayBill(Customer customer, int money, int bill);
}
