/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

INSERT INTO users (user_id, dark_mode, is_locked, login_attempts, timezone, username, email, first_name, last_name, password)
VALUES(1, 'f', 'f', 0, 'US/Central', 'testuser', 'testuser@test.com', 'test', 'user', '$2a$10$KBYfgR9LPgNQVk5WMLHaY.IXomFAN63n.td8nORiOaYUfxXylpwEy');
INSERT INTO users (user_id, dark_mode, is_locked, login_attempts, timezone, username, email, first_name, last_name, password)
VALUES(2, 'f', 'f', 0, 'US/Central', 'testchild', 'testchild@test.com', 'test', 'child', '$2a$10$KBYfgR9LPgNQVk5WMLHaY.IXomFAN63n.td8nORiOaYUfxXylpwEy');
INSERT INTO users (user_id, dark_mode, is_locked, login_attempts, timezone, username, email, first_name, last_name, password)
VALUES (3, 'f', 'f', 0, 'US/Central', 'testadmin', 'testadmin@test.com', 'test', 'admin', '$2a$10$KBYfgR9LPgNQVk5WMLHaY.IXomFAN63n.td8nORiOaYUfxXylpwEy');
INSERT INTO users (user_id, dark_mode, is_locked, login_attempts, timezone, username, email, first_name, last_name, password)
VALUES (4, 'f', 'f', 0, 'US/Central', 'testadult', 'testadult@test.com', 'test', 'adult', '$2a$10$KBYfgR9LPgNQVk5WMLHaY.IXomFAN63n.td8nORiOaYUfxXylpwEy');
INSERT INTO users (user_id, dark_mode, is_locked, login_attempts, timezone, username, email, first_name, last_name, password)
VALUES(5, 'f', 'f', 0, 'US/Central', 'testuser5', 'testuser5@test.com', 'test', 'user5', '$2a$10$KBYfgR9LPgNQVk5WMLHaY.IXomFAN63n.td8nORiOaYUfxXylpwEy');
INSERT INTO users (user_id, dark_mode, is_locked, login_attempts, timezone, username, email, first_name, last_name, password)
VALUES(6, 'f', 'f', 0, 'US/Central', 'testuser6', 'testuser6@test.com', 'test', 'user6', '$2a$10$KBYfgR9LPgNQVk5WMLHaY.IXomFAN63n.td8nORiOaYUfxXylpwEy');

INSERT INTO family (family_id, event_color, name, timezone, invite_code)
VALUES(1, 'ff0000', 'Test Family', 'US/Central', '5794be52-c089-444b-b84c-7691299986b0');
INSERT INTO family (family_id, event_color, name, timezone)
VALUES(2, 'ff0000', 'Test Family 2', 'US/Central');
INSERT INTO family (family_id, event_color, name, timezone)
VALUES(3, 'ff0000', 'Test Family 3', 'US/Central');
INSERT INTO family (family_id, event_color, name, timezone)
VALUES(4, 'ff0000', 'Test Family 3', 'US/Central');

INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(3, 1, 1, '00ff00');
INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(0, 2, 1, '00ff00');
INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(2, 3, 1, '00ff00');
INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(1, 4, 1, '00ff00');
INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(3, 1, 2, '00ff00');
INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(0, 2, 2, '00ff00');
INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(0, 3, 2, '00ff00');
INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(0, 5, 2, '00ff00');
INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(3, 1, 3, '00ff00');
INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(2, 4, 3, '00ff00');
INSERT INTO family_members (role, user_id, family_id, event_color)
VALUES(3, 1, 4, '00ff00');

INSERT INTO password_reset_code (password_reset_code_id, created, expired, reset_code, user_id)
VALUES (1, CURRENT_TIMESTAMP, 'f', '8f82148c-612c-40d5-af56-fd353a9c08a8', 3);
INSERT INTO password_reset_code (password_reset_code_id, created, expired, reset_code, user_id)
VALUES (2, CURRENT_TIMESTAMP, 't', '5794be52-c089-444b-b84c-7691299986b0', 1);

INSERT INTO member_invite (family_id, invite_code, user_email, created_at, initial_role)
VALUES (1, 'd8bb4a14-4bca-4258-b2f3-c124368e11fb', 'testuser6@test.com', CURRENT_TIMESTAMP, 1);
