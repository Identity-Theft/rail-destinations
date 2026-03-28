package identitytheft.raildestinations.mixin;

import identitytheft.raildestinations.util.IEntityDataSaver;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityDataSaverMixin implements IEntityDataSaver {
    @Unique
    private String destination = "";

    @Override
    public String rail_destinations$getDestination()
    {
       return this.destination;
    }

    @Override
    public void rail_destinations$setDestination(String destination) {
        this.destination = destination;
    }

    @Inject(method = "saveWithoutId", at = @At("HEAD"))
    public void rail_destinations$write(ValueOutput output, CallbackInfo ci) {
        output.putString("identitytheft.railswitch", destination);
    }

    @Inject(method = "load", at = @At("HEAD"))
    public void rail_destinations$read(ValueInput input, CallbackInfo ci) {
        this.destination = input.getString("identitytheft.railswitch").orElse("");
    }
}
