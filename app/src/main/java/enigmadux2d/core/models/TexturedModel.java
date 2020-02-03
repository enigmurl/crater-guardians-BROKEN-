package enigmadux2d.core.models;

import enigmadux2d.core.textures.BasicTexture;

/** While a mesh only stores data about the vertices, this also has an actual texture
 *
 * Portions of code inspired from https://www.youtube.com/watch?v=SPt-aogu72A
 *
 * @author Manu Bhat
 * @version BETA
 */
public class TexturedModel {

    //here is where the mesh is stored, (info about vertices, texture cords, etc)
    private Mesh modelMesh;
    //here is what separates it from just a mesh, it has an actual texture, which means that it can
    //have faces that can be from images
    private BasicTexture texture;


    //how much to translate the texture coordinates to the right
    private float deltaTextureX;
    //how much to translate the texture coordinates downward
    private float deltaTextureY;

    //an array of four floats that limits the rgba channels all are 0 to 1
    private float[] shader = new float[] {1,1,1,1};


    /** Default Constructor
     *
     * @param modelMesh a raw mesh that specifies info about the actual vertices of this object
     * @param texture a texture object that is used to color the faces of the object, not to be
     *                confused with texture coordinates
     */
    public TexturedModel(Mesh modelMesh, BasicTexture texture){
        this.modelMesh = modelMesh;
        this.texture = texture;

    }

    /** Gets the model mesh (info about vertices, texture cords, etc)
     *
     * @return the model mesh (info about vertices, texture cords, etc)
     */
    public Mesh getModelMesh() {
        return this.modelMesh;
    }

    /** Gets a texture object that is used to color the faces of the object, not to be confused with
     * texture coordinates
     *
     * @return a texture object that is used to color the faces of the object, not to be confused with
     *           texture coordinates
     */
    public BasicTexture getTexture() {
        return this.texture;
    }

    /** Gets how much to translate texture coordinates right
     *
     * @return how much to translate texture coordinates right
     */
    public float getDeltaTextureX() {
        return this.deltaTextureX;
    }

    /** Gets how much to translate texture coordinate downward
     *
     * @return how much to translate texture coordinates downward
     */
    public float getDeltaTextureY(){
        return this.deltaTextureY;
    }

    /** Gets how much to limit the rgba channels
     *
     * @return a float[4], each 0 to 1 where 0 is fully transparent, 1 is opaque
     */
    public float[] getShader() {
        return this.shader;
    }
}
