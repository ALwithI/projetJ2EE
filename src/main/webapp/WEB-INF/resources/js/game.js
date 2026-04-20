document.querySelectorAll('.tile').forEach(function(tile) {
    tile.addEventListener('click', function() {
        this.style.transform = 'scale(0.9)';
        var self = this;
        setTimeout(function() { self.style.transform = ''; }, 150);
    });
});