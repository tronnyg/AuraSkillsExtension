package com.tronnyg.auraSkillsExtension

import com.tronnyg.auraSkillsExtension.levelers.FarmingLeveler
import com.tronnyg.auraSkillsExtension.sources.BonemealSource
import com.tronnyg.auraSkillsExtension.sources.SeedPlantingSource
import com.tronnyg.auraSkillsExtension.sources.TillingSource
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.config.ConfigNode
import dev.aurelium.auraskills.api.source.SourceContext
import dev.aurelium.auraskills.api.source.SourceType
import dev.aurelium.auraskills.api.source.XpSourceParser
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin


class AuraSkillsExtension : JavaPlugin() {

    override fun onEnable() {
        val aura = AuraSkillsApi.get()
        val registry = aura.useRegistry("auraSkillsExtension", dataFolder)


        // Register source types
        val tilling: SourceType  = registry.registerSourceType(
            "tilling",
            XpSourceParser { source: ConfigNode?, context: SourceContext? ->
                val multiplier = source!!.node("multiplier").getDouble(1.0)
                TillingSource(context!!.parseValues(source), multiplier)
            }
        )
        val bonemealevent: SourceType  = registry.registerSourceType(
            "bonemealevent",
            XpSourceParser { source: ConfigNode?, context: SourceContext? ->
                val multiplier = source!!.node("multiplier").getDouble(1.0)
                BonemealSource(context!!.parseValues(source), multiplier)
            }
        )

        val seedPlantType: SourceType = registry.registerSourceType(
            "seedplant",
            XpSourceParser { source, context ->
                val seedMaterial = Material.matchMaterial(source!!.node("seed").string!!)
                    ?: error("Invalid seed material: ${source.node("seed").string}")
                SeedPlantingSource(context!!.parseValues(source), seedMaterial, source.node("multiplier").getDouble(1.0))

            }
        )

        // Register event listeners
        val farmingLeveler = FarmingLeveler(aura, tilling, bonemealevent, seedPlantType)
        server.pluginManager.registerEvents(farmingLeveler, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
