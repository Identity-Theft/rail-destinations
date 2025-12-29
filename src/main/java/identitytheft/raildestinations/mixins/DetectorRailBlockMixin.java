package identitytheft.raildestinations.mixins;

import identitytheft.raildestinations.destination.PlayerDestinationProvider;
import identitytheft.raildestinations.util.SwitchType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DetectorRailBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin {
	@Shadow
	protected abstract <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level pLevel, BlockPos pPos, Class<T> pCartType, Predicate<Entity> pFilter);

	@Shadow @Final
	public static BooleanProperty POWERED;

	@Inject(method = "checkPressed", at = @At("HEAD"), cancellable = true)
	public void rail_destinations$checkPressed(Level pLevel, BlockPos pPos, BlockState pState, CallbackInfo ci)
	{
		var thisRail = (DetectorRailBlock)(Object)this;
		var above = pLevel.getBlockState(pPos.above());

		if (above.is(BlockTags.SIGNS)) {
			var entity = (SignBlockEntity) pLevel.getBlockEntity(pPos.above());
			assert entity != null;
			var signText = entity.getFrontText().getMessages(false);

			// Use the sign's first line to determine if it's a switch
			var type = SwitchType.find(signText[0].getString().toLowerCase());

			if (type != null) {
				// Get list of carts on rail
				var carts = this.getInteractingMinecartOfType(pLevel, pPos, AbstractMinecart.class, (entity1 -> true));

				if (!carts.isEmpty() && carts.get(0).getFirstPassenger() instanceof Player playerEntity)
				{
					playerEntity.getCapability(PlayerDestinationProvider.PLAYER_DEST).ifPresent(playerDestination -> {
						// Update rail's state based on if player's destination matched
						BlockState blockState = pState.setValue(POWERED, (type == SwitchType.NORMAL) == rail_destinations$hasMatchingDestination(signText, playerDestination.getDest()));

						pLevel.setBlock(pPos, blockState, 3);
						pLevel.scheduleTick(pPos, thisRail, 20);
						pLevel.updateNeighbourForOutputSignal(pPos, thisRail);

						ci.cancel();
					});
				}
			}
		}
	}

	@Unique
	private static boolean rail_destinations$hasMatchingDestination(Component[] signText, String playerDestination) {
		if (playerDestination.isEmpty()) return false;
		if (playerDestination.contains("*")) return true;

		var lines = Arrays.copyOfRange(signText, 1, signText.length);
		var destinations = playerDestination.split(" ");

		// Check if rail has matching destination
		for (var line : lines) {
			if ("*".equals(line.getString()) || playerDestination.equalsIgnoreCase(line.getString())) return true;

			for (var destination : destinations) {
				if (destination.equalsIgnoreCase(line.getString())) return true;
			}
		}

		return false;
	}
}