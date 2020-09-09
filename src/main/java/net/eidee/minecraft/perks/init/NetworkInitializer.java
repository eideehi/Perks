package net.eidee.minecraft.perks.init;

import java.util.concurrent.atomic.AtomicInteger;

import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.DisplayToast;
import net.eidee.minecraft.perks.network.message.PerkControl;
import net.eidee.minecraft.perks.network.message.SyncCapability;

public class NetworkInitializer
{
    public static void registerMessage()
    {
        AtomicInteger id = new AtomicInteger( 0 );

        Networks.getChannel()
                .registerMessage( id.getAndIncrement(),
                                  SyncCapability.PerkEnergy.class,
                                  SyncCapability.PerkEnergy::encode,
                                  SyncCapability.PerkEnergy::decode,
                                  SyncCapability.PerkEnergy::handle );

        Networks.getChannel()
                .registerMessage( id.getAndIncrement(),
                                  SyncCapability.PerkExperience.class,
                                  SyncCapability.PerkExperience::encode,
                                  SyncCapability.PerkExperience::decode,
                                  SyncCapability.PerkExperience::handle );

        Networks.getChannel()
                .registerMessage( id.getAndIncrement(),
                                  SyncCapability.PerkData.class,
                                  SyncCapability.PerkData::encode,
                                  SyncCapability.PerkData::decode,
                                  SyncCapability.PerkData::handle );

        Networks.getChannel()
                .registerMessage( id.getAndIncrement(),
                                  PerkControl.LearningNextRank.class,
                                  PerkControl.LearningNextRank::encode,
                                  PerkControl.LearningNextRank::decode,
                                  PerkControl.LearningNextRank::handle );

        Networks.getChannel()
                .registerMessage( id.getAndIncrement(),
                                  PerkControl.ChangeRank.class,
                                  PerkControl.ChangeRank::encode,
                                  PerkControl.ChangeRank::decode,
                                  PerkControl.ChangeRank::handle );

        Networks.getChannel()
                .registerMessage( id.getAndIncrement(),
                                  PerkControl.SetPerkEnable.class,
                                  PerkControl.SetPerkEnable::encode,
                                  PerkControl.SetPerkEnable::decode,
                                  PerkControl.SetPerkEnable::handle );

        Networks.getChannel()
                .registerMessage( id.getAndIncrement(),
                                  DisplayToast.class,
                                  DisplayToast::encode,
                                  DisplayToast::decode,
                                  DisplayToast::handle );
    }
}
