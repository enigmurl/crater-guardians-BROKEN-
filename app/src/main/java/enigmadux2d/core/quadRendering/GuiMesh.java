package enigmadux2d.core.quadRendering;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class GuiMesh extends QuadMesh {

    IntBuffer indices;
    int[] posses;
    public GuiMesh(float[] positions) {
        super(positions);

        int[] posses = new int[] {
            0,4,1,
            4,1,5,
            1,5,2,
            5,2,6,
            2,6,3,
            6,3,7,

            4,8,5,
            8,5,9,
            5,9,6,
            9,6,10,
            6,10,7,
            10,7,11,

            8,12,9,
            12,9,13,
            9,13,10,
            13,10,14,
            10,14,11,
            14,11,15,
        };



        this.indices = this.pushDataInIntBuffer(posses);
    }

    /** OpenGL is written mostly in C, which understands buffers better than java arrays. So we have
     * to convert the data into int buffers
     *
     * @param data the data that needs to be stored into a int buffer
     * @return a IntBuffer object that has the same contents as the array
     */
    private IntBuffer pushDataInIntBuffer(int[] data){
        //this is allocating memory by saying the data length * the amount of bytes in a Float
        //allocates memory for a byte buffer in native order
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * Integer.SIZE/Byte.SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());

        //convert to an int buffer
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        //add the data to the float buffer
        intBuffer.put(data);
        //it was in write mode, now make it into read mode
        intBuffer.flip();

        return intBuffer;
    }

}
