package fr.crafter.tickleman.realstats;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityTameEvent;

//######################################################################### RealStatsEntityListener
public class RealStatsEntityListener extends EntityListener
{

	RealStatsPlugin plugin;
	Map<Entity, Player> lastAttacker = new HashMap<Entity, Player>();

	//----------------------------------------------------------------------- RealStatsEntityListener
	public RealStatsEntityListener(RealStatsPlugin plugin)
	{
		this.plugin = plugin;
	}

	//-------------------------------------------------------------------------------- onEntityDamage
	@Override
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent finalEvent = (EntityDamageByEntityEvent)event;
			if (finalEvent.getDamager() instanceof Player) {
				Entity entity = event.getEntity();
				Player player = (Player)finalEvent.getDamager();
				lastAttacker.put(entity, player);
				plugin.getPlayerStats(player).increment(RealPlayerStats.HIT, entity);
			}
		}
	}

	//--------------------------------------------------------------------------------- onEntityDeath
	@Override
	public void onEntityDeath(EntityDeathEvent event)
	{
		Entity entity = event.getEntity();
		Player player = lastAttacker.get(entity);
		if (player != null) {
			lastAttacker.remove(entity);
			plugin.getPlayerStats(player).increment(RealPlayerStats.KILL, entity);
		}
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

