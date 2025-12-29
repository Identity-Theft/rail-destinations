package identitytheft.raildestinations.commands;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import identitytheft.raildestinations.destination.PlayerDestinationProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class DestCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("dest").executes(context -> dest(context.getSource(), null))
				.then(Commands.argument("destination", StringArgumentType.greedyString())
						.executes(context -> dest(context.getSource(), StringArgumentType.getString(context, "destination")))));
	}

	private static int dest(CommandSourceStack source, @Nullable String dest)
	{
		if (source.getEntity() instanceof ServerPlayer serverPlayer)
		{
			serverPlayer.getCapability(PlayerDestinationProvider.PLAYER_DEST).ifPresent(playerDestination -> {
				if (Strings.isNullOrEmpty(dest))
				{
					playerDestination.setDest("");
					source.sendSuccess(() -> Component.literal("Unset destination (use /dest <destination> to set your destination)"), false);
					return;
				}

				if (!isDestValid(dest))
				{
					source.sendFailure(Component.literal("Each destination can not be more than 40 characters and may only use alphanumerical characters, ASCII symbols, and spaces."));
					return;
				}

				playerDestination.setDest(dest);
				source.sendSuccess(() -> Component.literal("Destination set to: " + dest), false);
			});


			if (serverPlayer.getCapability(PlayerDestinationProvider.PLAYER_DEST).isPresent()) return 1;
		}

		source.sendFailure(Component.literal("Failed to set destination"));
		return 0;
	}

	private static boolean isDestValid(String dest)
	{
		for (var d : dest.split(" "))
			if (d.length() > 40) return false;

		return CharMatcher.inRange('0', '9')
				.or(CharMatcher.inRange('a', 'z'))
				.or(CharMatcher.inRange('A', 'Z'))
				.or(CharMatcher.anyOf("!\"#$%&'()*+,-./;:<=>?@[]\\^_`{|}~ ")).matchesAllOf(dest);
	}
}