document.addEventListener('DOMContentLoaded', function() {
    const uploadForm = document.getElementById('uploadForm');
    if (uploadForm) {
        uploadForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const formData = new FormData();
            const fileInput = document.getElementById('fileInput');
            formData.append('file', fileInput.files[0]);

            fetch('/api/upload', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                document.getElementById('message').innerHTML = 'File uploaded successfully!';
            })
            .catch(error => {
                document.getElementById('message').innerHTML = 'Error uploading file.';
                console.error('Error:', error);
            });
        });
    }
});