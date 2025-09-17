package com.gencounters.common.selection;

import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public class SelectionData {
    private BlockPos firstPos;
    private BlockPos secondPos;

    public void setFirst(BlockPos pos) {
        this.firstPos = pos;
    }

    public void setSecond(BlockPos pos) {
        this.secondPos = pos;
    }

    @Nullable
    public BlockPos getFirst() {
        return firstPos;
    }

    @Nullable
    public BlockPos getSecond() {
        return secondPos;
    }

    public boolean isComplete() {
        return firstPos != null && secondPos != null;
    }

    public void reset() {
        firstPos = null;
        secondPos = null;
    }
}
