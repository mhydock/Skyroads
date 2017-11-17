#version 150 core

uniform mat4 mvpMatrix;

in vec4 in_Position;

void main(void)
{
	gl_Position = mvpMatrix * in_Position;
	
	gl_PointSize = 5;
}