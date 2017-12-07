package com.asymptote.gamelib.physics;

import com.asymptote.gamelib.graphics.Transformable;
import org.joml.Vector3f;

public abstract class Collidable extends Transformable {
    private float weight;
    private Vector3f velocity;
    private Vector3f rotVelocity;
     
    public float getWeight() { return this.weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public Vector3f getVelocity() { return this.velocity; }
    public void setVelocityX(float x) { this.velocity.x = x; }
    public void setVelocityY(float y) { this.velocity.y = y; }
    public void setVelocityZ(float z) { this.velocity.z = z; }
    public void setVelocity(float x, float y, float z)
    {
        this.velocity.x = x;
        this.velocity.y = y;
        this.velocity.z = z;
    }

    public Vector3f getRotVelocity() { return this.rotVelocity; }
    public void setRotVelocityX(float x) { this.rotVelocity.x = x; }
    public void setRotVelocityY(float y) { this.rotVelocity.y = y; }
    public void setRotVelocityZ(float z) { this.rotVelocity.z = z; }
    public void setRotVelocity(float x, float y, float z)
    {
        this.rotVelocity.x = x;
        this.rotVelocity.y = y;
        this.rotVelocity.z = z;
    }

    public abstract Collidable isColliding(Collidable obstacle);
}