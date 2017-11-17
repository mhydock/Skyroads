//==============================================================================
// Date Created:		28 April 2014
// Last Updated:		23 July 2014
//
// File name:			FrameBuffer.java
// Author(s):			M. Matthew Hydock
//
// File description:	A class to provide for rendering to a texture.
//==============================================================================

package com.gamelib.core;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;
import static com.gamelib.core.Utils.*;

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
		texID = glGenTextures();
		fboID = glGenFramebuffers();
		dboID = glGenRenderbuffers();
		
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

		glBindFramebuffer(GL_FRAMEBUFFER, fboID);

		glBindTexture(GL_TEXTURE_2D, texID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texID, 0);
		
		printGlError("Created texture for fbo " + fboID);
		
		glBindRenderbuffer(GL_RENDERBUFFER, dboID);
		glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, dboID);
		
		printGlError("Created render buffer for fbo " + fboID);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		printGlError("Done binding for fbo " + fboID);

		System.out.print("fbo " + fboID + ": ");
		printFramebufferError();
	}
	
	private void createFBO_Multisampled()
	{
		m_texID = glGenTextures();
		m_fboID = glGenFramebuffers();
		m_dboID = glGenRenderbuffers();

		glBindFramebuffer(GL_FRAMEBUFFER, m_fboID);
		
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, m_texID);
		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, NUM_SAMPLES, GL_RGBA, width, height, false);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, m_texID, 0);
		
		printGlError("Created multisampled texture for fbo " + m_fboID);
		
		glBindRenderbuffer(GL_RENDERBUFFER, m_dboID);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, NUM_SAMPLES, GL_DEPTH_COMPONENT24, width, height);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, m_dboID);

		printGlError("Created multisampled render buffer for fbo " + m_fboID);
		
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		printGlError("Done binding for fbo " + m_fboID);

		System.out.print("m_fbo " + m_fboID + ": ");
		printFramebufferError();
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
		}
		else
		{
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			glBindRenderbuffer(GL_RENDERBUFFER, 0);
			
			if (multisampled)
			{
				//System.out.println("copy fbo");
				glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fboID);
				printGlError("Binding " + fboID + " to Draw buffer");
				glBindFramebuffer(GL_READ_FRAMEBUFFER, m_fboID);
				printGlError("Binding " + m_fboID + " to Read buffer");
				glReadBuffer(GL_COLOR_ATTACHMENT0);
				glDrawBuffer(GL_COLOR_ATTACHMENT0);
				printGlError("Setting buffer attachments");
				glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
				printGlError("Attempting buffer blitting");
				glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
				printGlError("Unbinding Draw buffer");
				glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
				printGlError("Unbinding Read buffer");
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
		
		switch(e)
		{
			case GL_FRAMEBUFFER_UNSUPPORTED						: System.out.println("format not supported");
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT	: System.out.println("missing attachment");
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT			: System.out.println("incomplete attachment");
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE			: System.out.println("incomplete multisample");
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER			: System.out.println("missing draw buffer");
																  break;
			case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER			: System.out.println("missing read buffer");
																  break;
			case GL_FRAMEBUFFER_COMPLETE						: System.out.println("complete");
																  break;
			default												: System.out.println("mystery error: " + e);
																  break;		
		}
	}
}
