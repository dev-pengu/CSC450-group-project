package com.familyorg.familyorganizationapp.controller;

import com.familyorg.familyorganizationapp.DTO.ToDoListSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.ToDoListSearchResponseDto;
import java.util.List;
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
import com.familyorg.familyorganizationapp.DTO.ToDoListDto;
import com.familyorg.familyorganizationapp.DTO.ToDoTaskDto;
import com.familyorg.familyorganizationapp.service.ToDoListService;

@RestController
@RequestMapping("/api/v1/todo")
public class ToDoListController {

  ToDoListService toDoListService;

  @Autowired
  public ToDoListController(ToDoListService toDoListService) {
    this.toDoListService = toDoListService;
  }

  @GetMapping("/lists")
  public ResponseEntity<List<ToDoListDto>> getLists(
      @RequestParam(name = "familyId", required = false) Long familyId) {
    List<ToDoListDto> lists = toDoListService.getLists(familyId);
    return new ResponseEntity<>(lists, HttpStatus.OK);
  }

  @PostMapping()
  public ResponseEntity<String> createList(@RequestBody ToDoListDto request) {
    toDoListService.createList(request);
    return new ResponseEntity<>("To do list created successfully.", HttpStatus.CREATED);
  }

  @PatchMapping()
  public ResponseEntity<String> updateList(@RequestBody ToDoListDto request) {
    toDoListService.updateList(request);
    return new ResponseEntity<>("To do list updated successfully.", HttpStatus.OK);
  }

  @DeleteMapping()
  public ResponseEntity<String> deleteList(@RequestParam("id") Long listId) {
    toDoListService.deleteList(listId);
    return new ResponseEntity<>("To do list deleted successfully.", HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<ToDoListDto> getList(@RequestParam("id") Long listId) {
    ToDoListDto response = toDoListService.getToDoList(listId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/task")
  public ResponseEntity<ToDoTaskDto> getTask(@RequestParam("id") Long taskId) {
    ToDoTaskDto todotask = toDoListService.getTask(taskId);
    return new ResponseEntity<>(todotask, HttpStatus.OK);
  }

  @PostMapping("/task")
  public ResponseEntity<String> addTask(@RequestBody ToDoTaskDto request) {
    toDoListService.addTask(request);
    return new ResponseEntity<>("To do list created successfully.", HttpStatus.CREATED);
  }

  @PatchMapping("/task")
  public ResponseEntity<String> updateTask(@RequestBody ToDoTaskDto request) {
    toDoListService.updateTask(request);
    return new ResponseEntity<>("To do list updated successfully.", HttpStatus.OK);
  }

  @DeleteMapping("/task")
  public ResponseEntity<String> deleteTask(@RequestParam("id") Long taskId) {
    toDoListService.deleteTask(taskId);
    return new ResponseEntity<>("To do list deleted successfully.", HttpStatus.OK);
  }

  @PostMapping("/search")
  public ResponseEntity<ToDoListSearchResponseDto> search(
      @RequestBody() ToDoListSearchRequestDto request) {
    ToDoListSearchResponseDto response = toDoListService.search(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
