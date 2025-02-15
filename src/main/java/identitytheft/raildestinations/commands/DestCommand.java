package identitytheft.raildestinations.commands;

import com.google.common.base.Strings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import identitytheft.raildestinations.destination.PlayerDestinationProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class DestCommand {
    public DestCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("dest").executes(context -> run(context.getSource(), null))
                .then(Commands.argument("destination", StringArgumentType.string())
                        .executes(context -> run(context.getSource(), StringArgumentType.getString(context, "destination")))));
    }

    private int run(CommandSourceStack source, @Nullable String dest) {
        if (source.getEntity() instanceof ServerPlayer serverPlayer)
        {
            serverPlayer.getCapability(PlayerDestinationProvider.PLAYER_DEST).ifPresent(playerDestination -> {
                if (Strings.isNullOrEmpty(dest))
                {
                    // Get the player's destination if none was entered
                    var currentDest = playerDestination.getDest();
                    source.sendSuccess(() -> Component.literal("Your current destination is: " + currentDest), false);
                    return;
                }

                // Set the player's destination
                source.sendSuccess(() -> Component.literal("Destination set to: " + dest), false);
                playerDestination.setDest(dest);
            });

            if (serverPlayer.getCapability(PlayerDestinationProvider.PLAYER_DEST).isPresent()) return 1;
        }

        source.sendFailure(Component.literal("Could not set your destination."));
        return -1;
    }
}
