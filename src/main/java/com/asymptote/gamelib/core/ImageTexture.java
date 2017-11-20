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
import java.nio.channels.*;
import java.nio.file.*;

import org.lwjgl.*;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.BufferUtils.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class ImageTexture extends Texture
{	
	private ByteBuffer image;
	
	@SuppressWarnings("unused")
	private ImageTexture()
	{}
	
	public ImageTexture(String filePath)
	{
		try
		{
			loadImage(filePath);
		} catch (IOException ex)
		{
			System.out.println("Unable to load \"" + filePath + "\" : " + ex.getMessage());
		}
	}
	
	public ImageTexture(int width, int height, int components, ByteBuffer imgData)
	{
		loadImage(width, height, components, imgData);
	}
	
	public void loadImage(String filePath) throws IOException
	{
		ByteBuffer imageBuffer = ioResourceToByteBuffer(filePath, 8 * 1024);
		int width = 0, 
			height = 0,
			components = 0;

		try (MemoryStack stack = stackPush())
		{
            IntBuffer w    = stack.mallocInt(1);
            IntBuffer h    = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

			if (!stbi_info_from_memory(imageBuffer, w, h, comp))
			{
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
			}
			
            // Decode the image
            image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
			if (image == null)
			{
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

	/***
	 * Largely copied from https://github.com/LWJGL/lwjgl3/blob/master/modules/core/src/test/java/org/lwjgl/demo/util/IOUtil.java
	 * Don't much like the look of it. I'll retool it someday...
	 */
	private static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException
	{
        ByteBuffer buffer;

        Path path = Paths.get(resource);
		if (Files.isReadable(path))
		{
			try (SeekableByteChannel fc = Files.newByteChannel(path))
			{
                buffer = BufferUtils.createByteBuffer((int)fc.size() + 1);
                while (fc.read(buffer) != -1) { ; }
            }
		}
		else
		{
            try (InputStream source = ImageTexture.class.getClassLoader().getResourceAsStream(resource);
				 ReadableByteChannel rbc = Channels.newChannel(source))
			{
                buffer = BufferUtils.createByteBuffer(bufferSize);

				while (rbc.read(buffer) != -1)
				{
					if (buffer.remaining() == 0)
					{
						ByteBuffer oldBuffer = buffer;
						buffer = BufferUtils.createByteBuffer(oldBuffer.capacity() * 3 / 2);
						oldBuffer.flip();
						buffer.put(oldBuffer);
                    }
                }
            }
        }

        buffer.flip();
        return buffer.slice();
    }
}
