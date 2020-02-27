package com.enigmadux.craterguardians.GUILib;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.enigmadux.craterguardians.Characters.Player;
import com.enigmadux.craterguardians.CraterBackend;
import com.enigmadux.craterguardians.CraterRenderer;
import com.enigmadux.craterguardians.FileStreams.PlayerData;
import com.enigmadux.craterguardians.values.LayoutConsts;
import com.enigmadux.craterguardians.MathOps;
import com.enigmadux.craterguardians.R;

import enigmadux2d.core.EnigmaduxComponent;
import enigmadux2d.core.shapes.TexturedRect;

/** The Character Select Layout where user can upgrade and select characters
 *
 * @author Manu Bhat
 * @version BETA
 *
 */
public class CharacterSelect extends EnigmaduxComponent {

    //the upgrade cost given the current level
    private static final int[] UPGRADE_COSTS = new int[] {10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,
            10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,
            10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,10,
            };

    //the maximum level of any player
    private static final int MAX_LEVEL = UPGRADE_COSTS.length;
    //the amount of columns of the character icon 2d array
    private static final int CHARACTER_ICON_W = 4;
    //the amount of rows of the character icon 2d array
    private static final int CHARACTER_ICON_H = 2;

    //button used to go back home
    private HomeButton homeButton;
    //this is a textured rect that is at the bottom only for aesthetics
    private TexturedRect bottomEdge;

    //the button that is used to upgrade the characters
    private Upgradable upgradeButton;

    //the button that is used to select the characters
    private Button selectButton;


    //shows the experience
    private MatieralsBar matieralsBar;


    //an array of all the icons
    private Button[] characterIcons;


    private Player currentPlayer = null;

    /** Default Constructor
     *
     */
    public CharacterSelect(final CraterBackend backend, final CraterRenderer renderer, PlayerData playerData,MatieralsBar matieralsBar){
        super(-1,-1,2,2);
        float scaleX = (float) (LayoutConsts.SCREEN_HEIGHT )/ (LayoutConsts.SCREEN_WIDTH);

        this.homeButton = new HomeButton(-0.8f,0f,0.35f,backend,renderer);

        this.bottomEdge = new TexturedRect(-1f,-1f,2,0.5f);

        this.upgradeButton = new Upgradable(null,-0.6f,-0.75f,playerData);

        this.selectButton = new Button("Select --",0.6f,-0.75f,0.3f,0.15f,0.1f,LayoutConsts.CRATER_TEXT_COLOR,false) {
            @Override
            public boolean isSelect(MotionEvent e) {
                return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
            }

            @Override
            public void onRelease() {
                super.onRelease();

                if (currentPlayer != null) {
                    renderer.setPlayer(CharacterSelect.this.currentPlayer);
                    CharacterSelect.this.hide();

                    //this basically makes it go the home screen, I think we should find better way however
                    renderer.exitGame();
                    backend.killEndGamePausePeriod();
                    backend.setCurrentGameState(CraterBackend.GAME_STATE_HOMESCREEN);
                }
            }
        };


        this.characterIcons = new Button[PlayerData.CHARACTERS.length];
        for (int i  = 0;i<PlayerData.CHARACTERS.length;i++){
            float x = 1.6f * (i % CHARACTER_ICON_W)/(float) CHARACTER_ICON_W - 1 + 0.4f;
            float y = 1 - 2 * (i/CHARACTER_ICON_W % CHARACTER_ICON_H)/(float) CHARACTER_ICON_H - 1.5f/CHARACTER_ICON_H ;

            Log.d("CHARACTER SELECT","deltX : " + x + " y "  + y );

            TexturedRect image = new TexturedRect(x,y,scaleX * 1.75f/CHARACTER_ICON_W,1.75f/CHARACTER_ICON_W);

            final int charNum = i;
            this.characterIcons[i] = new Button(image) {
                @Override
                public boolean isSelect(MotionEvent e) {
                    return this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
                }

                @Override
                public void onRelease() {
                    super.onRelease();
                    CharacterSelect.this.currentPlayer = PlayerData.CHARACTERS[charNum];
                }
            };


        }

        this.matieralsBar = matieralsBar;
    }


    /** Loads the GL texture of all sub components
     *
     */
    public void loadGLTexture(Context context){


        this.upgradeButton.loadGLTexture();

        this.selectButton.loadGLTexture();

        this.homeButton.loadGLTexture(context, R.drawable.home_button);

        this.bottomEdge.loadGLTexture(context,R.drawable.character_select_bottom);


        int[] characterTextures = new int[] {R.drawable.kaiser_info,R.drawable.ryze_info};

        for (int i = 0;i<this.characterIcons.length;i++){
            this.characterIcons[i].loadGLTexture(context,characterTextures[i]);
        }
    }

    /** Draws the layout and all sub components
     *
     * @param parentMatrix matrix that represents how to manipulate it to the world coordinates
     */
    @Override
    public void draw(float[] parentMatrix) {
        this.bottomEdge.draw(parentMatrix);
        this.upgradeButton.draw(parentMatrix);
        this.selectButton.draw(parentMatrix);
        this.homeButton.draw(parentMatrix);

        for (int i = 0, size = this.characterIcons.length; i < size; i++) {
            this.characterIcons[i].draw(parentMatrix);
        }


        this.matieralsBar.draw(parentMatrix);

    }


