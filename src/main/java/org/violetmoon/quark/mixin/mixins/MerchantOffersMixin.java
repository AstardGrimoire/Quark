package org.violetmoon.quark.mixin.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.violetmoon.quark.content.experimental.hax.PseudoAccessorMerchantOffer;

import java.util.Collection;

@Mixin(MerchantOffers.class)
public class MerchantOffersMixin {

	/*@Inject(method = "<init>(Ljava/util/Collection;)V", at = @At("RETURN"))
	public void setUpTiers(Collection<MerchantOffer> merchantOffers, CallbackInfo ci) {
		MerchantOffers offers = (MerchantOffers) (Object) this;

		for(int i = 0; i < offers.size(); i++) {
            MerchantOffer offer = merchantOffers.stream().toList().get(i);

			if(offer.quark$getTier() < 0)
				offer.quark$setTier(i / 2);
			// We infer tiers for preexisting villagers, assuming each tier has two offers.
			// This assumption can be wrong, but usually won't be wrong enough to matter.
		}
	}*/

}
