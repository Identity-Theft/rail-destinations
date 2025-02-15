package identitytheft.raildestinations.mixins;

import com.google.common.base.Strings;
import identitytheft.raildestinations.RailDestinations;
import identitytheft.raildestinations.destination.PlayerDestination;
import identitytheft.raildestinations.destination.PlayerDestinationProvider;
import identitytheft.raildestinations.util.SwitchType;
import net.minecraft.core.BlockPos;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Mixin(DetectorRailBlock.class)
public abstract class DetectorRailBlockMixin {
    @Shadow @Final public static BooleanProperty POWERED;

    @Shadow protected abstract <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level pLevel, BlockPos pPos, Class<T> pCartType, Predicate<Entity> pFilter);

    @Inject(method = "checkPressed", at = @At("HEAD"), cancellable = true)
    public void rail_destinations$checkPressed(Level pLevel, BlockPos pPos, BlockState pState, CallbackInfo ci)
    {
        var thisRail = (DetectorRailBlock)(Object)this;
        var above = pLevel.getBlockState(pPos.above());

        if (above.is(BlockTags.SIGNS))
        {
            var signEntity = (SignBlockEntity)pLevel.getBlockEntity(pPos.above());
            var signText = signEntity.getFrontText().getMessages(false);

            // Use the sign's first line to determine if it's a switch
            var type = SwitchType.find(signText[0].getString());

            if (type != null) {
                // Get list of carts on rail
                var carts = this.getInteractingMinecartOfType(pLevel, pPos, AbstractMinecart.class, (entity1 -> true));

                if (!carts.isEmpty() && carts.get(0).getFirstPassenger() instanceof Player playerEntity)
                {
                    playerEntity.getCapability(PlayerDestinationProvider.PLAYER_DEST).ifPresent(playerDestination -> {
                        var playerDestinations = playerDestination.getDest().split(" ");
                        var switchDestinations = Arrays.copyOfRange(signText, 1, signText.length);

                        boolean matched = false;

                        // Check if rail has matching destination
                        for (var dest: playerDestinations) {
                            for (var switchDestination: switchDestinations) {
                                if (Strings.isNullOrEmpty(switchDestination.getString())) continue;

                                if (dest.equalsIgnoreCase(switchDestination.getString()))
                                {
                                    RailDestinations.LOGGER.info("Destination matches");
                                    matched = true;
                                    break;
                                }
                            }
                        }

                        // Update rail's state based on if player's destination matched
                        BlockState blockState = pState.setValue(POWERED, (type == SwitchType.NORMAL) == matched);

                        pLevel.setBlock(pPos, blockState, 3);
                        pLevel.scheduleTick(pPos, thisRail, 20);
                        pLevel.updateNeighbourForOutputSignal(pPos, thisRail);

                        ci.cancel();
                    });
                }
            }
        }
    }
}
