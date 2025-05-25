// Enhanced Audio Visualization Components for LogicBro
class KeyVisualization {
    constructor(container) {
        this.container = container;
        this.canvas = document.createElement('canvas');
        this.canvas.className = 'visualization-canvas';
        this.container.appendChild(this.canvas);
        this.ctx = this.canvas.getContext('2d');
        this.resize();
        window.addEventListener('resize', () => this.resize());
    }

    resize() {
        const rect = this.container.getBoundingClientRect();
        this.canvas.width = rect.width;
        this.canvas.height = Math.max(200, rect.width * 0.6);
        this.draw(); // Redraw on resize
    }

    drawCircleOfFifths(key, scale) {
        const notes = ['C', 'G', 'D', 'A', 'E', 'B', 'F#/Gb', 'Db', 'Ab', 'Eb', 'Bb', 'F'];
        const centerX = this.canvas.width / 2;
        const centerY = this.canvas.height / 2;
        const radius = Math.min(centerX, centerY) - 40;

        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        
        // Draw outer circle with gradient
        const gradient = this.ctx.createRadialGradient(centerX, centerY, 0, centerX, centerY, radius);
        gradient.addColorStop(0, 'rgba(74, 144, 226, 0.1)');
        gradient.addColorStop(1, 'rgba(74, 144, 226, 0.05)');
        
        this.ctx.beginPath();
        this.ctx.arc(centerX, centerY, radius, 0, 2 * Math.PI);
        this.ctx.fillStyle = gradient;
        this.ctx.fill();
        this.ctx.strokeStyle = 'var(--primary-color)';
        this.ctx.lineWidth = 2;
        this.ctx.stroke();

        // Draw notes with enhanced styling
        notes.forEach((note, i) => {
            const angle = (i * 30 - 90) * (Math.PI / 180);
            const x = centerX + radius * 0.8 * Math.cos(angle);
            const y = centerY + radius * 0.8 * Math.sin(angle);

            const isActiveKey = note.includes(key);
            
            // Draw note background circle
            this.ctx.beginPath();
            this.ctx.arc(x, y, 20, 0, 2 * Math.PI);
            this.ctx.fillStyle = isActiveKey ? 'var(--primary-color)' : 'var(--surface-color)';
            this.ctx.fill();
            this.ctx.strokeStyle = isActiveKey ? 'var(--primary-dark)' : 'var(--border-color)';
            this.ctx.lineWidth = 2;
            this.ctx.stroke();

            // Draw note text
            this.ctx.fillStyle = isActiveKey ? 'white' : 'var(--text-primary)';
            this.ctx.font = isActiveKey ? 'bold 12px Inter' : '12px Inter';
            this.ctx.textAlign = 'center';
            this.ctx.textBaseline = 'middle';
            this.ctx.fillText(note, x, y);
        });

        // Draw center text with better styling
        this.ctx.fillStyle = 'var(--text-primary)';
        this.ctx.font = 'bold 18px Inter';
        this.ctx.textAlign = 'center';
        this.ctx.textBaseline = 'middle';
        this.ctx.fillText(`${key} ${scale}`, centerX, centerY);
        
        // Add subtitle
        this.ctx.fillStyle = 'var(--text-secondary)';
        this.ctx.font = '12px Inter';
        this.ctx.fillText('Circle of Fifths', centerX, centerY + 25);
    }

    draw() {
        // Placeholder draw method for resize
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.ctx.fillStyle = 'var(--text-secondary)';
        this.ctx.font = '14px Inter';
        this.ctx.textAlign = 'center';
        this.ctx.textBaseline = 'middle';
        this.ctx.fillText('Upload audio to see key analysis', this.canvas.width / 2, this.canvas.height / 2);
    }
}

class ChordVisualization {
    constructor(container) {
        this.container = container;
        this.canvas = document.createElement('canvas');
        this.canvas.className = 'visualization-canvas';
        this.container.appendChild(this.canvas);
        this.ctx = this.canvas.getContext('2d');
        this.resize();
        window.addEventListener('resize', () => this.resize());
    }

    resize() {
        const rect = this.container.getBoundingClientRect();
        this.canvas.width = rect.width;
        this.canvas.height = Math.max(150, rect.width * 0.3);
        this.draw(); // Redraw on resize
    }

