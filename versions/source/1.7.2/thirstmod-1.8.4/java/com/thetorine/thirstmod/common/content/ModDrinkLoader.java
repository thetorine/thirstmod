package com.thetorine.thirstmod.common.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;
import com.thetorine.thirstmod.common.utils.DrinkLists;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ModDrinkLoader {

	public ModDrinkLoader(File file, File content) {
		try {
			findFiles(file, content);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void findFiles(File file, File contentDir) throws Exception {
		File modsDir = new File(file.getPath(), "/mods");
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
				ThirstUtils.print("No Drink Items found in: " + zipFile.getName());
			}

			zip.close();
		}

		createInstructions(new File(contentDir, "usage.info"));
		createDrinks(new File(contentDir, "thirstmod.txt"));

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
			if (line.startsWith("//")) {
				continue;
			}
			String[] colon = line.split(":");
			readFiles(colon);
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

		Item item = (Item) Item.itemRegistry.getObject(id);
		DrinkLists.addDrink(new ItemStack(item, 0, metadata), level, saturation, poison, chance);
		ThirstUtils.print("Added Drink from External Mod. ID:" + id + " Metadata:" + metadata);
	}

	public void createDrinks(File file) throws Exception {
		if (!file.exists()) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("//Mushroom Stew\n");
			writer.write("mushroom_stew:7:1.2\n");
			writer.write("//Milk Bucket\n");
			writer.write("milk_bucket:8:3.4\n");
			writer.write("//Water Bottle\n");
			writer.write("potion:3:1.4:true:0.4:0");
			writer.close();
		}
	}

	public void createInstructions(File file) throws Exception {
		if (!file.exists()) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("This newly implemented feature allows modders and players alike to replenish the thirst bar using items "
					+ "already created in the mod. This feature requires that the item have a 'onEaten' method witin the item class file. "
					+ "For players, the item should be able to be eaten/drunk much like potions and steak. If this is not present"
					+ ", this feature will not function and may cause crashes upon loading the game.\n\n");
			writer.write("Steps to Success\n");
			writer.write("1. Create a text file and within it, add the following:\n");
			writer.write("'id:thirst-replenish:thirst-saturation'\n");
			writer.write("Replace each word with a number. This should end up as for eg: 335:8:3.4 (Milk Bucket heals 4 droplets)\n\n");
			writer.write("id = item id of the item.\n\n");
			writer.write("thirst-replenish = how much thirst to heal. min 0, max 20, 2 is = 1 water droplet.\n\n");
			writer.write("thirst-saturation = look it up on mc wiki - hunger saturation.\n\n");
			writer.write("2. Do this for every drink you wish to add on each individual line.\n");
			writer.write("3. Save the file as modname.txt in .minecraft/ThirstMod/External Mods/ and Enjoy!");
			writer.close();
		}
	}

	// Not needed anymore!
	public URI fixURL(URL url) throws Exception {
		String s = url.toString();
		s = s.replace("jar:", "");
		s = s.substring(0, s.indexOf("!/"));
		return new URL(s).toURI();
	}
}
