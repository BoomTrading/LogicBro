document.addEventListener('DOMContentLoaded', function() {
    let wavesurfer = null;
    const dropZone = document.getElementById('dropZone');
    const fileInput = document.getElementById('fileInput');
    const uploadProgress = document.getElementById('uploadProgress');
    const progressBar = uploadProgress?.querySelector('.progress');
    const uploadStatus = document.getElementById('uploadStatus');
    const analysisResults = document.getElementById('analysisResults');
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
        
        if (keyVisualEl && window.Visualizations) {
            keyVis = new window.Visualizations.KeyVisualization(keyVisualEl);
        }
        if (chordVisualEl && window.Visualizations) {
            chordVis = new window.Visualizations.ChordVisualization(chordVisualEl);
        }
        if (melodyVisualEl && window.Visualizations) {
            melodyVis = new window.Visualizations.MelodyVisualization(melodyVisualEl);
        }
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
        if (!waveformContainer || typeof WaveSurfer === 'undefined') return;
        
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

    // File upload and analysis handling
    function handleFileUpload(file) {
        if (!file) return;
        
        // Check file size
        const maxSize = 50 * 1024 * 1024; // 50MB
        if (file.size > maxSize) {
            showToast('File size exceeds 50MB limit', 'error');
            return;
        }

        // Check if format needs conversion
        const supportedFormats = ['wav', 'aiff', 'aif', 'au'];
        const fileExtension = file.name.split('.').pop().toLowerCase();
        const needsConversion = !supportedFormats.includes(fileExtension);
        
        if (needsConversion) {
            const formatError = document.getElementById('formatError');
            if (formatError) {
                formatError.classList.remove('hidden');
                const errorMessage = document.getElementById('formatErrorMessage');
                if (errorMessage) {
                    errorMessage.textContent = 
                        `Your ${fileExtension.toUpperCase()} file will be converted to WAV format for analysis. This may take a moment.`;
                }
            }
        }

        // Show upload progress
        if (uploadProgress) uploadProgress.classList.remove('hidden');
        if (dropZone) dropZone.classList.add('hidden');
        
        // Upload file
        const formData = new FormData();
        formData.append('file', file);
        
        fetch('/api/audio/upload', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            currentFileId = data.fileId;
            updateUploadStatus('File uploaded successfully. Starting analysis...');
            return analyzeAudio(data.fileId);
        })
        .catch(error => {
            console.error('Upload error:', error);
            showToast('Error uploading file: ' + error.message, 'error');
            resetUploadUI();
        });
    }

    function analyzeAudio(fileId) {
        updateUploadStatus('Analyzing audio... This may take a few minutes.');
        
        return fetch(`/api/audio/analyze/${fileId}`, {
            method: 'GET'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Analysis failed: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            updateUploadStatus('Analysis complete!');
            setTimeout(() => {
                if (uploadProgress) uploadProgress.classList.add('hidden');
                displayAnalysisResults(data);
            }, 1000);
        })
        .catch(error => {
            console.error('Analysis error:', error);
            showToast('Error analyzing audio: ' + error.message, 'error');
            resetUploadUI();
        });
    }

    function displayAnalysisResults(data) {
        // Update key and scale
        const keyValue = document.getElementById('keyValue');
        const scaleValue = document.getElementById('scaleValue');
        
        if (data.key && keyValue) {
            keyValue.textContent = data.key;
        }
        if (data.scale && scaleValue) {
            scaleValue.textContent = data.scale;
        }
        
        // Update tempo
        const tempoValue = document.getElementById('tempoValue');
        if (data.tempo && data.tempo > 0 && tempoValue) {
            tempoValue.textContent = Math.round(data.tempo) + ' BPM';
        }
        
        // Update chord progression
        if (data.chordProgression && data.chordProgression.length > 0) {
            displayChordProgression(data.chordProgression);
        }
        
        // Update melodic patterns
        if (data.melodicPatterns && data.melodicPatterns.length > 0) {
            displayMelodicPatterns(data.melodicPatterns);
        }
        
        // Update visualizations
        updateVisualizations(data);
        
        // Show results
        if (analysisResults) analysisResults.classList.remove('hidden');
        
        showToast('Analysis completed successfully!', 'success');
    }

    function displayChordProgression(chords) {
        if (!chordSequence) return;
        
        chordSequence.innerHTML = '';
        
        chords.forEach((chord, index) => {
            const chip = document.createElement('div');
            chip.className = 'chip chord-chip';
            chip.innerHTML = `<span class="chord-number">${index + 1}</span><span class="chord-name">${chord}</span>`;
            chordSequence.appendChild(chip);
        });
    }

    function displayMelodicPatterns(patterns) {
        if (!melodyPatterns) return;
        
        melodyPatterns.innerHTML = '';
        
        patterns.forEach((pattern, index) => {
            const chip = document.createElement('div');
            chip.className = 'chip melody-chip';
            chip.innerHTML = `<span class="pattern-number">${index + 1}</span><span class="pattern-name">${pattern}</span>`;
            melodyPatterns.appendChild(chip);
        });
    }

    function resetUploadUI() {
        if (uploadProgress) uploadProgress.classList.add('hidden');
        if (dropZone) dropZone.classList.remove('hidden');
        
        const formatError = document.getElementById('formatError');
        if (formatError) formatError.classList.add('hidden');
        
        // Reset progress bar
        if (progressBar) {
            progressBar.style.width = '0%';
        }
    }

    function updateUploadStatus(message) {
        if (uploadStatus) {
            uploadStatus.textContent = message;
        }
    }

    function showToast(message, type = 'info') {
        // Create toast notification
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <i class="mdi ${type === 'error' ? 'mdi-alert-circle' : type === 'success' ? 'mdi-check-circle' : 'mdi-information'}"></i>
            <span>${message}</span>
        `;
        
        // Add to page
        document.body.appendChild(toast);
        
        // Remove after 5 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.remove();
            }
        }, 5000);
    }

    // File input change handler
    if (fileInput) {
        fileInput.addEventListener('change', function(e) {
            const file = e.target.files[0];
            if (file) {
                handleFileUpload(file);
            }
        });
    }

    // Drag and drop handlers
    if (dropZone) {
        dropZone.addEventListener('dragover', function(e) {
            e.preventDefault();
            dropZone.classList.add('drag-over');
        });

        dropZone.addEventListener('dragleave', function(e) {
            e.preventDefault();
            dropZone.classList.remove('drag-over');
        });

        dropZone.addEventListener('drop', function(e) {
            e.preventDefault();
            dropZone.classList.remove('drag-over');
            
            const files = e.dataTransfer.files;
            if (files.length > 0) {
                handleFileUpload(files[0]);
            }
        });
    }

    // Copy progression button handler
    const copyProgressionBtn = document.getElementById('copyProgressionBtn');
    if (copyProgressionBtn) {
        copyProgressionBtn.addEventListener('click', function() {
            const chordChips = document.querySelectorAll('#chordSequence .chord-chip .chord-name');
            const progression = Array.from(chordChips).map(chip => chip.textContent).join(' - ');
            
            if (navigator.clipboard && navigator.clipboard.writeText) {
                navigator.clipboard.writeText(progression).then(() => {
                    showToast('Chord progression copied to clipboard!', 'success');
                }).catch(() => {
                    showToast('Failed to copy to clipboard', 'error');
                });
            } else {
                // Fallback for older browsers
                const textArea = document.createElement('textarea');
                textArea.value = progression;
                document.body.appendChild(textArea);
                textArea.select();
                try {
                    document.execCommand('copy');
                    showToast('Chord progression copied to clipboard!', 'success');
                } catch (err) {
                    showToast('Failed to copy to clipboard', 'error');
                }
                document.body.removeChild(textArea);
            }
        });
    }

    // Initialize everything
    initWaveSurfer();
    initVisualizations();
});
