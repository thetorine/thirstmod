package com.thetorine.thirstmod.core.content.packs;

import java.io.*;
import java.util.*;

import com.thetorine.thirstmod.core.content.ItemDrink;
import com.thetorine.thirstmod.core.content.blocks.TileEntityDB;
import com.thetorine.thirstmod.core.main.ThirstMod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ContentLoader {
	public List<String> categories = new ArrayList<String>();
	public Map<String, ArrayList<TemplateModifier>> templateLoad = new HashMap<String, ArrayList<TemplateModifier>>();
	public Map<Integer, ArrayList<DrinkModifier>> drinkLoad = new HashMap<Integer, ArrayList<DrinkModifier>>();
	public List<File> filesToLoad = new ArrayList<File>();
	public String split;

	public ContentLoader() {
		parseTemplate();
		loadDrinks(new File(ThirstMod.mcDir(), "/thirstmod/content/"));
		addDrinks();
	}
	
	public void loadDrinks(File dir) {
		int i = 0;
		if(!dir.exists()) {
			dir.mkdirs();
		}
		findFiles(dir, ".txt");
		for(File f : filesToLoad) {
			try {
				Scanner s = new Scanner(f);
				parseDrink(s, i);
				i++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void parseDrink(Scanner reader, int currentLoad) {
		List<String> elements = new ArrayList<String>();
		while(true) {
			if(reader.hasNextLine()) {
				elements.add(reader.nextLine());
			} else break;
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
	
	public void addDrinks() {
		for(int i = 0; i < drinkLoad.size(); i++) {
			String name = "", shortname = "", item = "";
			int color = 0, stacksize = 0, metadata = 0, bar_heal = 0, bar_heal_hunger = 0, potionID = 0, duration = 0, amp = 0;
			float sat_thirst = 0, sat_hunger = 0, poisonChance = 0;
			boolean hasEffect = false, potion_cure = false, alwaysDrinkable = false;
			
			for(DrinkModifier dm : drinkLoad.get(i)) {
				if(dm.modifier.equals("name")) 
					name = (String) dm.value;
				if(dm.modifier.equals("internal_name")) 
					shortname = (String) dm.value;
				if(dm.modifier.equals("item"))
					item = (String) dm.value;
				if(dm.modifier.equals("colour"))
					color = (Integer) dm.value;
				if(dm.modifier.equals("stacksize"))
					stacksize = (Integer) dm.value;
				if(dm.modifier.equals("metadata"))
					metadata = (Integer) dm.value;
				if(dm.modifier.equals("bar_heal"))
					bar_heal = (Integer) dm.value;
				if(dm.modifier.equals("bar_heal_hunger"))
					bar_heal_hunger = (Integer) dm.value;
				if(dm.modifier.equals("id"))
					potionID = (Integer) dm.value;
				if(dm.modifier.equals("duration"))
					duration = (Integer) dm.value;
				if(dm.modifier.equals("amplifier"))
					amp = (Integer) dm.value;
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
			
			Item loadedDrink = new ItemDrink(bar_heal, sat_thirst, color, stacksize, hasEffect, alwaysDrinkable, shortname)
				.healFood(bar_heal_hunger, sat_hunger).setPoisoningChance(poisonChance)
				.setPotionEffect(potionID, duration).setCuresPotions(potion_cure);
			
			GameRegistry.registerItem(loadedDrink, shortname);
			LanguageRegistry.addName(loadedDrink, name);
			DrinkLists.addDrink(new ItemStack(loadedDrink), bar_heal);
			Item recipeItem = (Item) Item.itemRegistry.getObject(item);
			TileEntityDB.addRecipe(recipeItem.getUnlocalizedName(), new ItemStack(loadedDrink));
		}
	}
	
	public void parseTemplate() {
		Scanner reader = new Scanner(ContentLoader.class.getResourceAsStream("/assets/thirstmod/content/template.txt"));
		List<String> elements = new ArrayList<String>();
		while(true) {
			if(reader.hasNextLine()) {
				elements.add(reader.nextLine());
			} else break;
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
	}
	
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
	
	public void findFiles(File root, String filter) {
	    File[] files = root.listFiles(); 
	    if(files != null) {
	    	for (File file : files) {
		        if (file.isFile()) {
		            if(file.getName().endsWith(filter)) {
		            	filesToLoad.add(file);
		            }
		        } else if (file.isDirectory()) {
		            findFiles(file, filter);
		        }
		    }
	    }
	}
	
	public static class TemplateModifier {
		public String modifier;
		public String datatype;
		
		public TemplateModifier(String s, String s1) {
			this.modifier = s;
			this.datatype = s1;
		}
	}
	
	public static class DrinkModifier {
		public String modifier;
		public Object value;
		
		public DrinkModifier(String modifier, Object value) {
			this.modifier = modifier;
			this.value = value;
		}
	}
}
