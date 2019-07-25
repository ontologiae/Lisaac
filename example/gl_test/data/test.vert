
uniform float time;

float zfunction (vec4 v) {
	float  z;

	z = sin(5.0*v.x + time*0.05)*0.25;
	return(z);
}

void main()
{
	vec4 v = vec4(gl_Vertex);

	float newz = zfunction(v);
	v.z = newz;	

	// for the fragment shader
	gl_FrontColor = gl_Color;


	gl_Position = gl_ModelViewProjectionMatrix * v;
}

