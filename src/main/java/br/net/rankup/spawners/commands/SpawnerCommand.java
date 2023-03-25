package br.net.rankup.spawners.commands;

import br.net.rankup.logger.LogPlugin;
import br.net.rankup.spawners.SpawnerPlugin;
import br.net.rankup.spawners.inventory.SpawnerShopInventory;
import br.net.rankup.spawners.misc.BukkitUtils;
import br.net.rankup.spawners.misc.ItemBuilder;
import br.net.rankup.spawners.misc.SkullCreatorUtils;
import br.net.rankup.spawners.misc.Toolchain;
import br.net.rankup.spawners.model.shop.SpawnerShop;
import br.net.rankup.spawners.model.spawner.UpgradeModel;
import br.net.rankup.spawners.model.upgrade.CapacityUpgrade;
import br.net.rankup.spawners.model.upgrade.TimeUpgrade;
import br.net.rankup.spawners.model.upgrade.XpUpgrade;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SpawnerCommand {


    @Command(name = "spawner", aliases = {"gerador", "spawners"})
    public void handlerSpawnerCommand(Context<CommandSender> context) {
        CommandSender sender = context.getSender();
        if(sender instanceof Player) {
            Player player = (Player) sender;
            RyseInventory ryseInventory = new SpawnerShopInventory().build();
            ryseInventory.open(player);
        }
    }

    @Command(name = "spawner.help", aliases = {"ajuda"})
    public void handlerHelpCommand(Context<CommandSender> context) {
        CommandSender sender = context.getSender();
        if(sender instanceof Player) {
            BukkitUtils.sendMessage(sender, "");
            BukkitUtils.sendMessage(sender, " &a/spawners &f- &7Para abrir a loja de spawners.");
            if(sender.hasPermission("commands.spawner")) {
                BukkitUtils.sendMessage(sender, "");
                BukkitUtils.sendMessage(sender, " &a/spawner give <player> <type> <amount> <spawners> &f- &7Enviar um gerador a um jogador.");
                BukkitUtils.sendMessage(sender, " &a/spawner limite <player> <amount> &f- &7Enviar cheques de limite a um jogador.");
                BukkitUtils.sendMessage(sender, " &a/spawner stackmob <player> <amount> &f &7Enviar cheque de stackmob a um jogador.");
                BukkitUtils.sendMessage(sender, " &a/spawner help &f- &7Ver mensagem de ajuda.");
            }
            BukkitUtils.sendMessage(sender, "");
        }
    }

    @Command(name = "spawner.limite", permission = "commands.spawner")
    public void handlerLimiteCommand(Context<CommandSender> context, Player target, double amount) {
        CommandSender sender = context.getSender();

        ItemBuilder itemBuilder = new ItemBuilder(SkullCreatorUtils.itemFromUrl("caf039bec1fc1fb75196092b26e631f37a87dff143fc18297798d47c5eaaf"))
                .setName("§aLimite de Compra")
                .addLoreLine("§7Aumente seu limite de compra de")
                .addLoreLine("§7geradores na loja.")
                .addLoreLine("")
                .addLoreLine(" §fQuantia: §7{amount}".replace("{amount}", Toolchain.format(amount)))
                .addLoreLine("")
                .addLoreLine(" §aClique para ativar.");

        ItemStack itemStack = itemBuilder.build();

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        itemCompound.set("limite_amount", new NBTTagDouble(amount));
        nmsItem.setTag(itemCompound);
        CraftItemStack.asBukkitCopy(nmsItem);
        itemStack = (CraftItemStack.asBukkitCopy(nmsItem));

        if(sender instanceof Player) {
            LogPlugin.getInstance().getLogManager().registerEconomy(target, "SPAWNER_LIMITE_"+sender.getName().toUpperCase(), amount);
        } else {
            LogPlugin.getInstance().getLogManager().registerEconomy(target, "SPAWNER_LIMITE_CONSOLE", amount);
        }

        BukkitUtils.sendMessage(sender, "&eVocê enviou ao jogador &f"+target.getName()+ " &eum limite de compra.");
        target.getInventory().addItem(itemStack);
    }

    @Command(name = "spawner.stackmob", permission = "commands.spawner")
    public void handlerStackMobCommand(Context<CommandSender> context, Player target, double amount) {
        CommandSender sender = context.getSender();

        ItemBuilder itemBuilder = new ItemBuilder(SkullCreatorUtils.itemFromUrl("dc6bacd36ed60f533138e759c425946222b78eda6b616216f6dcc08e90d33e"))
                .setName("§aStack Mob")
                .addLoreLine("§7Aumente seu limite de mobs agrupados")
                .addLoreLine("§7gerados por um gerador.")
                .addLoreLine("")
                .addLoreLine(" §fQuantia: §7{amount}".replace("{amount}", Toolchain.format(amount)))
                .addLoreLine("")
                .addLoreLine(" §aClique para ativar.");

        ItemStack itemStack = itemBuilder.build();

        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        itemCompound.set("stackmob_amount", new NBTTagDouble(amount));
        nmsItem.setTag(itemCompound);
        CraftItemStack.asBukkitCopy(nmsItem);
        itemStack = (CraftItemStack.asBukkitCopy(nmsItem));

        if(sender instanceof Player) {
            LogPlugin.getInstance().getLogManager().registerEconomy(target, "SPAWNER_STACKMOB_"+sender.getName().toUpperCase(), amount);
        } else {
            LogPlugin.getInstance().getLogManager().registerEconomy(target, "SPAWNER_STACKMOB_CONSOLE", amount);
        }
        BukkitUtils.sendMessage(sender, "&eVocê enviou ao jogador &f"+target.getName()+ " &eum stack mob limite.");
        target.getInventory().addItem(itemStack);
    }

    @Command(name = "spawner.give", aliases = {"enviar"}, permission = "commands.spawner", usage = "spawner give <player> <type> <amount> <spawners>")
    public void handlerSpawnerCommand(Context<CommandSender> context, Player target, String type, double amount, double amount2) {
        CommandSender sender = context.getSender();

        if(amount < 0 && amount > 2300) {
            BukkitUtils.sendMessage(sender, "&cO valor não pode ser abaixo de 0.");
            return;
        }

        EntityType entityType = EntityType.fromName(type);
        if(entityType == null) {
        BukkitUtils.sendMessage(sender, "&cNão foi encontrado esse tipo de mob.");
            return;
        }

        TimeUpgrade timeUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getTime(0);
        XpUpgrade xpUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getXP(0);
        CapacityUpgrade capacityUpgrade = SpawnerPlugin.getInstance().getUpgradeManager().getCapacity(0);
        UpgradeModel upgradeModel = new UpgradeModel(timeUpgrade, xpUpgrade, capacityUpgrade);
        ItemStack itemStack = SpawnerPlugin.getInstance().getSpawnerManager().getItemStack(entityType, amount, upgradeModel);
        for(int i = 1; i < amount2; i++) {
            target.getInventory().addItem(itemStack);
        }
        target.getInventory().addItem(itemStack);

        if(sender instanceof Player) {
            LogPlugin.getInstance().getLogManager().registerSpawner(target, "SPAWNER_GIVE_"+sender.getName().toUpperCase(), entityType.getName(), amount);
        } else {
            LogPlugin.getInstance().getLogManager().registerSpawner(target, "SPAWNER_GIVE_CONSOLE", entityType.getName(), amount);
        }
        BukkitUtils.sendMessage(sender, "&aYay! Você enviou os spawners ao jogador.");

    }

}
