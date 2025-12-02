package net.adinvas.prototype_pain.blocks;

import net.adinvas.prototype_pain.PrototypePain;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BrownCapBlock extends BushBlock {
    protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    public BrownCapBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return pState.isSolidRender(pLevel, pPos);
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        int artificialLight = pLevel.getBrightness(LightLayer.BLOCK,pPos);

        if (artificialLight>10&&pRandom.nextInt(4)==0){
            Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(pRandom);
            BlockPos targetPos = pPos.relative(dir);
            BlockState targetState = pLevel.getBlockState(targetPos);
            if (targetState.isAir()&&!pLevel.getBlockState(targetPos.relative(Direction.DOWN)).isAir()) {
                pLevel.setBlock(targetPos, ModBlocks.BROWN_CAP.get().defaultBlockState(), Block.UPDATE_ALL);
            }
        }
    }
}
