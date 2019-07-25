uniform sampler2D colorMap;
uniform sampler2D noiseMap;

uniform float timer;

void main (void)
{
	// get texture coordinate for the fragment
	vec2 depl = gl_TexCoord[0].st;

	float scaledTimer = timer*0.05;

	// translate texture coordinate
	depl.x += scaledTimer;
	depl.y -= scaledTimer;

	// get random vector from noise texture
	vec3 noiseVec = normalize(texture2D(noiseMap, depl.xy).xyz);

	// scale down vector in [0..1]
	noiseVec = (noiseVec * 2.0 - 1.0) * 0.095;
	
	// select color in water texture
	gl_FragColor = texture2D(colorMap, gl_TexCoord[0].st + noiseVec.xy);

	// for alpha blending with water reflections
	gl_FragColor.a = 0.5; 
}

