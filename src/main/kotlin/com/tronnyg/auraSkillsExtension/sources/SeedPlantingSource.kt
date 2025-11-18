package com.tronnyg.auraSkillsExtension.sources

import dev.aurelium.auraskills.api.source.CustomSource
import dev.aurelium.auraskills.api.source.SourceValues
import org.bukkit.Material

class SeedPlantingSource(
    values: SourceValues,
    val seed: Material,
    val multiplier: Double
) : CustomSource(values)
