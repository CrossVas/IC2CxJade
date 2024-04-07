package dev.crossvas.jadexic2c.info;

import dev.crossvas.jadexic2c.JadeIC2CPluginHandler;
import dev.crossvas.jadexic2c.helpers.BarHelper;
import dev.crossvas.jadexic2c.helpers.IHelper;
import dev.crossvas.jadexic2c.helpers.TankHelper;
import dev.crossvas.jadexic2c.helpers.TextHelper;
import ic2.api.energy.EnergyNet;
import ic2.core.block.base.tiles.BaseElectricTileEntity;
import ic2.core.block.machines.tiles.hv.RocketMinerTileEntity;
import ic2.core.block.machines.tiles.lv.MinerTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum MinerInfoProvider implements IHelper<BlockEntity> {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (!shouldAddInfo(blockAccessor, "MinerInfo")) {
            return;
        }

        CompoundTag tag = getData(blockAccessor, "MinerInfo");
        if (blockAccessor.getBlockEntity() instanceof MinerTileEntity miner) {
            TextHelper.text(iTooltip, "ic2.probe.eu.tier.name", EnergyNet.INSTANCE.getDisplayTier(miner.getTier()));
            TextHelper.text(iTooltip, "ic2.probe.eu.max_in.name", miner.getMaxInput());
            TextHelper.text(iTooltip, "ic2.probe.eu.usage.name", miner.getEnergyUsage());

            float progress = tag.getFloat("progress");
            boolean isStuck = tag.getBoolean("isStuck");
            int minerState = tag.getInt("minerState");
            boolean isOperating = tag.getBoolean("isOperating");

            if (miner instanceof RocketMinerTileEntity) {
                RocketMinerTileEntity.MinerState state = RocketMinerTileEntity.MinerState.byId(minerState);
                TextHelper.text(iTooltip, state.getState().plainCopy().withStyle(ChatFormatting.WHITE));
                TankHelper.addClientTankFromTag(iTooltip, blockAccessor);
            } else {
                TextHelper.text(iTooltip, isStuck ? "ic2.probe.miner.stuck.name" : isOperating ? "ic2.probe.miner.mining.name" : "ic2.probe.miner.retracting.name");
            }

            TextHelper.text(iTooltip, Component.translatable("ic2.probe.miner.progress.name", miner.getPipeTip().getY()).withStyle(ChatFormatting.GOLD));

            if (!isStuck && progress > 0) {
                float scaledOp = Math.min(6.0E7F, progress);
                float scaledMaxOp = Math.min(6.0E7F, miner.getMaxProgress());
                BarHelper.bar(iTooltip, (int) scaledOp, (int) scaledMaxOp, Component.translatable("ic2.probe.progress.full.name", (int) scaledOp, (int) scaledMaxOp), -16733185);
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
        if (blockEntity instanceof BaseElectricTileEntity tile) {
            CompoundTag tag = new CompoundTag();
            if (tile instanceof RocketMinerTileEntity rocket) {
                tag.putInt("minerState", rocket.state.ordinal());
                TankHelper.loadTankData(rocket.tank, compoundTag);
            } else if (tile instanceof MinerTileEntity miner) {
                tag.putFloat("progress", miner.getProgress());
                tag.putBoolean("isStuck", miner.isStuck());
                tag.putBoolean("isOperating", miner.isOperating());
            }
            compoundTag.put("MinerInfo", tag);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return JadeIC2CPluginHandler.EU_READER_INFO;
    }
}
