package com.familyorg.familyorganizationapp.utility;

import com.familyorg.familyorganizationapp.domain.Calendar;
import com.familyorg.familyorganizationapp.domain.CalendarEvent;
import com.familyorg.familyorganizationapp.domain.EventRepetitionSchedule;
import com.familyorg.familyorganizationapp.domain.Family;
import com.familyorg.familyorganizationapp.domain.FamilyMembers;
import com.familyorg.familyorganizationapp.domain.MemberInvite;
import com.familyorg.familyorganizationapp.domain.PasswordResetCode;
import com.familyorg.familyorganizationapp.domain.Poll;
import com.familyorg.familyorganizationapp.domain.PollOption;
import com.familyorg.familyorganizationapp.domain.PollVote;
import com.familyorg.familyorganizationapp.domain.RecurringCalendarEvent;
import com.familyorg.familyorganizationapp.domain.ShoppingList;
import com.familyorg.familyorganizationapp.domain.ShoppingListItem;
import com.familyorg.familyorganizationapp.domain.ToDoList;
import com.familyorg.familyorganizationapp.domain.ToDoTask;
import com.familyorg.familyorganizationapp.domain.User;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {
    public static SessionFactory getSessionFactory() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        SessionFactory factory = new MetadataSources(registry)
                .buildMetadata().buildSessionFactory();
        return factory;
    }

    public static SessionFactory getIntegrationSessionFactory() {
        SessionFactory sessionFactory = null;

        Map<String, Object> settings = new HashMap<>();
        settings.put("hibernate.connection.driver_class", "org.h2.Driver");
        settings.put("hibernate.connection.url", "jdbc:h2:mem:test");
        settings.put("hibernate.connection.username", "sa");
        settings.put("hibernate.connection.password", "");
        settings.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        settings.put("hibernate.current_session_context_class", "thread");
        settings.put("hibernate.show_sql", "false");
        settings.put("hibernate.format_sql", "false");
        settings.put("hibernate.hbm2ddl.auto", "create-drop");

        try {
            ServiceRegistry standardRegistry = new StandardServiceRegistryBuilder()
                .applySettings(settings).build();

            Metadata metadata = new MetadataSources(standardRegistry)
                .addAnnotatedClass(User.class)
                .addAnnotatedClass(Family.class)
                .addAnnotatedClass(FamilyMembers.class)
                .addAnnotatedClass(Calendar.class)
                .addAnnotatedClass(CalendarEvent.class)
                .addAnnotatedClass(EventRepetitionSchedule.class)
                .addAnnotatedClass(MemberInvite.class)
                .addAnnotatedClass(PasswordResetCode.class)
                .addAnnotatedClass(Poll.class)
                .addAnnotatedClass(PollOption.class)
                .addAnnotatedClass(PollVote.class)
                .addAnnotatedClass(RecurringCalendarEvent.class)
                .addAnnotatedClass(ShoppingList.class)
                .addAnnotatedClass(ShoppingListItem.class)
                .addAnnotatedClass(ToDoList.class)
                .addAnnotatedClass(ToDoTask.class)
                .getMetadataBuilder()
                .build();

            sessionFactory = metadata.getSessionFactoryBuilder().build();
            return sessionFactory;
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
}
