// Minimal sidebar interactions and active highlighting
document.addEventListener('DOMContentLoaded', function(){
    // Toggle active classes for sidebar links to provide visual feedback on click (works without page navigation during dev)
    document.querySelectorAll('.subject-link, .unit-link, .topic-link').forEach(function(el){
        el.addEventListener('click', function(){
            // remove active from siblings of same group
            var cls = el.classList.contains('subject-link') ? 'subject-link' : (el.classList.contains('unit-link') ? 'unit-link' : 'topic-link');
            document.querySelectorAll('.' + cls).forEach(function(e){ e.classList.remove('active'); });
            el.classList.add('active');
        });
    });

    // Optional: make sidebar collapsible on small screens
    var sidebar = document.querySelector('.sidebar-inner');
    if(!sidebar) return;
    var btn = document.createElement('button');
    btn.className = 'btn btn-sm btn-outline-secondary d-md-none mb-3';
    btn.textContent = 'Toggle Subjects';
    btn.addEventListener('click', function(){
        sidebar.classList.toggle('collapsed');
        if(sidebar.classList.contains('collapsed')){
            sidebar.style.display = 'none';
        } else {
            sidebar.style.display = '';
        }
    });
    sidebar.parentNode.insertBefore(btn, sidebar);
});
