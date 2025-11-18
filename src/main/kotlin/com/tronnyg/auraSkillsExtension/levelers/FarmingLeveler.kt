package com.tronnyg.auraSkillsExtension.levelers

import com.tronnyg.auraSkillsExtension.sources.BonemealSource
import com.tronnyg.auraSkillsExtension.sources.TillingSource
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.skill.Skills
import dev.aurelium.auraskills.api.source.LevelerContext
import dev.aurelium.auraskills.api.source.SourceType
import dev.aurelium.auraskills.api.user.SkillsUser
import org.bukkit.Material
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class FarmingLeveler(private val api: AuraSkillsApi, private val tillingType: SourceType?, private val bonemealType: SourceType?) : Listener {

    private val tillingContext = LevelerContext(api, tillingType)
    private val bonemealContext = LevelerContext(api, bonemealType)
}
