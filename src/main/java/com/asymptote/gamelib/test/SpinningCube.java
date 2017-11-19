package com.gamelib.test;

import com.asymptote.gamelib.core.Utils;
import com.asymptote.gamelib.primitives.Cube;

public class SpinningCube extends Cube
{
	private float hue = 0.0f;

	public SpinningCube()
	{
		super();
	}
	
	public SpinningCube(float size)
	{
		super(size);
	}
	
	@Override
	public void update(double delta)
	{
		//System.out.println("Updating hue...");
		
		float change = (float)(delta*10);
		
		hue += change;
		
		if (hue > 360)
			hue -= 360;
		
		//setOrientation(hue, 1, 1, 1);
		rotate(change, 1, 1, 1);
			
		float[] color = Utils.HSVtoRGB(hue, 1.0f, 1.0f);
		setColor(color[0],color[1],color[2],1.0f);
		
		//System.out.println("Hue updated.");
	}
}