    drawChordProgression(chords) {
        if (!chords || chords.length === 0) {
            this.draw();
            return;
        }

        const padding = 20;
        const chordWidth = Math.max(60, (this.canvas.width - padding * 2) / chords.length - 10);
        const chordHeight = this.canvas.height - padding * 2;
        const totalWidth = chords.length * (chordWidth + 10) - 10;
        const startX = (this.canvas.width - totalWidth) / 2;

        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        // Draw progression line
        if (chords.length > 1) {
            this.ctx.beginPath();
            this.ctx.moveTo(startX + chordWidth / 2, padding + chordHeight / 2);
            chords.forEach((_, i) => {
                if (i > 0) {
                    const x = startX + i * (chordWidth + 10) + chordWidth / 2;
                    this.ctx.lineTo(x, padding + chordHeight / 2);
                }
            });
            this.ctx.strokeStyle = 'var(--border-color)';
            this.ctx.lineWidth = 2;
            this.ctx.stroke();
        }

        // Draw chords
        chords.forEach((chord, i) => {
            const x = startX + i * (chordWidth + 10);
            const y = padding;

            // Create gradient for chord box
            const gradient = this.ctx.createLinearGradient(x, y, x, y + chordHeight);
            gradient.addColorStop(0, 'var(--primary-light)');
            gradient.addColorStop(1, 'var(--primary-color)');

            // Draw chord box with rounded corners
            this.ctx.fillStyle = gradient;
            this.ctx.strokeStyle = 'var(--primary-dark)';
            this.ctx.lineWidth = 2;
            this.drawRoundedRect(x, y, chordWidth, chordHeight, 8);
            this.ctx.fill();
            this.ctx.stroke();

            // Add subtle shadow
            this.ctx.shadowColor = 'rgba(0, 0, 0, 0.1)';
            this.ctx.shadowBlur = 4;
            this.ctx.shadowOffsetY = 2;

            // Draw chord name
            this.ctx.shadowColor = 'transparent';
            this.ctx.fillStyle = 'white';
            this.ctx.font = 'bold 14px Inter';
            this.ctx.textAlign = 'center';
            this.ctx.textBaseline = 'middle';
            this.ctx.fillText(chord, x + chordWidth / 2, y + chordHeight / 2);

            // Draw chord number
            this.ctx.fillStyle = 'rgba(255, 255, 255, 0.8)';
            this.ctx.font = '10px Inter';
            this.ctx.fillText(`${i + 1}`, x + chordWidth / 2, y + chordHeight / 2 + 15);
        });
    }

    drawRoundedRect(x, y, width, height, radius) {
        this.ctx.beginPath();
        this.ctx.moveTo(x + radius, y);
        this.ctx.lineTo(x + width - radius, y);
        this.ctx.quadraticCurveTo(x + width, y, x + width, y + radius);
        this.ctx.lineTo(x + width, y + height - radius);
        this.ctx.quadraticCurveTo(x + width, y + height, x + width - radius, y + height);
        this.ctx.lineTo(x + radius, y + height);
        this.ctx.quadraticCurveTo(x, y + height, x, y + height - radius);
        this.ctx.lineTo(x, y + radius);
        this.ctx.quadraticCurveTo(x, y, x + radius, y);
        this.ctx.closePath();
    }

    draw() {
        // Placeholder draw method
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.ctx.fillStyle = 'var(--text-secondary)';
        this.ctx.font = '14px Inter';
        this.ctx.textAlign = 'center';
        this.ctx.textBaseline = 'middle';
        this.ctx.fillText('Chord progression will appear here', this.canvas.width / 2, this.canvas.height / 2);
    }
}

class MelodyVisualization {
    constructor(container) {
        this.container = container;
        this.canvas = document.createElement('canvas');
        this.canvas.className = 'visualization-canvas';
        this.container.appendChild(this.canvas);
        this.ctx = this.canvas.getContext('2d');
        this.resize();
        window.addEventListener('resize', () => this.resize());
    }

    resize() {
        const rect = this.container.getBoundingClientRect();
        this.canvas.width = rect.width;
        this.canvas.height = Math.max(150, rect.width * 0.3);
        this.draw(); // Redraw on resize
    }

