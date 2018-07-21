package com.thetorine.thirstmod.common.logic;

import com.thetorine.thirstmod.Constants;
import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.client.gui.GuiThirstBar;
import com.thetorine.thirstmod.common.blocks.TileEntityDrinksBrewer;
import com.thetorine.thirstmod.common.blocks.TileEntityDrinksStore;
import com.thetorine.thirstmod.common.blocks.TileEntityRainCollector;
import com.thetorine.thirstmod.common.content.DrinkItem;
import com.thetorine.thirstmod.common.content.ExternalDrink;
import com.thetorine.thirstmod.common.content.Drink;
import com.thetorine.thirstmod.network.NetworkManager;
import com.thetorine.thirstmod.network.PacketMovementSpeed;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
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
        event.getRegistry().register(ThirstMod.getProxy().DRINKS_BREWER);
        event.getRegistry().register(ThirstMod.getProxy().DRINKS_STORE);

        GameRegistry.registerTileEntity(TileEntityRainCollector.class, ThirstMod.getProxy().RAIN_COLLECTOR.getRegistryName().toString());
        GameRegistry.registerTileEntity(TileEntityDrinksBrewer.class, ThirstMod.getProxy().DRINKS_BREWER.getRegistryName().toString());
        GameRegistry.registerTileEntity(TileEntityDrinksStore.class, ThirstMod.getProxy().DRINKS_STORE.getRegistryName().toString());
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        final Item items[] = {
            ThirstMod.getProxy().DRINKS,
            ThirstMod.getProxy().CANTEEN,
            ThirstMod.getProxy().CUP,
            ThirstMod.getProxy().FILTER,
            ThirstMod.getProxy().CHARCOAL_FILTER,
            ThirstMod.getProxy().DIRTY_FILTER,
            new ItemBlock(ThirstMod.getProxy().RAIN_COLLECTOR).setRegistryName(Constants.MOD_ID, "rain_collector"),
            new ItemBlock(ThirstMod.getProxy().DRINKS_BREWER).setRegistryName(Constants.MOD_ID, "drinks_brewer"),
            new ItemBlock(ThirstMod.getProxy().DRINKS_STORE).setRegistryName(Constants.MOD_ID, "drinks_store")
        };

        event.getRegistry().registerAll(items);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void registerModels(ModelRegistryEvent event) {
        final Item items[] = {
                ThirstMod.getProxy().DRINKS,
                ThirstMod.getProxy().CANTEEN,
                ThirstMod.getProxy().CUP,
                ThirstMod.getProxy().FILTER,
                ThirstMod.getProxy().CHARCOAL_FILTER,
                ThirstMod.getProxy().DIRTY_FILTER,
                Item.getItemFromBlock(ThirstMod.getProxy().RAIN_COLLECTOR),
                Item.getItemFromBlock(ThirstMod.getProxy().DRINKS_BREWER),
                Item.getItemFromBlock(ThirstMod.getProxy().DRINKS_STORE)
        };

        for (Item item : items) {
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }

        for (int i = 0; i < Drink.ALL_DRINKS.size(); i++) {
            ModelLoader.setCustomModelResourceLocation(items[0], i, new ModelResourceLocation(items[0].getRegistryName(), "inventory"));
            ModelLoader.setCustomModelResourceLocation(items[2], i + 1, new ModelResourceLocation(items[2].getRegistryName(), "inventory"));
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

    @SubscribeEvent
    public void onFinishUsingItem(LivingEntityUseItemEvent.Finish event) {
        if (!event.getEntity().world.isRemote && event.getEntityLiving() instanceof EntityPlayer) {
            ItemStack eventItem = event.getItem();
            // have to increment count because if count == 0, then ItemAir is returned instead of the item that was just consumed.
            eventItem.setCount(eventItem.getCount() + 1);
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            for (DrinkItem drinkItem : ExternalDrink.EXTERNAL_DRINKS) {
                Item item = Item.getByNameOrId(drinkItem.name);
                if (eventItem.getItem().equals(item) && (drinkItem.metadata == -1 || eventItem.getMetadata() == drinkItem.metadata)) {
                    ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
                    stats.addStats(drinkItem.thirstReplenish, drinkItem.saturationReplenish);
                    stats.attemptToPoison(drinkItem.poisonChance);
                }
            }
            eventItem.setCount(eventItem.getCount() - 1);
        }
    }
}
