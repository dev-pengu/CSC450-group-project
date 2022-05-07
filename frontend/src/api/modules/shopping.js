import { http } from "./clients";

export default {
  searchShoppingLists(req) {
    return http.post('/shopping/search', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteShoppingList(id) {
    return http.delete('/shopping', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateShoppingList(req) {
    return http.patch('/shopping', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  createShoppingList(req) {
    return http.post('/shopping', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  getShoppingList(id) {
    return http.get('/shopping', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  createShoppingListItem(req) {
    return http.post('/shopping/item', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateShoppingListItem(req) {
    return http.patch('/shopping/item', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteShoppingListItem(id) {
    return http.delete('/shopping/item', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
}
