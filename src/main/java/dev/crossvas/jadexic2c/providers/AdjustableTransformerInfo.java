package dev.crossvas.jadexic2c.providers;

import dev.crossvas.jadexic2c.base.interfaces.IInfoProvider;
import dev.crossvas.jadexic2c.base.interfaces.IJadeHelper;
import dev.crossvas.jadexic2c.utils.EnergyContainer;
import ic2.core.block.wiring.tile.TileEntityAdjustableTransformer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class AdjustableTransformerInfo implements IInfoProvider {

    public static final AdjustableTransformerInfo THIS = new AdjustableTransformerInfo();

    @Override
    public void addInfo(IJadeHelper helper, TileEntity blockEntity, EntityPlayer player) {
        if (blockEntity instanceof TileEntityAdjustableTransformer) {
            TileEntityAdjustableTransformer transformer = (TileEntityAdjustableTransformer) blockEntity;
            int energyPacket = transformer.energyPacket;
            int packets = transformer.packetCount;
            text(helper, translatable("probe.energy.tier", transformer.sinkTier));
            text(helper, translatable("probe.energy.output.max", energyPacket));
            text(helper, translatable("probe.packet.tick", packets));
            EnergyContainer container = EnergyContainer.getContainer(transformer);
            addStats(helper, player, () -> addCableOut(helper, container));
        }
    }
}
