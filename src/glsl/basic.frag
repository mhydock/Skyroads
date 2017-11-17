#version 150 core

in vec4 pass_Color;

out vec4 out_Color;

void main(void)
{
	if (gl_FrontFacing)
		out_Color = pass_Color;
	else
		out_Color = vec4(1.0,0.0,0.5,1.0);	
}