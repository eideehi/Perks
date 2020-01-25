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
    var JumpInsnNode = Java.type( "org.objectweb.asm.tree.JumpInsnNode" );
    var MethodInsnNode = Java.type( "org.objectweb.asm.tree.MethodInsnNode" );
    var VarInsnNode = Java.type( "org.objectweb.asm.tree.VarInsnNode" );

    var isRemote = ASMAPI.mapField( "field_72995_K" );
    var rand = ASMAPI.mapField( "field_70146_Z" );
    var getBoolean = ASMAPI.mapMethod( "func_223586_b" );

    return {
        "ResidentOfEnd_01": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.item.ChorusFruitItem",
                "methodName": "func_77654_b",
                "methodDesc": "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/item/ItemStack;"
            },
            "transformer": function ( methodNode )
            {
                /*
                |   public ItemStack onItemUseFinish(ItemStack p_77654_1_, World p_77654_2_, LivingEntity p_77654_3_) {
                |       ItemStack lvt_4_1_ = super.onItemUseFinish(p_77654_1_, p_77654_2_, p_77654_3_);
                | -     if (!p_77654_2_.isRemote) {
                | +     if (!p_77654_2_.isRemote && !net.eidee.minecraft.perks.hooks.asm.ResidentOfEndAsmHooks.suppressTeleport(p_77654_2_, p_77654_3_)) {
                |           double lvt_5_1_ = p_77654_3_.func_226277_ct_();
                |       ...
                 */
                var instructions = methodNode.instructions;
                for ( var i = 0; i < instructions.size(); i++ )
                {
                    var node = instructions.get( i );
                    if ( node.getOpcode() !== Opcodes.ALOAD ||
                         node.var !== 2 )
                    {
                        continue;
                    }
                    node = instructions.get( i + 1 );
                    if ( node.getOpcode() !== Opcodes.GETFIELD ||
                         node.owner !== "net/minecraft/world/World" ||
                         node.name !== isRemote ||
                         node.desc !== "Z" )
                    {
                        continue;
                    }
                    node = instructions.get( i + 2 );
                    if ( node.getOpcode() !== Opcodes.IFNE )
                    {
                        continue;
                    }
                    instructions.insert( node, ASMAPI.listOf(
                        new VarInsnNode( Opcodes.ALOAD, 3 ),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/eidee/minecraft/perks/hooks/asm/ResidentOfEndAsmHooks",
                            "suppressTeleport",
                            "(Lnet/minecraft/entity/LivingEntity;)Z",
                            false
                        ),
                        new JumpInsnNode(
                            Opcodes.IFNE,
                            node.label
                        )
                    ) );
                    break;
                }
                return methodNode;
            }
        },
        "ResidentOfEnd_02": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.entity.item.EnderPearlEntity",
                "methodName": "func_70184_a",
                "methodDesc": "(Lnet/minecraft/util/math/RayTraceResult;)V"
            },
            "transformer": function ( methodNode )
            {
                /*
                |   protected void onImpact(RayTraceResult result) {
                |       ...
                |       net.minecraftforge.event.entity.living.EnderTeleportEvent event = new net.minecraftforge.event.entity.living.EnderTeleportEvent(serverplayerentity, this.func_226277_ct_(), this.func_226278_cu_(), this.func_226281_cx_(), 5.0F);
                |       if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) { // Don't indent to lower patch size
                | -     if (this.rand.nextFloat() < 0.05F && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                | +     if (this.rand.nextFloat() < 0.05F && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING) && !net.eidee.minecraft.perks.hooks.asm.ResidentOfEndAsmHooks.suppressEndermiteSpawn(serverplayerentity)) {
                |           EndermiteEntity endermiteentity = EntityType.ENDERMITE.create(this.world);
                |       ...
                 */
                var instructions = methodNode.instructions;
                for ( var i = 0; i < instructions.size(); i++ )
                {
                    var node = instructions.get( i );
                    if ( node.getOpcode() !== Opcodes.ALOAD ||
                         node.var !== 0 )
                    {
                        continue;
                    }
                    node = instructions.get( i + 1 );
                    if ( node.getOpcode() !== Opcodes.GETFIELD ||
                         node.owner !== "net/minecraft/entity/item/EnderPearlEntity" ||
                         node.name !== rand ||
                         node.desc !== "Ljava/util/Random;" )
                    {
                        continue;
                    }
                    node = instructions.get( i + 2 );
                    if ( node.getOpcode() !== Opcodes.INVOKEVIRTUAL ||
                         node.owner !== "java/util/Random" ||
                         node.name !== "nextFloat" ||
                         node.desc !== "()F" )
                    {
                        continue;
                    }
                    node = instructions.get( i + 3 );
                    if ( node.getOpcode() !== Opcodes.LDC )
                    {
                        continue;
                    }
                    node = instructions.get( i + 4 );
                    if ( node.getOpcode() !== Opcodes.FCMPG )
                    {
                        continue;
                    }
                    var jumpNode = undefined;
                    for ( var j = instructions.indexOf( node ); j < instructions.size(); j++ )
                    {
                        node = instructions.get( j );
                        if ( node.getOpcode() !== Opcodes.INVOKEVIRTUAL ||
                             node.owner !== "net/minecraft/world/GameRules" ||
                             node.name !== getBoolean ||
                             node.desc !== "(Lnet/minecraft/world/GameRules$RuleKey;)Z" )
                        {
                            continue;
                        }
                        node = instructions.get( j + 1 );
                        if ( node.getOpcode() !== Opcodes.IFEQ )
                        {
                            continue;
                        }
                        jumpNode = node;
                        break;
                    }
                    if ( jumpNode === undefined )
                    {
                        continue;
                    }
                    instructions.insert( jumpNode, ASMAPI.listOf(
                        new VarInsnNode( Opcodes.ALOAD, 3 ),
                        new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/eidee/minecraft/perks/hooks/asm/ResidentOfEndAsmHooks",
                            "suppressEndermiteSpawn",
                            "(Lnet/minecraft/entity/player/PlayerEntity;)Z",
                            false
                        ),
                        new JumpInsnNode(
                            Opcodes.IFNE,
                            jumpNode.label
                        )
                    ) );
                    break;
                }
                return methodNode;
            }
        }
    };
}