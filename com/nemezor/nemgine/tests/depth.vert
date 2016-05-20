#version 330 core

in vec3 position;
in vec3 normal;

out vec4 depthFragment;

uniform mat4 projection;
uniform mat4 transformation;

void main() {

	vec4 world = projection * (transformation * vec4(position, 1.0));
	depthFragment = vec4(world.z, 1, 0, 1);
	gl_Position = world;
}