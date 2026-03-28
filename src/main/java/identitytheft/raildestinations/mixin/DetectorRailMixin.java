package identitytheft.raildestinations.mixin;

import identitytheft.raildestinations.util.IEntityDataSaver;
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
public abstract class DetectorRailMixin {
    @Shadow protected abstract <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(final Level level, final BlockPos pos, final Class<T> type, final Predicate<Entity> containerEntitySelector);

    @Shadow @Final public static BooleanProperty POWERED;

    @Inject(method = "checkPressed", at = @At("HEAD"), cancellable = true)
    public void updatePoweredStatus(Level level, BlockPos pos, BlockState state, CallbackInfo ci)
    {
        var thisRail = (DetectorRailBlock)(Object)this;
        var above = level.getBlockState(pos.above());

        if (above.is(BlockTags.SIGNS)) {
            var entity = (SignBlockEntity) level.getBlockEntity(pos.above());
			assert entity != null;
			var signText = entity.getFrontText().getMessages(false);

            var type = SwitchType.find(signText[0].getString());

            if (type != null) {
                var carts = this.getInteractingMinecartOfType(level, pos, AbstractMinecart.class, (_ -> true));

                if (!carts.isEmpty() && carts.getFirst().getFirstPassenger() instanceof Player playerEntity)
                {
                    var playerDestination = ((IEntityDataSaver) playerEntity).rail_destinations$getDestination();

                    BlockState blockState = state.setValue(POWERED, (type == SwitchType.NORMAL) == hasMatchingDestination(signText, playerDestination));

                    level.setBlock(pos, blockState, 3);
                    level.scheduleTick(pos, thisRail, 20);
                    level.updateNeighbourForOutputSignal(pos, thisRail);

                    ci.cancel();
                }
            }
        }
    }

    @Unique
    private static boolean hasMatchingDestination(Component[] signText, String playerDestination) {
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