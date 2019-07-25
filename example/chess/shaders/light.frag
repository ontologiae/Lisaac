uniform sampler2D texture;

varying vec3 normal;
varying vec3 light;
varying vec3 halfVector;
varying vec4 ambient, globalAmbient;
varying float dist;

void main (void)
{
	// get texture color of current pixel
	vec4 base = texture2D(texture, gl_TexCoord[0].st);

	// 'normal' is interpolated and need to be normalized
	vec3 N = normalize(normal);
	vec3 L = normalize(light);

	// pseudo-angle between normal and light direction
	float coef = max(dot(N, L), 0.0);	
	
	float att = 1.0 / (gl_LightSource[0].constantAttenuation +
			gl_LightSource[0].linearAttenuation * dist +
			gl_LightSource[0].quadraticAttenuation * dist * dist);
	
	// diffuse color formula
	vec4 diffuse = gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse * coef;
		
	// specular color formula
	vec4 specular = gl_FrontMaterial.specular * gl_LightSource[0].specular * pow(max(dot(N,normalize(halfVector)),0.0), gl_FrontMaterial.shininess);
	
	// result
	gl_FragColor = globalAmbient + att * (diffuse + ambient + specular) * base;
}

