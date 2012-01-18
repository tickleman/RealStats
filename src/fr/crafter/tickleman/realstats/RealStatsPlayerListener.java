package fr.crafter.tickleman.realstats;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import fr.crafter.tickleman.realplugin.RealLocation;

//################################################################################## PlayerListener
public class RealStatsPlayerListener extends PlayerListener
{

	RealStatsPlugin plugin;

	//----------------------------------------------------------------------- RealStatsPlayerListener
	public RealStatsPlayerListener(RealStatsPlugin plugin)
	{
		this.plugin = plugin;
	}

	//------------------------------------------------------------------------------ onPlayerInteract
	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Block  block = event.getClickedBlock();
		if (player instanceof Player && block instanceof Block) {
			Action action = event.getAction();
			if (action.equals(Action.LEFT_CLICK_BLOCK)) {
				plugin.getPlayerStats(player).increment(RealPlayerStats.LEFT_CLICK, block);
			} else if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
				plugin.getPlayerStats(player).increment(RealPlayerStats.RIGHT_CLICK, block);
			}
		}
	}

	//------------------------------------------------------------------------ onPlayerInteractEntity
	@Override
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		if (
			player instanceof Player
			&& (entity != null)
			&& entity.getClass().getName().contains("Craft")
			&& player.getItemInHand().getType().equals(Material.WHEAT)
		) {
			plugin.getPlayerStats(player).increment(RealPlayerStats.FEED, entity);
		}
	}

	//---------------------------------------------------------------------------------- onPlayerMove
	@Override
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		if ((player instanceof Player) && player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.addToPlayerDistance(
				player, RealLocation.calculateDistance(event.getFrom(), event.getTo())
			);
		}
	}

	//---------------------------------------------------------------------------------- onPlayerQuit
	@Override
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		plugin.removePlayerStats(event.getPlayer());
	}

	//--------------------------------------------------------------------------- onPlayerToggleSneak
	@Override
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event)
	{
		if (!event.getPlayer().isInsideVehicle()) {
			if (event.isSneaking()) {
				plugin.setPlayerMovingType(event.getPlayer(), RealPlayerStats.MOVING_SNEAK);
			} else {
				plugin.setPlayerMovingType(event.getPlayer(), RealPlayerStats.MOVING_WALK);
			}
		}
	}

	//-------------------------------------------------------------------------- onPlayerToggleSprint
	@Override
	public void onPlayerToggleSprint(PlayerToggleSprintEvent event)
	{
		if (!event.getPlayer().isInsideVehicle()) {
			if (event.isSprinting()) {
				plugin.setPlayerMovingType(event.getPlayer(), RealPlayerStats.MOVING_SPRINT);
			} else {
				plugin.setPlayerMovingType(event.getPlayer(), RealPlayerStats.MOVING_WALK);
			}
		}
	}

}
