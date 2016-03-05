#version 330 core

in vec3 surfaceNorm;
in vec3 lightVector;
in vec3 lightColor;
in vec3 worldVec;

out vec4 finalColor;

void main() {
	
	
	
	vec3 unitNorm = normalize(surfaceNorm);
	vec3 finColor = vec3(0.0, 0.0, 0.0);
	
	vec3 unitLightVec = normalize(lightVector - worldVec);
	
	float nDot1 = dot(unitNorm, unitLightVec);
	float brightness = clamp(nDot1, 0, 1);
	vec3 diffuse = (brightness / 200) * lightColor * vec3(0x3F, 0x40, 0x40);
	
	finalColor = vec4(diffuse, 1.0);
}