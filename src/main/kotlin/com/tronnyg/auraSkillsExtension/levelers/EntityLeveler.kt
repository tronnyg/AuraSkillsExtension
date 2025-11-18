package com.tronnyg.auraSkillsExtension.levelers

import com.tronnyg.auraSkillsExtension.sources.EntityLevelSource
import dev.aurelium.auraskills.api.AuraSkillsApi
import dev.aurelium.auraskills.api.source.LevelerContext
import dev.aurelium.auraskills.api.source.SourceType
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.persistence.PersistentDataType

class EntityLeveler(private val api: AuraSkillsApi, private val entityLeveler: SourceType) : Listener {
    @EventHandler
    fun onEntityDeath(e: EntityDeathEvent) {
        val player = e.entity.killer ?: return
        val entity = e.entity


        val sources = api.sourceManager.getSourcesOfType(EntityLevelSource::class.java)
        if (sources.isEmpty()) return

        // Loop through all EntityLevelSource instances
        for (skillSource in sources) {
            val source = skillSource.source()
            val skill = skillSource.skill()

            // Skip if the entity type doesn't match this source
            if (entity.type != source.entityType) continue

            // Safety checks
            val context = LevelerContext(api, source.type)
            if (context.failsChecks(player, entity.location, skill)) continue

            // Get mob level from LeveledMobs
            val mobLevel = getMobLevel(entity)

            // Scale XP by mob level
            val xp = source.baseXp * mobLevel

            api.getUser(player.uniqueId)?.addSkillXp(skill, xp)
        }
    }


    fun getMobLevel(livingEntity: LivingEntity) : Int{
        val levelledMobs = Bukkit.getPluginManager().getPlugin("LevelledMobs") ?: return 0
        val levelKey = NamespacedKey(levelledMobs, "level")
        return livingEntity.persistentDataContainer.get(levelKey, PersistentDataType.INTEGER) ?: 0
    }
}