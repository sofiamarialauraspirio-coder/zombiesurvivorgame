package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MapLoader {

    public GameMap loadMap(String filePath) {
        GameMap gameMap = new GameMap();
        
        try {
            // Legge il file e lo trasforma in un oggetto JSON facilissimo da navigare
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            
            // Tiled salva i tile dentro un array chiamato "layers", prendiamo il primo (indice 0)
            JSONArray layers = jsonObject.getJSONArray("layers");
            JSONObject firstLayer = layers.getJSONObject(0);
            
            // Prendiamo la lista dei numerini "data"
            JSONArray data = firstLayer.getJSONArray("data");
            
            // Riempiamo la nostra matrice 12x12
            int index = 0;
            for (int r = 0; r < 12; r++) {
                for (int c = 0; c < 12; c++) {
                    int tileId = data.getInt(index);
                    gameMap.setTile(r, c, tileId);
                    index++;
                }
            }
            System.out.println("Mappa 12x12 caricata con successo da JSON!");
            
        } catch (Exception e) {
            System.err.println("Errore fatale: " + e.getMessage());
        }
        
        return gameMap;
    }
}