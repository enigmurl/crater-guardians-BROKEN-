package enigmadux2d.core.textures;

/** Stores a texture ID, that an object can use to draw the contents of the texture too
 *
 * Portions of code inspired from https://www.youtube.com/watch?v=SPt-aogu72A
 *
 * @author Manu Bhat
 * @version BETA
 */
public class BasicTexture {

    //this is the id of the texture that this class holds. It is given to us by openGL, we can use it
    //to "index" the actual texture contents (the bitmap) in memory
    private int textureID;



    /** Default Constructor
     *
     * @param textureID this is the id of the texture that this class holds. It is given to us by openGL, we can use it
     *                  to "index" the actual texture contents (the bitmap) in memory
     */
    public BasicTexture(int textureID){
        this.textureID = textureID;
    }

    /** Get the texture ID, that can be used to draw triangles with textures
     *
     * @return this is the id of the texture that this class holds. It is given to us by openGL, we can use it
     *         to "index" the actual texture contents (the bitmap) in memory
     */
    public int getTextureID() {
        return textureID;
    }
}
