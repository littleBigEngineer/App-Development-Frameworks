package com.kylar.Repo;

import com.kylar.Domain.Player;

public interface PlayerRepo {	
	Player getPlayer();
	void updatePlayerInfo(Player player);
	int[] endOfRoundInfo(Player player);
	void addPlayer(Player player);
}
