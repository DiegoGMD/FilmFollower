package com.diegogmd.filmfollower

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.diegogmd.filmfollower.data.local.FilmFillowerDatabase
import com.diegogmd.filmfollower.model.Film
import org.threeten.bp.LocalDate

fun seedFilms(context: Context) {
    val db = FilmFillowerDatabase(context).writableDatabase
    try {
        filmDataForTesting(db)
    } finally {
        db.close()
    }
}

fun filmDataForTesting(db: SQLiteDatabase) {
    Film(
        11,
        "Star Wars",
        "Star Wars",
        "Princess Leia is captured and held hostage by the evil Imperial forces in their effort to take over the galactic Empire. Venturesome Luke Skywalker and dashing captain Han Solo team together with the loveable robot duo R2-D2 and C-3PO to rescue the beautiful princess and restore peace and justice in the Empire.",
        LocalDate.of(1977, 5, 25),
        121,
        "/fai0rspsNeJCS69wHNjOdWxcI7P.jpg",
        "Released",
        LocalDate.of(2026, 10, 27),
        8.19999999999999929,
        "watchlist",
        null,
        0,
        LocalDate.of(2026, 10, 27)
    ).insertInto(db)

    Film(
        12,
        "Finding Nemo",
        "Finding Nemo",
        "Nemo, an adventurous young clownfish, is unexpectedly taken from his Great Barrier Reef home to a dentist's office aquarium. It's up to his worrisome father Marlin and a friendly but forgetful fish Dory to bring Nemo home -- meeting vegetarian sharks, surfer dude turtles, hypnotic jellyfish, hungry seagulls, and more along the way.",
        LocalDate.of(2003, 5, 30),
        100,
        "/eHuGQ10FUzK1mdOY69wF5pGgEf5.jpg",
        "Released",
        LocalDate.of(2026, 10, 27),
        7.79999999999999982,
        "seen",
        null,
        0,
        LocalDate.of(2026, 10, 27)
    ).insertInto(db)

    Film(
        78,
        "Blade Runner",
        "Blade Runner",
        "In the smog-choked dystopian Los Angeles of 2019, blade runner Rick Deckard is called out of retirement to terminate a quartet of replicants who have escaped to Earth seeking their creator for a way to extend their short life spans.",
        LocalDate.of(1982, 6, 25),
        118,
        "/63N9uy8nd9j7Eog2axPQ8lbr3Wj.jpg",
        "Released",
        LocalDate.of(2026, 10, 27),
        7.90000000000000035,
        "watchlist",
        null,
        0,
        LocalDate.of(2026, 10, 27)
    ).insertInto(db)

    Film(
        105,
        "Back to the Future",
        "Back to the Future",
        "Eighties teenager Marty McFly is accidentally sent back in time to 1955, inadvertently disrupting his parents first meeting and attracting his mother's romantic interest. Marty must repair the damage to history by rekindling his parents' romance and - with the help of his eccentric inventor friend Doc Brown - return to 1985.",
        LocalDate.of(1985, 7, 3),
        116,
        "/vN5B5WgYscRGcQpVhHl6p9DDTP0.jpg",
        "Released",
        LocalDate.of(2026, 10, 27),
        8.30000000000000071,
        "watchlist",
        null,
        0,
        LocalDate.of(2026, 10, 27)
    ).insertInto(db)

    Film(
        120,
        "The Lord of the Rings: The Fellowship of the Ring",
        "The Lord of the Rings: The Fellowship of the Ring",
        "Young hobbit Frodo Baggins, after inheriting a mysterious ring from his uncle Bilbo, must leave his home in order to keep it from falling into the hands of its evil creator. Along the way, a fellowship is formed to protect the ringbearer and make sure that the ring arrives at its final destination: Mt. Doom, the only place where it can be destroyed.",
        LocalDate.of(2001, 12, 18),
        179,
        "/6oom5QYQ2yQTMJIbnvbkBL9cHo6.jpg",
        "Released",
        LocalDate.of(2026, 10, 27),
        8.40000000000000035,
        "watchlist",
        null,
        0,
        LocalDate.of(2026, 10, 27)
    ).insertInto(db)

    Film(
        348,
        "Alien",
        "Alien",
        "During its return to the earth, commercial spaceship Nostromo intercepts a distress signal from a distant planet. When a three-member team of the crew discovers a chamber containing thousands of eggs on the planet, a creature inside one of the eggs attacks an explorer. The entire crew is unaware of the impending nightmare set to descend upon them when the alien parasite planted inside its unfortunate host is birthed.",
        LocalDate.of(1979, 5, 25),
        117,
        "/vfrQk5IPloGg1v9Rzbh2Eg3VGyM.jpg",
        "Released",
        LocalDate.of(2026, 10, 27),
        8.19999999999999929,
        "seen",
        null,
        0,
        LocalDate.of(2026, 10, 27)
    ).insertInto(db)

    Film(
        1895,
        "Star Wars: Episode III - Revenge of the Sith",
        "Star Wars: Episode III - Revenge of the Sith",
        "When the sinister Sith unveil a thousand-year-old plot to rule the galaxy, the Republic crumbles and from its ashes rises the evil Galactic Empire. Jedi hero Anakin Skywalker must choose a side.",
        LocalDate.of(2005, 5, 17),
        140,
        "/xfSAoBEm9MNBjmlNcDYLvLSMlnq.jpg",
        "Released",
        LocalDate.of(2026, 10, 27),
        7.5,
        "watchlist",
        null,
        0,
        LocalDate.of(2026, 10, 27)
    ).insertInto(db)

    Film(
        9277,
        "The Sting",
        "The Sting",
        "A novice con man teams up with an acknowledged master to avenge the murder of a mutual friend by pulling off the ultimate big con and swindling a fortune from a big-time mobster.",
        LocalDate.of(1973,12,25),
        129,
        "/ckmYng37zey8INYf6d10cVgIG93.jpg",
        "Released",
        LocalDate.of(2026,10,27),
        8.0,
        "watchlist",
        null,
        0,
        LocalDate.of(2026,10,27)
    ).insertInto(db)

    Film(
        10386,
        "The Iron Giant",
        "The Iron Giant",
        "In the small town of Rockwell, Maine in October 1957, a giant metal machine befriends a nine-year-old boy and ultimately finds its humanity by unselfishly saving people from their own fears and prejudices.",
        LocalDate.of(1999, 8, 6),
        86,
        "/k1Cv5CHJvqGWK1xJDUJz8DojFEy.jpg",
        "Released",
        LocalDate.of(2026, 10, 27),
        8.0,
        "seen",
        null,
        0,
        LocalDate.of(2026, 10, 27)
    ).insertInto(db)

    Film(
        10681,
        "WALL·E",
        "WALL·E",
        "After hundreds of years doing what he was built for, WALL•E— a robot designed to clean up the earth—discovers a new purpose in life when he meets a sleek search robot named EVE. EVE comes to realize that WALL•E has inadvertently stumbled upon the key to the planet's future, and races back to space to report to the humans. Meanwhile, WALL•E chases EVE across the galaxy and sets into motion one of the most imaginative adventures ever brought to the big screen.",
        LocalDate.of(2008,6,26),
        98,
        "/hbhFnRzzg6ZDmm8YAmxBnQpQIPh.jpg",
        "Released",
        LocalDate.of(2026,10,27),
        8.09999999999999964,
        "watchlist",
        null,
        0,
        LocalDate.of(2026,10,27)
    ).insertInto(db)

    Film(
        80274,
        "Ender's Game",
        "Ender's Game",
        "Based on the classic novel by Orson Scott Card, Ender's Game is the story of the Earth's most gifted children training to defend their homeplanet in the space wars of the future.",
        LocalDate.of(2013,10,24),
        114,
        "/pVcRI5YKnkkgaAD876jBeKb189d.jpg",
        "Released",
        LocalDate.of(2026,10,27),
        6.59999999999999964,
        "seen",
        null,
        0,
        LocalDate.of(2026,10,27)
    ).insertInto(db)

    Film(
        96721,
        "Rush",
        "Rush",
        "In the 1970s, a rivalry propels race car drivers Niki Lauda and James Hunt to fame and glory — until a horrible accident threatens to end it all.",
        LocalDate.of(2013,9,2),
        123,
        "/yqR7mzBKCv1ih6F2cXa0I8vFh3v.jpg",
        "Released",
        LocalDate.of(2026,10,27),
        7.70000000000000017,
        "seen",
        null,
        0,
        LocalDate.of(2026,10,27)
    ).insertInto(db)

    Film(
        212778,
        "Chef",
        "Chef",
        "When Chef Carl Casper suddenly quits his job at a prominent Los Angeles restaurant after refusing to compromise his creative integrity for its controlling owner, he is left to figure out what's next. Finding himself in Miami, he teams up with his ex-wife, his friend and his son to launch a food truck. Taking to the road, Chef Carl goes back to his roots to reignite his passion for the kitchen -- and zest for life and love.",
        LocalDate.of(2014,5,8),
        114,
        "/hyp8EXDmO4dSC8V6Q5jU7gD1kcg.jpg",
        "Released",
        LocalDate.of(2026,10,27),
        7.09999999999999964,
        "watchlist",
        null,
        0,
        LocalDate.of(2026,10,27)
    ).insertInto(db)

    Film(
        652837,
        "Josee, the Tiger and the Fish",
        "ジョゼと虎と魚たち",
        "With dreams of diving abroad, Tsuneo gets a job assisting Josee, an artist whose imagination takes her far beyond her wheelchair. But when the tide turns against them, they push each other to places they never thought possible, and inspire a love fit for a storybook.",
        LocalDate.of(2020,12,25),
        98,
        "/z1D8xi9x4uEhyFruo7uEHXUMD4K.jpg",
        "Released",
        LocalDate.of(2026,10,27),
        8.19999999999999929,
        "watchlist",
        null,
        0,
        LocalDate.of(2026,10,27)
    ).insertInto(db)

    Film(
        969681,
        "Spider-Man: Brand New Day",
        "Spider-Man: Brand New Day",
        "Fighting crime full-time as Spider-Man in a world that doesn't remember him—and the pressure of seeing his old friends move on without him—sparks a change in Peter Parker he may not have the power to control. But that transformation might also be the only thing that can stop a shocking new threat to the city and those he loves - a powerful villain no one can even see.",
        LocalDate.of(2026,7,29),
        145,
        "/bjiS5ipwxb9JFy3XRRN4OAilSeX.jpg",
        "Released",
        LocalDate.of(2026,10,27),
        8.5,
        "watchlist",
        null,
        0,
        LocalDate.of(2026,10,27)
    ).insertInto(db)

    Film(
        1311031,
        "Demon Slayer: Kimetsu no Yaiba Infinity Castle",
        "劇場版「鬼滅の刃」無限城編 第一章 猗窩座再来",
        "The Demon Slayer Corps are drawn into the Infinity Castle, where Tanjiro, Nezuko, and the Hashira face terrifying Upper Rank demons in a desperate fight as the final battle against Muzan Kibutsuji begins.",
        LocalDate.of(2025,7,18),
        156,
        "/fWVSwgjpT2D78VUh6X8UBd2rorW.jpg",
        "Released",
        LocalDate.of(2026,10,27),
        8.80000000000000071,
        "seen",
        null,
        0,
        LocalDate.of(2026,10,27)
    ).insertInto(db)
}