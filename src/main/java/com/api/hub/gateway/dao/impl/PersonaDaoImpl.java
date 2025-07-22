package com.api.hub.gateway.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.api.hub.gateway.dao.PersonaDao;
import com.api.hub.gateway.model.PersonaProperties;

@Repository
@ConditionalOnProperty(name = "sql.persona.props.enable", havingValue = "true")
public class PersonaDaoImpl implements PersonaDao{

	private static final String SELECT_SQL = "SELECT * FROM persona_properties";
	
	private static final String INSERT_SQL = """
            INSERT INTO persona_properties (
        persona,
        chat_history_enabled,
        query_rewrite_enabled,
        rag_enabled,
        tool_call_enabled,
        rag_source,
        max_fallback_models,
        tool_choice
    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    ON DUPLICATE KEY UPDATE
        chat_history_enabled = VALUES(chat_history_enabled),
        query_rewrite_enabled = VALUES(query_rewrite_enabled),
        rag_enabled = VALUES(rag_enabled),
        tool_call_enabled = VALUES(tool_call_enabled),
        rag_source = VALUES(rag_source),
        max_fallback_models = VALUES(max_fallback_models),
        tool_choice = VALUES(tool_choice)
    """;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Override
	public int save(PersonaProperties persona) {

        return jdbcTemplate.update(INSERT_SQL,
            persona.getPersona(),
            persona.isChatHistoryEnabled(),
            persona.isQueryRewriteEnabled(),
            persona.isRagEnabled(),
            persona.isToolCallEnabled(),
            persona.getRagSource(),
            persona.getMaxFallBackModels(),
            persona.getToolChoice()
        );
    }
	
	@Override
	public List<PersonaProperties> get() {
		return jdbcTemplate.query(
				SELECT_SQL,
                new PersonaPropsRowMapper()
        );
	}

	static class PersonaPropsRowMapper implements RowMapper<PersonaProperties> {
        @Override
        public PersonaProperties mapRow(ResultSet rs, int rowNum) throws SQLException {
        	PersonaProperties props = new PersonaProperties();

            props.setPersona(rs.getString("persona"));
            props.setChatHistoryEnabled(rs.getBoolean("chat_history_enabled"));
            props.setQueryRewriteEnabled(rs.getBoolean("query_rewrite_enabled"));
            props.setRagEnabled(rs.getBoolean("rag_enabled"));
            props.setToolCallEnabled(rs.getBoolean("tool_call_enabled"));
            props.setRagSource(rs.getString("rag_source"));
            props.setMaxFallBackModels(rs.getInt("max_fallback_models"));
            props.setToolChoice(rs.getString("tool_choice"));

            return props;
        }
	}
}
