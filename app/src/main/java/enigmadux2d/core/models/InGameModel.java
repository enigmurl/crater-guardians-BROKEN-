package enigmadux2d.core.models;

/** This is the base class for actual models. The way to use it is extend this, and manipulate the variables here.
 *
 * @author Manu Bhat
 * @version BETA
 */
public abstract class InGameModel {
    //this is the actual model that stores the textures and vertex data
    protected TexturedModel texturedModel;

    //this is the transform that does scaling, moving, and all of that stuff
    protected final float[] transform = new float[16];

    /** Default Constructor
     *
     * @param texturedModel the model that stores the texture and vertex data
     */
    public InGameModel(TexturedModel texturedModel){
        this.texturedModel = texturedModel;
    }

    /** Gets the textured model that this is based on
     *
     * @return the textured model that this is based on
     */
    public TexturedModel getTexturedModel() {
        return this.texturedModel;
    }

    /** Gets the transform
     *
     * @return the transform
     */
    public float[] getTransform() {
        return this.transform;
    }
}
