//==============================================================================
// Date Created:		11 April 2014
// Last Updated:		10 July 2014
//
// File name:			ImageTexture.java
// Author(s):			M. Matthew Hydock
//
// File description:	An implementation of a Texture object, that uses image
//						data for its texture data.
//==============================================================================

package com.gamelib.core;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import org.newdawn.slick.opengl.ImageData;
import org.newdawn.slick.opengl.ImageDataFactory;

public class ImageTexture extends Texture
{	
	@SuppressWarnings("unused")
	private ImageTexture()
	{}
	
	public ImageTexture(String filePath)
	{
		loadImage(filePath);
	}
	
	public ImageTexture(int width, int height, ByteBuffer imgData)
	{
		loadImage(width, height, imgData);
	}
	
	public void loadImage(String filePath)
	{
		ImageData data = ImageDataFactory.getImageDataFor(filePath);
		
		loadImage(data.getWidth(),data.getHeight(),data.getImageBufferData());
	}
	
	public void loadImage(int width,int height,ByteBuffer imgData)
	{		
		texID = glGenTextures();
		
		// activate texture
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texID);
		
		// define how components are laid out
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		// upload image data, and generate mipmaps for scaling
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_INT, imgData);
		glGenerateMipmap(GL_TEXTURE_2D);

		// setup coordinate system (enable wrapping)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		
		// setup scaling algorithms
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		
		// deactivate texture
		glActiveTexture(0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	@Override
	public void free()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		
	}
}
