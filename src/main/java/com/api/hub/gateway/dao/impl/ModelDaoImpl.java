package com.api.hub.gateway.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.api.hub.gateway.dao.ModelDao;
import com.api.hub.gateway.model.Model;

@Repository
public class ModelDaoImpl implements ModelDao{
	
	@Autowired
    private JdbcTemplate jdbcTemplate;

	@Override
	public List<Model> get(){
		
		return jdbcTemplate.query("select * from models", new ModelRowMapper());
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
