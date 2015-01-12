package com.thetorine.thirstmod.common.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import com.thetorine.thirstmod.client.main.ClientProxy;
import com.thetorine.thirstmod.common.blocks.JMRecipes;
import com.thetorine.thirstmod.common.blocks.RCRecipes;
import com.thetorine.thirstmod.common.main.ThirstMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;

public class ThirstUtils {
	public static final String NAME = "Thirst mod";
	public static final String ID = "thirstmod";

	public static String getPlayerName() {
		return ClientProxy.getPlayer().getDisplayName();
	}

	/**
	 * Gets the current biome the player is in.
	 * 
	 * @param entityplayer
	 * @return the current biome of the player.
	 */
	public static String getCurrentBiome(EntityPlayer entityplayer) {
		return entityplayer.worldObj.getWorldChunkManager().getBiomeGenAt((int) entityplayer.posX, (int) entityplayer.posZ).biomeName;
	}

	public static void setModUnloaded() {
		ThirstMod.proxy.loadedMod = false;
	}

	public static int getMovementStat(EntityPlayer player) {
		double s = player.posX;
		double s1 = player.posY;
		double s2 = player.posZ;
		double d = s - player.prevPosX;
		double d1 = s1 - player.prevPosY;
		double d2 = s2 - player.prevPosZ;
		return Math.round(MathHelper.sqrt_double((d * d) + (d1 * d1) + (d2 * d2)) * 100F);
	}

	/**
	 * Adds a recipe to the Drinks Brewer.
	 * 
	 * @param id
	 *            Item that is placed in the top Drinks Brewer Slot.
	 * @param item
	 *            The Item that is returned after the item (int id) is brewed.
	 */
	public static void addJMRecipe(String s, ItemStack item) {
		JMRecipes.solidifying().addSolidifying(s, item);
	}

	/**
	 * Adds a recipe to the Drinks Brewer.
	 * 
	 * @param id
	 *            Item that is placed in the top Drinks Brewer Slot.
	 * @param j
	 *            Metadata for (int i) if needed.
	 * @param item
	 *            The Item that is returned after the item (int id, int
	 *            metadata) is brewed.
	 */
	public static void addJMRecipe(String s, int metadata, ItemStack item) {
		JMRecipes.solidifying().addSolidifyingg(s, metadata, item);
	}

	/**
	 * Adds a recipe to the Rain Collector
	 * 
	 * @param id
	 *            The id of the item to fill.
	 * @param timeToFill
	 *            Amount of time taken to fill the item. For reference Glass
	 *            Bottle = 200, Bucket = 600;
	 * @param return1
	 *            The filled item.
	 */
	public static void addRCRecipe(String s, int timeToFill, ItemStack return1) {
		RCRecipes.fill().addRecipe(s, timeToFill, return1);
	}

	public static String getDir() {
		File s = ObfuscationReflectionHelper.getPrivateValue(Loader.class, Loader.instance(), "minecraftDir");
		return s.getAbsolutePath();
	}

	/**
	 * Prints to either the console or the minecraft server window depending on
	 * which side we are current at.
	 * 
	 * @param obj
	 *            something to print.
	 */
	public static void print(Object obj) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			System.out.println("[ThirstMod] " + obj);
		} else {
			FMLCommonHandler.instance().getMinecraftServerInstance().logInfo("[ThirstMod] " + obj.toString());
		}
	}

	public static void printValues(String s, Object... objects) {
		print(String.format(s, objects));
	}

	/**
	 * Converts data from the 1.2.0 or less release to the 1.2.1 and forwards
	 * data type.
	 * 
	 * @param nbt
	 *            Deprecated as of 1.3.0 release. Will be removed in Minecraft
	 *            1.5
	 */
	@Deprecated
	public static void convertData(NBTTagCompound nbt) {
		int level = nbt.getInteger("tmLevel");
		float exhaustion = nbt.getFloat("tmExhaustion");
		float saturation = nbt.getFloat("tmSaturation");
		int healHurtTimer = nbt.getInteger("tmTimer");
		int drinkTimer = nbt.getInteger("tmTimer2");

		NBTTagCompound newData = new NBTTagCompound();
		nbt.setTag("ThirstMod", newData);

		newData.setInteger("level", level);
		newData.setFloat("exhaustion", exhaustion);
		newData.setFloat("saturation", saturation);
		newData.setInteger("healHurtTimer", healHurtTimer);
		newData.setInteger("drinkTimer", drinkTimer);
		print("Converted old stats to new format!");
	}

	public static void addURLToSystemClassLoader(URL url) throws Exception {
		ClassLoader classloader = (net.minecraft.server.MinecraftServer.class).getClassLoader();
		Method method = (java.net.URLClassLoader.class).getDeclaredMethod("addURL", new Class[] { java.net.URL.class });
		method.setAccessible(true);
		method.invoke(classloader, new Object[] { url });
	}

	public static File[] getParentSources() {
		LaunchClassLoader classLoader = (LaunchClassLoader) ThirstUtils.class.getClassLoader();
		List<URL> urls = classLoader.getSources();
		File[] sources = new File[urls.size()];
		try {
			for (int i = 0; i < urls.size(); i++) {
				sources[i] = new File(urls.get(i).toURI());
			}
			return sources;
		} catch (URISyntaxException e) {
			throw new LoaderException(e);
		}
	}
	
	public static boolean isClient() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT;
	}
	
	public static boolean isServer() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER;
	}
}