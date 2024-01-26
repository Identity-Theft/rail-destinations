package identitytheft.rail_destinations.commands;

import com.google.common.base.Strings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import identitytheft.rail_destinations.util.DestinationData;
import identitytheft.rail_destinations.util.IEntityDataSaver;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class DestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment registrationEnvironment)
    {
        dispatcher.register(CommandManager.literal("dest").executes(context -> run(context, null))
                .then(CommandManager.argument("destination", StringArgumentType.string())
                        .executes(context -> run(context, StringArgumentType.getString(context, "destination")))));
    }

    private static int run(CommandContext<ServerCommandSource> context, @Nullable String dest) throws CommandSyntaxException {
        var source = context.getSource();

        if (source.getEntity() instanceof ServerPlayerEntity serverPlayerEntity)
        {
            if (Strings.isNullOrEmpty(dest))
            {
                // Get the player's destination if none was entered
                var currentDest = DestinationData.getDest((IEntityDataSaver) serverPlayerEntity);
                source.sendFeedback(() -> Text.literal("Your current destination is: " + currentDest), false);
                return 1;
            }

            // Set the player's destination
            source.sendFeedback(() -> Text.literal("Destination set to: " + dest), false);
            DestinationData.setDest((IEntityDataSaver) serverPlayerEntity, dest);

            return 1;
        }

        source.sendError(Text.literal("Could not set your destination."));
        return -1;
    }
}
