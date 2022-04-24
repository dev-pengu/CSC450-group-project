package com.familyorg.familyorganizationapp.controller;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.familyorg.familyorganizationapp.DTO.TodolistDto;
import com.familyorg.familyorganizationapp.DTO.TodotaskDto;
import com.familyorg.familyorganizationapp.service.TodolistService;


@RestController
@RequestMapping("/api/v1/calendar")
public class ToDoListController {
	
	private static final Logger logger = LoggerFactory.getLogger(CalendarController.class);

	TodolistService todolistService;
	TodotaskDto todotask;

	@Autowired
	public ToDoListController(TodolistService todolistService) {
		super();
		this.todolistService = todolistService;
	}
	
	@PostMapping()
	  public ResponseEntity<String> createList(@RequestBody TodolistDto request) {
	    todolistService.createList(request);
	    return new ResponseEntity<>("To do list created successfully.", HttpStatus.CREATED);
	  }
	
	@PatchMapping()
	  public ResponseEntity<String> updateList(@RequestBody TodolistDto request) {
	    todolistService.updateList(request);
	    return new ResponseEntity<>("To do list updated successfully.", HttpStatus.OK);
	  }
	
	@DeleteMapping()
	public ResponseEntity<String> deleteList(@RequestParam Long id){
		todolistService.deleteList(id);
	    return new ResponseEntity<>("To do list deleted successfully.", HttpStatus.OK);
	}
	
	@GetMapping("/task")
	public ResponseEntity<TodotaskDto> getTask(@RequestParam Long id) {
	    TodotaskDto todotask = todolistService.getTask(id);
	    return new ResponseEntity<>(todotask, HttpStatus.OK);
	  }
	
	@PostMapping("/task")
	  public ResponseEntity<String> addTask(@RequestBody TodotaskDto todotask) {
	    todolistService.addTask(todotask);
	    return new ResponseEntity<>("To do list created successfully.", HttpStatus.CREATED);
	  }
	
	@PatchMapping("/task")
	  public ResponseEntity<String> updateTask(@RequestBody TodotaskDto todotask) {
	    todolistService.updateTask(todotask);
	    return new ResponseEntity<>("To do list updated successfully.", HttpStatus.OK);
	  }
	
	@DeleteMapping("/task")
	public ResponseEntity<String> deleteTask(@RequestParam Long id){
		todolistService.deleteTask(id);
	    return new ResponseEntity<>("To do list deleted successfully.", HttpStatus.OK);
	}
}