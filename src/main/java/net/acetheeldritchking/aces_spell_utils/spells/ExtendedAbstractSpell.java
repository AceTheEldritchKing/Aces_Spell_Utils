package net.acetheeldritchking.aces_spell_utils.spells;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;
import net.acetheeldritchking.aces_spell_utils.utils.ASUtils;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ExtendedAbstractSpell extends AbstractSpell {
    public boolean hasRandomStartAnim()
    {
        return false;
    }

    public boolean hasRandomEndAnim()
    {
        return false;
    }

    // For advancement locking
    // Blocking this out for now until I actually understand what I'm doing
    /*public boolean isAdvancementLocked()
    {
        return false;
    }

    public String modID()
    {
        return null;
    }

    public String advancementPath()
    {
        return null;
    }*/
    //

    public List<AnimationHolder> startAnimations()
    {
        return new ArrayList<>();
    }

    public List<AnimationHolder> endAnimations()
    {
        return new ArrayList<>();
    }

    /*@Override
    public boolean canBeCraftedBy(Player player) {
        return isAdvancementLocked() ? ASUtils.hasAdvancementUnlocked(player, modID(), advancementPath()) : super.canBeCraftedBy(player);
    }*/

    @Override
    public AnimationHolder getCastStartAnimation() {
        if (hasRandomStartAnim())
        {
            Random random = new Random();
            AnimationHolder randomAnim = startAnimations().get(random.nextInt(startAnimations().size()));
            return randomAnim;
        } else
        {
            return super.getCastStartAnimation();
        }
    }

    @Override
    public AnimationHolder getCastFinishAnimation() {
        if (hasRandomEndAnim())
        {
            Random random = new Random();
            AnimationHolder randomAnim = endAnimations().get(random.nextInt(startAnimations().size()));
            return randomAnim;
        } else
        {
            return super.getCastFinishAnimation();
        }
    }
}
