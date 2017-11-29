//==============================================================================
// Date Created:		28 April 2014
// Last Updated:		23 July 2014
//
// File name:			FrameBuffer.java
// Author(s):			M. Matthew Hydock
//
// File description:	A class to provide for rendering to a texture.
//==============================================================================

package com.asymptote.gamelib.core;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static com.asymptote.gamelib.core.Utils.*;

import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

public class FrameBuffer extends Texture
{
	private static final int NUM_SAMPLES = 8;
	
	private int fboID;
	private int dboID;
	
	private int m_fboID;
	private int m_dboID;
	private int m_texID;
	private boolean multisampled;
	
	public FrameBuffer(int width, int height, boolean multi)
	{		
		this.width = width;
		this.height = height;
		this.multisampled = multi;
		
		createFBO();
		
		if (multi)
			createFBO_Multisampled();
	}
	
	private void createFBO()
	{
		ByteBuffer buffer = BufferUtils.createByteBuffer(width*height*4);

		texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);		
		printGlError("Created texture for fbo " + fboID);
		
		dboID = glGenRenderbuffers();
		glBindRenderbuffer(GL_RENDERBUFFER, dboID);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
		printGlError("Created render buffer for fbo " + fboID);
		
		fboID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, fboID);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texID, 0);		
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, dboID);
		printGlError("Done binding for fbo " + fboID);		
		
		System.out.print("fbo " + fboID + ": ");
		printFramebufferError();

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	private void createFBO_Multisampled()
	{
		m_texID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, m_texID);
		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, NUM_SAMPLES, GL_RGBA, width, height, false);
		printGlError("Created multisampled texture for fbo " + m_fboID);
		
		System.out.println("texture buffer samples: " + glGetTexLevelParameteri(GL_TEXTURE_2D_MULTISAMPLE, 0, GL_TEXTURE_SAMPLES));

		// m_dboID = glGenRenderbuffers();
		// glBindRenderbuffer(GL_RENDERBUFFER, m_dboID);
		// glRenderbufferStorageMultisample(GL_RENDERBUFFER, NUM_SAMPLES, GL_DEPTH_COMPONENT, width, height);
		// printGlError("Created multisampled render buffer for fbo " + m_fboID);
		
		// System.out.println("depth buffer samples: " + glGetRenderbufferParameteri(GL_RENDERBUFFER, GL_RENDERBUFFER_SAMPLES));

		m_fboID = glGenFramebuffers();
		glBindFramebuffer(GL_FRAMEBUFFER, m_fboID);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, m_texID, 0);
		//glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, m_dboID);
		printGlError("Done binding for fbo " + m_fboID);				
		
		System.out.print("m_fbo " + m_fboID + ": ");
		printFramebufferError();

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void use(boolean enable)
	{
		clearGlErrors();

		if (enable)
		{
			if (multisampled)
			{
				//System.out.println("use fbo");
				glBindRenderbuffer(GL_RENDERBUFFER, m_dboID);
				printGlError("Binding render buffer " + m_dboID + ":  ");
				glBindFramebuffer(GL_FRAMEBUFFER, m_fboID);
				printGlError("Binding frame buffer " + m_fboID + ":  ");
			}
			else
			{
				glBindRenderbuffer(GL_RENDERBUFFER, dboID);
				glBindFramebuffer(GL_FRAMEBUFFER, fboID);
			}
			
			printFramebufferError();			
		}
		else
		{
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			glBindRenderbuffer(GL_RENDERBUFFER, 0);
			
			if (multisampled)
			{
				printGlError("Binding " + fboID + " to Draw buffer");
				glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboID);
				printGlError("Binding " + m_fboID + " to Read buffer");
				glBindFramebuffer(GL_READ_FRAMEBUFFER, m_fboID);
				printGlError("Setting buffer attachments");
				glReadBuffer(GL_COLOR_ATTACHMENT0);
				glDrawBuffer(GL_COLOR_ATTACHMENT0);
				printGlError("Attempting buffer blitting");
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
				printGlError("Unbinding Draw buffer");
				glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
				printGlError("Unbinding Read buffer");
				glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
			}
		}
	}
	
	@Override
	public void free()
	{
		glDeleteFramebuffers(fboID);
		
		if (multisampled)
			glDeleteFramebuffers(m_fboID);
	}
	
	private void printFramebufferError()
	// Check to see if there were any framebuffer errors
	{
		int e = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		String message;

		if(e == GL_FRAMEBUFFER_COMPLETE) {
			//System.out.println("framebuffer complete");
			return;
		}
		
		switch(e)
		{
			case GL_FRAMEBUFFER_UNSUPPORTED						: message = "format not supported";
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT	: message = "missing attachment";
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT			: message = "incomplete attachment";
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE			: message = "incomplete multisample";
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER			: message = "missing draw buffer";
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER			: message = "missing read buffer";
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS		: message = "incomplete layer targets";
																  break;
			default												: message = "mystery error: " + e;
																  break;		
		}
		throw new RuntimeException("Framebuffer error: " + message);
	}
}
