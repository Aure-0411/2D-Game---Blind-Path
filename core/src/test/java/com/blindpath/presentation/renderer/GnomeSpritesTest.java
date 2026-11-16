package com.blindpath.presentation.renderer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import com.blindpath.domain.character.Direction;

class GnomeSpritesTest {

    @ParameterizedTest
    @EnumSource(Direction.class)
    void shouldReturnValidRegionNameForEveryDirection(Direction direction) {
        String regionName = GnomeSprites.regionName(direction);
        
        assertNotNull(regionName);
        assertFalse(regionName.isBlank());
    }

    @Test
    void shouldReturnExactExpectedNames() {
        assertEquals("walk_up", GnomeSprites.regionName(Direction.UP));
        assertEquals("walk_up_right", GnomeSprites.regionName(Direction.UP_RIGHT));
        assertEquals("walk_right", GnomeSprites.regionName(Direction.RIGHT));
        assertEquals("walk_down_right", GnomeSprites.regionName(Direction.DOWN_RIGHT));
        assertEquals("walk_down", GnomeSprites.regionName(Direction.DOWN));
        assertEquals("walk_down_left", GnomeSprites.regionName(Direction.DOWN_LEFT));
        assertEquals("walk_left", GnomeSprites.regionName(Direction.LEFT));
        assertEquals("walk_up_left", GnomeSprites.regionName(Direction.UP_LEFT));
    }

    @Test
    void atlasFileShouldContainFourFramesForEveryDirection() throws Exception {
        java.util.List<String> lines = java.nio.file.Files.readAllLines(
            java.nio.file.Path.of("../assets/gnome/gnome.atlas")
        );

        for (Direction d : Direction.values()) {
            if (d == Direction.NONE) continue;

            String regionName = GnomeSprites.regionName(d);
            java.util.Set<Integer> foundIndexes = new java.util.HashSet<>();

            for (int i = 0; i < lines.size(); i++) {
                // Si la ligne correspond exactement au nom de la région (ex: "gnome_up")
                if (lines.get(i).trim().equals(regionName)) {
                    // On lit les lignes indentées qui appartiennent à ce bloc
                    for (int j = i + 1; j < lines.size(); j++) {
                        String propLine = lines.get(j);
                        if (!propLine.startsWith(" ")) break; // Fin du bloc de cette région
                        
                        String trimmed = propLine.trim();
                        if (trimmed.startsWith("index:")) {
                            int index = Integer.parseInt(trimmed.substring("index:".length()).trim());
                            foundIndexes.add(index);
                            break;
                        }
                    }
                }
            }

            // Vérifie que les index 0, 1, 2 et 3 ont bien été trouvés
            for (int expectedIndex = 0; expectedIndex < 4; expectedIndex++) {
                assertTrue(
                    foundIndexes.contains(expectedIndex),
                    "Frame manquante dans l'atlas : " + regionName + " (index " + expectedIndex + ")"
                );
            }
        }
    }
}