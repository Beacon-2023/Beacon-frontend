package com.beacon.settings.guildLine

class RankItem(private val rankImageResource: Int, private val rankName: String) {

    fun getRankImageResource(): Int {
        return rankImageResource
    }

    fun getRankName(): String {
        return rankName
    }
}