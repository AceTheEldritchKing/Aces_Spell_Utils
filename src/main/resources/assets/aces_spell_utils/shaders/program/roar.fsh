#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

uniform float CenterX;
uniform float CenterY;
uniform float Style;
uniform float Strength;
uniform float Sharpness;
uniform float Radius;
uniform float Thickness;
uniform float Blur;
uniform float Refraction;
uniform float Aspect;
uniform float Intensity;

out vec4 fragColor;

const int RING_SAMPLES = 8;
// high enough that the widest streak the strength cap allows still resolves as motion rather than grain
const int ZOOM_SAMPLES = 32;

// interleaved gradient noise, which breaks the sample positions off the grid so the streak reads as motion instead of stacked copies
float gradientNoise(vec2 pos) {
    return fract(52.9829189 * fract(dot(pos, vec2(0.06711056, 0.00583715))));
}

vec4 passthrough() {
    return vec4(texture(DiffuseSampler, texCoord).rgb, 1.0);
}

// a centre off the side of the screen sends samples past the edge, where the wrap mode would otherwise fold the sky into the ground
vec3 sampleClamped(vec2 uv) {
    return texture(DiffuseSampler, clamp(uv, 0.0, 1.0)).rgb;
}

vec4 zoomBlur(vec2 center) {
    vec2 toCenter = center - texCoord;
    // the offset is proportional to the distance from the centre, so the creature stays readable while the edges streak
    float reach = pow(clamp(length(toCenter) * 1.4142, 0.0, 1.0), Sharpness);
    float strength = Strength * Intensity * reach;
    if (strength < 0.0005) {
        return passthrough();
    }

    float jitter = gradientNoise(gl_FragCoord.xy);
    vec3 color = vec3(0.0);
    float total = 0.0;
    for (int i = 0; i < ZOOM_SAMPLES; i++) {
        float percent = (float(i) + jitter) / float(ZOOM_SAMPLES);
        // the parabolic weight tapers both ends of the trail instead of cutting it off square
        float weight = 4.0 * (percent - percent * percent);
        color += sampleClamped(texCoord + toCenter * percent * strength) * weight;
        total += weight;
    }
    // alpha is never sampled here, since the main target clears it to zero and propagating it renders unwritten pixels black
    return vec4(color / total, 1.0);
}

vec4 ringBand(vec2 center) {
    vec2 fromCenter = texCoord - center;
    // measured with aspect applied so the ring is round rather than an ellipse
    vec2 scaled = vec2(fromCenter.x * Aspect, fromCenter.y);
    float dist = length(scaled);

    float band = 1.0 - smoothstep(0.0, Thickness, abs(dist - Radius));
    float strength = band * Intensity;
    if (strength < 0.001 || dist < 1.0E-5) {
        return passthrough();
    }

    // the aspect is undone here so the displacement is not stretched along x
    vec2 dir = vec2((scaled.x / dist) / Aspect, scaled.y / dist);
    vec2 base = texCoord + dir * Refraction * strength;

    vec3 color = vec3(0.0);
    for (int i = 0; i < RING_SAMPLES; i++) {
        float offset = float(i) / float(RING_SAMPLES - 1) - 0.5;
        color += sampleClamped(base + dir * offset * Blur * strength);
    }

    return vec4(color / float(RING_SAMPLES), 1.0);
}

void main(){
    vec2 center = vec2(CenterX, CenterY);
    // the branch is on a uniform, so every pixel in the pass takes the same side of it
    fragColor = Style < 0.5 ? zoomBlur(center) : ringBand(center);
}
