package fr.crafter.tickleman.realstats;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;

import fr.crafter.tickleman.realplugin.RealLocation;

//############################################################################## RealStatsListeners
public class RealStatsListeners implements Listener
{

	RealStatsPlugin plugin;
	Map<Entity, Player> lastAttacker = new HashMap<Entity, Player>();

	//---------------------------------------------------------------------------- RealStatsListeners
	public RealStatsListeners(RealStatsPlugin plugin)
	{
		this.plugin = plugin;
	}

	//---------------------------------------------------------------------------------- onBlockBreak
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if (player instanceof Player && player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.getPlayerStats(player.getName()).increment(RealPlayerStats.BREAK, event.getBlock());
		}
	}

	//---------------------------------------------------------------------------------- onBlockPlace
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if (player instanceof Player) {
			plugin.getPlayerStats(player.getName()).increment(RealPlayerStats.PLACE, event.getBlock());
		}
	}

	//-------------------------------------------------------------------------------- onEntityDamage
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent finalEvent = (EntityDamageByEntityEvent)event;
			if (finalEvent.getDamager() instanceof Player) {
				Entity entity = event.getEntity();
				Player player = (Player)finalEvent.getDamager();
				lastAttacker.put(entity, player);
				plugin.getPlayerStats(player.getName()).increment(RealPlayerStats.HIT, entity);
			}
		}
	}

	//--------------------------------------------------------------------------------- onEntityDeath
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent event)
	{
		Entity entity = event.getEntity();
		Player player = lastAttacker.get(entity);
		if (player != null) {
			lastAttacker.remove(entity);
			plugin.getPlayerStats(player.getName()).increment(RealPlayerStats.KILL, entity);
		}
	}

	//---------------------------------------------------------------------------------- onEntityTame
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityTame(EntityTameEvent event)
	{
		Player player = (Player)event.getOwner();
		if (player instanceof Player && player.getGameMode().equals(GameMode.SURVIVAL)) {
			plugin.getPlayerStats(player.getName()).increment(RealPlayerStats.TAME, event.getEntity());
		}
	}

	//------------------------------------------------------------------------------ onInventoryClick
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClick(InventoryClickEvent event)
	{
		Player player = event.getPlayer();
		if (player instanceof Player) {
		}
	}

	//------------------------------------------------------------------------------ onPlayerInteract
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Block  block = event.getClickedBlock();
		if ((player instanceof Player) && (block instanceof Block)) {
			Action action = event.getAction();
			if (action.equals(Action.LEFT_CLICK_BLOCK)) {
				plugin.getPlayerStats(player.getName()).increment(RealPlayerStats.LEFT_CLICK, block);
			} else if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
				plugin.getPlayerStats(player.getName()).increment(RealPlayerStats.RIGHT_CLICK, block);
			}
		}
	}

	//------------------------------------------------------------------------ onPlayerInteractEntity
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		if (
			player instanceof Player
			&& (entity != null)
			&& entity.getClass().getName().contains("Craft")
		) {
			if (player.getItemInHand() != null) {
				Material itemInHandType = player.getItemInHand().getType();
				if (itemInHandType.equals(Material.WHEAT)) {
					if (entity instanceof Animals) {
						Animals animal = (Animals)entity;
						if (animal.canBreed()) {
							plugin.getPlayerStats(player.getName()).increment(RealPlayerStats.FEED, entity);
						}
					}
				} else if (itemInHandType.equals(Material.SHEARS)) {
					if (entity instanceof Sheep) {
						Sheep sheep = (Sheep)entity;
						if (!sheep.isSheared()) {
							plugin.getPlayerStats(player.getName()).increment(RealPlayerStats.CUT, entity);
						}
					}
				}
			}
		}
	}

	//---------------------------------------------------------------------------------- onPlayerMove
	@EventHandler(priority = EventPriority.LOWEST)
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
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		plugin.removePlayerStats(event.getPlayer());
	}

	//--------------------------------------------------------------------------- onPlayerToggleSneak
	@EventHandler(priority = EventPriority.LOWEST)
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
	@EventHandler(priority = EventPriority.LOWEST)
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

	//-------------------------------------------------------------------------------- onVehicleEnter
	@EventHandler(priority = EventPriority.LOWEST)
	public void onVehicleEnter(VehicleEnterEvent event)
	{
		Player player = (Player)event.getEntered();
		if (player instanceof Player) {
			if (event.getVehicle() instanceof Boat) {
				plugin.setPlayerMovingType(player, RealPlayerStats.MOVING_BOAT);
			} else if (event.getVehicle() instanceof Minecart) {
				plugin.setPlayerMovingType(player, RealPlayerStats.MOVING_CART);
			}
		}
	}

	//--------------------------------------------------------------------------------- onVehicleExit
	@EventHandler(priority = EventPriority.LOWEST)
	public void onVehicleExit(VehicleExitEvent event)
	{
		Player player = (Player)event.getExited();
		if (player instanceof Player) {
			plugin.setPlayerMovingType(player, RealPlayerStats.MOVING_WALK);
		}
	}

}
