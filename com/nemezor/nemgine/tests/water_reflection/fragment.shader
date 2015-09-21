#version 330 core

in vec2 textureCoords;
in vec3 surfaceNorm;
in vec3 lightVec;

out vec4 finalColor;

uniform sampler2D sampler;

const vec3 lightColor = vec3(0.7, 0.5, 0.2);

void main() {

	//finalColor = texture(sampler, textureCoords);
	
	vec3 unitNorm = normalize(surfaceNorm);
	vec3 unitLightVec = normalize(lightVec);
	
	float nDot1 = dot(unitNorm, unitLightVec);
	float brightness = max(nDot1, 0.0);
	vec3 diffuse = brightness * lightColor;
	
	finalColor = vec4(diffuse, 1.0) * vec4(1.0, 1.0, 1.0, 1.0);

}