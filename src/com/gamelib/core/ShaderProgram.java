package com.gamelib.core;

import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glIsShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderProgram implements IGLObject
{
	private static ShaderProgram global;

	private Shader vert;
	private Shader frag;
	
	private int progID;	
	private int mvpMatrix;
	
	public ShaderProgram(String vsFileName, String fsFileName)
	{
		vert = new Shader(vsFileName);
		frag = new Shader(fsFileName);
		
		progID = buildProgram(vert.getID(), frag.getID());
		
		if (progID > 0)
			mvpMatrix = glGetUniformLocation(progID, "mvpMatrix");

	}
	
	public void disable()
	{
		glUseProgram(0);
		global = null;
	}
	
	public void use()
	{
		glUseProgram(progID);
		global = this;
			
		Camera camera = Camera.getGlobal();
		if (camera != null)
			glUniformMatrix4fv(mvpMatrix, false, camera.getMVP());
	}
	
	public static ShaderProgram getGlobal()
	{
		return global;
	}
	
	public void setValue(String name, int value)
	{
		if (progID < 0)
			return;
		
		int valueID = glGetUniformLocation(progID, name);
		
		if (valueID != -1)
			glUniform1i(valueID, value);
		else
			System.err.println("Uniform " + name + " could not be found.");
	}
	
	public void setValue(String name, float value)
	{
		if (progID < 0)
			return;
		
		int valueID = glGetUniformLocation(progID, name);
		
		if (valueID != -1)
			glUniform1f(valueID, value);
		else
			System.err.println("Uniform " + name + " could not be found.");
	}
	
	public void setMatrix(String name, FloatBuffer value)
	{
		if (progID < 0)
			return;
		
		int valueID = glGetUniformLocation(progID, name);
		
		if (valueID != -1)
			glUniformMatrix4fv(valueID, false, value);
		else
			System.err.println("Uniform " + name + " could not be found.");
	}	

	@Override
	public void free()
	{
		if (progID != -1)
		{
			glUseProgram(0);
			glDetachShader(progID, vert.getID());
			glDetachShader(progID, frag.getID());
			
			vert.free();
			frag.free();
			glDeleteProgram(progID);
		}
	}
	
	private int buildProgram(int vsID, int fsID)
	{
		//System.err.println("OpenGL version is " + GL11.glGetString(GL11.GL_VERSION));
		
		if (!glIsShader(vsID) || !glIsShader(fsID))
		{
			System.err.println("IDs are not shaders. Program build aborted.");
			return 0;
		}
		
		int pid = glCreateProgram();
		glAttachShader(pid, vsID);
		glAttachShader(pid, fsID);
		
		glBindAttribLocation(pid, Vertex.POS_ATTRIB, "in_Position");
		glBindAttribLocation(pid, Vertex.COL_ATTRIB, "in_Color");
		glBindAttribLocation(pid, Vertex.TEX_ATTRIB, "in_TexCoord");
		
		glLinkProgram(pid);		
		
		int error = glGetProgrami(pid, GL_LINK_STATUS);
		if (error == GL11.GL_FALSE)
		{
			System.err.println("LINKING ERROR - Could not create the shaders:\n" + GL20.glGetProgramInfoLog(pid, 2000));
			System.exit(-1);
		}
		
		glValidateProgram(pid);
				
		error = glGetProgrami(pid, GL_VALIDATE_STATUS);
		if (error == GL11.GL_FALSE)
		{
			System.err.println("VALIDATION ERROR - Could not create the shaders:\n" + GL20.glGetProgramInfoLog(pid, 2000));
			System.exit(-1);
		}
		
		return pid;
	}
}
