package com.timworksports.crestedpass.ui.media

import com.timworksports.crestedpass.R

fun imageRes(name: String): Int = when (name) {
    "shot_late_winner" -> R.drawable.shot_late_winner
    "shot_bwindi" -> R.drawable.shot_bwindi
    "shot_kampala_night" -> R.drawable.shot_kampala_night
    "shot_nakivubo_gates" -> R.drawable.shot_nakivubo_gates
    "shot_nile_cafe" -> R.drawable.shot_nile_cafe
    "reel_stands" -> R.drawable.reel_stands
    "reel_winner" -> R.drawable.reel_winner
    "reel_bwindi" -> R.drawable.reel_bwindi
    "reel_fanvillage" -> R.drawable.reel_fanvillage
    "reel_gates" -> R.drawable.reel_gates
    "reel_cafe" -> R.drawable.reel_cafe
    "dest_bwindi" -> R.drawable.dest_bwindi
    "dest_nile" -> R.drawable.dest_nile
    "dest_nakasero" -> R.drawable.dest_nakasero
    "dest_victoria" -> R.drawable.dest_victoria
    "highlight_winner" -> R.drawable.highlight_winner
    "highlight_ceremony" -> R.drawable.highlight_ceremony
    "event_kenya_match" -> R.drawable.event_kenya_match
    "event_soccer" -> R.drawable.event_soccer
    "event_basketball" -> R.drawable.event_basketball
    "event_tennis" -> R.drawable.event_tennis
    "event_boxing" -> R.drawable.event_boxing
    "event_swim" -> R.drawable.event_swim
    "event_track" -> R.drawable.event_track
    "hero_venue" -> R.drawable.hero_venue
    "coach_sofia" -> R.drawable.coach_sofia
    "coach_marcus" -> R.drawable.coach_marcus
    "coach_maya" -> R.drawable.coach_maya
    "coach_liam" -> R.drawable.coach_liam
    "coach_kenji" -> R.drawable.coach_kenji
    "coach_aisha" -> R.drawable.coach_aisha
    "event_football" -> R.drawable.event_football
    "ad_tickets" -> R.drawable.ad_tickets
    "ad_nile" -> R.drawable.ad_nile
    "ad_band" -> R.drawable.ad_band
    else -> R.drawable.app_icon
}
