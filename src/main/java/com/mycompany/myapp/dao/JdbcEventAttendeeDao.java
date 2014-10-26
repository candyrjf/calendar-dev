package com.mycompany.myapp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mycompany.myapp.domain.CalendarUser;
import com.mycompany.myapp.domain.Event;
import com.mycompany.myapp.domain.EventAttendee;

@Repository
public class JdbcEventAttendeeDao implements EventAttendeeDao {
	private JdbcTemplate jdbcTemplate;

	private RowMapper<EventAttendee> rowMapper;

	@Autowired
	private EventDao eventDao;
	// private CalendarUserDao eventDao;

	@Autowired
	private CalendarUserDao calendarUserDao;

	// --- constructors ---
	//ResultSet의 결과를 Event 오브젝트를 만들어 프로퍼티에 넣어줘야 한다. 재사용이 가능하도록 독립
	//RowMapper는 ResultSet의 로우 하나를 매핑하기 위해 사용되기 때문에 여러번 호출될 수 있다.
	public JdbcEventAttendeeDao() {
		rowMapper = new RowMapper<EventAttendee>() {
			public EventAttendee mapRow(ResultSet rs, int rowNum)
					throws SQLException {
				EventAttendee eventAttendeeList = new EventAttendee();

				/* TODO Assignment 3 */
				eventAttendeeList.setAttendee(calendarUserDao.findUser(rs
						.getInt("attendee")));
				eventAttendeeList.setEvent(eventDao.findEvent(rs
						.getInt("event_id")));
				eventAttendeeList.setId(rs.getInt("id"));

				return eventAttendeeList;
			}
		};
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<EventAttendee> findEventAttendeeByEventId(int eventId) {
		// TODO Assignment 3
		String sql_query = "select * from events_attendees where event_id = ?";
		return this.jdbcTemplate.query(sql_query, new Object[] { eventId },
				rowMapper);
	}// 여러개의 로우가 결과로 나오는 일반적인 경우에 사용

	@Override
	public List<EventAttendee> findEventAttendeeByAttendeeId(int attendeeId) {
		// TODO Assignment 3
		String sql_query = "select * from events_attendees where attendee = ?";
		return this.jdbcTemplate.query(sql_query, new Object[] { attendeeId },
				rowMapper);
	}

	@Override
	public int createEventAttendee(final EventAttendee eventAttendee) {
		// TODO Assignment 3
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(
					Connection connection) throws SQLException {
				PreparedStatement ps = connection
						.prepareStatement(
								"insert into events_attendees(event_id, attendee) values(?,?)",
								Statement.RETURN_GENERATED_KEYS);
				// 자동으로 증가하는 키를 반환하려는지 표시, 단일 행 insert문에서만 사용 가능
				ps.setInt(1, eventAttendee.getEvent().getId());
				ps.setInt(2, eventAttendee.getAttendee().getId());
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().intValue();
	}

	@Override
	public void deleteEventAttendee(int id) {
		// TODO Assignment 3
		String sql = "delete from events_attendees where id=?";
		this.jdbcTemplate.update(sql, id);
	}

	@Override
	public void deleteAll() {
		// TODO Assignment 3
		String sql = "delete from events_attendees";
		this.jdbcTemplate.update(sql);
	}
}