package fr.crafter.tickleman.realstats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftBlaze;
import org.bukkit.craftbukkit.entity.CraftBoat;
import org.bukkit.craftbukkit.entity.CraftCaveSpider;
import org.bukkit.craftbukkit.entity.CraftChicken;
import org.bukkit.craftbukkit.entity.CraftCow;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftEnderDragon;
import org.bukkit.craftbukkit.entity.CraftEnderman;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftMagmaCube;
import org.bukkit.craftbukkit.entity.CraftMinecart;
import org.bukkit.craftbukkit.entity.CraftMushroomCow;
import org.bukkit.craftbukkit.entity.CraftPig;
import org.bukkit.craftbukkit.entity.CraftPigZombie;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.entity.CraftSheep;
import org.bukkit.craftbukkit.entity.CraftSilverfish;
import org.bukkit.craftbukkit.entity.CraftSkeleton;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.craftbukkit.entity.CraftSpider;
import org.bukkit.craftbukkit.entity.CraftSquid;
import org.bukkit.craftbukkit.entity.CraftVillager;
import org.bukkit.craftbukkit.entity.CraftWolf;
import org.bukkit.craftbukkit.entity.CraftZombie;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

//##################################################################################### PlayerStats
public class RealPlayerStats
{

	private Player player;

	public static final int BREAK       = 1;
	public static final int FEED        = 2;
	public static final int HIT         = 3;
	public static final int KILL        = 4;
	public static final int LEFT_CLICK  = 5;
	public static final int MOVING      = 6;
	public static final int PLACE       = 7;
	public static final int RIGHT_CLICK = 8;
	public static final int TAME        = 9;

	public static final int MOVING_BOAT   = 1;
	public static final int MOVING_CART   = 2;
	public static final int MOVING_SNEAK  = 3;
	public static final int MOVING_SPRINT = 4;
	public static final int MOVING_WALK   = 5;

	public static final int CREATURES_COUNT = 23;
	public static final int VEHICLES_COUNT  = 6;

	private boolean mustSave = false;
	private long[] breakBlocks;
	private long[] feedCreatures;
	private long[] hitCreatures;
	private long[] killCreatures;
	private long[] leftClickBlocks;
	private long[] movingDistances;
	private long[] placeBlocks;
	private long[] rightClickBlocks;
	private long[] tameCreatures;

	//----------------------------------------------------------------------------------- PlayerStats
	public RealPlayerStats(RealStatsPlugin plugin, Player player)
	{
		this.player      = player;
		breakBlocks      = new long[net.minecraft.server.Block.byId.length + 1];
		feedCreatures    = new long[CREATURES_COUNT];
		hitCreatures     = new long[CREATURES_COUNT];
		killCreatures    = new long[CREATURES_COUNT];
		leftClickBlocks  = new long[net.minecraft.server.Block.byId.length + 1];
		movingDistances  = new long[VEHICLES_COUNT];
		placeBlocks      = new long[net.minecraft.server.Block.byId.length + 1];
		rightClickBlocks = new long[net.minecraft.server.Block.byId.length + 1];
		tameCreatures    = new long[CREATURES_COUNT];
		load(plugin);
	}

	//------------------------------------------------------------------------------ actionFromString
	public static int actionFromString(String string)
	{
		if (string.equals("break"))      return BREAK;
		if (string.equals("feed"))       return FEED;
		if (string.equals("hit"))        return HIT;
		if (string.equals("kill"))       return KILL;
		if (string.equals("leftclick"))  return LEFT_CLICK;
		if (string.equals("moving"))     return MOVING;
		if (string.equals("place"))      return PLACE;
		if (string.equals("rightclick")) return RIGHT_CLICK;
		if (string.equals("tame"))       return TAME;
		return 0;
	}

	//-------------------------------------------------------------------------------------- autoSave
	public void autoSave(RealStatsPlugin plugin)
	{
		if (mustSave) {
			save(plugin);
			mustSave = false;
		}
	}

	//--------------------------------------------------------------------------------- getCreatureId
	public int getCreatureId(Class<? extends Entity> entityClass)
	{
		if (entityClass.equals(CraftChicken.class))     return 1;
		if (entityClass.equals(CraftCow.class))         return 2;
		if (entityClass.equals(CraftPig.class))         return 3;
		if (entityClass.equals(CraftSheep.class))       return 4;
		if (entityClass.equals(CraftWolf.class))        return 5;

		if (entityClass.equals(CraftBlaze.class))       return 6;
		if (entityClass.equals(CraftCaveSpider.class))  return 7;
		if (entityClass.equals(CraftCreeper.class))     return 8;
		if (entityClass.equals(CraftEnderman.class))    return 9;
		if (entityClass.equals(CraftGhast.class))       return 10;
		if (entityClass.equals(CraftMagmaCube.class))   return 11;
		if (entityClass.equals(CraftMushroomCow.class)) return 12;
		if (entityClass.equals(CraftPigZombie.class))   return 13;
		if (entityClass.equals(CraftSilverfish.class))  return 14;
		if (entityClass.equals(CraftSkeleton.class))    return 15;
		if (entityClass.equals(CraftSlime.class))       return 16;
		if (entityClass.equals(CraftSpider.class))      return 17;
		if (entityClass.equals(CraftSquid.class))       return 18;
		if (entityClass.equals(CraftZombie.class))      return 19;

		if (entityClass.equals(CraftVillager.class))    return 20;
		if (entityClass.equals(CraftEnderDragon.class)) return 21;

		if (entityClass.equals(CraftPlayer.class))                  return 22;
		if (entityClass.getSimpleName().equals("SpoutCraftPlayer")) return 22;
		
		System.out.println("RealStats unknown creature class " + entityClass.getSimpleName());

		return 0;
	}

