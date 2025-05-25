package com.example.LogicBro.service;

import org.jfugue.pattern.Pattern;
import org.jfugue.theory.Chord;
import org.jfugue.theory.ChordProgression;
import org.jfugue.theory.Note;
import org.jfugue.theory.Scale;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ModularSoundService {
    
    @Async
    public CompletableFuture<Pattern> generateModularPattern(
            String baseChord,
            String scale,
            int complexity,
            double tempo,
            List<String> instruments) {
        
        Scale scaleObj = new Scale(scale);
        ChordProgression progression = new ChordProgression(baseChord);
        Pattern pattern = new Pattern();
        
        // Set tempo (convert double to int)
        pattern.setTempo((int)tempo);
        
        // Generate base pattern
        for (String instrument : instruments) {
            Pattern instrumentPattern = createInstrumentPattern(
                progression,
                scaleObj,
                complexity,
                instrument
            );
            pattern.add(instrumentPattern);
        }
        
        return CompletableFuture.completedFuture(pattern);
    }
    
    private Pattern createInstrumentPattern(
            ChordProgression progression,
            Scale scale,
            int complexity,
            String instrument) {
        
        Pattern pattern = new Pattern();
        pattern.setInstrument(instrument);
        
        // Add variations based on complexity
        for (Chord chord : progression.getChords()) {
            // Add base chord
            pattern.add(chord.toString());
            
            // Add melodic variations based on complexity
            if (complexity > 1) {
                // For JFugue 5, we'll work with music strings directly
                String[] scaleNotes = scale.toString().split(" ");
                String chordStr = chord.toString();
                for (String noteStr : scaleNotes) {
                    if (chordStr.contains(noteStr)) {
                        pattern.add(noteStr);
                    }
                }
            }
            
            // Add rhythmic variations based on complexity
            if (complexity > 2) {
                pattern.add("Ra*");  // Add rhythmic articulation
            }
        }
        
        return pattern;
    }
    
    @Async
    public CompletableFuture<Pattern> generateHarmony(
            Pattern melody,
            String scale,
            int voiceCount) {
        
        Pattern harmony = new Pattern();
        
        // Generate accompanying voices
        for (int i = 0; i < voiceCount; i++) {
            Pattern voice = new Pattern();
            int interval = (i + 1) * 2; // Generate harmonies at different intervals
            
            // Add notes from the melody at different intervals
            String melodyStr = melody.toString();
            String[] melodyTokens = melodyStr.split(" ");
            for (String token : melodyTokens) {
                try {
                    // Try to parse and transpose the note
                    Note note = new Note(token);
                    String noteStr = note.toString();
                    // Simple octave shift for harmony
                    if (noteStr.matches(".*\\d+.*")) {
                        int octave = Integer.parseInt(noteStr.replaceAll("\\D+", ""));
                        String notePart = noteStr.replaceAll("\\d+", "");
                        voice.add(notePart + (octave + interval/12));
                    } else {
                        voice.add(noteStr);
                    }
                } catch (Exception e) {
                    // If not a note, add as is
                    voice.add(token);
                }
            }
            harmony.add(voice);
        }
        
        return CompletableFuture.completedFuture(harmony);
    }
}
