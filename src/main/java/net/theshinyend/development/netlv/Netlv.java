package net.theshinyend.development.netlv;

import java.util.Random;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.*;
import static org.bukkit.GameMode.*;
import static org.bukkit.Material.AIR;

public class Netlv extends JavaPlugin implements Listener {
    private Plugin plugin;

    private final HashMap<String, HashMap<String, Double>> playerReach = new HashMap<>();

    private final HashMap<String, List<Double>> PlayerDeltaAimList = new HashMap<>();
    private final HashMap<String, List<Location>> PlayerHitpointList = new HashMap<>();
    private final HashMap<String, Double> PreviousBlockPlacementsYComp = new HashMap<>();
    private final HashMap<String, Double> OmniSprintCheck = new HashMap<>();
    private final HashMap<String, Double> AirFrictionCheck = new HashMap<>();
    private final HashMap<String, Double> AirTimeInTicks = new HashMap<>();
    private final HashMap<String, Boolean> explosionTickIgnoreairspeed = new HashMap<>();

    private final HashMap<String, Double> LastTimeSinceTeleportinTicks = new HashMap<>();
    private final HashMap<String, Double> LastTimeSinceJumpInTicks = new HashMap<>();
    private final HashMap<String, Double> LastTimeSinceSprintingInTicks = new HashMap<>();
    private final HashMap<String, Double> SafeWalkCheckZ = new HashMap<>();
    private final HashMap<String, Double> SafeWalkCheckX = new HashMap<>();
    private final HashMap<String, Double> SafeWalkCheckA = new HashMap<>();
    private final HashMap<String, Double> Interact6_double = new HashMap<>();
    private final HashMap<String, Location> Interact6_double_blockSelected = new HashMap<>();


    private final HashMap<String, Location> PreviousBlockPlacementLocation = new HashMap<>();

    private final HashMap<String, Double> totalAlertsinlast10seconds = new HashMap<>();
    private final HashMap<String, Double> previousVelocityY = new HashMap<>();


    private final HashMap<String, Pair<Double, Double>> playerXZ = new HashMap<>();
    private final HashMap<String, Boolean> isPlayerMoving = new HashMap<>();
    private final HashMap<String, Double> noVelYVerbose = new HashMap<>();
    private final HashMap<String, Double> SpeedVerbose = new HashMap<>();

    private final HashMap<String, Double> recentDeltaYaw = new HashMap<>();
    private final HashMap<String, Double> recentDeltaPitch = new HashMap<>();
    private final HashMap<String, Integer> InvmoveVerbose = new HashMap<>();
    private final HashMap<String, Integer> playerReachThreshold = new HashMap<>();
    private final HashMap<String, Double> MaxSetKnockbackheight = new HashMap<>();


//    playerXZ.put("Player1", Pair.of(10.0, 20.0));
//    playerXZ.put("Player2", Pair.of(30.0, 40.0));
//
//    Pair<Double, Double> player1Coordinates = playerXZ.get("Player1");
//    Double player1X = player1Coordinates.getLeft();
//    Double player1Z = player1Coordinates.getRight();


    private final HashMap<String, Set<String>> printreachEnabled = new HashMap<>();
    private final Map<String, Boolean> identifieralerts = new HashMap<>();

    private final HashMap<String, Double> previousDeltaY = new HashMap<>();
    private final HashMap<String, Location> previousLocationInventory = new HashMap<>();
    private Map<String, List<Pair<AxisAlignedBB, Location>>> playerPositions;

    private final Map<String, Double> previousAccelXZ = new HashMap<>();
    private final Map<String, Double> previousDeltaX = new HashMap<>();

    private final Map<String, Double> velocityXZ = new HashMap<>();

    private final Map<String, Double> previousDeltaZ = new HashMap<>();
    private final Map<String, Double> PlayerClicksRIGHT = new HashMap<>();
    private final Map<String, Double> PlayerClicksLEFT = new HashMap<>();
    private final Map<String, List<Double>> PlayerCPSLeftList = new HashMap<>();
    private final Map<String, List<Double>> PlayerCPSRightList = new HashMap<>();

//    private Map<String, List<Double>, List<Location>> PlayerPositionDetailsDelta = new HashMap<>();


    private final HashMap<String, Float> jumpTracker = new HashMap<>();
    private final HashMap<String, Boolean> FallingSlowly = new HashMap<>();
    private final HashMap<String, Boolean> FallingWeirdly = new HashMap<>();
    private final HashMap<String, Boolean> ShouldBeFalling = new HashMap<>();

    private final HashMap<String, Boolean> wasOnSlimeBlock = new HashMap<>();
    private final HashMap<String, Double> previousHeight = new HashMap<>();
    private final HashMap<String, Location> previousLocation_onGround = new HashMap<>();


    private final HashMap<String, Double> slimeHeight = new HashMap<>();
    private final HashMap<String, Double> AlertsTypeSafe = new HashMap<>(); //  high, basically meaning that there are barely any falses to them
    private final HashMap<String, Double> AlertsTypeSpam = new HashMap<>(); // med, flight, speed etc those that have a LOT of alerts per second
    private final HashMap<String, Double> AlertsTypeUnsolidified = new HashMap<>(); // low, these are those that are risky to ban, e.g. movement 1, air-speed etc.
    private final HashMap<String, Double> AlertsTypeUndetermined = new HashMap<>(); // these are for those that i am unsure that i need to ban for.
    private final HashMap<String, Double> AlertsTypeCombat = new HashMap<>(); // these are for those that i am unsure that i need to ban for.



    private final HashMap<String, Location> previous_location = new HashMap<>();
    private final HashMap<String, Double> previous_airspeed1 = new HashMap<>();

    private final HashMap<String, List<String>> keys_pressed = new HashMap<>();

    private final HashMap<Double, AxisAlignedBB> hitboxSaved_victim = new HashMap<>();


    private int verbose_hitbox = 0;
    private int alerts_hitbox = 0;
    private final int speed_buffer = 0;
    private final Boolean AlertBukkitChannel = true; // if false will disable alerts no matter enabled / disabled
    private Boolean HideHealth = false;
    private Boolean GhostBlockFix_8by8Intense = false;
    private Boolean disableMovement = false;

    private static final double THRESHOLD = 0.1;
    private static double numHitbox = 0;
    private int hitbox_in_a_row_check = 0;
    private final Map<String, Double> movement_identifications = new HashMap<>();
    private final Map<String, Double> isBridging = new HashMap<>();


    private final Map<String, List<PlayerSavedLocDetails>> playerPositionDetailsDelta = new HashMap<>();

    private final ExecutorService positionSavingExecutor = Executors.newFixedThreadPool(1); // Adjust the number of threads as needed
    private final ExecutorService positionSavingExecutor2 = Executors.newFixedThreadPool(1); // Adjust the number of threads as needed


    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean development_mode = false; // aka dev alerts to be enabled or not.
    private boolean test_mode = false;
    private boolean cancelorsetback = false;
    private boolean AlertAutoEnabled_player = false;


    private String commandrun = ""; // command after alerts = max and need to ban or kick the player., {player} will be replaced to the player name, {reason} to be either combat or whatever
    private String broadcastmsg = ""; // when executed, show this msg if "" ignore.
    private boolean FastPlace = false;
    private boolean Interact1 = false;
    private boolean Interact2 = false;
    private boolean Interact3 = false;
    private boolean Interact4 = false;
    private boolean Interact5 = false;
    private boolean Interact6 = false;


    private boolean Reach = false;
    private boolean Hitbox = false;
    private boolean Scaffold = false;

    private boolean Inventory_move = false;

    private boolean Phase = false;
    private boolean Invalid_pitch = false;

    private boolean Flight_H = false;
    private boolean Flight_G = false;
    private boolean Flight_F = false;
    private boolean Flight_E = false;
    private boolean Tumbling = false;
    private boolean Constant_B2 = false;
    private boolean Slime_C = false;
    private boolean Constant_B1 = false;
    private boolean Height_A2 = false;
    private boolean Height_A1 = false;
    private boolean Movement_I = false;
    private boolean Movement_II = false;
    private boolean Motion_Y = false;

    private boolean OmniSprint = false;
    private boolean Jump_A = false;
    private boolean Air_Friction = false;
    private boolean Air_Changes = false;
    private boolean Air_Speed = false;

    private boolean Safewalk = false;
    private boolean Safewalk_near = false;

    private boolean Speed_balancer_air = false;
    private boolean Speed = false;

    private boolean Jesus_I = false;
    private boolean Jesus_II = false;

