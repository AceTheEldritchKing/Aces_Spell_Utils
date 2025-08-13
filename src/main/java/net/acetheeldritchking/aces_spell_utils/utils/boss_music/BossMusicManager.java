package net.acetheeldritchking.aces_spell_utils.utils.boss_music;

import net.acetheeldritchking.aces_spell_utils.entity.mobs.GenericBossEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(Dist.CLIENT)
public class BossMusicManager {
    @Nullable
    private static BossMusicManager INSTANCE;
    static final SoundSource SOUND_SOURCE = SoundSource.RECORDS;

    GenericBossEntity genericBoss;
    final SoundManager manager;
    BossSoundInstance bossMusic;
    BossSoundInstance bossTransitionMusic;
    BossSoundInstance bossAltMusic;
    GenericBossEntity.Phase phase;
    Set<BossSoundInstance> layers = new HashSet<>();
    boolean finishedPlaying = false;

    private BossMusicManager(GenericBossEntity boss)
    {
        this.genericBoss = boss;
        this.manager = Minecraft.getInstance().getSoundManager();
        phase = GenericBossEntity.Phase.values()[boss.getPhase()];

        bossMusic = new BossSoundInstance(getBossMusic(), SOUND_SOURCE, true);
        bossTransitionMusic = new BossSoundInstance(getTransitionMusic(), SOUND_SOURCE, true);
        bossAltMusic = new BossSoundInstance(getOtherPhaseMusic(), SOUND_SOURCE, true);

        init();
    }

    private void init()
    {
        manager.stop(null, SoundSource.MUSIC);
        switch (phase)
        {
            case FirstPhase -> {
                addLayer(bossMusic);
            }
            // Since second phase can be used as a transition, we are going to check if it's being used as such
            // If not, play the alt music instead
            case SecondPhase -> {
                if (genericBoss.getChangeMusicOnPhaseChange() && genericBoss.getUseSecondPhaseAsTransition())
                {
                    addLayer(bossTransitionMusic);
                } else if (genericBoss.getChangeMusicOnPhaseChange())
                {
                    addLayer(bossAltMusic);
                }
            }
            case ThirdPhase -> {
                if (genericBoss.getChangeMusicOnPhaseChange() && genericBoss.getUseSecondPhaseAsTransition())
                {
                    addLayer(bossAltMusic);
                }
            }
        }
    }

    public SoundEvent getBossMusic() {
        return genericBoss.getBossMusic();
    }

    public SoundEvent getTransitionMusic()
    {
        return genericBoss.getTransitionMusic();
    }

    public SoundEvent getOtherPhaseMusic()
    {
        return genericBoss.getOtherPhaseMusic();
    }

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event)
    {
        if (INSTANCE != null && !Minecraft.getInstance().isPaused())
        {
            INSTANCE.tick();
        }
    }

    public static void createOrResumeInstance(GenericBossEntity boss)
    {
        if (INSTANCE == null || INSTANCE.isDonePlaying())
        {
            INSTANCE = new BossMusicManager(boss);
        }
        else
        {
            INSTANCE.triggerResumeMusic(boss);
        }
    }

    public static void stop(GenericBossEntity boss)
    {
        if (INSTANCE != null && INSTANCE.genericBoss.getUUID().equals(boss.getUUID()))
        {
            INSTANCE.stopLayers();
            INSTANCE.finishedPlaying = true;
        }
    }

    private void tick()
    {
        if (isDonePlaying() || finishedPlaying)
        {
            return;
        }
        if (genericBoss.isDeadOrDying() || genericBoss.isRemoved())
        {
            stopLayers();
            finishedPlaying = true;
            return;
        }

        var bossPhase = GenericBossEntity.Phase.values()[genericBoss.getPhase()];
        switch (bossPhase)
        {
            case FirstPhase -> {
                if (!manager.isActive(bossMusic))
                {
                    playFirstPhaseMusic();
                }
            }
            case SecondPhase -> {
                if (phase != GenericBossEntity.Phase.SecondPhase)
                {
                    if (genericBoss.getChangeMusicOnPhaseChange() && genericBoss.getUseSecondPhaseAsTransition())
                    {
                        phase = GenericBossEntity.Phase.SecondPhase;
                        stopLayers();
                        playTransitionPhaseMusic();
                    } else if (genericBoss.getChangeMusicOnPhaseChange())
                    {
                        phase = GenericBossEntity.Phase.SecondPhase;
                        stopLayers();
                        playAltPhaseMusic();
                    }
                }
            }
            case ThirdPhase -> {
                if (phase != GenericBossEntity.Phase.ThirdPhase)
                {
                    if (genericBoss.getChangeMusicOnPhaseChange() && genericBoss.getUseSecondPhaseAsTransition())
                    {
                        phase = GenericBossEntity.Phase.ThirdPhase;
                        stopLayers();
                        playAltPhaseMusic();
                    }
                }
            }
        }
    }

    private boolean isDonePlaying()
    {
        for (BossSoundInstance soundInstance : layers)
        {
            if (!soundInstance.isStopped() && manager.isActive(soundInstance))
            {
                return false;
            }
        }

        return true;
    }

    private void addLayer(BossSoundInstance instance)
    {
        layers.stream().filter((sound) -> sound.isStopped() || !manager.isActive(sound)).toList().forEach(layers::remove);
        manager.play(instance);
        layers.add(instance);
    }

    public void stopLayers()
    {
        layers.forEach(BossSoundInstance::triggerStop);
    }

    public static void hardStop()
    {
        if (INSTANCE != null)
        {
            INSTANCE.layers.forEach(INSTANCE.manager::stop);
            INSTANCE = null;
        }
    }

    public void triggerResumeMusic(GenericBossEntity boss)
    {
        if (boss.getUUID().equals(this.genericBoss.getUUID()))
        {
            this.genericBoss = boss;
        }

        if (this.genericBoss.isRemoved())
        {
            layers.forEach((sound) -> {
                if (!manager.isActive(sound))
                {
                    manager.play(sound);
                }
            });
            finishedPlaying = false;
        }
    }

    private void playFirstPhaseMusic()
    {
        addLayer(bossMusic);
    }

    private void playTransitionPhaseMusic()
    {
        addLayer(bossTransitionMusic);
    }

    private void playAltPhaseMusic()
    {
        addLayer(bossAltMusic);
    }
}
