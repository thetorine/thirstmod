package com.thetorine.thirstmod.core.main;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

public class EventSystem implements IGuiHandler {
	private int thirstToSet;
	
	@SubscribeEvent
	public void playerTick(PlayerTickEvent event) {
		if (event.side == Side.SERVER) {
			ThirstMod.commonProxy.serverTick(event.player);
		} else if (event.side == Side.CLIENT) {
			ThirstMod.commonProxy.clientTick(event.player);
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
		PlayerContainer.ALL_PLAYERS.remove(event.player.getDisplayNameString());
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
		PlayerContainer player = PlayerContainer.getPlayer(attack.entityPlayer);
		if ((player != null) && (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)) {
			player.addExhaustion(0.5f);
		}
	}

	@SubscribeEvent
	public void onHurt(LivingHurtEvent hurt) {
		if (hurt.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) hurt.entityLiving;
			PlayerContainer.getPlayer(player).addExhaustion(0.4f);
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
					PlayerContainer playCon = PlayerContainer.getPlayer(event.entityPlayer);
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
				PlayerContainer.respawnPlayer(event.entityPlayer);
			}
		}
	}
	
	@SubscribeEvent
	public void onSleep(PlayerSleepInBedEvent event) {
		if(event.entityPlayer.worldObj.isRemote) return;
		PlayerContainer playerContainer = PlayerContainer.getPlayer(event.entityPlayer);
		EntityPlayer player = playerContainer.getContainerPlayer();
		
		//Only run the code below if the difficulty allows it!
		if(!playerContainer.getStats().isThirstAllowedByDifficulty()) return;
		if(ThirstMod.config.DISABLE_THIRST_LOSS_FROM_SLEEP) return;
		
		int dayLength = 24000;
		int thirstInterval = 2000;
		int worldTime = (int) (event.entityPlayer.worldObj.getWorldTime() % dayLength);
		int sleepingTime = dayLength - worldTime;
		int newThirst = playerContainer.getStats().thirstLevel - (sleepingTime / thirstInterval);
		
		if(!player.worldObj.isDaytime()) {
			if (newThirst <= 8) {
				player.addChatMessage(new ChatComponentTranslation("thirstmod.toothirsty", new Object[0]));
				event.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
			} else {
				thirstToSet = newThirst;
			}
		}
	}
	
	@SubscribeEvent
	public void wakeUp(PlayerWakeUpEvent event) {
		if(ThirstMod.config.DISABLE_THIRST_LOSS_FROM_SLEEP) return;
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			PlayerContainer player = PlayerContainer.getPlayer(event.entityPlayer);
			player.stats.setStats(thirstToSet, player.stats.thirstSaturation);
			thirstToSet = 0;
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
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
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
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
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
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			PlayerContainer container = PlayerContainer.getPlayer(player);
			if(Keyboard.isKeyDown(Keyboard.KEY_B)) {
				container.addStats(-1, -1);
			} else if(Keyboard.isKeyDown(Keyboard.KEY_N)) {
				container.addStats(1, 1);
			}
		}
	}
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if(event.modID.equals(Constants.MODID)) {
			ThirstMod.config.setupConfig();
			System.out.println("sfasfafa");
		}
	}
}