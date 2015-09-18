package com.thetorine.thirstmod.core.content.packs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameData;

import com.thetorine.thirstmod.core.main.ThirstMod;

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
			System.out.println("Couldn't load external drinks.");
		}
	}

	public void readFiles(String[] colon) {
		String id = colon[0];
		int level = Integer.parseInt(colon[1]);
		float saturation = Float.parseFloat(colon[2]);
		boolean poison = false;
		float chance = 0.0f;
		int metadata = 0;

		if (colon.length == 5) {
			poison = Boolean.parseBoolean(colon[3]);
			chance = Float.parseFloat(colon[4]);
		} else if (colon.length == 6) {
			poison = Boolean.parseBoolean(colon[3]);
			chance = Float.parseFloat(colon[4]);
			metadata = Integer.parseInt(colon[5]);
		}

		Item item = (Item) GameData.getItemRegistry().getObject(id);
		DrinkLists.addDrink(new ItemStack(item, 0, metadata), level, saturation, poison, chance);
		ThirstMod.print("Added: " + id + " at " + item);
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
			writer.write("item_id thirst_replenish thirst_saturation causes_poison poison_chance metadata\n\n");
			writer.write("Copy and paste this in a new [.txt] file. Replace all of the above with the correct values.\n");
			writer.write("    item_id = String (consult wiki or mod author for item id)\n"
					   + "    thirst_replenish = Integer (one to twenty)\n"
					   + "    thirst_saturation = Decimal (consult food wiki page for details)\n"
					   + "    causes_poison = Boolean\n"
					   + "    poison_chance = Decimal (0.1 to 0.9)\n"
					   + "    metadata = Integer (consult wiki or mod author for item metadata)\n\n");
			writer.write("Use the exact format displayed. Do NOT misplace the values or the game WILL crash.\n\n");
			writer.write("Don't forget the spaces between each value!\n\n");
			writer.write("One item per line in a [.txt] file in this folder. Ask tarun1998 on the forums about any issues.");
			writer.close();
		}
	}
}
