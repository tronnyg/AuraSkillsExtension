package com.tronnyg.auraSkillsExtension.sources

import dev.aurelium.auraskills.api.source.CustomSource
import dev.aurelium.auraskills.api.source.SourceValues
import org.bukkit.entity.EntityType

class EntityLevelSource(
    values: SourceValues,
    val entityType: EntityType,
    val trigger: String,
    val damager: String?,
    val baseXp: Double
) : CustomSource(values)