package com.thetorine.thirstmod.common.logic;

import com.thetorine.thirstmod.Constants;
import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.client.gui.GuiThirstBar;
import com.thetorine.thirstmod.common.blocks.TileEntityRainCollector;
import com.thetorine.thirstmod.common.items.Drink;
import com.thetorine.thirstmod.network.NetworkManager;
import com.thetorine.thirstmod.network.PacketMovementSpeed;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.*;

import static com.thetorine.thirstmod.ThirstMod.gsonInstance;

public class EventHook {

    private static EventHook instance = new EventHook();

    public static EventHook getInstance() {
        return instance;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent event) {
        GuiThirstBar.onRenderGameOverlayEvent(event);
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
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(ThirstMod.getProxy().RAIN_COLLECTOR);
        GameRegistry.registerTileEntity(TileEntityRainCollector.class, ThirstMod.getProxy().RAIN_COLLECTOR.getRegistryName().toString());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        final Item items[] = {
            ThirstMod.getProxy().DRINKS,
            ThirstMod.getProxy().CANTEEN,
            ThirstMod.getProxy().FILTER,
            ThirstMod.getProxy().CHARCOAL_FILTER,
            ThirstMod.getProxy().DIRTY_FILTER,
            new ItemBlock(ThirstMod.getProxy().RAIN_COLLECTOR).setRegistryName(Constants.MOD_ID, "rain_collector")
        };

        event.getRegistry().registerAll(items);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        final Item items[] = {
                ThirstMod.getProxy().DRINKS,
                ThirstMod.getProxy().CANTEEN,
                ThirstMod.getProxy().FILTER,
                ThirstMod.getProxy().CHARCOAL_FILTER,
                ThirstMod.getProxy().DIRTY_FILTER,
                Item.getItemFromBlock(ThirstMod.getProxy().RAIN_COLLECTOR)
        };

        for (Item item : items) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }

        ModelLoader.setCustomModelResourceLocation(items[1], 0, new ModelResourceLocation(items[1].getRegistryName(), "inventory"));
        for (int i = 0; i < Drink.ALL_DRINKS.size(); i++) {
            ModelLoader.setCustomModelResourceLocation(items[0], i, new ModelResourceLocation(items[0].getRegistryName(), "inventory"));
            for (int j = 0; j < Constants.CANTEEN_CAPACITY; j++) {
                ModelLoader.setCustomModelResourceLocation(items[1], i*Constants.CANTEEN_CAPACITY + j + 1, new ModelResourceLocation(items[1].getRegistryName(), "inventory"));
            }
        }
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
                    if (stats == null) {
                        ThirstMod.getProxy().registerPlayer(player, new ThirstStats());
                    } else {
                        ThirstMod.getProxy().registerPlayer(player, stats);
                    }
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
