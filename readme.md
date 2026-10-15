# FilmFollower

[![en](https://img.shields.io/badge/lang-en-blue.svg)](readme.md)
[![es](https://img.shields.io/badge/lang-es-red.svg)](readme.es.md)

I've been using an aplication to keep track of films and TV shows, they are really convenient, but sadly the day the app closed reached. With that, the idea came easily, make your own, FilmFollower.

FilmFollower is designed to (as all the other ones) keep track of films and TV series of your interest. This is a list of the functionalities I would like it to have one it's finished.

**Future Functionalities:**
- Store films and series from The Movie Database (TMDB) API service
- Change the visual theme of the app from the ones avaliable
- Save as favourite your favourite films
- Export your own database so you never loose it
- Remember the last episode you watch
- Create and share lists
- Notify once the next episode is released 


## Database
```
Film                                    Genre                               List
├── film_id (PK)                        ├── genre_id (PK)                   ├── list_id (PK)
├── title (NOT NULL)                    └── name (UNIQUE)                   └── name (UNIQUE)
├── original_title
├── overview                            FilmGenre                           List_item
├── release_date                        ├── film_id (PK, FK → Film)         ├── list_id (PK, FK → List)
├── runtime                             └── genre_id (PK, FK → Genre)       ├── media_id
├── poster_path                                                             ├── media_type
├── tmdb_status                         TvShowGenre                         └── position
├── tmdb_last_synced                    ├── show_id (PK, FK → TvShow)
├── rating (0-10 CHECK)                 └── genre_id (PK, FK → Genre)
├── watch_status (watchlist/seen/archived)
├── watched_date
├── times_watched
└── added_at

TvShow                                  Season                               Episode
├── show_id (PK)                        ├── show_id (PK, FK → TvShow)        ├── show_id (PK, FK → Season)
├── title (NOT NULL)                    ├── season_number (PK)               ├── season_number (PK) 
├── original_title                      ├── tmdb_season_id                   ├── episode_number (PK)
├── overview                            ├── name                             ├── tmdb_episode_id   
├── first_air_date                      ├── overview                         ├── title
├── number_of_seasons                   ├── air_date                         ├── overview 
├── number_of_episodes                  ├── episode_count                    ├── air_date 
├── poster_path                         └── poster_path                      ├── runtime 
├── tmdb_status                                                              └── watched_date
├── tmdb_last_synced
├── watch_status (watchlist/seen/archived)
├── rating (0-10 CHECK)
└── added_at
```