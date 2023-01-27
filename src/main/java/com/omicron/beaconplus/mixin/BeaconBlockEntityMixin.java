package com.omicron.beaconplus.mixin;

import com.omicron.beaconplus.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(BeaconBlockEntity.class)
public abstract class BeaconBlockEntityMixin extends BlockEntity implements MenuProvider {

    private HashMap<Block, Integer> MAP = new HashMap<>();

    public BeaconBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(at = @At("HEAD"), method = "applyEffects", cancellable = true)
    private static void applyEffects(Level level, BlockPos pos, int lvl, MobEffect primaryEffect, MobEffect secondaryEffect, CallbackInfo ci) {
        if (!level.isClientSide && primaryEffect != null) {
            HashMap<Block, Integer> blockMap = new HashMap<>();

            int i = 0;

            for(int j = 1; j <= 4; i = j++) {
                HashMap<Block, Integer> blockMapTemp = new HashMap<>();
                int k = pos.getY() - j;
                if (k < level.getMinBuildHeight()) {
                    break;
                }

                boolean flag = true;

                for(int l = pos.getX() - j; l <= pos.getX() + j && flag; ++l) {
                    for(int i1 = pos.getZ() - j; i1 <= pos.getZ() + j; ++i1) {
                        if (!level.getBlockState(new BlockPos(l, k, i1)).is(BlockTags.BEACON_BASE_BLOCKS)) {
                            flag = false;
                            break;
                        }
                        else
                        {
                            Block tempBlock = level.getBlockState(new BlockPos(l, k, i1)).getBlock();
                            if(blockMapTemp.containsKey(tempBlock))
                                blockMapTemp.put(tempBlock, blockMapTemp.get(tempBlock) + 1);
                            else
                                blockMapTemp.put(tempBlock, 1);
                        }
                    }
                }

                if (!flag) {
                    break;
                }
                for(Map.Entry<Block, Integer> entry : blockMapTemp.entrySet())
                {
                    if(blockMap.containsKey(entry.getKey()))
                    {
                        blockMap.put(entry.getKey(), entry.getValue() + blockMap.get(entry.getKey()));
                    }
                    else
                    {
                        blockMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }




            double d0 = 0;
            if(lvl == 1)
                d0 = Config.TIER_1.get();
            if(lvl == 2)
                d0 = Config.TIER_2.get();
            if(lvl == 3)
                d0 = Config.TIER_3.get();
            if(lvl == 4)
                d0 = Config.TIER_4.get();

            for(Map.Entry<Block, Integer> entry : blockMap.entrySet())
            {
                if(Config.BLOCK_VALUES.containsKey(entry.getKey()))
                {
                    d0 += Config.BLOCK_VALUES.get(entry.getKey()) * (double) entry.getValue();
                }
            }

            i = 0;
            if (lvl >= 4 && primaryEffect == secondaryEffect) {
                i = 1;
            }

            int j = (9 + lvl * 2) * 20;
            AABB aabb = (new AABB(pos)).inflate(d0).expandTowards(0.0D, (double)level.getHeight(), 0.0D);
            if(Config.SHOULD_WORT_AT_ALL_Y_LEVELS.get())
                aabb = aabb.setMinY(-64.0D);
            List<Player> list = level.getEntitiesOfClass(Player.class, aabb);
            for(Player player : list) {
                player.addEffect(new MobEffectInstance(primaryEffect, j, i, true, true));
            }

            if (lvl >= 4 && primaryEffect != secondaryEffect && secondaryEffect != null) {
                for(Player player1 : list) {
                    player1.addEffect(new MobEffectInstance(secondaryEffect, j, 0, true, true));
                }
            }

        }
        ci.cancel();
    }
}
