#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;

uniform float CenterX;
uniform float CenterY;
uniform float Radius;
uniform float Strength;
uniform float Rows;
uniform float Density;
uniform float Phase;
uniform float Aspect;

out vec4 fragColor;

// the displacement can reach past the screen edge, where the wrap mode would otherwise fold the sky into the ground
vec3 sampleClamped(vec2 uv) {
    return texture(DiffuseSampler, clamp(uv, 0.0, 1.0)).rgb;
}

// built without a sine, so it avoids the precision problems that form of hash has on some drivers
float hash21(vec2 p) {
    p = fract(p * vec2(233.34, 851.73));
    p += dot(p, p + 23.45);
    return fract(p.x * p.y);
}

void main(){
    vec2 fromCenter = texCoord - vec2(CenterX, CenterY);
    // measured with aspect applied so the reach is round rather than an ellipse
    float dist = length(vec2(fromCenter.x * Aspect, fromCenter.y));

    if (Radius < 1.0E-5 || Strength < 1.0E-5) {
        fragColor = vec4(sampleClamped(texCoord), 1.0);
        return;
    }

    // strongest over the shell and gone by one radius outside it, so the rest of the screen is untouched
    float reach = 1.0 - smoothstep(0.0, 1.0, dist / Radius);
    if (reach <= 0.0) {
        fragColor = vec4(sampleClamped(texCoord), 1.0);
        return;
    }

    // both the row and the clock are quantised, so bands snap to a new offset rather than sliding smoothly
    float row = floor(texCoord.y * Rows);
    float bucket = floor(Phase);
    // most rows hold still and only a few tear at a time, which is what reads as a signal breaking up
    float active = step(1.0 - Density, hash21(vec2(row, bucket)));
    float amount = hash21(vec2(bucket, row)) * 2.0 - 1.0;
    // squared with the sign kept, so small tears are common and large ones rare
    float offset = amount * abs(amount) * Strength * reach * active;

    // horizontal only, so verticals stay put and the break-up reads as a sideways tear
    // alpha is never sampled here, since the main target clears it to zero and propagating it renders unwritten pixels black
    fragColor = vec4(sampleClamped(vec2(texCoord.x + offset, texCoord.y)), 1.0);
}
