package com.familyorg.familyorganizationapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.familyorg.familyorganizationapp.DTO.ShoppingListDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListItemDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListSearchResponseDto;
import com.familyorg.familyorganizationapp.DTO.builder.ShoppingListDtoBuilder;
import com.familyorg.familyorganizationapp.DTO.builder.ShoppingListItemDtoBuilder;
import com.familyorg.familyorganizationapp.service.ShoppingListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class ShoppingListControllerTest {

  private ShoppingListService service;
  private ShoppingListController controller;

  @BeforeEach
  public void setup() {
    service = mock(ShoppingListService.class);
    controller = new ShoppingListController(service);
  }

  @Test
  public void createShoppingList_success() {
    /* Given */
    doNothing().when(service).createShoppingList(any());

    /* When */
    ResponseEntity<String> response = controller.createShoppingList(new ShoppingListDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(201, response.getStatusCodeValue());
  }

  @Test
  public void when_createShoppingList_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).createShoppingList(any());

    /* When */
    assertThrows(
      RuntimeException.class,
      () -> {
        controller.createShoppingList(new ShoppingListDtoBuilder().build());
      });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void updateShoppingList_success() {
    /* Given */
    doNothing().when(service).updateShoppingList(any());

    /* When */
    ResponseEntity<String> response = controller.updateShoppingList(new ShoppingListDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_updateShoppingList_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).updateShoppingList(any());

    /* When */
    assertThrows(
      RuntimeException.class,
      () -> {
        controller.updateShoppingList(new ShoppingListDtoBuilder().build());
      });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void deleteShoppingList_success() {
    /* Given */
    doNothing().when(service).deleteShoppingList(any());

    /* When */
    ResponseEntity<String> response = controller.deleteShoppingList(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_deleteShoppingList_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).deleteShoppingList(any());

    /* When */
    assertThrows(
      RuntimeException.class,
      () -> {
        controller.deleteShoppingList(1L);
      });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void getShoppingList_success() {
    /* Given */
    when(service.getShoppingList(any(Long.class))).thenReturn(new ShoppingListDtoBuilder().build());

    /* When */
    ResponseEntity<ShoppingListDto> response = controller.getShoppingList(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_getShoppingList_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(service.getShoppingList(any(Long.class))).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
      RuntimeException.class,
      () -> {
        controller.getShoppingList(1L);
      });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void addListItem_success() {
    /* Given */
    doNothing().when(service).addItem(any());

    /* When */
    ResponseEntity<String> response = controller.addListItem(new ShoppingListItemDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(201, response.getStatusCodeValue());
  }

  @Test
  public void when_addListItem_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).addItem(any());

    /* When */
    assertThrows(
      RuntimeException.class,
      () -> {
        controller.addListItem(new ShoppingListItemDtoBuilder().build());
      });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void updateListItem_success() {
    /* Given */
    doNothing().when(service).updateItem(any());

    /* When */
    ResponseEntity<String> response = controller.updateListItem(new ShoppingListItemDtoBuilder().build());

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_updateListItem_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).updateItem(any());

    /* When */
    assertThrows(
      RuntimeException.class,
      () -> {
        controller.updateListItem(new ShoppingListItemDtoBuilder().build());
      });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void deleteListItem_success() {
    /* Given */
    doNothing().when(service).deleteItem(any());

    /* When */
    ResponseEntity<String> response = controller.deleteListItem(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_deleteListItem_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    doThrow(RuntimeException.class).when(service).deleteItem(any());

    /* When */
    assertThrows(
      RuntimeException.class,
      () -> {
        controller.deleteListItem(1L);
      });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void getListItem_success() {
    /* Given */
    when(service.getItem(any(Long.class))).thenReturn(new ShoppingListItemDtoBuilder().build());

    /* When */
    ResponseEntity<ShoppingListItemDto> response = controller.getListItem(1L);

    /* Then */
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void when_getListItem_and_exception_thrown_then_exception_bubbles() {
    /* Given */
    when(service.getItem(any(Long.class))).thenThrow(RuntimeException.class);

    /* When */
    assertThrows(
      RuntimeException.class,
      () -> {
        controller.getListItem(1L);
      });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }

  @Test
  public void search_success() {
    /* Given */
    when(service.search(any())).thenReturn(new ShoppingListSearchResponseDto());

    /* When */
    ResponseEntity<ShoppingListSearchResponseDto> response = controller.search(new ShoppingListSearchRequestDto());

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
        controller.search(new ShoppingListSearchRequestDto());
      });

    /* Then */
    // nothing, an exception should be thrown in the service call
  }
}
