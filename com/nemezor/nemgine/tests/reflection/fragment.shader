#version 330 core

in vec3 surfaceNorm;
in float lightVec[12];
in float light_color[12];
in vec3 worldVec;

out vec4 finalColor;

uniform sampler2D sampler;

void main() {
	
	vec3 unitNorm = normalize(surfaceNorm);
	vec3 finColor = vec3(0.0, 0.0, 0.0);
	
	for (int i = 0; i < 4; i++) {
		vec3 lightColor = vec3(light_color[i * 3], light_color[i * 3 + 1], light_color[i * 3 + 2]);
		vec3 lightVector = vec3(lightVec[i * 3], lightVec[i * 3 + 1], lightVec[i * 3 + 2]);
		
		vec3 unitLightVec = normalize(lightVector - worldVec);
		
		float nDot1 = dot(unitNorm, unitLightVec);
		float brightness = max(nDot1, 0.0);
		vec3 diffuse = brightness * lightColor;
		
		finColor += vec3(diffuse);
	}
	finalColor = vec4(finColor / 4, 1.0);
}