#version 150
uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform float Intensity;
uniform float Time;
in vec2 texCoord;
out vec4 fragColor;

float hash(vec2 p) {
    p = fract(p * vec2(123.34, 456.21));
    p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}
void main() {
    float VignetteRadius = mix(1.2, 0.35, pow(smoothstep(0.1, 1.0, Intensity), 1.2));
    float PulseIntensity = mix(0.10, 0.80, pow(smoothstep(0.1, 1.0, Intensity), 1.8));
    float noiseStrength  = mix(0.15, 0.25, smoothstep(0.1, 1.0, Intensity));
    vec4 color = texture(Sampler0,texCoord);
    float dist = distance(texCoord, vec2(0.5));
    float PulseSpeed = 4;
    float tm = (sin(Time * PulseSpeed) - 1.0) / 3.0;

    vec2 scaleduv = floor(texCoord * 256.0)/256.0;
    vec2 centered = texCoord - 0.5;
    float angle = atan(centered.y, centered.x);

    float jag = sin(angle * 16.0 + Time * 0.8) * 0.03;

    float n = sin(texCoord.x * 60.0 + Time * 1.3) * sin(texCoord.y * 40.0 + Time * 0.9) * 0.02;

    float distortedDist = dist + jag + n;

    float ran = hash(dot(scaleduv.y-0.1,scaleduv.x-0.1) * vec2(Time * 0.1));

    float vignette = smoothstep(VignetteRadius, VignetteRadius - 0.45+(tm*PulseIntensity), distortedDist+ (ran - 0.5) * noiseStrength);
    float vignette2 = smoothstep(VignetteRadius*0.7, VignetteRadius*0.8 - 0.25+(tm*PulseIntensity), dist+ (ran - 0.4) * noiseStrength);

    vec4 color2 = texture(Sampler1,texCoord);
    color2.a *= 1.0-(vignette2);
    color.rgb = mix(color.rgb,color2.rgb,color2.a);

    color.rgb = mix(color.rgb,vec3(1.0,0.0,0.0),1.0-vignette);
    fragColor = color;
}