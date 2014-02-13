package org.notificationengine.custom.jdbcselector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.DataSource;

import org.notificationengine.domain.RawNotification;
import org.notificationengine.domain.Recipient;
import org.notificationengine.domain.Subscription;
import org.notificationengine.domain.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component(value=JdbcSelectorConstants.DB_HELPER)
public class DbHelper {

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Value("${jdbc.sql.orderForSubscriptionsByTopic}")
	private String sqlOrderForSubscriptionsByTopic;
	
	@Value("${jdbc.sql.recipient.address.aliasForSubscriptionsByTopic}")
	private String recipientAddressAliasForSubscriptionsByTopic;
	
	@Value("${jdbc.sql.recipient.displayName.aliasForSubscriptionsByTopic}")
	private String recipientDisplayNameAliasForSubscriptionsByTopic;
	
	@Value("${jdbc.sql.topic.paramForSubscriptionsByTopic}")
	private String topicParamForSubscriptionsByTopic;
	
	@Value("${jdbc.sql.orderForAllSubscriptions}")
	private String sqlOrderForAllSubscriptions;
	
	@Value("${jdbc.sql.recipient.address.aliasForAllSubscriptions}")
	private String recipientAddressAliasForAllSubscriptions;
	
	@Value("${jdbc.sql.recipient.displayName.aliasForAllSubscriptions}")
	private String recipientDisplayNameAliasForAllSubscriptions;
	
	@Value("${jdbc.sql.topic.aliasForAllSubscriptions}")
	private String topicAliasForAllSubscriptions;
	
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

	public Collection<Subscription> retrieveSubscriptionsForTopic(
			final String topicName) {
		
		SqlParameterSource namedParameters = new MapSqlParameterSource(this.topicParamForSubscriptionsByTopic, topicName);

	    return this.namedParameterJdbcTemplate.query(this.sqlOrderForSubscriptionsByTopic, namedParameters, new RowMapper<Subscription>() {

			@Override
			public Subscription mapRow(ResultSet rs, int cptLine)
					throws SQLException {
				
				Topic topic = new Topic(topicName);
				
				Recipient recipient = new Recipient(rs.getString(recipientAddressAliasForSubscriptionsByTopic),
													rs.getString(recipientDisplayNameAliasForSubscriptionsByTopic));
				
				Subscription subscription = new Subscription(topic, recipient);

				return subscription;
			}	    	
		});
	}

	public Collection<Subscription> retrieveSubscriptions() {
		
	    return this.namedParameterJdbcTemplate.query(this.sqlOrderForAllSubscriptions, new RowMapper<Subscription>() {

			@Override
			public Subscription mapRow(ResultSet rs, int cptLine)
					throws SQLException {
				
				Topic topic = new Topic(rs.getString(topicAliasForAllSubscriptions));
				
				Recipient recipient = new Recipient(rs.getString(recipientAddressAliasForAllSubscriptions),
													rs.getString(recipientDisplayNameAliasForAllSubscriptions));
				
				Subscription subscription = new Subscription(topic, recipient);

				return subscription;
			}	    	
		});
	}

    public Collection<Subscription> retrieveSubscriptionsForRawNotification(RawNotification rawNotification) {

        String topicName = rawNotification.getTopic().getName();

        return this.retrieveSubscriptionsForTopic(topicName);

    }
	
	
}
