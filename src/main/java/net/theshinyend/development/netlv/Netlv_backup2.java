package net.theshinyend.development.netlv;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.*;
import static net.minecraft.server.v1_8_R3.EnumParticle.a;
import static org.bukkit.GameMode.SPECTATOR;
import static org.bukkit.Material.AIR;

public class Netlv_backup2 extends JavaPlugin implements Listener {
    private HashMap<String, HashMap<String, Double>> playerReach = new HashMap<>();
    private HashMap<String, Set<String>> printreachEnabled = new HashMap<>();
    private HashMap<String, Float> jumpTracker = new HashMap<>();
    private HashMap<String, Boolean>  wasOnSlimeBlock = new HashMap<>();
    private HashMap<String, Double> previousHeight = new HashMap<>();
    private HashMap<String, Double> slimeHeight = new HashMap<>();


    private int verbose_hitbox = 0;
    private int alerts_hitbox = 0;
    private int speed_buffer = 0;

    private static final double THRESHOLD = 0.1;
    private int hitbox_in_a_row_check = 0;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                verbose_hitbox = 0;
                alerts_hitbox = 0;
                hitbox_in_a_row_check = 0;
            }
        }, 0, 10, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        playerReach.clear();
        printreachEnabled.clear();
        scheduler.shutdownNow();
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
            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                player.sendMessage("Player not found.");
                return false;
            }

            Set<String> enabledPlayers = printreachEnabled.computeIfAbsent(player.getName(), k -> new HashSet<>());
            if (enabledPlayers.contains(targetPlayerName)) {
                enabledPlayers.remove(targetPlayerName);
                player.sendMessage("Printreach disabled for " + targetPlayerName);
            } else {
                enabledPlayers.add(targetPlayerName);
                player.sendMessage("Printreach enabled for " + targetPlayerName);
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

        // forceUpdateLookPacket(player); // unstable


        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        LivingEntity target = (LivingEntity) (event.getEntity());
        Location eyeLocation_victim = target.getEyeLocation();
        Vector direction_victim = eyeLocation.getDirection();


        // victim POV
        AxisAlignedBB hitbox_victim = ((CraftLivingEntity) damager).getHandle().getBoundingBox();
        double hitboxMinX_victim = Math.min(hitbox_victim.a, hitbox_victim.d) - 0.1;
        double hitboxMinY_victim = (Math.min(hitbox_victim.b, hitbox_victim.e) - 0.1);
        double hitboxMinZ_victim = Math.min(hitbox_victim.c, hitbox_victim.f) - 0.1;
        double hitboxMaxX_victim = Math.max(hitbox_victim.a, hitbox_victim.d) - 0.1;
        double hitboxMaxY_victim = Math.max(hitbox_victim.b, hitbox_victim.e) + 0.1;
        double hitboxMaxZ_victim = Math.max(hitbox_victim.c, hitbox_victim.f) + 0.1;

        double tMaxX_victim = (hitboxMaxX_victim - eyeLocation_victim.getX()) / direction_victim.getX();
        double tMinX_victim = (hitboxMinX_victim - eyeLocation_victim.getX()) / direction_victim.getX();
        double tMaxY_victim = (hitboxMaxY_victim - eyeLocation_victim.getY()) / direction_victim.getY();
        double tMinY_victim = (hitboxMinY_victim - eyeLocation_victim.getY()) / direction_victim.getY();
        double tMaxZ_victim = (hitboxMaxZ_victim - eyeLocation_victim.getZ()) / direction_victim.getZ();
        double tMinZ_victim = (hitboxMinZ_victim - eyeLocation_victim.getZ()) / direction_victim.getZ();

        double tMax_victim = Math.max(Math.max(Math.min(tMaxX_victim, tMinX_victim), Math.min(tMaxY_victim, tMinY_victim)), Math.min(tMaxZ_victim, tMinZ_victim));
        double tMin_victim = Math.min(Math.max(Math.min(tMinX_victim, tMaxX_victim), Math.min(tMinY_victim, tMaxY_victim)), Math.min(tMinZ_victim, tMaxZ_victim));

        Location hitboxOutermost_victim = eyeLocation_victim.clone().add(direction_victim.clone().multiply(tMax_victim));

        double eyeX_victim = eyeLocation_victim.getX();
        double eyeY_victim = eyeLocation_victim.getY();
        double eyeZ_victim = eyeLocation_victim.getZ();
        double hX_victim = hitboxOutermost_victim.getX();
        double hY_victim = hitboxOutermost_victim.getY();
        double hZ_victim = hitboxOutermost_victim.getZ();

        double vm_victim = Math.sqrt((pow(eyeX_victim - hX_victim, 2)) + (pow(eyeY_victim - hY_victim, 2)) + (pow(eyeZ_victim - hZ_victim, 2)));


        // Damager POV
        AxisAlignedBB hitbox = ((CraftLivingEntity) victim).getHandle().getBoundingBox();
        double hitboxMinX = Math.min(hitbox.a, hitbox.d) - 0.1;
        double hitboxMinY = (Math.min(hitbox.b, hitbox.e)) - 0.1;
        double hitboxMinZ = Math.min(hitbox.c, hitbox.f) - 0.1;
        double hitboxMaxX = Math.max(hitbox.a, hitbox.d) - -0.1;
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

        double vm = Math.sqrt((pow(eyeX - hX, 2)) + (pow(eyeY - hY, 2)) + (pow(eyeZ - hZ, 2)));


        float vm_vic_int = (float) vm_victim;
        float vm_int = (float) vm;

        if (vm_int == vm_vic_int) { // skip, as it's practically impossible unless bot both have hit each at the exact same tick and at the exact same head location position
            // do nothing
        } else if (!(((Player) damager).isSprinting()) && (victim.getVelocity().length() == 0.0784000015258789) && (vm_int > 3.009)) { // this is the best case, and this means they 100% have reach.
            damager.sendMessage("100% using reach modification");
            event.setCancelled(true);
        } else if (vm_vic_int > vm_int) {
            // interesting stuff
            int subA = (int) (vm_vic_int - vm_int);
            if (subA > 4) {
                damager.sendMessage("Kicked for high range, Either lag/delay-in-netty or reach modification.");
                event.setCancelled(true);
            }
        } else if (((vm_int - vm_vic_int) > 3) && (!((Player) damager).isSprinting()) && (!((Player) victim).isSprinting())) {
            damager.sendMessage("Reach, user reach larger than victim 'reach', and is more than 3 - less lienent");
            event.setCancelled(true);
            // should be normal, or detection, that's saying if best scenario. cause currently movement isn't being handled
        } else if ((((vm_int - vm_vic_int) > 5) && ((Player) damager).isSprinting()) && ((Player) victim).isSprinting()) {
            damager.sendMessage("Reach, user reach larger than victim 'reach', and is more than 5 - lienent ish, sprint dependent");
            event.setCancelled(true);
        } else if (((abs(vm_int - vm_vic_int)) < 1) && (vm_int > 3.09)) {
            damager.sendMessage("There is a a chance of reach usage/modification");
            event.setCancelled(true);
            //    } else if ((vm_int > 5) && (vm_victim < 3)) { // they probably didn't cheat and just lagged.
            //      }
        } else if (vm_int > 5) {
            damager.sendMessage("Reach too far - 5");
            event.setCancelled(true);
        }


        Location hitPoint = eyeLocation.clone().add(direction.clone().multiply(tMax));


        if (hitPoint.getX() < hitboxMinX || hitPoint.getX() > hitboxMaxX || hitPoint.getY() < hitboxMinY || hitPoint.getY() > hitboxMaxY || hitPoint.getZ() < hitboxMinZ || hitPoint.getZ() > hitboxMaxZ) {
            // TODO Need to make this less lienet, 10%~

            //event.setCancelled(true);
            //damager.sendMessage(ChatColor.AQUA + "Flagging hit box");
            hitbox_in_a_row_check++;
            if (verbose_hitbox > 10) {
                alerts_hitbox++;
                verbose_hitbox = 0;
                if (alerts_hitbox > 2) {
                    // make some enable disable system like you have for the Utilities, enable check X enable ban or message or disable overall it? ~
                    //damager.sendMessage(ChatColor.RED + "You should've been banned, hitbox");
                }
            } else if (hitbox_in_a_row_check > 2) {
                verbose_hitbox = verbose_hitbox + 2;
            } else {
                verbose_hitbox++;
            }
            //damager.sendMessage(ChatColor.GREEN + "Alerts: " + ChatColor.RESET + alerts_hitbox + ChatColor.GRAY +  ", Verbose: " + ChatColor.RESET +  verbose_hitbox + " in a row: " + hitbox_in_a_row_check);

        } else {
            hitbox_in_a_row_check = 0;
        }


        printReach(player, (LivingEntity) victim, vm, vm_victim);

    }


    public void forceUpdateLookPacket(Player player) { // math is weird. do not use
        byte yaw = (byte) ((player.getLocation().getYaw() - 180.0F) * 255.0F / 360.0F);
        byte pitch = (byte) (player.getLocation().getPitch() * 255.0F / 360.0F);
        PacketPlayOutEntity.PacketPlayOutEntityLook packet = new PacketPlayOutEntity.PacketPlayOutEntityLook(player.getEntityId(), yaw, pitch, false);
        // Get the player's network manager
        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;

        // Send the packet to the player
        playerConnection.sendPacket(packet);

    }



    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) { // you can now make a speed check, LETS GO


        double baseSpeed = 0.216;
        int verbose = 0;
        Player player = event.getPlayer();
        if (player.getGameMode() == SPECTATOR) { // there is nothing to do in spectator mode, i think it freezes the server.. unsure
            return;
        }
        double deltaX = (event.getTo().getX() - event.getFrom().getX()); //Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());
        double deltaZ = (event.getTo().getZ() - event.getFrom().getZ());
        double deltaY = (event.getTo().getY() - event.getFrom().getY());

        //player.sendMessage("DeltaVelocityY: " + (event.getPlayer().getVelocity().getY() + deltaY));// + ChatColor.RED+" neg: " + (event.getPlayer().getVelocity().getY() - deltaY));


        double playerY = event.getPlayer().getLocation().getY();

        //Float deltaXZ_formatted = Float.parseFloat(formatTriple(deltaXZ));
        Float deltaY_formatted = Float.parseFloat(formatTriple(deltaY));

        String gbup = getBlockUnderneathPlayer(player); // returns name of block under player
        int bnp = getBlockYUnderneathPlayer(player); // returns lowest y value of a block
        int pyv = (int) player.getLocation().getY();
        List<String> list_of_blocks_under_player = getBlocksUnderneathPlayer(player);
        List<String> list_of_blocks_ontop_player = getBlocksOnTopPlayer(player);
        List<String> list_of_blocks_Y3_player = GetBlocksInPlayerY3(player);
        List<String> blocksUnderPlayerLOWER = getBlocksUnderPlayerLOWER(player); // it may cause lag..

        //Float tmp = (float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp));


        // player.sendMessage(ChatColor.DARK_AQUA + "DeltaY: " + deltaY + " Y to fall: " + (playerY - bnp - 2) + " BlockUnderneath: " + gbup);


        //player.sendMessage("deltaXZ" + formatTriple(deltaXZ) + " deltaY" + formatTriple(deltaY));
        //player.sendMessage("Block under player: " + gbup);

        //player.sendMessage(ChatColor.RED + "IS Modified " +deltaY_formatted_double+" IT RIGHT? " + expectedDeltaY +" == "+ (0.333) + " result: " + (expectedDeltaY== deltaY_formatted_double));
        //player.sendMessage(ChatColor.GREEN +"" + list_of_blocks_under_player);
        //player.sendMessage(ChatColor.GREEN + ""+ list_of_blocks_ontop_player);
        //player.sendMessage(ChatColor.GREEN + "Is there a block where no air?: " + (!(isEverythingInListThatString(list_of_blocks_ontop_player, "AIR"))));
        //if getBlockUnderneathPlayer(player) ==
        // trapdoor is +0.1875
        // slab is +0.5
        // fence is +0.5 like slab
        // head is +0.75 it depends on whether it's on side or directly from ground


        double expectedDeltaY = getExpectedDeltaY(String.valueOf(player));
        expectedDeltaY = Double.parseDouble(formatTriple(expectedDeltaY));
        double deltaY_formatted_double = Double.parseDouble(formatTriple(deltaY_formatted));


        double jumpboost_amplifier = getPotionModifier(player, PotionEffectType.JUMP); // TODO: this
        // TODO: You need to make the slime block math, and also,
        //  whenever a player is jumping and landing exactly on the side top of a block it falses and say: deltaY: 0.5952911683122091.

        //player.sendMessage(ChatColor.GREEN + "jumpboost_amplifier: " + jumpboost_amplifier);

        if (((deltaY < 0) && (previousHeight.containsKey(player.getName())))) {
            //player.sendMessage("Falling..");
        } else if (previousHeight.containsKey(player.getName()) && containsWord(blocksUnderPlayerLOWER, "SLIME")) {
            wasOnSlimeBlock.put(player.getName(), true);
            slimeHeight.put(player.getName(), playerY - 1);
            //player.sendMessage("LAnded on slime");
        } else if (!(isEverythingInListThatString(blocksUnderPlayerLOWER, "AIR"))) {
            previousHeight.remove(player.getName());
        }

        //player.sendMessage(ChatColor.RED + "" + previousHeight.containsKey(player.getName()) + slimeHeight.containsKey(player.getName()) + wasOnSlimeBlock.containsKey(player.getName()));

        if (deltaY > 0 && wasOnSlimeBlock.containsKey(player.getName())) {
            double previousYValue = previousHeight.get(player.getName());
            double slimeBlockY = slimeHeight.get(player.getName());
            double subtractedYValue = (previousYValue - slimeBlockY);
            double maxHeight = predictedJumpHeight(subtractedYValue) + slimeBlockY + 2; // slime block getY is as a base height, the +2 is in case of an error
            //player.sendMessage(" previousYValue: " + previousYValue + " slimeBlockY: " + slimeBlockY + " maxHeight: " + maxHeight);
            //player.sendMessage(ChatColor.GREEN + "expectedDeltaY: " + expectedDeltaY + " maxHeight: " + maxHeight + " playerY: " + playerY);
            //player.sendMessage(ChatColor.GREEN + "expectedDeltaY: " + expectedDeltaY + " deltay: " + deltaY);
            if (expectedDeltaY != 0.0) {
                if (expectedDeltaY == (new Float(deltaY))) {
                    player.sendMessage("cannot remain at the same delta all the time, hacking");
                } else if ((deltaY > 0)) {
                    if (playerY > (maxHeight + 1)) {
                        player.sendMessage(ChatColor.GREEN + "You've surpassed the max height from the slime block " + maxHeight);
                    }
                }
            } else {
                trackJump(String.valueOf(player), new Float(deltaY)); // isn't actual for jump; for the expecteddeltaY, this is
            }
        } else if (deltaY < 0 && wasOnSlimeBlock.containsKey(player.getName())) {
            wasOnSlimeBlock.remove(player.getName());
            previousHeight.remove(player.getName());
            previousHeight.put(player.getName(), playerY);
            slimeHeight.remove(player.getName());
            expectedDeltaY = 0;

        } else if (!(expectedDeltaY==(0))) {
            if ((!(isEverythingInListThatString(list_of_blocks_ontop_player, "AIR"))) && (deltaY_formatted_double < 0)) {
                // player jumped and a block is on top of them
            } else if ((!(expectedDeltaY==(deltaY_formatted_double))) && (!((deltaY == 0.5 && (containsWord(list_of_blocks_under_player, "STEP")) || (containsWord(list_of_blocks_under_player, "STAIRS")) || (containsWord(list_of_blocks_under_player, "FENCE")) || (containsWord(list_of_blocks_under_player, "SKULL"))) || ((deltaY == 0.75) && (containsWord(list_of_blocks_under_player, "SKULL"))) || ((deltaY == 0.1875) && (containsWord(list_of_blocks_under_player, "TRAP_DOOR"))) || ((deltaY_formatted_double == 0.118) && (containsWord(list_of_blocks_under_player, "LADDER"))) || ((deltaY == 0.3125) && (containsWord(list_of_blocks_under_player, "STEP")) || (containsWord(list_of_blocks_under_player, "TRAP_DOOR"))) || ((deltaY == 0.125) && (containsWord(list_of_blocks_under_player, "REDSTONE")))))) {
                if (deltaY == 0) {
                    return; // a false!; player isn't moving in the Y value
                }
                player.sendMessage("Hacking, jumping weirdly");
                player.sendMessage(ChatColor.DARK_AQUA + "DeltaY: " + deltaY);
                //player.sendMessage(deltaY_formatted_double + " <- You're jumping weirdly.. Step? Bhop? Expected: " + expectedDeltaY);
                // 0.420, 0.333, 0.248, 0.165, 0.83 check.
            } else if (expectedDeltaY==(0.333+ (0.10000000149 * jumpboost_amplifier))) {
//                    if ((float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp)) != 0.42) {
//                        player.sendMessage("Weird jumping!");
//                    }
                trackJump(String.valueOf(player), new Float(0.248));
            } else if (expectedDeltaY==(0.248+ (0.10000000149 * jumpboost_amplifier))) {
//                    if ((float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp)) != 0.753) {
//                        player.sendMessage("Weird jumping!");
//                    }
                trackJump(String.valueOf(player), new Float(0.165));
            } else if (expectedDeltaY==(0.165+ (0.10000000149 * jumpboost_amplifier))) {
//                    if ((float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp)) != 1.001f) {
//                        player.sendMessage("Weird jumping!");
//                    }
                trackJump(String.valueOf(player), new Float(0.083));
            } else if (expectedDeltaY == (0.083 + (0.10000000149 * jumpboost_amplifier))) {
//                    if ((float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp)) != 1.166) {
//                        player.sendMessage("Weird jumping!");
//                    }
                //player.sendMessage("Jumped normally");
                trackJump(String.valueOf(player), new Float(0.0));
            }
        } else if (player.isFlying() == true || (player.getGameMode() == GameMode.CREATIVE)) { // also you should make a record to how many changes there is to the person when they jump, so 10 alerts?
            //player.sendMessage("flying");
            // check for is swming? does it mean if player one block in water or need to check, slime changes speed, and also cobweb etc.
        } else if (deltaY == 0.0) {
            // isn't jumping or doing anything, ignore
        } else if (deltaY < 0) {
            if (!(previousHeight.containsKey(player.getName()))) {
                previousHeight.put(player.getName(), playerY);
                //player.sendMessage("Placed playerY in hash." + deltaY);
            }
            // entity is falling, you can make a check but if it's too fast, like make a check whether it's a 1 block distance and then check if the speed is one of these: -0.078 -0.155 -0.231 -0.377.
        } else if (((((float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp)) == (0.0 +(jumpboost_amplifier*0.5)) )) || ((deltaY <= (0.42 + (0.10000000149 * jumpboost_amplifier)) && (deltaY >= (0.4+ (0.10000000149 * jumpboost_amplifier)))))) && (!(isEverythingInListThatString(list_of_blocks_under_player, "AIR")))) { // checking that the player hadn't jumped over 1 block, cause it isn't possible
            //player.sendMessage("You jumped! " + deltaY);
            trackJump(String.valueOf(player), (new Float(0.333)));

        } else if ((deltaY == 0.5 && (containsWord(list_of_blocks_under_player, "STEP")) || (containsWord(list_of_blocks_under_player, "STAIRS")) || (containsWord(list_of_blocks_under_player, "FENCE")) || (containsWord(list_of_blocks_under_player, "SKULL"))) || ((deltaY == 0.75) && (containsWord(list_of_blocks_under_player, "SKULL"))) || ((deltaY == 0.1875) && (containsWord(list_of_blocks_under_player, "TRAP_DOOR"))) || ((deltaY_formatted_double == 0.118) && (containsWord(list_of_blocks_under_player, "LADDER"))) || ((deltaY == 0.3125) && (containsWord(list_of_blocks_under_player, "STEP")) || (containsWord(list_of_blocks_under_player, "TRAP_DOOR"))) || ((deltaY == 0.125) && (containsWord(list_of_blocks_under_player, "REDSTONE")))) {
            // walking on weird objects
            //player.sendMessage("Walking on off objects");
        } else if ((containsWord(list_of_blocks_Y3_player, "LAVA") || containsWord(list_of_blocks_Y3_player, "WATER")) && (deltaY < 0.34002)) {
            //player.sendMessage("In a liquid.: " + list_of_blocks_Y3_player);
        } else {
            // note that the slime handling should be exactly before this
            player.sendMessage("Hacking. " + deltaY);
            //player.sendMessage(list_of_blocks_under_player + "");
            //player.sendMessage((previousHeight.containsKey(player.getName()) + " <- value previousheight: contains: " +containsWord(blocksUnderPlayerLOWER, "SLIME")));
            //player.sendMessage(ChatColor.DARK_AQUA + "DeltaY: " + deltaY);
        }






        // now for speed we shall check.


    }

    // make a check that they don't surpass the max value of going up, unless in creative mode

    //int speed = 0;

    // for speed potions, their amplifier times 0.056 and then added to the default speed which shall depend on whether sneaking, regualr or spriting
    // for jump boost it's 0.098(0.10000000149) times the amplifier



