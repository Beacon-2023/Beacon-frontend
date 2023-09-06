package com.beacon.settings.guildLine

class RankItem(private val rankImageResource: Int, private var rankName: String) {

    fun getRankImageResource(): Int {
        return rankImageResource
    }

    fun setRankName(newName: String) {
        rankName = newName
    }

    fun getRankName(): String {
        return rankName
    }
}