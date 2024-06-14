package identitytheft.raildestinations.util;

import net.minecraft.nbt.NbtCompound;

public class DestinationData {
    public static String setDest(IEntityDataSaver player, String dest)
    {
        NbtCompound nbt = player.getPersistentData();
        nbt.putString("destination", dest);

        return dest;
    }

    public static String getDest(IEntityDataSaver player)
    {
        return player.getPersistentData().getString("destination");
    }
}