    @Override
    public void onEnable() {
        playerPositions = new HashMap<>();
        this.plugin = this;


        FastPlace = get_check("FastPlace", true);
        Interact1 = get_check("Interact 1", true);
        Interact2 = get_check("Interact 2", true);
        Interact3 = get_check("Interact 3", true);
        Interact4 = get_check("Interact 4", true);
        Interact5 = get_check("Interact 5", true);
        Interact6 = get_check("Interact 6", true);

        Reach = get_check("Reach", true);
        Hitbox = get_check("Hitbox", true);
        Scaffold = get_check("Scaffold", true);

        Inventory_move = get_check("Inventory-move", true);

        Phase = get_check("Phase", true);
        Invalid_pitch = get_check("Invalid-pitch", true);

        Flight_H = get_check("Flight H", true);
        Flight_G = get_check("Flight G", true);
        Flight_F = get_check("Flight F", true);
        Flight_E = get_check("Flight E", true);
        Tumbling = get_check("Tumbling", true);
        Constant_B2 = get_check("Constant B2", true);
        Slime_C = get_check("Slime C", true);
        Constant_B1 = get_check("Constant B1", true);
        Height_A2 = get_check("Height A2", true);
        Height_A1 = get_check("Height A1", true);
        Movement_I = get_check("Movement I", true);
        Movement_II = get_check("Movement II", true);
        Motion_Y = get_check("Motion Y", true);

        OmniSprint = get_check("OmniSprint", true);
        Jump_A = get_check("Jump A", true);
        Air_Friction = get_check("Air Friction", true);
        Air_Changes = get_check("Air Changes", true);
        Air_Speed = get_check("Air Speed", true);

        Safewalk = get_check("Safewalk", true);
        Safewalk_near = get_check("Safewalk near", true);

        Speed_balancer_air = get_check("Speed balancer air", true);
        Speed = get_check("Speed", true);

        Jesus_I = get_check("Jesus-I", true);
        Jesus_II = get_check("Jesus-II", true);

        development_mode = get_check("development_mode", false);
        test_mode = get_check("test_mode", false);
        cancelorsetback = get_check("cancel/setback", false);
        HideHealth = get_check("HidePlayersHealth", false);
        GhostBlockFix_8by8Intense = get_check("GhostBlocksFix(heavy)", false);
        AlertAutoEnabled_player = get_check("AlertsAutoEnable", false);




        if (getConfig().get("command") != null) {
            commandrun = getConfig().getString("command");
            Bukkit.getLogger().info("Command to run " + commandrun);
        }
        if (getConfig().get("broadcastMessage") != null) {
            broadcastmsg = getConfig().getString("broadcastMessage");
        }


        for (Player player : Bukkit.getOnlinePlayers()) {
            if ((player.hasPermission("netlv.joinalert")) && (AlertAutoEnabled_player)) {
                boolean enabled = identifieralerts.computeIfAbsent(player.getName(), k -> false);
                if (!enabled) {
                    identifieralerts.put(player.getName(), true);
                    player.sendMessage(ChatColor.WHITE + "[" + ChatColor.YELLOW + "AntiCheat" + ChatColor.WHITE + "] " + ChatColor.AQUA + "- " + ChatColor.WHITE + "Identifier alerts auto enabled.");
                }
            }
        }
        startPositionSavingTask();
        HideHPRunalltime();
//        runalltime();
        runEvery1Second();
        getServer().getPluginManager().registerEvents(this, this);
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                movement_identifications.clear();
                verbose_hitbox = 0;
                alerts_hitbox = 0;
                hitbox_in_a_row_check = 0;
            }
        }, 0, 10, TimeUnit.MINUTES);

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    totalAlertsinlast10seconds.remove(player.getName());
                }

                for (String playerName : AlertsTypeCombat.keySet()) {
                    double combat = AlertsTypeCombat.computeIfAbsent(playerName, k -> 0.0);
//                    Bukkit.getPlayer(playerName).sendMessage(ChatColor.RED + "Combat: " + combat);

                    if (combat > 4) {
                        performCommand(commandrun.replace("{player}", playerName).replace("{reason}", "comabt"));
                    }
                    AlertsTypeCombat.put(playerName, 0.0);
                }
                for (String playerName : AlertsTypeUndetermined.keySet()) {
                    double undetermined = AlertsTypeUndetermined.computeIfAbsent(playerName, k -> 0.0);
//                    Bukkit.getPlayer(playerName).sendMessage(ChatColor.RED + "Undetermined: " + undetermined);
                    if (undetermined > 3) {
                        performCommand(commandrun.replace("{player}", playerName).replace("{reason}", "undetermined"));
                    }
                    AlertsTypeUndetermined.put(playerName, 0.0);
                }

                for (String playerName : AlertsTypeSpam.keySet()) {
                    double spam = AlertsTypeSpam.computeIfAbsent(playerName, k -> 0.0);
//                    Bukkit.getPlayer(playerName).sendMessage(ChatColor.RED + "Spam: " + spam);
                    if (spam > 6) {
                        performCommand(commandrun.replace("{player}", playerName).replace("{reason}", "spam"));
                    }
                    AlertsTypeSpam.put(playerName, 0.0);
                }

                for (String playerName : AlertsTypeUnsolidified.keySet()) {
                    double unsafe = AlertsTypeUnsolidified.computeIfAbsent(playerName, k -> 0.0);
//                    Bukkit.getPlayer(playerName).sendMessage(ChatColor.RED + "Unsafe: " + unsafe);
                    if (unsafe > 8) {
                        performCommand(commandrun.replace("{player}", playerName).replace("{reason}", "unsafe"));
                    }
                    AlertsTypeUnsolidified.put(playerName, 0.0);
                }

                for (String playerName : AlertsTypeSafe.keySet()) {
                    double safe = AlertsTypeSafe.computeIfAbsent(playerName, k -> 0.0);
//                    Bukkit.getPlayer(playerName).sendMessage(ChatColor.RED + "Safe: " + safe);
//                    String commandTL = commandrun.replace("{player}", playerName).replace("{reason}", "comabt");
                    if (safe > 4) {
                        performCommand(commandrun.replace("{player}", playerName).replace("{reason}", "safe"));
                    }
                    AlertsTypeSafe.put(playerName, 0.0);
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
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
//        if (!(player.hasPermission("
//        netlv.*"))) { sender.sendMessage(ChatColor.DARK_RED + "Invalid."); return false; }
        // above is a permission check.

        if (command.getName().equalsIgnoreCase("printreach")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/printreach <player>");
                return false;
            }
            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.DARK_RED + "Player '" + ChatColor.RED + targetPlayerName + ChatColor.DARK_RED + "' not found.");
                return false;
            }
            Set<String> enabledPlayers = printreachEnabled.computeIfAbsent(player.getName(), k -> new HashSet<>());
            if (enabledPlayers.contains(targetPlayerName)) {
                enabledPlayers.remove(targetPlayerName);
                player.sendMessage(ChatColor.BLUE + "You'll no longer get notified for " + targetPlayerName + ".");
            } else {
                enabledPlayers.add(targetPlayerName);
                player.sendMessage(ChatColor.DARK_AQUA + "Okay, You'll be getting notified for attack ranges for player " + ChatColor.AQUA + targetPlayerName);
            }
            return true;

        } else if (command.getName().equalsIgnoreCase("keys")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/keys <player>");
                return false;
            }
            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.DARK_RED + "Player '" + ChatColor.RED + targetPlayerName + ChatColor.DARK_RED + "' not found.");
                return false;
            }
            if (keys_pressed.containsKey(player.getName())) {
                player.sendMessage("That player is pressing: " + ChatColor.AQUA + keys_pressed.get(player.getName()).toString());
            } else {
                player.sendMessage(ChatColor.RED + "That player hasn't touched their keyboard since startup.");
            }
            return true;

        } else if (command.getName().equalsIgnoreCase("netlv")) {
            if (args.length == 0) {
                sendHelpPageNetlv(player);
                return false;
            }
            String task = args[0];

            if (task.equalsIgnoreCase("toggledevmode")) {
                if (!development_mode) {
                    player.sendMessage(ChatColor.DARK_AQUA + "Toggling development mode on, identifiers that are not complete will flag.");
                    development_mode = true;
                    getConfig().set("development_mode", true);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                } else if (development_mode) {
                    player.sendMessage(ChatColor.RED + "Toggling development mode off");
                    development_mode = false;
                    getConfig().set("development_mode", false);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                } else {
                    player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/netlv toggledevmode");
                }
            } else if ((task.equalsIgnoreCase("check")) || (task.equalsIgnoreCase("checks")) || (task.equalsIgnoreCase("togglecheck"))) {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /netlv check <checkName>");
                    return false;
                }

                String checkName = args[1];
//                lists of checks
//                private boolean FastPlace = false;
//                private boolean Interact1 = false;
//                private boolean Interact2 = false;
//                private boolean Interact3 = false;
//                private boolean Interact4 = false;
//                private boolean Interact5 = false;
//                private boolean Interact6 = false;
//
//                private boolean Reach = false;
//                private boolean Hitbox = false;
//                private boolean Scaffold = false;
//
//                private boolean Inventory_move = false;
//
//                private boolean Phase = false;
//                private boolean Invalid_pitch = false;
//
//                private boolean Flight_H = false;
//                private boolean Flight_G = false;
//                private boolean Flight_F = false;
//                private boolean Flight_E = false;
//                private boolean Tumbling = false;
//                private boolean Constant_B2 = false;
//                private boolean Slime_C = false;
//                private boolean Constant_B1 = false;
//                private boolean Height_A2 = false;
//                private boolean Height_A1 = false;
//                private boolean Movement_I = false;
//                private boolean Movement_II = false;
//                private boolean Motion_Y = false;
//
//                private boolean OmniSprint = false;
//                private boolean Jump_A = false;
//                private boolean Air_Friction = false;
//                private boolean Air_Changes = false;
//                private boolean Air_Speed = false;
//
//                private boolean Safewalk = false;
//                private boolean Safewalk_near = false;
//
//                private boolean Speed_balancer_air = false;
//                private boolean Speed = false;
//
//                private boolean Jesus_I = false;
//                private boolean Jesus_II = false;

                if (checkName.equalsIgnoreCase("FastPlace")) {
                    FastPlace = !FastPlace;
                    set("FastPlace", FastPlace);
                    player.sendMessage("Toggled FastPlace: " + FastPlace);
                } else if (checkName.equalsIgnoreCase("Interact1")) {
                    Interact1 = !Interact1;
                    set("Interact 1", Interact1);
                    player.sendMessage("Toggled Interact1: " + Interact1);
                } else if (checkName.equalsIgnoreCase("Interact2")) {
                    Interact2 = !Interact2;
                    set("Interact 2", Interact2);
                    player.sendMessage("Toggled Interact2: " + Interact2);
                } else if (checkName.equalsIgnoreCase("Interact3")) {
                    Interact3 = !Interact3;
                    set("Interact 3", Interact3);
                    player.sendMessage("Toggled Interact3: " + Interact3);
                } else if (checkName.equalsIgnoreCase("Interact4")) {
                    Interact4 = !Interact4;
                    set("Interact 4", Interact4);
                    player.sendMessage("Toggled Interact4: " + Interact4);
                } else if (checkName.equalsIgnoreCase("Interact5")) {
                    Interact5 = !Interact5;
                    set("Interact 5", Interact5);
                    player.sendMessage("Toggled Interact5: " + Interact5);
                } else if (checkName.equalsIgnoreCase("Interact6")) {
                    Interact6 = !Interact6;
                    set("Interact 6", Interact6);
                    player.sendMessage("Toggled Interact6: " + Interact6);
                }else if (checkName.equalsIgnoreCase("Reach")) {
                    Reach = !Reach;
                    set("Reach", Reach);
                    player.sendMessage("Toggled Reach: " + Reach);
                } else if (checkName.equalsIgnoreCase("Hitbox")) {
                    Hitbox = !Hitbox;
                    set("Hitbox", Hitbox);
                    player.sendMessage("Toggled Hitbox: " + Hitbox);
                } else if (checkName.equalsIgnoreCase("Scaffold")) {
                    Scaffold = !Scaffold;
                    set("Scaffold", Scaffold);
                    player.sendMessage("Toggled Scaffold: " + Scaffold);
                } else if (checkName.equalsIgnoreCase("Inventory_move")) {
                    Inventory_move = !Inventory_move;
                    set("Inventory Move", Inventory_move);
                    player.sendMessage("Toggled Inventory Move: " + Inventory_move);
                } else if (checkName.equalsIgnoreCase("Phase")) {
                    Phase = !Phase;
                    set("Phase", Phase);
                    player.sendMessage("Toggled Phase: " + Phase);
                } else if (checkName.equalsIgnoreCase("Invalid_pitch")) {
                    Invalid_pitch = !Invalid_pitch;
                    set("Invalid Pitch", Invalid_pitch);
                    player.sendMessage("Toggled Invalid Pitch: " + Invalid_pitch);
                } else if (checkName.equalsIgnoreCase("Flight_H")) {
                    Flight_H = !Flight_H;
                    set("Flight H", Flight_H);
                    player.sendMessage("Toggled Flight H: " + Flight_H);
                } else if (checkName.equalsIgnoreCase("Flight_G")) {
                    Flight_G = !Flight_G;
                    set("Flight G", Flight_G);
                    player.sendMessage("Toggled Flight G: " + Flight_G);
                }  else if (checkName.equalsIgnoreCase("Flight_F")) {
                    Flight_F = !Flight_F;
                    set("Flight F", Flight_F);
                    player.sendMessage("Toggled Flight F: " + Flight_F);
                } else if (checkName.equalsIgnoreCase("Flight_E")) {
                    Flight_E = !Flight_E;
                    set("Flight E", Flight_E);
                    player.sendMessage("Toggled Flight E: " + Flight_E);
                } else if (checkName.equalsIgnoreCase("Tumbling")) {
                    Tumbling = !Tumbling;
                    set("Tumbling", Tumbling);
                    player.sendMessage("Toggled Tumbling: " + Tumbling);
                } else if (checkName.equalsIgnoreCase("Constant_B2")) {
                    Constant_B2 = !Constant_B2;
                    set("Constant B2", Constant_B2);
                    player.sendMessage("Toggled Constant B2: " + Constant_B2);
                } else if (checkName.equalsIgnoreCase("Slime_C")) {
                    Slime_C = !Slime_C;
                    set("Slime C", Slime_C);
                    player.sendMessage("Toggled Slime C: " + Slime_C);
                } else if (checkName.equalsIgnoreCase("Constant_B1")) {
                    Constant_B1 = !Constant_B1;
                    set("Constant B1", Constant_B1);
                    player.sendMessage("Toggled Constant B1: " + Constant_B1);
                } else if (checkName.equalsIgnoreCase("Height_A2")) {
                    Height_A2 = !Height_A2;
                    set("Height A2", Height_A2);
                    player.sendMessage("Toggled Height A2: " + Height_A2);
                } else if (checkName.equalsIgnoreCase("Height_A1")) {
                    Height_A1 = !Height_A1;
                    set("Height A1", Height_A1);
                    player.sendMessage("Toggled Height A1: " + Height_A1);
                } else if (checkName.equalsIgnoreCase("Movement_I")) {
                    Movement_I = !Movement_I;
                    set("Movement I", Movement_I);
                    player.sendMessage("Toggled Movement I: " + Movement_I);
                } else if (checkName.equalsIgnoreCase("Movement_II")) {
                    Movement_II = !Movement_II;
                    set("Movement II", Movement_II);
                    player.sendMessage("Toggled Movement II: " + Movement_II);
                } else if (checkName.equalsIgnoreCase("Motion_Y")) {
                    Motion_Y = !Motion_Y;
                    set("Motion Y", Motion_Y);
                    player.sendMessage("Toggled Motion Y: " + Motion_Y);
                } else if (checkName.equalsIgnoreCase("OmniSprint")) {
                    OmniSprint = !OmniSprint;
                    set("OmniSprint", OmniSprint);
                    player.sendMessage("Toggled OmniSprint: " + OmniSprint);
                } else if (checkName.equalsIgnoreCase("Jump_A")) {
                    Jump_A = !Jump_A;
                    set("Jump A", Jump_A);
                    player.sendMessage("Toggled Jump A: " + Jump_A);
                } else if (checkName.equalsIgnoreCase("Air_Friction")) {
                    Air_Friction = !Air_Friction;
                    set("Air Friction", Air_Friction);
                    player.sendMessage("Toggled Air Friction: " + Air_Friction);
                } else if (checkName.equalsIgnoreCase("Air_Changes")) {
                    Air_Changes = !Air_Changes;
                    set("Air Changes", Air_Changes);
                    player.sendMessage("Toggled Air Changes: " + Air_Changes);
                } else if (checkName.equalsIgnoreCase("Air_Speed")) {
                    Air_Speed = !Air_Speed;
                    set("Air Speed", Air_Speed);
                    player.sendMessage("Toggled Air Speed: " + Air_Speed);
                } else if (checkName.equalsIgnoreCase("Safewalk")) {
                    Safewalk = !Safewalk;
                    set("Safewalk", Safewalk);
                    player.sendMessage("Toggled Safewalk: " + Safewalk);
                } else if (checkName.equalsIgnoreCase("Safewalk_near")) {
                    Safewalk_near = !Safewalk_near;
                    set("Safewalk Near", Safewalk_near);
                    player.sendMessage("Toggled Safewalk Near: " + Safewalk_near);
                } else if (checkName.equalsIgnoreCase("Speed_balancer_air")) {
                    Speed_balancer_air = !Speed_balancer_air;
                    set("Speed Balancer Air", Speed_balancer_air);
                    player.sendMessage("Toggled Speed Balancer Air: " + Speed_balancer_air);
                } else if (checkName.equalsIgnoreCase("Speed")) {
                    Speed = !Speed;
                    set("Speed", Speed);
                    player.sendMessage("Toggled Speed: " + Speed);
                } else if (checkName.equalsIgnoreCase("Jesus_I")) {
                    Jesus_I = !Jesus_I;
                    set("Jesus I", Jesus_I);
                    player.sendMessage("Toggled Jesus I: " + Jesus_I);
                } else if (checkName.equalsIgnoreCase("Jesus_II")) {
                    Jesus_II = !Jesus_II;
                    set("Jesus II", Jesus_II);
                    player.sendMessage("Toggled Jesus II: " + Jesus_II);
                } else {
                    player.sendMessage(ChatColor.RED + "Invalid check name.");
                }



            } else if (task.equalsIgnoreCase("toggletestmode")) {
                if (!test_mode) {
                    player.sendMessage(ChatColor.DARK_AQUA + "Toggling test mode on.");
                    test_mode = true;
                    getConfig().set("test_mode", true);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                } else {
                    player.sendMessage(ChatColor.RED + "Toggling test mode off");
                    test_mode = false;
                    getConfig().set("test_mode", false);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                }
            } else if (task.equalsIgnoreCase("togglecancelorsetback")) {
                if (!cancelorsetback) {
                    player.sendMessage(ChatColor.DARK_AQUA + "Toggling cancel/setback on.");
                    cancelorsetback = true;
                    getConfig().set("cancel/setback", true);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                } else {
                    player.sendMessage(ChatColor.RED + "Toggling cancel/setback off");
                    cancelorsetback = false;
                    getConfig().set("cancel/setback", false);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                }
            } else if (task.equalsIgnoreCase("setcommand")) {
                if (args.length > 1 && !args[1].equalsIgnoreCase("")) {
                    commandrun = String.join(" ", args).replace("setcommand ", "");
                    player.sendMessage(ChatColor.GREEN + "Command set to: " + ChatColor.YELLOW + commandrun);
                    getConfig().set("command", commandrun);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                } else {
                    player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/netlv setcommand <msg>");
                    player.sendMessage(ChatColor.RED + "   - Current set: " + ChatColor.LIGHT_PURPLE + commandrun);
                }
            } else if (task.equalsIgnoreCase("setbroadcastmessage")) {
                if (args.length > 1 && !args[1].equalsIgnoreCase("")) {
                    broadcastmsg = String.join(" ", args).replace("setbroadcastmessage ", "");
                    player.sendMessage(ChatColor.GREEN + "Broadcast set to: " + ChatColor.YELLOW + broadcastmsg);
                    getConfig().set("broadcastMessage", broadcastmsg);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                } else {
                    player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/netlv setbroadcastmessage <msg>");
                }
            } else if (task.equalsIgnoreCase("togglehidehealth")) {
                if (!HideHealth) {
                    player.sendMessage(ChatColor.DARK_AQUA + "Toggling Hide Players Health on.");
                    HideHealth = true;
                    getConfig().set("HidePlayersHealth", true);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                } else {
                    player.sendMessage(ChatColor.RED + "Toggling Hide Players Health off");
                    HideHealth = false;
                    getConfig().set("HidePlayersHealth", false);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                }
            } else if (task.equalsIgnoreCase("toggleghostblockfix")) {
                if (!GhostBlockFix_8by8Intense) {
                    player.sendMessage(ChatColor.DARK_AQUA + "Toggling Ghost Blocks Fix (heavy) on.");
                    GhostBlockFix_8by8Intense = true;
                    getConfig().set("GhostBlocksFix(heavy)", true);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                } else {
                    player.sendMessage(ChatColor.RED + "Toggling Ghost Blocks Fix (heavy) off");
                    GhostBlockFix_8by8Intense = false;
                    getConfig().set("GhostBlocksFix(heavy)", false);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                }
            } else if (task.equalsIgnoreCase("togglealertautoenable")) {
                if (!AlertAutoEnabled_player) {
                    player.sendMessage(ChatColor.DARK_AQUA + "Toggling Alerts Auto Enable on.");
                    AlertAutoEnabled_player = true;
                    getConfig().set("AlertsAutoEnable", true);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                } else {
                    player.sendMessage(ChatColor.RED + "Toggling Alerts Auto Enable off");
                    AlertAutoEnabled_player = false;
                    getConfig().set("AlertsAutoEnable", false);
                    getConfig().options().copyDefaults(false);
                    saveConfig();
                }
            } else if (task.equalsIgnoreCase("knockbacktest")) {
                if (args.length > 1 && !args[1].equalsIgnoreCase("")) {
                    String target_name = args[1];
                    final Player targetPL = Bukkit.getServer().getPlayer(target_name);
                    if (targetPL == null) {
                        player.sendMessage(ChatColor.DARK_RED + "Selected player, not found.");
                        return false;
                    }
                    knockbacktest_player(targetPL);

                } else {
                    player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/netlv knockbacktest <player>");
                }
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("identifiers")) {
            if (args.length != 0) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/identifiers");
                return false;
            }
            String targetPlayerName = sender.getName();

            boolean enabled = identifieralerts.computeIfAbsent(player.getName(), k -> false);

            if (enabled) {
                identifieralerts.put(player.getName(), false);
                player.sendMessage(ChatColor.RED + "You've disabled identifier notifications for all players.");
            } else {
                identifieralerts.put(player.getName(), true);
                player.sendMessage(ChatColor.DARK_AQUA + "You've enabled identifier notifications for all players.");
            }
            return true;

        } else if (command.getName().equalsIgnoreCase("ca")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/ca <player>");
                return false;
            }
            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.DARK_RED + "Player '" + ChatColor.RED + targetPlayerName + ChatColor.DARK_RED + "' not found.");
                return false;
            }
            calculateAim(targetPlayer, (Player) sender);


        } else if (command.getName().equalsIgnoreCase("clcps")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/clcps <player>");
                return false;
            }
            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.DARK_RED + "Player '" + ChatColor.RED + targetPlayerName + ChatColor.DARK_RED + "' not found.");
                return false;
            }
            calculateCPSLeft(targetPlayer, (Player) sender);
        } else if (command.getName().equalsIgnoreCase("crcps")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/crcps <player>");
                return false;
            }
            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.DARK_RED + "Player '" + ChatColor.RED + targetPlayerName + ChatColor.DARK_RED + "' not found.");
                return false;
            }
            calculateCPSRight(targetPlayer, (Player) sender);
        } else if (command.getName().equalsIgnoreCase("cps")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/cps <player>");
                return false;
            }
            String targetPlayerName = args[0];
            Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
            if (targetPlayer == null) {
                player.sendMessage(ChatColor.DARK_RED + "Player '" + ChatColor.RED + targetPlayerName + ChatColor.DARK_RED + "' not found.");
                return false;
            }
            List<Double> leftList = PlayerCPSLeftList.get(targetPlayerName);
            Double latestLeftValue = (leftList != null && !leftList.isEmpty()) ? leftList.get(leftList.size() - 1) : null;

            // Get the latest value of Double from the right list for the given player
            List<Double> rightList = PlayerCPSRightList.get(targetPlayerName);
            Double latestRightValue = (rightList != null && !rightList.isEmpty()) ? rightList.get(rightList.size() - 1) : null;

            // Send the latest values to the player
            player.sendMessage(ChatColor.YELLOW + "Latest CPS values for player " + targetPlayerName + ":");

            if (latestLeftValue != null) {
                player.sendMessage(ChatColor.GREEN + "Left CPS: " + ChatColor.WHITE + latestLeftValue);
            } else {
                player.sendMessage(ChatColor.GREEN + "Left CPS: " + ChatColor.WHITE + "Undefined");
            }

            if (latestRightValue != null) {
                player.sendMessage(ChatColor.GREEN + "Right CPS: " + ChatColor.WHITE + latestRightValue);
            } else {
                player.sendMessage(ChatColor.GREEN + "Right CPS: " + ChatColor.WHITE + "Undefined");
            }
        } else if (command.getName().equalsIgnoreCase("gamestate")) {
            if (args.length != 2) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/gamestate <value> <value>");
                return false;
            }
            byte reason = Byte.parseByte(args[0]);
            float value = Float.parseFloat(args[1]);
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(reason, value));
            player.sendMessage(ChatColor.YELLOW + "Sent game state to " + ChatColor.GREEN + reason + ChatColor.YELLOW + " and " + ChatColor.RED + value);
        } else if (command.getName().equalsIgnoreCase("fog")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/fog <value>");
                return false;
            }
            float value = Float.parseFloat(args[0]);
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(2, 0));
            nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, 1000f));
            nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, value));
            player.sendMessage(ChatColor.YELLOW + "Sent rain game state to " + ChatColor.AQUA + value);
        } else if (command.getName().equalsIgnoreCase("deepfog")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/deepfog <value>");
                return false;
            }
            float value = Float.parseFloat(args[0]);
            EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
            nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(2, 0));
            nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, 10f));
            nmsPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, value));
            player.sendMessage(ChatColor.YELLOW + "Sent deep rain game state to " + ChatColor.DARK_BLUE + value);
        } else if (command.getName().equalsIgnoreCase("biome")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/biome <biome>");
                return false;
            }
            Biome biome = Biome.valueOf(args[0].toUpperCase());
            Block base = player.getLocation().getBlock();
            int[] around = {-2, -1, 0, 1, 2};
            for (int x : around) {
                for (int z : around) {
                    base.getWorld().setBiome(base.getX() + x, base.getZ() + z, biome);
                }
            }
            updateChunkForPlayer(player, base.getChunk());
            player.sendMessage(ChatColor.YELLOW + "Set the biome around you to " + ChatColor.GREEN + biome.name() + ChatColor.YELLOW + "!");
        } else if (command.getName().equalsIgnoreCase("blindness")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/blindness <durationTicks>");
                return false;
            }
            int durationTicks = Integer.parseInt(args[0]);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, durationTicks, 9));
            player.sendMessage(ChatColor.YELLOW + "Added blindness!");
        } else if (command.getName().equalsIgnoreCase("nightvision") || command.getName().equalsIgnoreCase("nv")) {
            if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                player.sendMessage(ChatColor.YELLOW + "Night Vision turned " + ChatColor.RED + "OFF" + ChatColor.YELLOW + "!");
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE / 2, 9));
                player.sendMessage(ChatColor.YELLOW + "Night Vision turned " + ChatColor.GREEN + "ON" + ChatColor.YELLOW + "!");
            }
        } else if (command.getName().equalsIgnoreCase("nvtick")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 5 * 20, 9));
        } else if (command.getName().equalsIgnoreCase("env")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/env <index>");
                return false;
            }
            int index = Integer.parseInt(args[0]);
            World.Environment[] environments = World.Environment.values();
            if (index < 0 || index >= environments.length) {
                player.sendMessage(ChatColor.RED + "Invalid environment index!");
                return false;
            }
            World.Environment environment = environments[index];
            World world = Bukkit.getWorlds().stream()
                    .filter(w -> w.getEnvironment() == environment)
                    .findFirst().orElse(null);
            if (world == null) {
                player.sendMessage(ChatColor.RED + "There is no matching world!");
                return false;
            }
            Location spawnLoc = world.getSpawnLocation();
            if (environment == World.Environment.THE_END) {
                spawnLoc.setY(100);
            }
            player.teleport(spawnLoc);
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Sent you to " + ChatColor.WHITE + environment.name() + ChatColor.YELLOW + "!");
        } else if (command.getName().equalsIgnoreCase("togglemovement")) {
            if (args.length != 0) {
                player.sendMessage(ChatColor.GREEN + "Usage: " + ChatColor.YELLOW + "/togglemovement");
                return false;
            }
            if (disableMovement) {
                disableMovement = false;
                player.sendMessage(ChatColor.LIGHT_PURPLE + "Enabled movement");
                for (Player st : Bukkit.getOnlinePlayers()) {
                    st.sendTitle(String.valueOf(ChatColor.RED), ChatColor.DARK_GREEN + "Movement has been enabled");
                }

            } else {
                player.sendMessage(ChatColor.DARK_PURPLE + "Disabled movement");
                for (Player st : Bukkit.getOnlinePlayers()) {
                    st.sendTitle(ChatColor.RED + "Do not move or you'll get kicked", ChatColor.GREEN + "A Staff member has disabled the Movement");
                }
                disableMovement = true;
            }
        }

        return false;

    }

    private void sendHelpPageNetlv(Player player) {
        String separator = (ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + ChatColor.STRIKETHROUGH + "--------------------------------");

        player.sendMessage(separator);
        player.sendMessage(ChatColor.DARK_AQUA + "Netlv Plugin Help:");
        player.sendMessage(separator);

        // Add descriptions of different commands and functionalities
        player.sendMessage(ChatColor.YELLOW + "/netlv toggledevmode" + ChatColor.WHITE + " - Toggles development mode on or off.");
        player.sendMessage(ChatColor.YELLOW + "/netlv toggletestmode" + ChatColor.WHITE + " - Toggles test mode on or off.");
        player.sendMessage(ChatColor.YELLOW + "/netlv togglecancelorsetback" + ChatColor.WHITE + " - Toggles cancel/setback mode on or off.");
        player.sendMessage(ChatColor.YELLOW + "/netlv togglehidehealth" + ChatColor.WHITE + " - Toggles hiding player health on or off.");
        player.sendMessage(ChatColor.YELLOW + "/netlv toggleghostblockfix" + ChatColor.WHITE + " - Toggles Ghost Blocks Fix (heavy) on or off.");
        player.sendMessage(ChatColor.YELLOW + "/netlv togglealertautoenable" + ChatColor.WHITE + " - Toggles Alerts Auto Enable on or off.");
        player.sendMessage(ChatColor.YELLOW + "/netlv setcommand <command>" + ChatColor.WHITE + " - Sets the command to be executed.");
        player.sendMessage(ChatColor.YELLOW + "/netlv setbroadcastmessage <message>" + ChatColor.WHITE + " - Sets the broadcast message.");
        player.sendMessage(ChatColor.YELLOW + "/netlv knockbacktest <player>" + ChatColor.WHITE + " - Applies and calculates players knockback.");

        player.sendMessage(separator);
        player.sendMessage(ChatColor.GRAY + "Please note that some features may be unfinished or under development.");
        player.sendMessage(ChatColor.GRAY + "Use the commands carefully and report any issues to the server staff.");
        player.sendMessage(ChatColor.GRAY + "For more information, REDACTED; due to alpha stages.");
        player.sendMessage(separator);
    }

    private void identifieralertplayers(String type, Player cheater) { // TODO: add additional data for the highlight(mouse)
        totalAlertsinlast10seconds.computeIfAbsent(cheater.getName(), k -> (double) 0);
        totalAlertsinlast10seconds.put(cheater.getName(), (totalAlertsinlast10seconds.get(cheater.getName()) + 1));
//        cheater.teleport(previous_location.get(cheater.getName()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean enabled = identifieralerts.computeIfAbsent(player.getName(), k -> false);
            if (enabled) {
                player.sendMessage(ChatColor.YELLOW + "[" + ChatColor.AQUA + "AntiCheat" + ChatColor.YELLOW + "] " + ChatColor.DARK_PURPLE + "- " + ChatColor.LIGHT_PURPLE + cheater.getName() + ChatColor.GRAY + " is identified for " + ChatColor.GREEN + type + ChatColor.GRAY + " modification");
            }
        }
    }

    private void updateChunkForPlayer(Player player, Chunk chunk) {
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(chunk.getX(), chunk.getZ()));
    }

    private void runEvery1Second() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String playerName = player.getName();
                    Double lcps = 0.0;
                    Double rcps = 0.0;

                    if (PlayerClicksLEFT.containsKey(playerName)) {
                        lcps = PlayerClicksLEFT.get(playerName);
                        PlayerClicksLEFT.put(playerName, 0.0); // Resetting the left clicks to zero
                    }

                    if (PlayerClicksRIGHT.containsKey(playerName)) {
                        rcps = PlayerClicksRIGHT.get(playerName);
                        PlayerClicksRIGHT.put(playerName, 0.0); // Resetting the right clicks to zero
                    }

                    if (lcps != 0.0) {
                        if (PlayerCPSLeftList.containsKey(playerName)) {
                            List<Double> leftCpsList = PlayerCPSLeftList.get(playerName);
                            leftCpsList.add(lcps);
                        } else {
                            List<Double> leftCpsList = new ArrayList<>();
                            leftCpsList.add(lcps);
                            PlayerCPSLeftList.put(playerName, leftCpsList);
                        }
                    }
                    if (rcps != 0.0) {
                        if (FastPlace) {
                            Double int_rcps = getLatestValue(playerName, PlayerCPSRightList);
                            if (int_rcps != null) {
//                            player.sendMessage(rcps + "  : " + int_rcps);
                                if (rcps > 14) {
                                    if (rcps.equals(int_rcps)) {
//                                    player.sendMessage(ChatColor.RED +""+ rcps + "  : " + int_rcps);
                                        identifieralertplayers("FastPlace", player);
                                        UpdateAlerts(player, +1.0, "safe");
                                    }
                                }
                            }
                        }
                        if (PlayerCPSRightList.containsKey(playerName)) {
                            List<Double> rightCpsList = PlayerCPSRightList.get(playerName);
                            rightCpsList.add(rcps);
                        } else {
                            List<Double> rightCpsList = new ArrayList<>();
                            rightCpsList.add(rcps);
                            PlayerCPSRightList.put(playerName, rightCpsList);
                        }
                    }
                }
                //Bukkit.broadcastMessage("1 second");
            }
        }.runTaskTimer(this, 0L, 20L); // Save positions every 1 tick (10 milliseconds)
    }


    private void runalltime() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {


                }
            }
        }.runTaskTimer(this, 0L, 1L); // Save positions every 1 tick (10 milliseconds)
    }

    public Double getLatestValue(String playerName, Map<String, List<Double>> list) {
        List<Double> cpsList = list.get(playerName);

        if (cpsList != null && !cpsList.isEmpty()) {
            return cpsList.get(cpsList.size() - 1);
        }

        return null; // Return null if the player's list is empty or not found
    }

    private void HideHPRunalltime() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (HideHealth) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (Player playerTodo : Bukkit.getOnlinePlayers()) {
                            if (playerTodo.getName().equals(player.getName())) {
                                continue;
                            }
                            //PacketPlayOutUpdateHealth packet = new PacketPlayOutUpdateHealth(0, 0, 0);
                            //((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

                            // you can use the below to troll hackers and make it spoof their own health to max always, it'll make them think they're on full hp whilst not actually being full

                            DataWatcher dataWatcher = new DataWatcher(null);
                            dataWatcher.a(6, (float) (20)); // Set the health value
                            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(playerTodo.getEntityId(), dataWatcher, true);
                            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 1L); // Save positions every 1 tick (10 milliseconds)
    }

    private void calculateAim(Player player, Player playerRunCommand) {
        //List<Double> deltaList = PlayerDeltaAimList.get(player.getName());

        String playerName = player.getName();
        if (!(PlayerDeltaAimList.containsKey(playerName))) {
            playerRunCommand.sendMessage(ChatColor.RED + "That player hasn't changed their mouse position since startup. Be patient.");
            return;
        }
        List<Double> deltaList = PlayerDeltaAimList.get(playerName);

        // Calculate the average of the doubles
        double sum_check1 = 0.0;
        for (Double value : deltaList) {
            sum_check1 += value;
        }
        double average_check1 = sum_check1 / deltaList.size();

        // diff check.


        int numFifths = deltaList.size() / 5;
        int fifthSize = deltaList.size() / numFifths;

// Calculate the average for each fifth
        List<Double> fifthAverages = new ArrayList<>();
        for (int i = 0; i < numFifths; i++) {
            int startIndex = i * fifthSize;
            int endIndex = (i + 1) * fifthSize;
            List<Double> fifth = deltaList.subList(startIndex, endIndex);

            double sum = 0.0;
            for (Double value : fifth) {
                sum += value;
            }
            double average = sum / fifth.size();

            fifthAverages.add(average);
        }

// Compare the averages and calculate the similarity percentage
        double similarityPercentage = 0.0;
        for (int i = 0; i < fifthAverages.size() - 1; i++) {
            double average1 = fifthAverages.get(i);
            double average2 = fifthAverages.get(i + 1);

            if (Math.abs(average1 - average2) <= 0.3 * average1) {
                similarityPercentage += 1.0 / numFifths;
            }
        }

        List<Location> hitpointList = PlayerHitpointList.get(playerName);

// Calculate the average of hitpoints
        Location averageHitpoint = new Location(hitpointList.get(0).getWorld(), 0, 0, 0);
        for (Location hitpoint : hitpointList) {
            averageHitpoint.add(hitpoint.getX(), hitpoint.getY(), hitpoint.getZ());
        }
        averageHitpoint.multiply(1.0 / hitpointList.size());

// Calculate the sum of squared differences from the average
        double sumSquaredDifferences = 0.0;
        for (Location hitpoint : hitpointList) {
            double diffX = hitpoint.getX() - averageHitpoint.getX();
            double diffY = hitpoint.getY() - averageHitpoint.getY();
            double diffZ = hitpoint.getZ() - averageHitpoint.getZ();
            sumSquaredDifferences += diffX * diffX + diffY * diffY + diffZ * diffZ;
        }

// Calculate the standard deviation
        double variance = sumSquaredDifferences / hitpointList.size();
        double standardDeviation = Math.sqrt(variance);
        // lower number = less random, higher number = greater random

        // Print or use the average as needed
        playerRunCommand.sendMessage("Player: " + playerName + ", Average: " + average_check1 + " Similar 20%: " + similarityPercentage * 100 + " Deviation check: " + standardDeviation);
    }


    private void calculateCPSLeft(Player player, Player playerRunCommand) {
        //List<Double> deltaList = PlayerDeltaAimList.get(player.getName());

        String playerName = player.getName();
        if (!(PlayerCPSLeftList.containsKey(playerName))) {
            playerRunCommand.sendMessage(ChatColor.RED + "That player hasn't clicked at all since startup. Be patient.");
            return;
        }
        List<Double> CpsLeftList = PlayerCPSLeftList.get(playerName);

        // Calculate the average of the doubles
        double sum_check1 = 0.0;
        for (Double value : CpsLeftList) {
            sum_check1 += value;
        }
        double average_check1 = sum_check1 / CpsLeftList.size();

        // diff check.


        int numFifths = CpsLeftList.size() / 5;
        int fifthSize = CpsLeftList.size() / numFifths;

// Calculate the average for each fifth
        List<Double> fifthAverages = new ArrayList<>();
        for (int i = 0; i < numFifths; i++) {
            int startIndex = i * fifthSize;
            int endIndex = (i + 1) * fifthSize;
            List<Double> fifth = CpsLeftList.subList(startIndex, endIndex);

            double sum = 0.0;
            for (Double value : fifth) {
                sum += value;
            }
            double average = sum / fifth.size();

            fifthAverages.add(average);
        }

// Compare the averages and calculate the similarity percentage
        double similarityPercentage = 0.0;
        for (int i = 0; i < fifthAverages.size() - 1; i++) {
            double average1 = fifthAverages.get(i);
            double average2 = fifthAverages.get(i + 1);

            if (Math.abs(average1 - average2) <= 0.3 * average1) {
                similarityPercentage += 1.0 / numFifths;
            }
        }


        // lower number = less random, higher number = greater random

        // Print or use the average as needed
        playerRunCommand.sendMessage("Player: " + playerName + ", Average: " + average_check1 + " Similar 20%: " + similarityPercentage * 100);
    }

    private void calculateCPSRight(Player player, Player playerRunCommand) {
        //List<Double> deltaList = PlayerDeltaAimList.get(player.getName());

        String playerName = player.getName();
        if (!(PlayerCPSRightList.containsKey(playerName))) {
            playerRunCommand.sendMessage(ChatColor.RED + "That player hasn't clicked at all since startup. Be patient.");
            return;
        }
        List<Double> CpsLeftList = PlayerCPSRightList.get(playerName);

        // Calculate the average of the doubles
        double sum_check1 = 0.0;
        for (Double value : CpsLeftList) {
            sum_check1 += value;
        }
        double average_check1 = sum_check1 / CpsLeftList.size();

        // diff check.


        int numFifths = CpsLeftList.size() / 5;
        int fifthSize = CpsLeftList.size() / numFifths;

// Calculate the average for each fifth
        List<Double> fifthAverages = new ArrayList<>();
        for (int i = 0; i < numFifths; i++) {
            int startIndex = i * fifthSize;
            int endIndex = (i + 1) * fifthSize;
            List<Double> fifth = CpsLeftList.subList(startIndex, endIndex);

            double sum = 0.0;
            for (Double value : fifth) {
                sum += value;
            }
            double average = sum / fifth.size();

            fifthAverages.add(average);
        }

// Compare the averages and calculate the similarity percentage
        double similarityPercentage = 0.0;
        for (int i = 0; i < fifthAverages.size() - 1; i++) {
            double average1 = fifthAverages.get(i);
            double average2 = fifthAverages.get(i + 1);

            if (Math.abs(average1 - average2) <= 0.3 * average1) {
                similarityPercentage += 1.0 / numFifths;
            }
        }


        // lower number = less random, higher number = greater random

        // Print or use the average as needed
        playerRunCommand.sendMessage("Player: " + playerName + ", Average: " + average_check1 + " Similar 20%: " + similarityPercentage * 100);
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event) {
        event.setJoinMessage(""); // removing the join message.
        Player player = event.getPlayer();
        if ((player.hasPermission("netlv.joinalert")) && (AlertAutoEnabled_player)) {
            boolean enabled = identifieralerts.computeIfAbsent(player.getName(), k -> false);
            if (!enabled) {
                identifieralerts.put(player.getName(), true);
                player.sendMessage(ChatColor.WHITE + "[" + ChatColor.YELLOW + "AntiCheat" + ChatColor.WHITE + "] " + ChatColor.AQUA + "- " + ChatColor.WHITE + "Identifier alerts auto enabled.");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        Action action = event.getAction();

        Block clickedBlock = event.getClickedBlock();
        Location PBLoc = previous_location.get(player.getName());
// Check if the action is right click or left click and the clicked block is not air
        if ((action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) && clickedBlock != null) {
            // Get the eye location and the direction of the player
            Location eyeLocation = player.getEyeLocation();
            Vector direction = eyeLocation.getDirection();

            // Create a block iterator from the eye location and the direction
            BlockIterator blockIterator = new BlockIterator(eyeLocation, 0, 5);

            // Loop through the blocks in the iterator
            while (blockIterator.hasNext()) {
                // Get the next block
                Block nextBlock = blockIterator.next();

                // Check if the next block is equal to the clicked block
                if (nextBlock.equals(clickedBlock)) {
                    // Check if the clicked block is a full block or not
                    boolean isFullBlock = isFullBlock(clickedBlock);

                    // Check if the clicked block face is visible or not
                    boolean isFaceVisible = isFaceVisible(clickedBlock, event.getBlockFace());

                    // Check if the block next to the current selected one is visible to the player
                    boolean isVisibleToPlayer = isVisibleToPlayer(clickedBlock, player);

                    // If the clicked block is a full block or its face is visible or it's visible to the player, then the interaction is possible
                    if (isFullBlock || isFaceVisible || isVisibleToPlayer) {
                        // Do something with the interaction, for example, send a message to the player
//                        player.sendMessage("You " + action.name() + "ed " + clickedBlock.getType());
                    } else {
                        if (Interact4) {
                            // Otherwise, cancel the interaction and send a message to the player
                            if (cancelorsetback) {
                                event.setCancelled(true);
                            }
                            identifieralertplayers("Interact 4", player);
                            UpdateAlerts(player, +1.0, "undetermined");
//                        player.sendMessage("You cannot " + action.name() + " " + clickedBlock.getType());
                        }
                    }

                    // Check if the action is right click and the player has a block in hand
                    if (Interact1) {
                        if (action == Action.RIGHT_CLICK_BLOCK && player.getItemInHand() != null && player.getItemInHand().getType().isBlock()) {
                            // Get the block that would be placed by the player
                            Block placedBlock = clickedBlock.getRelative(event.getBlockFace());

                            // Check if it is air or not
                            if (placedBlock.getType() != Material.AIR) {
                                // Get the vector from the eye location to the placed block location
                                Vector eyeToPlaced = placedBlock.getLocation().toVector().subtract(eyeLocation.toVector());

                                // Get the angle between the direction and the eye to placed vector
                                double angle = direction.angle(eyeToPlaced);

                                // Check if the angle is less than 90 degrees or not
                                if (angle < Math.PI / 2) {
                                    // If it is, then the placement is possible and do something with it, for example, send a message to the player
//                                player.sendMessage("You can place " + player.getItemInHand().getType() + " here");
                                } else {
                                    // Otherwise, cancel the placement and send a message to the player
                                    if (cancelorsetback) {
                                        event.setCancelled(true);
                                    }
//                                player.sendMessage("You cannot place " + player.getItemInHand().getType() + " here");
                                    identifieralertplayers("Interact 1", player);
                                    UpdateAlerts(player, +1.0, "undetermined");

                                }
                            }
                        }
                    }

                    // Break out of the loop
                    break;
                } else {
                    if (Interact2) {
                        // If the next block is not equal to the clicked block, check if it is a full block or not
                        boolean isFullBlock = isFullBlock(nextBlock);

                        // If it is a full block, then there is a block in the way of the interaction and cancel it
                        if (isFullBlock) {
//                        event.setCancelled(true);
//                        identifieralertplayers("Interact3(Beta)"+nextBlock, player);
                            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                                double absD1 = Interact6_double.computeIfAbsent(player.getName(), k -> 0.0);
                                Location absD1Loc = Interact6_double_blockSelected.computeIfAbsent(player.getName(), k -> clickedBlock.getLocation());
//                                player.sendMessage(absD1Loc.equals(clickedBlock.getLocation()) + "");
//                                identifieralertplayers("Interact 2th", player);
                                if (absD1Loc.equals(clickedBlock.getLocation())) {
//                                    Random rand = new Random();
//                                    event.getPlayer().getInventory().setHeldItemSlot((new Random().nextInt(9)));
//                                    if (cancelorsetback) {
                                event.setCancelled(true);
//                                    }
                                    Interact6_double.put(player.getName(), 0.0);
                                    identifieralertplayers("Interact 2", player);
                                    UpdateAlerts(player, +1.6, "undetermined");


                                } else {
                                    Interact6_double.put(player.getName(), absD1 + 1);
                                }

                            }
                            break;
                        } else {}
                    }
                }
            }
        }

        if (Interact5) {
            // Check if the action is right click air and the player has a block in hand
            if (action == Action.RIGHT_CLICK_AIR && player.getItemInHand() != null && player.getItemInHand().getType().isBlock() && event.getClickedBlock().isEmpty()) {
                // Get the block below the player
                Block blockBelow = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

                // Check if it is not air or not
                if (blockBelow.getType() != Material.AIR) {
                    Location eyeLocation = player.getEyeLocation();
                    Vector direction = player.getEyeLocation().getDirection();

                    // Get the vector from the eye location to the block below location
                    Vector eyeToBelow = blockBelow.getLocation().toVector().subtract(eyeLocation.toVector());

                    // Get the angle between the direction and the eye to below vector
                    double angle = direction.angle(eyeToBelow);

                    // Check if the angle is greater than 90 degrees or not
                    if (angle > Math.PI / 2) {
                        // If it is, then the placement is not possible and cancel it and send a message to the player
                        if (cancelorsetback) {
                            event.setCancelled(true);
                        }
//                    player.sendMessage("You cannot place " + player.getItemInHand().getType() + " here");
                        UpdateAlerts(player, +1.0, "undetermined");
                        identifieralertplayers("Interact 5", player);
                    }
                }
            }
        }
    }

    // A helper method to check if a block is a full block or not
    public static boolean isFullBlock(Block block) {
        // Get the material of the block
        Material material = block.getType();

        // Check if it is air, water, lava, snow, carpet, slab, stair, fence, gate, wall, sign, banner, button, lever, pressure plate or trapdoor
        return !(material == Material.AIR || material == Material.WATER || material == Material.LAVA || material == Material.SNOW || material == Material.CARPET || material.name().contains("SLAB") || material.name().contains("STAIR") || material.name().contains("FENCE") || material.name().contains("GATE") || material.name().contains("WALL") || material.name().contains("SIGN") || material.name().contains("BANNER") || material.name().contains("BUTTON") || material.name().contains("LEVER") || material.name().contains("PLATE") || material.name().contains("TRAPDOOR") || material == Material.WOODEN_DOOR || material == Material.STAINED_GLASS || material == Material.THIN_GLASS || material == Material.STEP || containsWord(Collections.singletonList(material.name()), "DOOR") || containsWord(Collections.singletonList(material.name()), "STAIR") || containsWord(Collections.singletonList(material.name()), "PANE") || containsWord(Collections.singletonList(material.name()), "THIN") || containsWord(Collections.singletonList(material.name()), "BED_BLOCK") || containsWord(Collections.singletonList(material.name()), "STEP"));
    }

    // A helper method to check if a block face is visible or not
    public boolean isFaceVisible(Block block, BlockFace face) {
        // Get the relative block at the given face
        Block relativeBlock = block.getRelative(face);

        // Check if it is air or not
        return relativeBlock.getType() == Material.AIR;
    }

    public boolean isVisibleToPlayer(Block clickedBlock, Player player) {
        // Get the player's eye location and direction
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        // Create a block iterator from the eye location and direction
        BlockIterator blockIterator = new BlockIterator(eyeLocation, 0, 5);

        // Loop through the blocks in the iterator
        while (blockIterator.hasNext()) {
            // Get the next block
            Block nextBlock = blockIterator.next();

            // Check if the next block is equal to the clicked block
            if (nextBlock.equals(clickedBlock)) {
                // If it is, then the clicked block is visible to the player
                return true;
            } else {
                // If the next block is not equal to the clicked block, check if it is a full block or not
                boolean isFullBlock = isFullBlock(nextBlock);

                // If it is a full block, then there is a block in the way of the interaction and cancel it
                if (isFullBlock) {
                    return false;
                }
            }
        }

        // If we reach here, then the clicked block is not visible to the player
        return false;
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event) {  // do not touch, this is for cps calculations
        Player player = event.getPlayer();
        //event.getClickedBlock().get

        if (containsWord(Collections.singletonList(event.getAction().name()), "RIGHT")) {
            HandleRightClickEvent(event);
        } else if (containsWord(Collections.singletonList(event.getAction().name()), "LEFT")) {
            HandleLeftClickEvent(event);
        } else {
            // unknown event obtained.
        }
    }

    public void HandleRightClickEvent(PlayerInteractEvent event) {
        //event.getPlayer().sendMessage("right");
        String playerName = event.getPlayer().getName();

        if (PlayerClicksRIGHT.containsKey(playerName)) {
            Double clicks = PlayerClicksRIGHT.get(playerName);
            clicks += 1;
            PlayerClicksRIGHT.put(playerName, clicks);
        } else {
            PlayerClicksRIGHT.put(playerName, 1.0);
        }
//        double distance = event.getClickedBlock().getLocation().distance(event.getPlayer().getEyeLocation());
//        if (distance > 6) {
//            // alert, long interaction
//        }
        //player.sendMessage("Distance:" + event.getClickedBlock().getLocation().distance(event.getPlayer().getEyeLocation()));
    }

    public void HandleLeftClickEvent(PlayerInteractEvent event) {
        //event.getPlayer().sendMessage("left");
        String playerName = event.getPlayer().getName();

        if (PlayerClicksLEFT.containsKey(playerName)) {
            Double clicks = PlayerClicksLEFT.get(playerName);
            clicks += 1;
            PlayerClicksLEFT.put(playerName, clicks);
        } else {
            PlayerClicksLEFT.put(playerName, 1.0);
        }
    }

    @EventHandler
    public void PlayerAnimationEvent(PlayerAnimationEvent event) {

        Player player = event.getPlayer();
        //player.sendMessage("  " + player.isBlocking());
        if (player.isBlocking()) {
            //Bukkit.broadcastMessage("Player: " + player.getName() + " Is possibly using a sword block modification");
        }
    }


    public void knockbacktest_player(Player target) {
        Vector velocity = new Vector(0.5, 0.5, 0.5);
        target.setVelocity(velocity);

        new BukkitRunnable() {
            @Override
            public void run() {
                target.sendMessage("Velocity: " + target.getVelocity());
                double deltaZ = previousDeltaZ.get(target.getName());
                double delteX = previousDeltaX.get(target.getName());
                double deltaY = previousDeltaY.get(target.getName());
                target.sendMessage("DeltaZ: " + deltaZ + " DeltaY: " + deltaY + " DeltaX: " + delteX);
            }

        }.runTaskLaterAsynchronously(plugin, 0); // 20 ticks per second, 2 seconds = 40 ticks, use the players ping as math here. IF NEEDED, due to pingspoof can either false or do great.


    }

    public void performCommand(String command) {
        Bukkit.getLogger().info("Performing command: " + command);
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }
        }.runTask(plugin); // 20 ticks per second, 2 seconds = 40 ticks, use the players ping as math here. IF NEEDED, due to pingspoof can either false or do great.
    }

    @EventHandler
    public void onEntityDamageByEntity_(EntityDamageByEntityEvent event) {
        Player damager = (Player) event.getDamager();

        Player player = (Player) event.getEntity();

        AxisAlignedBB victim_boundingbox = ((CraftPlayer) player).getHandle().getBoundingBox();
        // Create a ray from the attacker's eye location


        // Calculate the eye location and direction of the attacker
        Location eyeLocation = damager.getEyeLocation();
        Vector direction = eyeLocation.getDirection();

        AxisAlignedBB hitbox = victim_boundingbox;
        if (Interact6) {
            double hitboxMinX = Math.min(hitbox.a, hitbox.d) - 0.1;
            double hitboxMinY = Math.min(hitbox.b, hitbox.e) - 0.13;
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

            double tMax = Math.max(Math.min(tMaxX, tMinX), Math.min(tMaxY, tMinY));
            tMax = Math.max(tMax, Math.min(tMaxZ, tMinZ));
            double tMin = Math.min(Math.max(tMinX, tMaxX), Math.max(tMinY, tMaxY));
            tMin = Math.min(tMin, Math.max(tMinZ, tMaxZ));


//        if (tMax >= tMin && tMax >= 0) {
            double x = floor(eyeLocation.getX());
            double y = eyeLocation.getY();
            double z = floor(eyeLocation.getZ());

            BlockIterator iterator = new BlockIterator(damager.getWorld(), damager.getEyeLocation().toVector(), damager.getEyeLocation().getDirection(), 0, 10);
            while (iterator.hasNext()) {
                Block block = iterator.next();
                if (block.getLocation().getBlockX() == player.getLocation().getBlockX() && block.getLocation().getBlockZ() == player.getLocation().getBlockZ()) {
                    break;
                }
//                damager.sendMessage(block.getType().name() + " " + block.getLocation() + " a" + block.getLocation().distance(player.getLocation()));
                if (block.getType() != Material.AIR) {// && block.getLocation().distanceSquared(new Location(damager.getWorld(), x, y, z)) < 1) {
//                    event.setCancelled(true); // The hit was through a wall
                    if (isFullBlock(block)) {
                        if (cancelorsetback) {
                            event.setCancelled(true);
                        }
                        identifieralertplayers("Interact 6(" + block.getType().name() + ")", damager);
                        UpdateAlerts(player, +1.0, "unsafe");
//                        damager.sendMessage("You can't hit through walls!");
                        break;
                    }
                }
            }
        }

        if (Reach) {
            numHitbox += 1;
            hitboxSaved_victim.put(numHitbox, victim_boundingbox);
            ct2(damager, (CraftLivingEntity) player, damager.getEyeLocation(), numHitbox);
        }
    }



    private void ct2(final Player damager, final CraftLivingEntity victim,
                     final Location eyeLocation_static, double hitbox_num_static_victim) {
        // yes not victim and player have been swapped again!

        int ping_dmg = ((CraftPlayer) damager).getHandle().playerConnection.getPlayer().getHandle().ping;
        AxisAlignedBB hitbox = hitboxSaved_victim.get(hitbox_num_static_victim);
//        damager.sendMessage("tsad: " + ((Math.min(hitbox.b, hitbox.e) - getBlockYUnderneathPlayer((Player) victim)) - 1));
        if (((Math.min(hitbox.b, hitbox.e) - getBlockYUnderneathPlayer((Player) victim)) - 1 <= 0.0)) {
            // alright, on the ground.
            // note that blocks that are not full such as fences, pressure plates and all of those count as blocks! so this number can be wrong - fixed? i think? putting a less than in the whether or not calculated to 0
        } else {

            return;
        }


        new BukkitRunnable() {
            @Override
            public void run() {
                double vm = Double.MAX_VALUE;

                //        String playerName = player.getName();
                double lowestValue = Double.MAX_VALUE;
                List<PlayerSavedLocDetails> playerDetailsList = playerPositionDetailsDelta.get(damager.getName());
                List<PlayerSavedLocDetails> DeltaLocList;


                if (playerDetailsList != null) {
                    DeltaLocList = getDeltaGroup(damager, Bukkit.getServer().getWorld("world").getFullTime(), 4);
                    for (PlayerSavedLocDetails details : DeltaLocList) {
                        List<Location> locations1 = details.getLocations1(); // loc of player
                        List<Location> locations2 = details.getLocations2(); // eyeloc
                        List<AxisAlignedBB> boundingBoxes = details.getBoundingBoxes(); // their bounding box


                        for (int i = 0; i < locations1.size(); i++) {
                            Location playerLocation = locations1.get(i);
                            Location eyeLocation = locations2.get(i);
                            //AxisAlignedBB hitbox = boundingBoxes.get(i);


                            Vector direction = eyeLocation.getDirection();

//                    AxisAlignedBB hitbox = ((CraftPlayer) victim).getHandle().getBoundingBox();//victimInfo.get(i).getLeft();


                            double hitboxMinX = Math.min(hitbox.a, hitbox.d) - 0.1;
                            double hitboxMinY = Math.min(hitbox.b, hitbox.e) - 0.13;
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

                            double tMax = max(max(min(tMaxX, tMinX), min(tMaxY, tMinY)), min(tMaxZ, tMinZ));
                            double tMin = min(max(min(tMinX, tMaxX), min(tMinY, tMaxY)), min(tMinZ, tMaxZ));

                            double shortestDistance = Double.MAX_VALUE;
                            double shortestX = 0.0;
                            double shortestY = 0.0;
                            double shortestZ = 0.0;

                            if (tMax >= tMin && tMax >= 0) {
                                double x = eyeLocation.getX() + direction.getX() * tMax;
                                double y = eyeLocation.getY() + direction.getY() * tMax;
                                double z = eyeLocation.getZ() + direction.getZ() * tMax;

                                double distance = sqrt((x - eyeLocation.getX()) * (x - eyeLocation.getX()) +
                                        (y - eyeLocation.getY()) * (y - eyeLocation.getY()) +
                                        (z - eyeLocation.getZ()) * (z - eyeLocation.getZ()));

                                if (distance < shortestDistance) {
                                    shortestDistance = distance;

                                    shortestX = x;
                                    shortestY = y;
                                    shortestZ = z;
                                }
                            }

//                    double dx = Math.max(0, Math.max(hitboxMinX - hitboxMaxX_victim, hitboxMinX_victim - hitboxMaxX));
//                    double dy = Math.max(0, Math.max(hitboxMinY - hitboxMaxY_victim, hitboxMinY_victim - hitboxMaxY));
//                    double dz = Math.max(0, Math.max(hitboxMinZ - hitboxMaxZ_victim, hitboxMinZ_victim - hitboxMaxZ));
//                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
//                    //player.sendMessage("Reach distance check: " + distance);

                            if (vm > shortestDistance) {
                                vm = shortestDistance;
                            }
                        }
                    }


                } // else is null and can not work with it.
//                damager.sendMessage("vm: " + vm);
                if (vm > 1000000) {

                } else {
                    if (vm >= 3.028) {
                        if (vm >= 50) {
//                            damager.sendMessage(ChatColor.DARK_BLUE + "Reach math broke: " + vm);

                        } else {
                            identifieralertplayers("Reach(" + vm + ")", damager);
                            UpdateAlerts(damager, +2.0, "combat");
//                            damager.sendMessage(ChatColor.DARK_AQUA + "Reaching! using either or more than: " + vm);
                        }
                    } else {
//                        damager.sendMessage(ChatColor.DARK_RED + "reach, " + vm);
                    }
                    printReach(damager, victim, vm, damager.getLocation(), victim.getLocation());

                }
            }
        }.runTaskLaterAsynchronously(plugin, (long) (((ping_dmg / 20) * 0.6) + 0.1));// this 0.1 used to be 3 // 20 ticks per second, 2 seconds = 40 ticks, use the players ping as math here. IF NEEDED, due to pingspoof can either false or do great.;

    }
    //damager.sendMessage("DamagerSprint: " + damager.isSprinting());
