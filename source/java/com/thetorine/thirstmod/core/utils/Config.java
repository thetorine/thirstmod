/**
 * Configuration Class. Holds all the variables that can be changed via a text file.
 */
package com.thetorine.thirstmod.core.utils;

import java.io.File;

import com.thetorine.thirstmod.core.main.ThirstMod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {
	private Configuration config = new Configuration(new File(ThirstMod.getMinecraftDir(), "/config/thirstmod.txt"));

	public boolean POISON_ON = setupConfig();
	public boolean PEACEFUL_ON;
	public boolean METER_ON_LEFT;
	public boolean DEATH_FROM_THIRST;

	public float NIGHT_RATE;
	public float IN_WATER_RATE;
	public float DESERT_RATE;
	public float WALKING_RATE;
	public float RUNNING_RATE;
	public float JUMP_RATE;

	private boolean setupConfig() {
		config.load();

		POISON_ON = Boolean.parseBoolean(get("Poisoning On", "General", true, "Allows you to turn the poison off or on.").getString());
		PEACEFUL_ON = Boolean.parseBoolean(get("Peaceful On", "General", false, "Allows the thirst bar to dehydrate on peaceful mode.").getString());
		METER_ON_LEFT = Boolean.parseBoolean(get("Meter on leftside", "General", false, "Moves the ThirstBar to be above the health bar.").getString());
		DEATH_FROM_THIRST = Boolean.parseBoolean(get("Death from Thirst", "General", false, "Allows you to determine whether death from thirst is possible. On HARD, the player will die regardless!").getString());

		NIGHT_RATE = Float.parseFloat(get("Night", "Exhaustion Rates", 0.9d, "How fast the player is exhausted at night. 1 is daytime speed.").getString());
		DESERT_RATE = Float.parseFloat(get("Desert", "Exhaustion Rates", 2d, "How fast the player is exhausted in the desert biome. 2 is 2x times faster.").getString());
		IN_WATER_RATE = Float.parseFloat(get("Water", "Exhaustion Rates", 0.03d, "How fast the player is exhausted when swimming.").getString());
		WALKING_RATE = Float.parseFloat(get("Walking", "Exhaustion Rates", 0.01d, "How fast the player is exhausted when walking.").getString());
		RUNNING_RATE = Float.parseFloat(get("Running", "Exhaustion Rates", 0.1d, "How fast the player is exhausted when sprinting.").getString());
		JUMP_RATE = Float.parseFloat(get("Jumping", "Exhaustion Rates", 0.03d, "How fast the player is exhausted when jumping. Sprint Jump is this multiplied by 2").getString());

		config.save();
		return POISON_ON;
	}

	public Property get(String key, String category, Object defaultValue) {
		if (defaultValue instanceof Integer) {
			return config.get(category, key, (Integer) defaultValue);
		} else if (defaultValue instanceof Boolean) { return config.get(category, key, (Boolean) defaultValue); }
		return null;
	}

	public Property get(String key, String category, Object defaultValue, String comment) {
		if (defaultValue instanceof Integer) {
			return config.get(category, key, (Integer) defaultValue, comment);
		} else if (defaultValue instanceof Boolean) {
			return config.get(category, key, (Boolean) defaultValue, comment);
		} else if (defaultValue instanceof Float) { 
			return config.get(category, key, (Float) defaultValue, comment); 
		} else if(defaultValue instanceof Double) {
			return config.get(category, key, (Double) defaultValue, comment);
		}
		return null;
	}
}
