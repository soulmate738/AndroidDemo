package com.example.sparkchaindemo.sql;

import android.content.Context;

import com.example.sparkchaindemo.entity.VoicePackage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anjia
 */
// VoicePackageRepository.java
public class VoicePackageRepository {

    private static final String DATA_FILE = "voice_packages.json";
    private Context context;
    private Gson gson;
    private List<VoicePackage> voicePackages;

    public VoicePackageRepository(Context context) {
        this.context = context;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadVoicePackages();
    }

    private void loadVoicePackages() {
        try {
            File file = new File(context.getFilesDir(), DATA_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                Type type = new TypeToken<List<VoicePackage>>(){}.getType();
                voicePackages = gson.fromJson(json.toString(), type);
            } else {
                voicePackages = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            voicePackages = new ArrayList<>();
        }
    }

    private void saveVoicePackages() {
        try {
            File file = new File(context.getFilesDir(), DATA_FILE);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(gson.toJson(voicePackages));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<VoicePackage> getAllVoicePackages() {
        return voicePackages;
    }

    public VoicePackage getVoicePackageById(String id) {
        for (VoicePackage voicePackage : voicePackages) {
            if (voicePackage.getId().equals(id)) {
                return voicePackage;
            }
        }
        return null;
    }

    public void addVoicePackage(VoicePackage voicePackage) {
        voicePackages.add(voicePackage);
        saveVoicePackages();
    }

    public void updateVoicePackage(VoicePackage voicePackage) {
        for (int i = 0; i < voicePackages.size(); i++) {
            if (voicePackages.get(i).getId().equals(voicePackage.getId())) {
                voicePackages.set(i, voicePackage);
                break;
            }
        }
        saveVoicePackages();
    }

    public void deleteVoicePackage(VoicePackage voicePackage) {
        voicePackages.remove(voicePackage);
        saveVoicePackages();
    }
}
