
uniform sampler2D Texture;

varying vec3 lVec;
varying vec3 norm;
varying vec2 texCoord;

void main(){
	vec4 tex_color = texture2D(Texture, texCoord);

	vec3 lightVec = normalize(lVec);
	float intensity = dot(lightVec, normalize(norm));

	//gl_FragColor = texture1D(CelShade, intensity);

	float gris;

  	if (intensity > 0.8)
		gris = 0.2;
	else if (intensity > 0.25)
		gris = 0.4;
	else if (intensity > 0.10)
		gris = 0.9;
	else
		gris = 1.0;

	//gl_FragColor = vec4(gris);
	gl_FragColor = clamp(vec4(gris) * tex_color, 0.1, 1.0);
}