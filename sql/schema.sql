DROP TABLE IF EXISTS listening_history, track_genres, users, tracks, albums, artists, genres CASCADE;

CREATE TABLE users ( user_id UUID PRIMARY KEY, username VARCHAR(50) NOT NULL UNIQUE );
CREATE TABLE artists ( artist_id UUID PRIMARY KEY, name VARCHAR(100) NOT NULL );
CREATE TABLE albums ( album_id UUID PRIMARY KEY, title VARCHAR(150) NOT NULL, artist_id UUID REFERENCES artists(artist_id) );
CREATE TABLE tracks ( track_id UUID PRIMARY KEY, title VARCHAR(150) NOT NULL, artist_id UUID REFERENCES artists(artist_id) );
CREATE TABLE listening_history ( listen_id BIGSERIAL PRIMARY KEY, user_id UUID REFERENCES users(user_id), track_id UUID REFERENCES tracks(track_id), listened_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP );

CREATE INDEX ON listening_history (user_id, listened_at);