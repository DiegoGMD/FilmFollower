package com.diegogmd.filmfollower

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.diegogmd.filmfollower.model.Episode
import com.diegogmd.filmfollower.model.Film
import com.diegogmd.filmfollower.model.Season
import com.diegogmd.filmfollower.model.TvShow
import org.threeten.bp.LocalDate

fun samplePlaceholderTvShows(): List<TvShow> = listOf(
    TvShow(
        77694, "Uma Musume: Pretty Derby", "ウマ娘 プリティーダービー",
        "In a world very much like our own, great race horses of the past have a chance " +
                "to be reborn as \"horse girls\"—girls with the ears and tails of horses as well " +
                "as their speed and endurance. The best of these horse girls go to train at " +
                "Tokyo's Tracen Academy, hopefully moving on to fame and fortune as both racers " +
                "and idols.",
        LocalDate.of(2018, 4, 2),
        4, 43, 8.2,"", "Ended",
        LocalDate.of(2026, 7, 31), "Watching",
        LocalDate.of(2026, 4, 12)
    ),
    TvShow(
        40075, "Gravity Falls", "Gravity Falls",
        "Twin brother and sister Dipper and Mabel Pines are in for an unexpected " +
                "adventure when they spend the summer helping their great uncle Stan run a " +
                "tourist trap in the mysterious town of Gravity Falls, Oregon.",
        LocalDate.of(2012, 6, 15),
        2, 40, 8.6,"", "Ended",
        LocalDate.of(2026, 7, 31), "Watching",
        LocalDate.of(2015, 2, 6)
    ),
    TvShow(
        2691, "Two and a Half Men", "Two and a Half Men",
        "A hedonistic jingle writer's free-wheeling life comes to an abrupt halt when " +
                "his brother and 10-year-old nephew move into his beach-front house.",
        LocalDate.of(2003, 9, 22),
        12, 262 , 7.5,"", "Ended",
        LocalDate.of(2026, 7, 31), "Watching",
        LocalDate.of(2026, 4, 25)
    ),
    TvShow(
        65942,
        "Re:ZERO -Starting Life in Another World-", "Re:ゼロから始める異世界生活",
        "Natsuki Subaru, an ordinary high school student, is on his way home from the " +
                "convenience store when he finds himself transported to another world. As he's " +
                "lost and confused in a new world where he doesn't even know left from right, " +
                "the only person to reach out to him was a beautiful girl with silver hair. " +
                "Determined to repay her somehow for saving him from his own despair, Subaru " +
                "agrees to help the girl find something she's looking for.",
        LocalDate.of(2016, 4, 4),
        1, 85 , 8.0,"", "Returning Series",
        LocalDate.of(2026, 7, 31), "Watching",
        LocalDate.of(2020, 9, 23)
    ),
    TvShow(
        209867,
        "Frieren: Beyond Journey's End", "葬送のフリーレン",
        "After the party of heroes defeated the Demon King, they restored peace to the " +
                "land and returned to lives of solitude. Generations pass, and the elven mage " +
                "Frieren comes face to face with humanity’s mortality. She takes on a new " +
                "apprentice and promises to fulfill old friends’ dying wishes. Can an elven mind " +
                "make peace with the nature of life and death? Frieren embarks on her quest to " +
                "find out.",
        LocalDate.of(2023, 9, 29),
        1, 38 , 8.8,"", "Returning Series",
        LocalDate.of(2026, 7, 31), "Watching",
        LocalDate.of(2020, 8, 11)
    ),
    TvShow(
        62560,
        "Mr. Robot", "Mr. Robot",
        "A young programmer, Elliot, suffers from a debilitating anti-social disorder " +
                "and decides that he can only connect to people by hacking them. He wields his " +
                "skills as a weapon to protect the people that he cares about. Elliot finds " +
                "himself in the intersection between a cybersecurity firm he works for and the " +
                "underworld organizations that are recruiting him to bring down corporate America.",
        LocalDate.of(2015, 6, 24),
        4, 45 , 8.3,"", "Ended",
        LocalDate.of(2026, 7, 31), "Watching",
        LocalDate.of(2026, 6, 21)
    ),

)
fun getPlaceholderTvShow(showId: Int): TvShow? {
    return samplePlaceholderTvShows().find { it.showId == showId }
}

fun samplePlaceholderTvSeason(): List<Season> = listOf()

fun samplePlaceholderEpisodes(): List<Episode> = listOf(
    Episode(
        77694, 2, 9, null, "Stopwatch",
        "Team Spica is stunned by BNW's outstanding performance at the Japanese Derby. " +
                "Meanwhile, Tokai Teio is supposed to be focused on making her comeback in the " +
                "Takarazuka Kinen, but ends up fracturing a bone for the third time. The doctor " +
                "tells her that even after it heals, there's a chance she'll never get her old " +
                "speed back again.",
        LocalDate.of(2021, 3, 2), 23, null
    ),
    Episode(
        40075, 2, 12, null, "A Tale of Two Stans",
        "Cornered underneath the Mystery Shack, Stan must finally reveal the secrets of " +
                "his past and his mysterious portal to Dipper and Mabel.",
        LocalDate.of(2015, 7, 13), 31, null
    ),
    Episode(
        2691, 5, 8, null, "Is There a Mrs. Waffles?",
        "Charlie writes a children's song and finds success as Charlie Waffles. " +
                "The children love him and the mothers seem to as well. Charlie's new found fame " +
                "irritates Alan.",
        LocalDate.of(2007, 11, 12), 20, null
    ),
    Episode(
        65942, 1, 74, null, "Who Are You?",
        "Subaru wakes up in the green room, remembering absolutely nothing about " +
                "the world he is in.",
        LocalDate.of(2026, 5, 27), 24, null
    ),
    Episode(
        209867, 1, 24, null, "Perfect Replicas",
        "Due to the Spiegel, a legendary monster that can create perfect replicas of " +
                "others, the test-takers who would conquer the Ruins of the King's Tomb must " +
                "first face the ultimate enemy: themselves.",
        LocalDate.of(2024, 2, 23), 25, null
    ),
    Episode(
        62560, 1, 4, null, "eps1.3_da3m0ns.mp4",
        "Elliot's inner-demons threaten an fsociety operation.",
        LocalDate.of(2015, 7, 15), 46, null
    ),
)