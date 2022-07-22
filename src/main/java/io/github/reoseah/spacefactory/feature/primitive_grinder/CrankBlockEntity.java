package io.github.reoseah.spacefactory.feature.primitive_grinder;

import io.github.reoseah.spacefactory.SpaceFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CrankBlockEntity extends BlockEntity {
    public int ticksToRotate = 0;
    public float angle = 0;

    public CrankBlockEntity(BlockPos pos, BlockState state) {
        super(SpaceFactory.BlockEntityTypes.CRANK, pos, state);
    }

    public static void tick(World world, @SuppressWarnings("unused") BlockPos pos, @SuppressWarnings("unused") BlockState state, CrankBlockEntity be) {
        if (be.ticksToRotate > 0) {
            be.ticksToRotate--;
            be.angle += 3.6 / 180F * Math.PI;
            if (be.angle > 2 * Math.PI) {
                be.angle -= 2 * Math.PI;
            }
            BlockEntity under = world.getBlockEntity(pos.down());
            if (under instanceof PrimitiveGrinderBlockEntity grinder) {
                grinder.setBeingRotated(true);
            }
            be.markDirty();
        } else {
            BlockEntity under = world.getBlockEntity(pos.down());
            if (under instanceof PrimitiveGrinderBlockEntity grinder) {
                grinder.setBeingRotated(false);
            }
        }
    }

    public void onUse() {
        this.ticksToRotate = 10;
        if (!this.world.isClient()) {
            this.markDirty();
            ((ServerWorld) this.world).getChunkManager().markForUpdate(this.pos);
        }
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.ticksToRotate = tag.getInt("TicksToRotate");
        this.angle = tag.getFloat("Angle");
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("TicksToRotate", this.ticksToRotate);
        tag.putFloat("Angle", this.angle);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound tag = super.toInitialChunkDataNbt();
        this.writeNbt(tag);
        return tag;
    }
}
