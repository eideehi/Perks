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
    var AbstractInsnNode = Java.type( "org.objectweb.asm.tree.AbstractInsnNode" );
    var FieldNode = Java.type( "org.objectweb.asm.tree.FieldNode" );
    var FieldInsnNode = Java.type( "org.objectweb.asm.tree.FieldInsnNode" );
    var JumpInsnNode = Java.type( "org.objectweb.asm.tree.JumpInsnNode" );
    var MethodInsnNode = Java.type( "org.objectweb.asm.tree.MethodInsnNode" );
    var VarInsnNode = Java.type( "org.objectweb.asm.tree.VarInsnNode" );

    var setEnchantments = ASMAPI.mapMethod( "func_82782_a" );

    return {
        "perks:GrindstoneContainer$4.onTake": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.inventory.container.GrindstoneContainer$4",
                "methodName": "func_190901_a",
                "methodDesc": "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/item/ItemStack;"
            },
            "transformer": function ( methodNode )
            {
                /*
                |   public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
                | +     net.eidee.minecraft.perks.asm.PolisherHooks.grindstoneOnTake(thePlayer);
                | +     if (!net.eidee.minecraft.perks.asm.PolisherHooks.usePerk(thePlayer, stack))
                |       worldPosCallableIn.consume((p_216944_1_, p_216944_2_) -> {
                |           int l = this.getEnchantmentXpFromInputs(p_216944_1_);
                |       ...
                |           p_216944_1_.playEvent(1042, p_216944_2_, 0);
                |       });
                | +     }
                |       GrindstoneContainer.this.field_217014_d.setInventorySlotContents(0, ItemStack.EMPTY);
                |       GrindstoneContainer.this.field_217014_d.setInventorySlotContents(1, ItemStack.EMPTY);
                |       return stack;
                |   }
                 */
                var instructions = methodNode.instructions;
                for ( var i = instructions.size() - 1; i >= 0; i-- )
                {
                    var node = instructions.get( i );
                    if ( node.getOpcode() !== Opcodes.ALOAD ||
                         node.var !== 0 )
                    {
                        continue;
                    }
                    node = undefined;
                    for ( var j = i - 1; j >= 0; j-- )
                    {
                        node = instructions.get( j );
                        if ( node.getOpcode() !== Opcodes.ALOAD ||
                             node.var !== 0 )
                        {
                            continue;
                        }
                        break;
                    }
                    var label = undefined;
                    if ( node !== undefined )
                    {
                        for ( var k = instructions.indexOf( node ) - 1; k >= 0; k-- )
                        {
                            node = instructions.get( k );
                            if ( node.getType() === AbstractInsnNode.LABEL )
                            {
                                label = node;
                                break;
                            }
                        }
                    }
                    if ( label !== undefined )
                    {
                        instructions.insertBefore( instructions.get( 0 ), ASMAPI.listOf(
                            new VarInsnNode( Opcodes.ALOAD, 1 ),
                            new VarInsnNode( Opcodes.ALOAD, 2 ),
                            new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "net/eidee/minecraft/perks/asm/PolisherHooks",
                                "usePerk",
                                "(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Z",
                                false
                            ),
                            new JumpInsnNode(
                                Opcodes.IFNE,
                                label
                            )
                        ) );
                        break;
                    }
                }
                instructions.insertBefore( instructions.get( 0 ), ASMAPI.listOf(
                    new VarInsnNode( Opcodes.ALOAD, 1 ),
                    new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "net/eidee/minecraft/perks/asm/PolisherHooks",
                        "grindstoneOnTake",
                        "(Lnet/minecraft/entity/player/PlayerEntity;)V",
                        false
                    )
                ) );
                return methodNode;
            }
        },
        "perks:GrindstoneContainer": {
            "target": {
                "type": "CLASS",
                "name": "net.minecraft.inventory.container.GrindstoneContainer"
            },
            "transformer": function ( classNode )
            {
                var fields = classNode.fields;
                fields.add( new FieldNode( Opcodes.ACC_PUBLIC,
                                           "playerInventory",
                                           "Lnet/minecraft/entity/player/PlayerInventory;",
                                           null,
                                           null ) );
                return classNode;
            }
        },
        "perks:GrindstoneContainer<init>": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.inventory.container.GrindstoneContainer",
                "methodName": "<init>",
                "methodDesc": "(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/util/IWorldPosCallable;)V"
            },
            "transformer": function ( methodNode )
            {
                /*
                |   public GrindstoneContainer(int windowIdIn, PlayerInventory p_i50081_2_, final IWorldPosCallable worldPosCallableIn) {
                |       ...
                |       }
                |
                | +     this.playerInventory = p_i50081_2_;
                |   }
                 */
                var instructions = methodNode.instructions;
                for ( var i = instructions.size() - 1; i >= 0; i--)
                {
                    var node = instructions.get( i );
                    if ( node.getOpcode() !== Opcodes.RETURN )
                    {
                        continue;
                    }
                    instructions.insertBefore( node, ASMAPI.listOf(
                        new VarInsnNode( Opcodes.ALOAD, 0 ),
                        new VarInsnNode( Opcodes.ALOAD, 2 ),
                        new FieldInsnNode(
                            Opcodes.PUTFIELD,
                            "net/minecraft/inventory/container/GrindstoneContainer",
                            "playerInventory",
                            "Lnet/minecraft/entity/player/PlayerInventory;"
                        )
                    ) );
                    break;
                }

                return methodNode;
            }
        },
        "perks:GrindstoneContainer.removeEnchantments": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.inventory.container.GrindstoneContainer",
                "methodName": "func_217007_a",
                "methodDesc": "(Lnet/minecraft/item/ItemStack;II)Lnet/minecraft/item/ItemStack;"
            },
            "transformer": function ( methodNode )
            {
                /*
                |   private ItemStack removeEnchantments(ItemStack stack, int damage, int count) {
                |      ...
                |       Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack).entrySet().stream().filter((p_217012_0_) -> {
                |          return p_217012_0_.getKey().isCurse();
                |       }).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
                | +     map = net.eidee.minecraft.perks.asm.PolisherHooks.getEnchantments(this.playerInventory, stack, map);
                |       EnchantmentHelper.setEnchantments(map, itemstack);
                |       itemstack.setRepairCost(0);
                |       ...
                 */
                var instructions = methodNode.instructions;
                for ( var i = 0; i < instructions.size(); i++ )
                {
                    var node = instructions.get( i );
                    if ( node.getOpcode() !== Opcodes.ALOAD ||
                         node.var !== 5 )
                    {
                        continue;
                    }
                    node = instructions.get( i + 1 );
                    if ( node.getOpcode() !== Opcodes.ALOAD ||
                         node.var !== 4 )
                    {
                        continue;
                    }
                    node = instructions.get( i + 2 );
                    if ( node.getOpcode() !== Opcodes.INVOKESTATIC ||
                         node.owner !== "net/minecraft/enchantment/EnchantmentHelper" ||
                         node.name !== setEnchantments ||
                         node.desc !== "(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V" )
                    {
                        continue;
                    }
                    instructions.insertBefore( instructions.get( i ), ASMAPI.listOf(
                        new VarInsnNode( Opcodes.ALOAD, 0 ),
                        new FieldInsnNode(
                            Opcodes.GETFIELD,
                            "net/minecraft/inventory/container/GrindstoneContainer",
                            "playerInventory",
                            "Lnet/minecraft/entity/player/PlayerInventory;"
                        ),
                        new VarInsnNode( Opcodes.ALOAD, 1 ),
                        new VarInsnNode( Opcodes.ALOAD, 5 ),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/eidee/minecraft/perks/asm/PolisherHooks",
                            "getEnchantments",
                            "(Lnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/item/ItemStack;Ljava/util/Map;)Ljava/util/Map;",
                            false
                        ),
                        new VarInsnNode( Opcodes.ASTORE, 5 )
                    ) );
                    break;
                }
                return methodNode;
            }
        }
    };
}