package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MapLoader {

    public GameMap loadMap(String filePath) {
        GameMap gameMap = new GameMap();
        
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject jsonObject = new JSONObject(content);
            JSONArray layers = jsonObject.getJSONArray("layers");
            
            for (int i = 0; i < layers.length(); i++) {
                JSONObject layer = layers.getJSONObject(i);
                
                // Leggiamo solo i livelli che contengono mattonelle (tilelayer)
                if (layer.has("type") && layer.getString("type").equals("tilelayer")) {
                    JSONArray data = layer.getJSONArray("data");
                    
                    int index = 0;
                    for (int r = 0; r < 12; r++) {
                        for (int c = 0; c < 12; c++) {
                            int rawTileId = data.getInt(index);
                            
                            // Tiled usa 0 per indicare "Nessuna mattonella" (Trasparenza).
                            // Sovrascriviamo la matrice della mappa SOLO se c'è un blocco disegnato!
                            if (rawTileId != 0) {
                                int cleanTileId = rawTileId & 0x0FFFFFFF; 
                                gameMap.setTile(r, c, cleanTileId);
                            }
                            index++;
                        }
                    }
                }
            }
            System.out.println("Mappa 12x12 caricata con successo da JSON (Tutti i layer processati)!");

            for (int i = 0; i < layers.length(); i++) {
                JSONObject layer = layers.getJSONObject(i);
                
                if (layer.has("type") && layer.getString("type").equals("objectgroup")) {
                    JSONArray objects = layer.getJSONArray("objects");
                    
                    for (int j = 0; j < objects.length(); j++) {
                        JSONObject obj = objects.getJSONObject(j);
                        String objName = obj.getString("name");
                        
                        if (objName.equals("Key")) {
                            int keyX = (int) obj.getDouble("x");
                            int keyY = (int) obj.getDouble("y");
                            Key chiave = new Key(keyX, keyY);
                            gameMap.setKey(chiave);
                            System.out.println("✅ Chiave caricata in posizione X: " + keyX + ", Y: " + keyY);
                        }
                        
                        if (objName.equals("Porta")) {
                            int doorX = (int) obj.getDouble("x");
                            int doorY = (int) obj.getDouble("y");
                            int gridRow = doorY / 64;
                            int gridColLeft = doorX / 64;
                            int gridColRight = gridColLeft + 1; 
                            Door porta = new Door(doorX, doorY, gridColLeft, gridColRight, gridRow);
                            gameMap.setDoor(porta);
                            System.out.println("🚪 Porta caricata! Occupa la riga " + gridRow + ", colonne " + gridColLeft + " e " + gridColRight);
                        }

                        if (objName.equals("Zombie")) {
                            int pixelX = (int) obj.getDouble("x");
                            int pixelY = (int) obj.getDouble("y");
                            int gridX = pixelX / 64;
                            int gridY = pixelY / 64;
                            Zombie zombie = new Zombie(gridX, gridY);
                            gameMap.setZombie(zombie);
                            System.out.println("🧟‍♂️ Zombie spawnato nella griglia a X: " + gridX + ", Y: " + gridY);
                        }

                        if (objName.equals("Sopravvissuto")) {
                            int pixelX = (int) obj.getDouble("x");
                            int pixelY = (int) obj.getDouble("y");
                            int gridX = pixelX / 64;
                            int gridY = pixelY / 64;
                            Survivor survivor = new Survivor(gridX, gridY);
                            gameMap.setSurvivor(survivor);
                            System.out.println("🏃‍♂️ Sopravvissuto spawnato nella griglia a X: " + gridX + ", Y: " + gridY);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Errore fatale in MapLoader: " + e.getMessage());
        }
        
        return gameMap;
    }
}