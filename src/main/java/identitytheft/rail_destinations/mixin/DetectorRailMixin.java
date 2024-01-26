package identitytheft.rail_destinations.mixin;

import com.google.common.base.Strings;
import identitytheft.rail_destinations.RailDestinations;
import identitytheft.rail_destinations.util.DestinationData;
import identitytheft.rail_destinations.util.IEntityDataSaver;
import identitytheft.rail_destinations.util.SwitchType;
import net.minecraft.block.BlockState;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow protected abstract void updateNearbyRails(World world, BlockPos pos, BlockState state, boolean unpowering);

    @Inject(method = "updatePoweredStatus", at = @At("HEAD"), cancellable = true)
    public void updatePoweredStatus(World world, BlockPos pos, BlockState state, CallbackInfo ci)
    {
        var thisRail = (DetectorRailBlock)(Object)this;
        var above = world.getBlockState(pos.up());

        if (above.isIn(BlockTags.SIGNS)) {
            var entity = (SignBlockEntity) world.getBlockEntity(pos.up());
            var signText = entity.getFrontText().getMessages(false);
            var line0 = signText[0];

            // Use the sign's first line to determine if it's a switch
            var type = SwitchType.find(line0.getString());

            if (type != null) {
                // Get list of carts on rail
                var carts = this.getCarts(world, pos, AbstractMinecartEntity.class, (entity1 -> true));

                if (!carts.isEmpty() && carts.get(0).getFirstPassenger() instanceof PlayerEntity playerEntity)
                {
                    var playerDestinations = DestinationData.getDest((IEntityDataSaver) playerEntity).split(" ");
                    var switchDestinations = Arrays.copyOfRange(signText, 1, signText.length);

                    boolean matched = false;

                    // Check if rail has matching destination
                    for (var playerDestination: playerDestinations) {
                        for (var switchDestination: switchDestinations) {
                            if (Strings.isNullOrEmpty(switchDestination.getString())) continue;

                            if (playerDestination.equalsIgnoreCase(switchDestination.getString()))
                            {
                                RailDestinations.LOGGER.info("Destination matches");
                                matched = true;
                                break;
                            }
                        }
                    }

                    // Update rail's state based on if player's destination matched
                    BlockState blockState = state.with(POWERED, type == SwitchType.NORMAL ? matched : !matched);

                    world.setBlockState(pos, blockState, 3);
                    world.scheduleBlockTick(pos, thisRail, 20);
                    world.updateComparators(pos, thisRail);

                    ci.cancel();
                }
            }
        }
    }
}
