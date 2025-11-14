#version 150
uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform float Intensity;
in vec2 texCoord;
out vec4 fragColor;

void main(){
    float x = Intensity;
    float dist = distance(texCoord, vec2(0.5));

    float vignette = smoothstep(0.9, 0.9 - x, dist);

    vec4 color = texture(Sampler0,texCoord);
    vec4 ov = texture(Sampler1,texCoord);
    ov.a *=1-vignette;
    color.rgb = mix(color.rgb,ov.rgb,ov.a);

    fragColor = color;
}