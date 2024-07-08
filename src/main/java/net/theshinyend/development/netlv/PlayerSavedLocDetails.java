package net.theshinyend.development.netlv;

import org.bukkit.Location;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import java.util.ArrayList;
import java.util.List;


public class PlayerSavedLocDetails {
    private List<Location> locations1;
    private List<Location> locations2;
    private List<AxisAlignedBB> boundingBoxes;
    private List<Long> ticks;

    public PlayerSavedLocDetails() {
        this.locations1 = new ArrayList<>();
        this.locations2 = new ArrayList<>();
        this.boundingBoxes = new ArrayList<>();
        this.ticks = new ArrayList<>();
    }

    public void addData(Location location1, Location location2, AxisAlignedBB boundingBox, Long tick) {
        locations1.add(location1);
        locations2.add(location2);
        boundingBoxes.add(boundingBox);
        ticks.add(tick);
    }

    public List<Location> getLocations1() {
        return locations1;
    }

    public List<Location> getLocations2() {
        return locations2;
    }

    public List<AxisAlignedBB> getBoundingBoxes() {
        return boundingBoxes;
    }

    public List<Long> getTicks() {
        return ticks;
    }
}
