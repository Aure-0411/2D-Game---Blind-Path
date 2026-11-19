# Intro de la démo

`Blind_Path_Intro.gif` est l'original fourni. Les 16 fichiers `intro_0000.png`
à `intro_0015.png` sont ses images recomposées (en tenant compte des modes
de disposition du GIF). `animation.json` conserve leurs durées en secondes.

`IntroScreen` utilise ces PNG pour une lecture LibGDX sans décodeur GIF
supplémentaire. L'animation est jouée une seule fois, pendant 10,4 secondes,
puis le menu principal s'ouvre. Elle est lancée uniquement au démarrage de
l'application, pas à chaque partie. Les proportions 354 × 265 sont conservées.

Si le GIF change, régénérer aussi les PNG et le manifeste de durées.
