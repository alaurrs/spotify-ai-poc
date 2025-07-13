TRUNCATE users, artists, albums, tracks, listening_history RESTART IDENTITY CASCADE;

INSERT INTO users (user_id, username) VALUES ('1d2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', 'testuser');
INSERT INTO artists (artist_id, name) VALUES ('e6560734-5f13-4916-9ce6-5d3c56f62c82', 'Sabrina Carpenter'), ('89bc617c-1f4d-4b70-8b91-aa168389bda0', 'Olivia Rodrigo'), ('d13d8284-f8c2-4de8-a5e7-acf05ae3e002', 'The Weeknd');
INSERT INTO tracks (track_id, title, artist_id) VALUES ('75028a46-7fb2-4bb5-aff3-b71ac4b6384f', 'Nonsense', 'e6560734-5f13-4916-9ce6-5d3c56f62c82'), ('b757e0fe-d08b-4fdf-8a71-d286ae92ccdb', 'Feather', 'e6560734-5f13-4916-9ce6-5d3c56f62c82'), ('f8573590-ae54-48df-b3cb-5b621fbc0a5a', 'good 4 u', '89bc617c-1f4d-4b70-8b91-aa168389bda0'), ('d4f2a5f2-23e7-46de-8b56-23c5fd2ff11b', 'Blinding Lights', 'd13d8284-f8c2-4de8-a5e7-acf05ae3e002');

-- Simuler 17 écoutes pour "Nonsense" AUJOURD'HUI pour l'utilisateur de test
INSERT INTO listening_history (user_id, track_id, listened_at)
SELECT '1d2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', '75028a46-7fb2-4bb5-aff3-b71ac4b6384f', NOW() - (s.a * interval '1 minute')
FROM generate_series(1, 17) AS s(a);

-- Simuler 5 écoutes pour "Feather" aujourd'hui
INSERT INTO listening_history (user_id, track_id, listened_at)
SELECT '1d2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', 'b757e0fe-d08b-4fdf-8a71-d286ae92ccdb', NOW() - (s.a * interval '10 minute')
FROM generate_series(1, 5) AS s(a);

-- Simuler quelques écoutes plus anciennes
INSERT INTO listening_history (user_id, track_id, listened_at) VALUES
                                                                   ('1d2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', 'f8573590-ae54-48df-b3cb-5b621fbc0a5a', NOW() - interval '2 day'),
                                                                   ('1d2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d', 'd4f2a5f2-23e7-46de-8b56-23c5fd2ff11b', NOW() - interval '1 month');