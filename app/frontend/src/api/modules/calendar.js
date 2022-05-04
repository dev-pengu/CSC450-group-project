import { http } from "./clients";

export default {
  searchCalendars(req) {
    return http.post('/calendar/search', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteEvent(id, isRecurring) {
    return http.delete('/calendar/event', {
      params: { id, recurring: isRecurring },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  getCalendarsForSelect() {
    return http.get('/calendar');
  },
  getPotentialAssignees(id) {
    return http.get('/calendar/assignees', {
      params: { id },
    });
  },
  addCalendarEvent(req) {
    return http.post('/calendar/event', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateCalendarEvent(req) {
    return http.patch('/calendar/event', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  createCalendar(req) {
    return http.post('/calendar', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  updateCalendar(req) {
    return http.patch('/calendar', req, {
      validateStatus(status) {
        return status < 500;
      },
    });
  },
  deleteCalendar(id) {
    return http.delete('/calendar', {
      params: { id },
      validateStatus(status) {
        return status < 500;
      },
    });
  },
}
