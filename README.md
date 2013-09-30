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
"import the restaurant application:

    File -> New -> Other

    Choose Java Project from Existing Ant Buildfile

    Click on the Browse button

    Navigate to the git repository you cloned in the previous section

    Choose the build.xml file.

    [IMP]: Check the Link to the buildfile in the file system box

    Press Finish
"

  
  
 ###Problems
 -If you create a customer before your create a waiter, that customer is forever lost. You must first create 
a waiter and then create a customer for them to interact. 
-There are no image icons on the waiters and customers. I did not know how to have the strings follow them
and stay directly on top of them the entire time
- There is a potential semaphore issue that runs in an infinite loop when all the customers leave and all the tables are empty
- I could not get the pause button to work