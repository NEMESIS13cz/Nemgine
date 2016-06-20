#version 330 core

in vec4 colorFragment;
in vec4 fragPosLight;
in vec3 worldVec;
in vec3 lightDirection;
in vec3 surfaceNorm;

out vec4 finalColor;

uniform sampler2D shadow_map;
uniform int shadowMapSize;

const int pcfSize = 3;
const int pcfTotal = (pcfSize * 2 + 1) * (pcfSize * 2 + 1);

void main() {
	float shadowBrightness = 1.0f;
	float texelSize = 1.0f / shadowMapSize;
	float inShadow = 0;
	
	vec3 projLight = (fragPosLight.xyz / fragPosLight.w) * 0.5f + 0.5f;
	for (int x = -pcfSize; x < pcfSize; x++) {
		for (int y = -pcfSize; y < pcfSize; y++) {
			vec4 depth = texture(shadow_map, projLight.xy + vec2(x, y) * texelSize);
			if (depth.g == 1 && depth.r < (fragPosLight.z + 1.0) / 2.0 - 0.010) {
				inShadow++;
			}
		}
	}
	inShadow /= pcfTotal;
	shadowBrightness *= 1 - inShadow;
	
	vec3 unitNorm = normalize(surfaceNorm);
	vec3 unitLightVec = normalize(lightDirection - worldVec);
	
	float nDot1 = dot(unitNorm, unitLightVec);
	float brightness = clamp(nDot1, 0, 1);
	vec3 diffuse = brightness * colorFragment.rgb;
	
	finalColor = vec4(diffuse * shadowBrightness, colorFragment.a);
}