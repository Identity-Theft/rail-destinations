package identitytheft.raildestinations.mixin;

import identitytheft.raildestinations.RailDestinations;
import identitytheft.raildestinations.util.SwitchType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
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
	protected abstract <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level world, BlockPos pos, Class<T> entityClass, Predicate<Entity> entityPredicate);

	@Shadow @Final
	public static BooleanProperty POWERED;

	@Inject(method = "checkPressed", at = @At("HEAD"), cancellable = true)
	public void rail_destinations$checkPressed(Level level, BlockPos pos, BlockState state, CallbackInfo ci)
	{
		var thisRail = (DetectorRailBlock)(Object)this;
		var above = level.getBlockState(pos.above());

		if (above.is(BlockTags.SIGNS)) {
			var entity = (SignBlockEntity) level.getBlockEntity(pos.above());
			assert entity != null;
			var signText = entity.getFrontText().getMessages(false);

			var type = SwitchType.find(signText[0].getString().toLowerCase());

			if (type != null) {
				var carts = this.getInteractingMinecartOfType(level, pos, AbstractMinecart.class, (entity1 -> true));

				if (!carts.isEmpty() && carts.getFirst().getFirstPassenger() instanceof Player playerEntity)
				{
					var playerDestination = playerEntity.getData(RailDestinations.DESTINATION);

					BlockState blockState = state.setValue(POWERED, (type == SwitchType.NORMAL) == rail_destinations$hasMatchingDestination(signText, playerDestination));

					level.setBlock(pos, blockState, 3);
					level.scheduleTick(pos, thisRail, 20);
					level.updateNeighbourForOutputSignal(pos, thisRail);

					ci.cancel();
				}
			}
		}
	}

	@Unique
	private static boolean rail_destinations$hasMatchingDestination(Component[] signText, String playerDestination) {
		if (playerDestination.isEmpty()) return false;

		var lines = Arrays.copyOfRange(signText, 1, signText.length);
		var destinations = playerDestination.split(" ");

		for (var line: lines) {
			if ("*".equals(line.getString()) || playerDestination.equalsIgnoreCase(line.getString())) return true;

			for (var destination: destinations) {
				if (destination.equalsIgnoreCase(line.getString())) return true;
			}
		}

		return false;
	}
}