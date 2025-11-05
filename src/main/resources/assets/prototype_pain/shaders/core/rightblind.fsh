#version 150
uniform sampler2D Sampler0;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    float VignetteRadius = 0.85;
    vec4 color = texture(Sampler0,texCoord);
    float dist = distance(texCoord, vec2(0,0.5));
    float vignette = smoothstep(VignetteRadius, VignetteRadius - 0.45,  dist);
    color.rgb = mix(color.rgb,vec3(0.0,0.0,0.0),1.0-vignette);
    fragColor = color;
}


