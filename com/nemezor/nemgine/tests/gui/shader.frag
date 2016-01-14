#version 330 core

in vec3 surfaceNorm;
in vec3 lightVector;
in vec3 lightColor;
in vec3 worldVec;

out vec4 finalColor;

//uniform sampler2D sampler;

void main() {
	
	vec3 unitNorm = normalize(surfaceNorm);
	vec3 finColor = vec3(0.0, 0.0, 0.0);
	
	vec3 unitLightVec = normalize(lightVector - worldVec);
	
	float nDot1 = dot(unitNorm, unitLightVec);
	float brightness = max(nDot1, 0.0);
	vec3 diffuse = brightness * lightColor;
	
	finalColor = vec4(diffuse, 1.0);
}