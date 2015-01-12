package com.thetorine.thirstmod.common.content;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import com.thetorine.thirstmod.common.utils.Version;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.discovery.ContainerType;
import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;

public class ContentLoader {
	public static List<String> addedfiles = new ArrayList<String>();

	public ContentDrink drink;
	public int metadata;
	public int replenish;
	public int colour;
	public String jmTop;
	public float saturation;
	public String name;
	public String shortName;
	public boolean alwaysDrinkable;
	public int maxStackSize;
	public float poisonChance;
	public boolean isShiny;
	public int foodHeal;
	public float satHeal;
	public boolean curesPotions;

	public int potionId;
	public int potionDuration;
	public int potionAmplifier;

	public ContentLoader loader;

	public ContentLoader(Side side) {
		//loadMainFiles(side);
	}

	public void loadMainFiles(Side side) {
		File contentDir = new File(ThirstUtils.getDir(), "/ThirstMod/Content Packs/");
		File modDir = new File(ThirstUtils.getDir(), "/ThirstMod/External Mods/");
		if (!contentDir.exists()) {
			contentDir.mkdirs();
		}
		if (!modDir.exists()) {
			modDir.mkdirs();
		}

		new ModDrinkLoader(new File(ThirstUtils.getDir()), modDir);

		LinkedList<File> contentList = new LinkedList<File>();
		LinkedList<String> drinkList = new LinkedList<String>();
		if (contentDir.listFiles().length > 0) {
			for (int i = 0; i < contentDir.listFiles().length; i++) {
				if (contentDir.listFiles()[i].isFile() == false) {
					contentList.add(contentDir.listFiles()[i]);
				}
			}
		}

		for (int i = 0; i < contentList.size(); i++) {
			File files = contentList.get(i);
			for (int j = 0; j < files.listFiles().length; j++) {
				File fileInDir = files.listFiles()[j];
				if (fileInDir.getName().endsWith(".txt")) {
					drinkList.add(fileInDir.getName());
					setDefaults();
					try {
						init(new BufferedReader(new FileReader(fileInDir)), files.getName());

						drink = ((ContentDrink) (((ContentDrink) new ContentDrink(replenish, saturation, alwaysDrinkable, colour).setUnlocalizedName(shortName).setMaxStackSize(maxStackSize))
								.setEffect(isShiny)).setPoisoningChance(poisonChance).setPotionEffect(potionId, potionDuration, potionDuration, 1).setCuresPotions(curesPotions));
						LanguageRegistry.addName(drink, name);
						Item item = (Item) Item.itemRegistry.getObject(jmTop);
						ThirstUtils.addJMRecipe(item.getUnlocalizedName(), new ItemStack(drink));
						GameRegistry.registerItem(drink, shortName);
						if (!addedfiles.contains(files.getName())) {
							addedfiles.add(files.getName());
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		ThirstUtils.print(new String("Loaded Drink Packs: " + addedfiles).replace("[", "").replace("]", ""));
	}

	public void init(BufferedReader br, String fileDir) {
		while (true) {
			String line = null;
			try {
				line = br.readLine();
			} catch (Exception e) {
				break;
			}
			if (line == null) {
				break;
			}
			String[] colon = line.split(":");
			read(colon, fileDir);
		}
	}

	/**
	 * Reads the text file.
	 * 
	 * @param colon
	 *            The character that all the information is written after. In
	 *            this case ':'
	 */
	public void read(String[] colon, String fileDir) {
		if (colon[0].equals("Replenish")) {
			replenish = Integer.parseInt(colon[1]);
		}
		if (colon[0].equals("Saturation")) {
			saturation = Float.parseFloat(colon[1]);
		}
		if (colon[0].equals("Name")) {
			name = colon[1];
		}
		if (colon[0].equals("Colour")) {
			colour = Integer.parseInt(colon[1], 16);
		}

		if (colon[0].equals("ShortName")) {
			shortName = colon[1];
		}
		if (colon[0].equals("DBRecipe")) {
			jmTop = colon[1];
			metadata = Integer.parseInt(colon[2]);
		}
		if (colon[0].equals("AlwaysDrinkable")) {
			alwaysDrinkable = Boolean.parseBoolean(colon[1].toLowerCase());
		}
		if (colon[0].equals("MaxStackSize")) {
			maxStackSize = Integer.parseInt(colon[1]);
		}
		if (colon[0].equals("PoisonChance")) {
			poisonChance = Float.parseFloat(colon[1]);
		}
		if (colon[0].equals("Shiny")) {
			isShiny = Boolean.parseBoolean(colon[1].toLowerCase());
		}
		if (colon[0].equals("FoodHeal")) {
			foodHeal = Integer.parseInt(colon[1]);
			satHeal = Float.parseFloat(colon[2]);
		}
		if (colon[0].equals("PotionEffect")) {
			potionId = Integer.parseInt(colon[1]);
			potionDuration = Integer.parseInt(colon[2]);
			potionAmplifier = Integer.parseInt(colon[3]);
		}
		if (colon[0].equals("CuresPotions")) {
			curesPotions = Boolean.parseBoolean(colon[1]);
		}
	}

	public void setDefaults() {
		metadata = 0;
		replenish = 0;
		jmTop = "";
		saturation = 0;
		name = "";
		shortName = "";
		alwaysDrinkable = false;
		maxStackSize = 0;
		poisonChance = 0.0f;
		isShiny = false;
		foodHeal = 0;
		satHeal = 0.0f;
		potionId = 0;
		potionDuration = 0;
		potionAmplifier = 0;
		curesPotions = false;
	}
}