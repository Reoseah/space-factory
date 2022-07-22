package io.github.reoseah.spacefactory.common.block.entity;

import net.minecraft.text.Text;
import net.minecraft.util.Nameable;

public interface Renameable extends Nameable {
    void setCustomName(Text name);
}
