package identitytheft.raildestinations.util;

public class DestinationData {
    public static void setDest(IEntityDataSaver player, String dest)
    {
        player.rail_destinations$setDestination(dest);
    }

    public static String getDest(IEntityDataSaver player)
    {
		return player.rail_destinations$getDestination();
	}
}