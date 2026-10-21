# FilmFollower

[![en](https://img.shields.io/badge/lang-en-blue.svg)](readme.md)
[![es](https://img.shields.io/badge/lang-es-red.svg)](readme.es.md)

He usado por mucho tiempo una aplicación para seguir películas y series que me interesáran, no puedo negar que son muy comodas, peró desgraciadamente llegó el día en el que la que usaba cerró su servicio. Con ello senzillamente pensé en no depender de otras empresas y tratar de hacer mi própia aplicación, FilmFollower.

FilmFollower esta pensada para (como cualquier otra) seguir tus películas y series de interés.Esta es una lista de las funcionalidades que me gustaría que tuviera una vez acabada.

**Funcionalidades Futuras:**
- Guarda películas y series del servicio API de The Movie Database (TMDB)
- Cambia el tema visual de la aplicación de entre los disponibles
- Guarda tus películas favoritas
- Exporta tu base de datos para no perderla nunca
- Recuerda el ultimo episodio en el que te quedaste
- Crea y comparte listas
- Notificaciones una vez salga el siguiente episodio

## Database
```
Film                                 Genre                            List
├── film_id (PK)                     ├── genre_id (PK)                ├── list_id (PK)
├── title (NOT NULL)                 └── name (UNIQUE)                └── list_name (UNIQUE)
├── original_title
├── overview                         FilmGenre                        List_item
├── release_date                     ├── film_id (PK, FK → Film)      ├── list_id (PK, FK → List)
├── runtime                          └── genre_id (PK, FK → Genre)    ├── media_id
├── poster_path                                                       ├── media_type
├── tmdb_status                      TvShowGenre                      └── position
├── tmdb_last_synced                 ├── show_id (PK, FK → TvShow)
├── rating (0-10 CHECK)              └── genre_id (PK, FK → Genre)
├── watch_status (watchlist/seen/archived)
├── watched_date
├── times_watched
└── added_at

TvShow                               Season                           Episode
├── show_id (PK)                     ├── show_id (PK, FK → TvShow)    ├── show_id (PK, FK → Season)
├── title (NOT NULL)                 ├── season_number (PK)           ├── season_number (PK) 
├── original_title                   ├── tmdb_season_id               ├── episode_number (PK)
├── overview                         ├── name                         ├── tmdb_episode_id   
├── first_air_date                   ├── overview                     ├── title
├── number_of_seasons                ├── air_date                     ├── overview 
├── number_of_episodes               ├── episode_count                ├── air_date 
├── poster_path                      └── poster_path                  ├── runtime 
├── tmdb_status                                                       └── watched_date
├── tmdb_last_synced
├── watch_status (watchlist/seen/archived)
├── rating (0-10 CHECK)
└── added_at
```