#version 150

uniform sampler2D Sampler0;  // Entity texture
uniform float TintStrength;  // Custom uniform for tint intensity

in vec2 texCoord0;           // Texture coordinates
out vec4 fragColor;          // Output color

void main() {
    vec4 color = texture(Sampler0, texCoord0); // Sample texture color
    color.r = min(color.r + TintStrength, 1.0); // Apply red tint dynamically
    fragColor = color;
}
