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

package net.eidee.minecraft.perks.hooks.eventhandler.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.gson.GsonBuilder;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.constants.RegistryNames;
import net.eidee.minecraft.perks.perk.Perk;
import net.eidee.minecraft.perks.registry.PerkRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.LibraryFinder;
import net.minecraftforge.registries.IForgeRegistry;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Mod.EventBusSubscriber( modid = PerksMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD )
public class PerkRegistryEventHandler
{
    public static void register( IForgeRegistry< Perk > registry, ResourceLocation registryName )
    {
        GsonBuilder gsonBuilder = new GsonBuilder().registerTypeAdapter( Perk.class, Perk.DESERIALIZER );
        Path path;
        {
            String resourceName = ( "definitions/" +
                                    registryName.getNamespace() +
                                    "/perk/" +
                                    registryName.getPath() +
                                    ".json" );
            File file = new File( Minecraft.getInstance().gameDir, resourceName );
            if ( file.isFile() && file.canRead() )
            {
                path = Paths.get( file.toURI() );
            }
            else
            {
                try
                {
                    URL resource = PerksMod.class.getClassLoader().getResource( resourceName );
                    if ( resource == null )
                    {
                        throw new RuntimeException( String.format( "Perk file not found. %s", resourceName ) );
                    }
                    URI uri = resource.toURI();
                    if ( uri.getScheme().equalsIgnoreCase( "file" ) )
                    {
                        path = Paths.get( uri );
                    }
                    else
                    {
                        PerksMod.getLogger().warn( "Scheme is not file: {}", uri );
                        path = LibraryFinder.findJarPathFor( resourceName, "perks", resource );
                    }
                }
                catch ( URISyntaxException e )
                {
                    throw new RuntimeException( e );
                }
            }
        }
        try
        {
            PerksMod.getLogger().info( "Load a perk-file: {}", path );
            BufferedReader reader = Files.newBufferedReader( path, StandardCharsets.UTF_8 );
            Perk perk = gsonBuilder.create().fromJson( reader, Perk.class );
            registry.register( perk.setRegistryName( registryName ) );
        }
        catch ( IOException ignored )
        {
            throw new RuntimeException( String.format( "Parsing of the Perk file failed. %s", path ) );
        }
    }

    @SubscribeEvent
    public static void newRegistry( RegistryEvent.NewRegistry event )
    {
        PerkRegistry.init();
    }

    @SubscribeEvent
    public static void registerPerk( RegistryEvent.Register< Perk > event )
    {
        IForgeRegistry< Perk > registry = event.getRegistry();
        Stream.of( RegistryNames.PERK_ECONOMY,
                   RegistryNames.PERK_INTELLIGENCE,
                   RegistryNames.PERK_IRON_FIST,
                   RegistryNames.PERK_CULTIVATOR,
                   RegistryNames.PERK_SEEDER,
                   RegistryNames.PERK_SPREADER,
                   RegistryNames.PERK_HARVESTER,
                   RegistryNames.PERK_FURNACEMAN,
                   RegistryNames.PERK_RESIDENT_OF_END,
                   RegistryNames.PERK_BREEDER,
                   RegistryNames.PERK_FEEDER,
                   RegistryNames.PERK_OBSIDIAN_BREAKER,
                   RegistryNames.PERK_POLISHER ).map( ResourceLocation::new ).forEach( x -> register( registry, x ) );
        if ( registry.isEmpty() )
        {
            throw new RuntimeException( "Register< Perk > is empty." );
        }
    }
}
