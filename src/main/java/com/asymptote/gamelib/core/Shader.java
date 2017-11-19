//==============================================================================
// Date Created:		08 April 2014
// Last Updated:		22 July 2014
//
// File name:			Shader.java
// Author(s):			M. Matthew Hydock
//
// File description:	A class to encapsulate functions related to programmable
//						shaders. Includes references to common inputs, such as
//						vertex attributes, and provides ways to access custom
//						inputs. The active shader is globally accessible.
//==============================================================================

package com.gamelib.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL20.*;

public class Shader implements IGLObject
{	
	private int id;
	private int type;
	
	public Shader(String fileName)
	{
		if (fileName.endsWith(".vert"))
			type = GL20.GL_VERTEX_SHADER;
		else if (fileName.endsWith(".frag"))
			type = GL20.GL_FRAGMENT_SHADER;
		else
			throw new RuntimeException("File is neither vertex (*.vert) nor fragment (*.frag) shader.");
		
		id = loadShader(fileName, type);		
	}
	
	public int getID()
	{
		return id;
	}
	
	public int getType()
	{
		return type;
	}
	
	public String getTypeString()
	{
		return type == GL20.GL_VERTEX_SHADER ? "Vertex" : "Fragment";
	}
	
	public void free()
	{
		if (id != -1)
			glDeleteShader(id);
	}
		
	private int loadShader(String filename, int type)
	{
		StringBuilder shaderSource = new StringBuilder();
		int shaderID = 0;
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			
			String line;
			while ((line = reader.readLine()) != null)
				shaderSource.append(line).append("\n");
			
			reader.close();
			
			//System.out.println(shaderSource.toString());
		}
		catch (IOException e)
		{
			System.err.println("Could not read file.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		
		if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL11.GL_FALSE)
		{
			System.err.println("Error compiling shader: " + glGetShaderInfoLog(shaderID, 10000));
			glDeleteShader(shaderID);
			return -1;
		}
		
		return shaderID;
	}
}