//        int ping = ((CraftPlayer) player).getHandle().playerConnection.getPlayer().getHandle().ping;

//        if (vm < 10000) {
//            Location eyeLocation = damager.getEyeLocation();
//            Vector direction = eyeLocation.getDirection();
//
//            Double vcLoctmpx = (player.getLocation().getX());
//            Double vcLoctmpz = (player.getLocation().getZ());
//
//
//            // Create a block iterator from the eye location and the direction
//            BlockIterator blockIterator = new BlockIterator(eyeLocation, 0, (int) (vm + 0.4));
//            // Loop through the blocks in the iterator
//            while (blockIterator.hasNext()) {
//                // Get the next block
//                Block nextBlock = blockIterator.next();
//
//                // Check if the next block is not air or not
//                if (nextBlock.getType() != Material.AIR) {
//                    // Check if the next block is a full block or not
//                    boolean isFullBlock = isFullBlock(nextBlock);
////                    damager.sendMessage((nextBlock.getZ()) + " " + (vcLoctmpz) + "  " + nextBlock.getX() + " " + vcLoctmpx);
//                    if (((abs(nextBlock.getZ() - vcLoctmpz) < 1)) && ((abs(nextBlock.getX() - vcLoctmpx) < 1))) {
//                        break;
//                    } else if (isFullBlock) {
//                        // If it is a full block, then there is a block in the way of the attack and cancel it and send a message to the player
//                        //                    event.setCancelled(true);
//
//                        damager.sendMessage("There is a block in the way of your attack: " + nextBlock.getX() + " " + nextBlock.getZ());
//                        break;
//                    }
//                }
//            }
//        }
//    }
//
//        damager.sendMessage("player.getVelocity(): " + player.getVelocity());
//        damager.sendMessage("player.getLocation(): " + player.getLocation());
//        damager.sendMessage("player.getActivePotionEffects(): " + player.getActivePotionEffects());
//        damager.sendMessage("player.getItemInHand(): " + player.getItemInHand());
//        damager.sendMessage("player.getEyeLocation(): " + player.getEyeLocation());
//        damager.sendMessage("player.getEyeHeight(): " + player.getEyeHeight());
//        damager.sendMessage("((CraftPlayer) player).getHandle().getHeadRotation(): " + ((CraftPlayer) player).getHandle().getHeadRotation());
//        damager.sendMessage("((CraftPlayer) player).getHandle().getBoundingBox(): " + ((CraftPlayer) player).getHandle().getBoundingBox());
//        damager.sendMessage("((CraftPlayer) player).getHandle().getDirection(): " + ((CraftPlayer) player).getHandle().getDirection());
//        damager.sendMessage("((CraftPlayer) player).getHandle().playerConnection.getPlayer().getHandle().ping: " + ((CraftPlayer) player).getHandle().playerConnection.getPlayer().getHandle().ping);
//
//


//
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                AxisAlignedBB victimhitbox = hitboxSaved_victim.get(hitbox_num_static_victim); // still not good, getting delayed hits.
//                hitboxSaved_victim.remove(hitbox_num_static_victim);
//                List<Pair<AxisAlignedBB, Location>> damagerInfo = getPlayerPositions((CraftLivingEntity) player);
//                List<Pair<AxisAlignedBB, Location>> victimInfo = getPlayerPositions((CraftLivingEntity) victim);
//                double lowestReach = Double.MAX_VALUE;
//                Location hitPoint = null;
//                Double hitboxMinX = null;
//                Double hitboxMinY = null;
//                Double hitboxMinZ = null;
//
//                // account for all the deltas, including the pitch and yaw ones.
//                double totalRotationChange = (abs(recentDeltaPitch.get(player.getName())) + abs(recentDeltaYaw.get(player.getName())));
//                int size = min(victimInfo.size(), damagerInfo.size());
//                //player.sendMessage("Your ping is " + ping);
//                Location lowestDistanceLoc_vic = victim.getLocation();
//                Location lowestDistanceLoc_dgr = player.getLocation();
//
//                Location lowestReachLoc_vic = victim.getLocation();
//                Location lowestReachLoc_dgr = player.getLocation();
//
//                AxisAlignedBB victim_hitbox = victimInfo.get(99).getLeft();//victimhitbox;
//                //player.sendMessage("Your hitbox is LOC: " + victimInfo.get(99).getRight());
//
//                boolean hitPossible = false;
//                boolean hitbox_flag = true; // making this less lenient
//                double hitRange = 0;
//
//
//
//
//                for (int i = 0; i < size; i++) {
//                    Location eyeLocation = damagerInfo.get(i).getRight();
//                    //Vector direction = eyeLocation.getDirection();
//
//                    AxisAlignedBB hitbox = victim_hitbox; //victimInfo.get(i).getLeft();
//
//
//// Calculate the direction vector from the player's eye location
//                    Vector direction = eyeLocation.getDirection().multiply(3); // Extend the ray 3 blocks ahead
//
//// Calculate the end point of the ray
//                    Location rayEnd = eyeLocation.clone().add(direction);
//
//// Calculate the minimum and maximum coordinates of the hitbox
//                    hitboxMinX = Math.min(hitbox.a, hitbox.d) - 0.1;
//                    hitboxMinY = Math.min(hitbox.b, hitbox.e) - 0.13;
//                    hitboxMinZ = Math.min(hitbox.c, hitbox.f) - 0.1;
//                    double hitboxMaxX = Math.max(hitbox.a, hitbox.d) + 0.1;
//                    double hitboxMaxY = Math.max(hitbox.b, hitbox.e) + 0.1;
//                    double hitboxMaxZ = Math.max(hitbox.c, hitbox.f) + 0.1;
//
//// Check for intersection between the ray and the hitbox
//                    hitPossible = rayIntersectsBoundingBox(eyeLocation, rayEnd, hitboxMinX, hitboxMinY, hitboxMinZ, hitboxMaxX, hitboxMaxY, hitboxMaxZ);
//                    //hitRange = eyeLocation.distance(rayEnd); // Calculate the range of the hit
//
//                    if (hitPossible == true) {
//                        break;
//                    }
//                }
//
//
//                for (int i = 0; i < size; i++) {
//                    Location eyeLocation = damagerInfo.get(i).getRight();
//
//                    Vector direction = eyeLocation.getDirection();
//
//                    AxisAlignedBB hitbox = victim_hitbox;//victimInfo.get(i).getLeft();
//
//
//                    hitboxMinX = (min(hitbox.a, hitbox.d) - 0.13);
//                    hitboxMinY = (min(hitbox.b, hitbox.e) - 0.13);
//                    hitboxMinZ = (min(hitbox.c, hitbox.f) - 0.11);
//                    double hitboxMaxX = (max(hitbox.a, hitbox.d) + 0.07);
//                    double hitboxMaxY = (max(hitbox.b, hitbox.e) + 0.1);
//                    double hitboxMaxZ = (max(hitbox.c, hitbox.f) + 0.08);
//
//                    double tMaxX = (hitboxMaxX - eyeLocation.getX()) / direction.getX();
//                    double tMinX = (hitboxMinX - eyeLocation.getX()) / direction.getX();
//                    double tMaxY = (hitboxMaxY - eyeLocation.getY()) / direction.getY();
//                    double tMinY = (hitboxMinY - eyeLocation.getY()) / direction.getY();
//                    double tMaxZ = (hitboxMaxZ - eyeLocation.getZ()) / direction.getZ();
//                    double tMinZ = (hitboxMinZ - eyeLocation.getZ()) / direction.getZ();
//
//                    double tMax = max(max(min(tMaxX, tMinX), min(tMaxY, tMinY)), min(tMaxZ, tMinZ));
//                    double tMin = min(max(min(tMinX, tMaxX), min(tMinY, tMaxY)), min(tMinZ, tMaxZ));
//
//                    Location hitboxOutermost = eyeLocation.clone().add(direction.clone().multiply(tMax));
//                    double difx = hitboxOutermost.getX() - eyeLocation.getX();
//                    double dify = hitboxOutermost.getY() - eyeLocation.getY();
//                    double difz = hitboxOutermost.getZ() - eyeLocation.getZ();
//
//                    double eyeX = eyeLocation.getX();
//                    double eyeY = eyeLocation.getY();
//                    double eyeZ = eyeLocation.getZ();
//                    double hX = hitboxOutermost.getX();
//                    double hY = hitboxOutermost.getY();
//                    double hZ = hitboxOutermost.getZ();
//
//                    double vm = Math.sqrt((Math.pow(eyeX - hX, 2)) + (Math.pow(eyeY - hY, 2)) + (Math.pow(eyeZ - hZ, 2)));
//
//                    //double vm = sqrt((pow(eyeX - hX, 2)) + (pow(eyeY - hY, 2)) + (pow(eyeZ - hZ, 2)));
//                    //player.sendMessage("VM: " + vm);
//// Define the number of points to sample on the hitbox
//                    int numPoints = 100;
//                    double closestRange = Double.MAX_VALUE;
//                    Location closestPoint = null;
//
//// Iterate over the points on the hitbox and find the closest range to the player's eye location
//                    for (int j = 0; j < numPoints; j++) {
//                        double t = (double) j / (numPoints - 1);
//                        double hitboxX = lerp(hitbox.a, hitbox.d, t);
//                        double hitboxY = lerp(hitbox.b, hitbox.e, t);
//                        double hitboxZ = lerp(hitbox.c, hitbox.f, t);
//                        Location point = new Location(eyeLocation.getWorld(), hitboxX, hitboxY, hitboxZ);
//                        double range = eyeLocation.distance(point);
//                        if (range < closestRange) {
//                            closestRange = range;
//                            closestPoint = point;
//                        }
//                    }
//
//                    if (vm > closestRange) {
//                        //vm = closestRange;
//                    }
//
//                    hitPoint = eyeLocation.clone().add(direction.clone().multiply(tMax));
//
//
//
//                    if (!(hitPoint.getX() < hitboxMinX || hitPoint.getX() > hitboxMaxX || hitPoint.getY() < hitboxMinY || hitPoint.getY() > hitboxMaxY || hitPoint.getZ() < hitboxMinZ || hitPoint.getZ() > hitboxMaxZ)) {
//                        hitbox_flag = false;
//                    }
//
//
//                    if (lowestReach > vm && (vm < 10E5)) {
//                        lowestReachLoc_vic = victim.getLocation();
//                        lowestReachLoc_dgr = player.getLocation();
//                        lowestReach = vm;
//                    }
//                }
//                if (hitbox_flag == true) {
//                    //player.sendMessage("hitbox, rotation: " + totalRotationChange); // TODO: for some reason the rotation mostly when actually alerting is equal or above 360. account for these.
//                }
//
//                String playerName = player.getName();
//
//                List<Location> hitpointList = PlayerHitpointList.getOrDefault(playerName, new ArrayList<>());
//
//                // Create a new Location object with relative coordinates
//                Location relativeHitPoint = new Location(
//                        hitPoint.getWorld(),
//                        hitPoint.getX() - hitboxMinX,
//                        hitPoint.getY() - hitboxMinY,
//                        hitPoint.getZ() - hitboxMinZ
//                );
//
//                hitpointList.add(relativeHitPoint);
//                PlayerHitpointList.put(playerName, hitpointList);
//                // distance check below, above reach.
//
//                double lowestDistance = Double.MAX_VALUE;
//
//                //int size = Math.min(victimInfo.size(), damagerInfo.size());
////                for (int i = 0; i < size; i++) {
////                    Pair<AxisAlignedBB, Location> position_victim = victimInfo.get(i);
////                    Pair<AxisAlignedBB, Location> position_attacker = damagerInfo.get(i);
////                    Location eyeLocation = position_attacker.getRight();
//
//                for (int i = 0; i < size; i++) {
//                    //Pair<AxisAlignedBB, Location> position_victim = victimInfo.get(i);
//                    //victim.
//
//                    //Vector direction = eyeLocation.getDirection();
//
//                    //AxisAlignedBB hitbox = victimInfo.get(i).getLeft();
//
//                    Location eyeLocation = damagerInfo.get(i).getRight();
//
//                    Vector direction = eyeLocation.getDirection();
//
//                    AxisAlignedBB hitbox = victimInfo.get(i).getLeft();
//                    hitboxMinX = min(hitbox.a, hitbox.d) - 0.1;
//                    hitboxMinY = min(hitbox.b, hitbox.e) - 0.13;
//                    hitboxMinZ = min(hitbox.c, hitbox.f) - 0.1;
//                    double hitboxMaxX = max(hitbox.a, hitbox.d) + 0.1;
//                    double hitboxMaxY = max(hitbox.b, hitbox.e) + 0.1;
//                    double hitboxMaxZ = max(hitbox.c, hitbox.f) + 0.1;
//
//
//                    AxisAlignedBB hitbox_victim = ((CraftLivingEntity) player).getHandle().getBoundingBox();
//                    double hitboxMinX_victim = min(hitbox_victim.a, hitbox_victim.d) - 0.1;
//                    double hitboxMinY_victim = (min(hitbox_victim.b, hitbox_victim.e) - 0.1);
//                    double hitboxMinZ_victim = min(hitbox_victim.c, hitbox_victim.f) - 0.1;
//                    double hitboxMaxX_victim = max(hitbox_victim.a, hitbox_victim.d) - 0.1;
//                    double hitboxMaxY_victim = max(hitbox_victim.b, hitbox_victim.e) + 0.1;
//                    double hitboxMaxZ_victim = max(hitbox_victim.c, hitbox_victim.f) + 0.1;
//
//
//                    double tMaxX = (hitboxMaxX - eyeLocation.getX()) / direction.getX();
//                    double tMinX = (hitboxMinX - eyeLocation.getX()) / direction.getX();
//                    double tMaxY = (hitboxMaxY - eyeLocation.getY()) / direction.getY();
//                    double tMinY = (hitboxMinY - eyeLocation.getY()) / direction.getY();
//                    double tMaxZ = (hitboxMaxZ - eyeLocation.getZ()) / direction.getZ();
//                    double tMinZ = (hitboxMinZ - eyeLocation.getZ()) / direction.getZ();
//
//                    double tMax = max(max(min(tMaxX, tMinX), min(tMaxY, tMinY)), min(tMaxZ, tMinZ));
//                    double tMin = min(max(min(tMinX, tMaxX), min(tMinY, tMaxY)), min(tMinZ, tMaxZ));
//
//                    double shortestDistance = Double.MAX_VALUE;
//                    double shortestX = 0.0;
//                    double shortestY = 0.0;
//                    double shortestZ = 0.0;
//
//                    if (tMax >= tMin && tMax >= 0) {
//                        double x = eyeLocation.getX() + direction.getX() * tMax;
//                        double y = eyeLocation.getY() + direction.getY() * tMax;
//                        double z = eyeLocation.getZ() + direction.getZ() * tMax;
//
//                        double distance = sqrt((x - eyeLocation.getX()) * (x - eyeLocation.getX()) +
//                                (y - eyeLocation.getY()) * (y - eyeLocation.getY()) +
//                                (z - eyeLocation.getZ()) * (z - eyeLocation.getZ()));
//
//                        if (distance < shortestDistance) {
//                            shortestDistance = distance;
//                            lowestDistanceLoc_vic = victim.getLocation();
//                            lowestDistanceLoc_dgr = player.getLocation();
//                            shortestX = x;
//                            shortestY = y;
//                            shortestZ = z;
//                        }
//                    }


    // also these are flipped than the other OnEntityDamageByEntity.

    //Bukkit.broadcastMessage(" " +damager.getItemInHand().containsEnchantment(Enchantment.KNOCKBACK));
//        double deltex = previousDeltaX.get(player.getName());
//        double deltaz = previousDeltaZ.get(player.getName());
//        double deltaXZ = Math.hypot(deltex, deltaz);
    //double velocityXZ = Math.hypot(player.getVelocity().getX(), player.getVelocity().getZ());

    //Bukkit.broadcastMessage("deltexz: "+ deltaXZ);

//        event.getEntity().getVelocity().get


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
//        AxisAlignedBB hitbox_victim = ((CraftLivingEntity) damager).getHandle().getBoundingBox();
//        double hitboxMinX_victim = Math.min(hitbox_victim.a, hitbox_victim.d) - 0.1;
//        double hitboxMinY_victim = (Math.min(hitbox_victim.b, hitbox_victim.e) - 0.1);
//        double hitboxMinZ_victim = Math.min(hitbox_victim.c, hitbox_victim.f) - 0.1;
//        double hitboxMaxX_victim = Math.max(hitbox_victim.a, hitbox_victim.d) - 0.1;
//        double hitboxMaxY_victim = Math.max(hitbox_victim.b, hitbox_victim.e) + 0.1;
//        double hitboxMaxZ_victim = Math.max(hitbox_victim.c, hitbox_victim.f) + 0.1;
//
//        double tMaxX_victim = (hitboxMaxX_victim - eyeLocation_victim.getX()) / direction_victim.getX();
//        double tMinX_victim = (hitboxMinX_victim - eyeLocation_victim.getX()) / direction_victim.getX();
//        double tMaxY_victim = (hitboxMaxY_victim - eyeLocation_victim.getY()) / direction_victim.getY();
//        double tMinY_victim = (hitboxMinY_victim - eyeLocation_victim.getY()) / direction_victim.getY();
//        double tMaxZ_victim = (hitboxMaxZ_victim - eyeLocation_victim.getZ()) / direction_victim.getZ();
//        double tMinZ_victim = (hitboxMinZ_victim - eyeLocation_victim.getZ()) / direction_victim.getZ();
//
//        double tMax_victim = Math.max(Math.max(Math.min(tMaxX_victim, tMinX_victim), Math.min(tMaxY_victim, tMinY_victim)), Math.min(tMaxZ_victim, tMinZ_victim));
//        double tMin_victim = Math.min(Math.max(Math.min(tMinX_victim, tMaxX_victim), Math.min(tMinY_victim, tMaxY_victim)), Math.min(tMinZ_victim, tMaxZ_victim));
//
//        Location hitboxOutermost_victim = eyeLocation_victim.clone().add(direction_victim.clone().multiply(tMax_victim));
//
//        double eyeX_victim = eyeLocation_victim.getX();
//        double eyeY_victim = eyeLocation_victim.getY();
//        double eyeZ_victim = eyeLocation_victim.getZ();
//        double hX_victim = hitboxOutermost_victim.getX();
//        double hY_victim = hitboxOutermost_victim.getY();
//        double hZ_victim = hitboxOutermost_victim.getZ();


