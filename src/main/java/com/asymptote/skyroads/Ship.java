package com.asymptote.skyroads;

import java.util.ArrayList;
import java.util.List;

import com.asymptote.gamelib.core.GameObject;
import com.asymptote.gamelib.core.Shader;
import com.asymptote.gamelib.core.ShaderProgram;
import com.asymptote.gamelib.core.Shape;
import com.asymptote.gamelib.primitives.Cube;
import com.asymptote.gamelib.primitives.PointCloud;

public class Ship implements GameObject
{
	Shape mesh = new Cube().setScale(2, 2, 2).setOrigin(-1f, -1f, 0);

	private static float DEFAULT_HORZ_SPEED = 10;
	private static float DEFAULT_ACCELERATE = 20f;
	private static float DEFAULT_MAX_VELOCITY = 100f;
	private static float DEFAULT_THRUST = 100f;
	private static float TOUCH_FACTOR = 1.5f;
	
	private float gFactor = Level.GRAVITY_CONTANT;

	private float horzSpeed = DEFAULT_HORZ_SPEED;
	private float maxVel = DEFAULT_MAX_VELOCITY;
	private float accel = DEFAULT_ACCELERATE;
	
	private float maxThrust = DEFAULT_THRUST;
	private float thrust = 0;
	
	private float[] velocity = new float[3]; // component-wise change in position

	private float horzPos;
	private float distance;
	private float altitude;
	
	private boolean moveLeft;
	private boolean moveRight;
	private boolean accelerate;
	private boolean decelerate;
	
	private boolean rising;
	private float touching;
	
	private List<float[]> top;
	private List<float[]> left;
	private List<float[]> right;
	private List<float[]> bottom;
	private List<float[]> front;
	private List<float[]> back;
	
	private ShaderProgram pointShader = new ShaderProgram("src/glsl/points.vert", "src/glsl/points.frag");
	private PointCloud collideCloud;
	
	public Ship() {	}
	
	public Ship(float horzSpeed, float maxVel, float accel, float thrust)
	{
		this.horzSpeed = horzSpeed;
		this.maxVel = maxVel;
		this.accel = accel;
		this.maxThrust = thrust;
	}
	
	public void moveLeft(boolean move)
	{
		moveLeft = move;
	}
	
	public void moveRight(boolean move)
	{
		moveRight = move;
	}
	
	public void accelerate(boolean acc)
	{
		accelerate = acc;
	}
	
	public void decelerate(boolean dec)
	{
		decelerate = dec;
	}
	
	public void rising(boolean rise)
	{
		rising = rise;
		
		if (!rising)
			thrust = 0;
	}
	
	public void reset()
	{
		horzPos = 0;
		distance = 0;
		altitude = 0;
		thrust = 0;

		velocity[0] = 0;
		velocity[1] = 0;
		velocity[2] = 0;

		moveLeft = false;
		moveRight = false;
		accelerate = false;
		decelerate = false;
		rising = false;
	}
	
	public float getHorzPos()
	{
		return horzPos;
	}
	
	public float getDistance()
	{
		return distance;
	}
	
	public float getAltitude()
	{
		return altitude;
	}
	
	public float getSpeed()
	{
		return velocity[1];
	}
	
	public void setSpeed(float speed)
	{
		velocity[1] = speed;
	}
		
	public float getMaxSpeed()
	{
		return maxVel;
	}
	
	public float[] getVelocity()
	{
		return velocity;
	}
	
	public void setVelocity(float x, float y, float z)
	{
		velocity[0] = x;
		velocity[1] = y;
		velocity[2] = z;
	}
	
	public float[] getPosition()
	{
		return new float[] {horzPos, distance, altitude};
	}
	
	public void setPosition(int[] pos)
	{
		setPosition((float)pos[0], (float)pos[1], (float)pos[2]);
	}
	
	public void setPosition(float[] pos)
	{
		setPosition(pos[0], pos[1], pos[2]);
	}
	
	public void setPosition(float x, float y, float z)
	{
		horzPos = x;
		distance = y;
		altitude = z;
	}
	
	public void setGravity(float g)
	{
		gFactor = g;
	}
	
	public boolean isTouching()
	{
		return touching > 0;
	}
	
