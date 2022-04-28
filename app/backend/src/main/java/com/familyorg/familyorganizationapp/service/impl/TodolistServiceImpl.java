package com.familyorg.familyorganizationapp.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.familyorg.familyorganizationapp.DTO.TodolistDto;
import com.familyorg.familyorganizationapp.DTO.TodotaskDto;
import com.familyorg.familyorganizationapp.DTO.builder.ToDoListDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.ToDoTaskDtoBuilder;
import com.familyorg.familyorganizationapp.Exception.AuthorizationException;
import com.familyorg.familyorganizationapp.Exception.BadRequestException;
import com.familyorg.familyorganizationapp.Exception.ResourceNotFoundException;
import com.familyorg.familyorganizationapp.Exception.UserNotFoundException;
import com.familyorg.familyorganizationapp.Exception.ApiExceptionCode;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.Role;
import com.familyorg.familyorganizationapp.domain.Todolist;
import com.familyorg.familyorganizationapp.domain.Todotask;
import com.familyorg.familyorganizationapp.domain.User;
import com.familyorg.familyorganizationapp.domain.search.SearchFilter;
import com.familyorg.familyorganizationapp.repository.TodolistRepository;
import com.familyorg.familyorganizationapp.repository.TodotaskRepository;
import com.familyorg.familyorganizationapp.service.FamilyService;
import com.familyorg.familyorganizationapp.service.TodolistService;
import com.familyorg.familyorganizationapp.service.UserService;
import com.familyorg.familyorganizationapp.util.DateUtil;

@Service
public class TodolistServiceImpl implements TodolistService {

  private Logger logger = LoggerFactory.getLogger(TodolistServiceImpl.class);
  TodolistRepository todolistRepository;
  TodotaskRepository todotaskRepository;

  UserService userService;
  FamilyService familyService;

  @Autowired
  	public TodolistServiceImpl(TodolistRepository todolistRepository,
	    TodotaskRepository todotaskRepository, UserService userService,
	    FamilyService familyService) 
    {
	    this.todolistRepository = todolistRepository;
	    this.todotaskRepository = todotaskRepository;
	    this.userService = userService;
	    this.familyService = familyService;
    }

	
	@Override
	@Transactional
	public void createList(TodolistDto request) 
		throws UserNotFoundException, AuthorizationException, ResourceNotFoundException 
		{
		    User requestingUser = userService.getRequestingUser();
		    Optional<Family> family = familyService.getFamilyById(request.getFamilyId());
		    if (request.getDescription() == null) {
			throw new BadRequestException(
				ApiExceptionCode.REQUIRED_PARAM_MISSING, "List description must not be null.");
			}
			if (request.getFamilyId() == null) {
			throw new BadRequestException(
				ApiExceptionCode.REQUIRED_PARAM_MISSING,
				"List must have a family id to associate the list with.");
			}
		    if (family.isEmpty()) //If family cannot be found
		    {
		    	throw new ResourceNotFoundException(
						ApiExceptionCode.FAMILY_DOESNT_EXIST,
				        "Family with id " + request.getFamilyId() + " not found.");
		    }
		    
		    boolean hasAppropriatePermissions =
		        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.ADULT);
		    
		    if (!hasAppropriatePermissions) //If user does not have permissions to create list
		    {
		    	throw new AuthorizationException(
		    			ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
		    }
		      
		    Todolist todolist = new Todolist();
		    todolist.setId(request.getId());
			todolist.setDescription(request.getDescription());
		    todolist.setFamily(family.get());
		    todolist.setIsDefault(false);
		    todolistRepository.save(todolist);
	}
	
