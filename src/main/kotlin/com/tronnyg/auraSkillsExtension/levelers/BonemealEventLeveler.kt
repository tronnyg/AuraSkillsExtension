package com.tronnyg.auraSkillsExtension.levelers

import com.tronnyg.auraSkillsExtension.sources.BonemealSource
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

class BonemealEventLeveler(val api: AuraSkillsApi?, val type: SourceType?) : Listener {
    private val context: LevelerContext = LevelerContext(api, type)

    @EventHandler
    fun onBonemealUse(e: PlayerInteractEvent) {
        val player = e.player
        val block = e.clickedBlock ?: return
        val item = player.inventory.itemInMainHand

        // Must be right-click and holding bonemeal
        if (e.action != Action.RIGHT_CLICK_BLOCK || item.type != Material.BONE_MEAL) return

        // Must be a growable crop
        val crop = block.blockData as? Ageable ?: return
        if (crop.age >= crop.maximumAge) return  // Already fully grown

        // Get custom source
        val skillSource = api?.sourceManager?.getSingleSourceOfType(BonemealSource::class.java) ?: return
        val source = skillSource.source()
        val skill = skillSource.skill()
        val xp = source.xp

        // Safety checks
        if (context.failsChecks(player, e.clickedBlock!!.location, Skills.FARMING)) return

        // Grant XP
        val user: SkillsUser? = api.getUser(player.uniqueId)
        user?.addSkillXp(skill, xp);
    }
}