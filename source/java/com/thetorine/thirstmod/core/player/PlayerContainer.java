package com.thetorine.thirstmod.core.player;

import java.util.HashMap;
import java.util.Map;

import com.thetorine.thirstmod.core.utils.Constants;

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
		if (!ALL_PLAYERS.containsKey(player.getDisplayName())) {
			PlayerContainer container = new PlayerContainer(player, new ThirstLogic(player));
			ALL_PLAYERS.put(player.getDisplayName(), container);
		}
	}
	
	public void respawnPlayer() {
		getStats().thirstLevel = Constants.MAX_LEVEL;
		getStats().thirstExhaustion = 0f;
		getStats().thirstSaturation = Constants.MAX_SATURATION;
		getStats().timer = 0;
		getStats().poisonLogic.setPoisonedTo(false);
		getStats().poisonLogic.setPoisonTime(0);
	}

	public static PlayerContainer getPlayer(String username) {
		return ALL_PLAYERS.get(username);
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