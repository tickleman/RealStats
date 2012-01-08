package fr.crafter.tickleman.realstats;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

//################################################################################## PlayerListener
public class RealStatsPlayerListener extends PlayerListener
{

	RealStatsPlugin plugin;

	//----------------------------------------------------------------------- RealStatsPlayerListener
	public RealStatsPlayerListener(RealStatsPlugin plugin)
	{
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------ onPlayerInteractEntity
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		if (
			player instanceof Player
			&& entity != null
			&& player.getItemInHand().getType().equals(Material.WHEAT)
			&& entity.getClass().getName().contains("Craft")
		) {
			plugin.getPlayerStats(player).increment(RealPlayerStats.FEED, entity);
		}
	}

	//---------------------------------------------------------------------------------- onPlayerQuit
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		plugin.removePlayerStats(event.getPlayer());
	}

}
