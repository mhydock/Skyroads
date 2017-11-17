//==============================================================================
// Date Created:		03 April 2014
// Last Updated:		03 July 2014
//
// File name:			Clock.java
// Author(s):			M. Matthew Hydock
//
// File description:	A class to manage global game times. Used to control
//						frames/updates per second limits.
//==============================================================================

package com.gamelib.core;

/**
 * Time management static class. All values are expressed as
 * seconds (or parts of seconds).
 * 
 * @author hm
 *
 */
public class Clock
{
	public static final int DEFAULT_FPS = 60;
	public static final double DEFAULT_SPF = 1.0/DEFAULT_FPS;
	public static final double NINM = 1000000;			// how many nanoseconds in a millisecond, for convenience.
	public static final double NSEC = 1000000000;		// how many nanoseconds in a second, for convenience.
	public static final double MAX_DELTA = 0.25 * NSEC;	// arbitrary value significantly larger than NSPF.
	
	private static int goalFPS = DEFAULT_FPS;
	private static double goalSPF = DEFAULT_SPF;
	
	private static double totalTime = 0;
	private static double accumTime = 0;
	private static double deltaTime = 0;
	private static double frameTime = 0;
	private static double lastTime = 0;
	private static double currTime = 0;	
	
	public static void setGoalFPS(int fps)
	{
		goalFPS = fps;
		goalSPF = 1.0/goalFPS;
	}	
	
	public static double total()
	{
		return totalTime;
	}
	
	public static double accum()
	{
		return accumTime;
	}
	
	public static double fixDelta()
	{
		return goalSPF;
	}
	
	public static double delta()
	{
		return deltaTime;
	}
	
	public static double frame()
	{
		return frameTime;
	}
	
	public static void step()
	{
		totalTime += goalSPF;
		accumTime -= goalSPF;
	}
	
	public static void update()
	{
		lastTime = (currTime == 0)?System.nanoTime()/NSEC:currTime;
		currTime = System.nanoTime()/NSEC;
		deltaTime = currTime - lastTime;
		frameTime = Math.min(deltaTime,MAX_DELTA);
		accumTime += frameTime;
	}
}
