package identitytheft.raildestinations;

import com.mojang.serialization.Codec;
import identitytheft.raildestinations.commands.DestCommand;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Supplier;

@Mod(RailDestinations.MOD_ID)
public class RailDestinations {
    public static final String MOD_ID = "rail_destinations";
    public static final Logger LOGGER = LogUtils.getLogger();

	private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

	public static final Supplier<AttachmentType<String>> DESTINATION = ATTACHMENT_TYPES.register(
			"data", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING.fieldOf("destination")).build()
	);

    public RailDestinations(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
		ATTACHMENT_TYPES.register(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
		LOGGER.info("Starting Rail Destinations!");
    }

	@SubscribeEvent
	public void registerCommands(RegisterCommandsEvent event)
	{
		DestCommand.register(event.getDispatcher());
	}

	@SubscribeEvent
	public void onPlayerCloned(PlayerEvent.Clone event) {
		if (event.isWasDeath() && event.getOriginal().hasData(DESTINATION)) {
			event.getEntity().setData(DESTINATION, event.getOriginal().getData(DESTINATION));
		}
	}
}
