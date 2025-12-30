package identitytheft.raildestinations.mixin;

import identitytheft.raildestinations.util.IEntityDataSaver;
import identitytheft.raildestinations.util.SwitchType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
    @Shadow protected abstract <T extends AbstractMinecartEntity> List<T> getCarts(World world, BlockPos pos, Class<T> entityClass, Predicate<Entity> entityPredicate);

    @Shadow @Final public static BooleanProperty POWERED;

    @Inject(method = "updatePoweredStatus", at = @At("HEAD"), cancellable = true)
    public void updatePoweredStatus(World world, BlockPos pos, BlockState state, CallbackInfo ci)
    {
        var thisRail = (DetectorRailBlock)(Object)this;
        var above = world.getBlockState(pos.up());

        if (above.isIn(BlockTags.SIGNS)) {
            var entity = (SignBlockEntity) world.getBlockEntity(pos.up());
			assert entity != null;
			var signText = entity.getFrontText().getMessages(false);

            var type = SwitchType.find(signText[0].getString().toLowerCase());

            if (type != null) {
                var carts = this.getCarts(world, pos, AbstractMinecartEntity.class, (entity1 -> true));

                if (!carts.isEmpty() && carts.getFirst().getFirstPassenger() instanceof PlayerEntity playerEntity)
                {
                    var playerDestination = ((IEntityDataSaver) playerEntity).rail_destinations$getDestination();

                    BlockState blockState = state.with(POWERED, (type == SwitchType.NORMAL) == hasMatchingDestination(signText, playerDestination));

                    world.setBlockState(pos, blockState, 3);
                    world.scheduleBlockTick(pos, thisRail, 20);
                    world.updateComparators(pos, thisRail);

                    ci.cancel();
                }
            }
        }
    }

    @Unique
    private static boolean hasMatchingDestination(Text[] signText, String playerDestination) {
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