package com.thetorine.thirstmod.common.player;

import org.lwjgl.input.Keyboard;
import com.thetorine.thirstmod.client.gui.GuiThirst;
import com.thetorine.thirstmod.common.main.ThirstMod;
import com.thetorine.thirstmod.common.network.PacketConfigs;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventHook {
	
	@SideOnly(Side.CLIENT)
	private GuiThirst gui = new GuiThirst();
	
	@SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			ThirstMod.INSTANCE.onTickInGame();
		}
	}

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			ThirstMod.INSTANCE.onServerTick(event.player);
			if(ThirstMod.DEBUG_MODE) {
				if(Keyboard.isKeyDown(Keyboard.KEY_B)) {
					PlayerHandler.getPlayer(event.player.getDisplayName()).stats.addStats(-1, 0);
				} else if(Keyboard.isKeyDown(Keyboard.KEY_N)) {
					PlayerHandler.getPlayer(event.player.getDisplayName()).stats.addStats(1, 0);
				}
			}
		}
	}

	@SubscribeEvent
	public void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayer player = event.player;
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			PlayerHandler.addPlayer(player.getDisplayName(), new PlayerHandler(player, new ThirstStats(player)));
			ThirstMod.network.sendTo(new PacketConfigs(), (EntityPlayerMP) player);
		}
	}
	
	@SubscribeEvent
	public void renderGame(RenderGameOverlayEvent event) {
		int width = event.resolution.getScaledWidth();
	    int height = event.resolution.getScaledHeight();
		switch(event.type) {
			case HEALTH: {
				if (!ThirstMod.MOD_OFF && !Minecraft.getMinecraft().thePlayer.isRidingHorse()) {
					gui.renderThirst(width, height);
				}
				break;
			}
			case AIR: {
			    gui.left_height = GuiIngameForge.left_height;
			    gui.right_height = GuiIngameForge.right_height;

				gui.renderAir(width, height);
				event.setCanceled(true);
				break;
			}
			case ARMOR: {
				gui.renderArmor(width, height);
				event.setCanceled(true);
				break;
			}
			default: break;
		}
	}

	@SubscribeEvent
	public void onAttack(AttackEntityEvent attack) {
		if ((PlayerHandler.getPlayer(attack.entityPlayer.getDisplayName()) != null) && (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)) {
			PlayerHandler.getPlayer(attack.entityPlayer.getDisplayName()).addExhaustion(0.5f);
		}
	}

	@SubscribeEvent
	public void onHurt(LivingHurtEvent hurt) {
		if (hurt.entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) hurt.entityLiving;
			PlayerHandler.getPlayer(player.getDisplayName()).addExhaustion(0.4f);
		}
	}

	@SubscribeEvent
	public void onHarvest(BlockEvent.HarvestDropsEvent event) {
		if ((FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) && (event.harvester != null)) {
			if (PlayerHandler.getPlayer(event.harvester.getDisplayName()) != null) {
				PlayerHandler.getPlayer(event.harvester.getDisplayName()).addExhaustion(0.03f);
			}
		}
	}

	@SubscribeEvent
	public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
		for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
			if (event.craftMatrix.getStackInSlot(i) != null) {
				if (event.craftMatrix.getStackInSlot(i).getUnlocalizedName().equals(ThirstMod.ccFilter.getUnlocalizedName())) {
					if (event.craftMatrix.getStackInSlot(i).getItemDamage() != 4) {
						ItemStack ccFilter = new ItemStack(ThirstMod.ccFilter);
						ccFilter.damageItem(event.craftMatrix.getStackInSlot(i).getItemDamage() + 1, event.player);
						event.player.inventory.addItemStackToInventory(ccFilter);
					}
				}
				if (event.craftMatrix.getStackInSlot(i).getUnlocalizedName().equals(ThirstMod.filter.getUnlocalizedName())) {
					if (event.craftMatrix.getStackInSlot(i).getItemDamage() != 4) {
						ItemStack Filter = new ItemStack(ThirstMod.filter);
						Filter.damageItem(event.craftMatrix.getStackInSlot(i).getItemDamage() + 1, event.player);
						event.player.inventory.addItemStackToInventory(Filter);
					} else {
						event.player.inventory.addItemStackToInventory(new ItemStack(ThirstMod.dFilter, 1));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onSleep(PlayerSleepInBedEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			World world = event.entityPlayer.worldObj;
			long sleepTime = 24000 - world.getWorldTime();
			int thirstInterval = 2500;

			int thirstLoss = Math.min(Math.max(Math.round((float) sleepTime / (float) thirstInterval), 0), 20);
			ThirstUtils.print(thirstLoss);
			if ((PlayerHandler.getPlayer(event.entityPlayer.getDisplayName()).stats.level - thirstLoss) <= 6) {
				Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("You are too thirsty to sleep!"));
				event.result = EntityPlayer.EnumStatus.OTHER_PROBLEM;
			} else {
				PlayerHandler.getPlayer(event.entityPlayer.getDisplayName()).stats.addStats(-thirstLoss, 0f);
			}
		}
	}
}
