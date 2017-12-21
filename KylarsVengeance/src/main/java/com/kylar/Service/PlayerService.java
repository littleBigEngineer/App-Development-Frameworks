package com.kylar.Service;

import com.kylar.Domain.Player;

public interface PlayerService {
	Player getPlayer();
	void updatePlayerInfo(Player player);
	int[] endOfRoundInfo(Player player);
	void addPlayer(Player player);
}
