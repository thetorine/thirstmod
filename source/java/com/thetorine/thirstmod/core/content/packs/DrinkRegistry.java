package com.thetorine.thirstmod.core.content.packs;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;

import com.thetorine.thirstmod.core.content.blocks.DBRecipes;
import com.thetorine.thirstmod.core.main.ThirstMod;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.*;

public class DrinkRegistry {

	public DrinkRegistry() {
		try {
			findFiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void findFiles() throws Exception {
		File modsDir = new File(ThirstMod.getMinecraftDir(), "/mods");
		List<URI> mods = new ArrayList<URI>();
		if (modsDir.listFiles().length > 0) {
			for (int i = 0; i < modsDir.listFiles().length; i++) {
				if (modsDir.listFiles()[i].getName().endsWith(".zip") || modsDir.listFiles()[i].getName().endsWith(".jar")) {
					mods.add(modsDir.listFiles()[i].toURI());
				}
			}
		}

		for (int i = 0; i < mods.size(); i++) {
			URI uri = mods.get(i);
			File zipFile = new File(uri);
			ZipFile zip = new ZipFile(zipFile);
			try {
				InputStream is = zip.getInputStream(zip.getEntry("drinks.txt"));
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				init(reader);
			} catch (Exception e) {
				ThirstMod.print("No Drink Items found in: " + zipFile.getName());
			}

			zip.close();
		}

		File contentDir = new File(ThirstMod.getMinecraftDir(), "/thirstmod/external/");
		contentDir.mkdirs();
		createInstructions(new File(contentDir, "usage.info"));
		createDrinks(new File(contentDir, "minecraft-items.txt"));

		if (contentDir.listFiles().length > 0) {
			for (int i = 0; i < contentDir.listFiles().length; i++) {
				if ((!contentDir.listFiles()[i].isFile()) || (!contentDir.listFiles()[i].getName().endsWith(".txt"))) {
					continue;
				}
				init(new BufferedReader(new FileReader(contentDir.listFiles()[i])));
			}
		}
	}

	public void init(BufferedReader br) {
		try {
			while(br.ready()) {
				String line = br.readLine();
				if(line.startsWith("//")) {
					continue;
				} else {
					String[] split = line.split(" ");
					readFiles(split);
				}
			}
		} catch(Exception e) {
			ThirstMod.print("Couldn't load external drinks.");
		}
	}

	public void readFiles(String[] elements) {
		String id = null;
		int level = 0;
		float saturation = 0f;
		boolean poison = false;
		float chance = 0.0f;
		int metadata = 0;
		String drinkRecipeID = null;
		
		for(int i = 0; i < elements.length; i++) {
			switch(i) {
				case 0: id = elements[i]; break;
				case 1: level = Integer.parseInt(elements[i]); break;
				case 2: saturation = Float.parseFloat(elements[i]); break;
				case 3: poison = Boolean.parseBoolean(elements[i]); break;
				case 4: chance = Float.parseFloat(elements[i]); break;
				case 5: metadata = Integer.parseInt(elements[i]); break;
				case 6: drinkRecipeID = elements[i]; break;
			}
		}

		Item item = (Item) GameData.getItemRegistry().getObject(id);
		if(item != null) {
			DrinkLists.addDrink(new ItemStack(item, 0, metadata), level, saturation, poison, chance);
			if(drinkRecipeID != null) {
				Item recipeItem = (Item) GameData.getItemRegistry().getObject(drinkRecipeID);
				if(recipeItem != null) {
					DBRecipes.instance().addRecipe(recipeItem.getUnlocalizedName(), new ItemStack(item));
				} else {
					ThirstMod.print("External Drink Loader: Failed to load recipe " + drinkRecipeID + " for " + id);
				}
			}
			ThirstMod.print("External Drink Loader: Added: " + id + " at " + item);
		} else {
			ThirstMod.print("External Drink Loader: No such item for id: " + id);
		}
	}

	public void createDrinks(File file) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write("minecraft:mushroom_stew 7 1.2\n");
		writer.write("minecraft:milk_bucket 8 3.4\n");
		writer.write("minecraft:potion 3 1.4 true 0.4 0");
		writer.close();
	}

	public void createInstructions(File file) throws Exception {
		if (!file.exists()) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("Lets hope you're not an idiot. :D\n\n");
			writer.write("item_id thirst_replenish thirst_saturation causes_poison poison_chance metadata recipe_id\n\n");
			writer.write("Copy and paste this in a new [.txt] file. Replace all of the above with the correct values.\n");
			writer.write("    item_id = String (consult wiki or mod author for item id)\n"
					   + "    thirst_replenish = Integer (one to twenty)\n"
					   + "    thirst_saturation = Decimal (consult food wiki page for details)\n"
					   + "    causes_poison = Boolean\n"
					   + "    poison_chance = Decimal (0.1 to 0.9)\n"
					   + "    metadata = Integer (consult wiki or mod author for item metadata)\n"
					   + "    recipe_id = String (adds a recipe for this drink to be brewed in the Drinks Brewer), (consult wiki or mod author for item id)\n\n");
			writer.write("Use the exact format displayed. Do NOT misplace the values or the game WILL crash.\n\n");
			writer.write("Don't forget the spaces between each value!\n\n");
			writer.write("One item per line in a [.txt] file in this folder. Ask tarun1998 on the forums about any issues.");
			writer.close();
		}
	}
}
