package net.eidee.minecraft.perks.network.message;

import static net.eidee.minecraft.perks.PerksMod.MOD_ID;

import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.capability.Capabilities;
import net.eidee.minecraft.perks.perk.PerkDataHandler;
import net.eidee.minecraft.perks.perk.PerkEnergyHandler;
import net.eidee.minecraft.perks.perk.PerkExperienceHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.nbt.ByteNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class SyncCapability
{
    private static final String TAG_SYNC_CAPABILITY = ( MOD_ID + ':' + "sync_capability" );

    private final INBT data;

    protected SyncCapability( @Nullable INBT nbt )
    {
        this.data = unwrap( nbt );
    }

    private INBT unwrap( @Nullable INBT nbt )
    {
        if ( nbt == null )
        {
            return ByteNBT.ZERO;
        }
        else if ( nbt instanceof CompoundNBT )
        {
            CompoundNBT compound = ( CompoundNBT )nbt;
            if ( compound.contains( TAG_SYNC_CAPABILITY ) )
            {
                return Objects.requireNonNull( compound.get( TAG_SYNC_CAPABILITY ) );
            }
        }
        return nbt;
    }

    public final INBT getData()
    {
        return data;
    }

    public final CompoundNBT getWrappedData()
    {
        CompoundNBT compound = new CompoundNBT();
        compound.put( TAG_SYNC_CAPABILITY, data );
        return compound;
    }

    public static class PerkEnergy
        extends SyncCapability
    {
        private PerkEnergy( @Nullable INBT nbt )
        {
            super( nbt );
        }

        public PerkEnergy( PerkEnergyHandler handler )
        {
            super( Capabilities.PERK_ENERGY.writeNBT( handler, null ) );
        }

        public static void encode( PerkEnergy message, PacketBuffer buffer )
        {
            buffer.writeCompoundTag( message.getWrappedData() );
        }

        public static PerkEnergy decode( PacketBuffer buffer )
        {
            return new PerkEnergy( buffer.readCompoundTag() );
        }

        public static void handle( PerkEnergy message, Supplier< NetworkEvent.Context > contextSupplier )
        {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork( () -> {
                ClientPlayerEntity player = Objects.requireNonNull( Minecraft.getInstance().player );
                player.getCapability( Capabilities.PERK_ENERGY ).ifPresent( handler -> {
                    Capabilities.PERK_ENERGY.readNBT( handler, null, message.getData() );
                } );
            } );
            context.setPacketHandled( true );
        }
    }

    public static class PerkExperience
        extends SyncCapability
    {
        private PerkExperience( @Nullable INBT nbt )
        {
            super( nbt );
        }

        public PerkExperience( PerkExperienceHandler handler )
        {
            this( Capabilities.PERK_EXPERIENCE.writeNBT( handler, null ) );
        }

        public static void encode( PerkExperience message, PacketBuffer buffer )
        {
            buffer.writeCompoundTag( message.getWrappedData() );
        }

        public static PerkExperience decode( PacketBuffer buffer )
        {
            return new PerkExperience( buffer.readCompoundTag() );
        }

        public static void handle( PerkExperience message, Supplier< NetworkEvent.Context > contextSupplier )
        {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork( () -> {
                ClientPlayerEntity player = Objects.requireNonNull( Minecraft.getInstance().player );
                player.getCapability( Capabilities.PERK_EXPERIENCE ).ifPresent( handler -> {
                    Capabilities.PERK_EXPERIENCE.readNBT( handler, null, message.getData() );
                } );
            } );
            context.setPacketHandled( true );
        }
    }

    public static class PerkData
        extends SyncCapability
    {
        private PerkData( @Nullable INBT nbt )
        {
            super( nbt );
        }

        public PerkData( PerkDataHandler handler )
        {
            this( Capabilities.PERK_DATA.writeNBT( handler, null ) );
        }

        public static void encode( PerkData message, PacketBuffer buffer )
        {
            buffer.writeCompoundTag( message.getWrappedData() );
        }

        public static PerkData decode( PacketBuffer buffer )
        {
            return new PerkData( buffer.readCompoundTag() );
        }

        public static void handle( PerkData message, Supplier< NetworkEvent.Context > contextSupplier )
        {
            NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork( () -> {
                ClientPlayerEntity player = Objects.requireNonNull( Minecraft.getInstance().player );
                player.getCapability( Capabilities.PERK_DATA ).ifPresent( handler -> {
                    Capabilities.PERK_DATA.readNBT( handler, null, message.getData() );
                } );
            } );
            context.setPacketHandled( true );
        }
    }
}