	public List<float[]> getTop()
	{
		if (top == null)
		{
			top = new ArrayList<float[]>();
			
			top.add(new float[]{-.1f,    0, 2});
			top.add(new float[]{ .1f,    0, 2});
			top.add(new float[]{   0, -.1f, 2});
			top.add(new float[]{   0,  .1f, 2});
		}
		
		return top;
	}
	
	public List<float[]> getLeft()
	{
		if (left == null)
		{
			left = new ArrayList<float[]>();
			
			left.add(new float[]{-1f, 0, 1});
		}
		
		return left;
	}
	
	public List<float[]> getRight()
	{
		if (right == null)
		{
			right = new ArrayList<float[]>();
			
			right.add(new float[]{1f, 0, 1});
		}
		
		return right;
	}
	
	public List<float[]> getBottom()
	{
		if (bottom == null)
		{
			bottom = new ArrayList<float[]>();
			
			bottom.add(new float[]{-.1f,    0, 0});
			bottom.add(new float[]{ .1f,    0, 0});
			bottom.add(new float[]{   0, -.1f, 0});
			bottom.add(new float[]{   0,  .1f, 0});
		}
		
		return bottom;
	}
	
	public List<float[]> getFront()
	{
		if (front == null)
		{
			front = new ArrayList<float[]>();
			
			front.add(new float[]{0, 1, 1});
		}
		
		return front;
	}
	
	public List<float[]> getBack()
	{
		if (back == null)
		{
			back = new ArrayList<float[]>();
			
			back.add(new float[]{0, -1, 1});
		}
		
		return back;
	}
	
	private PointCloud getCollisionPoints()
	{
		if (collideCloud == null)
		{
			collideCloud = new PointCloud();
			
			collideCloud.addPoints(getTop());
			collideCloud.addPoints(getLeft());
			collideCloud.addPoints(getRight());
			collideCloud.addPoints(getBottom());
			collideCloud.addPoints(getFront());
			collideCloud.addPoints(getBack());
		}
		
		return collideCloud;
	}
	
	public void bounce()
	{
		bounce(false);
	}
	
	public void bounce(boolean allowJump)
	{
		if (velocity[2] == 0)
			return;
		
		velocity[2] = -.5f * velocity[2];
		
		if ((int)(velocity[2] * 10) == 0)
			velocity[2] = 0;
		
		//System.out.println("Did bounce. Velocity is now: " + velocity[2]);
		if (allowJump)
			touching = TOUCH_FACTOR;
	}
	
	public void thrust()
	{
		if (!isTouching())
			return;
		
		velocity[2] = maxThrust/4;
		rising = true;
		touching = 0;
		//System.out.println("THRUSTING");
	}
	
	@Override
	public void update(double delta)
	{
		velocity[0] = 0;

		if (moveLeft)
			velocity[0] = -(float)delta*horzSpeed;
		
		if (moveRight)
			velocity[0] = (float)delta*horzSpeed;
		
		if (accelerate)
			velocity[1] += delta*accel;
		
		if (decelerate)
			velocity[1] -= delta*(0.75*accel);
	
		if (velocity[1] > maxVel)
			velocity[1] = maxVel;
		
		if (velocity[1] < 0)
			velocity[1] = 0;
		
		if (rising)
		{
			velocity[2] += delta*(maxThrust-thrust);
			thrust += delta*2*maxThrust;
			
			if (thrust >= maxThrust)
			{
				rising = false;
				thrust = 0;
			}
		}
		
		velocity[2] -= delta*gFactor;
		touching -= delta*4;
		
		if (touching < 0)
			touching = 0;
		
		horzPos += velocity[0];
		distance += velocity[1]*delta;
		altitude += velocity[2]*delta;
		
		//System.out.println(velocity[2]);
		
		mesh.setLocation(horzPos, distance, altitude);
		getCollisionPoints().setLocation(horzPos, distance, altitude);
	}

	@Override
	public void render()
	{
		// TODO Auto-generated method stub
		mesh.render();
		
		ShaderProgram prev = ShaderProgram.getGlobal();
		ShaderProgram curr = pointShader;
		
		prev.disable();
		curr.use();
		getCollisionPoints().render();
		curr.disable();
		prev.use();
	}
}