	//---------------------------------------------------------------------------------- getVehicleId
	public int getVehicleId(Class<? extends Entity> vehicleClass)
	{
		if (vehicleClass.equals(CraftBoat.class))     return MOVING_BOAT;
		if (vehicleClass.equals(CraftMinecart.class)) return MOVING_CART;
		return 0;
	}

	//----------------------------------------------------------------------------------------- getXp
	public long getXp(int action, int typeId)
	{
		try {
			switch (action) {
				case BREAK:       return breakBlocks     [typeId];
				case LEFT_CLICK:  return leftClickBlocks [typeId];
				case FEED:        return feedCreatures   [typeId];
				case HIT:         return hitCreatures    [typeId];
				case KILL:        return killCreatures   [typeId];
				case MOVING:      return movingDistances [typeId];
				case PLACE:       return placeBlocks     [typeId];
				case RIGHT_CLICK: return rightClickBlocks[typeId];
				case TAME:        return tameCreatures   [typeId];
			}
		} catch (ArrayIndexOutOfBoundsException e) {
		}
		return 0;
	}

	//------------------------------------------------------------------------------------- increment
	public void increment(int action, Block block)
	{
		switch (action) {
			case BREAK:       breakBlocks      [block.getTypeId()] ++;
			case LEFT_CLICK:  leftClickBlocks  [block.getTypeId()] ++;
			case PLACE:       placeBlocks      [block.getTypeId()] ++;
			case RIGHT_CLICK: rightClickBlocks [block.getTypeId()] ++;
		}
		mustSave = true;
	}

	//------------------------------------------------------------------------------------- increment
	public void increment(int action, Entity entity)
	{
		switch (action) {
			case FEED: feedCreatures[getCreatureId(entity.getClass())] ++; break;
			case HIT:  hitCreatures [getCreatureId(entity.getClass())] ++; break;
			case KILL: killCreatures[getCreatureId(entity.getClass())] ++; break;
			case TAME: tameCreatures[getCreatureId(entity.getClass())] ++; break;
		}
		mustSave = true;
	}

	//------------------------------------------------------------------------------------- increment
	public void increment(int action, Vehicle vehicle, long duration)
	{
		if (action == MOVING) movingDistances[getVehicleId(vehicle.getClass())] += duration;
		mustSave = true;
	}

	//------------------------------------------------------------------------------------- increment
	public void increment(int action, int typeId, long duration)
	{
		if (action == MOVING) movingDistances[typeId] += duration;
		mustSave = true;
	}

	//------------------------------------------------------------------------------------------ load
	public void load(RealStatsPlugin plugin)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
				plugin.getDataFolder() + "/" + player.getName() + ".txt")
			);
			String buffer;
			String[] list;
			while ((buffer = reader.readLine()) != null) {
				int equalPos = buffer.indexOf('=');
				if (equalPos > 0) {
					String key = buffer.substring(0, equalPos);
					buffer = buffer.substring(key.length() + 1);
					list = buffer.split(";");
					loadLongList(key, list, "break",      breakBlocks);
					loadLongList(key, list, "feed",       feedCreatures);
					loadLongList(key, list, "hit",        hitCreatures);
					loadLongList(key, list, "kill",       killCreatures);
					loadLongList(key, list, "leftclick",  leftClickBlocks);
					loadLongList(key, list, "moving",     movingDistances);
					loadLongList(key, list, "place",      placeBlocks);
					loadLongList(key, list, "rightclick", rightClickBlocks);
					loadLongList(key, list, "tame",       tameCreatures);
				}
			}
			reader.close();
		} catch (Exception e) {
			plugin.log(
				Level.INFO, "Write default " + plugin.getDataFolder() + "/config.txt file"
			);
			save(plugin);
		}
	}

	//---------------------------------------------------------------------------------- loadLongList
	public void loadLongList(String key, String[] readList, String listName, long[] intList)
	{
		if (key.equals(listName)) {
			for (int i = 0; i < Math.min(intList.length, readList.length); i++) {
				intList[i] = Long.parseLong(readList[i]);
			}
		}
	}

	//------------------------------------------------------------------------------------------ save
	public void save(RealStatsPlugin plugin)
	{
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
				plugin.getDataFolder() + "/" + player.getName() + ".txt"
			));
			saveLongList(writer, "break",      breakBlocks);
			saveLongList(writer, "feed",       feedCreatures);
			saveLongList(writer, "hit",        hitCreatures);
			saveLongList(writer, "kill",       killCreatures);
			saveLongList(writer, "leftclick",  leftClickBlocks);
			saveLongList(writer, "moving",     movingDistances);
			saveLongList(writer, "place",      placeBlocks);
			saveLongList(writer, "rightclick", rightClickBlocks);
			saveLongList(writer, "tame",       tameCreatures);
			writer.close();
		} catch (Exception e) {
			plugin.log(
				Level.SEVERE, "Could not save " + plugin.getDataFolder() + "/" + player.getName() + ".txt"
			);
		}
	}

	//---------------------------------------------------------------------------------- saveLongList
	private void saveLongList(BufferedWriter writer, String listName, long[] list) throws IOException
	{
		String buffer = "";
		for (int i = 0; i < list.length; i++) {
			buffer = buffer + ";" + list[i];
		}
		writer.write(listName + "=" + buffer.substring(1) + "\n");
	}

}
