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
    (c - a) * u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    mat2 rot = mat2(0.6, -0.8, 0.8, 0.6);

    for (int i = 0; i < 5; i++) {
        v += a * noise(p);
        p = rot * p * 2.0;
        a *= 0.5;
    }
    return v;
}


void main(){
    // UV for noise fields (stretched)
    float x = Intensity;
    vec2 uva = texCoord;
    uva.x *= 0.75;
    uva.y *= 0.60;

    float t = Time * 1.6;

    // Two layered distortion fields
    float d1 = fbm(uva * 10.0 + vec2(t, -t * 0.4));
    float d2 = fbm(uva * 20.0 + vec2(-t * 0.3, t));

    // Combine
    float distortion = (d1 * 0.6 + d2 * 0.4) - 0.5;

    // Radial falloff (vignette)
    float distFromCenter = distance(texCoord, vec2(0.5));
    float vignette = smoothstep(0.8, 0.3, distFromCenter);
    float orangevig = smoothstep(1.0,0.7-0.3*x,distFromCenter);
    // Final distortion amount
    float strength = x * vignette;

    // Offset UVs by distortion field
    vec2 offset = vec2(distortion * 0.125 * strength,
    distortion * 0.10 * strength);

    vec3 sceneColor = texture(Sampler0, texCoord + offset).rgb;
    sceneColor.rgb = mix(sceneColor.rgb,vec3(0.831, 0.431, 0),1.-orangevig);
    fragColor = vec4(sceneColor, 1.0);
}