package de.beta.spicrate.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class ItemBuilder {

    private Material material;
    private int amount, durability, subID;

    private List<Enchantment> enchantments;
    private List<Integer> enchantmentLevels;

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    public ItemBuilder( Material material ) {
        this.material = material;
        this.amount = 1;
        this.durability = Integer.MAX_VALUE;

        this.enchantmentLevels = new ArrayList();
        this.enchantments = new ArrayList();

        this.itemStack = new ItemStack( material, this.amount, (short) this.subID );
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public ItemBuilder( Material material, int amount, short subID ) {
        this.material = material;
        this.amount = amount;
        this.subID = subID;
        this.durability = Integer.MAX_VALUE;

        this.enchantmentLevels = new ArrayList();
        this.enchantments = new ArrayList();

        this.itemStack = new ItemStack( material, this.amount, (short) this.subID );
        this.itemMeta = this.itemStack.getItemMeta();
    }

    public static ItemBuilder fromItem( ItemStack itemStack ) {
        return new ItemBuilder( itemStack.getType() )
                .setAmount( itemStack.getAmount() )
                .setDisplayName( itemStack.getItemMeta().getDisplayName() )
                .setDurability( itemStack.getDurability() )
                .setLore( itemStack.getItemMeta().hasLore() ? itemStack.getItemMeta().getLore().toArray( new String[ 0 ] ) : Arrays.asList( "" ).toArray( new String[ 0 ] ) )
                .setSubID( itemStack.getData().getData() );
    }

    public static ItemBuilder fromHead( ItemStack itemStack ) {
        return new ItemBuilder( itemStack.getType() )
                .setAmount( itemStack.getAmount() )
                .setDisplayName( itemStack.getItemMeta().getDisplayName() )
                .setDurability( itemStack.getDurability() )
                .setLore( itemStack.getItemMeta().getLore().toArray( new String[ 0 ] ) )
                .setSubID( itemStack.getData().getData() )
                .setHeadOwner( ( (SkullMeta) itemStack.getItemMeta() ).getOwner() );
    }

    public static ItemBuilder fromLeather( ItemStack itemStack ) {
        return new ItemBuilder( itemStack.getType() )
                .setAmount( itemStack.getAmount() )
                .setDisplayName( itemStack.getItemMeta().getDisplayName() )
                .setDurability( itemStack.getDurability() )
                .setLore( itemStack.getItemMeta().getLore().toArray().toString() )
                .setSubID( itemStack.getData().getData() )
                .setLeatherColor( ( (LeatherArmorMeta) itemStack.getItemMeta() ).getColor() );
    }

    public ItemStack build() {
        this.itemStack = new ItemStack(this.material, this.amount, (short) this.subID);
        this.itemStack.setItemMeta(this.itemMeta);

        if (!this.enchantments.isEmpty())
            for (int i = 0; i < this.enchantments.size(); i++)
                this.itemStack.addUnsafeEnchantment( this.enchantments.get(i), this.enchantmentLevels.get(i));
        return this.itemStack;
    }

    public ItemBuilder setDisplayName(String displayName) {
        this.itemMeta.setDisplayName(displayName);
        return this;
    }

    public ItemBuilder setLore(String... lore) {
        this.itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        this.itemMeta.setLore(lore);
        return this;
    }

    public ItemBuilder appendLore(List<String> append) {
        List<String> lore = this.itemMeta.getLore();
        for (String toAppend : append) {
            lore.add(ChatMessage.color(toAppend));
        }
        return setLore(lore);
    }

    public ItemBuilder setUnbreakable(boolean boo) {
        itemMeta.setUnbreakable(boo);
        return this;
    }

    public ItemBuilder setSubID(int subID) {
        this.subID = subID;
        return this;
    }

    public ItemBuilder setDurability(int durability) {
        this.itemStack.setDurability((short) durability);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag... itemFlags) {
        this.itemMeta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        this.enchantments.add(enchantment);
        this.enchantmentLevels.add(Integer.valueOf( level ));
        return this;
    }

    public ItemBuilder setHeadOwner(String name) {
        ((SkullMeta) this.itemMeta).setOwner(name);
        return this;
    }

    public ItemBuilder setLeatherColor(Color color) {
        ((LeatherArmorMeta)this.itemMeta).setColor(color);
        return this;
    }

    public ItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder setLocalizedName(String value) {
        this.itemMeta.setLocalizedName(value);
        return this;
    }

}
