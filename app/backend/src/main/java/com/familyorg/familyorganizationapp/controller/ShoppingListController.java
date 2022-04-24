package com.familyorg.familyorganizationapp.controller;

import com.familyorg.familyorganizationapp.DTO.ShoppingListDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListItemDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListSearchRequestDto;
import com.familyorg.familyorganizationapp.DTO.ShoppingListSearchResponseDto;
import com.familyorg.familyorganizationapp.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api/v1/shopping")
public class ShoppingListController {

  private ShoppingListService shoppingListService;

  @Autowired
  public ShoppingListController(ShoppingListService shoppingListService) {
    this.shoppingListService = shoppingListService;
  }

  @PostMapping()
  public ResponseEntity<String> createShoppingList(@RequestBody() ShoppingListDto request) {
    shoppingListService.createShoppingList(request);
    return new ResponseEntity<>("List created successfully", HttpStatus.CREATED);
  }

  @PatchMapping()
  public ResponseEntity<String> updateShoppingList(@RequestBody() ShoppingListDto request) {
    shoppingListService.updateShoppingList(request);
    return new ResponseEntity<>("List updated successfully", HttpStatus.OK);
  }

  @DeleteMapping()
  public ResponseEntity<String> deleteShoppingList(@RequestParam("id") Long listId) {
    shoppingListService.deleteShoppingList(listId);
    return new ResponseEntity<>("List deleted successfully", HttpStatus.OK);
  }

  @GetMapping()
  public ResponseEntity<ShoppingListDto> getShoppingList(@RequestParam("id") Long listId) {
    ShoppingListDto response = shoppingListService.getShoppingList(listId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/item")
  public ResponseEntity<String> addListItem(@RequestBody() ShoppingListItemDto request) {
    shoppingListService.addItem(request);
    return new ResponseEntity<>("Item added successfully.", HttpStatus.CREATED);
  }

  @PatchMapping("/item")
  public ResponseEntity<String> updateListItem(@RequestBody() ShoppingListItemDto request) {
    shoppingListService.updateItem(request);
    return new ResponseEntity<>("Item updated successfully.", HttpStatus.OK);
  }

  @DeleteMapping("/item")
  public ResponseEntity<String> deleteListItem(@RequestParam("id") Long itemId) {
    shoppingListService.deleteItem(itemId);
    return new ResponseEntity<>("Item deleted successfully.", HttpStatus.OK);
  }

  @GetMapping("/item")
  public ResponseEntity<ShoppingListItemDto> getListItem(@RequestParam("id") Long itemId) {
    ShoppingListItemDto response = shoppingListService.getItem(itemId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/search")
  public ResponseEntity<ShoppingListSearchResponseDto> search(@RequestBody() ShoppingListSearchRequestDto request) {
    ShoppingListSearchResponseDto response = shoppingListService.search(request);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
