#version 330 core

in vec4 colorFragment;
in vec4 fragPosLight;

out vec4 finalColor;

uniform sampler2D shadow_map;

void main() {
	vec3 projLight = (fragPosLight.xyz / fragPosLight.w) * 0.5f + 0.5f;
	vec4 depth = texture(shadow_map, projLight.xy);
	if (depth.b == 0 && depth.r < fragPosLight.z - 0.005) {
		finalColor = vec4(colorFragment.rgb * 0.0f, colorFragment.a);
	}else{
		finalColor = colorFragment;
	}
}