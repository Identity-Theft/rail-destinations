package identitytheft.raildestinations.destination;

import net.minecraft.nbt.CompoundTag;

public class PlayerDestination {
    private String dest;

    public void setDest(String dest)
    {
        this.dest = dest;
    }

    public String getDest()
    {
        return dest;
    }

    public void copyFrom(PlayerDestination source)
    {
        this.dest = source.dest;
    }

    public void saveNBTData(CompoundTag nbt)
    {
        nbt.putString("dest", dest);
    }

    public void loadNBTData(CompoundTag nbt)
    {
        this.dest = nbt.getString("dest");
    }
}