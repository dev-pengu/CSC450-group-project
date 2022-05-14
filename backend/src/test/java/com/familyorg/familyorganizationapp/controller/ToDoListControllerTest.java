package com.familyorg.familyorganizationapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.familyorg.familyorganizationapp.DTO.ToDoListDto;
import com.familyorg.familyorganizationapp.DTO.ToDoListSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.ToDoListSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.ToDoTaskDto;
import com.familyorg.familyorganizationapp.DTO.builder.ToDoListDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.ToDoTaskDtoBuilder;
import com.familyorg.familyorganizationapp.service.ToDoListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class ToDoListControllerTest {

  private ToDoListService service;
  private ToDoListController controller;

  @BeforeEach
  public void setup() {
    service = mock(ToDoListService.class);
    controller = new ToDoListController(service);
  }

  @Test
  public void createList_success() {
    /* Given */
    doNothing().when(service).createList(any());

    /* When */
    ResponseEntity<String> response = controller.createList(new ToDoListDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(201, response.getStatusCodeValue());
  }

  @Test
  public void when_createList_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).createList(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          controller.createList(new ToDoListDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void updateList_success() {
    /* Given */
    doNothing().when(service).updateList(any());

    /* When */
    ResponseEntity<String> response = controller.updateList(new ToDoListDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_updateList_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).updateList(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          controller.updateList(new ToDoListDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void deleteList_success() {
    /* Given */
    doNothing().when(service).deleteList(any());

    /* When */
    ResponseEntity<String> response = controller.deleteList(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_deleteList_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).deleteList(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          controller.deleteList(1L);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void getList_success() {
    /* Given */
    when(service.getToDoList(any())).thenReturn(new ToDoListDtoBuilder().build());

    /* When */
    ResponseEntity<ToDoListDto> response = controller.getList(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_getList_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(service.getToDoList(any())).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          controller.getList(1L);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void getTask_success() {
    /* Given */
    when(service.getTask(any())).thenReturn(new ToDoTaskDtoBuilder().build());

    /* When */
    ResponseEntity<ToDoTaskDto> response = controller.getTask(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_getTask_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(service.getTask(any())).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          controller.getTask(1L);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void addTask_success() {
    /* Given */
    doNothing().when(service).addTask(any());

    /* When */
    ResponseEntity<String> response = controller.addTask(new ToDoTaskDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(201, response.getStatusCodeValue());
  }

  @Test
  public void when_addTask_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).addTask(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          controller.addTask(new ToDoTaskDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void updateTask_success() {
    /* Given */
    doNothing().when(service).updateTask(any());

    /* When */
    ResponseEntity<String> response = controller.updateTask(new ToDoTaskDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_updateTask_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).updateTask(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          controller.updateTask(new ToDoTaskDtoBuilder().build());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void deleteTask_success() {
    /* Given */
    doNothing().when(service).deleteTask(any());

    /* When */
    ResponseEntity<String> response = controller.deleteTask(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_deleteTask_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).deleteTask(any());

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          controller.deleteTask(1L);
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void search_success() {
    /* Given */
    when(service.search(any())).thenReturn(new ToDoListSearchResponseDto());

    /* When */
    ResponseEntity<ToDoListSearchResponseDto> response =
        controller.search(new ToDoListSearchRequestDto());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_search_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(service.search(any())).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
        RuntimeException.class,
        () -> {
          controller.search(new ToDoListSearchRequestDto());
        });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }
}
