package com.api.hub.gateway.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.api.hub.gateway.dao.ModelDao;
import com.api.hub.gateway.model.Model;

@Repository
@ConditionalOnProperty(name = "sql.model.metadata.enable", havingValue = "true")
public class ModelDaoImpl implements ModelDao{
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

	private static final String INSERT_QUERY = """
            INSERT INTO models (
                model_id,
                provider,
                model_name,
                type,
                model_rank,
                max_token_day,
                max_token_month,
                max_request_day,
                max_request_month,
                enable,
                topics
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
            provider = VALUES(provider),
            model_name = VALUES(model_name),
            type = VALUES(type),
            model_rank = VALUES(model_rank),
            max_token_day = VALUES(max_token_day),
            max_token_month = VALUES(max_token_month),
            max_request_day = VALUES(max_request_day),
            max_request_month = VALUES(max_request_month),
            enable = VALUES(enable),
            topics = VALUES(topics)
        """;
	
	@Override
	public List<Model> get(){
		
		return jdbcTemplate.query("select * from models", new ModelRowMapper());
	}
	
	public void save(Model model) {
		jdbcTemplate.update(INSERT_QUERY,
	            model.getModelId(),
	            model.getProvider(),
	            model.getModelName(),
	            model.getType(),
	            model.getRank(),
	            model.getMaxTokenDay(),
	            model.getMaxTokenMonth(),
	            model.getMaxRequestDay(),
	            model.getMaxRequestMonth(),
	            model.isEnable(),
	            String.join(",", model.getTopics())  // Convert list to CSV
	        );
	}
	
	static class ModelRowMapper implements RowMapper<Model> {
        @Override
        public Model mapRow(ResultSet rs, int rowNum) throws SQLException {
        	
    		Model model = new Model();
            model.setModelId(rs.getString("model_id"));
            model.setProvider(rs.getString("provider"));
            model.setModelName(rs.getString("model_name"));
            model.setType(rs.getString("type"));
            model.setRank(rs.getFloat("model_rank"));

            model.setMaxTokenDay(rs.getLong("max_token_day"));
            model.setMaxTokenMonth(rs.getLong("max_token_month"));
            model.setMaxRequestDay(rs.getLong("max_request_day"));
            model.setMaxRequestMonth(rs.getLong("max_request_month"));
            model.setEnable(rs.getBoolean("enable"));

            model.setTopicsFromString(rs.getString("topics"));
               
            return model;
        }
	}

}
