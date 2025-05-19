package com.example.LogicBro.service;

import org.jfugue.pattern.Pattern;
import org.jfugue.player.Player;
import org.jfugue.theory.Chord;
import org.jfugue.theory.ChordProgression;
import org.jfugue.theory.Note;
import org.jfugue.theory.Scale;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.ArrayList;
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
        
        // Set tempo
        pattern.setTempo(tempo);
        
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
            Note[] notes = chord.getNotes();
            
            // Add base chord
            pattern.add(chord.toString());
            
            // Add melodic variations based on complexity
            if (complexity > 1) {
                for (Note note : scale.getNotes()) {
                    if (chord.contains(note)) {
                        pattern.add(note.toString());
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
        Scale scaleObj = new Scale(scale);
        
        // Generate accompanying voices
        for (int i = 0; i < voiceCount; i++) {
            Pattern voice = generateHarmonicVoice(melody, scaleObj, i);
            harmony.add(voice);
        }
        
        return CompletableFuture.completedFuture(harmony);
    }
    
    private Pattern generateHarmonicVoice(Pattern melody, Scale scale, int voiceIndex) {
        Pattern voice = new Pattern();
        int interval = (voiceIndex + 1) * 2; // Generate harmonies at different intervals
        
        // Add notes from the scale at the calculated interval
        Note[] scaleNotes = scale.getNotes();
        for (Note note : scaleNotes) {
            voice.add(note.transpose(interval).toString());
        }
        
        return voice;
    }
}
