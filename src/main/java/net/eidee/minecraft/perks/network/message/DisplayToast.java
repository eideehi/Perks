package net.eidee.minecraft.perks.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import net.eidee.minecraft.perks.constants.RegistryKeys;
import net.eidee.minecraft.perks.gui.toasts.PerkUnlockToast;
import net.eidee.minecraft.perks.perk.Perk;
import net.eidee.minecraft.perks.registry.PerkRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class DisplayToast
{
    private final String perkId;

    private DisplayToast( String perkId )
    {
        this.perkId = perkId;
    }

    public DisplayToast( Perk perk )
    {
        this( Optional.ofNullable( perk.getRegistryName() ).orElse( RegistryKeys.PERK_NULL ).toString() );
    }

    public final String getPerkId()
    {
        return perkId;
    }

    public static void encode( DisplayToast message, PacketBuffer buffer )
    {
        buffer.writeString( message.getPerkId() );
    }

    public static DisplayToast decode( PacketBuffer buffer )
    {
        return new DisplayToast( buffer.readString() );
    }

    public static void handle( DisplayToast message, Supplier< NetworkEvent.Context > contextSupplier )
    {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork( () -> {
            PerkRegistry.getValue( message.getPerkId() ).ifPresent( perk -> {
                Minecraft.getInstance().getToastGui().add( new PerkUnlockToast( perk ) );
            } );
        } );
        context.setPacketHandled( true );
    }
}
