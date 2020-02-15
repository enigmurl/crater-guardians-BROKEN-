package com.enigmadux.craterguardians.players;

import com.enigmadux.craterguardians.Character;

import java.util.ArrayList;

import enigmadux2d.core.quadRendering.QuadRenderer;
import enigmadux2d.core.quadRendering.QuadTexture;

/** Updated Player Class, much more efficient
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class Player implements Character {


    /** The center x in openGL terms
     *
     */
    private float x;
    /** The center y in openGL terms
     *
     */
    private float y;

    /** The radius in openGL terms (half the width, half the height)
     *
     */
    private float r;


    /** All the entities that need to be rendered
     *
     */
    protected ArrayList<QuadTexture> entities;

    /** This is the health variable, by default if it's over 0 that indicates this character is alive
     *
     */
    protected int health;

    /** Default Constructor
     *
     * @param x the center x in openGL terms
     * @param y the center y in openGL terms
     * @param r the radius in openGL terms (half the width, half the height)
     */
    public Player(float x,float y,float r){
        //assign variables to attributes
        this.x = x;
        this.y = y;
        this.r = r;

        //we have to add our health bar


        this.addEntities();
    }


    /** Draws the player and sub components, given a quad renderer and the model view projection matrix
     *
     * @param mvpMatrix a 4x4 model view projection matrix that describes the outside transforms
     * @param quadRenderer a QuadRenderer object, that helps actually put textures onto the screen
     */
    public void draw(float[] mvpMatrix,QuadRenderer quadRenderer){
        quadRenderer.renderQuads(this.entities,mvpMatrix);
    }


    /** This method should add the needed entities to the "entities" array list, so that it can be drawn.
     * If you want these entities to be drawn below, add it to the beginning of the array, using
     * add(0,[object]), if you want to be drawn at the very top, use add([object])
     *
     */
    protected abstract void addEntities();

    /** Damages the player along, that's it for now but maybe in the future
     *
     * @param damage the amount of damage the character must take, a >0 value will decrease the health, <0 will increase, =0 will do nothing
     */
    @Override
    public void damage(int damage) {
        this.health -= damage;
    }
}
