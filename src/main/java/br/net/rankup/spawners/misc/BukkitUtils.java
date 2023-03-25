package br.net.rankup.spawners.misc;

import br.net.rankup.spawners.SpawnerPlugin;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.apache.commons.codec.binary.Base64;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;

import static java.lang.Double.parseDouble;
import static java.lang.Float.parseFloat;
import static org.apache.commons.lang3.Validate.notNull;

public class BukkitUtils {

    public static void sendMessage(CommandSender sender, String message) {
        message = message.replace("&", "§");

        if (sender instanceof ConsoleCommandSender) {
            String name = SpawnerPlugin.getInstance().getName();
            sender.sendMessage("§f[" + name + "] " + message);
            return;
        }

        sender.sendMessage(message);
    }

    public static void getLogger(String logType, String message) {
        String name = SpawnerPlugin.getInstance().getName();
        message = message.replace("&", "§");
        Bukkit.getConsoleSender().sendMessage("§f[" + logType.toUpperCase() + "] §f[" + name + "] §f" + message);
    }

    public static String serializeLocation(Location unserialized) {
        return unserialized.getWorld().getName() + "; " + unserialized.getX() + "; " + unserialized.getY() + "; " + unserialized.getZ() + "; " + unserialized
                .getYaw() + "; " + unserialized.getPitch();
    }

    public static Location deserializeLocation(String serialized) {
        String[] divPoints = serialized.split("; ");
        Location deserialized = new Location(Bukkit.getWorld(divPoints[0]), parseDouble(divPoints[1]), parseDouble(divPoints[2]), parseDouble(divPoints[3]));
        deserialized.setYaw(parseFloat(divPoints[4]));
        deserialized.setPitch(parseFloat(divPoints[5]));
        return deserialized;
    }

