package fr.crafter.tickleman.realstats;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftBlaze;
import org.bukkit.craftbukkit.entity.CraftCaveSpider;
import org.bukkit.craftbukkit.entity.CraftChicken;
import org.bukkit.craftbukkit.entity.CraftCow;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftEnderman;
import org.bukkit.craftbukkit.entity.CraftGhast;
import org.bukkit.craftbukkit.entity.CraftMagmaCube;
import org.bukkit.craftbukkit.entity.CraftMushroomCow;
import org.bukkit.craftbukkit.entity.CraftPig;
import org.bukkit.craftbukkit.entity.CraftPigZombie;
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

//##################################################################################### PlayerStats
public class RealPlayerStats
{


	private Player player;

	public static final int BREAK = 1;
	public static final int FEED = 2;
	public static final int PLACE = 3;
	public static final int TAME  = 4;

	private boolean mustSave = false;
	private int[] breakBlocks;
	private int[] feedCreatures;
	private int[] placeBlocks;
	private int[] tameCreatures;

	//----------------------------------------------------------------------------------- PlayerStats
	public RealPlayerStats(RealStatsPlugin plugin, Player player)
	{
		this.player   = player;
		breakBlocks   = new int[net.minecraft.server.Block.byId.length + 1];
		placeBlocks   = new int[net.minecraft.server.Block.byId.length + 1];
		feedCreatures = new int[getCreaturesCount()];
		tameCreatures = new int[getCreaturesCount()];
		load(plugin);
	}

	//-------------------------------------------------------------------------------------- autoSave
	public void autoSave(RealStatsPlugin plugin)
	{
		if (mustSave) {
			save(plugin);
			mustSave = false;
		}
	}

	//----------------------------------------------------------------------------- getCreaturesCount
	public int getCreaturesCount()
	{
		return 21;
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
		return 0;
	}

	//------------------------------------------------------------------------------------- increment
	public void increment(int action, Block block)
	{
		if      (action == BREAK) breakBlocks[block.getTypeId()] ++;
		else if (action == PLACE) placeBlocks[block.getTypeId()] ++;
		mustSave = true;
	}

	//------------------------------------------------------------------------------------- increment
	public void increment(int action, Entity entity)
	{
		if      (action == FEED) feedCreatures[getCreatureId(entity.getClass())] ++;
		else if (action == TAME) tameCreatures[getCreatureId(entity.getClass())] ++;
		mustSave = true;
	}

	//----------------------------------------------------------------------------------------- getXp
	public int getXp(int action, int typeId)
	{
		if      (action == BREAK) return breakBlocks[typeId];
		else if (action == FEED)  return feedCreatures[typeId];
		else if (action == PLACE) return placeBlocks[typeId];
		else if (action == TAME)  return tameCreatures[typeId];
		return 0;
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
			// breakBlockCounts
			while ((buffer = reader.readLine()) != null) {
				int equalPos = buffer.indexOf('=');
				if (equalPos > 0) {
					String key = buffer.substring(0, equalPos);
					buffer = buffer.substring(key.length() + 1);
					list = buffer.split(";");
					if (key.equals("break")) {
						for (int i = 0; i < Math.min(breakBlocks.length, list.length); i++) {
							breakBlocks[i] = Integer.parseInt(list[i]);
						}
					} else if (key.equals("feed")) {
						for (int i = 0; i < Math.min(feedCreatures.length, list.length); i++) {
							feedCreatures[i] = Integer.parseInt(list[i]);
						}
					} else if (key.equals("place")) {
						for (int i = 0; i < Math.min(placeBlocks.length, list.length); i++) {
							placeBlocks[i] = Integer.parseInt(list[i]);
						}
					} else if (key.equals("tame")) {
						for (int i = 0; i < Math.min(tameCreatures.length, list.length); i++) {
							tameCreatures[i] = Integer.parseInt(list[i]);
						}
					}
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

	//------------------------------------------------------------------------------------------ save
	public void save(RealStatsPlugin plugin)
	{
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(
				plugin.getDataFolder() + "/" + player.getName() + ".txt"
			));
			saveIntList(writer, "break", breakBlocks);
			saveIntList(writer, "feed",  feedCreatures);
			saveIntList(writer, "place", placeBlocks);
			saveIntList(writer, "tame",  tameCreatures);
			writer.close();
		} catch (Exception e) {
			plugin.log(
				Level.SEVERE, "Could not save " + plugin.getDataFolder() + "/" + player.getName() + ".txt"
			);
		}
	}

	//----------------------------------------------------------------------------------- saveIntList
	private void saveIntList(BufferedWriter writer, String listName, int[] list) throws IOException
	{
		String buffer = "";
		for (int i = 0; i < list.length; i++) {
			buffer = buffer + ";" + list[i];
		}
		writer.write(listName + "=" + buffer.substring(1) + "\n");
	}

}
