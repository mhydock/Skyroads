#version 150 core

uniform mat4 mvpMatrix;

in vec4 in_Position;
in vec4 in_Color;

out vec4 pass_Color;

void main(void)
{
	gl_Position = mvpMatrix * in_Position;
	pass_Color = in_Color;
}