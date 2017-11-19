//==============================================================================
// Date Created:		03 July 2014
// Last Updated:		03 July 2014
//
// File name:			Utils.java
// Author(s):			M. Matthew Hydock
//
// File description:	A collection of useful static methods.
//==============================================================================

package com.asymptote.gamelib.core;

import org.lwjgl.opengl.GL11;

public class Utils
{	
	public static final boolean DEBUG = false;
	
	public static float[] HSVtoRGB(float h, float s, float v)
	{
		if (s == 0)
			return new float[] {v,v,v};
		
		int i;
		float f,p,q,t;
		
		h /= 60;		// get the sector
		i = (int)h;		// get whole part of sector
		f = h - i;		// get fractional part of sector
		
		p = v * (1 - s);
		q = v * (1 - s * f);
		t = v * (1 - s * (1 - f));
		
		switch (i)
		{
			case 0: return new float[] {v,t,p};
			case 1: return new float[] {q,v,p};
			case 2: return new float[] {p,v,t};
			case 3: return new float[] {p,q,v};
			case 4: return new float[] {t,p,v};
			default: return new float[] {v,p,q};
		}
	}
	
	public static void clearGlErrors()
	{
		if (!DEBUG)
			return;
		
		int error;
		
		System.out.println("-----------------------------\nClearing GL Errors");
		
		while ((error = GL11.glGetError()) != GL11.GL_NO_ERROR)
			System.out.println(error);
		
		System.out.println("-----------------------------");
	}
	
	public static void printGlError()
	{
		if (!DEBUG)
			return;
		
		printGlError("");
	}
	
	public static void printGlError(String message)
	// Check to see if there were any gl errors
	{
		if (!DEBUG)
			return;
		
		int error = GL11.glGetError();
		
		String text = String.format("%1$-20s  %2$s", error, message);
		
		if (error == GL11.GL_NO_ERROR)
			System.out.println("DEBUG -- " + text);
		else
			System.err.println("ERROR -- " + text);
	}
}
