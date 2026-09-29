package net.acetheeldritchking.aces_spell_utils.spells;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.util.AnimationHolder;

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

    public List<AnimationHolder> startAnimations()
    {
        return new ArrayList<>();
    }

    public List<AnimationHolder> endAnimations()
    {
        return new ArrayList<>();
    }

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
