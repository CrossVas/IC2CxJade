package dev.crossvas.jadexic2c.providers;

import dev.crossvas.jadexic2c.base.JadeCommonHandler;
import dev.crossvas.jadexic2c.base.interfaces.IInfoProvider;
import dev.crossvas.jadexic2c.base.interfaces.IJadeHelper;
import ic2.api.energy.EnergyNet;
import ic2.core.block.machines.tiles.hv.RocketMinerTileEntity;
import ic2.core.block.machines.tiles.lv.MinerTileEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MinerInfo implements IInfoProvider {

    public static final MinerInfo THIS = new MinerInfo();

    @Override
    public void addInfo(IJadeHelper helper, BlockEntity blockEntity, Player player) {
        if (blockEntity instanceof MinerTileEntity miner) {
            defaultText(helper, "ic2.probe.eu.tier.name", EnergyNet.INSTANCE.getDisplayTier(miner.getTier()));
            defaultText(helper, "ic2.probe.eu.max_in.name", miner.getMaxInput());
            defaultText(helper, "ic2.probe.eu.usage.name", miner.getEnergyUsage());

            float progress = miner.getProgress();
            boolean isStuck = miner.isStuck();
            boolean isOperating = miner.isOperating();

            if (miner instanceof RocketMinerTileEntity rocketMiner) {
                RocketMinerTileEntity.MinerState state = rocketMiner.state;
                defaultText(helper, state.getState());
                JadeCommonHandler.addTankInfo(helper, rocketMiner);
            } else {
                defaultText(helper, isStuck ? "ic2.probe.miner.stuck.name" : isOperating ? "ic2.probe.miner.mining.name" : "ic2.probe.miner.retracting.name");
            }

            text(helper, Component.translatable("ic2.probe.miner.progress.name", miner.getPipeTip().getY()).withStyle(ChatFormatting.GOLD));
            if (!isStuck && progress > 0) {
                int scaledOp = (int) Math.min(6.0E7F, progress);
                int scaledMaxOp = (int) Math.min(6.0E7F, miner.getMaxProgress());
                bar(helper, scaledOp, scaledMaxOp, Component.translatable("ic2.probe.progress.full.name", scaledOp, scaledMaxOp), -16733185);
            }
        }
    }
}
