package identitytheft.raildestinations.events;

import identitytheft.raildestinations.RailDestinations;
import identitytheft.raildestinations.commands.DestCommand;
import identitytheft.raildestinations.destination.PlayerDestination;
import identitytheft.raildestinations.destination.PlayerDestinationProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = RailDestinations.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        new DestCommand(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onAttachPlayerCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof Player)
        {
            if (!event.getObject().getCapability(PlayerDestinationProvider.PLAYER_DEST).isPresent())
            {
                event.addCapability(new ResourceLocation(RailDestinations.MOD_ID, "properties"), new PlayerDestinationProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerDestinationProvider.PLAYER_DEST).ifPresent(oldStore -> {
                event.getOriginal().getCapability(PlayerDestinationProvider.PLAYER_DEST).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event)
    {
        event.register(PlayerDestination.class);
    }
}
