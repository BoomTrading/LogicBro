// Common utilities and functions for LogicBro application

// Toast notification system
function showToast(message, type = 'info', duration = 5000) {
    const toastContainer = document.getElementById('toastContainer') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    
    const icon = getToastIcon(type);
    toast.innerHTML = `
        <div class="toast-content">
            <i class="mdi ${icon}"></i>
            <span>${message}</span>
        </div>
        <button class="toast-close" onclick="removeToast(this.parentElement)">
            <i class="mdi mdi-close"></i>
        </button>
    `;
    
    toastContainer.appendChild(toast);
    
    // Animate in
    setTimeout(() => toast.classList.add('toast-show'), 100);
    
    // Auto remove
    setTimeout(() => removeToast(toast), duration);
    
    return toast;
}

function removeToast(toast) {
    toast.classList.remove('toast-show');
    setTimeout(() => {
        if (toast.parentNode) {
            toast.parentNode.removeChild(toast);
        }
    }, 300);
}

function getToastIcon(type) {
    const icons = {
        'success': 'mdi-check-circle',
        'error': 'mdi-alert-circle',
        'warning': 'mdi-alert',
        'info': 'mdi-information'
    };
    return icons[type] || icons.info;
}

function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container';
    document.body.appendChild(container);
    return container;
}

// Loading spinner utilities
function showLoading(element, text = 'Loading...') {
    const loadingHtml = `
        <div class="loading-overlay">
            <div class="loading-spinner">
                <div class="spinner"></div>
                <span>${text}</span>
            </div>
        </div>
    `;
    element.style.position = 'relative';
    element.insertAdjacentHTML('beforeend', loadingHtml);
}

function hideLoading(element) {
    const overlay = element.querySelector('.loading-overlay');
    if (overlay) {
        overlay.remove();
    }
}

// Form validation utilities
function validateForm(formElement) {
    const inputs = formElement.querySelectorAll('[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            showFieldError(input, 'This field is required');
            isValid = false;
        } else {
            clearFieldError(input);
        }
    });
    
    return isValid;
}

function showFieldError(input, message) {
    clearFieldError(input);
    input.classList.add('error');
    
    const errorEl = document.createElement('div');
    errorEl.className = 'field-error';
    errorEl.textContent = message;
    
    input.parentNode.appendChild(errorEl);
}

function clearFieldError(input) {
    input.classList.remove('error');
    const errorEl = input.parentNode.querySelector('.field-error');
    if (errorEl) {
        errorEl.remove();
    }
}

// File size and type validation
function validateAudioFile(file) {
    const validTypes = ['audio/mp3', 'audio/wav', 'audio/mpeg', 'audio/ogg', 'audio/m4a'];
    const maxSize = 50 * 1024 * 1024; // 50MB
    
    if (!validTypes.includes(file.type)) {
        throw new Error('Please upload a valid audio file (MP3, WAV, OGG, M4A)');
    }
    
    if (file.size > maxSize) {
        throw new Error('File size must be less than 50MB');
    }
    
    return true;
}

// Format file size for display
function formatFileSize(bytes) {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

// Format duration for display
function formatDuration(seconds) {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = Math.floor(seconds % 60);
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
}

// Debounce function for search and input handling
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// API request helper with error handling
async function apiRequest(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };
    
    const mergedOptions = { ...defaultOptions, ...options };
    
    try {
        const response = await fetch(url, mergedOptions);
        
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error('API request failed:', error);
        showToast(error.message || 'An error occurred', 'error');
        throw error;
    }
}

// Initialize common functionality
document.addEventListener('DOMContentLoaded', function() {
    // Add smooth scrolling for anchor links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
    
    // Add ripple effect to buttons
    document.querySelectorAll('.btn').forEach(button => {
        button.addEventListener('click', function(e) {
            const ripple = document.createElement('span');
            const rect = this.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;
            
            ripple.style.width = ripple.style.height = size + 'px';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            ripple.classList.add('ripple');
            
            this.appendChild(ripple);
            
            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
});

// Export for module usage
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        showToast,
        showLoading,
        hideLoading,
        validateForm,
        validateAudioFile,
        formatFileSize,
        formatDuration,
        debounce,
        apiRequest
    };
}