#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

uniform float BrightR;
uniform float BrightG;
uniform float BrightB;
uniform float DarkR;
uniform float DarkG;
uniform float DarkB;
uniform float Threshold;
uniform float Softness;
uniform float Invert;
uniform float Intensity;

out vec4 fragColor;

float Bayer2(vec2 c) { c = 0.5 * floor(c); return fract(1.5 * fract(c.y) + c.x); }
float Bayer4(vec2 c) { return 0.25 * Bayer2(0.5 * c) + Bayer2(c); }
float Bayer8(vec2 c) { return 0.25 * Bayer4(0.5 * c) + Bayer2(c); }
float Bayer16(vec2 c) { return 0.25 * Bayer8(0.5 * c) + Bayer2(c); }
float Bayer32(vec2 c) { return 0.25 * Bayer16(0.5 * c) + Bayer2(c); }
float Bayer64(vec2 c) { return 0.25 * Bayer32(0.5 * c) + Bayer2(c); }

void main(){
    vec4 diffuseColor = texture(DiffuseSampler, texCoord);

    // Vanilla leaves the horizon gap unwritten, so borrow the nearest drawn pixel above it
    for (int i = 1; i <= 16; i++) {
        if (diffuseColor.a >= 0.5) {
            break;
        }
        diffuseColor = texture(DiffuseSampler, texCoord + vec2(0.0, float(i) * 0.008));
    }

    float luminance = dot(diffuseColor.rgb, vec3(0.299, 0.587, 0.114));

    vec3 brightColor = vec3(BrightR, BrightG, BrightB);
    vec3 darkColor = vec3(DarkR, DarkG, DarkB);

    // Invert swaps which color the bright/dark halves map to, for the flash's middle phase
    vec3 highColor = mix(brightColor, darkColor, Invert);
    vec3 lowColor = mix(darkColor, brightColor, Invert);

    float edge = smoothstep(Threshold - Softness, Threshold + Softness, luminance);

    // Dither hides banding in the ramp
    float dither = Bayer64(gl_FragCoord.xy);
    edge = clamp(edge + (dither - 0.5) * 0.02, 0.0, 1.0);

    vec3 twoTone = mix(lowColor, highColor, edge);

    vec3 outColor = mix(diffuseColor.rgb, twoTone, Intensity);
    fragColor = vec4(outColor, 1.0);
}
