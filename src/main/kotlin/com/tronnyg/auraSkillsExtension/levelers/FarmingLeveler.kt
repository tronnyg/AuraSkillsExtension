package com.tronnyg.auraSkillsExtension.levelers

import com.tronnyg.auraSkillsExtension.sources.BonemealSource
import com.tronnyg.auraSkillsExtension.sources.SeedPlantingSource
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

class FarmingLeveler(private val api: AuraSkillsApi, private val tillingType: SourceType?, private val seedPlantType: SourceType?, private val bonemealType: SourceType?) : Listener {

    private val tillingContext = LevelerContext(api, tillingType)
    private val bonemealContext = LevelerContext(api, bonemealType)
    private val seedContext = LevelerContext(api, seedPlantType)

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val block = e.clickedBlock ?: return
        val item = player.inventory.itemInMainHand ?: return
        val farmableCrops = setOf(
            Material.WHEAT_SEEDS,
            Material.BEETROOT_SEEDS,
            Material.POTATO,
            Material.CARROT,
            Material.MELON_SEEDS,
            Material.PUMPKIN_SEEDS,
            Material.TORCHFLOWER_SEEDS,
            Material.PITCHER_POD,
            Material.SWEET_BERRIES,
            Material.GLOW_BERRIES,
            Material.NETHER_WART,
            Material.COCOA_BEANS,
            Material.RED_MUSHROOM,
            Material.BROWN_MUSHROOM
        )

        // Must be right-click
        if (e.action != Action.RIGHT_CLICK_BLOCK) return

        when {
            // --- TILLING ---
            item.type.name.contains("HOE") -> {
                val blockType = block.type
                if (blockType != Material.DIRT &&
                    blockType != Material.GRASS_BLOCK &&
                    blockType != Material.DIRT_PATH &&
                    blockType != Material.COARSE_DIRT
                ) return

                val skillSource = api.sourceManager.getSingleSourceOfType(TillingSource::class.java) ?: return
                val source = skillSource.source()
                val skill = skillSource.skill()

                // Safety checks
                if (tillingContext.failsChecks(player, block.location, skill)) return

                val xp = source.xp * source.multiplier
                val user: SkillsUser = api.getUser(player.uniqueId)
                user.addSkillXp(skill, xp)
            }

            // --- BONEMEAL ---
            item.type == Material.BONE_MEAL -> {
                val crop = block.blockData as? Ageable ?: return
                if (crop.age >= crop.maximumAge) return  // Already fully grown

                val skillSource = api.sourceManager.getSingleSourceOfType(BonemealSource::class.java) ?: return
                val source = skillSource.source()
                val skill = skillSource.skill()

                // Safety checks
                if (bonemealContext.failsChecks(player, block.location, skill)) return

                val xp = source.xp * source.multiplier
                val user: SkillsUser = api.getUser(player.uniqueId)
                user.addSkillXp(skill, xp)
            }

            item.type in farmableCrops -> {

                val sources = api.sourceManager.getSourcesOfType(SeedPlantingSource::class.java)
                for (skillSource in sources) {
                    val source = skillSource.source()
                    val skill = skillSource.skill()

                    if (item.type != source.seed) continue
                    if (block.type != Material.FARMLAND) continue
                    val blockAbove = block.location.add(0.0, 1.0, 0.0).block
                    if (blockAbove.type != Material.AIR) continue

                    // Safety checks
                    if (seedContext.failsChecks(player, block.location, skill)) continue

                    api.getUser(player.uniqueId)?.addSkillXp(skill, source.xp)

                    break
                }
            }
        }
    }
}
