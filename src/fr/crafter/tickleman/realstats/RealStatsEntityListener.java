package fr.crafter.tickleman.realstats;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTameEvent;

//######################################################################### RealStatsEntityListener
public class RealStatsEntityListener extends EntityListener
{

	RealStatsPlugin plugin;

	//----------------------------------------------------------------------- RealStatsEntityListener
	public RealStatsEntityListener(RealStatsPlugin plugin)
	{
		this.plugin = plugin;
	}

	//---------------------------------------------------------------------------------- onEntityTame
	@Override
	public void onEntityTame(EntityTameEvent event)
	{
		Player player = (Player)event.getOwner();
		if (player instanceof Player && player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.getPlayerStats(player).increment(RealPlayerStats.TAME, event.getEntity());
		}
	}

}

