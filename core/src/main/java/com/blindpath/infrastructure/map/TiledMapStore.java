package com.blindpath.infrastructure.map;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Disposable;

/**
 * Charge chaque carte Tiled une seule fois et partage ses textures entre les niveaux.
 *
 * Avant : la même carte (donc le tileset de murs de 4000x4750 px et deux calques
 * image de 7500x7500 px) était décodée 4 fois par niveau. Ici, l'AssetManager
 * compte les références des textures : un tileset commun à deux niveaux n'est
 * décodé qu'une fois, et le niveau suivant peut être préchargé en arrière-plan.
 */
public class TiledMapStore implements Disposable {

    private final AssetManager assets;
    private final Set<String> requested = new HashSet<>();

    /** Crée un cache vide, qui charge les cartes depuis les assets internes. */
    public TiledMapStore() {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assets = new AssetManager(resolver);
        assets.setLoader(TiledMap.class, new TmxMapLoader(resolver));
    }

    /**
     * Demande le chargement en arrière-plan (sans bloquer).
     *
     * @param path chemin de la carte
     */
    public void preload(String path) {
        if (requested.add(path)) {
            assets.load(path, TiledMap.class);
        }
    }

    /** À appeler à chaque frame : avance le chargement sans dépasser un petit budget. */
    public void update() {
        assets.update(4);
    }

    /**
     * Renvoie la carte, en terminant son chargement si nécessaire (instantané si préchargée).
     *
     * @param path chemin de la carte
     * @return la carte chargée
     */
    public TiledMap get(String path) {
        preload(path);
        if (!assets.isLoaded(path, TiledMap.class)) {
            assets.finishLoadingAsset(path);
        }
        return assets.get(path, TiledMap.class);
    }

    /**
     * Libère toutes les cartes sauf celles données (les textures partagées restent chargées).
     *
     * @param keep chemins des cartes à garder
     */
    public void retainOnly(Set<String> keep) {
        Iterator<String> it = requested.iterator();
        while (it.hasNext()) {
            String path = it.next();
            if (!keep.contains(path)) {
                it.remove();
                assets.unload(path);
            }
        }
    }

    /** Libère toutes les cartes et leurs textures. */
    @Override
    public void dispose() {
        requested.clear();
        assets.dispose();
    }
}
