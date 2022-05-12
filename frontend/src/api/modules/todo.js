import { http } from './clients';

export default {
  createTodoList(req) {
    return http.post('/todo', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateTodoList(req) {
    return http.patch('/todo', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteTodoList(id) {
    return http.delete('/todo', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  getTodoList(id) {
    return http.get('/todo', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  getTask(id) {
    return http.get('/todo/task', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  addTask(req) {
    return http.post('/todo/task', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateTask(req) {
    return http.patch('/todo/task', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteTask(id) {
    return http.delete('/todo/task', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  searchTodoLists(req) {
    return http.post('/todo/search', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  getToDoLists(familyId) {
    return http.get('/todo/lists', {
      params: { familyId },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
};
