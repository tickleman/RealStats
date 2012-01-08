package fr.crafter.tickleman.realstats;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;

import fr.crafter.tickleman.realplugin.RealPlugin;

//########################################################################### RealPlayerStatsPlugin
public class RealStatsPlugin extends RealPlugin
{

	Map<Player, RealPlayerStats> playersStats = new HashMap<Player, RealPlayerStats>();

	//-------------------------------------------------------------------------------------- autoSave
	public void autoSave()
	{
		for (RealPlayerStats playerStat : playersStats.values()) {
			playerStat.autoSave(this);
		}
	}

	//----------------------------------------------------------------------------------- PlayerStats
	public RealPlayerStats getPlayerStats(Player player)
	{
		RealPlayerStats playerStats = playersStats.get(player);
		if (playerStats == null) {
			playerStats = new RealPlayerStats(this, player);
			playersStats.put(player, playerStats);
		}
		return playerStats;
	}

	//------------------------------------------------------------------------------------------- log
	public void log(Level level, String message)
	{
		getServer().getLogger().log(level, "[" + getDescription().getName() + "] " + message);
	}

	//------------------------------------------------------------------------------------- onDisable
	@Override
	public void onDisable()
	{
		autoSave();
		super.onDisable();
	}

	//-------------------------------------------------------------------------------------- onEnable
	@Override
	public void onEnable()
	{
		super.onEnable();
		RealStatsBlockListener  blockListener  = new RealStatsBlockListener(this);
		RealStatsEntityListener entityListener = new RealStatsEntityListener(this);
		RealStatsPlayerListener playerListener = new RealStatsPlayerListener(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.BLOCK_BREAK,            blockListener, Priority.Lowest, this);
		pm.registerEvent(Type.BLOCK_PLACE,            blockListener, Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, playerListener, Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_QUIT,            playerListener, Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_TAME,            entityListener, Priority.Lowest, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(
			this, new Runnable() { public void run() { autoSave(); } }, 1200L, 1200L
		);
	}

	//----------------------------------------------------------------------------- removePlayerStats
	public void removePlayerStats(Player player)
	{
		RealPlayerStats playerStats = playersStats.get(player);
		if (playerStats != null) {
			playerStats.save(this);
			playersStats.remove(player);
		}
	}

}
