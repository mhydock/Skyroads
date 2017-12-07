//==============================================================================
// Date Created:		11 April 2014
// Last Updated:		22 July 2014
//
// File name:			Texture.java
// Author(s):			M. Matthew Hydock
//
// File description:	An abstract class for loading/binding textures.
//==============================================================================

package com.asymptote.gamelib.graphics;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;

import static com.asymptote.gamelib.graphics.Utils.*;

import java.util.List;
import java.util.ArrayList;

public abstract class Texture implements IGLObject
{	
	private static final int MAX_LAYERS = 10;
	private static int TEX_LAYER = GL_TEXTURE0;
	
	private static List<Texture> TEX_STACK = new ArrayList<Texture>(MAX_LAYERS);
	
	protected int texID;
	protected int texLayer = -1;

	protected int width;
	protected int height;
	protected int components;
	
	public int width()
	{
		return width;
	}
	
	public int height()
	{
		return height;
	}
	
	public int components()
	{
		return components;
	}
	
	public int texID()
	{
		return texID;
	}
	
	public int layer()
	{
		return texLayer-GL_TEXTURE0;
	}
	
	public void bind()
	{
		if (texLayer >= 0)
		{
			System.out.println("Cannot bind: Texture is already bound.");
			return;
		}
		
		if (TEX_LAYER == GL_TEXTURE0 + MAX_LAYERS)
		{
			System.out.println("Cannot bind: Too many textures already bound. Ignoring this texture...");
			return;
		}
		
		texLayer = TEX_LAYER;
		
		glActiveTexture(texLayer);
		glBindTexture(GL_TEXTURE_2D, texID);
		printGlError("Binding texture " + texID + " on layer " + layer());

		TEX_LAYER++;
		TEX_STACK.add(this);
	}
	
	public void unbind()
	{
		clearGlErrors();
		
		if (texLayer == -1)
		{
			System.out.println("Cannot unbind: Texture is not bound.");
			return;
		}
		
		if (texLayer < TEX_LAYER-1)
		{
			List<Texture> tx = new ArrayList<Texture>();
			for (Texture t : TEX_STACK)
				if (t.layer() > texLayer)
					tx.add(t);
			
			glActiveTexture(texLayer);
			glBindTexture(GL_TEXTURE_2D, 0);
			printGlError("Unbinding texture " + texID);
			
			for (Texture t : tx)
			{
				glActiveTexture(t.layer());
				glBindTexture(GL_TEXTURE_2D, 0);
				printGlError("Unbinding texture " + t.texID() + " from layer " + t.layer());
				
				glActiveTexture(t.layer() - 1);
				glBindTexture(GL_TEXTURE_2D, t.texID());
				printGlError("Rebinding texture " + t.texID() + " onto layer " + t.layer());
			}
		}
		
		TEX_LAYER--;
		glActiveTexture(TEX_LAYER);
		glBindTexture(GL_TEXTURE_2D, 0);
		printGlError("Unbinding texture from layer " + (TEX_LAYER-GL_TEXTURE0));
		
		texLayer = -1;
		
		TEX_STACK.remove(this);
	}
	
	/**
	 * Unbinds all active textures.
	 */
	public static void unbindAll()
	{
		// backwards iterate because unbinding removes from stack.
		for (int i = TEX_STACK.size()-1; i >= 0; i--)
			TEX_STACK.get(i).unbind();
	}
	
	public void reset()
	{
		
	}
}
