
package br.net.rankup.spawners;

import br.net.rankup.booster.misc.BukkitUtils;
import br.net.rankup.spawners.commands.SpawnerCommand;
import br.net.rankup.spawners.database.HikariDataBase;
import br.net.rankup.spawners.drops.cache.BonusCache;
import br.net.rankup.spawners.drops.controller.BonusController;
import br.net.rankup.spawners.drops.manager.DropManager;
import br.net.rankup.spawners.hook.PlaceHolderHook;
import br.net.rankup.spawners.listener.*;
import br.net.rankup.spawners.manager.ShopManager;
import br.net.rankup.spawners.manager.SpawnerManager;
import br.net.rankup.spawners.manager.UpgradeManager;
import br.net.rankup.spawners.manager.UserManager;
import br.net.rankup.spawners.misc.ConfigUtils;
import br.net.rankup.spawners.model.shop.SpawnerShop;
import br.net.rankup.spawners.registry.PlaceHolderRegistry;
import br.net.rankup.spawners.repository.SpawnerRepository;
import br.net.rankup.spawners.repository.UsersRepository;
import br.net.rankup.spawners.tasks.SpawnerTask;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
import lombok.Setter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageHolder;
import me.saiintbrisson.minecraft.command.message.MessageType;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
public final class SpawnerPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        instance = this;

        start = System.currentTimeMillis();
        configuration = new ConfigUtils(this,"config.yml");
        configuration.saveDefaultConfig();

        this.getServer().getWorld("plotworld").getEntities().clear();

        bukkitFrame = new BukkitFrame(SpawnerPlugin.getInstance());
        loadCommands();
        inventoryManager = new InventoryManager(this);
        inventoryManager.invoke();

        (this.userManager = new UserManager()).load();
        (this.spawnerManager = new SpawnerManager()).load();
        (this.upgradeManager = new UpgradeManager()).load();
        (this.dropManager = new DropManager()).loadAll();
        (this.shopManager = new ShopManager()).loadAll();

        this.bonusCache = new BonusCache(this);
        this.bonusController = new BonusController(this);

        Constants.loadRewards(this);

        HikariDataBase.prepareDatabase();
        bukkitFrame.registerCommands(new SpawnerCommand());

        usersRepository.loadAll();
        spawnerRepository.loadAll();

        PlaceHolderRegistry.init();

        final RegisteredServiceProvider<Economy> registration = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (registration != null) {
            economy = (Economy)registration.getProvider();
        }

        this.getServer().getPluginManager().registerEvents(new SpawnerListener(), this);
        this.getServer().getPluginManager().registerEvents(new UserListener(), this);
        this.getServer().getPluginManager().registerEvents(new UsableItemListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatBuyListener(), this);

        (new SpawnerTask()).runTaskTimer(this, 20L, 20L);

        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&aplugin started successfully ({time} ms)"
                .replace("{time}",""+(System.currentTimeMillis() - start)));
        enable.set(true);
    }

    @Override
    public void onDisable() {
        for (World world : getServer().getWorlds()) {
            for(Entity entity : world.getEntities()) {
                if(entity.hasMetadata("spawner_location")){
                    entity.remove();
                }
            }
         }

        this.getSpawnerManager().getSpawners().values().forEach(spawnerModel -> {
            this.getSpawnerRepository().update(spawnerModel);
        });

        this.getUserManager().getUsers().values().forEach(userModel -> {
            this.getUsersRepository().update(userModel);
        });

        if(enable.get()) {
            BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&cplugin successfully turned off!");
        } else {
            BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&cplugin suffered a some problem");
        }
    }



    private BonusCache bonusCache;
    private BonusController bonusController;
    @Getter
    private DropManager dropManager;
    @Getter
    private static Economy economy;
    @Getter
    private UserManager userManager;
    @Getter
    private SpawnerManager spawnerManager;
    @Getter
    private ShopManager shopManager;
    @Getter
    private UpgradeManager upgradeManager;
    private HikariDataBase hikariDataBase;
    private UsersRepository usersRepository;
    private SpawnerRepository spawnerRepository;
    static long start = 0;
    private AtomicBoolean enable = new AtomicBoolean(false);
    private static ConfigUtils configuration;
    private static SpawnerPlugin instance;
    @Getter
    private InventoryManager inventoryManager;
    @Getter
    private  BukkitFrame bukkitFrame;
    public static SpawnerPlugin getInstance() { return instance; }
    public static FileConfiguration getConfiguration() {
        return configuration.getConfig();
    }
    public static ConfigUtils getConfigUtils() {
        return configuration;
    }

    public static long getStart() {
        return start;
    }


    private void loadCommands() {
        MessageHolder messageHolder = getBukkitFrame().getMessageHolder();
        messageHolder.setMessage(MessageType.ERROR, "§cOcorreu um erro durante a execução deste comando, erro: §7{error}§c.");
        messageHolder.setMessage(MessageType.INCORRECT_USAGE, "§cUtilize: /{usage}");
        messageHolder.setMessage(MessageType.NO_PERMISSION, "§cVocê não tem permissão para executar esse comando.");
        messageHolder.setMessage(MessageType.INCORRECT_TARGET, "§cVocê não pode utilizar este comando pois ele é direcionado apenas para {target}.");
    }
        }
