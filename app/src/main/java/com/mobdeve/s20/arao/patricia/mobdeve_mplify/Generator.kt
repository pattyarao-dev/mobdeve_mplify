package com.mobdeve.s20.arao.patricia.mobdeve_mplify

class Generator {
    companion object {

        val sabrina = Artist("Sabrina", 142552, 10,  R.drawable.sabrina, "@sabriger")
        val niki = Artist("Niki", 4242552, 10,  R.drawable.niki, "@nikkier")
        val phoebe = Artist("Phoebe Bridges", 145252, 10, R.drawable.phoebe, "@phoebix")
        val clairo = Artist("Clairo", 8413553, 2, R.drawable.clairo, "@clairerthanever")
        val blink = Artist("Blink 182", 853178, 1, R.drawable.blink, "@blinkandimgone")

        fun loadPopularSongs(): ArrayList<Music> {
            val popularSongs = ArrayList<Music>()

            popularSongs.add(Music(blink, R.raw.blink_182_whats_my_age_again, 15362346, "What's My Age Again"))
            popularSongs.add(Music(blink, R.raw.blink_182_whats_my_age_again, 15362346, "First Date"))

            return popularSongs
        }

        fun loadAllSongs(): ArrayList<Music> {
            val allSongs = ArrayList<Music>()
            allSongs.add(Music(sabrina, R.raw.sabrina_good_graces, 2032, "Good Graces"))
            allSongs.add(Music(sabrina, R.raw.sabrina_bed_chem, 20123, "Bed Chem"))
            allSongs.add(Music(niki, R.raw.niki_focus,  12455, "Focus"))
            allSongs.add(Music(phoebe, R.raw.phoebe_motion_sickness,  5436, "Motion Sickness"))
            allSongs.add(Music(phoebe, R.raw.phoebe_scott_street,  5325, "Scott Street"))
            allSongs.add(Music(clairo, R.raw.clairo_juna, 151366, "Juna"))
            allSongs.add(Music(clairo, R.raw.clairo_nomad, 251561, "Nomad"))
            allSongs.add(Music(clairo, R.raw.clairo_sofia,  1251346, "Sofia"))
            allSongs.add(Music(blink, R.raw.blink_182_i_miss_you,  5136246, "I Miss You"))
            allSongs.add(Music(blink, R.raw.blink_182_whats_my_age_again, 15362346, "What's My Age Again"))
            return allSongs
        }

        fun loadArtistSongs(): ArrayList<Music> {
            val artistSongsData = ArrayList<Music>()
            artistSongsData.add(Music(clairo, R.raw.clairo_juna, 151366, "Juna"))
            artistSongsData.add(Music(clairo, R.raw.clairo_nomad, 251561, "Nomad"))
            artistSongsData.add(Music(clairo, R.raw.clairo_sofia,  1251346, "Sofia"))
            artistSongsData.add(Music(clairo, R.raw.clairo_sofia,  1251346, "Bags"))
            artistSongsData.add(Music(clairo, R.raw.clairo_sofia,  1251346, "Second Nature"))
            return artistSongsData
        }

        fun loadLikedSongs(): ArrayList<Music> {
            val likedSongsData = ArrayList<Music>()
            likedSongsData.add(Music(sabrina, R.raw.sabrina_good_graces, 2032, "Good Graces"))
            likedSongsData.add(Music(sabrina, R.raw.sabrina_bed_chem, 20123, "Bed Chem"))
            likedSongsData.add(Music(niki, R.raw.niki_focus,  12455, "Focus"))
            likedSongsData.add(Music(phoebe, R.raw.phoebe_motion_sickness,  5436, "Motion Sickness"))
            likedSongsData.add(Music(phoebe, R.raw.phoebe_scott_street,  5325, "Scott Street"))

            return likedSongsData
        }
    }
}