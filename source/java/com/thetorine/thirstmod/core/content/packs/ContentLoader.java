package com.thetorine.thirstmod.core.content.packs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.thetorine.thirstmod.core.content.ItemDrink;
import com.thetorine.thirstmod.core.content.blocks.DBRecipes;
import com.thetorine.thirstmod.core.main.ThirstMod;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ContentLoader {
	public List<String> categories = new ArrayList<String>();
	public Map<String, ArrayList<TemplateModifier>> templateLoad = new HashMap<String, ArrayList<TemplateModifier>>();
	public Map<Integer, ArrayList<DrinkModifier>> drinkLoad = new HashMap<Integer, ArrayList<DrinkModifier>>();
	public HashMap<String, String> languageTable = new HashMap<String, String>();
	public List<File> filesToLoad = new ArrayList<File>();
	public String split;

	public ContentLoader() {
		try {
			parseTemplate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parseTemplate() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(ContentLoader.class.getResourceAsStream("/assets/thirstmod/content/template.txt")));
		List<String> elements = new ArrayList<String>();
		while(reader.ready()) {
			elements.add(reader.readLine());
		}
		String prevCategory = "";
		for(String s : elements) {
			if(s.startsWith(" ")) {
				String[] modifers = s.replaceFirst(" ", "").split(" ");
				TemplateModifier c = new TemplateModifier(modifers[0], modifers[1]);
				addValues(prevCategory, c, 0);
			} else if(!s.contains("//")) {
				prevCategory = s;
				if(s.contains("split")) {
					split = s.split(" ", 2)[1];
				}
			}
		}
		reader.close();
		loadDrinks(new File(ThirstMod.getMinecraftDir(), "/thirstmod/content/"));
	}
	
	public void loadDrinks(File dir) {
		int i = 0;
		if(!dir.exists()) {
			dir.mkdirs();
			return;
		}
		findFiles(dir);
		for(File f : filesToLoad) {
			try {
				if(f.getName().endsWith(".txt")) {
					parseDrink(new BufferedReader(new FileReader(f)), i);
					i++;
				} else if(f.getName().endsWith(".zip")) {
					i = readZip(f, i);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		addDrinks();
	}
	
	public void findFiles(File root) {
	    File[] files = root.listFiles(); 
	    if(files != null) {
	    	for (File file : files) {
		        if (file.isFile()) {
		            if(file.getName().endsWith(".txt") || file.getName().endsWith(".zip")) {
		            	filesToLoad.add(file);
		            }
		        } else if (file.isDirectory()) {
		            findFiles(file);
		        }
		    }
	    }
	}
	
	public void parseDrink(BufferedReader reader, int currentLoad) throws IOException {
		List<String> elements = new ArrayList<String>();
		while(reader.ready()) {
			elements.add(reader.readLine());
		}
		String prevCategory = "";
		for(String s : elements) {
			if(s.startsWith(" ")) {
				String[] modifers = s.replaceFirst(" ", "").split(split, 2);
				for(TemplateModifier c: templateLoad.get(prevCategory)) {
					if(c.modifier.equals(modifers[0])) {
						DrinkModifier m = new DrinkModifier(modifers[0], parseValue(modifers[1], c.datatype));
						addValues(currentLoad, m, 1);
					}
				}
			} else {
				prevCategory = s;
				if(!categories.contains(s)) {
					categories.add(s);
				}
			}
		}	
		reader.close();
	}
	
	public int readZip(File zip, int i) throws IOException {
		int load = i;
		ZipFile zf = new ZipFile(zip);
		Enumeration<? extends ZipEntry> entries = zf.entries();
		while(entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if(entry.getName().endsWith(".txt") && !entry.getName().contains("__MACOSX/._")) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(zf.getInputStream(entry)));
				parseDrink(reader, load);
				reader.close();
				load++;
			}
		}
		zf.close();
		return load;
	}
	
	public void addDrinks() {
		for(int i = 0; i < drinkLoad.size(); i++) {
			String name = "", shortname = "", item = "";
			int color = 0, stacksize = 0, bar_heal = 0, bar_heal_hunger = 0, potionID = 0, duration = 0, itemMetadata = 0;
			float sat_thirst = 0, sat_hunger = 0, poisonChance = 0;
			boolean hasEffect = false, potion_cure = false, alwaysDrinkable = false;
			
			for(DrinkModifier dm : drinkLoad.get(i)) {
				if(dm.modifier.equals("name")) 
					name = (String) dm.value;
				if(dm.modifier.equals("internal_name")) 
					shortname = (String) dm.value;
				if(dm.modifier.equals("item"))
					item = (String) dm.value;
				if(dm.modifier.equals("item_mod_id")) 
					item = ((String) dm.value) + ":" + item; 
				if(dm.modifier.equals("item_metadata"))
					itemMetadata = (Integer) dm.value;
				if(dm.modifier.equals("colour"))
					color = (Integer) dm.value;
				if(dm.modifier.equals("stacksize"))
					stacksize = (Integer) dm.value;
				if(dm.modifier.equals("bar_heal"))
					bar_heal = (Integer) dm.value;
				if(dm.modifier.equals("bar_heal_hunger"))
					bar_heal_hunger = (Integer) dm.value;
				if(dm.modifier.equals("id"))
					potionID = (Integer) dm.value;
				if(dm.modifier.equals("duration"))
					duration = (Integer) dm.value;
				if(dm.modifier.equals("saturation_heal"))
					sat_thirst = (Float) dm.value;
				if(dm.modifier.equals("saturation_heal_hunger"))
					sat_hunger = (Float) dm.value;
				if(dm.modifier.equals("poisonChance"))
					poisonChance = (Float) dm.value;
				if(dm.modifier.equals("effect")) 
					hasEffect = (Boolean) dm.value;
				if(dm.modifier.equals("potion_cure")) 
					potion_cure = (Boolean) dm.value;
				if(dm.modifier.equals("always_drinkable")) 
					alwaysDrinkable = (Boolean) dm.value;
			}
			
			ItemDrink loadedDrink = new ItemDrink(bar_heal, sat_thirst, color, stacksize, hasEffect, alwaysDrinkable, shortname)
				.healFood(bar_heal_hunger, sat_hunger).setPoisoningChance(poisonChance)
				.setPotionEffect(potionID, duration).setCuresPotions(potion_cure);
			
			if(shortname.length() > 0) {
				Item recipeItem = GameData.getItemRegistry().getObject(item);
				if(recipeItem != null) {
					loadedDrink.setRecipeItem(recipeItem);
					GameRegistry.registerItem(loadedDrink, shortname);
					DrinkLists.addDrink(new ItemStack(loadedDrink), bar_heal, sat_thirst);
					ItemStack tempRecipeStack = new ItemStack(recipeItem, 0, itemMetadata);
					DBRecipes.instance().addRecipe(tempRecipeStack.getUnlocalizedName(), itemMetadata, new ItemStack(loadedDrink));
					languageTable.put(String.format("item.%s.name", shortname), name);
				} else {
					ThirstMod.print(
						"Content Loader: Unable to aquire recipe for item id: " + item 
					  + "/n Item not added: " + name);
				}
			} 
		}
		injectLanguage();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addValues(Object key, Object value, int map) {
		ArrayList tempList = null;
		switch(map) {
			case 0: {
				if (templateLoad.containsKey(key)) {
					tempList = templateLoad.get(key);
					if (tempList == null)
						tempList = new ArrayList<TemplateModifier>();
					tempList.add(value);
				} else {
					tempList = new ArrayList<TemplateModifier>();
					tempList.add(value);
				}
				templateLoad.put((String) key, tempList);
				break;
			}
			case 1: {
				if (drinkLoad.containsKey(key)) {
					tempList = drinkLoad.get(key);
					if (tempList == null)
						tempList = new ArrayList<TemplateModifier>();
					tempList.add(value);
				} else {
					tempList = new ArrayList<TemplateModifier>();
					tempList.add(value);
				}
				drinkLoad.put((Integer) key, tempList);
				break;
			}
		}
	}
	
	private Object parseValue(String s, String datatype) {
		if(datatype.equals("string")) {
			return s;
		} else if(datatype.equals("integer")) {
			return Integer.parseInt(s);
		} else if(datatype.equals("float")) {
			return Float.parseFloat(s);
		} else if(datatype.equals("boolean")) {
			return Boolean.parseBoolean(s);
		} else if(datatype.equals("hex")) {
			return Integer.parseInt(s, 16);
		}
		return null;
	}
	
	public void injectLanguage() {
		if(languageTable.size() <= 0) return;
		LanguageRegistry.instance().injectLanguage("en_US", languageTable);
	}
	
	private static class TemplateModifier {
		public String modifier;
		public String datatype;
		
		public TemplateModifier(String s, String s1) {
			this.modifier = s;
			this.datatype = s1;
		}
	}
	
	private static class DrinkModifier {
		public String modifier;
		public Object value;
		
		public DrinkModifier(String modifier, Object value) {
			this.modifier = modifier;
			this.value = value;
		}
	}
}