    drawMelodicPattern(pattern) {
        if (!pattern || pattern.length === 0) {
            this.draw();
            return;
        }

        const padding = 30;
        const width = this.canvas.width - padding * 2;
        const height = this.canvas.height - padding * 2;

        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        // Draw grid lines
        this.drawGrid(padding, width, height);

        // Normalize pattern values to fit canvas height
        const max = Math.max(...pattern);
        const min = Math.min(...pattern);
        const range = max - min || 1; // Avoid division by zero
        const normalizedPattern = pattern.map(value => 
            ((value - min) / range) * height * 0.8 + height * 0.1
        );

        // Create gradient for the line
        const gradient = this.ctx.createLinearGradient(0, 0, width, 0);
        gradient.addColorStop(0, 'var(--primary-color)');
        gradient.addColorStop(0.5, 'var(--accent-color)');
        gradient.addColorStop(1, 'var(--primary-color)');

        // Draw pattern line with smooth curves
        this.ctx.beginPath();
        this.ctx.moveTo(padding, this.canvas.height - padding - normalizedPattern[0]);

        for (let i = 1; i < normalizedPattern.length; i++) {
            const x = padding + (i * (width / (pattern.length - 1)));
            const y = this.canvas.height - padding - normalizedPattern[i];
            
            if (i === 1) {
                this.ctx.lineTo(x, y);
            } else {
                // Use quadratic curves for smoother lines
                const prevX = padding + ((i - 1) * (width / (pattern.length - 1)));
                const prevY = this.canvas.height - padding - normalizedPattern[i - 1];
                const cpX = (prevX + x) / 2;
                this.ctx.quadraticCurveTo(cpX, prevY, x, y);
            }
        }

        this.ctx.strokeStyle = gradient;
        this.ctx.lineWidth = 3;
        this.ctx.lineCap = 'round';
        this.ctx.lineJoin = 'round';
        this.ctx.stroke();

        // Fill area under the line with gradient
        this.ctx.lineTo(padding + width, this.canvas.height - padding);
        this.ctx.lineTo(padding, this.canvas.height - padding);
        this.ctx.closePath();
        
        const fillGradient = this.ctx.createLinearGradient(0, 0, 0, height);
        fillGradient.addColorStop(0, 'rgba(74, 144, 226, 0.2)');
        fillGradient.addColorStop(1, 'rgba(74, 144, 226, 0.05)');
        this.ctx.fillStyle = fillGradient;
        this.ctx.fill();

        // Draw data points
        normalizedPattern.forEach((value, i) => {
            const x = padding + (i * (width / (pattern.length - 1)));
            const y = this.canvas.height - padding - value;
            
            this.ctx.beginPath();
            this.ctx.arc(x, y, 4, 0, 2 * Math.PI);
            this.ctx.fillStyle = 'var(--primary-color)';
            this.ctx.fill();
            this.ctx.strokeStyle = 'white';
            this.ctx.lineWidth = 2;
            this.ctx.stroke();
        });
    }

    drawGrid(padding, width, height) {
        this.ctx.strokeStyle = 'var(--border-light)';
        this.ctx.lineWidth = 1;
        this.ctx.setLineDash([2, 2]);

        // Horizontal grid lines
        for (let i = 0; i <= 4; i++) {
            const y = padding + (i * height / 4);
            this.ctx.beginPath();
            this.ctx.moveTo(padding, y);
            this.ctx.lineTo(padding + width, y);
            this.ctx.stroke();
        }

        // Vertical grid lines
        for (let i = 0; i <= 8; i++) {
            const x = padding + (i * width / 8);
            this.ctx.beginPath();
            this.ctx.moveTo(x, padding);
            this.ctx.lineTo(x, padding + height);
            this.ctx.stroke();
        }

        this.ctx.setLineDash([]); // Reset line dash
    }

    draw() {
        // Placeholder draw method
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        this.ctx.fillStyle = 'var(--text-secondary)';
        this.ctx.font = '14px Inter';
        this.ctx.textAlign = 'center';
        this.ctx.textBaseline = 'middle';
        this.ctx.fillText('Melodic pattern visualization will appear here', this.canvas.width / 2, this.canvas.height / 2);
    }
}

// Export visualizations
window.Visualizations = {
    KeyVisualization,
    ChordVisualization,
    MelodyVisualization
};
