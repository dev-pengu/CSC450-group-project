package com.familyorg.familyorganizationapp.repository.impl;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import com.familyorg.familyorganizationapp.domain.Calendar;
import com.familyorg.familyorganizationapp.domain.Todolist;
import com.familyorg.familyorganizationapp.repository.custom.TodolistRepositoryCustom;
import com.querydsl.jpa.JPQLQuery;

@Repository
public class TodolistRepositoryImpl extends QuerydslRepositorySupport
    implements TodolistRepositoryCustom {
	
	public TodolistRepositoryImpl(Class<?> domainClass) {
		super(domainClass);
		// TODO Auto-generated constructor stub
	}

	private Logger logger = LoggerFactory.getLogger(TodolistRepositoryImpl.class);
	@Repository
	public class TodolistRepositoryImpl extends QuerydslRepositorySupport
	    implements TodolistRepositoryCustom 
	{
	  private Logger logger = LoggerFactory.getLogger(TodolistRepositoryImpl.class);

	  private Todolist todolistTable = Todolist.todolist;
	  private Todotask familyTable = Family.family;
	  private FamilyMembers memberTable = FamilyMembers.familyMembers;

	  public TodolistRepositoryImpl() {
	    super(Todolist.class);
	  }

	  @Override
	  public List<Todolist> todolistDataByFamilyIds(List<Long> familyIds) {
	    List<Todolist> todolists =
	        from(todolistTable).where(todolistTable.family.id.in(familyIds)).fetch();
	    return todolists;
	  }

	  @Override
	  public List<Todolist> search(List<Long> familyIds, List<Long> todolistIds, Set<Long> userIds) {
	    JPQLQuery<Todolist> query = from(todolistTable).innerJoin(todolistTable.family, familyTable)
	        .innerJoin(familyTable.members, memberTable).select(todolistTable).distinct();
	    
	    if (familyIds != null && familyIds.size() > 0) 
	    {
	      query.where(familyTable.id.in(familyIds));
	    }
	    
	    if (todolistIds != null && todolistIds.size() > 0) 
	    {
	      query.where(todolistTable.id.in(todolistIds));
	    }
	    
	    if (userIds != null && userIds.size() > 0) 
	    {
	      query.where(memberTable.user.id.in(userIds));
	    }
	    
	    return query.fetch();
	  }
	}

	@Override
	public List<Todolist> todolistDataByFamilyIds(List<Long> familyIds) {
		List<Todolist> todolists =
		        from(todolistTable).where(todolistTable.family.id.in(familyIds)).fetch();
		    return todolists;
	}

	@Override
	public List<Todolist> search(List<Long> familyIds, List<Long> todolistIds, 
			List<Long> todotaskIds, Set<Long> userIds) 
	{
		JPQLQuery<Calendar> query = from(todolistTable).innerJoin(todolistTable.family, familyTable)
		        .innerJoin(familyTable.members, memberTable)
		        .select(todolistTable)
		        .distinct();
		
		    if (familyIds != null && familyIds.size() > 0) 
		    {
		      query.where(familyTable.id.in(familyIds));
		    }
		    
		    if (todolistIds != null && todolistIds.size() > 0) 
		    {
		      query.where(todolistTable.id.in(todolistIds));
		    }
		    
		    if (userIds != null && userIds.size() > 0) {
		      query.where(memberTable.user.id.in(userIds));
		    }
		    
		    return query.fetch();
	}

}