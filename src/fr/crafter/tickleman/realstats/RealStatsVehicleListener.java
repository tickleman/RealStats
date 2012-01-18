package fr.crafter.tickleman.realstats;

import org.bukkit.craftbukkit.entity.CraftBoat;
import org.bukkit.craftbukkit.entity.CraftMinecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleListener;

//######################################################################## RealStatsVehicleListener
public class RealStatsVehicleListener extends VehicleListener
{

	private RealStatsPlugin plugin;

	//---------------------------------------------------------------------- RealStatsVehicleListener
	public RealStatsVehicleListener(RealStatsPlugin plugin)
	{
		this.plugin = plugin;
	}

	//-------------------------------------------------------------------------------- onVehicleEnter
	@Override
	public void onVehicleEnter(VehicleEnterEvent event)
	{
		Player player = (Player)event.getEntered();
		if (player instanceof Player) {
			Class<? extends Vehicle> vehicleClass = event.getVehicle().getClass();
			if (vehicleClass.equals(CraftBoat.class)) {
				plugin.setPlayerMovingType(player, RealPlayerStats.MOVING_BOAT);
			} else if (vehicleClass.equals(CraftMinecart.class)) {
				plugin.setPlayerMovingType(player, RealPlayerStats.MOVING_CART);
			}
		}
	}

	//--------------------------------------------------------------------------------- onVehicleExit
	@Override
	public void onVehicleExit(VehicleExitEvent event)
	{
		Player player = (Player)event.getExited();
		if (player instanceof Player) {
			plugin.setPlayerMovingType(player, RealPlayerStats.MOVING_WALK);
		}
	}

}
