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

	private Map<Player, Double>          playersDistance   = new HashMap<Player, Double>();
	private Map<Player, Integer>         playersMovingType = new HashMap<Player, Integer>();
	private Map<Player, RealPlayerStats> playersStats      = new HashMap<Player, RealPlayerStats>();

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
			getPlayerStats(player).increment(
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
		RealStatsBlockListener   blockListener   = new RealStatsBlockListener(this);
		RealStatsEntityListener  entityListener  = new RealStatsEntityListener(this);
		RealStatsPlayerListener  playerListener  = new RealStatsPlayerListener(this);
		RealStatsVehicleListener vehicleListener = new RealStatsVehicleListener(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.BLOCK_BREAK,            blockListener,   Priority.Lowest, this);
		pm.registerEvent(Type.BLOCK_PLACE,            blockListener,   Priority.Lowest, this);
		pm.registerEvent(Type.ENTITY_TAME,            entityListener,  Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_INTERACT,        playerListener,  Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_INTERACT_ENTITY, playerListener,  Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_MOVE,            playerListener,  Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_QUIT,            playerListener,  Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_TOGGLE_SPRINT,   playerListener,  Priority.Lowest, this);
		pm.registerEvent(Type.PLAYER_TOGGLE_SNEAK,    playerListener,  Priority.Lowest, this);
		pm.registerEvent(Type.VEHICLE_ENTER,          vehicleListener, Priority.Lowest, this);
		pm.registerEvent(Type.VEHICLE_EXIT,           vehicleListener, Priority.Lowest, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(
			this, new Runnable() { public void run() { autoSave(); } }, 1200L, 1200L
		);
	}

	//----------------------------------------------------------------------------- removePlayerStats
	public void removePlayerStats(Player player)
	{
		flushPlayerDistance(player);
		RealPlayerStats playerStats = playersStats.get(player);
		if (playerStats != null) {
			playerStats.save(this);
		}
		playersDistance.remove(player);
		playersMovingType.remove(player);
		playersStats.remove(player);
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
