package net.eidee.minecraft.perks.util;

import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class BlockPosUtil
{
    private BlockPosUtil()
    {
    }

    public static Iterator< BlockPos > getSpiralPositions( BlockPos base, int n )
    {
        int x = base.getX();
        int y = base.getY();
        int z = base.getZ();
        int max = ( ( n << 1 ) + 1 ) * ( ( n << 1 ) + 1 );

        return new AbstractIterator< BlockPos >()
        {
            private final BlockPos.Mutable pos = new BlockPos.Mutable();
            private Direction direction = Direction.NORTH;
            private int posCount = 0;
            private int offsetX = 0;
            private int offsetZ = 0;
            private int rotationTimer = 1;
            private int timerBase = 1;
            private int baseUpTimer = 2;

            protected BlockPos computeNext()
            {
                if ( posCount++ < max )
                {
                    pos.setPos( x + offsetX, y, z + offsetZ );

                    offsetX += direction.getXOffset();
                    offsetZ += direction.getZOffset();

                    if ( --rotationTimer == 0 )
                    {
                        direction = direction.rotateY();
                        if ( --baseUpTimer == 0 )
                        {
                            baseUpTimer = 2;
                            timerBase++;
                        }
                        rotationTimer = timerBase;
                    }

                    return pos;
                }
                else
                {
                    return endOfData();
                }
            }
        };
    }
}
