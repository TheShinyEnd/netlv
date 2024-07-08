

package net.theshinyend.development.netlv;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class Netlv_backup extends JavaPlugin implements Listener {
    private HashMap<UUID, Boolean> printReachToggle = new HashMap<>();
    private HashMap<UUID, HashMap<UUID, Double>> playerReach = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }
        Player player = (Player) sender;
        if (command.getName().equalsIgnoreCase("printreach")) {
            if (args.length != 1) {
                player.sendMessage("Usage: /printreach <playername>");
                return false;
            }
            Player targetPlayer = getServer().getPlayer(args[0]);
            if (targetPlayer == null) {
                player.sendMessage("Player not found.");
                return false;
            }
            UUID targetUUID = targetPlayer.getUniqueId();
            if (!printReachToggle.containsKey(targetUUID) || !printReachToggle.get(targetUUID)) {
                printReachToggle.put(targetUUID, true);
                player.sendMessage("Print reach enabled for " + targetPlayer.getName() + ".");
            } else {
                printReachToggle.put(targetUUID, false);
                player.sendMessage("Print reach disabled for " + targetPlayer.getName() + ".");
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if (!(damager instanceof Player) || !(victim instanceof LivingEntity)) {
            return;
        }

        Player player = (Player) damager;
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        AxisAlignedBB hitbox = ((CraftLivingEntity) victim).getHandle().getBoundingBox();
        double hitboxMinX = Math.min(hitbox.a, hitbox.d) - 0.1;
        double hitboxMinY = (Math.min(hitbox.b, hitbox.e) - 0.1) - 2;
        double hitboxMinZ = Math.min(hitbox.c, hitbox.f) - 0.1;
        double hitboxMaxX = Math.max(hitbox.a, hitbox.d) + 0.1;
        double hitboxMaxY = Math.max(hitbox.b, hitbox.e) + 0.1;
        double hitboxMaxZ = Math.max(hitbox.c, hitbox.f) + 0.1;

        double tMaxX = (hitboxMaxX - eyeLocation.getX()) / direction.getX();
        double tMinX = (hitboxMinX - eyeLocation.getX()) / direction.getX();
        double tMaxY = (hitboxMaxY - eyeLocation.getY()) / direction.getY();
        double tMinY = (hitboxMinY - eyeLocation.getY()) / direction.getY();
        double tMaxZ = (hitboxMaxZ - eyeLocation.getZ()) / direction.getZ();
        double tMinZ = (hitboxMinZ - eyeLocation.getZ()) / direction.getZ();

        double tMax = Math.max(Math.max(Math.min(tMaxX, tMinX), Math.min(tMaxY, tMinY)), Math.min(tMaxZ, tMinZ));
        double tMin = Math.min(Math.max(Math.min(tMinX, tMaxX), Math.min(tMinY, tMaxY)), Math.min(tMinZ, tMaxZ));

        Location hitboxOutermost = eyeLocation.clone().add(direction.clone().multiply(tMax));
        double difx = hitboxOutermost.getX() - eyeLocation.getX();
        double dify = hitboxOutermost.getY() - eyeLocation.getY();
        double difz = hitboxOutermost.getZ() - eyeLocation.getZ();

        double distanceMathS = Math.sqrt(difx * difx + dify * dify + difz * difz);

        double eyeX = eyeLocation.getX();
        double eyeY = eyeLocation.getY();
        double eyeZ = eyeLocation.getZ();
        double hX = hitboxOutermost.getX();
        double hY = hitboxOutermost.getY();
        double hZ = hitboxOutermost.getZ();

        double vm = Math.sqrt((Math.pow(eyeX - hX, 2)) + (Math.pow(eyeY - hY, 2)) + (Math.pow(eyeZ - hZ, 2)));

        if (tMax < 0 || tMin > tMax) {
            event.setCancelled(true);
            damager.sendMessage(ChatColor.RED + "Hitpoint is within the hitbox"
                    + ChatColor.GRAY + "tMax: " + tMax + ChatColor.GRAY + "tMin: " + tMin);
        }

        // Calculate the hit point
        Location hitPoint = eyeLocation.clone().add(direction.clone().multiply(tMax));

        if (hitPoint.getX() < hitboxMinX || hitPoint.getX() > hitboxMaxX || hitPoint.getY() < hitboxMinY || hitPoint.getY() > hitboxMaxY || hitPoint.getZ() < hitboxMinZ || hitPoint.getZ() > hitboxMaxZ) {
            event.setCancelled(true);
            damager.sendMessage(ChatColor.RED + "Hitpoint is outside the hitbox");
        }


        printReach(player, (LivingEntity) victim, vm);
    }

    private void printReach(Player player, LivingEntity victim, double hitDistance) {
        if (printReachToggle.containsKey(player.getUniqueId()) && printReachToggle.get(player.getUniqueId())) {
            UUID playerUUID = player.getUniqueId();
            UUID victimUUID = victim.getUniqueId();
            if (!playerReach.containsKey(playerUUID)) {
                playerReach.put(playerUUID, new HashMap<>());
            }
            playerReach.get(playerUUID).put(victimUUID, hitDistance);
            player.sendMessage("Your reach to " + victim.getName() + " is " + String.format("%.2f", hitDistance) + " blocks.");
        }
    }
}


