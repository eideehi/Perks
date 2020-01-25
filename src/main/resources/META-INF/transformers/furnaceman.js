/*
 * MIT License
 *
 * Copyright (c) 2020 EideeHi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

function initializeCoreMod()
{
    var ASMAPI = Java.type( "net.minecraftforge.coremod.api.ASMAPI" );
    var Opcodes = Java.type( "org.objectweb.asm.Opcodes" );
    var FieldInsnNode = Java.type( "org.objectweb.asm.tree.FieldInsnNode" );
    var MethodInsnNode = Java.type( "org.objectweb.asm.tree.MethodInsnNode" );
    var VarInsnNode = Java.type( "org.objectweb.asm.tree.VarInsnNode" );

    var cookTime = ASMAPI.mapField( "field_214020_l" );
    var cookTimeTotal = ASMAPI.mapField( "field_214021_m" );

    return {
        "Furnaceman_01": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.tileentity.AbstractFurnaceTileEntity",
                "methodName": "func_73660_a",
                "methodDesc": "()V"
            },
            "transformer": function ( methodNode )
            {
                /*
                |   public void tick() {
                |       ...
                |       if (this.isBurning() && this.canSmelt(irecipe)) {
                |           ++this.cookTime;
                | +         this.cookTime = net.eidee.minecraft.perks.hooks.asm.FurnacemanAsmHooks.calcCookTime(this, this.cookTime, this.cookTimeTotal);
                |           if (this.cookTime == this.cookTimeTotal) {
                |       ...
                 */
                var instructions = methodNode.instructions;
                for ( var i = 0; i < instructions.size(); i++ )
                {
                    var node = instructions.get( i );
                    if ( node.getOpcode() !== Opcodes.GETFIELD ||
                         node.owner !== "net/minecraft/tileentity/AbstractFurnaceTileEntity" ||
                         node.name !== cookTime ||
                         node.desc !== "I" )
                    {
                        continue;
                    }
                    node = instructions.get( i + 1 );
                    if ( node.getOpcode() !== Opcodes.ICONST_1 )
                    {
                        continue;
                    }
                    node = instructions.get( i + 2 );
                    if ( node.getOpcode() !== Opcodes.IADD )
                    {
                        continue;
                    }
                    node = instructions.get( i + 3 );
                    if ( node.getOpcode() !== Opcodes.PUTFIELD ||
                         node.owner !== "net/minecraft/tileentity/AbstractFurnaceTileEntity" ||
                         node.name !== cookTime ||
                         node.desc !== "I" )
                    {
                        continue;
                    }
                    instructions.insert( node, ASMAPI.listOf(
                        new VarInsnNode( Opcodes.ALOAD, 0 ),
                        new VarInsnNode( Opcodes.ALOAD, 0 ),
                        new VarInsnNode( Opcodes.ALOAD, 0 ),
                        new FieldInsnNode(
                            Opcodes.GETFIELD,
                            "net/minecraft/tileentity/AbstractFurnaceTileEntity",
                            cookTime,
                            "I"
                        ),
                        new VarInsnNode( Opcodes.ALOAD, 0 ),
                        new FieldInsnNode(
                            Opcodes.GETFIELD,
                            "net/minecraft/tileentity/AbstractFurnaceTileEntity",
                            cookTimeTotal,
                            "I"
                        ),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/eidee/minecraft/perks/hooks/asm/FurnacemanAsmHooks",
                            "calcCookTime",
                            "(Lnet/minecraft/tileentity/TileEntity;II)I",
                            false
                        ),
                        new FieldInsnNode(
                            Opcodes.PUTFIELD,
                            "net/minecraft/tileentity/AbstractFurnaceTileEntity",
                            cookTime,
                            "I"
                        )
                    ) );
                    break;
                }
                return methodNode;
            }
        }
    };
}