    /** Processed touch events
     *
     * @param e the MotionEvent describing how the user interacted with the screen
     * @return whether or not the touch event has been processed
     */
    @Override
    public boolean onTouch(MotionEvent e) {
        if (homeButton.onTouch(e)) return true;

        if (upgradeButton.onTouch(e)) return true;

        if (selectButton.onTouch(e)) return true;

        for (int i = this.characterIcons.length-1;i>=0;i--){
            EnigmaduxComponent cmp = this.characterIcons[i];
            if (cmp.onTouch(e)) {
                if (e.getActionMasked() == MotionEvent.ACTION_UP) {
                    this.currentPlayer = PlayerData.CHARACTERS[i];
                    this.upgradeButton.setPlayer(this.currentPlayer);
                    this.upgradeButton.setText(this.currentPlayer.getPlayerLevel() >= MAX_LEVEL ? "MAX LEVEL (" + MAX_LEVEL + ")":"Level: " + this.currentPlayer.getPlayerLevel() + " \n Upgrade for " + UPGRADE_COSTS[this.currentPlayer.getPlayerLevel()]);
                    this.selectButton.setText("Select " + this.currentPlayer);
                }
                return true;
            }
        }

        return false;
    }

    /** Hides subc omponents
     *
     */
    @Override
    public void hide() {
        super.hide();
        for (int i = 0,size = this.characterIcons.length;i<size;i++){
            this.characterIcons[i].hide();
        }
        this.bottomEdge.hide();

        this.selectButton.hide();

        this.upgradeButton.hide();

        this.homeButton.hide();

        this.matieralsBar.hide();
    }

    /** Shows the sub components
     *
     */
    @Override
    public void show() {
        super.show();
        for (int i = 0,size = this.characterIcons.length;i<size;i++){
            this.characterIcons[i].show();
        }
        this.bottomEdge.show();

        this.selectButton.show();

        this.upgradeButton.show();
        this.homeButton.show();

        this.matieralsBar.show();
    }

    /** In the character select layout
     *
     * @author Manu Bhat
     * @version BETA
     */
    private class Upgradable extends Button{
        //the minimum width of the box
        private static final float BOX_WIDTH = 0.3f;
        //the maximum width of the box
        private static final float BOX_HEIGHT = 0.15f;
        //the height of the font
        private static final float FONT_HEIGHT = 0.1f;

        /**
         *  Used to refer to the xp value
         */
        private final PlayerData playerData;

        //the player object that needs to upgrade, but it does the upgrading for the whole class
        private Player player;


        /** Default Constructor
         *
         * @param player the player class this button is associated with
         * @param x the center deltX
         * @param y the center y
         */
        public Upgradable(Player player,float x,float y,PlayerData playerData) {
            super((player != null) ? "UPGRADE: " + player.getPlayerLevel(): "UPGRADE: -",x,y,BOX_WIDTH,BOX_HEIGHT,FONT_HEIGHT, LayoutConsts.CRATER_TEXT_COLOR,false);
            this.playerData = playerData;
            this.player = player;
        }

        /** Sets the current player of this button
         *
         */
        public void setPlayer(Player player){
            this.player = player;
            if (this.player.getPlayerLevel() >= MAX_LEVEL || PlayerData.getExperience() < UPGRADE_COSTS[this.player.getPlayerLevel()] ){
                this.setShader(1,0.75f,0.75f,1);
            } else {
                this.setShader(0.75f,1,0.5f,1);
            }
        }

        /** Sees if this button is being selected
         *
         * @param e the MotionEvent describing how the user interacted with the screen
         * @return whether or not this button is being selected by the main pointer
         */
        @Override
        public boolean isSelect(MotionEvent e) {
            if (this.player == null) return false;
            return this.player.getPlayerLevel() < MAX_LEVEL && PlayerData.getExperience() >= UPGRADE_COSTS[this.player.getPlayerLevel()] && this.visible && this.isInside(MathOps.getOpenGLX(e.getRawX()),MathOps.getOpenGLY(e.getRawY()));
        }

        /**
         *
         */
        public void onRelease(){
            super.onRelease();
            if (player == null) return;

            player.setPlayerLevel(player.getPlayerLevel()+1);
            playerData.updateXP(PlayerData.getExperience() - UPGRADE_COSTS[this.player.getPlayerLevel()]);
            if (this.player.getPlayerLevel() >= MAX_LEVEL || PlayerData.getExperience() < UPGRADE_COSTS[this.player.getPlayerLevel()] ){
                this.setShader(1,0.75f,0.75f,1);
            } else {
                this.setShader(0.75f,1,0.5f,1);
            }
            this.setText(this.player.getPlayerLevel() >= MAX_LEVEL ? "MAX LEVEL (" + MAX_LEVEL + ")":"Level: " + this.player.getPlayerLevel() + " \n Upgrade for " + UPGRADE_COSTS[this.player.getPlayerLevel()]);

            CharacterSelect.this.matieralsBar.updateResources();
        }
    }

}
