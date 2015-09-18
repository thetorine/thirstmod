package com.thetorine.thirstmod.core.player;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerContainer {
	public static final Map<String, PlayerContainer> ALL_PLAYERS = new HashMap<String, PlayerContainer>();

	public EntityPlayer player;
	public ThirstLogic stats;

	public PlayerContainer(EntityPlayer player, ThirstLogic stats) {
		this.player = player;
		this.stats = stats;
	}

	public static void addPlayer(EntityPlayer player) {
		if (!ALL_PLAYERS.containsKey(player.getDisplayNameString())) {
			PlayerContainer container = new PlayerContainer(player, new ThirstLogic(player));
			ALL_PLAYERS.put(player.getDisplayNameString(), container);
		}
	}
	
	public static void respawnPlayer(EntityPlayer newPlayer) {
		ALL_PLAYERS.remove(newPlayer.getDisplayNameString());
		addPlayer(newPlayer);
	}

	public static PlayerContainer getPlayer(EntityPlayer player) {
		return ALL_PLAYERS.get(player.getDisplayNameString());
	}
	
	public static PlayerContainer getPlayer(String username) {
		return ALL_PLAYERS.get(username);
	}
	
	public EntityPlayer getContainerPlayer() {
		return player;
	}

	public ThirstLogic getStats() {
		return stats;
	}

	public void addStats(int level, float saturation) {
		getStats().addStats(level, saturation);
	}

	public void addExhaustion(float f) {
		getStats().addExhaustion(f);
	}
}