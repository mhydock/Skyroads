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

package com.asymptote.gamelib.core;

import java.io.*;
import java.nio.*;

import static org.lwjgl.demo.util.IOUtil.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class ImageTexture extends Texture
{	
	private final ByteBuffer image;
	
	@SuppressWarnings("unused")
	private ImageTexture()
	{}
	
	public ImageTexture(String filePath)
	{
		loadImage(filePath);
	}
	
	public ImageTexture(int width, int height, int components, ByteBuffer imgData)
	{
		loadImage(width, height, components, imgData);
	}
	
	public void loadImage(String filePath)
	{
		ByteBuffer imageBuffer = ioResourceToByteBuffer(filePath, 8 * 1024);
		int width = 0, 
			height = 0,
			components = 0;

		try (MemoryStack stack = stackPush()) {
            IntBuffer w    = stack.mallocInt(1);
            IntBuffer h    = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            // Use info to read image metadata without decoding the entire image.
            // We don't need this for this demo, just testing the API.
            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            }

            System.out.println("Image width: " + w.get(0));
            System.out.println("Image height: " + h.get(0));
            System.out.println("Image components: " + comp.get(0));
            System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

            // Decode the image
            image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (image == null) {
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }

            width = w.get(0);
            height = h.get(0);
            components = comp.get(0);
		}
		
		loadImage(width, height, components, image);
	}
	
	public void loadImage(int width, int height, int components, ByteBuffer imgData)
	{		
		this.width = width;
		this.height = height;
		this.components = components;

		this.texID = glGenTextures();
		
		int format = components == 3 ? GL_RGB : GL_RGBA;

		// activate texture
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texID);
		
		// define how components are laid out
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		// upload image data, and generate mipmaps for scaling
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, format, GL_INT, imgData);
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
