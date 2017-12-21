package com.kylar.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.kylar.Domain.ShopItem;

public class ShopItemMapper implements RowMapper<ShopItem> {
	@Override
	public ShopItem mapRow(ResultSet rs, int i) throws SQLException {
		return new ShopItem(rs.getString("item"),rs.getInt("protection"), rs.getInt("damage"), 
				rs.getInt("durability"), rs.getInt("currentLevel"), rs.getInt("priceForUpgrade"), rs.getInt("beSold"), 
				rs.getInt("salePrice"), rs.getInt("stock"), rs.getInt("price"));
	}
}
