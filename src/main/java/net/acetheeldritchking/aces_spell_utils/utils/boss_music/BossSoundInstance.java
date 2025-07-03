package net.acetheeldritchking.aces_spell_utils.utils.boss_music;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class BossSoundInstance extends AbstractTickableSoundInstance {
    boolean starting = false;
    private boolean triggerEnd = false;

    protected BossSoundInstance(SoundEvent soundEvent, SoundSource source, boolean loop) {
        super(soundEvent, source, SoundInstance.createUnseededRandom());
        this.looping = loop;
        this.starting = false;
    }

    @Override
    public void tick() {
        if (starting)
        {
            starting = false;
        }
        if (triggerEnd)
        {
            this.stop();
        }
    }

    public void triggerStop()
    {
        this.triggerEnd = true;
    }

    public void triggerStart()
    {
        this.triggerEnd = false;
        starting = true;
    }
}
