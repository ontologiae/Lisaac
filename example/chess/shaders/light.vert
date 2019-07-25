varying vec3 normal;
varying vec3 light;
varying vec3 halfVector;
varying vec4 ambient, globalAmbient;
varying float dist;

void main(void)
{ 
	// for texture mapping
	gl_TexCoord[0] = gl_MultiTexCoord0;

	// get vertex normal in world space
	normal = normalize(gl_NormalMatrix * gl_Normal);

	// vertex position in world space
	vec4 pos = gl_ModelViewMatrix * gl_Vertex;
	
	// get light direction
	light = vec3(gl_LightSource[0].position - pos);	

	// light distance from vertex
	dist = length(light);

	light = normalize(light);

	// get per-vertex ambient colors
	ambient = gl_FrontMaterial.ambient * gl_LightSource[0].ambient;
	globalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;

	// get OpenGL half vector of light
	halfVector = normalize(gl_LightSource[0].halfVector.xyz);

	// output
	gl_Position = ftransform();
}


