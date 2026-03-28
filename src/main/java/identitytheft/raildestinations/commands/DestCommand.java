package identitytheft.raildestinations.commands;

import com.google.common.base.Strings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import identitytheft.raildestinations.util.IEntityDataSaver;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public class DestCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("dest").executes(context -> run(context, null))
                .then(Commands.argument("destination", StringArgumentType.greedyString())
                        .executes(context -> run(context, StringArgumentType.getString(context, "destination")))));
    }

    private static int run(CommandContext<CommandSourceStack> context, @Nullable String dest) {
        var source = context.getSource();

        if (source.getEntity() instanceof ServerPlayer serverPlayer)
        {
            if (Strings.isNullOrEmpty(dest))
            {
				((IEntityDataSaver) serverPlayer).rail_destinations$setDestination("");
                source.sendSuccess(() -> Component.translatable("rail-destination.unset"), false);
                return 1;
            }

			if (!isDestValid(dest))
			{
				source.sendFailure(Component.translatable("rail-destination.too_long"));
				return 0;
			}

            source.sendSuccess(() -> Component.translatable("rail-destination.set", dest), false);
			((IEntityDataSaver) serverPlayer).rail_destinations$setDestination(dest);

            return 1;
        }

        source.sendFailure(Component.translatable("rail-destination.failed"));
        return 0;
    }

	private static boolean isDestValid(String dest)
	{
		for (var d : dest.split(" "))
			if (d.length() > 40) return false;

		return true;
	}
}