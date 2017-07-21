package com.thetorine.thirstmod.common;

/*
    Author: tarun1998 (http://www.minecraftforum.net/members/tarun1998)
    Date: 21/07/2017
    Handles all events for this mod.
 */

import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.network.NetworkManager;
import com.thetorine.thirstmod.network.PacketMovementSpeed;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.*;

import static com.thetorine.thirstmod.ThirstMod.gsonInstance;

public class EventHook {

    private static EventHook instance = new EventHook();

    public static EventHook getInstance() {
        return instance;
    }

    public static ResourceLocation THIRST_BAR_ICONS = new ResourceLocation("thirstmod:textures/gui/thirstbar.png");

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
            ScaledResolution scaledRes = event.getResolution();

            Minecraft.getMinecraft().getTextureManager().bindTexture(THIRST_BAR_ICONS);

            if (!Minecraft.getMinecraft().player.isRidingHorse() && ThirstMod.getClientProxy().clientStats != null) {
                int thirstLevel = ThirstMod.getClientProxy().clientStats.thirstLevel;
                int xStart = scaledRes.getScaledWidth()/2 + 10;
                int yStart = scaledRes.getScaledHeight() - 49;

                for (int i = 0; i < 10; i++) {
                    gui.drawTexturedModalRect(xStart + i*8, yStart, 1, 1, 7, 9); //empty thirst droplet
                    if (thirstLevel % 2 != 0 && 10 - i - 1 == thirstLevel/2) {
                        gui.drawTexturedModalRect(xStart + i*8, yStart, 17, 1, 7, 9); //half full thirst droplet
                    } else if (thirstLevel/2 >= 10 - i) {
                        gui.drawTexturedModalRect(xStart + i*8, yStart, 9, 1, 7, 9); //full thirst droplet
                    }
                }
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.AIR) {
            event.setCanceled(true);

            GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
            ScaledResolution scaledRes = event.getResolution();

            int xStart = (scaledRes.getScaledWidth() / 2) + 91;
            int yStart = scaledRes.getScaledHeight() - 49;
            int xModifier = 0;
            int yModifier = 0;

            int armorLevel = ForgeHooks.getTotalArmorValue(Minecraft.getMinecraft().player);

            if (!Minecraft.getMinecraft().player.isRidingHorse()) {
                if (armorLevel > 0) {
                    yModifier = -10;
                } else {
                    xModifier = -101;
                }
            }

            if (Minecraft.getMinecraft().player.isInsideOfMaterial(Material.WATER)) {
                int air = Minecraft.getMinecraft().player.getAir();
                int full = MathHelper.ceil(((air - 2) * 10.0D) / 300.0D);
                int partial = MathHelper.ceil((air * 10.0D) / 300.0D) - full;

                for (int i = 0; i < (full + partial); ++i) {
                    gui.drawTexturedModalRect((xStart - (i * 8) - 9) + xModifier, yStart + yModifier, (i < full ? 16 : 25), 18, 9, 9);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if(!event.player.world.isRemote) {
            ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(event.player.getUniqueID());
            if(stats != null) {
                stats.update(event.player);
            }
        } else {
            NetworkManager.getNetworkWrapper().sendToServer(new PacketMovementSpeed(event.player, ThirstMod.getClientProxy().clientStats));
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        Items.registerDrinkItems(event);
    }

    @SubscribeEvent
    public void onAttack(AttackEntityEvent attack) {
        if (!attack.getEntityPlayer().world.isRemote) {
            ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(attack.getEntityPlayer().getUniqueID());
            stats.addExhaustion(0.5f);
        }
        attack.setResult(Event.Result.DEFAULT);
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent hurt) {
        if (hurt.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) hurt.getEntity();
            if (!player.world.isRemote) {
                ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
                stats.addExhaustion(0.4f);
            }
        }
        hurt.setResult(Event.Result.DEFAULT);
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if(player != null) {
            if(!player.world.isRemote) {
                ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
                stats.addExhaustion(0.03f);
            }
        }
        event.setResult(Event.Result.DEFAULT);
    }

    @SubscribeEvent
    public void playedCloned(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        if(!event.getEntityPlayer().world.isRemote) {
            if(event.isWasDeath()) {
                ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(event.getEntityPlayer().getUniqueID());
                stats.resetStats();
            }
        }
    }

    @SubscribeEvent
    public void onLoadPlayerData(PlayerEvent.LoadFromFile event) {
        if (!event.getEntityPlayer().world.isRemote) {
            EntityPlayer player = event.getEntityPlayer();
            File saveFile = event.getPlayerFile("thirstmod");
            if(!saveFile.exists()) {
                ThirstMod.getProxy().registerPlayer(player, new ThirstStats());
            } else {
                try {
                    FileReader reader = new FileReader(saveFile);
                    ThirstStats stats = gsonInstance.fromJson(reader, ThirstStats.class);
                    ThirstMod.getProxy().registerPlayer(player, stats);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SubscribeEvent
    public void onSavePlayerData(PlayerEvent.SaveToFile event) {
        if (!event.getEntityPlayer().world.isRemote) {
            ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(event.getEntityPlayer().getUniqueID());
            File saveFile = new File(event.getPlayerDirectory(), event.getPlayerUUID() + ".thirstmod");
            try {
                String write = gsonInstance.toJson(stats);
                saveFile.createNewFile();
                BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
                writer.write(write);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
