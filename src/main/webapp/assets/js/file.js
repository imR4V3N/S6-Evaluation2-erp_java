document.querySelectorAll('.custom-file-input').forEach(function (input) {
    input.addEventListener('change', function (e) {
        var fileName = e.target.files[0]?.name || "Choisir un fichier";
        var label = e.target.nextElementSibling;
        if (label && label.classList.contains('custom-file-label')) {
            label.textContent = fileName;
        }
    });
});