//    public static List<String> getBlocksUnderneathPlayer(Player player) {
//        List<String> blocks = new ArrayList<>();
//        Location location = player.getLocation().clone();
//        for (int x = -1; x <= 1; x++) {
//            for (int z = -1; z <= 1; z++) {
//                Location loc = location.clone().add(x, -0.1875, z); // y value is always -1
//                //player.sendMessage(loc.getBlock().getType().name() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
//
//                if (loc.getBlock().getType() != AIR) {
//                    blocks.add(loc.getBlock().getType().name());
//                }
//            }
//        }
//        return blocks;
//    }





    public static List<String> getBlocksUnderneathPlayer(Player player) {
        List<String> blocks = new ArrayList<>();
        Location location = player.getLocation().clone();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = location.clone().add(x, 1.1875, z); // y value is always -1
                //player.sendMessage(loc.getBlock().getType().name() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                if (loc.getBlock().getType() != AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }

                // Also check the block at y = 1
                loc = location.clone().add(x, 0.1875, z);
                if (loc.getBlock().getType() != AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }
                loc = location.clone().add(x, -0.1875, z);
                if (loc.getBlock().getType() != AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }

                loc = location.clone().add(x, -1.1875, z);
                if (loc.getBlock().getType() != AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }
                loc = location.clone().add(x, -2.1875, z);
                if (loc.getBlock().getType() != AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }
            }
        }
        return blocks;
    }

    public static List<String> getBlocksUnderPlayerLOWER(Player player) { // for slime check
        List<String> blocks = new ArrayList<>();
        Location location = player.getLocation().clone();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = location.clone().add(x, -0.1875, z); // y value is always -1
                //player.sendMessage(loc.getBlock().getType().name() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ());

                //if (loc.getBlock().getType() != AIR) {
                //    blocks.add(loc.getBlock().getType().name());
                //}

                // Also check the block at y = 1
                loc = location.clone().add(x, -0.1875, z);
                if (loc.getBlock().getType() != AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }

                // Also check the block at y = 1
                loc = location.clone().add(x, -1.1875, z);
                if (loc.getBlock().getType() != AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }
                loc = location.clone().add(x, -2.1875, z);
                if (loc.getBlock().getType() != AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }
                loc = location.clone().add(x, -3.1875, z);
                if (loc.getBlock().getType() != AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }
            }
        }
        return blocks;
    }

    public static double predictedJumpHeight(double FromYValue) {
        return ((-0.0011 * pow(FromYValue, 2)) + (0.43529 * FromYValue) + 1.7323);
    }

    public static List<String> GetBlocksInPlayerY3(Player player) {
        List<String> blocks = new ArrayList<>();
        Location location = player.getLocation().clone();

        blocks.add(String.valueOf(player.getLocation().clone().add(0, +1, 0).getBlock().getType()));
        blocks.add(String.valueOf(player.getLocation().clone().add(0, 0, 0).getBlock().getType()));
        blocks.add(String.valueOf(player.getLocation().clone().add(0, -1, 0).getBlock().getType()));

        return blocks;
    }


    public static List<String> getBlocksOnTopPlayer(Player player) {
        List<String> blocks = new ArrayList<>();
        Location location = player.getLocation().clone();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = location.clone().add(x, 2, z); // y value is always -1
                //player.sendMessage(loc.getBlock().getType().name() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ());
                blocks.add(loc.getBlock().getType().name());

                // Also check the block at y = 1
                loc = location.clone().add(x, 1, z);
                blocks.add(loc.getBlock().getType().name());
            }
        }
        return blocks;
    }

    public static Material getBlockOntoPlayer(Player player) { // you can just compare to BLOCK, e.g. getBlockOntoPlayer == AIR, and if the block is air then is true.
        return player.getLocation().clone().add(0, +1.8125, 0).getBlock().getType();
    }

    public static double getPotionModifier(Player player, PotionEffectType effectName) {
        double Modifier = 0;
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(effectName)) { // e.g. PotionEffectType.SPEED
                Modifier += effect.getAmplifier() + 1;
            }
        }
        return Modifier;
    }
    public static boolean containsWord(List<String> listOfBlocksUnderPlayer, String word) {
        for (String block : listOfBlocksUnderPlayer) {
            if (block.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEverythingInListThatString(List<String> listOfBlocksUnderPlayer, String block) { // you give a list, and it checks if everything is that string, if not, returns false
        for (String blockUnderPlayer : listOfBlocksUnderPlayer) {
            if (!blockUnderPlayer.toLowerCase().equals(block.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    public static String getBlockUnderneathPlayer(Player player) {
        Location location = player.getLocation().clone();
        location.subtract(0, 0.1875, 0);
        return location.getBlock().getType().name();
    }
    public static int getBlockYUnderneathPlayer(Player player) {
        Location location = player.getLocation().clone();
        int check = 0;
        int y = 0;
        while (location.getBlock().getType() == AIR) {
            location.subtract(0, 1, 0);
            if (location.getY() < 1) {
                check = 1;
                break;
            }
            y++;
        }
        if (check == 1) {
            return 0; // as in no block was found
        } else {
            return location.getBlock().getY();
        }
    }


    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        double actualVelocityX = event.getVelocity().getX();
        double actualVelocityY = event.getVelocity().getY();
        double actualVelocityZ = event.getVelocity().getZ();

        // Calculate the expected velocity of the player based on their movement input and the physics of the game.
        double expectedVelocityX = getExpectedVelocityX(player);
        double expectedVelocityY = getExpectedVelocityY(player);
        double expectedVelocityZ = 0;

        // Track the player's movement over time.
        List<Double[]> velocities = new ArrayList<>();
        velocities.add(new Double[]{actualVelocityX, actualVelocityY, actualVelocityZ});

        // Take into account the player's momentum.
        double momentumX = 0;
        double momentumY = 0;

        // Use the Kalman filter to predict the player's future velocity.
        // double[] predictedVelocity = kalmanFilter.predict(velocities, momentumX, momentumY);

        // Compare the expected velocity with the actual velocity and the predicted velocity.
        if (abs(expectedVelocityX - actualVelocityX) > THRESHOLD || abs(expectedVelocityY - actualVelocityY) > THRESHOLD || abs(expectedVelocityZ - actualVelocityZ) > THRESHOLD) {
            // The player is using a velocity modifier.
            //player.sendMessage("You are using a velocity modifier!");
        }
    }

    private double getExpectedVelocityX(Player player) {
        Vector direction = player.getLocation().getDirection();
        double x = direction.getX();
        double y = direction.getY();
        return x * player.getWalkSpeed() * -1;
    }

    private double getExpectedVelocityY(Player player) {
        double fallDistance = player.getFallDistance();
        return Math.sqrt(fallDistance);
    }

    public void trackJump(String username, float deltaY) {
        jumpTracker.put(username, deltaY);
    }

    public double getExpectedDeltaY(String username) {
        if (jumpTracker.containsKey(username)) {
            double deltaY = jumpTracker.get(username);
            jumpTracker.remove(username);
            return deltaY;
        } else {
            return 0.0;
        }
    }


    private void printReach(Player damager, LivingEntity victim, double hitDistance, double vm) {
        String damagerName = damager.getName();
        String victimName = victim.getName();
        if (!playerReach.containsKey(victimName)) {
            playerReach.put(victimName, new HashMap<>());
        }
        playerReach.get(victimName).put(damager.getName(), hitDistance);

        if (!playerReach.containsKey(damager.getName())) {
            playerReach.put(damager.getName(), new HashMap<>());
        }
        playerReach.get(damager.getName()).put(victim.getName(), vm);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (printreachEnabled.containsKey(player.getName()) && printreachEnabled.get(player.getName()).contains(damagerName)) {
                TextComponent mainComponent = new TextComponent(ChatColor.YELLOW + damagerName + " Reach: " + ChatColor.WHITE + formatDouble(hitDistance) + ChatColor.RESET + ChatColor.ITALIC + "" + " (Hover to see details)");
                mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(getReachDetails(player, victim)).create()));
                player.spigot().sendMessage(mainComponent);
            }
        }
    }

    private String getReachDetails(Player player, LivingEntity victim) {
        StringBuilder details = new StringBuilder();
        details.append(ChatColor.GRAY).append("Player: ").append(ChatColor.WHITE).append(player.getName()).append("\n");
        details.append(ChatColor.GRAY).append("Player's Reach: ").append(ChatColor.WHITE).append(formatDouble(playerReach.get(victim.getName()).get(player.getName()))).append(" blocks\n");
        details.append(ChatColor.GRAY).append("Victim: ").append(ChatColor.WHITE).append(victim.getName()).append("\n");
        details.append(ChatColor.GRAY).append("Victim's Reach: ").append(ChatColor.WHITE).append(formatDouble(playerReach.get(player.getName()).get(victim.getName()))).append(" blocks\n");

        return details.toString();
    }

    private String formatDouble(double value) {
        return BigDecimal.valueOf(value).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
    }
    private String formatTriple(double value) {
        return BigDecimal.valueOf(value).setScale(3, BigDecimal.ROUND_HALF_UP).toString();
    }
}