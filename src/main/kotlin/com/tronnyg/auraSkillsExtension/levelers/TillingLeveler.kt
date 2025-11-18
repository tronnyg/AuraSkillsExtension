package com.tronnyg.auraSkillsExtension.levelers

import com.tronnyg.auraSkillsExtension.sources.TillingSource
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.skill.Skills
import dev.aurelium.auraskills.api.source.LevelerContext
import dev.aurelium.auraskills.api.source.SourceType
import dev.aurelium.auraskills.api.user.SkillsUser
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent


class TillingLeveler(val api: AuraSkillsApi?, val type: SourceType?) : Listener {
    private val context: LevelerContext = LevelerContext(api, type)

    @EventHandler
    fun onTill(e: PlayerInteractEvent) {
        val player = e.player
        if (!e.hasBlock()) return
        if (e.action != Action.RIGHT_CLICK_BLOCK) return

        val clicked = e.clickedBlock!!.type

        if (clicked != Material.DIRT && clicked != Material.GRASS_BLOCK && clicked != Material.DIRT_PATH && clicked != Material.COARSE_DIRT) return

        val item = player.inventory.itemInMainHand
        if (!item.type.name.contains("HOE")) return

        // Safety checks
        if (context.failsChecks(player, e.clickedBlock!!.location, Skills.FARMING)) return

        // Get the custom source and multiplier
        val skillSource = api?.sourceManager?.getSingleSourceOfType(TillingSource::class.java) ?: return
        val source = skillSource.source()
        val skill = skillSource.skill()
        val xp = source.xp

        // Grant XP
        val user: SkillsUser? = api.getUser(player.uniqueId)
        user?.addSkillXp(skill, xp);

    }
}