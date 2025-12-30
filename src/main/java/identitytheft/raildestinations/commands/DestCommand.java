package identitytheft.raildestinations.commands;

import com.google.common.base.Strings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import identitytheft.raildestinations.util.IEntityDataSaver;
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
                .then(CommandManager.argument("destination", StringArgumentType.greedyString())
                        .executes(context -> run(context, StringArgumentType.getString(context, "destination")))));
    }

    private static int run(CommandContext<ServerCommandSource> context, @Nullable String dest) {
        var source = context.getSource();

        if (source.getEntity() instanceof ServerPlayerEntity serverPlayerEntity)
        {
            if (Strings.isNullOrEmpty(dest))
            {
				((IEntityDataSaver) serverPlayerEntity).rail_destinations$setDestination("");
                source.sendFeedback(() -> Text.literal("Unset destination (use /dest <destination> to set your destination)"), false);
                return 1;
            }

			if (!isDestValid(dest))
			{
				source.sendError(Text.literal("Each destination can not be more than 40 characters."));
				return 0;
			}

            source.sendFeedback(() -> Text.literal("Destination set to: " + dest), false);
			((IEntityDataSaver) serverPlayerEntity).rail_destinations$setDestination(dest);

            return 1;
        }

        source.sendError(Text.literal("Could not set your destination."));
        return 0;
    }

	private static boolean isDestValid(String dest)
	{
		for (var d : dest.split(" "))
			if (d.length() > 40) return false;

		return true;
	}
}