//        double shortestDistance_victim = Double.MAX_VALUE;
//        double shortestX_victim = 0.0;
//        double shortestY_victim = 0.0;
//        double shortestZ_victim = 0.0;
//
//        if (tMax_victim >= tMin_victim && tMax_victim >= 0) {
//            double x = eyeLocation_victim.getX() + direction_victim.getX() * tMax_victim;
//            double y = eyeLocation_victim.getY() + direction_victim.getY() * tMax_victim;
//            double z = eyeLocation_victim.getZ() + direction_victim.getZ() * tMax_victim;
//
//            double distance_victim = Math.sqrt((x - eyeLocation_victim.getX()) * (x - eyeLocation_victim.getX()) +
//                    (y - eyeLocation_victim.getY()) * (y - eyeLocation_victim.getY()) +
//                    (z - eyeLocation_victim.getZ()) * (z - eyeLocation_victim.getZ()));
//
//            if (distance_victim < shortestDistance_victim) {
//                shortestDistance_victim = distance_victim;
//                shortestX_victim = x;
//                shortestY_victim = y;
//                shortestZ_victim = z;
//            }
//        }

//        double vm_victim = shortestDistance_victim;

        //double vm_victim = Math.sqrt((pow(eyeX_victim - hX_victim, 2)) + (pow(eyeY_victim - hY_victim, 2)) + (pow(eyeZ_victim - hZ_victim, 2)));


        AxisAlignedBB hitbox = ((CraftLivingEntity) victim).getHandle().getBoundingBox();
        double hitboxMinX = Math.min(hitbox.a, hitbox.d) - 0.1;
        double hitboxMinY = Math.min(hitbox.b, hitbox.e) - 0.13;
        double hitboxMinZ = Math.min(hitbox.c, hitbox.f) - 0.1;
        double hitboxMaxX = Math.max(hitbox.a, hitbox.d) + 0.1;
        double hitboxMaxY = Math.max(hitbox.b, hitbox.e) + 0.1;
        double hitboxMaxZ = Math.max(hitbox.c, hitbox.f) + 0.1;

        //player.sendMessage("hitboxMaxY: " + hitboxMaxY + "hitboxMinY: " +hitboxMinY);

        double tMaxX = (hitboxMaxX - eyeLocation.getX()) / direction.getX();
        double tMinX = (hitboxMinX - eyeLocation.getX()) / direction.getX();
        double tMaxY = (hitboxMaxY - eyeLocation.getY()) / direction.getY();
        double tMinY = (hitboxMinY - eyeLocation.getY()) / direction.getY();
        double tMaxZ = (hitboxMaxZ - eyeLocation.getZ()) / direction.getZ();
        double tMinZ = (hitboxMinZ - eyeLocation.getZ()) / direction.getZ();

        double tMax = Math.max(Math.max(Math.min(tMaxX, tMinX), Math.min(tMaxY, tMinY)), Math.min(tMaxZ, tMinZ));
        double tMin = Math.min(Math.max(Math.min(tMinX, tMaxX), Math.min(tMinY, tMaxY)), Math.min(tMinZ, tMaxZ));
//
//        double shortestDistance = Double.MAX_VALUE;
//        double shortestX = 0.0;
//        double shortestY = 0.0;
//        double shortestZ = 0.0;
//
//        if (tMax >= tMin && tMax >= 0) {
//            double x = eyeLocation.getX() + direction.getX() * tMax;
//            double y = eyeLocation.getY() + direction.getY() * tMax;
//            double z = eyeLocation.getZ() + direction.getZ() * tMax;
//
//            double distance = Math.sqrt((x - eyeLocation.getX()) * (x - eyeLocation.getX()) +
//                    (y - eyeLocation.getY()) * (y - eyeLocation.getY()) +
//                    (z - eyeLocation.getZ()) * (z - eyeLocation.getZ()));
//
//            if (distance < shortestDistance) {
//                shortestDistance = distance;
//                shortestX = x;
//                shortestY = y;
//                shortestZ = z;
//            }
//        }
//
//        double vm = shortestDistance;

//        // Damager POV
//        AxisAlignedBB hitbox = ((CraftLivingEntity) victim).getHandle().getBoundingBox();
//        double hitboxMinX = Math.min(hitbox.a, hitbox.d) - 0.1;
//        double hitboxMinY = (Math.min(hitbox.b, hitbox.e) - 0.1) ;
//        double hitboxMinZ = Math.min(hitbox.c, hitbox.f) - 0.1;
//        double hitboxMaxX = Math.max(hitbox.a, hitbox.d) + 0.1;
//        double hitboxMaxY = Math.max(hitbox.b, hitbox.e) + 0.1;
//        double hitboxMaxZ = Math.max(hitbox.c, hitbox.f) + 0.1;
//
//        double tMaxX = (hitboxMaxX - eyeLocation.getX()) / direction.getX();
//        double tMinX = (hitboxMinX - eyeLocation.getX()) / direction.getX();
//        double tMaxY = (hitboxMaxY - eyeLocation.getY()) / direction.getY();
//        double tMinY = (hitboxMinY - eyeLocation.getY()) / direction.getY();
//        double tMaxZ = (hitboxMaxZ - eyeLocation.getZ()) / direction.getZ();
//        double tMinZ = (hitboxMinZ - eyeLocation.getZ()) / direction.getZ();
//
//        double tMax = Math.max(Math.max(Math.min(tMaxX, tMinX), Math.min(tMaxY, tMinY)), Math.min(tMaxZ, tMinZ));
//        double tMin = Math.min(Math.max(Math.min(tMinX, tMaxX), Math.min(tMinY, tMaxY)), Math.min(tMinZ, tMaxZ));
//
        Location hitboxOutermost = eyeLocation.clone().add(direction.clone().multiply(tMax));
        double difx = hitboxOutermost.getX() - eyeLocation.getX();
        double dify = hitboxOutermost.getY() - eyeLocation.getY();
        double difz = hitboxOutermost.getZ() - eyeLocation.getZ();
//
//        double distanceMathS = Math.sqrt(difx * difx + dify * dify + difz * difz);
//
        double eyeX = eyeLocation.getX();
        double eyeY = eyeLocation.getY();
        double eyeZ = eyeLocation.getZ();
        double hX = hitboxOutermost.getX();
        double hY = hitboxOutermost.getY();
        double hZ = hitboxOutermost.getZ();
//
        double vm = Math.sqrt((Math.pow(eyeX - hX, 2)) + (Math.pow(eyeY - hY, 2)) + (Math.pow(eyeZ - hZ, 2)));
        //player.sendMessage("VM: " + vm);

        //float vm_vic_int = (float) vm_victim;
//        float vm_int = (float) vm;
//
//        if (vm_int == vm_vic_int) { // skip, as it's practically impossible unless bot both have hit each at the exact same tick and at the exact same head location position
//            // do nothing
//        } else if (!(((Player) damager).isSprinting()) && (victim.getVelocity().length() == 0.0784000015258789) && (vm_int > 3.009)) { // this is the best case, and this means they 100% have reach.
//           // damager.sendMessage("100% using reach modification");
//        } else if (vm_vic_int > vm_int) {
//            // interesting stuff
//            int subA = (int) (vm_vic_int - vm_int);
//            if (subA > 4) {
//              //  damager.sendMessage("Kicked for high range, Either lag/delay-in-netty or reach modification.");
//            }
//        } else if (((vm_int - vm_vic_int) > 3) && (!((Player) damager).isSprinting()) && (!((Player) victim).isSprinting())) {
//            //damager.sendMessage("Reach, user reach larger than victim 'reach', and is more than 3 - less lienent");
//            // should be normal, or detection, that's saying if best scenario. cause currently movement isn't being handled
//        } else if ((((vm_int - vm_vic_int) > 5) && ((Player) damager).isSprinting()) && ((Player) victim).isSprinting()) {
//            //damager.sendMessage("Reach, user reach larger than victim 'reach', and is more than 5 - lienent ish, sprint dependent");
//        } else if (((abs(vm_int - vm_vic_int)) < 1) && (vm_int > 3.09)) {
//          //  damager.sendMessage("There is a a chance of reach usage/modification");
//            //    } else if ((vm_int > 5) && (vm_victim < 3)) { // they probably didn't cheat and just lagged.
//            //      }
//        } else if (vm_int > 5) {
//        //    damager.sendMessage("Reach too far - 5");
//        }

        // Retrieve the player's saved positions

        // player.sendMessage(player.getEyeLocation().getYaw() + "");

        AxisAlignedBB victim_boundingbox = ((CraftLivingEntity) victim).getHandle().getBoundingBox();
        long currentTime = System.currentTimeMillis();
        String victimName = victim.getName();
        //player.sendMessage(victim_boundingbox + "");
        //player.sendMessage(currentTime + "");
        //player.sendMessage((Player) victim + "");
        //saveHitbox_timedlist(victim_boundingbox, currentTime, ((Player) victim)); // this is the victim frozen hitbox as hit.


        //Vector direction = eyeLocation.getDirection();


// Calculate the direction vector from the player's eye location
        Vector direction_ray3blcks = eyeLocation.getDirection().multiply(3); // Extend the ray 3 blocks ahead

// Calculate the end point of the ray
        Location rayEnd = eyeLocation.clone().add(direction_ray3blcks);


// Check for intersection between the ray and the hitbox
        if (Hitbox) {
            boolean hitPossible_nodelay = rayIntersectsBoundingBox(eyeLocation, rayEnd, hitboxMinX, hitboxMinY, hitboxMinZ, hitboxMaxX, hitboxMaxY, hitboxMaxZ);
            //hitRange = eyeLocation.distance(rayEnd); // Calculate the range of the hit
            if (victim.isOnGround() || (victim.getLocation().getY() - getBlockYUnderneathPlayer((Player) victim)) - 1 < 0.0) {
                numHitbox += 1;
                hitboxSaved_victim.put(numHitbox, victim_boundingbox);
                if (Bukkit.getOnlinePlayers().contains(victim)) {
                    calculateLowestPossibility(player, (CraftLivingEntity) victim, player.getEyeLocation(), numHitbox, hitPossible_nodelay);
                }
            }
        }
//        double lowestDistance = calculateLowestDistance(victimInfo, (CraftLivingEntity) player);
//        if (lowestDistance > 3) {
//            player.sendMessage(ChatColor.RED + "Lowest Distance obtained: " + lowestDistance);
//        } else {
//           // player.sendMessage("Lowest Distance obtained: " + lowestDistance);
//        }


        Location hitPoint = eyeLocation.clone().add(direction.clone().multiply(tMax));

        //PlayerHitpointList

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


//         Calculate the distance between the entities, actual distance and not reach. distance between 2 hitboxes
//        double dx = Math.max(0, Math.max(hitboxMinX - hitboxMaxX_victim, hitboxMinX_victim - hitboxMaxX));
//        double dy = Math.max(0, Math.max(hitboxMinY - hitboxMaxY_victim, hitboxMinY_victim - hitboxMaxY));
//        double dz = Math.max(0, Math.max(hitboxMinZ - hitboxMaxZ_victim, hitboxMinZ_victim - hitboxMaxZ));
//        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
//        player.sendMessage("Reach distance check: " + distance); // this is very accurate and stable. but on higher pings.. it falls apart.
//
//        if (distance > 3.6) {
//            player.sendMessage(ChatColor.DARK_AQUA+"Reach distance check");
//        }
//
//         player.sendMessage(ChatColor.RED + "Range: " + distance);
//
//

// Calculate the distance between the interpolated positions of the hitboxes


        //double line = getClosestLineDistance(hitboxOutermost, hitboxOutermost_victim);

        // previous_location - where the previous location, if the victim has no previous location, assume as they hadn't moved. or an npc

        //player.sendMessage(ChatColor.RED + "Range: " + (range_x + range_y + range_z));
        //player.sendMessage(ChatColor.RED + "RangeX: " + ChatColor.RESET + range + ChatColor.RED + "RangeY: " + ChatColor.RESET + range +ChatColor.RED + "RangeZ: " + ChatColor.RESET + range);


    }

    //    private double calculateLowestDistance(List<Pair<AxisAlignedBB, Location>> positions, CraftLivingEntity player) {
//        double lowestDistance = Double.MAX_VALUE;
//
//        for (Pair<AxisAlignedBB, Location> pair : positions) {
//            AxisAlignedBB position = pair.getLeft();
//            Location eyeLocation = player.getEyeLocation();
//
//            Vector direction = eyeLocation.getDirection();
//
//            AxisAlignedBB hitbox = position;
//            double hitboxMinX = Math.min(hitbox.a, hitbox.d) - 0.1;
//            double hitboxMinY = (Math.min(hitbox.b, hitbox.e)) - 0.1;
//            double hitboxMinZ = Math.min(hitbox.c, hitbox.f) - 0.1;
//            double hitboxMaxX = Math.max(hitbox.a, hitbox.d) - -0.1;
//            double hitboxMaxY = Math.max(hitbox.b, hitbox.e) + 0.1;
//            double hitboxMaxZ = Math.max(hitbox.c, hitbox.f) + 0.1;
//
//
//            AxisAlignedBB hitbox_victim = ((CraftLivingEntity) player).getHandle().getBoundingBox();
//            double hitboxMinX_victim = Math.min(hitbox_victim.a, hitbox_victim.d) - 0.1;
//            double hitboxMinY_victim = (Math.min(hitbox_victim.b, hitbox_victim.e) - 0.1);
//            double hitboxMinZ_victim = Math.min(hitbox_victim.c, hitbox_victim.f) - 0.1;
//            double hitboxMaxX_victim = Math.max(hitbox_victim.a, hitbox_victim.d) - 0.1;
//            double hitboxMaxY_victim = Math.max(hitbox_victim.b, hitbox_victim.e) + 0.1;
//            double hitboxMaxZ_victim = Math.max(hitbox_victim.c, hitbox_victim.f) + 0.1;
//
//
//            double dx = Math.max(0, Math.max(hitboxMinX - hitboxMaxX_victim, hitboxMinX_victim - hitboxMaxX));
//            double dy = Math.max(0, Math.max(hitboxMinY - hitboxMaxY_victim, hitboxMinY_victim - hitboxMaxY));
//            double dz = Math.max(0, Math.max(hitboxMinZ - hitboxMaxZ_victim, hitboxMinZ_victim - hitboxMaxZ));
//            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
//            //player.sendMessage("Reach distance check: " + distance);
//
//            if (lowestDistance > distance) {
//                lowestDistance = distance;
//            }
//        }
//        return lowestDistance;
//    }
    private boolean rayIntersectsBoundingBox(Location rayStart, Location rayEnd, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        double tMinX = (minX - rayStart.getX()) / (rayEnd.getX() - rayStart.getX());
        double tMaxX = (maxX - rayStart.getX()) / (rayEnd.getX() - rayStart.getX());
        double tMinY = (minY - rayStart.getY()) / (rayEnd.getY() - rayStart.getY());
        double tMaxY = (maxY - rayStart.getY()) / (rayEnd.getY() - rayStart.getY());
        double tMinZ = (minZ - rayStart.getZ()) / (rayEnd.getZ() - rayStart.getZ());
        double tMaxZ = (maxZ - rayStart.getZ()) / (rayEnd.getZ() - rayStart.getZ());

        double tMin = Math.max(Math.max(Math.min(tMinX, tMaxX), Math.min(tMinY, tMaxY)), Math.min(tMinZ, tMaxZ));
        double tMax = Math.min(Math.min(Math.max(tMinX, tMaxX), Math.max(tMinY, tMaxY)), Math.max(tMinZ, tMaxZ));

        return tMax >= tMin && tMax >= 0 && tMin <= 1;
    }

    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private void calculateLowestPossibility(final Player player, final CraftLivingEntity victim,
                                            final Location eyeLocation_static, double hitbox_num_static_victim, boolean hitPossible_nodelay) {
        int ping = ((CraftPlayer) player).getHandle().playerConnection.getPlayer().getHandle().ping; // 20 ticks per second, 2 seconds = 40 ticks, use the players ping as math here. IF NEEDED, due to pingspoof can either false or do great.

        new BukkitRunnable() {
            @Override
            public void run() {
                AxisAlignedBB victimhitbox = hitboxSaved_victim.get(hitbox_num_static_victim); // still not good, getting delayed hits.
                hitboxSaved_victim.remove(hitbox_num_static_victim);
                List<Pair<AxisAlignedBB, Location>> damagerInfo = getPlayerPositions((CraftLivingEntity) player);
                List<Pair<AxisAlignedBB, Location>> victimInfo = getPlayerPositions(victim);
                double lowestReach = Double.MAX_VALUE;
                Location hitPoint = null;
                Double hitboxMinX = null;
                Double hitboxMinY = null;
                Double hitboxMinZ = null;

                // account for all the deltas, including the pitch and yaw ones.
                double totalRotationChange = (abs(recentDeltaPitch.get(player.getName())) + abs(recentDeltaYaw.get(player.getName())));
                int size = min(victimInfo.size(), damagerInfo.size());
                //player.sendMessage("Your ping is " + ping);
                Location lowestDistanceLoc_vic = victim.getLocation();
                Location lowestDistanceLoc_dgr = player.getLocation();

                Location lowestReachLoc_vic = victim.getLocation();
                Location lowestReachLoc_dgr = player.getLocation();

                AxisAlignedBB victim_hitbox = victimInfo.get(size - 1).getLeft();//victimhitbox;


                //player.sendMessage("Your hitbox is LOC: " + victimInfo.get(99).getRight());

                boolean hitPossible = false;
                boolean hitbox_flag = true; // making this less lenient
                double hitRange = 0;


                for (int i = 0; i < size; i++) {
                    Location eyeLocation = damagerInfo.get(i).getRight();
                    //Vector direction = eyeLocation.getDirection();

                    AxisAlignedBB hitbox = victim_hitbox; //victimInfo.get(i).getLeft();


// Calculate the direction vector from the player's eye location
                    Vector direction = eyeLocation.getDirection().multiply(3); // Extend the ray 3 blocks ahead

// Calculate the end point of the ray
                    Location rayEnd = eyeLocation.clone().add(direction);

// Calculate the minimum and maximum coordinates of the hitbox
                    hitboxMinX = Math.min(hitbox.a, hitbox.d) - 0.1;
                    hitboxMinY = Math.min(hitbox.b, hitbox.e) - 0.13;
                    hitboxMinZ = Math.min(hitbox.c, hitbox.f) - 0.1;
                    double hitboxMaxX = Math.max(hitbox.a, hitbox.d) + 0.1;
                    double hitboxMaxY = Math.max(hitbox.b, hitbox.e) + 0.1;
                    double hitboxMaxZ = Math.max(hitbox.c, hitbox.f) + 0.1;

// Check for intersection between the ray and the hitbox
                    hitPossible = rayIntersectsBoundingBox(eyeLocation, rayEnd, hitboxMinX, hitboxMinY, hitboxMinZ, hitboxMaxX, hitboxMaxY, hitboxMaxZ);
                    //hitRange = eyeLocation.distance(rayEnd); // Calculate the range of the hit

                    if (hitPossible) {
                        break;
                    }
                }


                for (int i = 0; i < size; i++) {
                    Location eyeLocation = damagerInfo.get(i).getRight();

                    Vector direction = eyeLocation.getDirection();

                    AxisAlignedBB hitbox = victim_hitbox;//victimInfo.get(i).getLeft();


                    hitboxMinX = (min(hitbox.a, hitbox.d) - 0.13);
                    hitboxMinY = (min(hitbox.b, hitbox.e) - 0.13);
                    hitboxMinZ = (min(hitbox.c, hitbox.f) - 0.11);
                    double hitboxMaxX = (max(hitbox.a, hitbox.d) + 0.07);
                    double hitboxMaxY = (max(hitbox.b, hitbox.e) + 0.1);
                    double hitboxMaxZ = (max(hitbox.c, hitbox.f) + 0.08);

                    double tMaxX = (hitboxMaxX - eyeLocation.getX()) / direction.getX();
                    double tMinX = (hitboxMinX - eyeLocation.getX()) / direction.getX();
                    double tMaxY = (hitboxMaxY - eyeLocation.getY()) / direction.getY();
                    double tMinY = (hitboxMinY - eyeLocation.getY()) / direction.getY();
                    double tMaxZ = (hitboxMaxZ - eyeLocation.getZ()) / direction.getZ();
                    double tMinZ = (hitboxMinZ - eyeLocation.getZ()) / direction.getZ();

                    double tMax = max(max(min(tMaxX, tMinX), min(tMaxY, tMinY)), min(tMaxZ, tMinZ));
                    double tMin = min(max(min(tMinX, tMaxX), min(tMinY, tMaxY)), min(tMinZ, tMaxZ));

                    Location hitboxOutermost = eyeLocation.clone().add(direction.clone().multiply(tMax));
                    double difx = hitboxOutermost.getX() - eyeLocation.getX();
                    double dify = hitboxOutermost.getY() - eyeLocation.getY();
                    double difz = hitboxOutermost.getZ() - eyeLocation.getZ();

                    double eyeX = eyeLocation.getX();
                    double eyeY = eyeLocation.getY();
                    double eyeZ = eyeLocation.getZ();
                    double hX = hitboxOutermost.getX();
                    double hY = hitboxOutermost.getY();
                    double hZ = hitboxOutermost.getZ();

                    double vm = Math.sqrt((Math.pow(eyeX - hX, 2)) + (Math.pow(eyeY - hY, 2)) + (Math.pow(eyeZ - hZ, 2)));

                    //double vm = sqrt((pow(eyeX - hX, 2)) + (pow(eyeY - hY, 2)) + (pow(eyeZ - hZ, 2)));
                    //player.sendMessage("VM: " + vm);
// Define the number of points to sample on the hitbox
                    int numPoints = 100;
                    double closestRange = Double.MAX_VALUE;
                    Location closestPoint = null;

// Iterate over the points on the hitbox and find the closest range to the player's eye location
                    for (int j = 0; j < numPoints; j++) {
                        double t = (double) j / (numPoints - 1);
                        double hitboxX = lerp(hitbox.a, hitbox.d, t);
                        double hitboxY = lerp(hitbox.b, hitbox.e, t);
                        double hitboxZ = lerp(hitbox.c, hitbox.f, t);
                        Location point = new Location(eyeLocation.getWorld(), hitboxX, hitboxY, hitboxZ);
                        double range = eyeLocation.distance(point);
                        if (range < closestRange) {
                            closestRange = range;
                            closestPoint = point;
                        }
                    }

                    if (vm > closestRange) {
                        //vm = closestRange;
                    }

                    hitPoint = eyeLocation.clone().add(direction.clone().multiply(tMax));


                    if (!(hitPoint.getX() < hitboxMinX || hitPoint.getX() > hitboxMaxX || hitPoint.getY() < hitboxMinY || hitPoint.getY() > hitboxMaxY || hitPoint.getZ() < hitboxMinZ || hitPoint.getZ() > hitboxMaxZ)) {
                        hitbox_flag = false;
                    }


                    if (lowestReach > vm && (vm < 10E5)) {
                        lowestReachLoc_vic = victim.getLocation();
                        lowestReachLoc_dgr = player.getLocation();
                        lowestReach = vm;
                    }
                }
                if (hitbox_flag) {
                    //player.sendMessage("hitbox, rotation: " + totalRotationChange); // TODO: for some reason the rotation mostly when actually alerting is equal or above 360. account for these.
                }

                String playerName = player.getName();

                List<Location> hitpointList = PlayerHitpointList.getOrDefault(playerName, new ArrayList<>());

                // Create a new Location object with relative coordinates
                Location relativeHitPoint = new Location(
                        hitPoint.getWorld(),
                        hitPoint.getX() - hitboxMinX,
                        hitPoint.getY() - hitboxMinY,
                        hitPoint.getZ() - hitboxMinZ
                );

                hitpointList.add(relativeHitPoint);
                PlayerHitpointList.put(playerName, hitpointList);
                // distance check below, above reach.

                double lowestDistance = Double.MAX_VALUE;

                //int size = Math.min(victimInfo.size(), damagerInfo.size());
//                for (int i = 0; i < size; i++) {
//                    Pair<AxisAlignedBB, Location> position_victim = victimInfo.get(i);
//                    Pair<AxisAlignedBB, Location> position_attacker = damagerInfo.get(i);
//                    Location eyeLocation = position_attacker.getRight();

                for (int i = 0; i < size; i++) {
                    //Pair<AxisAlignedBB, Location> position_victim = victimInfo.get(i);
                    //victim.

                    //Vector direction = eyeLocation.getDirection();

                    //AxisAlignedBB hitbox = victimInfo.get(i).getLeft();

                    Location eyeLocation = damagerInfo.get(i).getRight();

                    Vector direction = eyeLocation.getDirection();

                    AxisAlignedBB hitbox = victimInfo.get(i).getLeft();
                    hitboxMinX = min(hitbox.a, hitbox.d) - 0.1;
                    hitboxMinY = min(hitbox.b, hitbox.e) - 0.13;
                    hitboxMinZ = min(hitbox.c, hitbox.f) - 0.1;
                    double hitboxMaxX = max(hitbox.a, hitbox.d) + 0.1;
                    double hitboxMaxY = max(hitbox.b, hitbox.e) + 0.1;
                    double hitboxMaxZ = max(hitbox.c, hitbox.f) + 0.1;


                    AxisAlignedBB hitbox_victim = ((CraftLivingEntity) player).getHandle().getBoundingBox();
                    double hitboxMinX_victim = min(hitbox_victim.a, hitbox_victim.d) - 0.1;
                    double hitboxMinY_victim = (min(hitbox_victim.b, hitbox_victim.e) - 0.1);
                    double hitboxMinZ_victim = min(hitbox_victim.c, hitbox_victim.f) - 0.1;
                    double hitboxMaxX_victim = max(hitbox_victim.a, hitbox_victim.d) - 0.1;
                    double hitboxMaxY_victim = max(hitbox_victim.b, hitbox_victim.e) + 0.1;
                    double hitboxMaxZ_victim = max(hitbox_victim.c, hitbox_victim.f) + 0.1;


                    double tMaxX = (hitboxMaxX - eyeLocation.getX()) / direction.getX();
                    double tMinX = (hitboxMinX - eyeLocation.getX()) / direction.getX();
                    double tMaxY = (hitboxMaxY - eyeLocation.getY()) / direction.getY();
                    double tMinY = (hitboxMinY - eyeLocation.getY()) / direction.getY();
                    double tMaxZ = (hitboxMaxZ - eyeLocation.getZ()) / direction.getZ();
                    double tMinZ = (hitboxMinZ - eyeLocation.getZ()) / direction.getZ();

                    double tMax = max(max(min(tMaxX, tMinX), min(tMaxY, tMinY)), min(tMaxZ, tMinZ));
                    double tMin = min(max(min(tMinX, tMaxX), min(tMinY, tMaxY)), min(tMinZ, tMaxZ));

                    double shortestDistance = Double.MAX_VALUE;
                    double shortestX = 0.0;
                    double shortestY = 0.0;
                    double shortestZ = 0.0;

                    if (tMax >= tMin && tMax >= 0) {
                        double x = eyeLocation.getX() + direction.getX() * tMax;
                        double y = eyeLocation.getY() + direction.getY() * tMax;
                        double z = eyeLocation.getZ() + direction.getZ() * tMax;

                        double distance = sqrt((x - eyeLocation.getX()) * (x - eyeLocation.getX()) +
                                (y - eyeLocation.getY()) * (y - eyeLocation.getY()) +
                                (z - eyeLocation.getZ()) * (z - eyeLocation.getZ()));

                        if (distance < shortestDistance) {
                            shortestDistance = distance;
                            lowestDistanceLoc_vic = victim.getLocation();
                            lowestDistanceLoc_dgr = player.getLocation();
                            shortestX = x;
                            shortestY = y;
                            shortestZ = z;
                        }
                    }

//                    double dx = Math.max(0, Math.max(hitboxMinX - hitboxMaxX_victim, hitboxMinX_victim - hitboxMaxX));
//                    double dy = Math.max(0, Math.max(hitboxMinY - hitboxMaxY_victim, hitboxMinY_victim - hitboxMaxY));
//                    double dz = Math.max(0, Math.max(hitboxMinZ - hitboxMaxZ_victim, hitboxMinZ_victim - hitboxMaxZ));
//                    double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
//                    //player.sendMessage("Reach distance check: " + distance);

                    if (lowestDistance > shortestDistance) {
                        lowestDistance = shortestDistance;
                    }
                }

                // code here!
                if (lowestReach > 3) {
                    // player.sendMessage(ChatColor.RED + "Reach check: " + lowestReach);
                }
                if (lowestDistance > 3) {
                    //   player.sendMessage(ChatColor.DARK_AQUA + "distance check: " + lowestDistance);
                }

                if (lowestDistance < 1 && lowestReach > 3) {
                    // false positive
                } else if (lowestReach > 100 && lowestDistance > 100) {
                    // movement checks should kick in then this
                } else if (lowestReach < 3 && lowestDistance > 7) {
                    // player is probably moving too fast
                } else {
                    if (lowestReach > 3) {
                        //player.sendMessage(ChatColor.DARK_RED + "Using reach of " + ChatColor.YELLOW + lowestReach);
                    }
                }

                if (!hitPossible && !hitPossible_nodelay && hitbox_flag) {
//            damager.sendMessage(ChatColor.WHITE + "[" + ChatColor.AQUA + "AntiCheat" + ChatColor.WHITE + "] " + ChatColor.DARK_PURPLE + "- " + ChatColor.YELLOW + damagerName + ChatColor.WHITE + " is identified in suspicious Combat, hitbox");
                    identifieralertplayers("Hitbox", player);
                    UpdateAlerts(player, +0.5, "combat");

                }
//                    printReach(player, victim, lowestReach, lowestDistance, lowestReachLoc_vic, lowestReachLoc_dgr, lowestDistanceLoc_vic, lowestDistanceLoc_dgr, hitbox_flag, hitPossible, hitPossible_nodelay);
            }

        }.runTaskLaterAsynchronously(plugin, ((ping / 20) + 5)); // 20 ticks per second, 2 seconds = 40 ticks, use the players ping as math here. IF NEEDED, due to pingspoof can either false or do great.

        //printReach(lowestDistance); // this works, just that the update, of the lowestdistance isn't working.
    }


    private List<Pair<AxisAlignedBB, Location>> getPlayerPositions(CraftLivingEntity player) {
        return playerPositions.getOrDefault(player.getName(), new ArrayList<>());
    }


    private double getDistance(Location from, Location to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }


    public static double getClosestLineDistance(Location hitbox1, Location hitbox2) {
        List<Location> line = new ArrayList<>();

        // Calculate the direction vectors of the two hitboxes.
        Vector direction1 = hitbox1.subtract(hitbox2).toVector();
        Vector direction2 = hitbox2.subtract(hitbox1).toVector();

        // Calculate the two intersection points of the two lines.
        Location intersection1 = hitbox1.clone().add(direction1.multiply(direction2.dot(direction1) / direction2.dot(direction2)));
        Location intersection2 = hitbox2.clone().add(direction2.multiply(direction1.dot(direction2) / direction1.dot(direction1)));

        // Add the two intersection points to the line.
        line.add(intersection1);
        line.add(intersection2);

        // Calculate the range of the two lines.
        double range1 = Math.abs(intersection1.getX()) + Math.abs(intersection1.getY()) + Math.abs(intersection1.getZ());
        double range2 = Math.abs(intersection2.getX()) + Math.abs(intersection2.getY()) + Math.abs(intersection2.getZ());

        // Find the line with the lowest range.
        Location closestLine = line.get(0);
        double lowestRange = range1;
        if (range2 < lowestRange) {
            closestLine = line.get(1);
            lowestRange = range2;
        }

        // Return the distance of the closest line.
        return closestLine.distance(hitbox1);
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


    private void startPositionSavingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    //PacketPlayOutUpdateHealth packet = new PacketPlayOutUpdateHealth(0, 0, 0);
                    //((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

                    // you can use the below to troll hackers and make it spoof their own health to max always, it'll make them think they're on full hp whilst not actually being full
//                    dataWatcher.a(6, (float) (20)); // Set the health value
//                    PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(player.getEntityId(), dataWatcher, true);
//                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    positionSavingExecutor2.submit(() -> saveData(player));

                    positionSavingExecutor.submit(() -> savePlayerPosition((CraftPlayer) player));
                }
            }
        }.runTaskTimer(this, 0L, 1L); // Save positions every 1 tick (10 milliseconds)
    }


    private void savePlayerPosition(CraftLivingEntity player) {
        List<Pair<AxisAlignedBB, Location>> positions = playerPositions.computeIfAbsent(player.getName(), k -> new ArrayList<>());
        positions.add(Pair.of(player.getHandle().getBoundingBox(), player.getEyeLocation()));

        // Ensure the list of positions does not exceed the maximum size of 100
        int ping = ((CraftPlayer) player).getHandle().playerConnection.getPlayer().getHandle().ping; // 20 ticks per second, 2 seconds = 40 ticks, use the players ping as math here. IF NEEDED, due to pingspoof can either false or do great.

        if (positions.size() > (ping + 50)) {
            positions.remove(0);
        }
    }


    public List<PlayerSavedLocDetails> getDeltaGroup(Player player, long currentTick, int deltaSize) {
        String playerName = player.getName();
        List<PlayerSavedLocDetails> playerDetailsList = playerPositionDetailsDelta.get(playerName);

        List<PlayerSavedLocDetails> deltaGroup = new ArrayList<>();

        if (playerDetailsList != null) {
            int startIndex = -1;
            int endIndex = -1;

            // Find the index of the data point closest to the currentTick
            for (int i = 0; i < playerDetailsList.size(); i++) {
                PlayerSavedLocDetails details = playerDetailsList.get(i);
                List<Long> ticks = details.getTicks();

                long lastTick = ticks.get(ticks.size() - 1);

                if (currentTick <= lastTick) {
                    endIndex = i;
                    startIndex = Math.max(0, endIndex - (deltaSize / 2));
                    break;
                }
            }

            if (startIndex != -1 && endIndex != -1) {
                // Collect the delta group of data points
                for (int i = startIndex; i <= endIndex; i++) {
                    deltaGroup.add(playerDetailsList.get(i));
                }
            }
        }

        return deltaGroup;
    }


    // Saving data for a player
    public void saveData(Player player) {
        Location location1 = player.getLocation();
        Location location2 = player.getEyeLocation();
        AxisAlignedBB boundingBox = ((CraftPlayer) player).getHandle().getBoundingBox();
        Long tick = Bukkit.getServer().getWorld("world").getFullTime();
        String playerName = player.getName();
        List<PlayerSavedLocDetails> playerDetailsList = playerPositionDetailsDelta.getOrDefault(playerName, new ArrayList<>());

        // Retrieve the last PlayerPositionDetails object from the list
        PlayerSavedLocDetails lastDetails = playerDetailsList.isEmpty() ? null : playerDetailsList.get(playerDetailsList.size() - 1);

        if (lastDetails == null || lastDetails.getLocations1().size() >= 10) {
            // If no previous entry or the last entry is already full (10 elements), create a new PlayerPositionDetails object
            lastDetails = new PlayerSavedLocDetails();
            playerDetailsList.add(lastDetails);
        }

        // Add the data and the corresponding tick to the last PlayerPositionDetails object
        lastDetails.addData(location1, location2, boundingBox, tick);

        // Update the playerDetailsList in the map
        playerPositionDetailsDelta.put(playerName, playerDetailsList);
    }


    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent event) {
        if (!development_mode) {
            return;
        }
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == CREATIVE) {
            return;
        }
        String playerName = player.getName();

        //event.getItemInHand().getAmount();
        double totalRotationChange = Math.abs(recentDeltaPitch.get(player.getName())) + Math.abs(recentDeltaYaw.get(player.getName()));
        if (round(totalRotationChange) >= 720) {
            totalRotationChange -= 720;
        } else if (round(totalRotationChange) >= 320) {
            totalRotationChange -= 320;
        } else if (round(totalRotationChange) >= 40) {
            totalRotationChange -= 40;
        }
