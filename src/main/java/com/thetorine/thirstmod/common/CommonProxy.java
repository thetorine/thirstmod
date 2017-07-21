package com.thetorine.thirstmod.common;

/*
    Author: tarun1998 (http://www.minecraftforum.net/members/tarun1998)
    Date: 21/07/2017
    Contains common code to server and client.
 */

import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.UUID;

public class CommonProxy {

    public HashMap<UUID, ThirstStats> loadedPlayers = new HashMap<>();

    public ThirstStats getStatsByUUID(UUID uuid) {
        ThirstStats stats = loadedPlayers.get(uuid);
        if (stats == null) {
            System.out.println("Error: Attempted to access non-existent player with UUID: " + uuid);
            return null;
        }
        return stats;
    }

    public void registerPlayer(EntityPlayer player, ThirstStats stats) {
        UUID uuid = player.getUniqueID();
        if (loadedPlayers.containsKey(uuid)) {
            // Player already loaded from previous login session where the
            // server was not closed since the players last login.
            return;
        }
        loadedPlayers.put(uuid, stats);
    }
}
