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

package net.eidee.minecraft.perks.gui;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mcp.MethodsReturnNonnullByDefault;
import net.eidee.minecraft.perks.PerksMod;
import net.eidee.minecraft.perks.network.Networks;
import net.eidee.minecraft.perks.network.message.PerkControl;
import net.eidee.minecraft.perks.perk.Perk;
import net.eidee.minecraft.perks.perk.PerkData;
import net.eidee.minecraft.perks.perk.PerkManager;
import net.eidee.minecraft.perks.settings.KeyBindings;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn( Dist.CLIENT )
public class PerksGui
    extends Screen
{
    static final ResourceLocation GUI_TEXTURE;

    static
    {
        GUI_TEXTURE = new ResourceLocation( PerksMod.MOD_ID, "textures/gui/perks.png" );
    }

    private int guiWidth;
    private int guiHeight;
    private int guiLeft;
    private int guiTop;

    private int scrollOffset;

    private PerkManager perkManager;
    private List< Perk > perks;

    private List< IRenderable2 > renderables;

    private PerkPanel[] perkPanels;
    private PerkController[] perkControllers;
    private Scrollbar scrollbar;
    private TextFieldWidget filter;

    public PerksGui()
    {
        super( new TranslationTextComponent( "gui.perks.perks.title" ) );
        this.guiWidth = 228;
        this.guiHeight = 181;
    }

    private void preRender()
    {
        RenderSystem.color4f( 1.0F, 1.0F, 1.0F, 1.0F );
        getMinecraft().getTextureManager().bindTexture( GUI_TEXTURE );
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc( GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA );
    }

    private boolean isInBox( int x, int y, int xStart, int yStart, int xEnd, int yEnd )
    {
        return x >= guiLeft + xStart && x <= guiLeft + xEnd && y >= guiTop + yStart && y <= guiTop + yEnd;
    }

    private void updateScroll( float scroll )
    {
        scrollOffset = ( int )( ( double )( scroll * ( float )( perks.size() - 7 ) ) + 0.5D );
        if ( scrollOffset < 0 )
        {
            scrollOffset = 0;
        }

        assignPerks();
    }

    private void updateFilter( String filter )
    {
        perks.clear();

        perkManager.getPerkStorage().getStorage().forEach( ( key, value ) -> {
            if ( value.isUnlocked() )
            {
                String displayName = I18n.format( key.getName() );
                if ( filter.isEmpty() || displayName.contains( filter ) )
                {
                    perks.add( key );
                }
            }
        } );

        perks.sort( Comparator.comparing( Perk::getName ) );

        scrollOffset = 0;
        scrollbar.reset();
        scrollbar.setEnabled( perks.size() > 7 );

        assignPerks();
    }

    private void assignPerks()
    {
        for ( int i = 0; i < 7; i++ )
        {
            int index = scrollOffset + i;
            if ( index < perks.size() )
            {
                Perk perk = perks.get( index );

                perkPanels[ i ].setVisible( true );
                perkPanels[ i ].setPerk( perk );
                perkControllers[ i ].setPerk( perk );
                perkControllers[ i ].setVisible( true );
            }
            else
            {
                perkPanels[ i ].setVisible( false );
                perkControllers[ i ].setVisible( false );
            }
        }
    }

    private void perkControl( Perk perk, Control control )
    {
        ResourceLocation registryName = perk.getRegistryName();
        if ( registryName == null )
        {
            return;
        }
        String perkId = registryName.toString();
        if ( control == Control.PERK_LEARNING || control == Control.RANK_LEARNING )
        {
            if ( perkManager.learningNextRank( perk ) )
            {
                Networks.CHANNEL.sendToServer( new PerkControl.LearningNextRank( perkId ) );
            }
        }
        else if ( control == Control.PREVIOUS_RANK || control == Control.NEXT_RANK )
        {
            int n = ( control == Control.PREVIOUS_RANK ? -1 : 1 );
            PerkData data = perkManager.getPerkStorage().getData( perk );
            int newRank = data.getRank() + n;
            if ( perkManager.changeRank( perk, newRank ) )
            {
                Networks.CHANNEL.sendToServer( new PerkControl.ChangeRank( perkId, newRank ) );
            }
        }
        else if ( control == Control.PERK_ENABLE || control == Control.PERK_DISABLE )
        {
            boolean enable = ( control == Control.PERK_ENABLE );
            if ( perkManager.setPerkEnable( perk, enable ) )
            {
                Networks.CHANNEL.sendToServer( new PerkControl.SetPerkEnable( perkId, enable ) );
            }
        }

        assignPerks();
    }

    @Override
    protected void init()
    {
        super.init();
        getMinecraft().keyboardListener.enableRepeatEvents( true );

        guiLeft = ( width - guiWidth ) / 2;
        guiTop = ( height - guiHeight ) / 2;

        scrollOffset = 0;

        perkManager = PerkManager.of( Objects.requireNonNull( getMinecraft().player ) );
        perks = Lists.newArrayList();
        perkManager.getPerkStorage().getStorage().forEach( ( key, value ) -> {
            if ( value.isUnlocked() )
            {
                perks.add( key );
            }
        } );
        perks.sort( Comparator.comparing( Perk::getName ) );

        renderables = Lists.newArrayList();

        perkPanels = new PerkPanel[ 7 ];
        perkControllers = new PerkController[ 7 ];
        for ( int i = 0; i < 7; i++ )
        {
            int x = 8;
            int y = 32 + ( i * 20 );
            perkPanels[ i ] = new PerkPanel( x, y );
            renderables.add( perkPanels[ i ] );

            x += 114;
            perkControllers[ i ] = new PerkController( x, y );
            children.add( perkControllers[ i ] );
            renderables.add( perkControllers[ i ] );
        }

        scrollbar = new Scrollbar();
        children.add( scrollbar );
        renderables.add( scrollbar );
        scrollbar.setEnabled( perks.size() > 7 );

        filter = new TextFieldWidget( getMinecraft().fontRenderer, guiLeft + 25, guiTop + 17, 198, 12, "" );
        filter.setEnableBackgroundDrawing( false );
        filter.func_212954_a( this::updateFilter );
        children.add( filter );

        assignPerks();
    }

    @Override
    public void removed()
    {
        super.removed();
        getMinecraft().keyboardListener.enableRepeatEvents( false );
    }

    @Override
    public void tick()
    {
        super.tick();
        filter.tick();
    }

    @Override
    public void render( int p_render_1_, int p_render_2_, float p_render_3_ )
    {
        renderBackground();

        preRender();

        blit( guiLeft, guiTop, getBlitOffset(), 0, 0, guiWidth, guiHeight, 256, 512 );

        renderables.forEach( renderable -> renderable.render( p_render_1_, p_render_2_, p_render_3_ ) );

        renderables.forEach( renderable -> renderable.renderToolTip( p_render_1_, p_render_2_ ) );

        filter.render( p_render_1_, p_render_2_, p_render_3_ );
    }

    @Override
    public boolean mouseClicked( double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_ )
    {
        setFocused( null );
        filter.setFocused2( false );
        return super.mouseClicked( p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_ );
    }

    @Override
    public boolean mouseScrolled( double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_ )
    {
        if ( super.mouseScrolled( p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_ ) )
        {
            return true;
        }
        return scrollbar.mouseScrolled( p_mouseScrolled_1_, p_mouseScrolled_3_, p_mouseScrolled_5_ );
    }

    @Override
    public boolean keyPressed( int key, int scanCode, int modifiers )
    {
        if ( super.keyPressed( key, scanCode, modifiers ) )
        {
            return true;
        }
        if ( !filter.isFocused() )
        {
            InputMappings.Input mouseKey = InputMappings.getInputByCode( key, scanCode );
            if ( KeyBindings.OPEN_PERK_GUI.isActiveAndMatches( mouseKey ) ||
                 getMinecraft().gameSettings.keyBindInventory.isActiveAndMatches( mouseKey ) )
            {
                this.onClose();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public enum Control
    {
        PERK_LEARNING,
        RANK_LEARNING,
        PREVIOUS_RANK,
        NEXT_RANK,
        PERK_ENABLE,
        PERK_DISABLE
    }

    public interface IRenderable2
        extends IRenderable
    {
        default void renderToolTip( int p_renderToolTip_1_, int p_renderToolTip_2_ )
        {
        }
    }

    public class PerkPanel
        implements IRenderable2,
                   IGuiEventListener
    {
        private int x;
        private int y;
        private final int textureX = 229;
        private int textureY;
        private String text;
        private int textColor;
        private List< String > tooltips;
        private boolean visible;

        public PerkPanel( int x, int y )
        {
            this.x = x;
            this.y = y;
        }

        private String getText( Perk perk, PerkData data )
        {
            ITextComponent nameText = new TranslationTextComponent( perk.getName() );
            if ( data.isLearned() && ( data.getRank() != 1 || perk.getMaxRank() != 1 ) )
            {
                ITextComponent rankText = new TranslationTextComponent( "gui.perks.perks.rank." + data.getRank() );
                nameText.appendText( " " ).appendSibling( rankText );
            }
            return nameText.getFormattedText();
        }

        private int getTextColor( Perk perk, PerkData data )
        {
            return data.isEnabled() ? 0xFF3A3A3A : 0xFFFAFAFA;
        }

        private int getTextureY( Perk perk, PerkData data )
        {
            switch ( perk.getRarity() )
            {
                case COMMON:
                    return data.isEnabled() ? 1 : 22;
                case RARE:
                    return data.isEnabled() ? 43 : 64;
                case LEGENDARY:
                    return data.isEnabled() ? 85 : 106;
            }
            return 1;
        }

        private List< String > generateTooltips( Perk perk, PerkData data )
        {
            List< String > tooltips = Lists.newArrayList();
            if ( !data.isLearned() || data.isEnabled() )
            {
                int rank = data.getRank();
                if ( data.isLearned() )
                {
                    int usageCost = perkManager.getUsageCost( perk, rank );
                    tooltips.add( I18n.format( "gui.perks.perks.tooltip.cost_energy", usageCost ) );
                    tooltips.add( I18n.format( perk.getName() + ".description." + rank ) );
                }
                int nextRank = rank + 1;
                if ( nextRank > data.getLearnedMaxRank() && nextRank <= perk.getMaxRank() )
                {
                    if ( !tooltips.isEmpty() )
                    {
                        tooltips.add( "" );
                    }
                    tooltips.add( I18n.format( "gui.perks.perks.tooltip.next_rank_description" ) );
                    tooltips.add( I18n.format( perk.getName() + ".description." + nextRank ) );
                }
            }
            return tooltips;
        }

        public void setVisible( boolean visible )
        {
            this.visible = visible;
        }

        public void setPerk( Perk perk )
        {
            PerkData data = perkManager.getPerkStorage().getData( perk );
            this.text = getText( perk, data );
            this.textColor = getTextColor( perk, data );
            this.textureY = getTextureY( perk, data );
            this.tooltips = generateTooltips( perk, data );
        }

        @Override
        public void render( int p_render_1_, int p_render_2_, float p_render_3_ )
        {
            if ( visible )
            {
                preRender();

                blit( guiLeft + x, guiTop + y, getBlitOffset(), textureX, textureY, 114, 20, 256, 512 );

                FontRenderer fontRenderer = getMinecraft().fontRenderer;
                fontRenderer.drawString( text,
                                         guiLeft + x + 114.0F / 2.0F - fontRenderer.getStringWidth( text ) / 2.0F,
                                         guiTop + y + ( 20.0F - 8.0F ) / 2.0F,
                                         textColor );
            }
        }

        @Override
        public void renderToolTip( int p_renderToolTip_1_, int p_renderToolTip_2_ )
        {
            if ( visible && isInBox( p_renderToolTip_1_, p_renderToolTip_2_, x, y, x + 114, y + 20 ) )
            {
                preRender();

                GuiUtils.drawHoveringText( tooltips,
                                           p_renderToolTip_1_,
                                           p_renderToolTip_2_,
                                           width,
                                           height,
                                           -1,
                                           getMinecraft().fontRenderer );
            }
        }
    }

    public class PerkController
        implements IRenderable2,
                   IGuiEventListener
    {
        private boolean visible;
        private Perk perk;
        private Widget perkLearning;
        private Widget rankLearning;
        private Widget previousRank;
        private Widget nextRank;
        private Widget perkEnable;
        private Widget perkDisable;
        private List< Widget > widgets;
        private Map< Widget, List< String > > tooltipMap;

        public PerkController( int x, int y )
        {
            this.visible = true;
            this.perk = null;
            this.perkLearning = newImageButton( x, y, 80, 20, 1, 182, 21 );
            this.rankLearning = newImageButton( x, y, 20, 20, 82, 182, 21 );
            this.previousRank = newImageButton( x + 20, y, 20, 20, 103, 182, 21 );
            this.nextRank = newImageButton( x + 40, y, 20, 20, 124, 182, 21 );
            this.perkEnable = newImageButton( x + 60, y, 20, 20, 145, 182, 21 );
            this.perkDisable = newImageButton( x + 60, y, 20, 20, 166, 182, 21 );
            this.widgets = Lists.newArrayList();
            this.tooltipMap = Maps.newHashMap();

            this.widgets.addAll( Arrays.asList( this.perkLearning,
                                                this.rankLearning,
                                                this.previousRank,
                                                this.nextRank,
                                                this.perkEnable,
                                                this.perkDisable ) );
        }

        private Widget newImageButton( int x,
                                       int y,
                                       int width,
                                       int height,
                                       int textureX,
                                       int textureY,
                                       int textureYDiff )
        {
            return new ImageButton( guiLeft + x,
                                    guiTop + y,
                                    width,
                                    height,
                                    textureX,
                                    textureY,
                                    textureYDiff,
                                    PerksGui.GUI_TEXTURE,
                                    512,
                                    256,
                                    this::actionPerformed,
                                    "" );
        }

        private void actionPerformed( Button button )
        {
            if ( button == perkLearning )
            {
                perkControl( perk, Control.PERK_LEARNING );
            }
            else if ( button == rankLearning )
            {
                perkControl( perk, Control.RANK_LEARNING );
            }
            else if ( button == previousRank )
            {
                perkControl( perk, Control.PREVIOUS_RANK );
            }
            else if ( button == nextRank )
            {
                perkControl( perk, Control.NEXT_RANK );
            }
            else if ( button == perkEnable )
            {
                perkControl( perk, Control.PERK_ENABLE );
            }
            else if ( button == perkDisable )
            {
                perkControl( perk, Control.PERK_DISABLE );
            }
        }

        public void setVisible( boolean visible )
        {
            this.visible = visible;
        }

        public void setPerk( Perk perk )
        {
            this.perk = perk;
            this.tooltipMap.clear();

            String perkName = I18n.format( perk.getName() );
            PerkData data = perkManager.getPerkStorage().getData( perk );
            int perkExperience = perkManager.getPerkExperience().getValue();
            int learningCost = perkManager.getLearningCost( perk );
            List< String > tooltips;

            if ( data.isLearned() )
            {
                boolean perkEnabled = data.isEnabled();
                this.perkLearning.visible = false;
                this.rankLearning.visible = true;
                this.previousRank.visible = true;
                this.nextRank.visible = true;
                this.perkEnable.visible = !perkEnabled;
                this.perkDisable.visible = perkEnabled;

                int learnedMaxRank = data.getLearnedMaxRank();
                int nextRank = learnedMaxRank + 1;
                int maxRank = perk.getMaxRank();
                int currentRank = data.getRank();
                boolean maxRankUnlearned = nextRank <= maxRank;

                this.rankLearning.active = ( maxRankUnlearned && perkExperience >= learningCost );
                this.previousRank.active = currentRank > 1;
                this.nextRank.active = currentRank < learnedMaxRank;

                if ( maxRankUnlearned )
                {
                    String rankString = I18n.format( "gui.perks.perks.rank." + nextRank );
                    tooltips = Lists.newArrayList();
                    tooltips.add( I18n.format( "gui.perks.perks.tooltip.learning_rank", perkName, rankString ) );
                    tooltips.add( I18n.format( "gui.perks.perks.tooltip.cost_experience", learningCost ) );
                    tooltips.add( I18n.format( "gui.perks.perks.tooltip.current_experience", perkExperience ) );
                    tooltipMap.put( this.rankLearning, tooltips );
                }

                if ( this.previousRank.active )
                {
                    tooltips = Lists.newArrayList();
                    tooltips.add( I18n.format( "gui.perks.perks.tooltip.previous_rank" ) );
                    tooltipMap.put( this.previousRank, tooltips );
                }

                if ( this.nextRank.active )
                {
                    tooltips = Lists.newArrayList();
                    tooltips.add( I18n.format( "gui.perks.perks.tooltip.next_rank" ) );
                    tooltipMap.put( this.nextRank, tooltips );
                }

                tooltips = Lists.newArrayList();
                tooltips.add( I18n.format( "gui.perks.perks.tooltip.enable" ) );
                tooltipMap.put( this.perkEnable, tooltips );

                tooltips = Lists.newArrayList();
                tooltips.add( I18n.format( "gui.perks.perks.tooltip.disable" ) );
                tooltipMap.put( this.perkDisable, tooltips );
            }
            else
            {
                this.perkLearning.visible = true;
                this.rankLearning.visible = false;
                this.previousRank.visible = false;
                this.nextRank.visible = false;
                this.perkEnable.visible = false;
                this.perkDisable.visible = false;

                this.perkLearning.active = perkExperience >= learningCost;

                tooltips = Lists.newArrayList();
                tooltips.add( I18n.format( "gui.perks.perks.tooltip.learning_perk", perkName ) );
                tooltips.add( I18n.format( "gui.perks.perks.tooltip.cost_experience", learningCost ) );
                tooltips.add( I18n.format( "gui.perks.perks.tooltip.current_experience", perkExperience ) );
                tooltipMap.put( this.perkLearning, tooltips );
            }
        }

        @Override
        public void render( int p_render_1_, int p_render_2_, float p_render_3_ )
        {
            if ( visible )
            {
                for ( Widget widget : widgets )
                {
                    widget.render( p_render_1_, p_render_2_, p_render_3_ );
                }
            }
        }

        @Override
        public void renderToolTip( int p_renderToolTip_1_, int p_renderToolTip_2_ )
        {
            if ( visible )
            {
                for ( Widget widget : widgets )
                {
                    if ( widget.visible && widget.isHovered() )
                    {
                        List< String > tooltips = tooltipMap.getOrDefault( widget, Collections.emptyList() );
                        GuiUtils.drawHoveringText( tooltips,
                                                   p_renderToolTip_1_,
                                                   p_renderToolTip_2_,
                                                   width,
                                                   height,
                                                   -1,
                                                   getMinecraft().fontRenderer );
                    }
                }
            }
        }

        @Override
        public boolean mouseClicked( double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_ )
        {
            for ( Widget widget : widgets )
            {
                if ( widget.mouseClicked( p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_ ) )
                {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void mouseMoved( double p_212927_1_, double p_212927_3_ )
        {
            for ( Widget widget : widgets )
            {
                widget.mouseMoved( p_212927_1_, p_212927_3_ );
            }
        }

        @Override
        public boolean mouseReleased( double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_ )
        {
            for ( Widget widget : widgets )
            {
                if ( widget.mouseReleased( p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_ ) )
                {
                    return true;
                }
            }
            return false;
        }
    }

    public class Scrollbar
        implements IRenderable2,
                   IGuiEventListener
    {
        private final int x = 208;
        private int y = 32;
        private boolean enabled;
        private boolean clicked;
        private float scroll;

        public void setEnabled( boolean enabled )
        {
            this.enabled = enabled;
        }

        public void reset()
        {
            this.y = 32;
        }

        @Override
        public void render( int p_render_1_, int p_render_2_, float p_render_3_ )
        {
            preRender();

            blit( guiLeft + x, guiTop + y, getBlitOffset(), enabled ? 187 : 200, 182, 12, 15, 256, 512 );
        }

        @Override
        public boolean mouseClicked( double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_ )
        {
            clicked = false;
            if ( enabled && isInBox( ( int )p_mouseClicked_1_, ( int )p_mouseClicked_3_, 209, y, 220, y + 15 ) )
            {
                clicked = true;
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged( double p_mouseDragged_1_,
                                     double p_mouseDragged_3_,
                                     int p_mouseDragged_5_,
                                     double p_mouseDragged_6_,
                                     double p_mouseDragged_8_ )
        {
            if ( enabled && clicked )
            {
                y = MathHelper.clamp( ( int )p_mouseDragged_3_ - guiTop - 7, 32, 32 + 140 - 15 );
                scroll = MathHelper.clamp( ( y - 32.0F ) / ( 140.0F - 15.0F ), 0.0F, 1.0F );
                updateScroll( scroll );
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseScrolled( double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_ )
        {
            if ( enabled )
            {
                int i = ( perks.size() + 7 - 1 ) - 7;
                scroll = MathHelper.clamp( scroll - ( float )( p_mouseScrolled_5_ / i ), 0.0F, 1.0F );
                y = MathHelper.clamp( ( int )( scroll * ( 140 - 15 ) ) + 32, 32, 32 + 140 - 15 );
                updateScroll( scroll );
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased( double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_ )
        {
            boolean result = clicked;
            clicked = false;
            return result;
        }
    }
}