//        player.sendMessage("totalRotationChange: " + totalRotationChange);
        double DeltaX = previousDeltaX.get(playerName);
        double DeltaZ = previousDeltaZ.get(playerName);
        double deltaXZ = Math.hypot(DeltaX, DeltaZ);
        double deltaY = previousDeltaY.get(playerName);

        // no need for deltaY
//        player.sendMessage("deltaXZ: " + deltaXZ);

        // is block placed under player.
        double playerY = player.getLocation().getY(); // note that the Y position is their legs not eyes, so it's 1 block from the ground.
        double BlockY = event.getBlockPlaced().getY();
//        player.sendMessage("blockY: " + BlockY);
//        player.sendMessage("PlayerY: " + playerY);
//        player.sendMessage("deltaY: " + deltaY);
//
//        player.sendMessage("playerX: " + Math.ceil(player.getLocation().getX()));
//        player.sendMessage("BlockX: " + event.getBlockPlaced().getX());
//
//        player.sendMessage(ChatColor.RED + "True: " + (((playerY - BlockY) < 2) & (playerY - BlockY) > 0));

        // Raytrace check
        if (Interact3) {
            BlockFace face = event.getBlockAgainst().getFace(event.getBlockPlaced());
            Vector direction = new Vector(face.getModX(), face.getModY(), face.getModZ());

            Location eyeLocation = player.getEyeLocation();
            Vector eyeDirection = eyeLocation.getDirection();

            if (direction.dot(eyeDirection) > 0) {
                event.setCancelled(true);
                identifieralertplayers("Interact 3", player);
                UpdateAlerts(player, +1.0, "undetermined");



            }
        }
//
//        Block ba = event.getBlockAgainst();
//
//        if (!event.getBlockPlaced().getType().isBlock()) {
//
//        } else {
//            Block b = event.getBlock();
//            double ypos = b.getLocation().getY() - player.getLocation().getY();
//            double distance = player.getLocation().distance(b.getLocation());
//            double ab_distance = player.getLocation().distance(ba.getLocation()) + 0.4;
//
//            if (distance >= 1.3 && distance > ab_distance && ypos <= 0.5) {
//                player.sendMessage("Unusual block placement?");
//            }
//        }
        double previousBlockList = PreviousBlockPlacementsYComp.getOrDefault(player.getName(), new Double(0));

        Location PBLocation = PreviousBlockPlacementLocation.getOrDefault(player.getName(), event.getBlockPlaced().getLocation());

        PreviousBlockPlacementLocation.put(player.getName(), event.getBlockPlaced().getLocation());

        double rangeFromBlockX = abs(abs(player.getLocation().getX()) - abs(event.getBlockPlaced().getX()));
        double rangeFromBlockZ = abs(abs(player.getLocation().getZ()) - abs(event.getBlockPlaced().getZ()));
//        player.sendMessage("numbers; " + rangeFromBlockZ + " anotherone " + rangeFromBlockX);
//        player.sendMessage(ChatColor.RED + "True2: " +(rangeFromBlockX < 1 && rangeFromBlockZ < 1));

        if ((((playerY - BlockY) < 2) & (playerY - BlockY) > 0) && rangeFromBlockX < 1 && rangeFromBlockZ < 1) {
            // block placed under player
//            player.sendMessage("Block placed");

            previousBlockList += 1;
            PreviousBlockPlacementsYComp.put(player.getName(), previousBlockList);
            if (previousBlockList > 2) {
//                player.sendMessage("You're bridging.");
                isBridging.put(player.getName(), (double) Bukkit.getServer().getWorld("world").getFullTime());
                if ((BlockY - PBLocation.getY()) == 1) {
//                    player.sendMessage("Towering up");
                } else { // just a weird layout, combine both. temporary
                    if (totalRotationChange > 5) {
//                        player.sendMessage(ChatColor.DARK_RED + "Scaffold - beta check, " + totalRotationChange);
                        if (Scaffold) { // it's this tight and not total disabled due to it being in works
                            UpdateAlerts(player, +1.0, "unsafe");
                            identifieralertplayers("Scaffold(Beta, " + totalRotationChange + ")", player);
                        }// code an interact check and you're basically done with scaffold, and a fastplace check but that's for autoclickers
                    }
                }
            }

        } else {
            PreviousBlockPlacementsYComp.remove(playerName);
        }

    }


//    @EventHandler
//    public void onInventoryClick(InventoryClickEvent event) {
//        if (!(event.getWhoClicked() instanceof Player)) {
//            return;
//        }
//        Player player = (Player) event.getWhoClicked();
//        if (player.getGameMode() == SPECTATOR) {
//            return;
//        }
//        Location previousLocation = null;
//        if (previousLocationInventory.containsKey(player.getName())) {
//            previousLocation = previousLocationInventory.get(player.getName());
//            previousLocationInventory.remove(player.getName());
//            previousLocationInventory.put(player.getName(), player.getLocation());
//        } else {
//            previousLocationInventory.put(player.getName(), player.getLocation());
//        }
//        double invmoveverbose = 0;
//        if (InvmoveVerbose.containsKey(player.getName())) {
//            invmoveverbose = InvmoveVerbose.get(player.getName());
//            player.sendMessage("SET" + invmoveverbose);
//        } else {
//            InvmoveVerbose.put(player.getName(), (double) 0);
//        }
//
//        if (previousLocation == null) {
//            return;
//        }
//        if (player.isSprinting()) {
//            player.sendMessage("invmove sprint");
//            return;
//        }
//        player.sendMessage("invmove verbose" + invmoveverbose);
//        if ((!(formatOne(previousLocation.getX()).contentEquals(formatOne(player.getLocation().getX())) && (formatOne(previousLocation.getZ()).contentEquals(formatOne(player.getLocation().getZ()))))) == true);
//         invmoveverbose +=1;
//         InvmoveVerbose.remove(player.getName());
//        InvmoveVerbose.put(player.getName(), (double) invmoveverbose);
//        player.sendMessage("TEST +:" + InvmoveVerbose.get(player.getName()));
//        if (invmoveverbose > 3) {
//             player.sendMessage("Invmove verbose location >3");
//             return;
//        } else {
//             invmoveverbose -=1;
//            InvmoveVerbose.remove(player.getName());
//            InvmoveVerbose.put(player.getName(), (double) invmoveverbose);
//
//         }
//        // here meaning no flag. you can either decrease or do something.
//        //player.sendMessage("invmove location" + (formatDouble(previousLocation.getZ()).contentEquals(formatDouble(player.getLocation().getZ()))) + " aaaa " + formatDouble(previousLocation.getX()).contentEquals(formatDouble(player.getLocation().getX())));
//
//        }


    public void updateInvVerbose(Player player, int amount) {
        int curamnt = InvmoveVerbose.getOrDefault(player.getName(), 0);
        int tmp = curamnt + amount;
        if (tmp < 0) {
            tmp = 0;
        }
        InvmoveVerbose.remove(player.getName());
        InvmoveVerbose.put(player.getName(), tmp);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!development_mode || !Inventory_move) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == CREATIVE) {
            return;
        }


        Location previousLocation = previousLocationInventory.getOrDefault(player.getName(), null);
        previousLocationInventory.put(player.getName(), player.getLocation());

        if (previousLocation.distance(player.getLocation()) > 2) {
            previousLocation = previousLocation_onGround.get(player.getName()); // a fail safe/fallback.
        }
        //player.sendMessage("TEST " + InvmoveVerbose.get(player.getName()));
//        player.sendMessage("dis: " + previousLocation.distance(player.getLocation()));
        if (player.isSprinting()) {
            updateInvVerbose(player, +2);
            event.setCancelled(true);
        } else if (previousLocation != null && (previousLocation.distance(player.getLocation()) > 0.02) && player.isOnGround() && !((player.getNoDamageTicks() <= 20) && (7 <= player.getNoDamageTicks()))) {
            updateInvVerbose(player, +1);
        } else {
            updateInvVerbose(player, -1);
        }
        if (InvmoveVerbose.get(player.getName()) > 3) {
            identifieralertplayers("Inventory move(Gamma)", player); // when goes higher than 3 it resets back to 0 and adds +1 alert to invmove, and also, for sprint and this normal one two diff types
//            event.setCancelled(true);
            UpdateAlerts(player, +1.0, "safe");
            InvmoveVerbose.put(player.getName(), 0);
        }

    }

    private boolean hasSameCoordinates(Location loc1, Location loc2) {
        return formatOne(loc1.getX()).equals(formatOne(loc2.getX())) &&
                formatOne(loc1.getZ()).equals(formatOne(loc2.getZ()));
    }

    public static void sendBlockChangeArea(Player player) { // updates ghost blocks for player. a 8 by 8
        Location location = player.getLocation();
        int xMin = location.getBlockX() - 4;
        int xMax = location.getBlockX() + 4;
        int yMin = location.getBlockY() - 4;
        int yMax = location.getBlockY() + 4;
        int zMin = location.getBlockZ() - 4;
        int zMax = location.getBlockZ() + 4;
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    Block block = player.getWorld().getBlockAt(x, y, z);
                    player.sendBlockChange(new Location(player.getWorld(), x, y, z), block.getType(), block.getData());
                }
            }
        }
    }


    public static int higher_getY3by3BlockUnderplay_max(Player player) {
        List<String> wordsListofdumbblocks = new ArrayList<>(Arrays.asList(
                "SKULL",
                "LADDER",
                "REDSTONE",
                "BUTTON",
                "SIGN",
                "PLATE",
                "PISTON_EXTENSION",
                "WEB",
                "WATER",
                "LAVA",
                "FLOWER",
                "LONG_GRASS",
                "ROSE",
                "DEAD_BUSH",
                "RAIL",
                "TORCH",
                "PLANT"
        ));
        int highestY = Integer.MIN_VALUE;
        Location location = player.getLocation().clone().add(0,-0.4,0);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = location.clone().add(x, 0, z);
                while (loc.getBlock().getType() == Material.AIR || wordsListofdumbblocks.contains(loc.getBlock().getType().name())) {
                    loc.subtract(0, 1, 0);
                    if (loc.getY() < 1) {
                        break;
                    }
                }
                if (loc.getBlock().getType() != Material.AIR && !wordsListofdumbblocks.contains(loc.getBlock().getType().name()) && loc.getBlockY() > highestY) {
                    highestY = loc.getBlockY();
                }
            }
        }
        return highestY == Integer.MIN_VALUE ? 0 : highestY;
    }

    public static int lower_getY3by3BlockUnderplay_max(Player player) {
        List<String> wordsListofdumbblocks = new ArrayList<>(Arrays.asList(
                "SKULL",
                "LADDER",
                "REDSTONE",
                "BUTTON",
                "SIGN",
                "PLATE",
                "PISTON_EXTENSION",
                "WEB",
                "WATER",
                "LAVA",
                "FLOWER",
                "LONG_GRASS",
                "ROSE",
                "DEAD_BUSH",
                "RAIL",
                "TORCH",
                "PLANT"
        ));
        int highestY = Integer.MIN_VALUE;
        Location location = player.getLocation().clone().add(0,-1,0);
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = location.clone().add(x, 0, z);
                while (loc.getBlock().getType() == Material.AIR || wordsListofdumbblocks.contains(loc.getBlock().getType().name())) {
                    loc.subtract(0, 1, 0);
                    if (loc.getY() < 1) {
                        break;
                    }
                }
                if (loc.getBlock().getType() != Material.AIR && !wordsListofdumbblocks.contains(loc.getBlock().getType().name()) && loc.getBlockY() > highestY) {
                    highestY = loc.getBlockY();
                }
            }
        }
        return highestY == Integer.MIN_VALUE ? 0 : highestY;
    }

    public static Location findEmptyPlace(Player player, int x) {
        Location loc = player.getLocation();
        int y = loc.getBlockY();
        World world = player.getWorld();
        int radius = x;
        double minDistance = Double.MAX_VALUE;
        Location closestLocation = null;
        while (true) {
            for (int i = loc.getBlockX() - radius; i <= loc.getBlockX() + radius; i++) {
                for (int j = loc.getBlockZ() - radius; j <= loc.getBlockZ() + radius; j++) {
                    for (int k = y - radius; k <= y + radius; k++) {
                        if (world.getBlockAt(i, k, j).isEmpty() && world.getBlockAt(i, k + 1, j).isEmpty()) {
                            Location newLocation = new Location(world, i + 0.5, k, j + 0.5);
                            double distance = newLocation.distance(loc);
                            if (distance < minDistance) {
                                minDistance = distance;
                                closestLocation = newLocation;
                            }
                        }
                    }
                }
            }
            if (closestLocation != null) {
                return closestLocation;
            }
            radius++;
        }
    }


    public static final double MINIMUM_DIVISOR = ((Math.pow(0.2f, 3) * 8) * 0.15) - 1e-3; // 1e-3 for float imprecision


    public static double gcd(double a, double b) {
        if (a == 0) return 0;

        // Make sure a is larger than b
        if (a < b) {
            double temp = a;
            a = b;
            b = temp;
        }

        while (b > MINIMUM_DIVISOR) { // Minimum minecraft sensitivity
            double temp = a - (Math.floor(a / b) * b);
            a = b;
            b = temp;
        }

        return a;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) { // you can now make a speed check, LETS GO
        Player player = event.getPlayer();
        if (disableMovement && (!player.hasPermission("bypassDisableMovement"))) {
            player.teleport(previous_location.get(player.getName()));
            //            event.setCancelled(true);
        }
        //savePlayerPosition(player, (CraftLivingEntity) player);

        if (player.getGameMode() == SPECTATOR || player.getGameMode() == CREATIVE || player.getAllowFlight()) { // there is nothing to do in spectator mode, i think it freezes the server.. unsure
            return;
        }
        List<String> wordsListofdumbblocks = new ArrayList<>(Arrays.asList(
                "STEP",
                "STAIRS",
                "FENCE",
                "SKULL",
                "SKULL",
                "TRAP_DOOR",
                "LADDER",
                "STEP",
                "TRAP_DOOR",
                "REDSTONE",
                "BED",
                "CHEST",
                "BUTTON",
                "SIGN",
                "PLATE",
                "PISTON_EXTENSION",
                "WEB",
                "WATER",
                "LAVA",
                "FLOWER",
                "LONG_GRASS",
                "ROSE",
                "DEAD_BUSH",
                "SLAB",
                "RAIL",
                "TORCH",
                "PLANT",
                "THIN_GLASS",
                "STAINED_GLASS_PANE",
                "SOUL_SAND",
                "DOOR"
        ));

        if (Phase) {
            AxisAlignedBB hitbox = ((CraftPlayer) player).getHandle().getBoundingBox();
            World world = player.getWorld();
            for (int x = (int) Math.floor(hitbox.a); x < Math.ceil(hitbox.d); x++) {
                for (int y = (int) Math.floor(hitbox.b); y < Math.ceil(hitbox.e); y++) {
                    for (int z = (int) Math.floor(hitbox.c); z < Math.ceil(hitbox.f); z++) {
                        Block block = world.getBlockAt(x, y, z);

                        if (!(containsWord_rev(wordsListofdumbblocks, block.getType().name()))) {
                            if (block.getType() != Material.AIR) {
                                // The top half of the player's hitbox is colliding with a block
                                if (block.getType() != Material.AIR) {
                                    // The player's hitbox is colliding with a block
//                                player.sendMessage("inside a block " + block.getType().name());
//                                player.teleport(previousLocation_onGround.get(player.getName()));
//                                event.setCancelled(true);
                                    Location destination = findEmptyPlace(player, 0);
                                    destination.setYaw(player.getLocation().getYaw());
                                    destination.setPitch(player.getLocation().getPitch());
                                    player.teleport(destination);
//                                    identifieralertplayers("Phase(" + block.getType().name() + ")", player);
                                    // i think for now, it'll remain just a blocker
                                }
                            }
                        }
                    }
                }
            }
        }

        if (previous_location.containsKey(player.getName())) {
            previous_location.remove(player.getName());
            previous_location.put(player.getName(), player.getLocation());
        } else {
            previous_location.put(player.getName(), player.getLocation());
        }


//        player.sendMessage("pitch: " +((CraftPlayer) player).getHandle().pitch);
        if (Invalid_pitch) {
            if (!((((CraftPlayer) player).getHandle().pitch) > -91 && ((CraftPlayer) player).getHandle().pitch < 91)) {
//            event.setCancelled(true);
                //((CraftPlayer) player).getHandle().pitch = 100;
                identifieralertplayers("Invalid pitch", player);
                UpdateAlerts(player, +1.0, "spam");
                player.teleport(previous_location.get(player.getName()));
//            event.setCancelled(true);
//            player.sendMessage("true1:" + (((((CraftPlayer) player).getHandle().pitch) > -89)));
//            player.sendMessage("true2:" + (((CraftPlayer) player).getHandle().pitch < 91));

            }
        }
//        player.sendMessage("yaw: " +((CraftPlayer) player).getHandle().yaw);


        Location currentLocation = player.getLocation().clone().add(player.getLocation().getX(), player.getLocation().getY() + 1, player.getLocation().getZ());
        Block block = currentLocation.getBlock();

        // Check if the player is inside the block
        //if (block.getType() != Material.AIR && block.getType() != Material.WATER && block.getType() != Material.STATIONARY_WATER && block.getType() != Material.LAVA && block.getType() != Material.STATIONARY_LAVA ) {
        //player.sendMessage("In a block");
        //}


        // TODO: as you did to the getDirection of the glass weird staircase, that falsely flags the jump detection,
        //  you may be able to getLocation of that direction, and via the same past direction/location you subtract them both to get a delta X(that works) and Z.

        Double LastJump_tick = LastTimeSinceJumpInTicks.computeIfAbsent(player.getName(), k -> 0.0);
        Double LastTeleport_tick = LastTimeSinceTeleportinTicks.computeIfAbsent(player.getName(), k -> 0.0);
        Double LastSprint_tick = LastTimeSinceSprintingInTicks.computeIfAbsent(player.getName(), k -> 0.0);

        Double lastTimeSinceJump_calc = (Bukkit.getServer().getWorld("world").getFullTime() - LastJump_tick);
        Double LastTimeSinceTeleport_calc = (Bukkit.getServer().getWorld("world").getFullTime() - LastTeleport_tick);
        Double LastTimeSinceSprint_calc = (Bukkit.getServer().getWorld("world").getFullTime() - LastSprint_tick);

        Boolean walkingonoffobjects = false;
        Boolean in_a_liquid = false;
        Boolean player_jumped = false;
        Boolean modificationUsage = false;
        Boolean landedonslime = false;
        wasOnSlimeBlock.computeIfAbsent(player.getName(), k -> false);

        // TODO: YOU HAVE TO MAKE A CHECK AND TO KNOW WHETHER OR NOT THERE IS A BLOCK ON TOP OF THE BLOCK "UNDERPLAYER" CAUSE IT FALSES,
        // TODO: AND YOU CAN FLY UP NEXT TO A WALL.
        double deltaX = (event.getTo().getX() - event.getFrom().getX()); //Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());
        double deltaZ = (event.getTo().getZ() - event.getFrom().getZ());
        double deltaXZ = Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ());

        //double VelocityH = Math.hypot(player.getVelocity().getX(), player.getVelocity().getZ());

        double deltaY = (event.getTo().getY() - event.getFrom().getY());
//         player.getLocation().getYaw();
//         player.sendMessage("last_yaw: " + ((CraftPlayer) player).getHandle().lastYaw + " current yaw: " + );
        double deltaPitch = (event.getTo().getPitch() - event.getFrom().getPitch());
        double deltaYaw = (event.getTo().getYaw() - event.getFrom().getYaw());

        if (recentDeltaYaw.containsKey(player.getName())) {
            recentDeltaYaw.remove(player.getName());
            recentDeltaYaw.put(player.getName(), deltaYaw);
        } else {
            recentDeltaYaw.put(player.getName(), deltaYaw);
        }

        if (recentDeltaPitch.containsKey(player.getName())) {
            recentDeltaPitch.remove(player.getName());
            recentDeltaPitch.put(player.getName(), deltaPitch);
        } else {
            recentDeltaPitch.put(player.getName(), deltaPitch);
        }




        if (player.isOnGround()) {
            AirTimeInTicks.put(player.getName(), (double) (Bukkit.getServer().getWorld("world").getFullTime()));
        }
        double AirTimeCalc = (((double) (Bukkit.getServer().getWorld("world").getFullTime())) - AirTimeInTicks.computeIfAbsent(player.getName(), k -> (double) (Bukkit.getServer().getWorld("world").getFullTime())));

        double totalRotationChange = Math.abs(recentDeltaPitch.get(player.getName())) + Math.abs(recentDeltaYaw.get(player.getName()));
        if (PlayerDeltaAimList.containsKey(player.getName())) {
            if (totalRotationChange != 0) {
                List<Double> deltaList = PlayerDeltaAimList.get(player.getName());
                deltaList.add(totalRotationChange);
            }
        } else {
            if (totalRotationChange != 0) {
                List<Double> deltaList = new ArrayList<>();
                deltaList.add(totalRotationChange);
                PlayerDeltaAimList.put(player.getName(), deltaList);
            }
        }
        //player.sendMessage("DeltaVelocityY: " + (event.getPlayer().getVelocity().getY() + deltaY));// + ChatColor.RED+" neg: " + (event.getPlayer().getVelocity().getY() - deltaY));

        double deltaZ_old = 0;

        if (previousDeltaZ.containsKey(player.getName())) {
            deltaZ_old = previousDeltaZ.get(player.getName());
            previousDeltaZ.remove(player.getName());
            previousDeltaZ.put(player.getName(), deltaZ);
        } else {
            previousDeltaZ.put(player.getName(), deltaZ);
        }

        double deltaX_old = 0;

        if (previousDeltaX.containsKey(player.getName())) {
            deltaX_old = previousDeltaX.get(player.getName());
            previousDeltaX.remove(player.getName());
            previousDeltaX.put(player.getName(), deltaX);
        } else {
            previousDeltaX.put(player.getName(), deltaX);
        }

        double accelXZ_old = 0;

        if (previousAccelXZ.containsKey(player.getName())) {
            accelXZ_old = previousAccelXZ.get(player.getName());
            previousAccelXZ.remove(player.getName());
            previousAccelXZ.put(player.getName(), deltaX);
        } else {
            previousAccelXZ.put(player.getName(), deltaX);
        }

        if (velocityXZ.containsKey(player.getName())) {
            velocityXZ.remove(player.getName());
            velocityXZ.put(player.getName(), player.getVelocity().getX());
        } else {
            velocityXZ.put(player.getName(), player.getVelocity().getX());
        }

        double deltaY_old = 0;

        if (previousDeltaY.containsKey(player.getName())) {
            deltaY_old = previousDeltaY.get(player.getName());
            previousDeltaY.remove(player.getName());
            previousDeltaY.put(player.getName(), deltaY);
        } else {
            previousDeltaY.put(player.getName(), deltaY);
        }

        double prvsVelocityY = player.getVelocity().getY();

        if (previousVelocityY.containsKey(player.getName())) {
            prvsVelocityY = previousVelocityY.get(player.getName());
            previousVelocityY.remove(player.getName());
            previousVelocityY.put(player.getName(), player.getVelocity().getY());
        } else {
            previousVelocityY.put(player.getName(), player.getVelocity().getY());
        }

        isBridging.computeIfAbsent(player.getName(), k -> (double) (Bukkit.getServer().getWorld("world").getFullTime()));
        Double bridgingTimeSince = (Bukkit.getServer().getWorld("world").getFullTime() - isBridging.get(player.getName()));

        List<String> brokeyblocks_towalkon = Arrays.asList("STEP", "STAIRS", "FENCE", "SKULL", "TRAP_DOOR", "BED", "SOUL_SAND", "SLAB");


        double deltaXZ_old = Math.hypot(deltaX_old, deltaZ_old);

        double playerY = event.getPlayer().getLocation().getY();

        //Float deltaXZ_formatted = Float.parseFloat(formatTriple(deltaXZ));
        Float deltaY_formatted = Float.parseFloat(formatTriple(deltaY));

        String gbup = getBlockUnderneathPlayer(player); // returns name of block under player
        String gbupy2 = getBlockUnderneathPlayery2(player); // returns name of block under player, a little bit lower than the normal version

        Location PreviousOnGroundLocation = previousLocation_onGround.get(player.getName());
        int bnp = getBlockYUnderneathPlayer(player); // returns lowest y value of a block
        int pyv = (int) player.getLocation().getY();
        List<String> list_of_blocks_under_player = getBlocksUnderneathPlayer(player);
        List<String> list_of_blocks_ontop_player = getBlocksOnTopPlayer(player);
        List<String> list_of_blocks_Y3_player = GetBlocksInPlayerY3(player);
        List<String> blocksUnderPlayerLOWER = getBlocksUnderPlayerLOWER(player); // it may cause lag..
        List<String> gbupalnbiot = getBlocksUnderneathPlayerAsLongNoBlockIsOnTop(player);
        int lower_yupvalue = lower_getY3by3BlockUnderplay_max(player);
        int higher_yupvalue = higher_getY3by3BlockUnderplay_max(player);
        Boolean blocknearplayer = isNonAirBlockNearPlayer(player);
