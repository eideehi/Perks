package net.eidee.minecraft.perks.gui;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.PerkControl;
import net.eidee.minecraft.perks.perk.Perk;
import net.eidee.minecraft.perks.perk.PerkController;
import net.eidee.minecraft.perks.perk.PerkData;
import net.eidee.minecraft.perks.perk.PerkManager;
import net.eidee.minecraft.perks.util.Message;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn( Dist.CLIENT )
public class PerksScreen
    extends Screen
{
    private static final ResourceLocation GUI_TEXTURE;
    private static final StringTextComponent NEW_LINE;

    static
    {
        GUI_TEXTURE = new ResourceLocation( PerksMod.MOD_ID, "textures/gui/perks.png" );
        NEW_LINE = new StringTextComponent( "\n" );
    }

    private int guiWidth;
    private int guiHeight;
    private int guiLeft;
    private int guiTop;

    private double scroll;
    private int perksOffset;

    private TextFieldWidget filter;

    private List< PerkController > rawControllers;
    private List< PerkController > controllers;

    private Object2IntMap< Widget > columnIdMap;
    private Object2IntMap< Widget > rowIdMap;
    private Int2ObjectMap< Widget > buttonByIdMap;

    public PerksScreen()
    {
        super( Message.of( "gui.perks.perks.title" ) );
        this.guiWidth = 228;
        this.guiHeight = 181;
        this.columnIdMap = new Object2IntOpenHashMap<>();
        this.rowIdMap = new Object2IntOpenHashMap<>();
        this.buttonByIdMap = new Int2ObjectOpenHashMap<>();
    }

    private List< ? extends IReorderingProcessor > wrapLines( List< ITextComponent > lines )
    {
        int maxWidth = ( width / 2 ) + ( guiWidth / 2 );

        List< IReorderingProcessor > wrappedLines = Lists.newArrayList();
        for ( ITextComponent line : lines )
        {
            wrappedLines.addAll( font.trimStringToWidth( line, maxWidth ) );
        }

        return wrappedLines;
    }

    private void updateControllers( String filter )
    {
        controllers = rawControllers.stream()
                                    .filter( x -> !x.getTargetPerk().isHidden() || x.getReferenceData().isUnlocked() )
                                    .filter( x -> filter.isEmpty() || getName( x ).contains( filter ) )
                                    .sorted( this::comparePerk )
                                    .collect( Collectors.toList() );
    }

    private String getName( PerkController controller )
    {
        return controller.getTargetPerk().getDisplayName().getString();
    }

    private int comparePerk( PerkController controller1, PerkController controller2 )
    {
        return getName( controller1 ).compareTo( getName( controller2 ) );
    }

    @Override
    public void renderToolTip( MatrixStack matrixStack,
                               List< ? extends IReorderingProcessor > lines,
                               int mouseX,
                               int mouseY,
                               FontRenderer font )
    {
        if ( !lines.isEmpty() )
        {
            int w = 0;

            for ( IReorderingProcessor processor : lines )
            {
                int textWidth = font.func_243245_a( processor );
                if ( textWidth > w )
                {
                    w = textWidth;
                }
            }

            int h = 8;
            if ( lines.size() > 1 )
            {
                h += 2 + ( lines.size() - 1 ) * 10;
            }

            int x = ( width / 2 ) - ( w / 2 );
            int y = ( height / 2 ) - ( h / 2 );

            matrixStack.push();

            int color1 = -267386864;
            int color2 = 1347420415;
            int color3 = 1344798847;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin( 7, DefaultVertexFormats.POSITION_COLOR );
            Matrix4f matrix4f = matrixStack.getLast().getMatrix();
            fillGradient( matrix4f, buffer, x - 3, y - 4, x + w + 3, y - 3, 400, color1, color1 );
            fillGradient( matrix4f, buffer, x - 3, y + h + 3, x + w + 3, y + h + 4, 400, color1, color1 );
            fillGradient( matrix4f, buffer, x - 3, y - 3, x + w + 3, y + h + 3, 400, color1, color1 );
            fillGradient( matrix4f, buffer, x - 4, y - 3, x - 3, y + h + 3, 400, color1, color1 );
            fillGradient( matrix4f, buffer, x + w + 3, y - 3, x + w + 4, y + h + 3, 400, color1, color1 );
            fillGradient( matrix4f, buffer, x - 3, y - 3 + 1, x - 3 + 1, y + h + 3 - 1, 400, color2, color3 );
            fillGradient( matrix4f, buffer, x + w + 2, y - 3 + 1, x + w + 3, y + h + 3 - 1, 400, color2, color3 );
            fillGradient( matrix4f, buffer, x - 3, y - 3, x + w + 3, y - 3 + 1, 400, color2, color2 );
            fillGradient( matrix4f, buffer, x - 3, y + h + 2, x + w + 3, y + h + 3, 400, color3, color3 );
            RenderSystem.enableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.shadeModel( 7425 );
            buffer.finishDrawing();
            WorldVertexBufferUploader.draw( buffer );
            RenderSystem.shadeModel( 7424 );
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            IRenderTypeBuffer.Impl bufferImpl = IRenderTypeBuffer.getImpl( Tessellator.getInstance().getBuffer() );
            matrixStack.translate( 0.0D, 0.0D, 400.0D );

            for ( int i = 0; i < lines.size(); ++i )
            {
                IReorderingProcessor processor = lines.get( i );
                if ( processor != null )
                {
                    font.func_238416_a_( processor, x, y, -1, true, matrix4f, bufferImpl, false, 0, 15728880 );
                }

                if ( i == 0 )
                {
                    y += 2;
                }

                y += 10;
            }

            bufferImpl.finish();
            matrixStack.pop();
        }
    }

    private void perkTooltip( PerkController controller, MatrixStack matrixStack, int mouseX, int mouseY )
    {
        List< ITextComponent > lines = Lists.newArrayList();

        Perk perk = controller.getTargetPerk();
        PerkData data = controller.getReferenceData();

        lines.add( perk.getDisplayName() );

        if ( data.isLearned() )
        {
            if ( !data.isEnabled() )
            {
                lines.add( Message.of( "gui.perks.perks.tooltip.perk_disabled" ) );
            }
            else
            {
                int rank = data.getRank();
                int i = perk.getRankPolicy() == Perk.RankPolicy.MORE_SKILL ? 1 : rank;
                for ( ; i <= rank; i++ )
                {
                    lines.add( Message.of( "{0} ({1})",
                                           Message.format( perk.getName() + ".description." + i ),
                                           Message.format( "gui.perks.perks.tooltip.cost_energy",
                                                           controller.getUsageCost( i ) ) ) );
                }
            }
        }
        else
        {
            int progress = MathHelper.floor( data.getUnlockProgress() * 100 );
            if ( progress < 100 )
            {
                String action = Message.format( perk.getName() + ".required_action" );

                lines.add( Message.of( "gui.perks.perks.tooltip.perk_progress", 100 - progress ) );
                lines.add( Message.of( "gui.perks.perks.tooltip.required_action", action ) );
            }
            else
            {
                lines.add( Message.of( "gui.perks.perks.tooltip.perk_unlocked" ) );
            }
        }

        renderToolTip( matrixStack, wrapLines( lines ), mouseX, mouseY, font );
    }

    private void buttonClick( Widget button )
    {
        int column = columnIdMap.getInt( button );
        int index = perksOffset + column;
        if ( index <= controllers.size() )
        {
            PerkController controller = controllers.get( index );
            Perk perk = controller.getTargetPerk();

            int row = rowIdMap.getInt( button );
            if ( ( row == 0 || row == 1 ) && controller.learningNextRank( true ) )
            {
                Networks.getChannel().sendToServer( new PerkControl.LearningNextRank( perk ) );
            }
            else
            {
                PerkData data = controller.getReferenceData();

                if ( ( row == 2 || row == 3 ) )
                {
                    int rank = data.getRank() + ( row == 2 ? -1 : 1 );
                    if ( controller.updateRank( rank, true ) )
                    {
                        Networks.getChannel().sendToServer( new PerkControl.ChangeRank( perk, rank ) );
                    }
                }
                else if ( row == 4 || row == 5 )
                {
                    Networks.getChannel().sendToServer( new PerkControl.SetPerkEnable( perk, !data.isEnabled() ) );
                }
            }
        }
    }

    private void buttonTooltip( Widget button, MatrixStack matrixStack, int mouseX, int mouseY )
    {
        if ( !hasShiftDown() || !button.active )
        {
            return;
        }

        int column = columnIdMap.getInt( button );
        int index = perksOffset + column;
        if ( index <= controllers.size() )
        {
            List< ITextComponent > lines = Lists.newArrayList();

            PerkController controller = controllers.get( index );
            Perk perk = controller.getTargetPerk();

            int row = rowIdMap.getInt( button );
            if ( row == 0 )
            {
                lines.add( Message.of( "gui.perks.perks.tooltip.learning_perk", perk.getDisplayName() ) );
                lines.add( Message.of( perk.getName() + ".description.1" ) );
                lines.add( NEW_LINE );
                lines.add( Message.of( "gui.perks.perks.tooltip.cost_experience", controller.getLearningCost( 1 ) ) );
                lines.add( Message.of( "gui.perks.perks.tooltip.current_experience", controller.getExperience() ) );
            }
            else if ( row == 1 )
            {
                int rank = controller.getReferenceData().getRank() + 1;
                String rankText = Message.format( "gui.perks.perks.rank." + rank );

                lines.add( Message.of( "gui.perks.perks.tooltip.learning_rank", perk.getDisplayName(), rankText ) );
                lines.add( Message.of( perk.getName() + ".description." + rank ) );
                lines.add( NEW_LINE );
                lines.add( Message.of( "gui.perks.perks.tooltip.cost_experience",
                                       controller.getLearningCost( rank ) ) );
                lines.add( Message.of( "gui.perks.perks.tooltip.current_experience", controller.getExperience() ) );
            }
            else if ( row == 2 )
            {
                int rank = controller.getReferenceData().getRank() - 1;
                lines.add( Message.of( "gui.perks.perks.tooltip.previous_rank" ) );
                lines.add( Message.of( perk.getName() + ".description." + rank ) );
            }
            else if ( row == 3 )
            {
                int rank = controller.getReferenceData().getRank() + 1;
                lines.add( Message.of( "gui.perks.perks.tooltip.next_rank" ) );
                lines.add( Message.of( perk.getName() + ".description." + rank ) );
            }
            else if ( row == 4 )
            {
                lines.add( Message.of( "gui.perks.perks.tooltip.disable" ) );
            }
            else if ( row == 5 )
            {
                lines.add( Message.of( "gui.perks.perks.tooltip.enable" ) );
            }

            renderToolTip( matrixStack, wrapLines( lines ), mouseX, mouseY, font );
        }
    }

    private void updateButtons()
    {
        for ( int i = 0; i < 7; i++ )
        {
            int index = perksOffset + i;
            boolean visible = index < controllers.size();
            for ( int j = 0; j < 6; j++ )
            {
                Widget button = buttonByIdMap.get( ( i * 7 ) + j );
                if ( button.visible = visible )
                {
                    PerkController controller = controllers.get( index );
                    Perk perk = controller.getTargetPerk();
                    PerkData data = controller.getReferenceData();

                    button.visible = ( j == 0 && !data.isLearned() ) ||
                                     ( ( j > 0 && data.isLearned() ) &&
                                       ( ( j <= 3 ) ||
                                         ( j == 4 && data.isEnabled() ) ||
                                         ( j == 5 && !data.isEnabled() ) ) );

                    button.active = ( j == 0 && data.isUnlocked() && !data.isLearned() ) ||
                                    ( data.isLearned() &&
                                      ( ( j == 1 && data.getLearnedMaxRank() < perk.getMaxRank() ) ||
                                        ( j == 2 && data.getRank() > 1 ) ||
                                        ( j == 3 && data.getRank() < data.getLearnedMaxRank() ) ||
                                        ( j == 4 && data.isEnabled() ) ||
                                        ( j == 5 && !data.isEnabled() ) ) );
                }
            }
        }
    }

    @Override
    protected void init()
    {
        PlayerEntity player = Objects.requireNonNull( getMinecraft().player );

        guiLeft = ( width - guiWidth ) / 2;
        guiTop = ( height - guiHeight ) / 2;

        rawControllers = PerkManager.createControllerAll( player ).collect( Collectors.toList() );

        filter = addButton( new TextFieldWidget( font,
                                                 guiLeft + 26,
                                                 guiTop + 16,
                                                 194,
                                                 10,
                                                 StringTextComponent.EMPTY ) );
        filter.setEnableBackgroundDrawing( false );
        filter.setResponder( this::updateControllers );

        addButton( new Widget( guiLeft + 208, guiTop + 32, 12, 15, StringTextComponent.EMPTY )
        {
            @Override
            public void playDownSound( SoundHandler handler )
            {
            }

            @Override
            public void renderButton( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks )
            {
                getMinecraft().getTextureManager().bindTexture( GUI_TEXTURE );

                int y = MathHelper.clamp( ( int )( scroll * ( 140 - 15 ) ) + 32, 32, 32 + 140 - 15 );
                boolean active = controllers.size() > 7;

                RenderSystem.enableDepthTest();
                blit( matrixStack, x, guiTop + y, active ? 187 : 200, 182, width, height, 512, 256 );

                this.y = guiTop + y;
                this.active = active;
            }

            @Override
            protected void onDrag( double mouseX, double mouseY, double p_230983_5_, double p_230983_7_ )
            {
                if ( active )
                {
                    int y = MathHelper.clamp( ( int )mouseY - guiTop - 7, 32, 32 + 140 - 15 );
                    scroll = MathHelper.clamp( ( y - 32.0 ) / ( 140.0 - 15.0 ), 0.0, 1.0 );
                }
            }
        } );

        columnIdMap.clear();
        rowIdMap.clear();
        buttonByIdMap.clear();

        for ( int i = 0; i < 7; i++ )
        {
            int y = guiTop + 32 + ( i * 20 );

            final int columnNumber = i;
            addButton( new Widget( guiLeft + 8, y, 114, 20, StringTextComponent.EMPTY )
            {
                @Override
                public boolean mouseClicked( double mouseX, double mouseY, int p_231044_5_ )
                {
                    return false;
                }

                @Override
                public void renderButton( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks )
                {
                    int index = perksOffset + columnNumber;
                    if ( index >= controllers.size() )
                    {
                        return;
                    }

                    getMinecraft().getTextureManager().bindTexture( GUI_TEXTURE );
                    RenderSystem.enableDepthTest();

                    PerkController controller = controllers.get( index );
                    Perk perk = controller.getTargetPerk();

                    Perk.Rarity rarity = perk.getRarity();
                    int rarityOffsetY = rarity == Perk.Rarity.COMMON ? 0 : ( rarity == Perk.Rarity.RARE ? 1 : 2 );
                    int textureY = 1 + ( 63 * rarityOffsetY );

                    PerkData data = controller.getReferenceData();
                    if ( data.isUnlocked() )
                    {
                        textureY += 21;
                        if ( !data.isEnabled() )
                        {
                            textureY += 21;
                        }
                    }

                    blit( matrixStack, x, y, 229, textureY, width, height, 512, 256 );

                    if ( !data.isUnlocked() )
                    {
                        int textureWidth = ( int )( 114 * data.getUnlockProgress() );
                        blit( matrixStack, x, y, 229, textureY + 21, textureWidth, height, 512, 256 );
                    }

                    ITextComponent text = perk.getDisplayName();
                    if ( data.isLearned() )
                    {
                        text = Message.of( "{0} {1}", text, Message.of( "gui.perks.perks.rank." + data.getRank() ) );
                    }

                    drawCenteredString( matrixStack,
                                        font,
                                        text,
                                        x + width / 2,
                                        y + ( height - 8 ) / 2,
                                        getFGColor() | MathHelper.ceil( alpha * 255.0F ) << 24 );

                    if ( hasShiftDown() && isHovered() )
                    {
                        perkTooltip( controller, matrixStack, mouseX, mouseY );
                    }
                }
            } );

            for ( int j = 0; j < 6; j++ )
            {
                int x = j != 0 ? ( j < 5 ? guiLeft + 102 + ( j * 20 ) : guiLeft + 182 ) : guiLeft + 122;
                int width = j != 0 ? 20 : 80;
                int textureX = j != 0 ? 61 + ( j * 21 ) : 1;

                Widget button = addButton( new AbstractButton( x, y, width, 20, StringTextComponent.EMPTY )
                {
                    @Override
                    public void onPress()
                    {
                        PerksScreen.this.buttonClick( this );
                    }

                    @Override
                    public void renderButton( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks )
                    {
                        getMinecraft().getTextureManager().bindTexture( GUI_TEXTURE );

                        int textureY = 182;
                        if ( !active )
                        {
                            textureY += 42;
                        }
                        else if ( isHovered() )
                        {
                            textureY += 21;
                        }

                        RenderSystem.enableDepthTest();
                        blit( matrixStack, x, y, textureX, textureY, width, height, 512, 256 );

                        if ( isHovered() )
                        {
                            PerksScreen.this.buttonTooltip( this, matrixStack, mouseX, mouseY );
                        }
                    }
                } );

                columnIdMap.put( button, i );
                rowIdMap.put( button, j );
                buttonByIdMap.put( ( i * 7 ) + j, button );
            }
        }

        updateControllers( "" );
        updateButtons();
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void tick()
    {
        super.tick();

        filter.tick();

        controllers.forEach( PerkController::updateReference );
        perksOffset = ( int )Math.max( ( scroll * ( controllers.size() - 7.5 ) ) + 0.5, 0 );

        updateButtons();
    }

    @Override
    public void render( MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks )
    {
        renderBackground( matrixStack );

        RenderSystem.color4f( 1.0F, 1.0F, 1.0F, 1.0F );
        getMinecraft().getTextureManager().bindTexture( GUI_TEXTURE );
        blit( matrixStack, guiLeft, guiTop, getBlitOffset(), 0, 0, guiWidth, guiHeight, 256, 512 );

        super.render( matrixStack, mouseX, mouseY, partialTicks );
    }

    @Override
    public boolean keyPressed( int keyCode, int scanCode, int modifiers )
    {
        if ( super.keyPressed( keyCode, scanCode, modifiers ) )
        {
            return true;
        }

        InputMappings.Input mouseKey = InputMappings.getInputByCode( keyCode, scanCode );
        if ( !filter.isFocused() && getMinecraft().gameSettings.keyBindInventory.isActiveAndMatches( mouseKey ) )
        {
            closeScreen();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled( double mouseX, double mouseY, double scrollDelta )
    {
        scroll = MathHelper.clamp( ( perksOffset - scrollDelta ) / ( controllers.size() - 7.5 ), 0, 1 );
        return true;
    }
}
