package com.mycompany.myapp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mycompany.myapp.dao.CalendarUserDao;
import com.mycompany.myapp.dao.EventAttendeeDao;
import com.mycompany.myapp.dao.EventDao;
import com.mycompany.myapp.domain.CalendarUser;
import com.mycompany.myapp.domain.Event;
import com.mycompany.myapp.domain.EventAttendee;
import com.mycompany.myapp.domain.EventLevel;

@Service
public class DefaultCalendarService implements CalendarService {
	@Autowired
	private EventDao eventDao;

	@Autowired
	private CalendarUserDao userDao;

	@Autowired
	private EventAttendeeDao eventattendeeDao;

	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Override
	public void setTransactionManager(
			PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	/* CalendarUser */
	// JdbcCalendarUserDao.java에 정의했던 메소드들을 호출한다.
	@Override
	public CalendarUser getUser(int id) {
		// TODO Assignment 3
		return userDao.findUser(id);
	}

	@Override
	public CalendarUser getUserByEmail(String email) {
		// TODO Assignment 3
		return userDao.findUserByEmail(email);
	}

	@Override
	public List<CalendarUser> getUsersByEmail(String partialEmail) {
		// TODO Assignment 3
		return userDao.findUsersByEmail(partialEmail);
	}

	@Override
	public int createUser(CalendarUser user) {
		// TODO Assignment 3
		return userDao.createUser(user);
	}

	@Override
	public void deleteAllUsers() {
		// TODO Assignment 3
		userDao.deleteAll();
	}

	/* Event */
	// JdbcEventDao.java에 정의했던 메소드들을 호출한다.
	@Override
	public Event getEvent(int eventId) {
		// TODO Assignment 3
		return eventDao.findEvent(eventId);
	}

	@Override
	public List<Event> getEventForOwner(int ownerUserId) {
		// TODO Assignment 3
		return eventDao.findForOwner(ownerUserId);
	}

	@Override
	public List<Event> getAllEvents() {
		// TODO Assignment 3
		return eventDao.findAllEvents();
	}

	@Override
	public int createEvent(Event event) {
		// TODO Assignment 3

		if (event.getEventLevel() == null) {
			event.setEventLevel(EventLevel.NORMAL);
		}
		return eventDao.createEvent(event);
	}

	@Override
	public void deleteAllEvents() {
		// TODO Assignment 3
		eventDao.deleteAll();
	}

	/* EventAttendee */
	// JdbcEventAttendeeDao.java에 정의했던 메소드들을 호출한다.
	@Override
	public List<EventAttendee> getEventAttendeeByEventId(int eventId) {
		// TODO Assignment 3
		return eventattendeeDao.findEventAttendeeByEventId(eventId);
	}

	@Override
	public List<EventAttendee> getEventAttendeeByAttendeeId(int attendeeId) {
		// TODO Assignment 3
		return eventattendeeDao.findEventAttendeeByAttendeeId(attendeeId);
	}

	@Override
	public int createEventAttendee(EventAttendee eventAttendee) {
		// TODO Assignment 3
		return eventattendeeDao.createEventAttendee(eventAttendee);
	}

	@Override
	public void deleteEventAttendee(int id) {
		// TODO Assignment 3
		eventattendeeDao.deleteEventAttendee(id);
	}

	@Override
	public void deleteAllEventAttendees() {
		// TODO Assignment 3
		eventattendeeDao.deleteAll();
	}

	/* upgradeEventLevels */
	@Override
	public void upgradeEventLevels() throws Exception {
		// TODO Assignment 3
		// 트랜잭션 관련 코딩 필요함
		TransactionStatus status = this.transactionManager
				.getTransaction(new DefaultTransactionDefinition());

		try {
			List<Event> users = eventDao.findAllEvents();
			for (Event user : users) {
				if (canUpgradeEventLevel(user)) {
					upgradeEventLevel(user);
				}
			}
			this.transactionManager.commit(status);
		} catch (RuntimeException e) {
			this.transactionManager.rollback(status);
			throw e;
		}
	}

	public static final int numLike = 10;

	@Override
	public boolean canUpgradeEventLevel(Event event) {
		// TODO Assignment 3
		// event.getNumLikes()의 숫자를 얻어와 10이 넘으면 return값을 true로 반환한다.
		EventLevel currentLevel = event.getEventLevel();
		switch (currentLevel) {
		case NORMAL:
			return (event.getNumLikes() >= numLike);
		case HOT:
			return (false);
		default:
			throw new IllegalArgumentException("Unknow Level: " + currentLevel);
		}
	}

	@Override
	public void upgradeEventLevel(Event event) {
		event.upgradeLevel();
		eventDao.udpateEvent(event);
	}
}