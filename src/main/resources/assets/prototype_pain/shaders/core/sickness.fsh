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

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);

    float a = hash(i);
    float b = hash(i + vec2(1,0));
    float c = hash(i + vec2(0,1));
    float d = hash(i + vec2(1,1));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x) +
    (c - a)*u.y*(1.0 - u.x) +
    (d - b)*u.x*u.y;
}

float fbm(vec2 p)
{
    float v = 0.3;
    float a = 1.0;
    mat2 rot = mat2(0.6, -0.8, 0.8, 0.6);

    for (int i = 0; i < 5; i++)
    {
        v += a * noise(p);
        p = rot * p * 2.0;
        a *= 0.5;
    }
    return v;
}

void main(){
    float x = 1.0-pow(Intensity,2.0);
    vec2 uvu = texCoord;
    uvu.x *= 0.5;
    uvu.y *= 0.4;

    float dist = distance(texCoord, vec2(0.5));

    float vignette = smoothstep(0.9, 0.9 - x, dist);
    float t = Time * 5.6;
    float n = fbm(uvu * 20.0 + vec2(-t, t*0.5)/20);
    float n2 = fbm(uvu * 20.0 + vec2(t*0.8, -t)/10);

    float clouds = smoothstep(0.3, 1, n);
    float clouds2 = smoothstep(0.3, 0.7, n2);

    vec4 color = texture(Sampler0,texCoord);
    vec4 ov = texture(Sampler1,texCoord);
    vec4 ov2 = ov;

    ov.a = max(clouds,0.5);
    ov2.a = clouds2;
    ov2.a = mix(ov2.a,0.0,vignette);
    ov.a = mix(ov.a,0.0,vignette);
    color.rgb = mix(color.rgb,ov.rgb,ov.a);
    color.rgb = mix(color.rgb,ov2.rgb,ov2.a);

    fragColor = color;
}