package com.api.hub.gateway.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.api.hub.gateway.dao.ModelMetricDao;
import com.api.hub.gateway.model.ModelMetric;

@Repository
@ConditionalOnProperty(name = "sql.model.metrics.enable", havingValue = "true")
public class ModelMetricDaoImpl implements ModelMetricDao {

	@Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String INSERT_SQL = """
        INSERT INTO model_metrics (
            entry_date, model_id,
            current_input_token_day, current_input_token_month,
            current_output_token_day, current_output_token_month,
            request_day, request_month,
            total_failures_today, failure_interval
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    		""";
    private static final String SELECT_SQL = """
    		SELECT *
		FROM model_metrics m
		WHERE (m.model_id, m.entry_date) IN (
		    SELECT model_id, MAX(entry_date)
		    FROM model_metrics
		    GROUP BY model_id
		)""";
	@Override
	public void save(ModelMetric metric) {
		jdbcTemplate.update(INSERT_SQL,
                metric.getCurrentDate().get(),
                metric.getModelId(),
                metric.getCurrentInputTokenConsumedPerDay().get(),
                metric.getCurrentInputTokenConsumedPerMonth().get(),
                metric.getCurrentOutputTokenConsumedPerDay().get(),
                metric.getCurrentOutputTokenConsumedPerMonth().get(),
                metric.getRequestPerDay().get(),
                metric.getRequestPerMonth().get(),
                metric.getTotalFailuresToday().get(),
                metric.getFailureInterval().get()
        );
		
	}

	@Override
	public List<ModelMetric> get() {
		return jdbcTemplate.query(
				SELECT_SQL,
                new ModelMetricRowMapper()
        );
	}

	static class ModelMetricRowMapper implements RowMapper<ModelMetric> {
        @Override
        public ModelMetric mapRow(ResultSet rs, int rowNum) throws SQLException {
        	
	            ModelMetric metric = new ModelMetric();
	            
	            Timestamp timestamp = rs.getTimestamp("entry_date");
	            Date utilDate = new Date(timestamp.getTime());  
	            metric.setCurrentDate(utilDate);
	            metric.setModelId(rs.getString("model_id"));
	
	            metric.setCurrentInputTokenConsumedPerDay(rs.getLong("current_input_token_day"));
	            metric.setCurrentInputTokenConsumedPerMonth(rs.getLong("current_input_token_month"));
	            metric.setCurrentOutputTokenConsumedPerDay(rs.getLong("current_output_token_day"));
	            metric.setCurrentOutputTokenConsumedPerMonth(rs.getLong("current_output_token_month"));
	            metric.setRequestPerDay(rs.getLong("request_day"));
	            metric.setRequestPerMonth(rs.getLong("request_month"));
	            metric.setTotalFailuresToday(rs.getLong("total_failures_today"));
	            metric.setFailureInterval(rs.getLong("failure_interval"));
	            
            return metric;
        }
	}
}