//        player.sendMessage("Blocks under: " + gbupalnbiot);
        List<String> gbotpalnbiu = getBlocksOnTopPlayerAsLongNoBlockIsUnder(player);
        List<String> gbunpwtrlava = gbunpwtrlva(player);
        List<String> ftblks = FeetBlocks(player);
        List<String> hdblks = HeadBlocks(player);
        List<String> hdftblks = new ArrayList<>();
        hdftblks.addAll(hdblks);
        hdftblks.addAll(ftblks);

        double accelXZ = Math.abs(deltaXZ - deltaXZ_old);


        if (previousLocation_onGround.containsKey(player.getName()) && (player.isOnGround() || player.isFlying()) && !(isEverythingInListThatString(gbupalnbiot, "AIR"))) {
            previousLocation_onGround.remove(player.getName());
            previousLocation_onGround.put(player.getName(), player.getLocation());
        } else {
            if (!(previousLocation_onGround.containsKey(player.getName()))) {
                previousLocation_onGround.put(player.getName(), player.getLocation());
            }
        }


        if ((containsWord(list_of_blocks_Y3_player, "LAVA") || containsWord(list_of_blocks_Y3_player, "WATER")) && (deltaY < 0.34002)) {
            in_a_liquid=true;
        }

        Location playerLoc1 = player.getLocation().clone().add(0, -1, 0);

// get the block under the player
        Block bloc11k = playerLoc1.getBlock();

// get the coordinates of the block
        int blockX = bloc11k.getX();
        int blockY = bloc11k.getY();
        int blockZ = bloc11k.getZ();

// get the fractional part of the player's position in each axis
        double offsetX = playerLoc1.getX() % 1;
        double offsetZ = playerLoc1.getZ() % 1;

// get the center of each block in the area
        double centerX = blockX + 0.5;
        double centerZ = blockZ + 0.5;

// multiply the fractional part by the sign of the difference between the player's position and the center of the block
        offsetX *= Math.signum(playerLoc1.getX() - centerX);
        offsetZ *= Math.signum(playerLoc1.getZ() - centerZ);
        double oldX = SafeWalkCheckX.computeIfAbsent(player.getName(), k -> 0.0);
        double oldZ = SafeWalkCheckZ.computeIfAbsent(player.getName(), k -> 0.0);
        SafeWalkCheckX.put(player.getName(), Double.valueOf(formatDouble(offsetX)));
        SafeWalkCheckZ.put(player.getName(), Double.valueOf(formatDouble(offsetZ)));


        if (deltaYaw == 0 && deltaPitch > 0 && deltaPitch < 1 && Math.abs(event.getTo().getPitch()) != 90.0f) {
//            MINIMUM_DIVISOR

            double gcdcheckbaritone = gcd(deltaPitch, previous_location.get(player.getName()).getPitch());
//            player.sendMessage("gcd cehckbaritone: " + gcdcheckbaritone);
            if (gcdcheckbaritone < 1 && gcdcheckbaritone > 0.005) {} else {

                identifieralertplayers("Automation(Baritone)", player);
                // need to add a baritone temp ban for 30m 60m whatever gives
                UpdateAlerts(player, +2.0, "safe");
            }
//            double divisorY = gcd(deltaPitch, previous_location.get(player.getName()).getPitch());

        }


//        if (development_mode == true) {
//// get the player's location
//            Location playerLoc = player.getLocation().clone().add(0, -1, 0);
//
//// get the block under the player
//            Block bloc11k = playerLoc.getBlock();
//
//// get the coordinates of the block
//            int blockX = bloc11k.getX();
//            int blockY = bloc11k.getY();
//            int blockZ = bloc11k.getZ();
//
//// get the fractional part of the player's position in each axis
//            double offsetX = playerLoc.getX() % 1;
//            double offsetZ = playerLoc.getZ() % 1;
//
//// get the center of each block in the area
//            double centerX = blockX + 0.5;
//            double centerZ = blockZ + 0.5;
//
//// multiply the fractional part by the sign of the difference between the player's position and the center of the block
//            offsetX *= Math.signum(playerLoc.getX() - centerX);
//            offsetZ *= Math.signum(playerLoc.getZ() - centerZ);
////        if (deltaXZ > 0 && !(player.isSneaking())) {
////            player.sendMessage("offsetX: " + offsetX + "  offsetZ:" + offsetZ);
////
////        } else {
////            // reset the list
////        }
//            Location location = player.getLocation();
//            // get the block under the player
//            Location blockLocation = location.clone().add(0, -1, 0);
//            Material blockMaterial = blockLocation.getBlock().getType();
//
//            // check if the block is solid and not air or water
//            isBridging.computeIfAbsent(player.getName(), k -> (double) (Bukkit.getServer().getWorld("world").getFullTime()));
//            Double bridgingTimeSince = (Bukkit.getServer().getWorld("world").getFullTime() - isBridging.get(player.getName()));
//            if (bridgingTimeSince < 40) {
//                if (blockMaterial != Material.AIR) {
//                    // check if the distance is less than the safe distance in either axis
//
//                } else {
//                    if (deltaXZ > 0 && !(player.isSneaking()) && player.isOnGround()) {
//                        double oldX = SafeWalkCheckX.computeIfAbsent(player.getName(), k -> 0.0);
//                        double oldZ = SafeWalkCheckX.computeIfAbsent(player.getName(), k -> 0.0);
////                    player.sendMessage("offsetX: " + formatDouble(offsetX) + "  offsetZ:" + formatDouble(offsetZ));
//                        if (Double.parseDouble(formatDouble(oldX)) == Double.parseDouble(formatDouble(offsetX)) || Double.parseDouble(formatDouble(oldZ)) == Double.parseDouble(formatDouble(offsetZ))) {
//                            identifieralertplayers("Safewalk(Alpha)", player);
//                        }
//                        SafeWalkCheckX.put(player.getName(), offsetX);
//                        SafeWalkCheckZ.put(player.getName(), offsetZ);
//
////                    SafeWalkCheckA.put(player.getName(), SafeWalkCheckA.get(player.getName()) + 1.0);
////                    if (SafeWalkCheckA.get(player.getName()) > 3) { // tolerance
////                        player.sendMessage("Safwalk check");
//
//                    }
//                }
//
//                if (deltaXZ < 0.15D
//                        && deltaXZ > 0.1D
//                        && deltaXZ_old > 0.15D
//                        && accelXZ < 0.1
//                        && accelXZ > 0.099) {
//                player.sendMessage("safewalk x kauri");
//                }
//            } else {
//            }
//        }

//        double vY = player.getVelocity().getY();
//
//        double pct = deltaY / vY * 100;
//        //player.sendMessage(ChatColor.RED + "vY: "+vY +   "  deltaY: " + deltaY);
//        if (pct < 99.999 || pct > 400) {
//             //   && !data.playerInfo.lastBlockPlace.isNotPassed(5)
//               // && !data.blockInfo.blocksAbove) {
//          //  player.sendMessage("Velocity check ratio is " + pct);
//        }
//
//        vY-= 0.08;
//        vY*= 0.98;

        //player.sendMessage("pct=" + pct + " vY=" + vY);


        //player.sendMessage(ChatColor.GREEN + "DeltaY: " + ChatColor.YELLOW + deltaY);


        if (Flight_H){
            if ((deltaY == 0) && gbupalnbiot.isEmpty() && (!(player.isFlying())) && !((containsWord(list_of_blocks_Y3_player, "WATER")) || (containsWord(list_of_blocks_Y3_player, "LAVA")) || (containsWord(list_of_blocks_Y3_player, "LADDER")) || (containsWord(hdftblks, "WEB")))) {
                if (ShouldBeFalling.containsKey(player.getName())) {
                    if (ShouldBeFalling.get(player.getName())) {
                        if (AlertBukkitChannel) {
                            identifieralertplayers("Flight H", player);
                            UpdateAlerts(player, +1.0, "safe");
                        }
                        modificationUsage = true;
                        if (cancelorsetback == true) {
                            player.teleport(previousLocation_onGround.get(player.getName()));
                        }
                        //player.sendMessage(ChatColor.RED + "You should be falling but you aren't.");
                        //event.setCancelled(true);
                        //player.teleport(previousLocation_onGround.get(player.getName()));

                    } else {
                        ShouldBeFalling.put(player.getName(), true);
                    }
                } else {
                    ShouldBeFalling.put(player.getName(), true);
                }

            } else {
                ShouldBeFalling.remove(player.getName());
            }
        }
        if (Flight_G) {
            if ((new Double(formatTriple(deltaY)) > -0.1) && (!(deltaY >= 0)) && (playerY - lower_yupvalue > 1.02) && (player.getGameMode() == SURVIVAL || player.getGameMode() == ADVENTURE) && (!((containsWord(list_of_blocks_Y3_player, "LAVA") || containsWord(list_of_blocks_Y3_player, "WATER")) && (deltaY < 0.34002))) && (!(containsWord(list_of_blocks_Y3_player, "WEB")) && (deltaY < -0.07)) && (!(containsWord(list_of_blocks_Y3_player, "PISTON")))) { // make a check if 2 in a row of these packets.
                if (FallingSlowly.containsKey(player.getName())) {
                    if (FallingSlowly.get(player.getName())) {
                        if (AlertBukkitChannel) {
                            identifieralertplayers("Flight G(un-stable-ish)", player);
                            UpdateAlerts(player, +1.0, "unsafe");
                            if ((playerY - bnp) < 1) {
                                player.sendMessage(ChatColor.AQUA + "You were stuck!");
                                player.teleport(player.getLocation().clone().add(0, 0.5f, 0));
//                                updateMovementAlerts(player, (double) +10);
                            }
                        }
                        modificationUsage = true;
                    } else {
                        FallingSlowly.put(player.getName(), true);
                    }
                } else {
                    FallingSlowly.put(player.getName(), true);
                }
            } else {
                FallingSlowly.remove(player.getName());
            }
        }
        //Float tmp = (float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp));

        //player.sendMessage(ChatColor.GREEN + "Velocity: " + player.getVelocity().getY());
        //player.sendMessage(ChatColor.DARK_AQUA + "DeltaY: " + deltaY); // + " Y to fall: " + (playerY - bnp - 2) + " BlockUnderneath: " + gbup);

        if (Flight_F){
            if (deltaY > -0.07 && deltaY < -0.01 && deltaY_old > -0.07 && deltaY_old < -0.01 && (!(containsWord(hdftblks, "WEB"))) && (!(containsWord(hdftblks, "WATER"))) && (!(containsWord(hdftblks, "LAVA")))) {
                if (AlertBukkitChannel) {
                    identifieralertplayers("Flight F", player);
                    UpdateAlerts(player, +1.0, "safe");
                }
                modificationUsage = true;
            }
        }

        if (Flight_E) {
            if ((!(deltaY == 0)) && (deltaY > 1000 || (deltaY > -0.001 && deltaY < 0))) {
                //player.sendMessage(ChatColor.RED + "DeltaY is too large; Y velocity: " + player.getVelocity().getY());
                if (AlertBukkitChannel) {
                    identifieralertplayers("Flight E", player);
                    UpdateAlerts(player, +1.0, "safe");
                }
                modificationUsage = true;
            }
        }
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
        // TODO: You need to make the slime block math, and also, - DONE SLIME_BLOCK
        //  whenever a player is jumping and landing exactly on the side top of a block it falses and say: deltaY: 0.5952911683122091.


        if (Jesus_II){
            if ((isEverythingInListThatString(gbunpwtrlava, "WATER") || isEverythingInListThatString(gbunpwtrlava, "LAVA")) && (!(gbunpwtrlava.isEmpty()))) {
                if ((playerY - bnp) > 0.75) {
                    if (deltaY >= 0 && player.getVelocity().getY() < 0 && !landedonslime) {
                        //event.setCancelled(true);
                        if (development_mode) {
                            identifieralertplayers("Jesus check 2 (beta)", player);// + player.getVelocity().getY());
                            UpdateAlerts(player, +1.0, "safe"); // just fix the jump thing safe with the other check
                        }
                    }
                }
            }
        }
//        player.sendMessage(playerY - higher_yupvalue + "");

        if (((deltaY < 0) && (previousHeight.containsKey(player.getName())))) {
            //player.sendMessage("Falling..");
            if (Tumbling) {
                if ((String.valueOf(formatQuad(deltaY))).equals(String.valueOf(formatQuad((deltaY_old - 0.08D) * 0.9800000190734863D)))) {
                    FallingWeirdly.remove(player.getName());
                } else {
                    if ((playerY - higher_yupvalue <= 1.0) || ((player.getNoDamageTicks() <= 14) && (7 <= player.getNoDamageTicks())) || (!(isEverythingInListThatString(list_of_blocks_ontop_player, "AIR"))) || (containsWord(Collections.singletonList(gbup), "WATER")) || (containsWord(Collections.singletonList(gbup), "LAVA")) || (containsWord(Collections.singletonList(gbupy2), "SLIME")) || (containsWord(Collections.singletonList(gbup), "SLIME")) || ((containsWord(hdftblks, "WEB"))) && (deltaY < -0.07) || (containsWord(list_of_blocks_Y3_player, "PISTON"))) {
                        // if any of the above exists, it ignores(in the "OR")
                    } else {
                        if (FallingWeirdly.containsKey(player.getName())) {
                            if (FallingWeirdly.get(player.getName())) {
                                //player.sendMessage(ChatColor.RED + "Falling speed adjusted by a client modification");
                                if (AlertBukkitChannel) {
                                    identifieralertplayers("Tumbling", player);
                                    UpdateAlerts(player, +0.5, "safe");
                                }
                                modificationUsage = true;

                            } else {
                                FallingWeirdly.put(player.getName(), true);
                            }
                        } else {
                            FallingWeirdly.put(player.getName(), true);
                        }
                    }
                }
            }
        } else if (previousHeight.containsKey(player.getName()) && containsWord(blocksUnderPlayerLOWER, "SLIME")) {
            wasOnSlimeBlock.put(player.getName(), true);
            slimeHeight.put(player.getName(), playerY - 1);
//            player.sendMessage("LAnded on slime");
            landedonslime = true;
        } else if ((!(isEverythingInListThatString(blocksUnderPlayerLOWER, "AIR")))) {
            previousHeight.remove(player.getName());
        }
        //player.sendMessage(ChatColor.RED + "" + previousHeight.containsKey(player.getName()) + slimeHeight.containsKey(player.getName()) + wasOnSlimeBlock.containsKey(player.getName()));
        if ((player.getNoDamageTicks() <= 20) && (10 <= player.getNoDamageTicks()) && isEverythingInListThatString(list_of_blocks_ontop_player, "AIR")) {
            if (player.getLastDamageCause().getCause().name().equalsIgnoreCase("ENTITY_ATTACK"))  {
                if (deltaY == 0) {
                    updateVelYVerbose(player, 1.0);
                    if (noVelYVerbose.get(player.getName()) > 5) {
                        player.sendMessage("No velocity taken, Y, cause: " + player.getLastDamageCause().getCause().name());
                    }
                } else {
//                player.sendMessage("dlt " + deltaY);
                    noVelYVerbose.remove(player.getName());
                }

//                player.setVelocity(new Vector(0, 0.5, 0));
            }
        }
//        if (MaxSetKnockbackheight.containsValue(player.getName())) {
//            player.sendMessage("temporarily ignoring motionY checks");
//            if (playerY > MaxSetKnockbackheight.get(player.getName())) {
//                player.sendMessage("Hacking motion height wrong!");
//            }
//            if (deltaY <= 0) {
//                MaxSetKnockbackheight.remove(player.getName());
//            }
//        } else
        if (deltaY > 0 && wasOnSlimeBlock.get(player.getName())) {
            double previousYValue = previousHeight.get(player.getName());
            double slimeBlockY = slimeHeight.get(player.getName());
            double subtractedYValue = (previousYValue - slimeBlockY);
            double maxHeight = predictedJumpHeight(subtractedYValue) + slimeBlockY + 2; // slime block getY is as a base height, the +2 is in case of an error
            //player.sendMessage(" previousYValue: " + previousYValue + " slimeBlockY: " + slimeBlockY + " maxHeight: " + maxHeight);
            //player.sendMessage(ChatColor.GREEN + "expectedDeltaY: " + expectedDeltaY + " maxHeight: " + maxHeight + " playerY: " + playerY);
            //player.sendMessage(ChatColor.GREEN + "expectedDeltaY: " + expectedDeltaY + " deltay: " + deltaY);
            if (expectedDeltaY != 0.0) {
                double as = formatQuad(2.2492 + (0.6568822107 * getPotionModifier(player, PotionEffectType.JUMP)));
                //player.sendMessage(as + " dela: " + (slimeBlockY - playerY));
                if (Constant_B2){
                    if (expectedDeltaY == (new Float(deltaY))) {
                        //player.sendMessage("cannot remain at the same delta all the time, hacking");
                        if (AlertBukkitChannel) {
                            identifieralertplayers("Constant B2", player);
                            UpdateAlerts(player, +1.0, "safe");
                        }
                    }
                    modificationUsage = true;
                } else if ((deltaY > 0)) {
                    if (Slime_C){
                        if ((playerY > (maxHeight + 1)) || (((formatQuad(slimeBlockY - playerY)) >= (formatQuad(2.2492 + (0.6568822107 * getPotionModifier(player, PotionEffectType.JUMP))))) && deltaY > 0)) {
                            //player.sendMessage(ChatColor.GREEN + "You've surpassed the max height from the slime block " + maxHeight);
                            if (AlertBukkitChannel) {
                                identifieralertplayers("Slime C", player);
                                UpdateAlerts(player, +1.0, "safe");
                            }
                            modificationUsage = true;
                        }
                    }
                }
            } else {
                trackJump(String.valueOf(player), new Float(deltaY)); // isn't actual for jump; for the expecteddeltaY, this is
            }
        } else if (deltaY < 0 && wasOnSlimeBlock.get(player.getName())) {
            wasOnSlimeBlock.put(player.getName(), false);
            previousHeight.remove(player.getName());
            previousHeight.put(player.getName(), playerY);
            slimeHeight.remove(player.getName());
            expectedDeltaY = 0;

        } else if (!(expectedDeltaY == (0))) {
//            player.sendMessage(gbotpalnbiu + " blocks above");
            if ((!(isEverythingInListThatString(gbotpalnbiu, "AIR")))) {
                // player jumped and a block is on top of them
            } else if ((!(expectedDeltaY == (deltaY_formatted_double))) && (!((deltaY == 0.5 && (containsWord(list_of_blocks_under_player, "STEP")) || (containsWord(list_of_blocks_under_player, "STAIRS")) || (containsWord(list_of_blocks_under_player, "FENCE")) || (containsWord(list_of_blocks_under_player, "SKULL"))) || ((deltaY == 0.75) && (containsWord(list_of_blocks_under_player, "SKULL"))) || ((deltaY == 0.1875) && (containsWord(list_of_blocks_under_player, "TRAP_DOOR"))) || ((deltaY_formatted_double == 0.118) && (containsWord(list_of_blocks_under_player, "LADDER"))) || ((deltaY == 0.3125) && (containsWord(list_of_blocks_under_player, "STEP")) || (containsWord(list_of_blocks_under_player, "TRAP_DOOR"))) || ((deltaY == 0.125) && (containsWord(list_of_blocks_under_player, "REDSTONE"))) || ((deltaY == 0.5625) && (containsWord(ftblks, "BED"))))) || ((deltaY == 0.078) && (containsWord(list_of_blocks_under_player, "SOUL_SAND")))) {
                if (deltaY == 0) {
                    return; // a false!; player isn't moving in the Y value
                }
                if (jumpboost_amplifier > 0) { // let the other check do its thing
                    // jump boost casues falses. ignore
                    //player.sendMessage("Jump boost effected player");
                } else if ((containsWord(Collections.singletonList(gbup), "STEP") || containsWord(Collections.singletonList(gbup), "SLAB") || containsWord(Collections.singletonList(gbupy2), "STEP") || containsWord(Collections.singletonList(gbupy2), "SLAB")) && (deltaY == 0.28000006079673767)) {
                    // above is a slab false fix, if a player is standing atop slab, it will false with that exact deltaY value
                } else {
                    if (Jump_A) {
                        if ((player.getNoDamageTicks() <= 20) && (12 <= player.getNoDamageTicks())) { // (deltaY == 0.5 || deltaY == 0.3249680141754183 || (deltaY < 0)) ||
//                             can cause bypasses!
                        } else
                            if (AlertBukkitChannel) {
                            identifieralertplayers("Jump A", player);
                                UpdateAlerts(player, +0.5, "safe");
                        }
                        modificationUsage = true;
                    }
                }
                //player.sendMessage(ChatColor.DARK_AQUA + "DeltaY: " + deltaY);
                //player.sendMessage(deltaY_formatted_double + " <- You're jumping weirdly.. Step? Bhop? Expected: " + expectedDeltaY);
                // 0.420, 0.333, 0.248, 0.165, 0.83 check.
            } else if (expectedDeltaY == (0.333 + (0.10000000149 * jumpboost_amplifier))) {
//                    if ((float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp)) != 0.42) {
//                        player.sendMessage("Weird jumping!");
//                    }
                player_jumped = true;
                trackJump(String.valueOf(player), new Float(0.248));
            } else if (expectedDeltaY == (0.248)) {
//                    if ((float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp)) != 0.753) {
//                        player.sendMessage("Weird jumping!");
//                    }
                player_jumped = true;

                trackJump(String.valueOf(player), new Float(0.165));
            } else if (expectedDeltaY == (0.165)) {
//                    if ((float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp)) != 1.001f) {
//                        player.sendMessage("Weird jumping!");
//                    }
                player_jumped = true;

                trackJump(String.valueOf(player), new Float(0.083));
            } else if (expectedDeltaY == (0.083)) {
//                    if ((float) Float.parseFloat(formatTriple((player.getLocation().getY() - 1) - bnp)) != 1.166) {
//                        player.sendMessage("Weird jumping!");
//                    }
                //player.sendMessage("Jumped normally");
                player_jumped = true;

                trackJump(String.valueOf(player), new Float(0.0));
            }
        } else if (player.isFlying() || (player.getGameMode() == GameMode.CREATIVE)) { // also you should make a record to how many changes there is to the person when they jump, so 10 alerts?
            //player.sendMessage("flying");
            // check for is swming? does it mean if player one block in water or need to check, slime changes speed, and also cobweb etc.
        } else if (deltaY == 0.0) {
            // isn't jumping or doing anything, ignore
            //} else if (!(isEverythingInListThatString(list_of_blocks_ontop_player, "AIR"))) {
            //    player.sendMessage(ChatColor.YELLOW + " akjsnfasfd");
        } else if (deltaY < 0) {
            if (!(previousHeight.containsKey(player.getName()))) {
                previousHeight.put(player.getName(), playerY);
                //player.sendMessage("Placed playerY in hash." + deltaY);
            }
            // entity is falling, you can make a check but if it's too fast, like make a check whether it's a 1 block distance and then check if the speed is one of these: -0.078 -0.155 -0.231 -0.377.
        } else if ((deltaY <= (0.42) && (deltaY >= (0.4)) && (!(isEverythingInListThatString(list_of_blocks_under_player, "AIR"))))) { // checking that the player hadn't jumped over 1 block, cause it isn't possible
//            player.sendMessage("You jumped! " + deltaY);
            trackJump(String.valueOf(player), (new Float(0.333)));


            player_jumped = true;
            if (Jesus_I){
                if (containsWord(Collections.singletonList(gbup), "WATER") || containsWord(Collections.singletonList(gbup), "LAVA")) {
                    if (development_mode) {
                        identifieralertplayers("Jesus check (beta)", player);
                        UpdateAlerts(player, +0.5, "safe");
                    }
                }
            }
        } else if ((deltaY == 0.5 && (containsWord(list_of_blocks_under_player, "STEP")) || (containsWord(list_of_blocks_under_player, "STAIRS")) || (containsWord(list_of_blocks_under_player, "FENCE")) || (containsWord(list_of_blocks_under_player, "SKULL"))) || ((deltaY == 0.75) && (containsWord(list_of_blocks_under_player, "SKULL"))) || ((deltaY == 0.1875) && (containsWord(list_of_blocks_under_player, "TRAP_DOOR"))) || ((deltaY_formatted_double == 0.118) && (containsWord(list_of_blocks_under_player, "LADDER"))) || ((deltaY == 0.3125) && (containsWord(list_of_blocks_under_player, "STEP")) || (containsWord(list_of_blocks_under_player, "TRAP_DOOR"))) || ((deltaY == 0.125) && (containsWord(list_of_blocks_under_player, "REDSTONE"))) || ((deltaY == 0.5625) && (containsWord(ftblks, "BED"))) || ((deltaY == 0.078) && (containsWord(list_of_blocks_under_player, "SOUL_SAND")))) {
            // walking on weird objects
//            player.sendMessage("Walking on off objects");
            walkingonoffobjects = true;
        } else if ((containsWord(list_of_blocks_Y3_player, "LAVA") || containsWord(list_of_blocks_Y3_player, "WATER")) && (deltaY < 0.34002)) {
//            player.sendMessage("In a liquid.: " + list_of_blocks_Y3_player);
            in_a_liquid = true;
        } else if (deltaY == deltaY_old && deltaY != 0) {
            if (Constant_B1){//player.sendMessage(ChatColor.DARK_BLUE + "DeltaY is the same, cannot be so.");
                if (AlertBukkitChannel) {
                    identifieralertplayers("Constant B1", player);
                    UpdateAlerts(player, +1.0, "safe");
                }
            }
            modificationUsage = true;
        } else if (((((formatQuad(playerY - PreviousOnGroundLocation.getY())) >= (formatQuad(2.25 + 2 * (0.75 * getPotionModifier(player, PotionEffectType.JUMP))))) && deltaY > 0)) && (((player.getNoDamageTicks() <= 20) && (7 <= player.getNoDamageTicks())) && (playerY - PreviousOnGroundLocation.getY()) > 2.9)) {
            // since you ain't falling you can't use the previousLoc Y value.
            if (Height_A2){
                if (player.getVelocity().getY() > 0.08) {
//                    player.sendMessage(ChatColor.GREEN + "You should be fine, max set height is: " + (player.getVelocity().getY() + playerY));
                } else {
                    //player.sendMessage(ChatColor.RED + "Too high!" + (playerY - PreviousOnGroundLocation.getY()) + "  " + (formatQuad(2.25 + 2 * (0.75 * getPotionModifier(player, PotionEffectType.JUMP)))));
//                player.sendMessage(ChatColor.GREEN + " A:" + (playerY - PreviousOnGroundLocation.getY()));
                    if (AlertBukkitChannel) {
                        identifieralertplayers("Height A2", player);
                        UpdateAlerts(player, +1.0, "unsafe");
                    }
                    modificationUsage = true;
                }
            }
            //zevent.setCancelled(true);
            //player.sendMessage(formatQuad(playerY - bnp) + "Hacking, Flying possibly. " + formatQuad(2.1661092609382139 + (0.6568822107 * getPotionModifier(player, PotionEffectType.JUMP))) + " is equal?: "
            //       + ((formatQuad(playerY - bnp)) <= (formatQuad(2.1661092609382139 + (0.6568822107 * getPotionModifier(player, PotionEffectType.JUMP)))))); // if on a side block it can cause false; and that block is high than the rest.
        } else {


            Location playerLocation = player.getLocation();
            Vector playerDirection = playerLocation.getDirection();

            // Normalize the direction vector.
            playerDirection.normalize();

            // Multiply the direction vector by 1 block.
            playerDirection.multiply(1);
            Vector loc_up1 = playerDirection.multiply(2);
            Vector loc_up2 = playerDirection.multiply(3);

            // Get the block in front of the player.

            String blockInFront = playerLocation.add(playerDirection).getBlock().getType().name();
            String blockInFront1 = playerLocation.add(loc_up1).getBlock().getType().name();
            String blockInFront2 = playerLocation.add(loc_up2).getBlock().getType().name();

            if (formatQuad(deltaY) == 0.200 && (!(isEverythingInListThatString(gbotpalnbiu, "AiR")))) {
                // Block on top
                //player.sendMessage("Block on top");
            } else if (blockInFront != "AIR" && blockInFront1 == "AIR" && blockInFront2 == "AIR" && (deltaY == 0.5926045976350593)) {
                /// coast is clear
            } else {
                if (player.getVelocity().getY() > 0.08) {
                    //MaxSetKnockbackheight.put(player.getName(), (player.getVelocity().getY() + playerY));

                    // player.sendMessage(ChatColor.GREEN + "You should be fine, max height set is: " + (player.getVelocity().getY() + playerY));
                } else {
                    if (getPotionModifier(player, PotionEffectType.JUMP) > 0) {
                        if (Height_A1){
                            if ((((formatQuad(playerY - PreviousOnGroundLocation.getY())) >= (formatQuad(2.25 + 2 * (0.75 * getPotionModifier(player, PotionEffectType.JUMP))))) && deltaY > 0)) {
                                // since you ain't falling you can't use the previousLoc Y value.
                                if (player.getVelocity().getY() > 0.08) {
                                    // MaxSetKnockbackheight.put(player.getName(), (player.getVelocity().getY() + playerY));
//                                player.sendMessage(ChatColor.GREEN + "You should be fine, max height set is: " + (player.getVelocity().getY() + playerY));
                                } else {
                                    //player.sendMessage(ChatColor.RED + "Too high!" + (playerY - PreviousOnGroundLocation.getY()) + "  " + (formatQuad(2.25 + 2 * (0.75 * getPotionModifier(player, PotionEffectType.JUMP)))));
                                    if (AlertBukkitChannel) {
                                        identifieralertplayers("Height A1", player);
                                        UpdateAlerts(player, +1.0, "unsafe");
                                    }
                                    modificationUsage = true;
                                }
                            }
                        }
                    } else {
                        if ((player.getNoDamageTicks() <= 20) && (5 <= player.getNoDamageTicks())) {
                            double velocityY = player.getVelocity().getY();
                            if (player.isOnGround() && (velocityY == -0.0784000015258789)) {
                                if (deltaY != 0.46074999999999733 && deltaY != 0.360750000000003 && deltaY != 0.32237500000000097 && deltaY != 0.3607499999999959 && deltaY != 0.46075000000000443 && deltaY != 0.46075000000000443) {
//                                    identifieralertplayers("Velocity type Y", player);
//                                    player.sendMessage(velocityY + "Velocity <   " + deltaY);
                                    // code something that uses a formula like the ration of the deltas to this, as it's unstable; too many different values to maintain
                                    modificationUsage = true;
                                }
                            }
                            //player.setLastDamageCause();
                            //player.sendMessage(ChatColor.DARK_GRAY + "You were attacked. it's fine, " + player.getNoDamageTicks());
                            // calculate velocity by player
                        } else { // Make it so that if the below is triggered and then the above, ignore, but if only the below is triggered, then alert.
//                            getServer().dispatchCommand(getServer().getConsoleSender(), "teleport " + player.getName() + " " + player.getName());
                            if (Motion_Y){
                                if (AlertBukkitChannel) {
                                    identifieralertplayers("Motion Y(" + deltaY_formatted + ")", player);
                                    UpdateAlerts(player, +1.0, "unsafe");
                                }
                                modificationUsage = true;
                            }
//                            if (player.getVelocity().getY() > 0.1) {
//                                double pct = (previousDeltaY.get(player.getName()) / player.getVelocity().getY()) * 100;
//                                if (pct < 99.999 || pct > 400) {
//                                    player.sendMessage("Vertical velocity modification");
//                                }
//                                Bukkit.broadcastMessage(" Y: " + (previousDeltaY.get(player.getName()) / player.getVelocity().getY()));
//
//                            }
                            //player.sendMessage(ChatColor.RED + "Hacking.  dmgTicks:" + player.getNoDamageTicks() + " " + deltaY + " " + deltaY_old);
//                            event.setCancelled(true);
                        }
                    }
                }
                //event.setCancelled(true);
                //player.sendMessage("Player is on untreated deltas. DeltaY: " + deltaY);
                // here handle the rest of the falses and the alert otherwise cause they ARE CHEATING
                //player.sendMessage(deltaY + "<- deltaY, Unaccounted modification." + blockInFront + blockInFront1 + blockInFront2);
                //player.sendMessage("Block under player : " + gbup);
                //player.sendMessage("Hacking. " + deltaY);
            }

            // CHECK IF THERE BLOCK AIR ON TOP AND ON TOP OF THAT AS WELL

            // note that the slime handling should be exactly before this
            // here is where you flag.. but i don't think you should as some falses stil exist.
            //player.sendMessage(list_of_blocks_under_player + "");
            //player.sendMessage((previousHeight.containsKey(player.getName()) + " <- value previousheight: contains: " +containsWord(blocksUnderPlayerLOWER, "SLIME")));
            //player.sendMessage(ChatColor.DARK_AQUA + "DeltaY: " + deltaY);
        }

