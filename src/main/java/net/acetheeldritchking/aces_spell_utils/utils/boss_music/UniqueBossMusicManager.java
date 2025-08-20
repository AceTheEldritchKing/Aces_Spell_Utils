package net.acetheeldritchking.aces_spell_utils.utils.boss_music;

import net.acetheeldritchking.aces_spell_utils.entity.mobs.GenericUniqueBossEntity;
import net.acetheeldritchking.aces_spell_utils.entity.mobs.GenericUniqueBossEntity;
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
public class UniqueBossMusicManager {
    @Nullable
    private static UniqueBossMusicManager INSTANCE;
    static final SoundSource SOUND_SOURCE = SoundSource.RECORDS;

    GenericUniqueBossEntity genericBoss;
    final SoundManager manager;
    BossSoundInstance bossMusic;
    BossSoundInstance bossTransitionMusic;
    BossSoundInstance bossAltMusic;
    int phaseForTransition;
    int phaseForMusicChange;
    boolean hasCustomMusic;
    GenericUniqueBossEntity.Phase phase;
    Set<BossSoundInstance> layers = new HashSet<>();
    boolean finishedPlaying = false;

    private UniqueBossMusicManager(GenericUniqueBossEntity boss)
    {
        this.genericBoss = boss;
        this.manager = Minecraft.getInstance().getSoundManager();
        phase = GenericUniqueBossEntity.Phase.values()[boss.getPhase()];
        phaseForTransition = boss.usePhaseAsTransition();
        phaseForMusicChange = boss.usePhaseForMusicChange();

        hasCustomMusic = boss.hasCustomMusic();
        bossMusic = new BossSoundInstance(getBossMusic(), SOUND_SOURCE, true);
        bossTransitionMusic = new BossSoundInstance(getTransitionMusic(), SOUND_SOURCE, true);
        bossAltMusic = new BossSoundInstance(getOtherPhaseMusic(), SOUND_SOURCE, true);

        init();
    }

    private void init()
    {
        manager.stop(null, SoundSource.MUSIC);

        // We only do this if the boss wants to use our music manager
        if (hasCustomMusic)
        {
            switch (phase)
            {
                case FirstPhase -> {
                    addLayer(bossMusic);
                }
                // Since second phase or third can be used as a transition, we are going to check if it's being used as such
                // If not, play the alt music instead
                // Eventually this will be made more dynamic, but this will suffice
                case SecondPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 1)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 1)
                    {
                        addLayer(bossAltMusic);
                    }
                }
                case ThirdPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 2)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 2)
                    {
                        addLayer(bossAltMusic);
                    }
                }
                case FourthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 3)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 3)
                    {
                        addLayer(bossAltMusic);
                    }
                }
                case FifthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 4)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 4)
                    {
                        addLayer(bossAltMusic);
                    }
                }
                case SixthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 5)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 5)
                    {
                        addLayer(bossAltMusic);
                    }
                }
                case SeventhPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 6)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 6)
                    {
                        addLayer(bossAltMusic);
                    }
                }
                case EighthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 7)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 7)
                    {
                        addLayer(bossAltMusic);
                    }
                }
                case NinethPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 8)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 8)
                    {
                        addLayer(bossAltMusic);
                    }
                }
                case TenthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 9)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 9)
                    {
                        addLayer(bossAltMusic);
                    }
                }
                case EleventhPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 10)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 10)
                    {
                        addLayer(bossAltMusic);
                    }
                }
                case TwelfthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && genericBoss.usePhaseAsTransition() == 11)
                    {
                        addLayer(bossTransitionMusic);
                    } else if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseForMusicChange() == 11)
                    {
                        addLayer(bossAltMusic);
                    }
                }

                default -> {
                    if (!genericBoss.changeMusicOnPhaseChange())
                    {
                        addLayer(bossMusic);
                    }
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

    public static void createOrResumeInstance(GenericUniqueBossEntity boss)
    {
        if (INSTANCE == null || INSTANCE.isDonePlaying())
        {
            INSTANCE = new UniqueBossMusicManager(boss);
        }
        else
        {
            INSTANCE.triggerResumeMusic(boss);
        }
    }

    public static void stop(GenericUniqueBossEntity boss)
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

        var bossPhase = GenericUniqueBossEntity.Phase.values()[genericBoss.getPhase()];
        int transitionPhase = genericBoss.usePhaseAsTransition();
        int changePhase = genericBoss.usePhaseForMusicChange();

        // We only do this if the boss wants to use our music manager
        if (genericBoss.hasCustomMusic())
        {
            switch (bossPhase)
            {
                case FirstPhase -> {
                    if (!manager.isActive(bossMusic))
                    {
                        playFirstPhaseMusic();
                    }
                }
                case SecondPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 1)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.SecondPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.SecondPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 1)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.SecondPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.SecondPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                case ThirdPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 2)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.ThirdPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.ThirdPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 2)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.ThirdPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.ThirdPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                case FourthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 3)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.FourthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.FourthPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 3)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.FourthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.FourthPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                case FifthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 4)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.FifthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.FifthPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 4)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.FifthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.FifthPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                case SixthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 5)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.SixthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.SixthPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 5)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.SixthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.SixthPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                case SeventhPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 6)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.SeventhPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.SeventhPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 6)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.SeventhPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.SeventhPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                case EighthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 7)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.EighthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.EighthPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 7)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.EighthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.EighthPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                case NinethPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 8)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.NinethPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.NinethPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 8)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.NinethPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.NinethPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                case TenthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 9)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.TenthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.TenthPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 9)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.TenthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.TenthPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                case EleventhPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 10)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.EleventhPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.EleventhPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 10)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.EleventhPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.EleventhPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                case TwelfthPhase -> {
                    if (genericBoss.changeMusicOnPhaseChange() && genericBoss.hasTransitionPhase() && transitionPhase == 11)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.TwelfthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.TwelfthPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        }
                    } else if (genericBoss.changeMusicOnPhaseChange() && changePhase == 11)
                    {
                        if (phase != GenericUniqueBossEntity.Phase.TwelfthPhase)
                        {
                            phase = GenericUniqueBossEntity.Phase.TwelfthPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }
                /*case ThirdPhase -> {
                    if (phase != GenericUniqueBossEntity.Phase.ThirdPhase)
                    {
                        if (genericBoss.changeMusicOnPhaseChange() && genericBoss.usePhaseAsTransition())
                        {
                            phase = GenericUniqueBossEntity.Phase.ThirdPhase;
                            stopLayers();
                            playTransitionPhaseMusic();
                        } else if (genericBoss.changeMusicOnPhaseChange())
                        {
                            phase = GenericUniqueBossEntity.Phase.ThirdPhase;
                            stopLayers();
                            playAltPhaseMusic();
                        }
                    }
                }*/

                default -> {
                    if (!manager.isActive(bossMusic) && !genericBoss.changeMusicOnPhaseChange())
                    {
                        playFirstPhaseMusic();
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

    public void triggerResumeMusic(GenericUniqueBossEntity boss)
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
