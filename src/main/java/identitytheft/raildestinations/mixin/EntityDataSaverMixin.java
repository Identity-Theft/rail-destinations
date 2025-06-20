package identitytheft.raildestinations.mixin;

import identitytheft.raildestinations.util.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityDataSaverMixin implements IEntityDataSaver {
    @Unique
    private String destination;

    @Override
    public String rail_destinations$getDestination()
    {
       return this.destination;
    }

    @Override
    public void rail_destinations$setDestination(String destination) {
        this.destination = destination;
    }

    @Inject(method = "writeData", at = @At("HEAD"))
    public void rail_destinations$write(WriteView view, CallbackInfo ci) {
        view.putString("identitytheft.railswitch", destination);
    }

    @Inject(method = "readData", at = @At("HEAD"))
    public void rail_destinations$read(ReadView view, CallbackInfo ci) {
        this.destination = view.getString("identitytheft.railswitch", "");
    }
}
