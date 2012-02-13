package fr.crafter.tickleman.realstats;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import fr.crafter.tickleman.realplugin.RealPlugin;

//########################################################################### RealPlayerStatsPlugin
public class RealStatsPlugin extends RealPlugin
{

	private Map<Player, Double>          playersDistance   = new HashMap<Player, Double>();
	private Map<Player, Integer>         playersMovingType = new HashMap<Player, Integer>();
	private Map<String, RealPlayerStats> playersStats      = new HashMap<String, RealPlayerStats>();

	//--------------------------------------------------------------------------- addToPlayerDistance
	public void addToPlayerDistance(Player player, double distance)
	{
		playersDistance.put(player, getPlayerDistance(player) + distance);
	}

	//-------------------------------------------------------------------------------------- autoSave
	public void autoSave()
	{
		flushPlayersDistance();
		for (RealPlayerStats playerStat : playersStats.values()) {
			playerStat.autoSave(this);
		}
	}

	//-------------------------------------------------------------------------- flushPlayersDistance
	public void flushPlayersDistance()
	{
		for (Player player : playersDistance.keySet()) {
			flushPlayerDistance(player);
		}
	}

	//--------------------------------------------------------------------------- flushPlayerDistance
	public void flushPlayerDistance(Player player)
	{
		Double distance = getPlayerDistance(player);
		if (distance > 0.0) {
			getPlayerStats(player.getName()).increment(
				RealPlayerStats.MOVING,
				getPlayerMovingType(player),
				Math.round(distance)
			);
			playersDistance.put(player, 0.0);
		}
	}

	//----------------------------------------------------------------------------- getPlayerDistance
	public Double getPlayerDistance(Player player)
	{
		Double distance = playersDistance.get(player);
		if (distance == null) {
			distance = new Double(0.0);
			playersDistance.put(player, distance);
		}
		return distance;
	}

	//--------------------------------------------------------------------------- getPlayerMovingType
	public Integer getPlayerMovingType(Player player)
	{
		Integer moving = playersMovingType.get(player);
		if (moving == null) {
			moving = new Integer(RealPlayerStats.MOVING_WALK);
			playersMovingType.put(player, moving);
		}
		return moving;
	}

	//-------------------------------------------------------------------------------- getPlayerStats
	public RealPlayerStats getPlayerStats(String playerName)
	{
		RealPlayerStats playerStats = playersStats.get(playerName);
		if (playerStats == null) {
			playerStats = new RealPlayerStats(this, playerName);
			playersStats.put(playerName, playerStats);
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
		getServer().getPluginManager().registerEvents(new RealStatsListeners(this), this);
		getServer().getScheduler().scheduleSyncRepeatingTask(
			this, new Runnable() { public void run() { autoSave(); } }, 1200L, 1200L
		);
	}

	//----------------------------------------------------------------------------- removePlayerStats
	public void removePlayerStats(Player player)
	{
		flushPlayerDistance(player);
		RealPlayerStats playerStats = playersStats.get(player.getName());
		if (playerStats != null) {
			playerStats.save(this);
		}
		playersDistance.remove(player);
		playersMovingType.remove(player);
		playersStats.remove(player.getName());
	}

	//--------------------------------------------------------------------------- setPlayerMovingType
	public void setPlayerMovingType(Player player, int newMovingType)
	{
		int movingType = getPlayerMovingType(player);
		if (newMovingType != movingType) {
			flushPlayerDistance(player);
			playersMovingType.put(player, newMovingType);
		}
	}

}