//        double prediction = (OldDeltaY - 0.8) * 0.9800000190734863;
//        if (!(player.isOnGround())) {
//            if (!(deltaY - prediction < 1E-13) && OldDeltaY > 0 && deltaY != 0) {
//                player.sendMessage("Going up too fast");
//            }
//        }
        //player.sendMessage(ChatColor.DARK_AQUA + "Current DeltaY: " + deltaY_formatted + " Previous deltaY: " + OldDeltaY + ChatColor.RED +" prediction: " + prediction );;
        if (Movement_I){
            if (LastTimeSinceTeleport_calc > 4 && (playerY - lower_yupvalue <= 1) && (!(player.isOnGround())) && (!(isEverythingInListThatString(gbupalnbiot, "AIR"))) && (!(containsWord(Collections.singletonList(gbup), "SLIME"))) && (!(containsWord(hdftblks, "WEB"))) && !(player_jumped && !(isEverythingInListThatString(list_of_blocks_ontop_player, "AIR")))) {
                if (deltaY < deltaY_old && walkingonoffobjects == true) {
                } else { // is falling so ignore
                    //player.sendMessage(ChatColor.DARK_AQUA + "Spoofing not being on ground");
                    if (AlertBukkitChannel) {
//                    player.teleport(findEmptyPlace(player, 0));
                        identifieralertplayers("Movement 1 ", player);
                        UpdateAlerts(player, +0.5, "safe");
//                player.sendMessage("Did you jump?: " + player_jumped);
                    }
                    modificationUsage = true;
                } // the stair check is due the fact that minecraft says it's not onground whilst deltaY being a 0. falsing both checks.
            }
        }
//        player.sendMessage("isBlockunder:" + (containsAnyWordOfTheWordsInTwoLists(list_of_blocks_under_player, brokeyblocks_towalkon)));
//        player.sendMessage("blocksunderplayer: " + list_of_blocks_under_player);
//        player.sendMessage("SA: " + (playerY-yupvalue));
        if (Movement_II){
            if ((player.isOnGround()) && playerY - higher_yupvalue > 1.02) {
//            player.sendMessage(containsAnyWordOfTheWordsInTwoLists(list_of_blocks_under_player, brokeyblocks_towalkon) + "  : " + (playerY - lower_yupvalue) + "");
                if ((containsAnyWordOfTheWordsInTwoLists(list_of_blocks_under_player, brokeyblocks_towalkon)) && playerY - lower_yupvalue <= 1.875) {
                } else {

                    if (AlertBukkitChannel) {
                        identifieralertplayers("Movement 2", player);
                        UpdateAlerts(player, +0.4, "safe");
                        sendBlockChangeArea(player);
                        if ((player.getNoDamageTicks() <= 20) && (7 <= player.getNoDamageTicks())) {

                        } else {
                            player.teleport(findEmptyPlace(player, 0).add(0, 0.4, 0));
//                            player.teleport(previousLocation_onGround.get(player.getName()));
//                        event.setCancelled(true);
//                        player.teleport(previousLocation_onGround.get(player.getName()));
                        }
                    }
                    modificationUsage = true;
                    //player.sendMessage((ChatColor.RED + "Spoofing on ground value"));
                }
            }
        }

        if (GhostBlockFix_8by8Intense) {
            if (player.isOnGround() && (((playerY - 1) - bnp) >= 0.2)) {
                sendBlockChangeArea(player);
            }
        }


        double numalerts10 = totalAlertsinlast10seconds.computeIfAbsent(player.getName(), k -> (double) 0);
        if (numalerts10 > 10 && modificationUsage) {
            event.setCancelled(true);
            Location loctp = previousLocation_onGround.get(player.getName()).clone().add(0, 0, 0);
            player.teleport(loctp);
            //player.sendTitle(ChatColor.DARK_RED+"Do not move", ChatColor.RED+"It'll mess things up");
            totalAlertsinlast10seconds.put(player.getName(), 5.0);
//            updateMovementAlerts(player, (double) +5);
        } else if (modificationUsage) {
//            updateMovementAlerts(player, (double) +3);
        }

        //player.sendMessage("" + player.getEyeLocation().getDirection());
        //player.sendMessage(ChatColor.DARK_AQUA + "DeltaX: " + ChatColor.RESET + deltaX);
        //player.sendMessage(ChatColor.AQUA + "DeltaZ: " + ChatColor.RESET + deltaZ);
        double PlayerX = player.getEyeLocation().getX();
        double PlayerZ = player.getEyeLocation().getZ();

        double velocityX = player.getVelocity().getX();
        double velocityY = player.getVelocity().getY();
        double velocityZ = player.getVelocity().getZ();

        // player.sendMessage(ChatColor.DARK_AQUA + "VelocityX " + velocityX);

        if (playerXZ.containsKey(player.getName())) {
            Pair<Double, Double> playersLocation_old = playerXZ.get(player.getName());
            double PlayerX_old = playersLocation_old.getLeft();
            double PlayerZ_old = playersLocation_old.getRight();
            playerXZ.remove(player.getName());
            playerXZ.put(player.getName(), Pair.of(PlayerX, PlayerZ));
            // now you can continue with the math.
            double deltaX_loc = PlayerX - PlayerX_old;
            double deltaZ_loc = PlayerZ - PlayerZ_old;
            // player.sendMessage(((abs(deltaX_loc)) + (abs(deltaZ_loc))) + " :Delta");
        } else {
            playerXZ.put(player.getName(), Pair.of(PlayerX, PlayerZ));
        }
        double deltaXZ_quad = formatQuad(deltaXZ);

        //player.sendMessage(ChatColor.DARK_AQUA + "DeltaXZ " + deltaXZ_quad + "  DeltaX: " + formatQuad(deltaX) + " DeltaZ: " +formatQuad(deltaZ));

        Location from = event.getFrom();
        Location to = event.getTo();
        float yaw = from.getYaw();
        double dx = to.getX() - from.getX();
        double dz = to.getZ() - from.getZ();
        double angle = Math.atan2(dz, dx);
        double degree = Math.toDegrees(angle);
        double yawDiff = yaw - degree;

        List<String> pressedKeys = new ArrayList<>();

        if (yawDiff <= 0) {
            yawDiff += 360;
        }
        if (yawDiff < -360) {
//            player.sendMessage(ChatColor.DARK_AQUA+"yawDiff: " + yawDiff);
        } else {
//            player.sendMessage("yawDiff: " + yawDiff);
            if ((isWithinTenPercent(-90, yawDiff))) {
//                    player.sendMessage("W");
                pressedKeys.add("W");
            }
            if ((isWithinTenPercent(90, yawDiff))) {
//                player.sendMessage("S");
                pressedKeys.add("S");

            }
            if ((isWithinTenPercent(270, yawDiff))) {
//                player.sendMessage("W");
                pressedKeys.add("W");

            }
            if ((isWithinTenPercent(360, yawDiff)) || (isWithinTenPercent(1, yawDiff))) {
//                player.sendMessage("A");
                pressedKeys.add("A");
            } else if ((isWithinTenPercent(180, yawDiff))) {
//                player.sendMessage("D");
                pressedKeys.add("D");
            }
            if ((isWithinTenPercent(315, yawDiff) || isWithinTenPercent(-45, yawDiff))) {
//                player.sendMessage("W+A");
                pressedKeys.add("W");
                pressedKeys.add("A");
            }
            if ((isWithinTenPercent(225, yawDiff) || isWithinTenPercent(-135, yawDiff))) {
//                player.sendMessage("W+D");
                pressedKeys.add("W");
                pressedKeys.add("D");
            }

            if ((isWithinTenPercent(135, yawDiff))) {
//                player.sendMessage("S+D");
                pressedKeys.add("S");
                pressedKeys.add("D");
            }
            if ((isWithinTenPercent(45, yawDiff))) {
//                player.sendMessage("S+A");
                pressedKeys.add("S");
                pressedKeys.add("A");
            }
            if ((isWithinTenPercent(405, yawDiff))) {
//                player.sendMessage("S+A");
                pressedKeys.add("S");
                pressedKeys.add("A");
            }
        }

        if (keys_pressed.containsKey(player.getName())) {
            keys_pressed.remove(player.getName());
            keys_pressed.put(player.getName(), pressedKeys);
        } else {
            keys_pressed.put(player.getName(), pressedKeys);
        }
        // these are SET values, subtract these from the BASE - forward no sprint
        double baseSpeed = 0.2159;
        double ifwassprinting_onground = 0.2159+0.065;

        if (OmniSprint) {
            if (!((player.getNoDamageTicks() <= 20) && (7 <= player.getNoDamageTicks()))) {
                if (!(pressedKeys.contains("W")) && player.isSprinting() && !(pressedKeys.isEmpty())) {
                    updateOmniSprintVerbose(player, +1.0);
                    if ((OmniSprintCheck.get(player.getName())) > 9) {
                        identifieralertplayers("OmniSprint", player);
                        UpdateAlerts(player, +1.0, "safe");
                        OmniSprintCheck.put(player.getName(), 5.0);
                    }
                } else {
                    OmniSprintCheck.remove(player.getName());
                }
            }
        }

        // double speedCalculated = 0; // basically here you change the speed, if speed potion add it here, if sneak change here if sprint change here; the value

        if (player.isSprinting()) {
            baseSpeed += 0.065;
        }

        if (player.isSneaking()) { // here add this, but at the end of the check,
            baseSpeed *= 0.3;
        }
        if (pressedKeys.contains("W") && (pressedKeys.contains("D") || pressedKeys.contains("A"))) {
            if (player.isSneaking()) {
                baseSpeed *= 1.413946;
                ifwassprinting_onground *= 1.413946;
            } else {
                baseSpeed *= 1.0203136137;
                ifwassprinting_onground *= 1.0203136137;
            }
        }
        if (pressedKeys.contains("S") && (pressedKeys.contains("D") || pressedKeys.contains("A"))) {
            if (player.isSneaking()) {
                baseSpeed *= 1.4140131971;
                ifwassprinting_onground *= 1.4140131971;
            } else {
                baseSpeed *= 1.0203136137;
                ifwassprinting_onground *= 1.0203136137;
            }
        }
        // if they are speeding and they are in fact sneaking, unsneak them and if they continue to sneak assume a hacker trying to abuse the system and alert
        if (containsWord(Collections.singletonList(gbup), "SLIME")) {
            baseSpeed *= 0.318;
            ifwassprinting_onground *= 0.318;
        }
        if (containsWord(Collections.singletonList(gbup), "SOUL")) {
            baseSpeed *= 0.5;
            ifwassprinting_onground *= 0.5;
        }

        if ((player_jumped) && (!isEverythingInListThatString(list_of_blocks_ontop_player, "AIR"))) {
            baseSpeed *= 2.34959060164;
            updateSpeedVerbose(player, -1.0);
        }

        // for speed and slowness the following :
        baseSpeed *= (((20 * (getPotionModifier(player, PotionEffectType.SPEED))) / 100) + 1); // speed potion
        ifwassprinting_onground *= (((20 * (getPotionModifier(player, PotionEffectType.SPEED))) / 100) + 1);
        if (getPotionModifier(player, PotionEffectType.SLOW) != 0) {
            baseSpeed *= (abs(((15 * (getPotionModifier(player, PotionEffectType.SLOW))) / 100) - 1)); // slowness potion
        }
        if ((getPotionModifier(player, PotionEffectType.SLOW) != 0)) {
            ifwassprinting_onground *= (abs(((15 * (getPotionModifier(player, PotionEffectType.SLOW))) / 100) - 1));
        }
        // these 2 aren't dead accurate.. if falses check if it is these
        // end of calculation for speed and slowness

//        player.sendMessage(+ deltaXZ + " It calculated to be: " + (baseSpeed));
        //        prediction += MathUtil.movingFlyingV3(user, false);

        double AirCheck = AirFrictionCheck.computeIfAbsent(player.getName(), k -> 0.0);

        double predicted = deltaXZ_old * 0.91;
        double diff = deltaXZ - predicted;
//        player.sendMessage("ZF: " + diff +  "  AIRTIME: " + AirTimeCalc);
        if (AirTimeCalc > 1) {
//            double predicted = deltaXZ_old * 0.91;
//            double diff = deltaXZ - predicted;
            double maxVAirSpeed = 0;
            if (pressedKeys.contains("W") && pressedKeys.size() == 1) {
                maxVAirSpeed = 0.0254800081;
            }
            if (pressedKeys.contains("W") && pressedKeys.size() > 1) {
                maxVAirSpeed = 0.026000005697854878;
            }
            if (pressedKeys.contains("S") && pressedKeys.size() == 1) {
                maxVAirSpeed = 0.019600004445;
            }
            if (pressedKeys.contains("S") && pressedKeys.size() > 1) {
                maxVAirSpeed = 0.0200000055;
            }
            if (pressedKeys.contains("A") && pressedKeys.size() == 1 || pressedKeys.contains("D") && pressedKeys.size() == 1) {
                maxVAirSpeed = 0.019600004445;
            }
            if (Air_Friction){
                if (pressedKeys.isEmpty()) {
                } else { // ignore if empty
                    if (diff > maxVAirSpeed && !((player.getNoDamageTicks() <= 10) && (1 <= player.getNoDamageTicks()))) {// && in_a_liquid == false && walkingonoffobjects == false && !(isEverythingInListThatString(list_of_blocks_ontop_player, "AIR"))) {
                        AirFrictionCheck.put(player.getName(), (AirCheck + 1));
                        if (AirCheck > 8) {
                            identifieralertplayers("Air-Friction(" + diff + ", " + maxVAirSpeed + ")", player);
                            UpdateAlerts(player, +0.5, "safe");
                            player.teleport(previous_location.get(player.getName()));
//                        player.sendMessage(ChatColor.GREEN + "Possible air modification, by: " + ((diff / maxVAirSpeed) * 100));
                        }
                    } else if (diff < maxVAirSpeed) {
                        AirFrictionCheck.put(player.getName(), 0.0);
                    }
                }
            }
        }
        if (Air_Changes){
            double accelX = deltaX - deltaX_old;
            double accelZ = deltaZ - deltaZ_old;
            double hypot = (accelX * accelX) + (accelZ * accelZ);
            if (AirTimeCalc > 2 && hypot > 0.01 && !in_a_liquid && (accelX > -0.0049 || accelZ > -0.0049) && blocknearplayer == false && !((player.getNoDamageTicks() <= 20) && (1 <= player.getNoDamageTicks())) && LastTimeSinceTeleport_calc > 6) { //0.1*0.1
                identifieralertplayers("Air-Changes", player);
                UpdateAlerts(player, +0.7, "safe");
            }
            if (player.isSneaking() && player.isSprinting()) {
                player.teleport(previous_location.get(player.getName()));
                player.setSneaking(false);
                player.setSprinting(false);
//            player.sendMessage(ChatColor.DARK_GRAY + "Sneaking and sprinting at the same time?");
            }
        }
        Double vZ = player.getVelocity().getZ();
        Double vX = player.getVelocity().getX();

//        player.sendMessage(deltaXZ +"");
//        player.sendMessage(baseSpeed +" prediction");
        // air prediction                 double prediction = lastDeltaXZ * 0.91F + 0.026F;
        //                 double motionXZ = deltaXZ - prediction;
//                                if (motionXZ > 0.001 && deltaXZ > 0.22) {
//            then flag.

//        player.sendMessage("Last time since jump" + lastTimeSinceJump_calc);

        // add micro jumps, meaning, if there is a block atop player, and the player deltaY is 0.42~

        if (Speed){
            if (deltaXZ > (baseSpeed) && player.isOnGround() && (lastTimeSinceJump_calc > 18) && !(pressedKeys.isEmpty())) {
//                if (deltaXZ <= 0.2202643222340769 && (!(player.isSprinting()))) {
//
//                } else if (deltaXZ <= 0.28634360057548175 && player.isSprinting()) {
//
//                } else {
//            player.sendMessage(ChatColor.RED + "Speeding!" + deltaXZ + " Its calculated to be: " + (baseSpeed));

                //player.sendMessage("V: " + ((abs(player.getVelocity().getZ()))+(abs(player.getVelocity().getX()))));
//            player.sendMessage("LastTimeSinceSprint_calc: " + LastTimeSinceSprint_calc + " deltaXZ: " + deltaXZ + " ifwassprinting_onground: " + ifwassprinting_onground + " IsSprinting: " + player.isSprinting());
                if (LastTimeSinceSprint_calc < 110 && deltaXZ <= ifwassprinting_onground && !player.isSprinting()) {
//                    player.sendMessage("Switched from Sprint to none sprint causing a false, expected: " + ifwassprinting_onground + " actual: " + deltaXZ);
                } else {
                    updateSpeedVerbose(player, +1.0);
                    if (SpeedVerbose.get(player.getName()) > 8) {
                        identifieralertplayers("Speed(" + deltaXZ + ")", player);
                        UpdateAlerts(player, +1.0, "safe");
//                    player.sendMessage(ChatColor.RED + "Speeding!" + deltaXZ + " Its calculated to be: " + (baseSpeed));
//                    event.setCancelled(true);
                        SpeedVerbose.remove(player.getName());
                    }
                }  // since when the player is moving their cursor it starts to panic, very much

//            } else if ((!player.isOnGround())) {
//                double prediction = deltaXZ_old * 0.91F + 0.026F;
//                prediction += (player.getVelocity().getX()) + player.getVelocity().getZ();
//                double motionXZ = deltaXZ - prediction;
//                player.sendMessage(motionXZ +"");
//                player.sendMessage(ChatColor.GREEN + "" + deltaXZ);
//                if (motionXZ > 0.001 && deltaXZ > 0.22) {
//                    player.sendMessage(ChatColor.GREEN + "Test");
//                }
                //                if (player_jumped == true) {
//                    float radians = yaw * 0.017453292F;
//                    lastDeltaX -= (Math.sin(radians) * 0.2F);
//                    lastDeltaZ += (Math.cos(radians) * 0.2F);
//                }
//                double prediction = deltaXZ_old * 0.91F + 0.026F;
//                double motionXZ = deltaXZ - prediction;
//
//                player.sendMessage("mo: " + motionXZ);
//                player.sendMessage(ChatColor.GREEN + "DeltaXZ" + deltaXZ);

            } else if (deltaXZ <= (baseSpeed)) {
                SpeedVerbose.remove(player.getName());
            }
        }
        if (Speed_balancer_air){
            if (deltaXZ == deltaXZ_old && !(deltaXZ <= 0.01) && !player.isOnGround() && !(containsWord(hdftblks, "WEB"))) {
                identifieralertplayers("Speed(Balancer, Air)", player);
                UpdateAlerts(player, +0.5, "safe");
            }
        }

        if (player.getNoDamageTicks() > 0) {
            if (player.getLastDamageCause().getCause().name().equalsIgnoreCase("ENTITY_EXPLOSION") || player.getLastDamageCause().getCause().name().equalsIgnoreCase("FALL")) {
                explosionTickIgnoreairspeed.put(player.getName(), true);
            }
        }
        Boolean toIgnoreAirSpeedCheck = explosionTickIgnoreairspeed.computeIfAbsent(player.getName(), k -> false);
        if ((playerY - lower_yupvalue <= 1.02) && toIgnoreAirSpeedCheck == true) {
            explosionTickIgnoreairspeed.put(player.getName(), false);
        }
//        player.sendMessage("Ignore air speed check: " + toIgnoreAirSpeedCheck + " ground: " + player.isOnGround() + " getlastcause " + player.getLastDamageCause().getCause().name());


        if (Air_Speed) {
            if (!(player.isOnGround())) {
                double baseSpeed_noground = 0.200204948146435444;

                if (player.isSprinting()) {
                    baseSpeed_noground *= 1.7971609021;
                }
//            double baseSpeed_noground = 0.37699;


                baseSpeed_noground *= Math.pow(1 + 4.8 / 100, getPotionModifier(player, PotionEffectType.SPEED));

                // sprint speed is 0.3597400000
// speed 1 max speed is 0.37699
//speed 2 is 0.3950671599
//            double baseSpeed_noground =0.3950671599;
// current difference is 1.0479512982, from this 0.37699
//            0.37698171117088
                //            if (pressedKeys.contains("W") && player.isSprinting()) {
//                if (deltaXZ > 0.35972) {
//                    player.sendMessage("deltaXZ W: " + deltaXZ);
//                }
//            }
//            if (pressedKeys.contains("W") && !player.isSprinting()) {
//                if (deltaXZ > 0.200171281030545) {
//                    player.sendMessage("deltaXZ NoSprint W: " + deltaXZ);
//                }
//            }
//            player.sendMessage("Debug expected: " + baseSpeed_noground + " actual: " + deltaXZ);
//            if (player.getLastDamageCause() != null) {
//                player.sendMessage("LastDamageCause: " + player.getLastDamageCause().getCause().name());
//            }
                if (toIgnoreAirSpeedCheck == true || LastTimeSinceTeleport_calc < 3) {
                } else {
//                player.sendMessage("Check is running");
                    double ifwassprintingspeed = 0;
                    if (!player.isSprinting()) {
                        ifwassprintingspeed = ((0.200204948146435444 * 1.7971609021) * (Math.pow(1 + 4.8 / 100, getPotionModifier(player, PotionEffectType.SPEED))));
                    }
                    if (pressedKeys.isEmpty()) {
                    } else {
                        if ((deltaXZ > baseSpeed_noground && !((player.getNoDamageTicks() <= 20) && (2 <= player.getNoDamageTicks())))) {
//                Bukkit.broadcastMessage(player.getNoDamageTicks() + "");
                            if (LastTimeSinceSprint_calc < 110 && ifwassprintingspeed >= deltaXZ && !player.isSprinting()) {
//                    player.sendMessage("Switched from Sprint to none sprint causing a false, expected: " + baseSpeed_noground + " actual: " + deltaXZ);
                            } else if (deltaY != 0 && deltaXZ < 1 && containsWord(blocksUnderPlayerLOWER, "ICE")) {

                            } else {
//                            player.sendMessage((lastTimeSinceJump_calc < 110) + " - " + (ifwassprintingspeed >= deltaXZ) + " - " + player.isSprinting() + " - " + player.isSneaking());
                                Double previousAirSpeedXZ = previous_airspeed1.computeIfAbsent(player.getName(), k -> 0.0);
                                if (previousAirSpeedXZ > deltaXZ) {
                                } else {
                                    identifieralertplayers("Air-Speed(E: " + baseSpeed_noground + ", A:" + deltaXZ + ")", player);
                                    previous_airspeed1.put(player.getName(), deltaXZ);
                                    if (containsWord(blocksUnderPlayerLOWER, "ICE")) { // sometimes it falses on ice, so lowering the vl, also the other airspeed check can take care of this
                                        UpdateAlerts(player, +0.4, "unsafe");

                                    } else {
                                        UpdateAlerts(player, +1.0, "unsafe");
                                    }
                                    if (!player.isSprinting()) {
                                        player.setSprinting(true);
                                    }
                                    if (cancelorsetback) {
                                        player.teleport(previous_location.get(player.getName()));
                                    }
                                }
                            }
//                    player.sendMessage("Off ground Speed is faster, expected: " + baseSpeed_noground + " actual: " + deltaXZ);
                        }
                    }
                }
            }
        }
        if (Safewalk) {
            if (accelXZ < 0.1 && accelXZ > 0.099 && bridgingTimeSince < 40) {
                identifieralertplayers("Safewalk", player);
                UpdateAlerts(player, +1.0, "safe");
            }
        }
