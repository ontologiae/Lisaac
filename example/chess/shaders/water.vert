
void main()
{
	// for texture mapping
	gl_TexCoord[0] = gl_MultiTexCoord0;

	// usual OpenGL fixed transformation
	gl_Position = ftransform(); 
}