document.addEventListener('DOMContentLoaded', function() {
    let wavesurfer = null;
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const uploadProgress = document.getElementById('uploadProgress');
    const progressBar = uploadProgress?.querySelector('.progress-bar');
    const uploadStatus = document.getElementById('uploadStatus');
    const analysisResults = document.getElementById('analysisResults');
    const variationModal = document.getElementById('variationModal');
    const generateVariationBtn = document.getElementById('generateVariation');
    const playBtn = document.getElementById('playBtn');
    const stopBtn = document.getElementById('stopBtn');
    const chordSequence = document.getElementById('chordSequence');
    const melodyPatterns = document.getElementById('melodyPatterns');
    
    let keyVis, chordVis, melodyVis;
    let currentFileId = null;

    // Initialize visualizations
    function initVisualizations() {
        const keyVisualEl = document.getElementById('keyVisual');
        const chordVisualEl = document.getElementById('chordVisual');
        const melodyVisualEl = document.getElementById('melodyVisual');
        
        if (keyVisualEl) keyVis = new Visualizations.KeyVisualization(keyVisualEl);
        if (chordVisualEl) chordVis = new Visualizations.ChordVisualization(chordVisualEl);
        if (melodyVisualEl) melodyVis = new Visualizations.MelodyVisualization(melodyVisualEl);
    }

    function updateVisualizations(data) {
        try {
            if (data.key && data.scale && keyVis) {
                keyVis.drawCircleOfFifths(data.key, data.scale);
            }
            if (data.chordProgression && chordVis) {
                chordVis.drawChordProgression(data.chordProgression);
            }
            if (data.melodicPattern && melodyVis) {
                melodyVis.drawMelodicPattern(data.melodicPattern);
            }
        } catch (error) {
            console.error('Error updating visualizations:', error);
        }
    }
    
    function initWaveSurfer() {
        const waveformContainer = document.getElementById('waveform');
        if (!waveformContainer) return;
        
        wavesurfer = WaveSurfer.create({
            container: '#waveform',
            waveColor: 'var(--primary-color)',
            progressColor: 'var(--primary-dark)',
            cursorColor: 'var(--accent-color)',
            height: 120,
            responsive: true,
            normalize: true,
            partialRender: true,
            backend: 'WebAudio'
        });

        wavesurfer.on('ready', function() {
            if (playBtn) playBtn.disabled = false;
            if (stopBtn) stopBtn.disabled = false;
            showToast('Audio loaded successfully', 'success');
        });

        wavesurfer.on('finish', function() {
            if (playBtn) {
                playBtn.innerHTML = '<i class="mdi mdi-play"></i> Play';
            }
        });

        wavesurfer.on('error', function(error) {
            console.error('WaveSurfer error:', error);
            showToast('Error loading audio', 'error');
        });
    }

    // Initialize everything
    initWaveSurfer();
    initVisualizations();

    // Play/Stop controls with enhanced feedback
    if (playBtn) {
        playBtn.addEventListener('click', () => {
            if (!wavesurfer) return;
            
            try {
                if (wavesurfer.isPlaying()) {
                    wavesurfer.pause();
                    playBtn.innerHTML = '<i class="mdi mdi-play"></i> Play';
                    playBtn.classList.remove('btn-playing');
                } else {
                    wavesurfer.play();
                    playBtn.innerHTML = '<i class="mdi mdi-pause"></i> Pause';
                    playBtn.classList.add('btn-playing');
                }
            } catch (error) {
                console.error('Playback error:', error);
                showToast('Error during playback', 'error');
            }
        });
    }

    if (stopBtn) {
        stopBtn.addEventListener('click', () => {
            if (!wavesurfer) return;
            
            try {
                wavesurfer.stop();
                if (playBtn) {
                    playBtn.innerHTML = '<i class="mdi mdi-play"></i> Play';
                    playBtn.classList.remove('btn-playing');
                }
            } catch (error) {
                console.error('Stop error:', error);
            }
        });
    }

    // Enhanced drag and drop handling
    if (dropZone) {
        dropZone.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropZone.classList.add('upload-area-active');
        });

        dropZone.addEventListener('dragleave', (e) => {
            e.preventDefault();
            if (!dropZone.contains(e.relatedTarget)) {
                dropZone.classList.remove('upload-area-active');
            }
        });

        dropZone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropZone.classList.remove('upload-area-active');
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                handleFileUpload(files[0]);
            }
        });

        dropZone.addEventListener('click', () => {
            if (fileInput) fileInput.click();
        });
    }

    if (fileInput) {
        fileInput.addEventListener('change', (e) => {
            if (e.target.files.length > 0) {
                handleFileUpload(e.target.files[0]);
            }
        });
    }

    // Enhanced file upload handling with better progress tracking
    async function handleFileUpload(file) {
        try {
            // Validate file using common utility
            validateAudioFile(file);
            
            const formData = new FormData();
            formData.append('file', file);
            
            // Show upload progress
            if (uploadProgress) {
                uploadProgress.style.display = 'block';
                uploadProgress.classList.add('active');
            }
            
            // Update UI to show file info
            updateUploadStatus(`Uploading ${file.name} (${formatFileSize(file.size)})...`);

            const response = await fetch('/api/audio/upload', {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                throw new Error(`Upload failed: ${response.statusText}`);
            }
            
            const data = await response.json();
            currentFileId = data.fileId;
            
            updateUploadStatus('Processing audio...');
            
            // Load the audio file into WaveSurfer
            if (wavesurfer) {
                const audioUrl = URL.createObjectURL(file);
                wavesurfer.load(audioUrl);
            }
            
            await processAudio(data.fileId);
            
        } catch (error) {
            console.error('Upload error:', error);
            showToast(error.message || 'Error uploading file', 'error');
            hideUploadProgress();
        }
    }

    function updateUploadStatus(message, progress = null) {
        if (uploadStatus) {
            uploadStatus.textContent = message;
        }
        if (progress !== null && progressBar) {
            progressBar.style.width = progress + '%';
            progressBar.setAttribute('aria-valuenow', progress);
        }
    }

    function hideUploadProgress() {
        if (uploadProgress) {
            uploadProgress.style.display = 'none';
            uploadProgress.classList.remove('active');
        }
    }

    // Enhanced audio processing and analysis
    async function processAudio(fileId) {
        try {
            updateUploadStatus('Analyzing audio patterns...');
            
            const response = await fetch(`/api/audio/analyze/${fileId}`);
            if (!response.ok) {
                throw new Error(`Analysis failed: ${response.statusText}`);
            }
            
            const data = await response.json();
            
            // Update visualizations with new data
            updateVisualizations(data);
            
            // Show analysis results with animation
            hideUploadProgress();
            if (analysisResults) {
                analysisResults.style.display = 'grid';
                analysisResults.classList.add('fade-in');
            }
            
            // Update UI elements with analysis data
            updateAnalysisResults(data);
            
            showToast('Audio analysis complete!', 'success');
            
        } catch (error) {
            console.error('Analysis error:', error);
            showToast(error.message || 'Error analyzing audio', 'error');
            hideUploadProgress();
        }
    }

    function updateAnalysisResults(data) {
        // Update key and scale info
        const keyChip = document.getElementById('keyChip');
        const scaleChip = document.getElementById('scaleChip');
        
        if (keyChip && data.key) {
            keyChip.textContent = `Key: ${data.key}`;
        }
        if (scaleChip && data.scale) {
            scaleChip.textContent = `Scale: ${data.scale}`;
        }
        
        // Update chord sequence
        if (chordSequence && data.chordProgression) {
            chordSequence.innerHTML = data.chordProgression
                .map(chord => `<div class="chip chip-primary">${chord}</div>`)
                .join('');
        }
        
        // Update melody patterns
        if (melodyPatterns && data.melodicPatterns) {
            melodyPatterns.innerHTML = data.melodicPatterns
                .map(pattern => `<div class="chip chip-secondary">${pattern}</div>`)
                .join('');
        }
        
        // Update additional analysis info
        updateAdditionalInfo(data);
    }

    function updateAdditionalInfo(data) {
        // Update tempo if available
        const tempoEl = document.getElementById('tempo');
        if (tempoEl && data.tempo) {
            tempoEl.textContent = `${data.tempo} BPM`;
        }
        
        // Update time signature if available
        const timeSigEl = document.getElementById('timeSignature');
        if (timeSigEl && data.timeSignature) {
            timeSigEl.textContent = data.timeSignature;
        }
        
        // Update confidence scores with progress bars
        updateConfidenceScores(data);
    }

    function updateConfidenceScores(data) {
        const scores = [
            { id: 'keyConfidence', value: data.keyConfidence },
            { id: 'chordConfidence', value: data.chordConfidence },
            { id: 'melodyConfidence', value: data.melodyConfidence }
        ];
        
        scores.forEach(score => {
            const element = document.getElementById(score.id);
            if (element && score.value !== undefined) {
                const percentage = Math.round(score.value * 100);
                element.style.width = percentage + '%';
                element.setAttribute('aria-valuenow', percentage);
                element.textContent = percentage + '%';
            }
        });
    }

    // Generate variation functionality
    if (generateVariationBtn) {
        generateVariationBtn.addEventListener('click', async () => {
            if (!currentFileId) {
                showToast('Please upload and analyze an audio file first', 'warning');
                return;
            }
            
            const variationAmount = document.getElementById('variationAmount')?.value || 50;
            const variationStyle = document.getElementById('variationStyle')?.value || 'similar';
            
            try {
                showLoading(generateVariationBtn.parentElement, 'Generating variation...');
                
                const response = await fetch('/api/audio/generate-variation', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        fileId: currentFileId,
                        amount: parseInt(variationAmount),
                        style: variationStyle
                    })
                });
                
                if (!response.ok) {
                    throw new Error('Failed to generate variation');
                }
                
                const result = await response.json();
                showToast('Variation generated successfully!', 'success');
                closeModal('variationModal');
                
                // Optionally reload or update the interface
                // location.reload();
                
            } catch (error) {
                console.error('Variation generation error:', error);
                showToast(error.message || 'Error generating variation', 'error');
            } finally {
                hideLoading(generateVariationBtn.parentElement);
            }
        });
    }
});
