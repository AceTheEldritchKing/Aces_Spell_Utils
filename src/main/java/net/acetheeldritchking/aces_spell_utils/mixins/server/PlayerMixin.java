package net.acetheeldritchking.aces_spell_utils.mixins.server;

import net.acetheeldritchking.aces_spell_utils.registries.ASAttachmentRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(method = "dropEquipment", at = @At("HEAD"), cancellable = true)
    private void dropEquipment(CallbackInfo ci)
    {
        Object player = this;

        if (player instanceof ServerPlayer serverPlayer && serverPlayer.getData(ASAttachmentRegistry.KEEP_INV_ON_DEATH.get()))
        {
            ci.cancel();
        }
    }
}
