package net.eidee.minecraft.perks.util;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ForgeI18n;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class Message
{
    private Message()
    {
    }

    private static Object[] adjustArgs( Object[] args )
    {
        Object[] result = new Object[ args.length ];
        for ( int i = 0; i < args.length; i++ )
        {
            if ( args[ i ] instanceof ITextComponent )
            {
                result[ i ] = ( ( ITextComponent )args[ i ] ).getString();
            }
            else
            {
                result[ i ] = args[ i ];
            }
        }
        return result;
    }

    public static String format( String text, Object... args )
    {
        return ForgeI18n.parseMessage( text, adjustArgs( args ) );
    }

    public static ITextComponent of( String text )
    {
        return new TranslationTextComponent( text );
    }

    public static ITextComponent of( String text, Object... args )
    {
        return new TranslationTextComponent( text, adjustArgs( args ) );
    }
}
