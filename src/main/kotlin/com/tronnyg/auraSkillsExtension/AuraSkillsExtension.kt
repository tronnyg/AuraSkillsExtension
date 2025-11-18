package com.tronnyg.auraSkillsExtension

import com.tronnyg.auraSkillsExtension.levelers.BonemealEventLeveler
import com.tronnyg.auraSkillsExtension.levelers.TillingLeveler
import com.tronnyg.auraSkillsExtension.sources.BonemealSource
import com.tronnyg.auraSkillsExtension.sources.TillingSource
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.config.ConfigNode
import dev.aurelium.auraskills.api.source.SourceContext
import dev.aurelium.auraskills.api.source.SourceType
import dev.aurelium.auraskills.api.source.XpSourceParser
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
        val tillingLeveler = TillingLeveler(aura, tilling)
        server.pluginManager.registerEvents(tillingLeveler, this)

        val bonemealevent: SourceType  = registry.registerSourceType(
            "bonemealevent",
            XpSourceParser { source: ConfigNode?, context: SourceContext? ->
                val multiplier = source!!.node("multiplier").getDouble(1.0)
                BonemealSource(context!!.parseValues(source), multiplier)
            }
        )
        val bonemealEventLeveler = BonemealEventLeveler(aura, bonemealevent)
        server.pluginManager.registerEvents(bonemealEventLeveler, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
