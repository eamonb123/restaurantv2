##Restaurant Project Repository

###Student Information
  + Name: Eamon Barkhordarian
  + USC Email: ebarkhor@usc.edu
  + USC ID: 2894601675
  + section 30300
  + lab 30239 

###Resources
  + [Restaurant v1](http://www-scf.usc.edu/~csci201/readings/restaurant-v1.html)
  + [Agent Roadmap](http://www-scf.usc.edu/~csci201/readings/agent-roadmap.html)
  
 ###How to run
  You can simply run the code by clicking the green arrow in Eclipe.
  To run it in Eclipse after cloning the project, do the same as you would to open up the project for Lab1

  TO TEST DIFFERENT SCENARIOS
  - out of food: go to the cook agent and change the 3rd parameter of the Food constructor. Set it to zero and then all the foods the cook has will be set to zero. Run the code and then witness the changes
  - low food: go to the same line as the out of food, but set a number above zero and below the threshold. when the waiter delivers that order to the cook, the cook will realize he is low in certain foods and send out an order to the market
  - to manipulate the market inventory, you can change line 47 in the marketagent.java file to whatever number you want. depending on what that number is, the cook will order accordingly from the different markets
  - waiter on break: once you create a waiter, you can click his break checkbox on the bottom of the window. if there is more than one waiter, he will go on break, otherwise he will keep working
  - cashier: once the customer is done eating, there will be print statements that calculate whether he can pay the meal or not. he will leave either way

  
  
 ###Problems
 - I currently cannot uncheck a waiter on break to get him back to work
 - I did not yet implement the GUI for the cashier interaction but all the communication is in print statements.
 - the markets send
 