    public static ItemStack loadItemStack2(String item) {
        if (item == null || item.isEmpty()) {
            return new ItemStack(Material.AIR);
        }

        item = StringUtils.formatColors(item).replace("\\n", "\n");
        String[] split = item.split(" : ");
        String id = split[0].split(":")[0];

        ItemStack itemStack = new ItemStack(Material.getMaterial(Integer.parseInt(id)), 1);
        if (split[0].split(":").length > 1) {
            itemStack.setDurability((short) Integer.parseInt(split[0].split(":")[1]));
        }
        ItemMeta itemMeta = itemStack.getItemMeta();

        BookMeta book = itemMeta instanceof BookMeta ? ((BookMeta) itemMeta) : null;
        SkullMeta skull = itemMeta instanceof SkullMeta ? ((SkullMeta) itemMeta) : null;
        PotionMeta potion = itemMeta instanceof PotionMeta ? ((PotionMeta) itemMeta) : null;
        EnchantmentStorageMeta enchantment = itemMeta instanceof EnchantmentStorageMeta ? ((EnchantmentStorageMeta) itemMeta) : null;

        if (split.length > 1) {
            itemStack.setAmount(Math.min(Integer.parseInt(split[1]), 64));
        }

        List<String> lore = new ArrayList<>();
        for (int i = 2; i < split.length; i++) {
            String tag = split[i];

            if (tag.startsWith("nome>")) {
                itemMeta.setDisplayName(StringUtils.formatColors(tag.split(">")[1]));
            }

            else if (tag.startsWith("lore>")) {
                for (String loreString: tag.split(">")[1].split("\n")) {
                    lore.add(StringUtils.formatColors(loreString));
                }
            }

            else if (tag.startsWith("encantar>")) {
                for (String enchanted : tag.split(">")[1].split("\n")) {
                    if (enchantment != null) {
                        enchantment.addStoredEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), true);
                        continue;
                    }

                    itemMeta.addEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), true);
                }
            }


            else if (tag.startsWith("dono>") && skull != null) {
                skull.setOwner(tag.split(">")[1]);
            }

            else if (tag.startsWith("skin>") && skull != null) {
                String url = tag.split(">")[1];
                if (url != null && !url.isEmpty()) {
                    if (!url.startsWith("http://textures.minecraft.net/texture/")) {
                        url = "http://textures.minecraft.net/texture/" + url;
                    }
                    GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(url.getBytes()), null);
                    profile.getProperties().put("textures", new Property("textures", new String(Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes()))));
                    Field field = null;
                    try {
                        field = skull.getClass().getDeclaredField("profile");
                    } catch (NoSuchFieldException n) {
                        n.printStackTrace();
                    }
                    Objects.requireNonNull(field).setAccessible(true);
                    try {
                        field.set(skull, profile);
                    } catch (IllegalAccessException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    }
                }
            }

            else if (tag.startsWith("paginas>") && book != null) {
                book.setPages(tag.split(">")[1].split("\\{pular}"));
            }

            else if (tag.startsWith("autor>") && book != null) {
                book.setAuthor(tag.split(">")[1]);
            }

            else if (tag.startsWith("titulo>") && book != null) {
                book.setTitle(tag.split(">")[1]);
            }

            else if (tag.startsWith("efeito>") && potion != null) {
                for (String pe : tag.split(">")[1].split("\n")) {
                    potion.addCustomEffect(new PotionEffect(PotionEffectType.getByName(pe.split(":")[0]), Integer.parseInt(pe.split(":")[2]), Integer.parseInt(pe.split(":")[1])), false);
                }
            }

            else if (tag.startsWith("esconder>")) {
                String[] flags = tag.split(">")[1].split("\n");
                for (String flag : flags) {
                    if (flag.equalsIgnoreCase("TUDO")) {
                        itemMeta.addItemFlags(ItemFlag.values());
                        break;
                    } else {
                        itemMeta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
                    }
                }
            }
        }

        if (!lore.isEmpty()) {
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack loadItemStack(String item) {
        if (item == null || item.isEmpty()) {
            return new ItemStack(Material.AIR);
        }

        item = StringUtils.formatColors(item).replace("\\n", "\n");
        String[] split = item.split(" : ");
        String id = split[0].split(":")[0];

        ItemStack itemStack = new ItemStack(Integer.parseInt(id), 1);
        if (split[0].split(":").length > 1) {
            itemStack.setDurability((short) Integer.parseInt(split[0].split(":")[1]));
        }
        ItemMeta itemMeta = itemStack.getItemMeta();

        BookMeta book = itemMeta instanceof BookMeta ? ((BookMeta) itemMeta) : null;
        SkullMeta skull = itemMeta instanceof SkullMeta ? ((SkullMeta) itemMeta) : null;
        PotionMeta potion = itemMeta instanceof PotionMeta ? ((PotionMeta) itemMeta) : null;
        FireworkEffectMeta effect = itemMeta instanceof FireworkEffectMeta ? ((FireworkEffectMeta) itemMeta) : null;
        LeatherArmorMeta armor = itemMeta instanceof LeatherArmorMeta ? ((LeatherArmorMeta) itemMeta) : null;
        EnchantmentStorageMeta enchantment = itemMeta instanceof EnchantmentStorageMeta ? ((EnchantmentStorageMeta) itemMeta) : null;

        if (split.length > 1) {
            itemStack.setAmount(Math.min(Integer.parseInt(split[1]), 64));
        }

        List<String> lore = new ArrayList<>();
        for (int i = 2; i < split.length; i++) {
            String tag = split[i];

            if (tag.startsWith("nome>")) {
                itemMeta.setDisplayName(StringUtils.formatColors(tag.split(">")[1]));
            }

            else if (tag.startsWith("lore>")) {
                for (String loreString: tag.split(">")[1].split("\n")) {
                    lore.add(StringUtils.formatColors(loreString));
                }
            }

            else if (tag.startsWith("encantar>")) {
                for (String enchanted : tag.split(">")[1].split("\n")) {
                    if (enchantment != null) {
                        enchantment.addStoredEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), true);
                        continue;
                    }

                    itemMeta.addEnchant(Enchantment.getByName(enchanted.split(":")[0]), Integer.parseInt(enchanted.split(":")[1]), true);
                }
            }

            else if (tag.startsWith("pintar>") && (effect != null || armor != null)) {
                for (String color : tag.split(">")[1].split("\n")) {
                    if (color.split(":").length > 2) {
                        if (armor != null) {
                            armor.setColor(Color.fromRGB(Integer.parseInt(color.split(":")[0]), Integer.parseInt(color.split(":")[1]), Integer.parseInt(color.split(":")[2])));
                        } else if (effect != null) {
                            effect.setEffect(FireworkEffect.builder()
                                    .withColor(Color.fromRGB(Integer.parseInt(color.split(":")[0]), Integer.parseInt(color.split(":")[1]), Integer.parseInt(color.split(":")[2]))).build());
                        }
                        continue;
                    }
                }
            }

            else if (tag.startsWith("dono>") && skull != null) {
                skull.setOwner(tag.split(">")[1]);
            }

            else if (tag.startsWith("skin>") && skull != null) {
                String url = tag.split(">")[1];
                if (url != null && !url.isEmpty()) {
                    if (!url.startsWith("http://textures.minecraft.net/texture/")) {
                        url = "http://textures.minecraft.net/texture/" + url;
                    }
                    GameProfile profile = new GameProfile(UUID.nameUUIDFromBytes(url.getBytes()), null);
                    profile.getProperties().put("textures", new Property("textures", new String(Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes()))));
                    Field field = null;
                    try {
                        field = skull.getClass().getDeclaredField("profile");
                    } catch (NoSuchFieldException n) {
                        n.printStackTrace();
                    }
                    Objects.requireNonNull(field).setAccessible(true);
                    try {
                        field.set(skull, profile);
                    } catch (IllegalAccessException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    }
                }
            }

            else if (tag.startsWith("paginas>") && book != null) {
                book.setPages(tag.split(">")[1].split("\\{pular}"));
            }

            else if (tag.startsWith("autor>") && book != null) {
                book.setAuthor(tag.split(">")[1]);
            }

            else if (tag.startsWith("titulo>") && book != null) {
                book.setTitle(tag.split(">")[1]);
            }

            else if (tag.startsWith("efeito>") && potion != null) {
                for (String pe : tag.split(">")[1].split("\n")) {
                    potion.addCustomEffect(new PotionEffect(PotionEffectType.getByName(pe.split(":")[0]), Integer.parseInt(pe.split(":")[2]), Integer.parseInt(pe.split(":")[1])), false);
                }
            }

            else if (tag.startsWith("esconder>")) {
                String[] flags = tag.split(">")[1].split("\n");
                for (String flag : flags) {
                    if (flag.equalsIgnoreCase("TUDO")) {
                        itemMeta.addItemFlags(ItemFlag.values());
                        break;
                    } else {
                        itemMeta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
                    }
                }
            }
        }

        if (!lore.isEmpty()) {
            itemMeta.setLore(lore);
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static String saveItemStack(ItemStack item) {
        StringBuilder stringBuilder = new StringBuilder(item.getType().getId() + (item.getDurability() != 0 ? ":" + item.getDurability() : "") + " : " + item.getAmount());
        ItemMeta itemMeta = item.getItemMeta();

        BookMeta book = itemMeta instanceof BookMeta ? ((BookMeta) itemMeta) : null;
        SkullMeta skull = itemMeta instanceof SkullMeta ? ((SkullMeta) itemMeta) : null;
        PotionMeta potion = itemMeta instanceof PotionMeta ? ((PotionMeta) itemMeta) : null;
        FireworkEffectMeta effect = itemMeta instanceof FireworkEffectMeta ? ((FireworkEffectMeta) itemMeta) : null;
        LeatherArmorMeta armor = itemMeta instanceof LeatherArmorMeta ? ((LeatherArmorMeta) itemMeta) : null;
        EnchantmentStorageMeta enchantment = itemMeta instanceof EnchantmentStorageMeta ? ((EnchantmentStorageMeta) itemMeta) : null;

        if (itemMeta.hasDisplayName()) {
            stringBuilder.append(" : nome>").append(StringUtils.deformatColors(itemMeta.getDisplayName()));
        }

        if (itemMeta.hasLore()) {
            stringBuilder.append(" : lore>");
            for (int i = 0; i < itemMeta.getLore().size(); i++) {
                String line = itemMeta.getLore().get(i);
                stringBuilder.append(line).append(i + 1 == itemMeta.getLore().size() ? "" : "\n");
            }
        }

        if (itemMeta.hasEnchants() || (enchantment != null && enchantment.hasStoredEnchants())) {
            stringBuilder.append(" : encantar>");
            int size = 0;
            for (Map.Entry<Enchantment, Integer> entry : (enchantment != null ? enchantment.getStoredEnchants() : itemMeta.getEnchants()).entrySet()) {
                int level = entry.getValue();
                String name = entry.getKey().getName();
                stringBuilder.append(name).append(":").append(level).append(++size == (enchantment != null ? enchantment.getStoredEnchants() : itemMeta.getEnchants()).size() ? "" : "\n");
            }
        }

        if ((effect != null && effect.hasEffect() && !effect.getEffect().getColors().isEmpty()) || (armor != null && armor.getColor() != null)) {
            Color color = effect != null ? effect.getEffect().getColors().get(0) : armor.getColor();
            stringBuilder.append(" : pintar>").append(color.getRed()).append(":").append(color.getGreen()).append(":").append(color.getBlue());
        }

        if (skull != null && !skull.getOwner().isEmpty()) {
            stringBuilder.append(" : dono>").append(skull.getOwner());
        }

        if (book != null && book.hasAuthor()) {
            stringBuilder.append(" : autor>").append(book.getAuthor());
        }

        if (book != null && book.hasTitle()) {
            stringBuilder.append(" : titulo>").append(book.getTitle());
        }

        if (book != null && book.hasPages()) {
            stringBuilder.append(" : paginas>").append(StringUtils.join(book.getPages(), "{pular}"));
        }

        if (potion != null && potion.hasCustomEffects()) {
            stringBuilder.append(" : efeito>");
            int size = 0;
            for (PotionEffect pe : potion.getCustomEffects()) {
                stringBuilder.append(pe.getType().getName()).append(":").append(pe.getAmplifier()).append(":").append(pe.getDuration()).append(++size == potion.getCustomEffects().size() ? "" : "\n");
            }
        }

        for (ItemFlag flag : itemMeta.getItemFlags()) {
            stringBuilder.append(" : esconder>").append(flag.name());
        }

        return StringUtils.deformatColors(stringBuilder.toString()).replace("\n", "\\n");
    }

    public static void sendTitle(Player player, String titleLine1, String titleLine2) {
        titleLine1 = titleLine1.replace("&", "§").replace("{player}", player.getName());
        titleLine2 = titleLine2.replace("&", "§").replace("{player}", player.getName());
        player.sendTitle(titleLine1, titleLine2);
    }

    public static void sendActionBar(String text, Player p) {
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + text + "\"}"), (byte)2);
        ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
    }

    public static ItemStack deserializeItemStack(String data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream((new BigInteger(data, 32)).toByteArray());
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        ItemStack itemStack = null;

        try {
            Class<?> nbtTagCompoundClass = getNMSClass("NBTTagCompound");
            Class<?> nmsItemStackClass = getNMSClass("ItemStack");
            Object nbtTagCompound = getNMSClass("NBTCompressedStreamTools").getMethod("a", DataInputStream.class).invoke((Object)null, dataInputStream);
            Object craftItemStack = nmsItemStackClass.getMethod("createStack", nbtTagCompoundClass).invoke((Object)null, nbtTagCompound);
            itemStack = (ItemStack)getOBClass("inventory.CraftItemStack").getMethod("asBukkitCopy", nmsItemStackClass).invoke((Object)null, craftItemStack);
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return itemStack;
    }

    public static String serializeItemStack(ItemStack item) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(outputStream);

        try {
            Class<?> nbtTagCompoundClass = getNMSClass("NBTTagCompound");
            Constructor<?> nbtTagCompoundConstructor = nbtTagCompoundClass.getConstructor();
            Object nbtTagCompound = nbtTagCompoundConstructor.newInstance();
            Object nmsItemStack = getOBClass("inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke((Object)null, item);
            getNMSClass("ItemStack").getMethod("save", nbtTagCompoundClass).invoke(nmsItemStack, nbtTagCompound);
            getNMSClass("NBTCompressedStreamTools").getMethod("a", nbtTagCompoundClass, DataOutput.class).invoke((Object)null, nbtTagCompound, dataOutput);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | SecurityException var7) {
            var7.printStackTrace();
        }

        return (new BigInteger(1, outputStream.toByteArray())).toString(32);
    }

    private static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }
    private static Class<?> getOBClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
        } catch (ClassNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static ItemStack setNBT(ItemStack var1, String var2, String var3) {
        net.minecraft.server.v1_8_R3.ItemStack var4 = CraftItemStack.asNMSCopy(var1);
        if (var4 == null) {
            return null;
        } else {
            NBTTagCompound var5 = var4.getTag();
            if (var5 == null) {
                var5 = new NBTTagCompound();
                var4.setTag(var5);
                var5 = var4.getTag();
            }

            var5.setString(var2, var3);
            var4.setTag(var5);
            return CraftItemStack.asBukkitCopy(var4);
        }
    }

    public static String getNBT(ItemStack var1, String var2) {
        net.minecraft.server.v1_8_R3.ItemStack var3 = CraftItemStack.asNMSCopy(var1);
        NBTTagCompound var4 = var3.getTag();
        return var4 != null && var4.hasKey(var2) ? var4.getString(var2) : null;
    }

    public static boolean hasNBT(ItemStack var1, String var2) {
        if (var1 != null && var1.getType() != Material.AIR) {
            net.minecraft.server.v1_8_R3.ItemStack var3 = CraftItemStack.asNMSCopy(var1);
            NBTTagCompound var4 = var3.getTag();
            return var4 != null && var4.hasKey(var2);
        } else {
            return false;
        }
    }

    public static ItemStack removeNBT(ItemStack var1, String var2) {
        net.minecraft.server.v1_8_R3.ItemStack var3 = CraftItemStack.asNMSCopy(var1);
        NBTTagCompound var4 = var3.getTag();
        if (var4 == null) {
            var4 = new NBTTagCompound();
            var3.setTag(var4);
            var4 = var3.getTag();
        }

        if (var4.hasKey(var2)) {
            var4.remove(var2);
        }

        var3.setTag(var4);
        return CraftItemStack.asBukkitCopy(var3);
    }

}
