package com.thetorine.thirstmod.common.utils;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;

public class DamageThirst extends DamageSource {

	public DamageThirst(String par1Str) {
		super(par1Str);
	}

	@Override
	public IChatComponent func_151519_b(EntityLivingBase player) {
		String s1 = ((EntityPlayer) player).getDisplayName() + "'s body is now made up of 0% water!";
		return new ChatComponentText(s1);
	}
}
