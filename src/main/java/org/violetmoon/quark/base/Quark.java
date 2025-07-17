package org.violetmoon.quark.base;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.violetmoon.quark.base.proxy.ClientProxy;
import org.violetmoon.quark.base.proxy.CommonProxy;
import org.violetmoon.quark.integration.lootr.ILootrIntegration;
import org.violetmoon.quark.integration.lootr.LootrIntegration;
import org.violetmoon.quark.integration.terrablender.AbstractUndergroundBiomeHandler;
import org.violetmoon.quark.integration.terrablender.TerrablenderUndergroundBiomeHandler;
import org.violetmoon.quark.integration.terrablender.VanillaUndergroundBiomeHandler;
import org.violetmoon.zeta.Zeta;
import org.violetmoon.zeta.multiloader.Env;
import org.violetmoon.zetaimplforge.ForgeZeta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

@Mod(Quark.MOD_ID)
public class Quark {

	public static final String MOD_ID = "quark";

	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	public static final Zeta ZETA = new ForgeZeta(MOD_ID, LogManager.getLogger("quark-zeta"));
	public static final String ODDITIES_ID = ZETA.isProduction ? "quarkoddities" : "quark";

	public static Quark instance;
	public static CommonProxy proxy;

	public Quark(IEventBus bus) {
		instance = this;

		ZETA.start();

		proxy = Env.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
		proxy.start();

		// Causes issues for now, will see about re-enabling later
		//if (!ZETA.isProduction) // force all mixins to load in dev
		//	MixinEnvironment.getCurrentEnvironment().audit();

		bus.addListener(Quark::addPackFinders);
	}

	/*
	public static final IClaimIntegration FLAN_INTEGRATION = ZETA.modIntegration("flan",
			() -> FlanIntegration::new,
			() -> IClaimIntegration.Dummy::new);
	 */

	public static final ILootrIntegration LOOTR_INTEGRATION = ZETA.modIntegration("lootr",
			() -> LootrIntegration::new,
			() -> ILootrIntegration.Dummy::new);

	public static final AbstractUndergroundBiomeHandler TERRABLENDER_INTEGRATION = ZETA.modIntegration("terrablender",
			() -> TerrablenderUndergroundBiomeHandler::new,
			() -> VanillaUndergroundBiomeHandler::new);

	public static ResourceLocation asResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}

	public static <T> ResourceKey<T> asResourceKey(ResourceKey<? extends Registry<T>> base, String name) {
		return ResourceKey.create(base, asResource(name));
	}

	public static <T> TagKey<T> asTagKey(ResourceKey<? extends Registry<T>> base, String name) {
		return TagKey.create(base, asResource(name));
	}

	private static void addPackFinders(AddPackFindersEvent event){
		final boolean QUARK_VDO = true; //make config maybe?
		IModFile quarkJar = ModList.get().getModFileById(MOD_ID).getFile();
		if (event.getPackType() == PackType.SERVER_DATA && QUARK_VDO) {
			Path path = quarkJar.findResource("datapacks", "quark_vdo");

			PackSelectionConfig packSelectionConfig = new PackSelectionConfig(false, Pack.Position.TOP, true);
			PackLocationInfo packLocationInfo = new PackLocationInfo("quark_vdo", Component.literal("Quark VDO"), PackSource.BUILT_IN,
					Optional.empty()
			);

			PathPackResources.PathResourcesSupplier pathResourcesSupplier = new PathPackResources.PathResourcesSupplier(path);
			Pack pack = Pack.readMetaAndCreate(packLocationInfo, pathResourcesSupplier, PackType.SERVER_DATA, packSelectionConfig);
			event.addRepositorySource(packConsumer -> {
				packConsumer.accept(pack);
			});
		}
	}
}
