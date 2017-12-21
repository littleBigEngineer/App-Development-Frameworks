package com.kylar.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kylar.Domain.Player;

public class PlayerMapper implements RowMapper<Player>{

	@Override
	public Player mapRow(ResultSet rs, int i) throws SQLException {
		return new Player(rs.getString("firstName"), rs.getString("lastName"), 
				rs.getString("ign"), rs.getInt("balance"), rs.getInt("health"));
	}

}
