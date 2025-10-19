#version 150
uniform sampler2D Sampler0;
uniform float Intensity;
uniform float BlurScaleY;
uniform float BlurScaleX;
in vec2 texCoord;
out vec4 fragColor;

vec4 squareblur(sampler2D smap, vec2 uv, vec2 scale, float radius){
    vec4 sum = vec4(0.0);
    sum += texture2D(smap, uv) * 0.24;
    sum += texture2D(smap, uv + vec2(scale.x * radius, 0.0))*0.19;
    sum += texture2D(smap, uv - vec2(scale.x * radius, 0.0))*0.19;
    sum += texture2D(smap, uv + vec2(0.0,scale.y * radius))*0.19;
    sum += texture2D(smap, uv - vec2(0.0,scale.y * radius))*0.19;
    return sum;
}


void main() {
    float BlurRadius = Intensity*8;
    vec2 texel = vec2(BlurScaleX,BlurScaleY);
    fragColor =squareblur(Sampler0,texCoord,texel,BlurRadius);
}