	@Override
	@Transactional
	public void updateList(TodolistDto request) 
		throws UserNotFoundException, AuthorizationException, ResourceNotFoundException 
	{
		//locate user and their role privileges
		User requestingUser = userService.getRequestingUser();
	    Optional<Todolist> todolistOpt = todolistRepository.findById(request.getId());
	    
	    if (todolistOpt.isEmpty())
	    {
	    	throw new ResourceNotFoundException(
				ApiExceptionCode.LIST_DOESNT_EXIST, "To do list with id " + request.getId() + " not found.");
	    }
	      
	    boolean hasAppropriatePermissions = familyService
	        .verfiyMinimumRoleSecurity(todolistOpt.get().getFamily(), requestingUser, Role.ADULT);
	    
	    if (!hasAppropriatePermissions) {
		throw new AuthorizationException(
			ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
		}
	    
	    Todolist todolist = todolistOpt.get();
	    
	    if (todolist.getDescription() != request.getDescription())
	    {
	    	todolist.setDescription(request.getDescription());
	    }
	    
	    //Save to do list item to the repository
	    todolistRepository.save(todolist);
	}
	
	@Override
	@Transactional
	public void deleteList(Long id) 
		throws UserNotFoundException, AuthorizationException, ResourceNotFoundException 
	{
		User requestingUser = userService.getRequestingUser();
	    Optional<Todolist> todolist = todolistRepository.findById(id);
	    
	    if (todolist.isEmpty())
	    {
	    	throw new ResourceNotFoundException(
	    			ApiExceptionCode.LIST_DOESNT_EXIST, "To do list with id " + id + " not found.");
	    }
	    
	    boolean hasAppropriatePermissions = familyService
	        .verfiyMinimumRoleSecurity(todolist.get().getFamily(), requestingUser, Role.ADMIN);
	    
	    if (!hasAppropriatePermissions)
	    {
	    	throw new AuthorizationException(
	    			ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
	    }
		if (todolist.get().getIsDefault()) {
		throw new BadRequestException(
			ApiExceptionCode.ILLEGAL_ACTION_REQUESTED,
			"The family's default to do list cannot be deleted.");
		}
		todolistRepository.delete(todolist.get());
	}
	
	@Override
	public TodotaskDto getTask(Long id) 
			throws UserNotFoundException, AuthorizationException, ResourceNotFoundException 
	{
		if (id == null) {
		throw new BadRequestException(
			ApiExceptionCode.REQUIRED_PARAM_MISSING, "Item id must not be null.");
		}
		Optional<Todotask> todotask = todotaskRepository.findById(id);
		if (todotask.isEmpty()) {
		throw new ResourceNotFoundException(
			ApiExceptionCode.LIST_ITEM_DOESNT_EXIST, "List item with id " + id + " not found.");
		}
		User requestingUser = userService.getRequestingUser();
		boolean hasAppropriatePermissions =
			familyService.verfiyMinimumRoleSecurity(
				todotask.get().getFamily(), requestingUser, Role.CHILD);
		if (!hasAppropriatePermissions) {
		throw new AuthorizationException(
			ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
		}

		return new ToDoListDtoBuilder()
			.setId(todotask.get().getId())
			.setDescription(todotask.get().getDescription())
			.setIsDefault(todotask.get().getIsDefault())
			.setFamilyId(todotask.get().getFamily())
			.build();
	}
	
	
	@Override
	@Transactional
	public void addTask(TodotaskDto task) 
		throws UserNotFoundException, AuthorizationException, ResourceNotFoundException 
	{
		User requestingUser = userService.getRequestingUser();
	    Optional<Family> family = familyService.getFamilyById(task.getId());
	    
	    if (family.isEmpty()) //If family cannot be found
	    {
	    	throw new ResourceNotFoundException(
	    			ApiExceptionCode.LIST_DOESNT_EXIST, "Family with id " + task.getId() + " not found.");
	    }
	    
	    boolean hasAppropriatePermissions =
	        familyService.verfiyMinimumRoleSecurity(family.get(), requestingUser, Role.CHILD);
	    
	    if (!hasAppropriatePermissions) //If user does not have permissions to add task
	    {
	    	throw new AuthorizationException(
	    			ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
	    }
	      
	    Todotask todotask = new Todotask();
	    todotask.setId(task.getId());
		todotask.setDescription(task.getDescription());
	    todotask.setFamily(family.get());
	    todotask.setIsDefault(false);
	    todotaskRepository.save(todotask);
	}
	
	@Override
	@Transactional
	public void updateTask(TodotaskDto task) 
		throws UserNotFoundException, AuthorizationException, ResourceNotFoundException 
	{
		//locate user and their role privileges
		User requestingUser = userService.getRequestingUser();
		Optional<Todotask> todotaskOpt = todotaskRepository.findById(task.getId());
			    
		if (todotaskOpt.isEmpty())
		{
			throw new ResourceNotFoundException(
					ApiExceptionCode.LIST_DOESNT_EXIST, "To do list with id " + task.getId() + " not found.");
		}
			      
		boolean hasAppropriatePermissions = familyService
			.verfiyMinimumRoleSecurity(todotaskOpt.get().getFamily(), requestingUser, Role.ADULT);
			    
		if (!hasAppropriatePermissions)
		{
			throw new AuthorizationException(
					ApiExceptionCode.USER_PRIVILEGES_TOO_LOW,"User not authorized to complete this actions.");
		}
			    
			Todotask todotask = todotaskOpt.get();
			if (task.getDescription() != null) {
      			todotask.getId().setDescription(todotask.getDescription());
    		}
			todotask.getId().setId(todotask.getId());
			todotask.setFamily(todotask.getFamily());
			
	    	todotask.setIsDefault(false);
			todotaskRepository.save(todotask.getId());
	}
	
	@Override
	@Transactional
	public void deleteTask(Long id) 
		throws UserNotFoundException, AuthorizationException, ResourceNotFoundException 
	{
		User requestingUser = userService.getRequestingUser();
	    Optional<Todotask> todotask = todotaskRepository.findById(id);
	    
	    if (todotask.isEmpty())
	    {
	    	throw new ResourceNotFoundException(
	    			ApiExceptionCode.LIST_DOESNT_EXIST,"To do task with id " + id + " not found.");
	    }
	    
	    boolean hasAppropriatePermissions = familyService
	        .verfiyMinimumRoleSecurity(todotask.get().getFamily(), requestingUser, Role.ADMIN);
	    
	    if (!hasAppropriatePermissions)
	    {
	    	throw new AuthorizationException(
	    			ApiExceptionCode.USER_PRIVILEGES_TOO_LOW, "User not authorized to complete this action.");
	    }
	    
	    todotaskRepository.delete(todotask.get());
	}

}

