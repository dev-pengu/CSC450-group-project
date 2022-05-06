import auth from './modules/auth';
import calendar from './modules/calendar';
import family from './modules/family';
import poll from './modules/poll';
import shopping from './modules/shopping';
import todo from './modules/todo';
import user from './modules/user';
import utility from './modules/utility';

export default {
  ...auth,
  ...calendar,
  ...family,
  ...poll,
  ...shopping,
  ...todo,
  ...user,
  ...utility,
};
