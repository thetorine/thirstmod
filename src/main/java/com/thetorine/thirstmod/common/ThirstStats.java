package com.thetorine.thirstmod.common;

/*
    Author: tarun1998 (http://www.minecraftforum.net/members/tarun1998)
    Date: 21/07/2017
    Handles all thirst bar related logic.
*/

import com.thetorine.thirstmod.network.NetworkManager;
import com.thetorine.thirstmod.network.PacketThirstStats;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.biome.BiomeDesert;

public class ThirstStats {

    public int thirstLevel;
    public float saturation;
    public float exhaustion;
    public int thirstTimer;

    public int movementSpeed;

    public transient int lastThirstLevel;
    public transient float lastSaturation;

    public transient DamageSource thirstDmgSource = new DamageThirst();

    public ThirstStats() {
        lastThirstLevel = -1; // Trigger a refresh when this class is loaded.
        resetStats();
    }

    public void update(EntityPlayer player) {
        // Only send packet update if the thirst level or saturation has changed.
        if (lastThirstLevel != thirstLevel || lastSaturation != saturation) {
            NetworkManager.getNetworkWrapper().sendTo(new PacketThirstStats(this), (EntityPlayerMP) player);
            lastThirstLevel = thirstLevel;
            lastSaturation = saturation;
        }

        if (exhaustion > 5.0f) {
            exhaustion -= 5.0f;
            if (saturation > 0.0f) {
                saturation = Math.max(saturation - 1.0f, 0);
            } else if (player.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
                thirstLevel = Math.max(thirstLevel - 1, 0);
            }
        }

        if (thirstLevel <= 6) {
            player.setSprinting(false);
            if (thirstLevel == 0) {
                thirstTimer++;
                if (thirstTimer > 200) {
                    if (player.getHealth() > 10.0f || player.world.getDifficulty() == EnumDifficulty.HARD || (player.world.getDifficulty() == EnumDifficulty.NORMAL && player.getHealth() > 1.0f)) {
                        thirstTimer = 0;
                        player.attackEntityFrom(this.thirstDmgSource, 1);
                    }
                }
            }
        }

        int ms = player.isRiding() ? 10 : movementSpeed;
        float exhaustMultiplier = player.world.isDaytime() ? 1.0f : 0.9f;
        exhaustMultiplier *= player.world.getBiomeForCoordsBody(player.getPosition()) instanceof BiomeDesert ? 2.0f : 1.0f;

        if (player.isInsideOfMaterial(Material.WATER) || player.isInWater()) {
            addExhaustion(0.03f * ms * 0.003f * exhaustMultiplier);
        } else if (player.onGround) {
            if (player.isSprinting()) {
                addExhaustion(0.06f * ms * 0.018f * exhaustMultiplier);
            } else {
                addExhaustion(0.01f * ms * 0.018f * exhaustMultiplier);
            }
        } else if (!player.isRiding()) { // must be in the air/jumping
            if (player.isSprinting()) {
                addExhaustion(0.06f * ms * 0.025f * exhaustMultiplier);
            } else {
                addExhaustion(0.01f * ms * 0.025f * exhaustMultiplier);
            }
        }
    }

    public void addStats(int heal, float sat) {
        thirstLevel = Math.min(thirstLevel + heal, 20);
        saturation = Math.min(saturation + (heal * sat * 2.0f), thirstLevel);
    }

    public void addExhaustion(float exhaustion) {
        this.exhaustion = Math.min(this.exhaustion + exhaustion, 40.0f);
    }

    public boolean canDrink() {
        return thirstLevel < 20;
    }

    public int getMovementSpeed(EntityPlayer player) {
        double x = player.posX - player.prevPosX;
        double y = player.posY - player.prevPosY;
        double z = player.posZ - player.prevPosZ;
        return (int) Math.round(100.0d * Math.sqrt(x*x + y*y + z*z));
    }

    public void resetStats() {
        thirstLevel = 20;
        saturation = 5f;
        exhaustion = 0f;
    }

    public static class DamageThirst extends DamageSource {
        public DamageThirst() {
            super("thirst");
            setDamageBypassesArmor();
            setDamageIsAbsolute();
        }

        @Override
        public ITextComponent getDeathMessage(EntityLivingBase entity) {
            if(entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer)entity;
                return new TextComponentString(player.getDisplayName() + "'s body is now made up of 0% water!");
            }
            return super.getDeathMessage(entity);
        }
    }
}
