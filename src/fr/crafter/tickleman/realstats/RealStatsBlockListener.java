package fr.crafter.tickleman.realstats;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

//################################################################################### BlockListener
public class RealStatsBlockListener extends BlockListener
{

	RealStatsPlugin plugin;

	//------------------------------------------------------------------------ RealStatsBlockListener
	public RealStatsBlockListener(RealStatsPlugin plugin)
	{
		this.plugin = plugin;
	}

	//---------------------------------------------------------------------------------- onBlockBreak
	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if (player instanceof Player && player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.getPlayerStats(player).increment(RealPlayerStats.BREAK, event.getBlock());
		}
	}

	//---------------------------------------------------------------------------------- onBlockPlace
	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if (player instanceof Player) {
			plugin.getPlayerStats(player).increment(RealPlayerStats.PLACE, event.getBlock());
		}
	}

}
