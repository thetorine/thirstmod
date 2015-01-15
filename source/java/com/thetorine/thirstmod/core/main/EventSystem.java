package com.thetorine.thirstmod.core.main;

import org.lwjgl.input.Keyboard;

import com.thetorine.thirstmod.core.client.gui.GuiDB;
import com.thetorine.thirstmod.core.client.gui.GuiDS;
import com.thetorine.thirstmod.core.client.gui.GuiRC;
import com.thetorine.thirstmod.core.client.gui.GuiRenderBar;
import com.thetorine.thirstmod.core.content.ItemLoader;
import com.thetorine.thirstmod.core.content.blocks.ContainerDB;
import com.thetorine.thirstmod.core.content.blocks.ContainerDS;
import com.thetorine.thirstmod.core.content.blocks.ContainerRC;
import com.thetorine.thirstmod.core.content.blocks.TileEntityDB;
import com.thetorine.thirstmod.core.content.blocks.TileEntityDS;
import com.thetorine.thirstmod.core.content.blocks.TileEntityRC;
import com.thetorine.thirstmod.core.content.packs.DrinkLists;
import com.thetorine.thirstmod.core.content.packs.DrinkLists.Drink;
import com.thetorine.thirstmod.core.player.PlayerContainer;
import com.thetorine.thirstmod.core.utils.Constants;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.*;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.*;

public class EventSystem implements IGuiHandler {
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		switch(event.side) {
			case SERVER: {
				ThirstMod.commonProxy.serverTick(event.player); 
				if(Constants.ECLIPSE_ENVIRONMENT) {
					debugCode(event.player);
				}
				break;
			}
			case CLIENT: {
				ThirstMod.commonProxy.clientTick(event.player);
				break;
			}
		}
	}
	
	@SubscribeEvent
	public void onLogin(PlayerLoggedInEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			PlayerContainer.addPlayer(event.player);
		}
	}
	
	@SubscribeEvent
	public void onLogout(PlayerLoggedOutEvent event) {
		PlayerContainer.ALL_PLAYERS.remove(event.player.getDisplayName());
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void renderBar(RenderGameOverlayEvent event) {
		int width = event.resolution.getScaledWidth();
	    int height = event.resolution.getScaledHeight();
		if(event.type != null) {
			switch(event.type) {
			case FOOD: {
				if (!Minecraft.getMinecraft().thePlayer.isRidingHorse()) {
					if(Constants.ECLIPSE_ENVIRONMENT) {
						GuiRenderBar.drawTemperature(width, height);
					}
					GuiRenderBar.renderThirst(width, height);
				}
				break;
			}
			case AIR: {
				event.setCanceled(true);
			    GuiRenderBar.left_height = GuiIngameForge.left_height;
			    GuiRenderBar.right_height = GuiIngameForge.right_height;
				GuiRenderBar.renderAir(width, height);
				break;
			}
			case ARMOR: {
				event.setCanceled(true);
				GuiRenderBar.renderArmor(width, height);
				break;
			}
			default: break;
		}
		}
	}
	
	@SubscribeEvent
	public void onAttack(AttackEntityEvent attack) {
		PlayerContainer player = PlayerContainer.getPlayer(attack.entityPlayer.getDisplayName());
		if ((player != null) && (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)) {
			player.addExhaustion(0.5f);
		}
	}

	@SubscribeEvent
	public void onHurt(LivingHurtEvent hurt) {
		if (hurt.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) hurt.entityLiving;
			PlayerContainer.getPlayer(player.getDisplayName()).addExhaustion(0.4f);
		}
	}

	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		if(player != null) {
			if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
				player.addExhaustion(0.03f);
			}
		}
		event.setResult(Result.DEFAULT);
	}
	
	@SubscribeEvent 
	public void onFinishUsingItem(PlayerUseItemEvent.Finish event) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			String id = event.item.getUnlocalizedName();
			for(Drink d: DrinkLists.EXTERNAL_DRINKS) {
				String possibleID = d.item.getUnlocalizedName();
				if(id.equals(possibleID) && event.item.getItemDamage() == d.item.getItemDamage()) {
					PlayerContainer playCon = PlayerContainer.getPlayer(event.entityPlayer.getDisplayName());
					playCon.addStats(d.replenish, d.saturation);
					break;
				}
			}
		}
	}
	
	@SubscribeEvent
	public void playedCloned(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			if(event.wasDeath) {
				EntityPlayer player = event.entityPlayer;
				PlayerContainer.getPlayer(player.getDisplayName()).respawnPlayer();
			}
		}
	}
	
	@SubscribeEvent
	public void onSleep(PlayerSleepInBedEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			PlayerContainer player = PlayerContainer.getPlayer(event.entityPlayer.getDisplayName());
			
			long worldTime = event.entityPlayer.worldObj.getWorldTime() % 24000;
			float sleepingTime = (float) (24000 - worldTime);
			int thirstLoss = player.getStats().thirstLevel - Math.round(sleepingTime / 2000f);
			if(player.getStats().isNight(player.player)) {
				if ((thirstLoss) <= 8) {
					player.player.addChatMessage(new ChatComponentText("You are too thirsty to sleep!"));
					event.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
				} else {
					player.stats.setStats(thirstLoss, player.stats.thirstSaturation);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
		for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
			ItemStack item = event.craftMatrix.getStackInSlot(i);
			if(item != null) {
				ItemStack filter = new ItemStack(ItemLoader.filter, 1, item.getItemDamage() + 1);
				if(item.getUnlocalizedName().equals(filter.getUnlocalizedName())) {
					if(item.getItemDamage() != 4) {
						event.player.inventory.addItemStackToInventory(filter);
					} else {
						event.player.inventory.addItemStackToInventory(new ItemStack(ItemLoader.dirty_filter, 1));
					}
				}
				
				ItemStack charcoal_filter = new ItemStack(ItemLoader.charcoal_filter, 1, item.getItemDamage() + 1);
				if(item.getUnlocalizedName().equals(charcoal_filter.getUnlocalizedName())) {
					if(item.getItemDamage() != 4) {
						event.player.inventory.addItemStackToInventory(charcoal_filter);
					}
				}
			}
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		switch(ID) {
			case Constants.DRINKS_STORE_ID: 
				return new ContainerDS(player.inventory, (TileEntityDS) tile);
			case Constants.DRINKS_BREWER_ID: 
				return new ContainerDB(player.inventory, (TileEntityDB) tile);
			case Constants.RAIN_COLLECTOR_ID:
				return new ContainerRC(player.inventory, (TileEntityRC) tile);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		switch(ID) {
			case Constants.DRINKS_STORE_ID: 
				return new GuiDS(player.inventory, (TileEntityDS) tile);
			case Constants.DRINKS_BREWER_ID: 
				return new GuiDB(player.inventory, (TileEntityDB) tile);
			case Constants.RAIN_COLLECTOR_ID:
				return new GuiRC(player.inventory, (TileEntityRC) tile);
		}
		return null;
	}
	
	public void debugCode(EntityPlayer player) {
		PlayerContainer container = PlayerContainer.getPlayer(player.getDisplayName());
		if(Keyboard.isKeyDown(Keyboard.KEY_B)) {
			container.addStats(-1, -1);
		} else if(Keyboard.isKeyDown(Keyboard.KEY_N)) {
			container.addStats(1, 1);
		}
	}
}