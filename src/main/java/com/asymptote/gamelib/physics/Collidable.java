package com.asymptote.gamelib.core;

import org.joml.Vector3f;

public abstract class Collidable extends Transform {
    private float weight;
    private Vector3f velocity;
    private Vector3f rotVelocity;
     
    public float getWeight() { return this.weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public float getVelocity() { return this.velocity; }
    public float setVelocityX(float x) { this.velocity.x = x; }
    public float setVelocityY(float y) { this.velocity.y = y; }
    public float setVelocityZ(float z) { this.velocity.z = z; }
    public float setVelocity(float x, float y, float z)
    {
        this.velocity.x = x;
        this.velocity.y = y;
        this.velocity.z = z;
    }

    public float getRotVelocity() { return this.rotVelocity; }
    public float setRotVelocityX(float x) { this.rotVelocity.x = x; }
    public float setRotVelocityY(float y) { this.rotVelocity.y = y; }
    public float setRotVelocityZ(float z) { this.rotVelocity.z = z; }
    public float setRotVelocity(float x, float y, float z)
    {
        this.rotVelocity.x = x;
        this.rotVelocity.y = y;
        this.rotVelocity.z = z;
    }

    public abstract Collidable isColliding(Collidable obstacle);
}