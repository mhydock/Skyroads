in vec4 pass_Color;
in vec2 pass_TexCoord;

uniform sampler2D mytexture;
 
void main(void)
{
	
	if (gl_FrontFacing)
	{
		gl_FragColor = pass_Color;
		gl_FragColor = texture2D(mytexture, pass_TexCoord);
	}
	else
		gl_FragColor = vec4(1.0,0.0,0.5,1.0);
}
