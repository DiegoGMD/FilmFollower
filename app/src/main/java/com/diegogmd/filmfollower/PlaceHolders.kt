package com.diegogmd.filmfollower

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

fun samplePlaceholderFilms(): List<Film> = listOf(
    Film(11, "Star Wars", "Star Wars",
        "Princess Leia is captured and held hostage by the evil Imperial forces in their " +
                "effort to take over the galactic Empire. Venturesome Luke Skywalker and dashing " +
                "captain Han Solo team together with the loveable robot duo R2-D2 and C-3PO to " +
                "rescue the beautiful princess and restore peace and justice in the Empire.",
        LocalDate.of(1997, 5, 25), 121, "","Released",
        LocalDate.of(2026, 7, 31), 8.2, "Watchlist",
        LocalDate.of(2009, 1, 1), 10,
        LocalDate.of(2026, 7, 31)
    ),
    Film(1895, "Star Wars: Episode III - Revenge of the Sith",
        "Star Wars: Episode III - Revenge of the Sith",
        "When the sinister Sith unveil a thousand-year-old plot to rule the galaxy, " +
                "the Republic crumbles and from its ashes rises the evil Galactic Empire. " +
                "Jedi hero Anakin Skywalker must choose a side.",
        LocalDate.of(2015, 5, 19), 140, "","Released",
        LocalDate.of(2026, 7, 31), 7.5, "Watchlist",
        LocalDate.of(2009, 1, 1), 15,
        LocalDate.of(2026, 7, 31)
    ),
    Film(10681, "WALL·E", "WALL·E",
        "After hundreds of years doing what he was built for, WALL•E— a robot designed to " +
                "clean up the earth—discovers a new purpose in life when he meets a sleek search " +
                "robot named EVE. EVE comes to realize that WALL•E has inadvertently stumbled upon " +
                "the key to the planet's future, and races back to space to report to the humans. " +
                "Meanwhile, WALL•E chases EVE across the galaxy and sets into motion one of the " +
                "most imaginative adventures ever brought to the big screen.",
        LocalDate.of(2008, 6, 21), 98, "","Released",
        LocalDate.of(2026, 7, 31), 8.1, "Watchlist",
        LocalDate.of(2009, 7, 29), 20,
        LocalDate.of(2026, 7, 31)
    ),
    Film(120, "The Lord of the Rings: The Fellowship of the Ring",
        "The Lord of the Rings: The Fellowship of the Ring",
        "Young hobbit Frodo Baggins, after inheriting a mysterious ring from his uncle " +
                "Bilbo, must leave his home in order to keep it from falling into the hands of " +
                "its evil creator. Along the way, a fellowship is formed to protect the ringbearer " +
                "and make sure that the ring arrives at its final destination: Mt. Doom, the only " +
                "place where it can be destroyed.",
        LocalDate.of(2001, 12, 10), 208, "","Released",
        LocalDate.of(2026, 7, 31), 8.4, "Watchlist",
        LocalDate.of(2025, 4, 14), 2,
        LocalDate.of(2026, 7, 31)
    ),
    Film(1003596, "Avengers: Doomsday", "Avengers: Doomsday",
        "Beloved heroes from three distinct universes are set on a deadly collision course " +
                "and face an existential threat unlike anything they've ever encountered.",
        LocalDate.of(2026, 12, 18), 208, "","Post Production",
        LocalDate.of(2026, 7, 31), 8.4, "Watchlist",
        null, 0,
        LocalDate.of(2026, 7, 31)
    ),
    Film(78, "Blade Runner", "Blade Runner",
        "In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is " +
                "called out of retirement to terminate a quartet of replicants who have escaped to " +
                "Earth seeking their creator for a way to extend their short life spans.",
        LocalDate.of(1982, 6, 25), 117, "","Released",
        LocalDate.of(2026, 7, 31), 7.9, "Watchlist",
        LocalDate.of(2016, 8, 22), 1,
        LocalDate.of(2026, 7, 31)
    ),
    Film(1170608, "Dune: Part Three", "Dune: Part Three",
        "Emperor Paul Atreides faces the fallout from his ascent to power as political " +
                "plots and a galaxy-wide holy war endanger the future only he can see.",
        LocalDate.of(2026, 12, 18), 208, "","Post Production",
        LocalDate.of(2026, 7, 31), 8.4, "Watchlist",
        null, 0,
        LocalDate.of(2026, 7, 31)
    ),
)

fun getPlaceholderFilm(filmId: Int): Film? {
    return samplePlaceholderFilms().find { it.filmId == filmId }
}