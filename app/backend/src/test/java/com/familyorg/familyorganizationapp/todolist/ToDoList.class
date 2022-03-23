package com.familyorg.familyorganizationapp.todolist;

import com.familyorg.familyorganizationapp.controller.FamilyController;
import com.familyorg.familyorganizationapp.service.FamilyService;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;


public class ToDoList {
	static FamilyService familyService;
	  static FamilyController familyController;
	  public static List<String> currentTasks;
	  
	  public ToDoList()
	  {
		  //Create a list if one does not exist
		  currentTasks = new ArrayList<String>();
	  }
	 
	  public ToDoList(List<String> passedTasks)
	  {
		  //Constructor for "a list of tasks was given at creation of to do list" 
		  currentTasks = passedTasks;
	  }
	  
	  public static void CheckPrivileges()
	  {
		  //Ensure that only people with privileges can edit lists
		  
	  }
	  
	  /* Description: The user will give a list of tasks separated by commas
	   * Example input: Feed the dog, take the kids to school, do dishes
	   * With this input, we separate each task and add it to the list
	   */
	  public static void AddToList(String passedTasks)
	  {
		  //Search for commas in the passedTasks given by the user
		  int commaLocated = passedTasks.indexOf( ',' );
		  
		  //If commaLocated is at least 0, there are commas and multiple tasks
		  //will be added to the to-do list
		  if(commaLocated >= 0)
		  {
			  
			  //Split each string based on commas
			  //List<String> newList = passedTasks.split(",");
			  //Remove said commas
			  //Add each new task into the list
			  
		  }
		  //If commaLocated = -1, there are no commas and only 1 task is added to the to-do list
		  else
		  {
			  //Add a new task to the to-do list
			  currentTasks.add(passedTasks);
			  
		  }
	  }
	  public static void DeleteFromList(int selectedTask)
	  {
		  //Remove an item from list
		  currentTasks.remove(selectedTask);
	  }
}
