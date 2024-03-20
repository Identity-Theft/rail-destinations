package identitytheft.rail_destinations;

import identitytheft.rail_destinations.commands.DestCommand;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RailDestinations implements ModInitializer {
	public static final String MOD_ID = "rail_destinations";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Starting rail destinations");
		registerCommands();

	}

	private void registerCommands() {
		CommandRegistrationCallback.EVENT.register(DestCommand::register);
	}
}