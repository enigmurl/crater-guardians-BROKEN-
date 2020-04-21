package enigmadux2d.core.quadRendering;

import java.util.Comparator;

/** Todo, in the future, need to make it so that stuff of the same layer are rendered together
 * Basically need
 */
public class QuadTextureComparator implements Comparator<QuadTexture> {
    @Override
    public int compare(QuadTexture o1, QuadTexture o2) {
        return o1.getTexture() - o2.getTexture();
    }
}
