#version 150
uniform sampler2D Sampler0;
uniform float Intensity;
uniform float BlurScaleY;
uniform float BlurScaleX;
in vec2 texCoord;
out vec4 fragColor;

vec4 squareblur(sampler2D smap, vec2 uv, vec2 scale, float radius){
    vec4 sum = vec4(0.0);
    sum += texture(smap, uv) * 0.24;
    sum += texture(smap, uv + vec2(scale.x * radius, 0.0))*0.19;
    sum += texture(smap, uv - vec2(scale.x * radius, 0.0))*0.19;
    sum += texture(smap, uv + vec2(0.0,scale.y * radius))*0.19;
    sum += texture(smap, uv - vec2(0.0,scale.y * radius))*0.19;
    return sum;
}


void main() {
    float VignetteRadius = mix(2., 0.15, pow(smoothstep(0.1, 1.0, Intensity), 1.2));
    float BlurRadius = mix(0,8,pow(Intensity,2));
    vec2 texel = vec2(BlurScaleX,BlurScaleY);
    float dists = distance(texCoord,vec2(0.5));
    float vignette = smoothstep(VignetteRadius,VignetteRadius-1.,dists);
    vec4 color = squareblur(Sampler0,texCoord,texel,BlurRadius);
    color.rgb *=vignette;
    fragColor = color;
}