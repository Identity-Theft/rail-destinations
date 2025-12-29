package identitytheft.raildestinations;

import com.mojang.logging.LogUtils;
import identitytheft.raildestinations.commands.DestCommand;
import identitytheft.raildestinations.destination.PlayerDestination;
import identitytheft.raildestinations.destination.PlayerDestinationProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(RailDestinations.MOD_ID)
public class RailDestinations
{
    public static final String MOD_ID = "rail_destinations";
	public static final Logger LOGGER = LogUtils.getLogger();

    public RailDestinations(FMLJavaModLoadingContext context)
    {
		IEventBus modEventBus = context.getModEventBus();
		modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

	private void commonSetup(final FMLCommonSetupEvent event)
	{
		LOGGER.info("Starting Rail Destinations!");
	}

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
		DestCommand.register(event.getDispatcher());
    }

	@SubscribeEvent
	public void onAttachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event)
	{
		if (event.getObject() instanceof Player)
		{
			if (!event.getObject().getCapability(PlayerDestinationProvider.PLAYER_DEST).isPresent())
				event.addCapability(new ResourceLocation(RailDestinations.MOD_ID, "properties"), new PlayerDestinationProvider());
		}
	}

	@SubscribeEvent
	public void onPlayerCloned(PlayerEvent.Clone event) {
		if (event.isWasDeath()) {
			event.getOriginal().getCapability(PlayerDestinationProvider.PLAYER_DEST).ifPresent(oldStore -> {
				event.getOriginal().getCapability(PlayerDestinationProvider.PLAYER_DEST).ifPresent(newStore -> {
					newStore.copyFrom(oldStore);
				});
			});
		}
	}

	@SubscribeEvent
	public void onRegisterCapabilities(RegisterCapabilitiesEvent event)
	{
		event.register(PlayerDestination.class);
	}
}
