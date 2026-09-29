package net.acetheeldritchking.aces_spell_utils.entity.mobs.trading;

import io.redspace.ironsspellbooks.player.AdditionalWanderingTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

public class MerchantTradeUtils {
    // Simple buy & sell class but with the option to have your own currency instead
    public static class OtherCurrencyBuy extends AdditionalWanderingTrades.SimpleTrade
    {
        public OtherCurrencyBuy(int tradeCount, ItemCost buy, Item currency, int minEmeralds, int maxEmeralds) {
            super(
                    (trader, random) -> {
                        return new MerchantOffer(
                                buy,
                                new ItemStack(currency, random.nextIntBetweenInclusive(minEmeralds, maxEmeralds)),
                                tradeCount,
                                0,
                                0.05F
                        );
                    }
            );
        }
    }

    public static class OtherCurrencySell extends AdditionalWanderingTrades.SimpleTrade
    {
        public OtherCurrencySell(int tradeCount, ItemStack sell, Item currency, int minEmeralds, int maxEmeralds) {
            super(
                    (trader, random) -> {
                        return new MerchantOffer(
                                new ItemCost(currency, random.nextIntBetweenInclusive(minEmeralds, maxEmeralds)),
                                sell,
                                tradeCount,
                                0,
                                0.05F
                        );
                    }
            );
        }
    }
}
