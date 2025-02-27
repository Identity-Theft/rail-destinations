package identitytheft.raildestinations.util;

import net.minecraft.nbt.NbtCompound;

public class DestinationData {
    public static void setDest(IEntityDataSaver player, String dest)
    {
        NbtCompound nbt = player.rail_destinations$getPersistentData();
        nbt.putString("destination", dest);

    }

    public static String getDest(IEntityDataSaver player)
    {
        return player.rail_destinations$getPersistentData().getString("destination");
    }
}