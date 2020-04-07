package enigmadux2d.physics;

public abstract class Hitbox {
    private float mass;
    private float resistance;
    //mass, and resitance to objects going inside of it
    public Hitbox(float mass,float resistance){
        this.mass = mass;
        this.resistance = resistance;
    }
}
