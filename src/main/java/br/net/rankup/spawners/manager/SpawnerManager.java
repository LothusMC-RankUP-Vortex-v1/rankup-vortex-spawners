package br.net.rankup.spawners.manager;

import br.net.rankup.spawners.misc.ItemBuilder;
import br.net.rankup.spawners.misc.Toolchain;
import br.net.rankup.spawners.misc.TranslateMob;
import br.net.rankup.spawners.model.spawner.SpawnerModel;
import br.net.rankup.spawners.model.spawner.UpgradeModel;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import java.util.HashMap;

public class SpawnerManager {

    private HashMap<Location, SpawnerModel> spawners;

    public void load() {
        spawners = new HashMap<>();
    }

    public HashMap<Location, SpawnerModel> getSpawners() {
        return spawners;
    }

    public ItemStack getItemStack(EntityType entityType, double amount, UpgradeModel upgradeModel) {
        ItemBuilder itemBuilder = new ItemBuilder(Material.MOB_SPAWNER)
                .setName("§aGerador")
                .addLoreLine("§fTipo: §7"+TranslateMob.traslateName(entityType.toString()))
                .addLoreLine("§fQuantidade: §7"+Toolchain.format(amount))
                .addLoreLine("")
                .addLoreLine("§eUpgrades:")
                .addLoreLine(" §fVelocidade: §7"+upgradeModel.getTimeUpgrade().getValue()+"s")
                .addLoreLine(" §fXP p/ mob: §7"+upgradeModel.getXpUpgrade().getValue()+"x")
                .addLoreLine(" §fCapacidade: §7"+Toolchain.format(upgradeModel.getCapacityUpgrade().getValue())+" geradores")
                .addLoreLine("")
                .addLoreLine("§aClique no chão para colocar.");

        ItemStack itemStack = itemBuilder.build();
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
        itemCompound.set("spawner_type", new NBTTagString(entityType.getName()));
        itemCompound.set("spawner_amount", new NBTTagDouble(amount));
        itemCompound.set("upgrade_time", new NBTTagInt(upgradeModel.getTimeUpgrade().getIndentifier()));
        itemCompound.set("upgrade_xp", new NBTTagInt(upgradeModel.getXpUpgrade().getIndentifier()));
        itemCompound.set("upgrade_capacity", new NBTTagInt(upgradeModel.getCapacityUpgrade().getIndentifier()));
        nmsItem.setTag(itemCompound);
        CraftItemStack.asBukkitCopy(nmsItem);
        itemStack = (CraftItemStack.asBukkitCopy(nmsItem));

        return itemStack;
    }

    public void add(SpawnerModel spawnerModel) {
        if(!spawners.containsKey(spawnerModel.getLocation())) {
            this.spawners.put(spawnerModel.getLocation(), spawnerModel);
        }
    }

    public void remove(SpawnerModel spawnerModel) {
        if(spawners.containsKey(spawnerModel.getLocation())) {
            this.spawners.remove(spawnerModel.getLocation());
        }
    }
}

