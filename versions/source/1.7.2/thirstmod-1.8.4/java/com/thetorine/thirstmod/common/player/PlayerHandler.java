package com.thetorine.thirstmod.common.player;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerHandler {
	public static final Map<String, PlayerHandler> ALL_PLAYERS = new HashMap<String, PlayerHandler>();

	public EntityPlayer player;
	public ThirstStats stats;

	public PlayerHandler(EntityPlayer player, ThirstStats stats) {
		this.player = player;
		this.stats = stats;
	}

	public static void addPlayer(String username, PlayerHandler player) {
		if (!ALL_PLAYERS.containsKey(username)) {
			ALL_PLAYERS.put(username, player);
		}
	}

	public static PlayerHandler getPlayer(String username) {
		return ALL_PLAYERS.get(username);
	}

	/**
	 * Gets the ThirstStats.class instance.
	 * 
	 * @return the ThirstStats.class instance.
	 */
	public ThirstStats getStats() {
		return stats;
	}

	/**
	 * Sets all the data in ThirstStats to their original values.
	 */
	public void setDefaults() {
		getStats().level = 20;
		getStats().exhaustion = 0f;
		getStats().saturation = 5f;
		getStats().timer = 0;
		getStats().getPoison().setPoisonedTo(false);
		getStats().getPoison().setPoisonTime(0);
	}

	/**
	 * Sets player stats to the arguments.
	 * 
	 * @param level
	 * @param saturation
	 */
	public void setStats(int level, float saturation) {
		getStats().level = level;
		getStats().saturation = saturation;
	}

	/**
	 * Adds exhaustion to the player.
	 * 
	 * @param f
	 *            amount of exhaustion to add.
	 */
	public void addExhaustion(float f) {
		getStats().addExhaustion(f);
	}
}
