package identitytheft.raildestinations.destination;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDestinationProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<PlayerDestination> PLAYER_DEST = CapabilityManager.get(new CapabilityToken<>() { });

    private PlayerDestination dest = null;
    private final LazyOptional<PlayerDestination> optional = LazyOptional.of(this::createPlayerDestination);

    private PlayerDestination createPlayerDestination() {
        if (this.dest == null)
        {
            this.dest = new PlayerDestination();
        }

        return this.dest;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_DEST)
        {
            return optional.cast();
        }

        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        createPlayerDestination().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createPlayerDestination().loadNBTData(nbt);
    }
}