//        player.sendMessage(ChatColor.RED + "accel: " + accelXZ);

        if (Safewalk_near){
            if (deltaXZ > 0 && !(player.isSneaking()) && player.isOnGround() && bloc11k.getType().name().equals("AIR")) {
//            player.sendMessage("offsetX: " + formatDouble(offsetX) + "  offsetZ:" + formatDouble(offsetZ) + " oldX:" + formatDouble(oldX) + " oldZ:" + formatDouble(oldZ));
                if (Double.parseDouble(formatDouble(oldX)) == Double.parseDouble(formatDouble(offsetX)) || Double.parseDouble(formatDouble(oldZ)) == Double.parseDouble(formatDouble(offsetZ))) {
                    if (pressedKeys.contains("W") || pressedKeys.contains("S")) {
                        double temp1 = SafeWalkCheckA.computeIfAbsent(player.getName(), k -> 0.0);
                        SafeWalkCheckA.put(player.getName(), temp1 + 1.0);
                        if (SafeWalkCheckA.get(player.getName()) > 3) { // tolerance
                            identifieralertplayers("Safewalk(Beta, Near)", player);
//                            UpdateAlerts(player, +1.0, "undetermined");

                            if (cancelorsetback) {
                                player.teleport(previous_location.get(player.getName()));
//                                event.setCancelled(true);
                            }
                        }
                    }
                }


            } else {
                SafeWalkCheckA.put(player.getName(), 0.0);
            }
        }

//        if (user.getTick() < 60
//                || user.getVehicleTicks() > 0
//                || user.shouldCancel()
//                || user.getLastTeleportTimer().hasNotPassed(9)
//                || user.getMovementProcessor().isBouncedOnSlime()
//                || user.getActionProcessor().getRespawnTimer().hasNotPassed(20)
//                || user.getPlayer().isDead()
//                || user.getPlayer().getWalkSpeed() != 0.2F
//                || user.getActionProcessor().getServerPositionTimer().hasNotPassed(5)
//                || !user.isChunkLoaded()
//                || user.getElytraProcessor().isUsingElytra()) {
//            return;
//        }


        double prediction = deltaXZ_old * 0.91F + 0.026F;

        double velocityH = Math.hypot(player.getVelocity().getX(), player.getVelocity().getZ());
//        if (deltaY != 0) {
//            prediction += velocityH;
//        }

        double motionXZ = deltaXZ - prediction;
        if (!player.isOnGround() && (AirTimeCalc > 2) && in_a_liquid==false && !((containsWord(list_of_blocks_Y3_player, "LADDER")) || (containsWord(hdftblks, "WEB")))) {
//            player.sendMessage(ChatColor.GREEN + "MotionXZ: " + motionXZ + " deltaXZ: " + deltaXZ);
//            identifieralertplayers("Air-Speed 2", player);

            if (motionXZ > 0 && !(player.getNoDamageTicks() < 20 && player.getNoDamageTicks() > 1)) {
//                player.sendMessage(ChatColor.GREEN + "MotionXZ: " + motionXZ + " deltaXZ: " + deltaXZ);
//                player.sendMessage("test speed");
                identifieralertplayers("Air-Speed 2("+motionXZ+")", player);
                UpdateAlerts(player, +0.4, "safe");
                // air speed modification
            }
        }

                //We must get the friction of the block the player is currently on.
        double blockFriction = (0.91F * 0.6F);


        if ((player.isOnGround()) || (containsWord(list_of_blocks_under_player, ("SLIME")))) {


                    if (containsWord(list_of_blocks_under_player, ("SLIME"))) {
                        blockFriction = 0.91F * 0.8F;
                    }

                    if (containsWord(list_of_blocks_under_player, ("ICE"))) {
                        blockFriction = 0.91F * 0.98F;
                    }

                    if (containsWord(list_of_blocks_under_player, ("ICE")) && containsWord(list_of_blocks_under_player, ("SLIME"))) {
                        blockFriction = (0.91F * 0.8F) * 0.98F;
                    }

                } else {
                        //When the player is in the air their friction is always 0.91F
                        blockFriction=0.91F;
                }
//
//            case Packet.Server.EXPLOSION: {
//                WrappedOutExplosionPacket explosionPacket =
//                        new WrappedOutExplosionPacket(event.getPacket(), user.getPlayer());
//
//                double expX = explosionPacket.getMotionX(), expZ = explosionPacket.getMotionZ();
//
//                double expDeltaX = Math.abs(Math.abs(expX)
//                        - Math.abs(lastExpX));
//                double expDeltaZ = Math.abs(Math.abs(expZ)
//                        - Math.abs(lastExpZ));
//
//                explosionSpeed = Math.hypot(expDeltaX, expDeltaZ);
//
//
//                this.lastExpX = expX;
//                this.lastExpZ = expZ;
//                break;
//            }
//
//            case Packet.Client.BLOCK_PLACE: {
//
//                WrappedInBlockPlacePacket wrappedInBlockPlacePacket = new WrappedInBlockPlacePacket(event.getPacket(), user.getPlayer());
//
//                if (!wrappedInBlockPlacePacket.getItemStack().getType().isBlock()) {
//
//                    if (wrappedInBlockPlacePacket.getPosition().getX() == -1
//                            && wrappedInBlockPlacePacket.getPosition().getY() == -1 && wrappedInBlockPlacePacket.getPosition().getZ() == -1) {
//
//                        if (user.isSword(user.getPlayer().getItemInHand()) && user.getPlayer().getItemInHand() != null) {
//                            if (!hit) {
//                                useSword = useItem = true;
//                            }
//                        }
//                    }
//                }







        double prediction_ground = deltaXZ_old * blockFriction;


        if (player_jumped){
            prediction_ground += 0.2F;
        }

//         // interact thing
//            if (user.getCombatProcessor().getUseEntityTimer().hasNotPassed(20)) {
//                prediction += 0.0101f;
//            }

        if (velocityH !=0) {
            prediction_ground += velocityH;
        }


        prediction_ground += baseSpeed - 0.2159;
        double totalSpeed = deltaXZ - prediction_ground - 0.0624; // undefined delta idk why it is here

        if (player.isOnGround() && AirTimeCalc <= 2 && !(player.getNoDamageTicks() < 20 && player.getNoDamageTicks() > 1)) {
//            player.sendMessage("ground speed over done: " + totalSpeed);
            if (totalSpeed > 0.6) {
                identifieralertplayers("Ground Peaks", player);
                UpdateAlerts(player, +0.5, "safe");
            }
        }







        if (player_jumped) {
            LastTimeSinceJumpInTicks.put(player.getName(), (double) Bukkit.getServer().getWorld("world").getFullTime());
        }
        if (player.isSprinting()) {
            LastTimeSinceSprintingInTicks.put(player.getName(), (double) Bukkit.getServer().getWorld("world").getFullTime());
        }




//        if (yawDiff >= 315 || yawDiff < 45) {
//            // Player moved in the positive X direction (pressed "D")
//            keysPressed+=("D");
//        } else if (yawDiff >= 45 && yawDiff < 135) {
//            // Player moved in the positive Z direction (pressed "W")
//            keysPressed+=("W");
//        } else if (yawDiff >= 135 && yawDiff < 225) {
//            // Player moved in the negative X direction (pressed "A")
//            keysPressed+=("A");
//        } else if (yawDiff >= 225 && yawDiff < 315) {
//            // Player moved in the negative Z direction (pressed "S")
//            keysPressed+=("S");
//        }
////
//        // Check if the player is moving diagonally
//        if (keysPressed.length() > 1) {
//            player.sendMessage("You are moving diagonally: " + keysPressed);
//        } else if (keysPressed.length() == 1) {
//            // Only one key is pressed
//            player.sendMessage("You pressed: " + keysPressed);
//        } else {
//            // No movement keys pressed
//            player.sendMessage("You are not moving.");
//        }


        //motionx = deltaX
        //motionz = deltaz
        // WASD to know?


        //player.sendMessage(" " + player_jumped);
        //player.sendMessage("Calculated Speed should be: " + (baseSpeed + speedCalculated));

        // sprint speed 0.2806
        // jumping speed(max) 0.5364673141356507

        // forward no sprint 0.2159 - THIS IS DEFAULT
        // backward 0.2155
        // to side, left or right, 0.2155
        // Sneaking 0.0648
        // Speed potion amplifier Sprinting 0.3367 - amplifier of 1
        // speed potion amplifier no sprint 0.259 - amplifier of 1

        // speed amplifier is  0.0562 * amplifier.
        // SPRINT Speed 1 0.3367
        //SPRINT Speed 2 0.3929

        // Slowness amplifier is -0.0421 * amplifier
        // SPRINT SLOWNESS 1 0.2385
        // SPRINT SLOWNESS 2 0.1961


        // slime block 0.0687

        //    playerXZ.put("Player1", Pair.of(10.0, 20.0));
//    playerXZ.put("Player2", Pair.of(30.0, 40.0));
//
//    Pair<Double, Double> player1Coordinates = playerXZ.get("Player1");
//    Double player1X = player1Coordinates.getLeft();
//    Double player1Z = player1Coordinates.getRight();

//
//        if (yaw >= 0 && yaw < 22.5) {
//            player.sendMessage("South");
//        } else if (yaw >= 22.5 && yaw < 67.5) {
//            player.sendMessage("Southwest");
//        } else if (yaw >= 67.5 && yaw < 112.5) {
//            player.sendMessage("West");
//        } else if (yaw >= 112.5 && yaw < 157.5) {
//            player.sendMessage("Northwest");
//        } else if (yaw >= 157.5 && yaw < 202.5) {
//            player.sendMessage("North");
//        } else if (yaw >= 202.5 && yaw < 247.5) {
//            player.sendMessage("Northeast");
//        } else if (yaw >= 247.5 && yaw < 292.5) {
//            player.sendMessage("East");
//        } else if (yaw >= 292.5 && yaw < 337.5) {
//            player.sendMessage("Southeast");
//        } else {
//            player.sendMessage("South");
//        }


        // now for speed we shall check.


    }
    public boolean isNonAirBlockNearPlayer(Player player) {
        Vector playerPos = player.getLocation().toVector();
        Vector belowPlayer = playerPos.clone().subtract(new Vector(0, 1, 0));
        Vector atPlayer = playerPos.clone();
        Vector abovePlayer = playerPos.clone().add(new Vector(0, 1, 0));

        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Vector checkPos = playerPos.clone().add(new Vector(x, y, z));
                    if (checkPos.equals(belowPlayer) || checkPos.equals(atPlayer) || checkPos.equals(abovePlayer)) continue;

                    Material checkBlockType = player.getWorld().getBlockAt(checkPos.toLocation(player.getWorld())).getType();
                    if (checkBlockType != Material.AIR) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    public static boolean isWithinTenPercent(double number1, double number2) {
//        number1 = Math.abs(number1);
//        number2 = Math.abs(number2);
        double range;

        // Calculate the range by multiplying number2 by 10% (0.1)
        if (number1 < 2 && number1 > -2) {
            range = abs(10);
        } else {
            range = abs(number2 / 10);
        }
        double rn1 = number2 - range;
        double rn2 = number2 + range;


        // Check if number1 is within the range of number2
        return (number1 >= rn1 && number1 < rn2);
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


    private double getVelocity(double x, double y, double z) {
        // Calculate velocity based on motionX, motionY, motionZ
        return Math.sqrt(x * x + y * y + z * z);
    }

    public void UpdateAlerts(Player player, Double amount, String Severity) { // Safe Spam undetermined/unsafe
        double curamt = 0;
        if (Severity.toLowerCase() == "safe") {
            curamt = AlertsTypeSafe.computeIfAbsent(player.getName(), k -> (double) 0);
            AlertsTypeSafe.put(player.getName(), curamt + amount);
        } else if (Severity.toLowerCase() == "unsafe") {
            curamt = AlertsTypeUnsolidified.computeIfAbsent(player.getName(), k -> (double) 0);
            AlertsTypeUnsolidified.put(player.getName(), curamt + amount);
        } else if (Severity.toLowerCase() == "spam") {
            curamt = AlertsTypeSpam.computeIfAbsent(player.getName(), k -> (double) 0);
            AlertsTypeSpam.put(player.getName(), curamt + amount);
        } else if (Severity.toLowerCase() == "undetermined") {
            curamt = AlertsTypeUndetermined.computeIfAbsent(player.getName(), k -> (double) 0);
            AlertsTypeUndetermined.put(player.getName(), curamt + amount);
        } else if (Severity.toLowerCase() == "combat") {
            curamt = AlertsTypeCombat.computeIfAbsent(player.getName(), k -> (double) 0);
            AlertsTypeCombat.put(player.getName(), curamt + amount);
        } else {
            Bukkit.getLogger().info("Severity not found: " + Severity + " for player: " + player.getName());
        }
//        movement_identifications.computeIfAbsent(player.getName(), k -> (double) 0);
//        player.sendMessage("Your movement alerts are: " + movement_identifications.get(player.getName()));
    }

    public void updateMovementAlerts(Player player, Double amount) {
        movement_identifications.computeIfAbsent(player.getName(), k -> (double) 0);
        double amt = (movement_identifications.get(player.getName()) + amount);
        if (amt < 0) {
            amt = 0;
        }
        movement_identifications.put(player.getName(), amt);
//        player.sendMessage("Your movement alerts are: " + movement_identifications.get(player.getName()));
    }

    public void updateVelYVerbose(Player player, Double amount) {
        noVelYVerbose.computeIfAbsent(player.getName(), k -> (double) 0);
        double amt = (noVelYVerbose.get(player.getName()) + amount);
        if (amt < 0) {
            amt = 0;
        }
        noVelYVerbose.put(player.getName(), amt);
//        player.sendMessage("Your movement alerts are: " + movement_identifications.get(player.getName()));
    }

    public void updateSpeedVerbose(Player player, Double amount) {
        SpeedVerbose.computeIfAbsent(player.getName(), k -> (double) 0);
        double amt = (SpeedVerbose.get(player.getName()) + amount);
        if (amt < 0) {
            amt = 0;
        }
        SpeedVerbose.put(player.getName(), amt);
//        player.sendMessage("Your movement alerts are: " + movement_identifications.get(player.getName()));
    }

    public void updateOmniSprintVerbose(Player player, Double amount) {
        OmniSprintCheck.computeIfAbsent(player.getName(), k -> (double) 0);
        double amt = (OmniSprintCheck.get(player.getName()) + amount);
        if (amt < 0) {
            amt = 0;
        }
        OmniSprintCheck.put(player.getName(), amt);
//        player.sendMessage("Your movement alerts are: " + movement_identifications.get(player.getName()));
    }

    public static List<String> gbunpwtrlva(Player player) {
        List<String> blocks = new ArrayList<>();
        Location location = player.getLocation().clone();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = location.clone().add(x, -0.1875, z); // y value is always -1

                // Check if there is a non-AIR block above
                if (loc.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
                    continue;
                }

                if (loc.getBlock().getType() != Material.AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }


                Location loc1 = location.clone().add(x, -1.1875, z); // y value is always -1

                // Check if there is a non-AIR block above
                if (loc1.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
                    continue;
                }

                if (loc1.getBlock().getType() != Material.AIR) {
                    blocks.add(loc1.getBlock().getType().name());
                }

            }
        }

        return blocks;
    }


    public static List<String> getBlocksUnderneathPlayerAsLongNoBlockIsOnTop(Player player) {
        List<String> blocks = new ArrayList<>();
        Location location = player.getLocation().clone();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = location.clone().add(x, -0, z); // y value is always -1

                // Check if there is a non-AIR block above
                if (loc.clone().add(0, 1, 0).getBlock().getType() != Material.LAVA && loc.clone().add(0, 1, 0).getBlock().getType() != Material.STATIONARY_LAVA && loc.clone().add(0, 1, 0).getBlock().getType() != Material.WATER && loc.clone().add(0, 1, 0).getBlock().getType() != Material.STATIONARY_WATER && loc.clone().add(0, 1, 0).getBlock().getType() != Material.WEB && loc.clone().add(0, 1, 0).getBlock().getType() != Material.SIGN && loc.clone().add(0, 1, 0).getBlock().getType() != Material.WALL_SIGN && loc.clone().add(0, 1, 0).getBlock().getType() != Material.SIGN_POST && loc.clone().add(0, 1, 0).getBlock().getType() != Material.AIR && loc.clone().add(0, 1, 0).getBlock().getType() != Material.LONG_GRASS && containsWord(Collections.singletonList(loc.clone().add(0, 1, 0).getBlock().getType().name()), "FENCE")) {
                    continue;
                }

                if (loc.getBlock().getType() != Material.AIR) {
                    blocks.add(loc.getBlock().getType().name());
                }


                Location loc1 = location.clone().add(x, -1, z); // y value is always -1

                // Check if there is a non-AIR block above
                if (loc1.clone().add(0, 1, 0).getBlock().getType() != Material.LAVA && loc1.clone().add(0, 1, 0).getBlock().getType() != Material.STATIONARY_LAVA && loc1.clone().add(0, 1, 0).getBlock().getType() != Material.WATER && loc1.clone().add(0, 1, 0).getBlock().getType() != Material.STATIONARY_WATER && loc1.clone().add(0, 1, 0).getBlock().getType() != Material.WEB && loc1.clone().add(0, 1, 0).getBlock().getType() != Material.SIGN && loc1.clone().add(0, 1, 0).getBlock().getType() != Material.WALL_SIGN && loc1.clone().add(0, 1, 0).getBlock().getType() != Material.SIGN_POST && loc1.clone().add(0, 1, 0).getBlock().getType() != Material.AIR && loc.clone().add(0, 1, 0).getBlock().getType() != Material.LONG_GRASS && containsWord(Collections.singletonList(loc.clone().add(0, 1, 0).getBlock().getType().name()), "FENCE")) {
                    continue;
                }

                if (loc1.getBlock().getType() != Material.AIR) {
                    blocks.add(loc1.getBlock().getType().name());
                }


                // Rest of the code...
            }
        }

        return blocks;
    }


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

    public static List<String> FeetBlocks(Player player) {
        List<String> blocks = new ArrayList<>();
        Location location = player.getLocation().clone();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = location.clone().add(x, 0, z); // y value is always -1
                if (loc.clone().getBlock().getType().name() == "AIR") {
                } else {
                    blocks.add(loc.getBlock().getType().name());
                }
                Location loc1 = loc.clone().add(0, 1, 0);
                if (loc1.clone().getBlock().getType().name() == "AIR") {
                } else {
                    blocks.add(loc1.getBlock().getType().name());
                }
            }
        }
        return blocks;
    }

    public static List<String> HeadBlocks(Player player) {
        List<String> blocks = new ArrayList<>();
        Location location = player.getLocation().clone();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = location.clone().add(x, 1.875, z); // y value is always -1

                // Check if there is a non-AIR block below
                if (loc.clone().getBlock().getType().name() == "AIR") {
                    continue;
                }
                blocks.add(loc.getBlock().getType().name());
                Location loc1 = loc.clone().subtract(0, 1, 0);
                if (loc1.clone().getBlock().getType().name() == "AIR") {
                } else {
                    blocks.add(loc1.getBlock().getType().name());
                }

            }
        }

        return blocks;
    }

    public static List<String> getBlocksOnTopPlayerAsLongNoBlockIsUnder(Player player) {
        List<String> blocks = new ArrayList<>();
        Location location = player.getLocation().clone();

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Location loc = location.clone().add(x, 2, z); // y value is always -1

                // Check if there is a non-AIR block below
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
                    continue;
                }

                blocks.add(loc.getBlock().getType().name());

                // Also check the block at y = 1
                loc = location.clone().add(x, 1, z);

                // Check if there is a non-AIR block below
                if (loc.clone().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
                    continue;
                }

                blocks.add(loc.getBlock().getType().name());
            }
        }

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

    public static boolean containsWord(List<String> list_of_strings, String word) {
        for (String string1 : list_of_strings) {
            if (string1.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsAnyWordOfTheWordsInTwoLists(List<String> list1, List<String> list2) {
        for (String word1 : list1) {
            for (String word2 : list2) {
                if (word1.toLowerCase().contains(word2.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean containsWord_rev(List<String> list_of_strings, String word) {
        for (String string1 : list_of_strings) {
            if (word.toLowerCase().contains(string1.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEverythingInListThatString(List<String> listOfBlocksUnderPlayer, String block) { // you give a list, and it checks if everything is that string, if not, returns false
        if (block.equalsIgnoreCase("air")) {
            for (String blockUnderPlayer : listOfBlocksUnderPlayer) {
                if (!blockUnderPlayer.equalsIgnoreCase(block)) {
                    return false;
                } // note that is list is empty it can still return a true
            }
        } else {
            for (String blockUnderPlayer : listOfBlocksUnderPlayer) {
                if (!blockUnderPlayer.toLowerCase().contains(block.toLowerCase())) {
                    return false;
                } // note that is list is empty it can still return a true
            }
        }
        return true;
    }

    public static String getBlockUnderneathPlayer(Player player) {
        Location location = player.getLocation().clone();
        location.subtract(0, 0.1875, 0);
        return location.getBlock().getType().name();
    }

    public static String getBlockUnderneathPlayery2(Player player) {
        Location location = player.getLocation().clone();
        location.subtract(0, 1.1875, 0);
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
    public void InventoryEvent(InventoryEvent event) {
//    Bukkit.broadcastMessage("Inventory event called, " +event.getEventName());
    // literally does nothing
    }
    @EventHandler
    public void OnTeleportEvent(PlayerTeleportEvent event) {
//    event.getPlayer().sendMessage("Teleport event called");
        LastTimeSinceTeleportinTicks.put(event.getPlayer().getName(), (double) Bukkit.getServer().getWorld("world").getFullTime());
    }

    @EventHandler
    public void onPlayerVelocity(PlayerVelocityEvent event) {
//        Player player = event.getPlayer();
//        double actualVelocityX = event.getVelocity().getX();
//        double actualVelocityY = event.getVelocity().getY();
//        double actualVelocityZ = event.getVelocity().getZ();
//
//        // Calculate the expected velocity of the player based on their movement input and the physics of the game.
//        double expectedVelocityX = getExpectedVelocityX(player);
//        double expectedVelocityY = getExpectedVelocityY(player);
//        double expectedVelocityZ = 0;
//
//        // Track the player's movement over time.
//        List<Double[]> velocities = new ArrayList<>();
//        velocities.add(new Double[]{actualVelocityX, actualVelocityY, actualVelocityZ});
//
//        // Take into account the player's momentum.
//        double momentumX = 0;
//        double momentumY = 0;
//
//        // Use the Kalman filter to predict the player's future velocity.
//        // double[] predictedVelocity = kalmanFilter.predict(velocities, momentumX, momentumY);
//
//        // Compare the expected velocity with the actual velocity and the predicted velocity.
//        if (abs(expectedVelocityX - actualVelocityX) > THRESHOLD || abs(expectedVelocityY - actualVelocityY) > THRESHOLD || abs(expectedVelocityZ - actualVelocityZ) > THRESHOLD) {
//            // The player is using a velocity modifier.
//            //player.sendMessage("You are using a velocity modifier!");
//        }
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


    private void printReach(Player damager, Entity victim, double Reach, Location rhvic, Location rhdgr) {// double Distance, Location dsvic, Location dsdgr, Location rhvic, Location rhdgr, Boolean hitbox, Boolean hitpossible, Boolean hitPossible_nodelay) { // distance between hitboxes
        String damagerName = damager.getName();
        String victimName = victim.getName();

        if (!playerReach.containsKey(damager.getName())) {
            playerReach.put(damager.getName(), new HashMap<>());
        }
        playerReach.get(damager.getName()).put(victim.getName(), Reach);

        //        details.append(ChatColor.GRAY).append("(Reach) Distance between the two: ").append(ChatColor.WHITE).append(getDistance(dsvic, dsdgr) + "\n");

        String Reachdetails = ChatColor.GRAY + "Player: " + ChatColor.WHITE + damagerName + "\n" +
                ChatColor.GRAY + "Player's Reach: " + ChatColor.WHITE + Reach + " blocks\n" +
                ChatColor.GRAY + "Victim: " + ChatColor.WHITE + victim.getName() + "\n" +
//        details.append(ChatColor.GRAY).append("Distance: ").append(ChatColor.WHITE).append(Distance + " blocks\n");
//        details.append(ChatColor.GRAY).append("Player hit possible: ").append(ChatColor.WHITE).append(hitpossible + "\n");
//        details.append(ChatColor.GRAY).append("Player hit possible no delay: ").append(ChatColor.WHITE).append(hitPossible_nodelay + "\n");

//        details.append(ChatColor.GRAY).append("Player using hitbox: ").append(ChatColor.WHITE).append(hitbox + "\n");

//        details.append(ChatColor.GRAY).append("Victim Distance Check Location: ").append(ChatColor.WHITE).append("\n");
//        details.append(ChatColor.DARK_GRAY).append("X: " + dsvic.getX() + " Y: " + dsvic.getY() + " Z: " + dsvic.getZ()).append(ChatColor.WHITE).append("\n");
//        details.append(ChatColor.GRAY).append("Damager Distance Check Location: ").append(ChatColor.WHITE).append("\n");
//        details.append(ChatColor.DARK_GRAY).append("X: " + dsdgr.getX() + " Y: " + dsdgr.getY() + " Z: " + dsdgr.getZ()).append(ChatColor.WHITE).append("\n");
                ChatColor.GRAY + "Reach Check Victim Location: " + ChatColor.WHITE + "\n" +
                ChatColor.DARK_GRAY + "X: " + rhvic.getX() + " Y: " + rhvic.getY() + " Z: " + rhvic.getZ() + ChatColor.WHITE + "\n" +
                ChatColor.GRAY + "Reach Check Damager Location: " + ChatColor.WHITE + "\n" +
                ChatColor.DARK_GRAY + "X: " + rhdgr.getX() + " Y: " + rhdgr.getY() + " Z: " + rhdgr.getZ() + ChatColor.WHITE + "\n" +
                ChatColor.GRAY + "(Distance) Distance between the two: " + ChatColor.WHITE + getDistance(rhvic, rhdgr) + "\n"
//        details.append(ChatColor.GRAY).append("(Reach) Distance between the two: ").append(ChatColor.WHITE).append(getDistance(dsvic, dsdgr) + "\n");
                ;

        TextComponent mainComponent = new TextComponent(ChatColor.YELLOW + damagerName + " Reach: " + ChatColor.WHITE + formatDouble(Reach) + ChatColor.RESET + ChatColor.ITALIC + " (Hover to see details)");
        mainComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Reachdetails).create()));
//        if (hitpossible == false && hitPossible_nodelay == false && hitbox == true) {
////            damager.sendMessage(ChatColor.WHITE + "[" + ChatColor.AQUA + "AntiCheat" + ChatColor.WHITE + "] " + ChatColor.DARK_PURPLE + "- " + ChatColor.YELLOW + damagerName + ChatColor.WHITE + " is identified in suspicious Combat, hitbox");
//                identifieralertplayers("Hitbox", damager);
//        }

//        if ((new Double(formatDouble(Distance))) >= 3.1 && (new Double(formatDouble(Reach))) > 3 && hitpossible == false && hitPossible_nodelay == false) {// && hitbox == true) {
////            TextComponent textReachAlert = new TextComponent(ChatColor.WHITE + "[" + ChatColor.YELLOW + "AntiCheat" + ChatColor.WHITE + "] " + ChatColor.AQUA + "- " + ChatColor.RED + damagerName + ChatColor.WHITE + " is identified in suspicious Combat(beta & unstable)");
////            textReachAlert.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Reachdetails).create()));
////            damager.spigot().sendMessage(textReachAlert);
//        }


        for (Player player : Bukkit.getOnlinePlayers()) {
            if (printreachEnabled.containsKey(player.getName()) && printreachEnabled.get(player.getName()).contains(damagerName)) {
                player.spigot().sendMessage(mainComponent);
            }
        }
    }



    public boolean get_check(String checkName, boolean defaultValue) {
        if (getConfig().contains(checkName)) {
            Object configValue = getConfig().get(checkName);
            if (configValue instanceof Boolean) {
                return (boolean) configValue;
            } else if (configValue instanceof String) {
                String stringValue = (String) configValue;
                if ("true".equalsIgnoreCase(stringValue)) {
                    getConfig().set(checkName, true);
                    saveConfig();
                    return true;
                } else if ("false".equalsIgnoreCase(stringValue)) {
                    getConfig().set(checkName, false);
                    saveConfig();
                    return false;
                }
            } else if (configValue instanceof Number) {
                Number numberValue = (Number) configValue;
                if (numberValue.intValue() == 0) {
                    getConfig().set(checkName, false);
                    saveConfig();
                    return false;
                } else if (numberValue.intValue() == 1) {
                    getConfig().set(checkName, true);
                    saveConfig();
                    return true;
                }
            }
            // Handle other cases here

            // Default return if none of the above cases match
            getConfig().set(checkName, defaultValue);
            saveConfig();
            return defaultValue;
        } else {
            getConfig().set(checkName, defaultValue);
            saveConfig();
            return defaultValue;
        }
    }


    public void set(String checkName, boolean value) {
        getConfig().set(checkName, value);
        saveConfig();
    }




private String formatDouble(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).toString();
    }

    private String formatOne(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).toString();
    }

    private String formatTriple(double value) {
        return BigDecimal.valueOf(value).setScale(3, RoundingMode.HALF_UP).toString();
    }

    private double formatQuad(double value) {
        return (new Double((BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP)).toString()));
    }

}
