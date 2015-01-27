# Thirst Mod 
The Mod that makes you thirsty. 

# Creating New Drinks
1. Open your prefered text editor. Examples include Notepad++ for Windows and TextEdit for Mac OSX.
2. Copy and paste the all the text from this [file](https://github.com/thetorine/thirstmod/blob/master/contentpacks/contentpack_template.txt).
3. Fill the details of the drink you want to create after each colon. (':') Follow the guide below.

**_Item_**
- **name:** Put the ingame name of the drink here. Examples include: Apple Juice
- **internal\_name:** Put any name here but ensure there are no spaces between any words and that it is all in lowercase. Examples include: apple\_juice
- **colour:** Put a hex representation of any colour here. This will define the look of the liquid in game. [Colour Picker](http://www.colorpicker.com/) is a great website for gathering hex colours. Examples include: C4694B
- **stacksize:** This will be the max stacksize of the drink you create. Must be between 1 and 64.
- **effect:** This will make the drink shine like a potion. Can be either "true" or "false". (Without the quotation marks)
- **always\_drinkable:** This will allow the drink to be drank even if the thirst bar is full. Can be either "true" or "false". (Without the quotation marks)

**_Recipe_**
- **item:** This is the item id of the item that creates the drink using a Drinks Brewer. Examples include: apple
- **item\_mod\_id:** This is only necessary if the ingredient item does not exist in Minecraft and is added through a mod. This can be found by either asking the mod developer or reading through the mods "mcmod.info" file. Examples include: thirstmod
- **item\_metadata:** This is also only necessary if the item is a subtype. Usually an integer. Ask the mod author or consult the Minecraft wiki for this number. Examples include: 1

**_Thirst Stats_**
- **bar\_heal:** This is an integer between -20 and 20 that determines how much hydration the drink restores upon being drunk. One droplet is 2 here. 20 symbolizes 10 droplets. A negative number will remove hydration from the thirst bar. 
- **saturation\_heal:** This is a value that determines how slowly the thirst bar can drop again. It accumulates over every drink that is consumed. This can be a decimal value. Examples include: 1.6
- **poison\_chance:** A value between 0 and 1 that determines the chance in percentage for thirst poison. 0.5 represents 50%.
- **potion\_cure:** This is a "true" or "false" value that determines whether it removes all potions from the player upon drinking. 

**_Hunger Stats_**
- **bar\_heal\_hunger:** Same as bar\_heal except this modifies the hunger bar. 
- **saturation\_heal\_hunger:** Same as saturation\_heal except this modifies the hunger bar.

**_Potion_**
- **id:** This is the id of a potion effect that is applied when the drink is consumed. [Read this wiki article for the ids.](http://minecraft.gamepedia.com/Status_effect)
- **duration:** This is the duration of the potion effect. It is an integer in the format of (time in seconds)*20. Examples include: 100

Not every element above is absolutely necessary. You can remove certain elements that are not necessary to a drink however ensure that **name**, **internal\_name**, **colour**, **stacksize**, **item**, **bar\_heal** and **saturation\_heal** are present in every single file.

Save this text file as [name of drink].txt in [minecraft-dir]/thirstmod/content/ or [minecraft-dir]/thirstmod/content/[drink-pack-name]/

# Making drinkable items from other mods replenish the Thirst Bar
1. First ensure that the item you want to add is drinkable i.e. plays the drinking animation. 
2. Copy the following line to a new [.txt] file. 
3. item\_id thirst\_replenish thirst\_saturation causes\_poison poison_chance metadata
4. Replace each element in the line above with the following:

- **item_id** = String (consult wiki or mod author for item id)
- **thirst_replenish** = Integer (one to twenty)
- **thirst_saturation** = Decimal (consult food wiki page for details)
- **causes_poison** = Boolean
- **poison_chance** = Decimal (0.1 to 0.9)
- **metadata = Integer** (consult wiki or mod author for item metadata)

Use the exact format displayed. Do NOT misplace the values or the game WILL crash.

Don't forget the spaces between each value!

One item per line in a [.txt] file in this folder. 
Save the file in [minecraft-dir]/thirstmod/externaldrinks/

# Credits
tarun1998 - For the initial mod. 

HunterzCZ - For some bug fixes. 
