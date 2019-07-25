uniform vec3 lightPos;

varying vec3 lVec;
varying vec3 norm;
varying vec2 texCoord;

void main(){
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

	lVec = lightPos; // - gl_Vertex.xyz;
	norm = gl_Normal;

	texCoord = gl_MultiTexCoord0.xy;	
}

